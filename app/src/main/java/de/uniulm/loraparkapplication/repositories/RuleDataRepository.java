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
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

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
     * Insert a complete rule (overrides an existing one)
     *
     * @param completeRule the rule to insert
     * @return
     */
    public Observable<String> insertCompleteRule(@NonNull CompleteRule completeRule){

        return Observable.defer(() -> {

            try {
                mRuleDao.insertCompleteRule(completeRule);
                return Observable.just(completeRule.getRule().getName());
            } catch (Exception e) {
                return Observable.error(e);
            }
        });
    }

    public Integer getAmountOfRules(@NonNull String ruleId){
        return mRuleDao.getAmountOfRules(ruleId);
    }

    /**
     * Deletes all rules
     *
     */
    public Completable deleteAllRules() {

        return Completable.defer(() ->{
            try {
                mRuleDao.deleteAllRules();
                return Completable.complete();
            }catch(Exception e){
                return Completable.error(e);
            }
        });
    }

    /**
     * Delete the specific rule
     *
     * @param rule
     */
    public void deleteRule(Rule rule) {
        try{
            mRuleDao.delete(rule);
        }catch(Exception ex){
            Log.e("", "");
        }
    }

    //endregion
}
