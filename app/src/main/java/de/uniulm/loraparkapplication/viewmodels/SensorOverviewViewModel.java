package de.uniulm.loraparkapplication.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import de.uniulm.loraparkapplication.models.SensorDescription;
import de.uniulm.loraparkapplication.repositories.SensorDescriptionRepository;

public class SensorOverviewViewModel extends ViewModel {

    private MutableLiveData<List<SensorDescription>> mSensorDescriptions;
    private SensorDescriptionRepository mSensorDescriptionRepository;

    public void init() {
        if(mSensorDescriptions != null){
            return;
        }
        mSensorDescriptionRepository = SensorDescriptionRepository.getInstance();
        mSensorDescriptions = mSensorDescriptionRepository.getSensorDescriptions();
    }

    public LiveData<List<SensorDescription>> getSensorDescriptions(){
        return mSensorDescriptions;
    }
}
