package de.uniulm.loraparkapplication.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.uniulm.loraparkapplication.models.Rule;

@Dao
public interface RuleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Rule rule) throws Exception;

    @Update
    void update(Rule rule) throws Exception;

    @Delete
    void delete(Rule rule) throws Exception;

    @Query("DELETE FROM rule_table")
    void deleteAllRules() throws Exception;

    @Query("SELECT * FROM rule_table WHERE is_active = :isActive")
    LiveData<List<Rule>> findRules(Boolean isActive);

    @Query("SELECT * FROM rule_table")
    LiveData<List<Rule>> findAllRules();

    @Query("SELECT * FROM rule_table WHERE id = :ruleId")
    LiveData<Rule> findRule(String ruleId);

}
