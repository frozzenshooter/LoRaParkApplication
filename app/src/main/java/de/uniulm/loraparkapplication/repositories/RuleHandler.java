package de.uniulm.loraparkapplication.repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import de.uniulm.loraparkapplication.models.CompleteRule;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.models.RuleDeserializer;
import de.uniulm.loraparkapplication.network.HttpClient;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
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
                .concatMap(this::downloadNewRule, 1, Schedulers.io())
                .concatMap((completeRule) -> this.insertCompleteRuleSave(completeRule).andThen(Observable.just(completeRule.getRule().getName())), 1, Schedulers.io());
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
     * If it already exists it will deactivate the current rule, delete it and replace(insert in db) the current rule with the new rule
     *
     * @param completeRule the rule to insert
     */
    public Completable insertCompleteRuleSave(@NonNull CompleteRule completeRule){
        Rule rule = completeRule.getRule();
       /* return this.mRuleDataRepository.existsRule(rule.getId())
                    .subscribeOn(Schedulers.io())
                    .flatMapCompletable(exists ->{
                        if(exists){
                            // if the rule exists -> deactivate it and delete it
                            return this.deactivateRule(rule.getId())
                                    .andThen(this.mRuleDataRepository.deleteRule(rule));
                        }
                        return Completable.complete();
                    })
                    .andThen(this.mRuleDataRepository.insertCompleteRule(completeRule));*/

        Boolean exists = this.mRuleDataRepository.existsRuleSync(rule.getId());
        if(exists){
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
                .concatMap(completeRule -> {
                    completeRule.getRule().setIsActive(false);
                    return Single.just(completeRule);
                })
                .concatMap((completeRule) -> {
                    return this.mRuleDataRepository.updateRule(completeRule).andThen(Single.just(completeRule));
                })
                .flatMapObservable((completeRule) -> {
                    return Observable.fromIterable(completeRule.getGeofences());
                })
                .concatMapCompletable(this.mGeofenceRepository::deleteGeofence);


                /* OLD
                .concatMap((completeRule) -> {
                    Rule rule = completeRule.getRule();
                    if(rule.getIsActive()){
                        rule.setIsActive(false);
                        this.mRuleDataRepository.updateRule(rule);
                    }
                    return Single.just(completeRule);
                })
                .flatMapObservable((completeRule) -> {
                    return Observable.fromIterable(completeRule.getGeofences());
                })
                .flatMapCompletable(this.mGeofenceRepository::deleteGeofence);

                 */
    }

    /**
     * Deactivates all rules
     */
    public Completable deactivateAllRules(){

        /*List<Rule> rules = this.mRuleDataRepository.findAllRulesSync();
        Completable c = Observable.fromIterable(rules)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(rule -> {
                    return this.deactivateRule(rule.getId());
                });

        c.subscribe();*/

        return Completable.complete();
    }

    //endregion

    //region Rule activation

    public Completable activateRule(String ruleId){
        return this.mRuleDataRepository
                .getSingleCompleteRule(ruleId)
                .subscribeOn(Schedulers.io())
                /*Test*/
                .concatMap(completeRule -> {
                    completeRule.getRule().setIsActive(true);
                    return Single.just(completeRule);
                })
                .concatMap((completeRule) -> {
                   return this.mRuleDataRepository.updateRule(completeRule).andThen(Single.just(completeRule));
                })
                .flatMapObservable((completeRule) -> {
                    return Observable.fromIterable(completeRule.getGeofences());
                })
                .concatMapCompletable(this.mGeofenceRepository::createGeofence);

                /*OLD
                .flatMap((completeRule) -> {

                    Rule rule = completeRule.getRule();
                    if(!rule.getIsActive()){
                        rule.setIsActive(true);
                        //TODO: this is just a workaround
                        this.mRuleDataRepository.updateRule(rule).subscribe();
                    }
                    return Single.just(completeRule);
                })
                .flatMapObservable((completeRule) -> {
                    return Observable.fromIterable(completeRule.getGeofences());
                })
                .flatMapCompletable(this.mGeofenceRepository::createGeofence);
                */
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
