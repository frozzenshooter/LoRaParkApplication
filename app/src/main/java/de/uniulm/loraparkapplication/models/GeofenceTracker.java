package de.uniulm.loraparkapplication.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "geofence_tracker_table")
public class GeofenceTracker {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Integer id;

    @ColumnInfo(name = "geofence_id")
    private String geofenceId;

    @ColumnInfo(name = "fence_state")
    private Integer fenceState;

    @ColumnInfo(name = "prev_fence_state")
    private Integer previousFenceState;

    @ColumnInfo(name = "last_updated")
    private Long lastUpdated;

    @ColumnInfo(name = "insertion_time")
    private Long insertionTime;

    @ColumnInfo(name ="was_triggered_manually")
    private Boolean wasTriggerdeManually;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGeofenceId() {
        return geofenceId;
    }

    public void setGeofenceId(String geofenceId) {
        this.geofenceId = geofenceId;
    }

    public Integer getFenceState() {
        return fenceState;
    }

    public void setFenceState(Integer fenceState) {
        this.fenceState = fenceState;
    }

    public Integer getPreviousFenceState() {
        return previousFenceState;
    }

    public void setPreviousFenceState(Integer previousFenceState) {
        this.previousFenceState = previousFenceState;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Long getInsertionTime() {
        return insertionTime;
    }

    public void setInsertionTime(Long insertionTime) {
        this.insertionTime = insertionTime;
    }

    public Boolean getWasTriggerdeManually() {
        return wasTriggerdeManually;
    }

    public void setWasTriggerdeManually(Boolean wasTriggerdeManually) {
        this.wasTriggerdeManually = wasTriggerdeManually;
    }
}