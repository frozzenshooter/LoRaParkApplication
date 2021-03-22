package de.uniulm.loraparkapplication.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.uniulm.loraparkapplication.models.GeofenceTracker;
import de.uniulm.loraparkapplication.repositories.FenceTestRepository;

public class FenceTestViewModel extends AndroidViewModel {

    private LiveData<List<GeofenceTracker>> mGeofenceTrackers;
    private FenceTestRepository mFenceTestRepository;

    public FenceTestViewModel(@NonNull Application application) {
        super(application);
        this.mFenceTestRepository = FenceTestRepository.getInstance(application);
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
}
