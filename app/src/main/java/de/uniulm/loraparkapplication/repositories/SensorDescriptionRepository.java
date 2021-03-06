package de.uniulm.loraparkapplication.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.uniulm.loraparkapplication.BuildConfig;
import de.uniulm.loraparkapplication.models.Location;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.SensorDescription;
import de.uniulm.loraparkapplication.network.HttpClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Handles the retrieval of the descriptions of the sensors
 *
 */
public class SensorDescriptionRepository {

    private static SensorDescriptionRepository instance;

    public static SensorDescriptionRepository getInstance() {
        if(instance == null){
            instance = new SensorDescriptionRepository();
        }
        return instance;
    }


    public MutableLiveData<Resource<List<SensorDescription>>> getSensorDescriptions() {

        MutableLiveData<Resource<List<SensorDescription>>> data = new MutableLiveData<>();

        data.setValue(Resource.loading(null));

        HttpClient.getInstance().newCall(HttpClient.getSensorDescriptionsRequest()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                data.postValue(Resource.error("Retrieval failed", null));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try {
                    // parse the data using Gson
                    Gson gson = new Gson();
                    SensorDescription[] sensorDescriptions = gson.fromJson(response.body().charStream(), SensorDescription[].class);

                    data.postValue(Resource.success(Arrays.asList(sensorDescriptions)));

                }catch(Exception ex){

                    data.postValue(Resource.error("Parsing failed", null));
                }
            }
        });

        return data;
    }
}
