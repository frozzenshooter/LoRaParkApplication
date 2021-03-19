package de.uniulm.loraparkapplication.repositories;

import android.app.Application;
import android.database.SQLException;
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
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RuleDataRepository {

    //region Singleton creation

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

    //endregion

    //region Rule access

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
     *  Returns a single rule
     *
     * @param ruleId the id of the requested rule
     * @return the rule with the requested ruleId
     */
    public LiveData<Rule> getRule(String ruleId) {
        return mRuleDao.findRule(ruleId);
    }

    //endregion

    //region CompleteRule access

    /**
     * Returns all local complete rules (with sensors,geofences and actions) filtered by state: if they are currently active
     *
     * @return list with filtered completeRules
     */
    public LiveData<List<CompleteRule>> getCompleteRules(Boolean isActive){
        return mRuleDao.findCompleteRules(isActive);
    }

    /**
     *
     * @param ruleId
     * @return
     * @throws Exception
     */
    public CompleteRule getCompleteRule(@NonNull String ruleId) throws Exception{
        return mRuleDao.findCompleteRule(ruleId);
    }

    //endregion

    //region Rule creation/update/deletion

    /**
     * Inserts the completeRule in the database - will replace an existing one
     *
     * @param completeRule the completeRule to insert
     */
    public void insertCompleteRule(@NonNull CompleteRule completeRule){
           mRuleDao.insertCompleteRule(completeRule);
    }

    /**
     * Deletes all rules
     *
     * @return completable
     */
    public void deleteAllRules() throws Exception{
        mRuleDao.deleteAllRules();
    }

    /**
     * Delete this specific rule
     *
     * @param rule the rule to delete
     */
    public void deleteRule(Rule rule) throws Exception {
        mRuleDao.delete(rule);
    }

    /**
     * Updates the specific rule
     *
     * @param rule the rule to update
     * @throws Exception
     */
    public void updateRule(@NonNull Rule rule) throws Exception{
        mRuleDao.update(rule);
    }

    //endregion
}
