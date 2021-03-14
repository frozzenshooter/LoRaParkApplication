package de.uniulm.loraparkapplication.repositories;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import de.uniulm.loraparkapplication.models.DownloadRule;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.network.HttpClient;
import io.reactivex.rxjava3.core.Completable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DownloadRuleRepository {

    private static DownloadRuleRepository instance;

    public static DownloadRuleRepository getInstance() {
        if(instance == null){
            instance = new DownloadRuleRepository();
        }
        return instance;
    }


    /**
     * Loads the list with downloadable rules from the server
     *
     * @return list with rules on the server
     */
    public MutableLiveData<Resource<List<DownloadRule>>> getDownloadRules() {

        MutableLiveData<Resource<List<DownloadRule>>> data = new MutableLiveData<>();

        data.setValue(Resource.loading(null));

        HttpClient.getInstance().newCall(HttpClient.getDownloadRuleRequest()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                data.postValue(Resource.error("Retrieval failed", null));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try {
                    // parse the data using Gson
                    Gson gson = new Gson();
                    DownloadRule[] sensorDescriptions = gson.fromJson(response.body().charStream(), DownloadRule[].class);

                    data.postValue(Resource.success(Arrays.asList(sensorDescriptions)));

                }catch(Exception ex){

                    data.postValue(Resource.error("Parsing failed", null));
                }
            }
        });

        return data;
    }

}
