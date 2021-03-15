package de.uniulm.loraparkapplication.repositories;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import de.uniulm.loraparkapplication.database.RuleDao;
import de.uniulm.loraparkapplication.database.RuleDatabase;
import de.uniulm.loraparkapplication.models.CompleteRule;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.Rule;

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
    public LiveData<List<Rule>> getAllRules(){
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
     * Returns all local complete rules (with sensors,geofences and actions)
     *
     * @return list with all completeRules
     */
    public LiveData<List<CompleteRule>> getCompleteRules(){
        return mRuleDao.findCompleteRules();
    }

    /**
     * Returns a specific complete rule (with sensors,geofences and actions)
     *
     * @param ruleId the id of the requested rule
     * @return the complete rule
     */
    public LiveData<CompleteRule> getCompleteRule(@NonNull String ruleId){
        return mRuleDao.findCompleteRule(ruleId);
    }

    /**
     * Returns all local complete rules (with sensors,geofences and actions) filtered by state: if they are currently active
     *
     * @return list with filtered completeRules
     */
    public LiveData<List<CompleteRule>> getCompleteRules(Boolean isActive){
        return mRuleDao.findCompleteRules(isActive);
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
     * Insert a complete rule (overrides an existing one)
     *
     * @param completeRule the rule to insert
     * @return
     */
    public LiveData<Resource<String>> insertCompleteRule(@NonNull CompleteRule completeRule){
        MutableLiveData<Resource<String>> data = new MutableLiveData<>();
        data.postValue(Resource.loading(null));

        RuleDatabase.databaseExecutor.execute(() -> {
            try {
                mRuleDao.insertCompleteRule(completeRule);
                data.postValue(Resource.success(""));
            }catch(Exception e){
                data.postValue(Resource.error(e.getMessage(), ""));
            }
        });

        return data;
    }

    public Integer getAmountOfRules(@NonNull String ruleId){
        return mRuleDao.getAmountOfRules(ruleId);
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
                mRuleDao.deleteAllRules();
                data.postValue(Resource.success(""));
            }catch(Exception e){
                data.postValue(Resource.error(e.getMessage(), ""));
            }
        });

        return data;
    }

    public void deleteRule(Rule rule) {

        // TODO: SOLVE DELETION WITH RX JAVA
        try{
            mRuleDao.delete(rule);
        }catch(Exception ex){
            Log.e("", "");
        }
    }

    //endregion
}
