package de.uniulm.loraparkapplication.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.SensorDescription;
import de.uniulm.loraparkapplication.repositories.SensorDescriptionRepository;

/**
 * ViewModel that handles the data for the sensor descriptions
 */
public class SensorOverviewViewModel extends ViewModel {

    private MutableLiveData<Resource<List<SensorDescription>>> mSensorDescriptions;

    public void init() {
        if(mSensorDescriptions != null){
            return;
        }
        SensorDescriptionRepository mSensorDescriptionRepository = SensorDescriptionRepository.getInstance();
        mSensorDescriptions = mSensorDescriptionRepository.getSensorDescriptions();
    }

    public LiveData<Resource<List<SensorDescription>>> getSensorDescriptions(){
        return mSensorDescriptions;
    }
}
