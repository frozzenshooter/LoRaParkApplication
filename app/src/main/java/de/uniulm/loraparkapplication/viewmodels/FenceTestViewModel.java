package de.uniulm.loraparkapplication.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.gms.awareness.fence.FenceState;

import java.util.List;

import de.uniulm.loraparkapplication.models.Geofence;
import de.uniulm.loraparkapplication.models.GeofenceTracker;
import de.uniulm.loraparkapplication.repositories.FenceTestRepository;
import de.uniulm.loraparkapplication.repositories.GeofenceRepository;

public class FenceTestViewModel extends AndroidViewModel {

    private LiveData<List<GeofenceTracker>> mGeofenceTrackers;
    private FenceTestRepository mFenceTestRepository;
    private GeofenceRepository mGeofenceRepository;

    public FenceTestViewModel(@NonNull Application application) {
        super(application);
        this.mFenceTestRepository = FenceTestRepository.getInstance(application);
        this.mGeofenceRepository = GeofenceRepository.getInstance(application);

        mGeofenceTrackers = this.mFenceTestRepository.getAllGeofenceTrackers();
    }

    public LiveData<List<GeofenceTracker>> getGeofenceTrackers(){
        return mGeofenceTrackers;
    }

    public void deleteAllGeofenceTrackers(){
        this.mFenceTestRepository.deleteAllGeofenceTrackers();
    }

    public void insertGeofenceTracker(GeofenceTracker geofenceTracker){
        this.mFenceTestRepository.insertGeofenceTracker(geofenceTracker);
    }

    public void addGeofence(Geofence geofence) throws Exception{

        GeofenceTracker geofenceTracker = new GeofenceTracker();
        geofenceTracker.setInsertionTime(System.currentTimeMillis());
        geofenceTracker.setLastUpdated(System.currentTimeMillis());
        geofenceTracker.setFenceState(FenceState.UNKNOWN);
        geofenceTracker.setPreviousFenceState(FenceState.UNKNOWN);
        geofenceTracker.setInserted(true);
        geofenceTracker.setDeleted(false);
        geofenceTracker.setWasTriggerdeManually(false);
        geofenceTracker.setGeofenceId(geofence.getGeofenceId());

        this.insertGeofenceTracker(geofenceTracker);

        this.mGeofenceRepository.createGeofence(geofence);
    }

    public void deleteGeofence(String geofenceId){

        GeofenceTracker geofenceTracker = new GeofenceTracker();
        geofenceTracker.setInsertionTime(System.currentTimeMillis());
        geofenceTracker.setLastUpdated(System.currentTimeMillis());
        geofenceTracker.setFenceState(FenceState.UNKNOWN);
        geofenceTracker.setPreviousFenceState(FenceState.UNKNOWN);
        geofenceTracker.setInserted(false);
        geofenceTracker.setDeleted(true);
        geofenceTracker.setWasTriggerdeManually(false);
        geofenceTracker.setGeofenceId(geofenceId);

        this.insertGeofenceTracker(geofenceTracker);

        this.mGeofenceRepository.deleteGeofence(geofenceId);
    }
}
