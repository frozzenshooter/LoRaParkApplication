package de.uniulm.loraparkapplication.repositories;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import de.uniulm.loraparkapplication.R;
import de.uniulm.loraparkapplication.SensorOverviewActivity;
import de.uniulm.loraparkapplication.models.CompleteRule;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.models.RuleDeserializer;
import de.uniulm.loraparkapplication.models.SensorDescription;
import de.uniulm.loraparkapplication.network.HttpClient;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Response;

public class RuleHandler {

    private final RuleDataRepository mRuleDataRepository;
    private final GeofenceRepository mGeofenceRepository;

    private final LiveData<List<Rule>> mAllRules;
    private final LiveData<List<Rule>> mActiveRules;
    private final LiveData<List<Rule>> mInactiveRules;

    private static RuleHandler instance;
    public static RuleHandler getInstance(@NonNull Application application){
        if(instance == null){
            instance = new RuleHandler(application);
        }
        return instance;
    }

    private RuleHandler(@NonNull Application application){
        this.mRuleDataRepository = RuleDataRepository.getInstance(application);
        this.mGeofenceRepository = GeofenceRepository.getInstance(application);

        this.mAllRules = this.mRuleDataRepository.getAllRules();
        this.mActiveRules = this.mRuleDataRepository.getRules(true);
        this.mInactiveRules = this.mRuleDataRepository.getRules(false);
    }

    //region Rule access

    public LiveData<List<Rule>> getAllRules(){
        return this.mAllRules;
    }

    public LiveData<List<Rule>> getActiveRules(){
        return this.mActiveRules;
    }

    public LiveData<List<Rule>> getInactiveRules(){
        return this.mInactiveRules;
    }

    //endregion

    //region Rule deletion

    public Completable deleteAllRules(){

        return Completable
                .defer(this::deactivateAllRules)
                .subscribeOn(Schedulers.io())
                .concatWith(this.mRuleDataRepository.deleteAllRules());
    }

    //endregion

    //region Download new rules

    /**
     * Downloads rules in the background and inserts them into the database
     *
     * @param ruleIds the ids of the rules to download and insert into the db
     */
    public Observable<String> downloadRules(List<String> ruleIds){
        return Observable.fromIterable(ruleIds)
                .subscribeOn(Schedulers.io())
                .concatMap(this::downloadNewRule)
                .concatMap(this::insertCompleteRuleSave);
    }

    /**
     * Download a new rule from the server
     * @param ruleId the id of the rule to download
     * @return observable which will return the parsed rule
     */
    public Observable<CompleteRule> downloadNewRule(@NonNull String ruleId){
        return Observable.defer(() -> {

            try {
                Response response = HttpClient.getInstance().newCall(HttpClient.getRule(ruleId)).execute();

                GsonBuilder gsonBuilder = new GsonBuilder();

                gsonBuilder.registerTypeAdapter(CompleteRule.class, new RuleDeserializer());

                Gson ruleGson = gsonBuilder.create();

                CompleteRule completeRule = ruleGson.fromJson(response.body().charStream(), CompleteRule.class);

                return Observable.just(completeRule);
            } catch (IOException e) {
                return Observable.error(e);
            }

        });
    }

    //endregion

    //region Rule insertion

    /**
     * Insert a complete rule, but checking if it already exists.
     * If it already exists it will deactivate the current rule and replace the current rule with the new rule
     *
     * @param completeRule the rule to insert
     */
    public Observable<String> insertCompleteRuleSave(@NonNull CompleteRule completeRule){
        Rule rule = completeRule.getRule();
        Integer count = this.mRuleDataRepository.getAmountOfRules(rule.getId());

        if(count > 0){
            // Remove old rule with old triggers
            deactivateRule(rule.getId());

            // this will delete also all attached sensors, geofences and actions
           this.mRuleDataRepository.deleteRule(rule);
        }

        return this.mRuleDataRepository.insertCompleteRule(completeRule);
    }

    //endregion

    //region Rule deactivation

    /**
     * Deactivates a rule and removes active geofences
     *
     * @param ruleId the id of the rule to deactivate
     */
    public Completable deactivateRule(@NonNull String ruleId){
        return this.mRuleDataRepository
                .getSingleCompleteRule(ruleId)
                .subscribeOn(Schedulers.io())
                .flatMap((completeRule) -> {

                    if(!completeRule.getRule().getIsActive()){
                        return Single.error(new Exception("Rule is already inactive"));
                    }else{
                        return Single.just(completeRule);
                    }
                })
                .flatMap((completeRule) -> {
                    Rule rule = completeRule.getRule();
                    rule.setIsActive(false);
                    this.mRuleDataRepository.updateRule(rule);
                    return Single.just(completeRule);
                }).flatMapObservable((completeRule) -> {
                    return Observable.fromIterable(completeRule.getGeofences());
                })
                .flatMapCompletable(this.mGeofenceRepository::deleteGeofence);
    }

    /**
     * Deactivates all rules
     */
    public Completable deactivateAllRules(){

        // TODO: deactivate all rules
        return  Completable.complete();
    }

    //endregion

    //region Rule activation

    public Completable activateRule(String ruleId){
        return this.mRuleDataRepository
                .getSingleCompleteRule(ruleId)
                .subscribeOn(Schedulers.io())
                .flatMap((completeRule) -> {

                    if(completeRule.getRule().getIsActive()){
                        return Single.error(new Exception("Rule is already active"));
                    }else{
                        return Single.just(completeRule);
                    }
                })
                .flatMap((completeRule) -> {
                    Rule rule = completeRule.getRule();
                    rule.setIsActive(true);
                    this.mRuleDataRepository.updateRule(rule);
                    return Single.just(completeRule);
                })
                .flatMapObservable((completeRule) -> {
                    return Observable.fromIterable(completeRule.getGeofences());
                })
                .flatMapCompletable(this.mGeofenceRepository::createGeofence);
    }

    //endregion

    // region Complete rule access

    /**
     * Returns all rules as complete rules
     *
     * @return list of complete rules
     */
    public LiveData<List<CompleteRule>> getCompleteRules(){
        return this.mRuleDataRepository.getCompleteRules();
    }

    /**
     * Returns a specific complete rule
     *
     * @param ruleId the id of the requested rule
     * @return complete rule
     */
    public LiveData<CompleteRule> getCompleteRule(@NonNull String ruleId){
        return this.mRuleDataRepository.getCompleteRule(ruleId);
    }

    /**
     * Returns a list of complete rules filtered by their state: if they are active
     *
     * @param isActive if thr rules are active
     * @return filtered complete rules
     */
    public LiveData<List<CompleteRule>> getCompleteRules(Boolean isActive){
        return this.mRuleDataRepository.getCompleteRules(isActive);
    }

    public LiveData<Rule> getRule(String ruleId) {
        return this.mRuleDataRepository.getRule(ruleId);
    }

    //endregion

    // TODO FIIIIIX THIS MESS
    public Completable updateRule(@NonNull Rule rule){
        return Completable.defer(()->{
            try{
                this.mRuleDataRepository.updateRule(rule);
                return Completable.complete();
            }catch(Exception ex){
                return Completable.error(ex);
            }
        }).subscribeOn(Schedulers.io());
    }
}
