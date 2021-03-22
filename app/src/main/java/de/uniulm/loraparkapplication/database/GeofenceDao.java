package de.uniulm.loraparkapplication.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import de.uniulm.loraparkapplication.models.GeofenceTracker;

@Dao
public abstract class GeofenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(GeofenceTracker geofenceTracker);

    @Query("DELETE FROM geofence_tracker_table")
    public abstract void deleteAllGeofenceTrackers();

    @Query("SELECT * FROM geofence_tracker_table")
    public abstract LiveData<List<GeofenceTracker>> getAllGeofenceTrackers();

}
