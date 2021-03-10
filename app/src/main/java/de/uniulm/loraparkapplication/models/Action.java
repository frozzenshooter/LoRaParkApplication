package de.uniulm.loraparkapplication.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "action_table",
        foreignKeys = {@ForeignKey(entity = Rule.class,
        parentColumns = "id",
        childColumns = "rule_id",
        onDelete = ForeignKey.CASCADE)
})
public class Action {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private Integer id;

    @ColumnInfo(name="action")
    @NonNull
    private String action;

    @ColumnInfo(name="data")
    @NonNull
    private String data;

    @ColumnInfo(name = "rule_id")
    @NonNull
    private String ruleId;

    //region Getters and setters

    @NotNull
    public Integer getId() {
        return id;
    }

    public void setId(@NotNull Integer id) {
        this.id = id;
    }

    @NotNull
    public String getAction() {
        return action;
    }

    public void setAction(@NotNull String action) {
        this.action = action;
    }

    @NotNull
    public String getData() {
        return data;
    }

    public void setData(@NotNull String data) {
        this.data = data;
    }

    @NotNull
    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(@NotNull String ruleId) {
        this.ruleId = ruleId;
    }

    //endregion

}
