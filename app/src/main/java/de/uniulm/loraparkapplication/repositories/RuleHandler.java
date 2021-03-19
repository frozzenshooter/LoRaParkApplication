package de.uniulm.loraparkapplication.repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import de.uniulm.loraparkapplication.models.CompleteRule;
import de.uniulm.loraparkapplication.models.Geofence;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.models.RuleDeserializer;
import de.uniulm.loraparkapplication.network.HttpClient;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
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

    /**
     * Returns a list with all rules saved in the db
     *
     * @return LiveData
     */
    public LiveData<List<Rule>> getAllRules(){
        return this.mAllRules;
    }

    /**
     * Returns a list with all active rules
     *
     * @return LiveData
     */
    public LiveData<List<Rule>> getActiveRules(){
        return this.mActiveRules;
    }

    /**
     * Returns a list with all inactivate rules
     *
     * @return LiveData
     */
    public LiveData<List<Rule>> getInactiveRules(){
        return this.mInactiveRules;
    }

    /**
     * Returns the rule with the requested ud
     *
     * @param ruleId the id of the rule
     * @return LiveData
     */
    public LiveData<Rule> getRule(String ruleId) {
        return this.mRuleDataRepository.getRule(ruleId);
    }

    /**
     * Returns a list of complete rules filtered by their state: if they are active
     *
     * @param isActive if thr rules are active
     * @return filtered complete rules
     */
    public LiveData<List<CompleteRule>> getCompleteRules(Boolean isActive){
        //TODO: can this be changed to initialising it in the constructor and only hand over the reference?
        return this.mRuleDataRepository.getCompleteRules(isActive);
    }

    //endregion

    //region Rule deletion

    /**
     * Delete all existing rules
     *
     * @return Completable
     */
    public Completable deleteAllRules(){

        return Completable
                .defer(() ->{
                    // TODO: this::deactivateAllRules (or at least delete all geofences);
                    return Completable.complete();
                })
                .concatWith(Completable.defer(() ->{
                                try {
                                    this.mRuleDataRepository.deleteAllRules();
                                    return Completable.complete();
                                }catch(Exception e){
                                    return Completable.error(e);
                                }
                            }));
    }

    /**
     * Deletes the rule and additional removes the attached geofences
     *
     * @param completeRule the rule to delete
     * @throws Exception
     */
    private void deleteRule(CompleteRule completeRule) throws Exception{
        for(Geofence geofence : completeRule.getGeofences()){
            //TODO: delete geofences
        }

        this.mRuleDataRepository.deleteRule(completeRule.getRule());
    }

    //endregion

    //region Download new rules

    /**
     * Downloads rules in the background and inserts them into the database
     *
     * @param ruleIds the ids of the rules to download and insert into the db
     */
    public Observable<CompleteRule> downloadRules(List<String> ruleIds){
        return Observable.fromIterable(ruleIds)
                .concatMap(this::downloadAndSaveRule);
    }

    /**
     * Download a new rule from the server
     * @param ruleId the id of the rule to download
     * @return observable which will return the parsed rule
     */
    public Observable<CompleteRule> downloadAndSaveRule(@NonNull String ruleId){
        return Observable.defer(() -> {

            CompleteRule completeRule;
            try {
                Response response = HttpClient.getInstance().newCall(HttpClient.getRule(ruleId)).execute();

                GsonBuilder gsonBuilder = new GsonBuilder();

                gsonBuilder.registerTypeAdapter(CompleteRule.class, new RuleDeserializer());

                Gson ruleGson = gsonBuilder.create();

                completeRule = ruleGson.fromJson(response.body().charStream(), CompleteRule.class);


            } catch (IOException ex) {
                return Observable.error(ex);
            }

            if(completeRule!= null){

                try{
                    CompleteRule oldCompleteRule = this.mRuleDataRepository.getCompleteRule(ruleId);

                    if(oldCompleteRule != null){
                        // Deactivate and delete old rule to be sure there is no geofence left
                        deleteRule(oldCompleteRule);
                    }

                    this.mRuleDataRepository.insertCompleteRule(completeRule);
                }catch(Exception ex){
                    return Observable.error(ex);
                }
            }else{
                return Observable.error(new Exception("Not posible to download and insert the rule!"));
            }

            return Observable.just(completeRule);
        });
    }


    /**
     * Insert a complete rule, but checking if it already exists.
     * If it already exists it will deactivate the current rule, delete it and replace(insert in db) the current rule with the new rule
     *
     * @param completeRule the rule to insert
     */
    public Completable insertCompleteRuleSave(@NonNull CompleteRule completeRule){

        return Completable.defer(() ->{
            try{
                CompleteRule oldCompleteRule = this.mRuleDataRepository.getCompleteRule(completeRule.getRule().getId());

                if(oldCompleteRule != null){
                    deleteRule(oldCompleteRule);
                }
                this.mRuleDataRepository.insertCompleteRule(completeRule);

            }catch(Exception ex){
                return Completable.error(ex);
            }
            return Completable.complete();
        });

    }

    //endregion

    //region Rule de/activation

    /**
     * Deactivates a rule and removes the attached geofences (if not already inactive)
     *
     * @param ruleId the id of the rule to deactivate
     */
    public Completable deactivateRule(@NonNull String ruleId){

        return Completable.defer(() -> {
            try {

                CompleteRule completeRule = this.mRuleDataRepository.getCompleteRule(ruleId);
                if (completeRule != null && completeRule.getRule().getIsActive()) {
                    // delete existing geofences for this rule
                    for (Geofence geofence : completeRule.getGeofences()) {
                        //TODO: geofence delete has to be sync
                        // this.mGeofenceRepository.deleteGeofence(geofence);
                        // PROBLEM: error handling has to be async
                    }
                    Rule rule = completeRule.getRule();
                    rule.setIsActive(false);

                    this.mRuleDataRepository.updateRule(rule);
                }
            }catch(Exception ex){
                return Completable.error(ex);
            }

            return Completable.complete();
        });
    }

    /**
     * Activates the rule if its not already active. This will also create the geofences required for this rule
     *
     * @param ruleId id of the rule to activate
     * @return Completable
     */
    public Completable activateRule(String ruleId){

        return Completable.defer(() -> {
            try {

                CompleteRule completeRule = this.mRuleDataRepository.getCompleteRule(ruleId);
                if (completeRule != null && !completeRule.getRule().getIsActive()) {

                    for (Geofence geofence : completeRule.getGeofences()) {
                        //TODO: geofence creation has to be sync
                        // this.mGeofenceRepository.createGeofence(geofence);
                    }
                    Rule rule = completeRule.getRule();
                    rule.setIsActive(true);

                    this.mRuleDataRepository.updateRule(rule);
                }
            }catch(Exception ex){
                return Completable.error(ex);
            }

            return Completable.complete();
        });
    }

    //endregion

    // TODO FIIIIIX THIS MESS
    public Completable updateRule(@NonNull Rule rule) {
        return Completable.defer(()->{
            try{
                this.mRuleDataRepository.updateRule(rule);
                return Completable.complete();
            }catch(Exception ex){
                return Completable.error(ex);
            }
        });
    }
}
