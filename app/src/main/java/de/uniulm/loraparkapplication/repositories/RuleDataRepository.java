package de.uniulm.loraparkapplication.repositories;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import de.uniulm.loraparkapplication.database.RuleDao;
import de.uniulm.loraparkapplication.database.RuleDatabase;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.models.RuleDeserializer;
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

    public LiveData<List<Rule>> getLiveAllRules(){
        return mRuleDao.findAllRules();
    }

    public LiveData<List<Rule>> getRules(Boolean isActive) {
        return mRuleDao.findRules(isActive);
    }


    public LiveData<Rule> getRule(String ruleId) {
        return mRuleDao.findRule(ruleId);
    }


    //endregion

    //region Rule creation and deletion

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

    public LiveData<Resource<String>> deleteAllRules() {
        MutableLiveData<Resource<String>> data = new MutableLiveData<>();
        data.postValue(Resource.loading(null));

        RuleDatabase.databaseExecutor.execute(() -> {
            try {
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


    //endregion
}
