package de.uniulm.loraparkapplication.repositories;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import de.uniulm.loraparkapplication.R;
import de.uniulm.loraparkapplication.SensorDetailActivity;
import de.uniulm.loraparkapplication.database.RuleDao;
import de.uniulm.loraparkapplication.database.RuleDatabase;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.models.RuleDeserializer;
import de.uniulm.loraparkapplication.models.SensorDetail;
import de.uniulm.loraparkapplication.network.HttpClient;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RuleDataRepository {

    private final RuleDao mRuleDao;

    private static RuleDataRepository instance;

    public static RuleDataRepository getInstance(Application application) {
        if(instance == null){
            instance = new RuleDataRepository(application);
        }
        return instance;
    }

    private RuleDataRepository(Application application){
        RuleDatabase db = RuleDatabase.getDatabase(application);
        this.mRuleDao = db.ruleDao();
    }

    //region Rule queries

    /**
     * Returns all local rules (in the db)
     *
     * @return list with all rules
     */
    public LiveData<List<Rule>> getLiveAllRules(){
        return mRuleDao.findAllRules();
    }

    /**
     * Returns a list with filtered rules. The rules will be filtered by their state: if they are currently active
     *
     * @param isActive filters the rules if they are active
     * @return list with filtered rules
     */
    public LiveData<List<Rule>> getRules(Boolean isActive) {
        return mRuleDao.findRules(isActive);
    }

    /**
     *  Returns a single rule
     *
     * @param ruleId the id of the requested rule
     * @return the rule with the requested ruleId
     */
    public LiveData<Rule> getRule(String ruleId) {
        return mRuleDao.findRule(ruleId);
    }


    //endregion

    //region Rule creation and deletion

    /**
     * Insert rule (overrides an existing one)
     *
     * @param rule the rule to insert
     * @return
     */
    public LiveData<Resource<String>> insertRule(@NonNull Rule rule) {

        MutableLiveData<Resource<String>> data = new MutableLiveData<>();
        data.postValue(Resource.loading(null));

        RuleDatabase.databaseExecutor.execute(() -> {
            try {
                mRuleDao.insert(rule);
                data.postValue(Resource.success(""));
            }catch(Exception e){
                data.postValue(Resource.error(e.getMessage(), ""));
            }
        });

        return data;
    }

    /**
     * Insert a rule, but checking if it already exists.
     * If it already exists it will deactivate the current rule and replace the current rule with the new rule
     *
     * @param rule the rule to insert
     */
    public LiveData<Resource<String>> insertRuleSave(@NonNull Rule rule){
        Integer count = mRuleDao.getAmountOfRules(rule.getId());

        //TODO: Perhaps replace with a delete rule and insert afterwards (the deletion has to deactivate anyway
        if(count > 0){
            deactivateRule(rule);
        }

        return insertRule(rule);
    }

    /**
     * Deletes all rules
     *
     * @return resource with the state of the deletion
     */
    public LiveData<Resource<String>> deleteAllRules() {
        MutableLiveData<Resource<String>> data = new MutableLiveData<>();
        data.postValue(Resource.loading(null));

        RuleDatabase.databaseExecutor.execute(() -> {
            try {
                deactivateAllRules();
                mRuleDao.deleteAllRules();
                data.postValue(Resource.success(""));
            }catch(Exception e){
                data.postValue(Resource.error(e.getMessage(), ""));
            }
        });

        return data;
    }

    //endregion

    //region Download and process new rules

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
     * Deactivates a rule and removes active geofences
     *
     * @param rule the rule to deactivate
     */
    public void deactivateRule(@NonNull Rule rule){
        // Deactivate rule
    }

    /**
     * Deactivates all rules
     */
    public void deactivateAllRules(){
        // TODO: deactivate all rules
    }


    //endregion


}
