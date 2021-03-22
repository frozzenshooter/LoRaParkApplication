package de.uniulm.loraparkapplication.repositories;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.uniulm.loraparkapplication.database.GeofenceDao;
import de.uniulm.loraparkapplication.database.RuleDatabase;
import de.uniulm.loraparkapplication.models.GeofenceTracker;

public class FenceTestRepository {

    private static FenceTestRepository instance;

    private final GeofenceDao mGeofenceTrackerDao;
    private final RuleDatabase db;

    public static FenceTestRepository getInstance(@NonNull Application application) {
        if (instance == null) {
            instance = new FenceTestRepository(application);
        }
        return instance;
    }

    private FenceTestRepository(@NonNull Application application) {
        db = RuleDatabase.getDatabase(application);
        this.mGeofenceTrackerDao = db.geofenceDao();
    }

    public LiveData<List<GeofenceTracker>> getAllGeofenceTrackers(){
        return mGeofenceTrackerDao.getAllGeofenceTrackers();
    }

    public void insertGeofenceTracker(GeofenceTracker geofenceTracker){
        db.getQueryExecutor().execute(() -> {
            this.mGeofenceTrackerDao.insert(geofenceTracker);
        });
    }

    public void deleteAllGeofenceTrackers(){
        db.getQueryExecutor().execute(this.mGeofenceTrackerDao::deleteAllGeofenceTrackers);
    }
}
