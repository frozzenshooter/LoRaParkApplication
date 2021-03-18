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

    //region Rule queries LiveData

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

    //endregion

    //region Rule creation

    /**
     * Insert a complete rule (overrides an existing one)
     *
     * @param completeRule the rule to insert
     * @return
     */
    public Completable insertCompleteRule(@NonNull CompleteRule completeRule){

        return Completable.defer(() -> {

            try {
                mRuleDao.insertCompleteRule(completeRule);
                return Completable.complete();
            } catch (Exception e) {
                return Completable.error(e);
            }
        });
    }

    //endregion

    //region Rule access (RxJava)

    /**
     * Get a single complete rule
     *
     * @param ruleId the id of the requested rule
     * @return the rule
     */
    public Single<CompleteRule> getSingleCompleteRule(@NonNull String ruleId){
        return Single.defer(()->{
            try{
                CompleteRule rule = mRuleDao.getCompleteRule(ruleId);
                return Single.just(rule);
            }catch(Exception ex){
                return Single.error(ex);
            }
        });
    }

    /**
     * Checks if a rule exists
     * @param ruleId the id to check
     *
     * @return true if the rule exists - otherwise false
     */
    public Single<Boolean> existsRule(@NonNull String ruleId){

        return Single.defer(()->{
            try {
                Integer count = mRuleDao.getAmountOfRules(ruleId);
                if (count > 0) {
                    return Single.just(true);
                } else {
                    return Single.just(false);
                }
            }catch(Exception ex){
                return Single.error(ex);
            }
        });
    }

    public Boolean existsRuleSync(@NonNull String ruleId){
        Integer count = mRuleDao.getAmountOfRules(ruleId);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Deletes all rules
     *
     * @return completable
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
     * Delete this specific rule
     *
     * @param rule
     */
    public Completable deleteRule(Rule rule) {

        return Completable.defer(() -> {
            try{
                mRuleDao.delete(rule);
                return Completable.complete();
            }catch(Exception ex){
                return Completable.error(ex);
            }
        });
    }

    public Completable updateRule(@NonNull Rule rule){

        return Completable.defer(() -> {
            try{
                mRuleDao.update(rule);
                return Completable.complete();
            }catch(Exception ex){
                return Completable.error(ex);
            }
        });
    }

    public Completable updateRule(@NonNull CompleteRule completeRule){

        return updateRule(completeRule.getRule());
    }

    //endregion
}
