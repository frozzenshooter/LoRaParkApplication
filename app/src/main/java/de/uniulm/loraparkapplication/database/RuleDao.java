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
    void insert(Rule rule);

    @Update
    void update(Rule rule);

    @Delete
    void delete(Rule rule);

    @Query("DELETE FROM rule_table")
    void deleteAllRules();

    @Query("SELECT * FROM rule_table")
    List<Rule> findAll();

    @Query("SELECT * FROM rule_table WHERE is_active = :isActive")
    List<Rule> findRules(Boolean isActive);
}
