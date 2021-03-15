package de.uniulm.loraparkapplication.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "rule_table")
public class Rule {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "condition")
    @NonNull
    private String condition;

    @ColumnInfo(name = "is_active")
    private Boolean isActive;

    //region Additional fields for rule engine

    @ColumnInfo(name = "last_triggered")
    private Long lastTriggered;

    @ColumnInfo(name = "was_triggered")
    private Boolean wasTriggered;

    //endregion

    //region Getters and setters

    @NotNull
    public String getId() {
        return id;
    }

    public void setId(@NotNull String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @NotNull
    public String getCondition() {
        return condition;
    }

    public void setCondition(@NotNull String condition) {
        this.condition = condition;
    }


    public void setIsActive(Boolean isActive){
        this.isActive = isActive;
    }

    public Boolean getIsActive(){
        return this.isActive;
    }


    public Long getLastTriggered() {
        return lastTriggered;
    }

    public void setLastTriggered(Long lastTriggered) {
        this.lastTriggered = lastTriggered;
    }


    public Boolean getWasTriggered() {
        return wasTriggered;
    }

    public void setWasTriggered(Boolean wasTriggered) {
        this.wasTriggered = wasTriggered;
    }

    //endregion
}
