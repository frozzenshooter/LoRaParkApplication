package de.uniulm.loraparkapplication.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.SensorValue;
import de.uniulm.loraparkapplication.repositories.SensorDataRepository;

public class SensorDetailViewModel extends ViewModel {

    private MutableLiveData<Resource<List<SensorValue>>> mSensorValues;
    private SensorDataRepository mSensorDataRepository;

    public void init(@NonNull String sensorId) {
        if(mSensorDataRepository != null){
            return;
        }
        mSensorDataRepository = SensorDataRepository.getInstance();
        mSensorValues = mSensorDataRepository.getSensorValues(sensorId);
    }

    public LiveData<Resource<List<SensorValue>>> getSensorValues(){
        return mSensorValues;
    }
}
