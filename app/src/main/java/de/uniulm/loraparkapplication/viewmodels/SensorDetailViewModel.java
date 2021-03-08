package de.uniulm.loraparkapplication.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.SensorDetail;
import de.uniulm.loraparkapplication.repositories.SensorDataRepository;

public class SensorDetailViewModel extends ViewModel {

    private MutableLiveData<Resource<List<SensorDetail>>> mSensorValues;
    private SensorDataRepository mSensorDataRepository;

    public void init(@NonNull String sensorId) {
        if(mSensorDataRepository != null){
            return;
        }
        mSensorDataRepository = SensorDataRepository.getInstance();
        mSensorValues = mSensorDataRepository.getSensorDetails(sensorId);
    }

    public LiveData<Resource<List<SensorDetail>>> getSensorValues(){
        return mSensorValues;
    }
}
