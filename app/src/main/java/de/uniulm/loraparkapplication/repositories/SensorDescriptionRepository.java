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

public class SensorDescriptionRepository {

    private static SensorDescriptionRepository instance;
    private final ArrayList<SensorDescription> dataSet = new ArrayList<>();

    public static SensorDescriptionRepository getInstance() {
        if(instance == null){
            instance = new SensorDescriptionRepository();
        }
        return instance;
    }


    public MutableLiveData<List<SensorDescription>> getSensorDescriptions() {

        MutableLiveData<List<SensorDescription>> data = new MutableLiveData<>();

        HttpClient.getInstance().newCall(HttpClient.getSensorDescriptionsRequest()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //TODO: HANDLE THE EXCEPTION AND HAND IT OVER TO THE UI by using the .models.Resource class
                Log.e("OKHTTP FAILURE", "COULDNT RETRIEVE THE DATA");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                // parse the data using Gson
                Gson gson = new Gson();
                SensorDescription[] sensorDescriptions = gson.fromJson(response.body().charStream(), SensorDescription[].class);

                data.postValue(Arrays.asList(sensorDescriptions));
            }
        });

        return data;
    }
}
