package de.uniulm.loraparkapplication.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.SensorDescription;
import de.uniulm.loraparkapplication.models.SensorValue;
import de.uniulm.loraparkapplication.network.HttpClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Handles the retrieval of data from a sensor
 */
public class SensorDataRepository {

    private static SensorDataRepository instance;

    public static SensorDataRepository getInstance() {
        if(instance == null){
            instance = new SensorDataRepository();
        }
        return instance;
    }

    public MutableLiveData<Resource<List<SensorValue>>> getSensorValues(@NonNull String sensorId) {
        MutableLiveData<Resource<List<SensorValue>>> data = new MutableLiveData<>();

        data.setValue(Resource.error("Not implemented yet.", null));

//        HttpClient.getInstance().newCall(HttpClient.getSensorDetailRequest(sensorId)).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//
//                data.postValue(Resource.error("Retrieval failed", null));
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//
//                try {
//                    // parse the data using Gson
//                    Gson gson = new Gson();
//                    SensorDescription[] sensorDescriptions = gson.fromJson(response.body().charStream(), SensorDescription[].class);
//
//                    data.postValue(Resource.success(Arrays.asList(sensorDescriptions)));
//
//                }catch(Exception ex){
//
//                    data.postValue(Resource.error("Parsing failed", null));
//                }
//            }
//        });

        return data;
    }

}