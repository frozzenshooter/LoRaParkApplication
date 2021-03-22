package de.uniulm.loraparkapplication.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "sensor_table",
        foreignKeys = {@ForeignKey(entity = Rule.class,
        parentColumns = "id",
        childColumns = "rule_id",
        onDelete = ForeignKey.CASCADE)
})
public class Sensor {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private Integer id;

    /*@ColumnInfo(name = "domain")
    @NonNull
    private String domain;*/

    @ColumnInfo(name = "sensor_id")
    @NonNull
    private String sensorId;

    /*@ColumnInfo(name = "value")
    @NonNull
    private String value;*/

    @ColumnInfo(name = "rule_id", index = true)
    @NonNull
    private String ruleId;

    /*@ColumnInfo(name = "rule_sensor_id")
    private String ruleSensorId;*/

    //region Getters and setters

    @NotNull
    public Integer getId() {
        return id;
    }

    public void setId(@NotNull Integer id) {
        this.id = id;
    }


    /*@NotNull
    public String getDomain() {
        return domain;
    }

    public void setDomain(@NotNull String domain) {
        this.domain = domain;
    }*/


    @NotNull
    public String getSensorId(){
        return sensorId;
    }

    public void setSensorId(@NotNull String sensorId){
        this.sensorId = sensorId;
    }

    /*@NotNull
    public String getValue() {
        return value;
    }

    public void setValue(@NotNull String value) {
        this.value = value;
    }*/

    @NotNull
    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(@NotNull String ruleId) {
        this.ruleId = ruleId;
    }

    /*public String getRuleSensorId() {
        return ruleSensorId;
    }

    public void setRuleSensorId(@NonNull String ruleSensorId) {
        this.ruleSensorId = ruleSensorId;
    }*/

    //endregion

}
