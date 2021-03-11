package de.uniulm.loraparkapplication.repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import de.uniulm.loraparkapplication.database.RuleDao;
import de.uniulm.loraparkapplication.database.RuleDatabase;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.Rule;

public class RuleDataRepository {

    private RuleDao mRuleDao;


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

    public MutableLiveData<Resource<List<Rule>>> getAllRules() {

        MutableLiveData<Resource<List<Rule>>> data = new MutableLiveData<>();

        data.setValue(Resource.loading(null));

        RuleDatabase.databaseExecutor.execute(() -> {
            List<Rule> rules = mRuleDao.findAll();
            data.postValue(Resource.success(rules));
        });

        return data;
    }

    public MutableLiveData<Resource<List<Rule>>> getRules(Boolean isActive) {

        MutableLiveData<Resource<List<Rule>>> data = new MutableLiveData<>();

        data.setValue(Resource.loading(null));

        RuleDatabase.databaseExecutor.execute(() -> {
            List<Rule> rules = mRuleDao.findRules(isActive);
            data.postValue(Resource.success(rules));
        });

        return data;
    }

    public void insertRule(@NonNull Rule rule) {

        //TODO: add error handling

        RuleDatabase.databaseExecutor.execute(() -> {
            mRuleDao.insert(rule);
        });
    }

    public void deleteAllRules() {
        //TODO: add error handling

        RuleDatabase.databaseExecutor.execute(() -> {
            mRuleDao.deleteAllRules();
        });
    }
}
