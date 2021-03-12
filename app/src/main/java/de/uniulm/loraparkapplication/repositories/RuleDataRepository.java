package de.uniulm.loraparkapplication.repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import de.uniulm.loraparkapplication.database.RuleDao;
import de.uniulm.loraparkapplication.database.RuleDatabase;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.Rule;
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

    //endregion

    //region Rule creation and deletion

    public Observable<Resource<Boolean>> insertRule(@NonNull Rule rule) {

        RuleDatabase.databaseExecutor.execute(() -> {
                mRuleDao.insert(rule);
        });

        return Observable.create(new ObservableOnSubscribe<Resource<Boolean>>() {
            @Override
            public void subscribe(final ObservableEmitter<Resource<Boolean>> emitter) throws Exception {

                RuleDatabase.databaseExecutor.execute(() -> {

                    if(!emitter.isDisposed()){
                        emitter.onNext(Resource.success(true));
                        emitter.onComplete();
                    }
                });
            }
        });
    }

    public Observable<Resource<Boolean>> deleteAllRules() {
        RuleDatabase.databaseExecutor.execute(() -> {
            mRuleDao.deleteAllRules();
        });
        return Observable.create(new ObservableOnSubscribe<Resource<Boolean>>() {

            @Override
            public void subscribe(final ObservableEmitter<Resource<Boolean>> emitter) throws Exception {

                RuleDatabase.databaseExecutor.execute(() -> {


                    if(!emitter.isDisposed()){
                        emitter.onNext(Resource.success(true));
                        emitter.onComplete();
                    }

                });
            }
        });
    }

    //endregion

    //region Download and process new rules

    public void downloadNewRules(List<String> ruleIds){
        for(String ruleId: ruleIds){
            downloadNewRule(ruleId);
        }
    }

    public void downloadNewRule(String ruleId){

        HttpClient.getInstance().newCall(HttpClient.getRule(ruleId)).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                // ERROR
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try {
                    // parse the data using Gson
                    Gson gson = new Gson();
                    Rule rule = gson.fromJson(response.body().charStream(), Rule.class);

                    insertRule(rule);

                }catch(Exception ex){

                    //ERROR
                }
            }

        });
    }

    //endregion
}
