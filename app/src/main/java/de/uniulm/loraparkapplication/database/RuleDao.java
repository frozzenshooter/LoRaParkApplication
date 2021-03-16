package de.uniulm.loraparkapplication.database;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.sql.SQLException;
import java.util.List;

import de.uniulm.loraparkapplication.models.Action;
import de.uniulm.loraparkapplication.models.CompleteRule;
import de.uniulm.loraparkapplication.models.Geofence;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.models.Sensor;

@Dao
public abstract class RuleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Rule rule) throws Exception;

    @Update
    public abstract void update(Rule rule) throws Exception;

    @Delete
    public abstract void delete(Rule rule) throws Exception;

    @Query("DELETE FROM rule_table")
    public abstract void deleteAllRules() throws Exception;

    @Query("SELECT * FROM rule_table WHERE is_active = :isActive ORDER BY name ASC")
    public abstract LiveData<List<Rule>> findRules(Boolean isActive);

    @Query("SELECT * FROM rule_table ORDER BY name ASC")
    public abstract LiveData<List<Rule>> findAllRules();

    @Query("SELECT * FROM rule_table WHERE id = :ruleId")
    public abstract LiveData<Rule> findRule(String ruleId);

    @Query("SELECT COUNT(id) FROM rule_table WHERE id = :ruleId")
    public abstract Integer getAmountOfRules(String ruleId);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Sensor sensor) throws Exception;

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Geofence geofence) throws Exception;

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Action action) throws Exception;

    @Transaction
    public void insertCompleteRule(CompleteRule completeRule){
        try {
            Rule rule = completeRule.getRule();
            insert(rule);

            for (Sensor sensor : completeRule.getSensors()) {
                insert(sensor);
            }

            for (Geofence geofence : completeRule.getGeofences()) {
                insert(geofence);
            }

            for (Action action : completeRule.getActions()) {
                insert(action);
            }

        }catch(Exception ex){
            Log.e("SQL_EXCPETION", "Not able to insert complete rule: "+ ex.getMessage());
        }
    }

    @Query("SELECT * FROM rule_table ORDER BY name ASC")
    public abstract LiveData<List<CompleteRule>> findCompleteRules();

    @Query("SELECT * FROM rule_table WHERE is_active = :isActive ORDER BY name ASC")
    public abstract LiveData<List<CompleteRule>> findCompleteRules(Boolean isActive);

    @Query("SELECT * FROM rule_table WHERE id = :ruleId")
    public abstract LiveData<CompleteRule> findCompleteRule(String ruleId);

    @Query("SELECT * FROM rule_table WHERE id = :ruleId")
    public abstract CompleteRule getCompleteRule(String ruleId);
}
