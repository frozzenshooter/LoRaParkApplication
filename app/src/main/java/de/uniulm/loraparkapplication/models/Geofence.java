package de.uniulm.loraparkapplication.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "geofence_table",
        foreignKeys = {@ForeignKey(entity = Rule.class,
        parentColumns = "id",
        childColumns = "rule_id",
        onDelete = ForeignKey.CASCADE)
})
public class Geofence {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    @NonNull
    private Integer id;

    @ColumnInfo(name = "geofence_id")
    @NonNull
    private String geofenceId;

    @ColumnInfo(name = "rule_id")
    @NonNull
    private String ruleId;

    @ColumnInfo(name = "latitude")
    @NonNull
    private Double latitude;

    @ColumnInfo(name = "longitude")
    @NonNull
    private Double longitude;

    @ColumnInfo(name = "radius")
    @NonNull
    private Integer radius;

    //region Getters and setters

    @NotNull
    public Integer getId() {
        return id;
    }

    public void setId(@NotNull Integer id) {
        this.id = id;
    }

    @NotNull
    public String getGeofenceId() {
        return geofenceId;
    }

    public void setGeofenceId(@NotNull String id) {
        this.geofenceId = id;
    }

    @NotNull
    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(@NotNull String ruleId) {
        this.ruleId = ruleId;
    }

    @NotNull
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(@NotNull Double latitude) {
        this.latitude = latitude;
    }

    @NotNull
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(@NotNull Double longitude) {
        this.longitude = longitude;
    }

    @NotNull
    public Integer getRadius() {
        return radius;
    }

    public void setRadius(@NotNull Integer radius) {
        this.radius = radius;
    }

    //endregion
}
