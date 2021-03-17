package de.uniulm.loraparkapplication.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.SensorDetail;
import de.uniulm.loraparkapplication.network.HttpClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Handles the retrieval of data from a sensor
 */
public class SensorValuesRepository {

    private static SensorValuesRepository instance;

    public static SensorValuesRepository getInstance() {
        if(instance == null){
            instance = new SensorValuesRepository();
        }
        return instance;
    }

    /**
     * Loads all sensor values from a server.
     *
     * E.g. name = temperature, value = 25 and unit = °C
     *
     * @param sensorIds list of ids of the sensors
     * @return a map with the sensor id and a map with the domain and values
     */
    public MutableLiveData<Resource<Map<String, Map<String, Map<String, Object>>>>> getSensorValues(@NonNull List<String> sensorIds) {
        MutableLiveData<Resource<Map<String, Map<String, Map<String, Object>>>>> data = new MutableLiveData<>();

        data.setValue(Resource.loading(null));

        HttpClient.getInstance().newCall(HttpClient.getSensorValuesRequest(sensorIds)).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                data.postValue(Resource.error("Retrieval failed", null));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    Gson gson = new Gson();

                    Type type = new TypeToken<Map<String, Map<String, Map<String, Object>>>>(){}.getType();
                    Map<String, Map<String, Map<String, Object>>> sensorValues = gson.fromJson(response.body().charStream(), type);

                    data.postValue(Resource.success(sensorValues));
                } catch(Exception ex) {
                    data.postValue(Resource.error("Parsing failed", null));
                }
            }
        });

        return data;
    }

}
