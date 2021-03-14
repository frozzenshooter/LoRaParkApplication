package de.uniulm.loraparkapplication.repositories;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.models.RuleDeserializer;
import de.uniulm.loraparkapplication.network.HttpClient;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Response;

public class RuleHandler {

    private final RuleDataRepository mRuleDataRepository;

    private final LiveData<List<Rule>> mAllRules;
    private final LiveData<List<Rule>> mActiveRules;
    private final LiveData<List<Rule>> mInactiveRules;

    public RuleHandler(@NonNull Application application){
        this.mRuleDataRepository = RuleDataRepository.getInstance(application);

        this.mAllRules = this.mRuleDataRepository.getAllRules();
        this.mActiveRules = this.mRuleDataRepository.getRules(true);
        this.mInactiveRules = this.mRuleDataRepository.getRules(false);
    }

    public LiveData<List<Rule>> getAllRules(){
        return this.mAllRules;
    }

    public LiveData<List<Rule>> getActiveRules(){
        return this.mActiveRules;
    }

    public LiveData<List<Rule>> getInactiveRules(){
        return this.mInactiveRules;
    }

    public LiveData<Resource<String>> deleteAllRules(){

        this.deactivateAllRules();
        return this.mRuleDataRepository.deleteAllRules();
    }

    public LiveData<Resource<String>> insertRule(@NonNull Rule rule){

        //TODO: delete later -> Rules won't be added manually and therefore this shouldn't be used
        return this.mRuleDataRepository.insertRule(rule);
    }


    //region Download new rules

    /**
     * Downloads rules in the background and inserts them into the database
     *
     * @param ruleIds the ids of the rules to download and insert into the db
     */
    public void downloadRules(List<String> ruleIds){

        if(ruleIds != null && ruleIds.size() > 0){
            Observable<String> ruleIdObservable = Observable.fromArray(ruleIds.toArray(new String[0]));
            ruleIdObservable.subscribeOn(Schedulers.io())
                    .flatMap(this::downloadNewRule)
                    .subscribe(this::insertRuleSave);
        }
    }

    /**
     * Download a new rule from the server
     * @param ruleId the id of the rule to download
     * @return observable which will return the parsed rule
     */
    public Observable<Rule> downloadNewRule(@NonNull String ruleId){
        return Observable.defer(() -> {

            try {
                Response response = HttpClient.getInstance().newCall(HttpClient.getRule(ruleId)).execute();

                GsonBuilder gsonBuilder = new GsonBuilder();

                gsonBuilder.registerTypeAdapter(Rule.class, new RuleDeserializer());

                Gson ruleGson = gsonBuilder.create();

                Rule rule = ruleGson.fromJson(response.body().charStream(), Rule.class);

                //TODO: DELETE AFTER DEBUGGING
                StringBuilder builder = new StringBuilder();
                builder.append("id: ").append(rule.getId()).append("; ");
                builder.append("name: ").append(rule.getName()).append("; ");
                builder.append("description: ").append(rule.getDescription()).append("; ");
                builder.append("condition: ").append(rule.getCondition()).append("; ");

                Log.e("RULE_DOWNLOAD", builder.toString());

                return Observable.just(rule);
            } catch (IOException e) {
                return Observable.error(e);
            }

        });
    }

    /**
     * Insert a rule, but checking if it already exists.
     * If it already exists it will deactivate the current rule and replace the current rule with the new rule
     *
     * @param rule the rule to insert
     */
    public LiveData<Resource<String>> insertRuleSave(@NonNull Rule rule){
        Integer count = this.mRuleDataRepository.getAmountOfRules(rule.getId());

        if(count > 0){
            deactivateRule(rule);
        }

        return insertRule(rule);
    }

    //endregion


    /**
     * Deactivates a rule and removes active geofences
     *
     * @param rule the rule to deactivate
     */
    public void deactivateRule(@NonNull Rule rule){
        // Deactivate rule
        // Delete geofences
        // Delete sensor fetching
    }

    /**
     * Deactivates all rules
     */
    public void deactivateAllRules(){
        // TODO: deactivate all rules
    }

}
