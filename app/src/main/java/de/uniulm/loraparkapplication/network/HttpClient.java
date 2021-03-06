package de.uniulm.loraparkapplication.network;

import android.util.Log;

import androidx.annotation.NonNull;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Allows to connect and retrieve HTTP resources
 */
public class HttpClient {

    private static final String HTTP_CLIENT_CLASSNAME = HttpClient.class.getName();

    private final static String sensorDescriptionsURL = "https://raw.githubusercontent.com/frozzenshooter/LoRaParkApplication/osmdroid_integration/sensor_descriptions/sensors.json";

    private static OkHttpClient instance;

    public static OkHttpClient getInstance(){

        if(instance == null){
            instance = new OkHttpClient();
        }

        return instance;
    }

    public static Request getSensorDescriptionsRequest(){
        Request req = new Request.Builder().url(HttpClient.sensorDescriptionsURL).build();
        return req;
    }

    public static Request getSensorDetailRequest(@NonNull String sensorId){
        Log.i(HTTP_CLIENT_CLASSNAME,"Creation of request for sensorId: " + sensorId + "started.");

        //TODO: implement correct request for API
        Request req = new Request.Builder().url(HttpClient.sensorDescriptionsURL).build();
        return req;
    }
}
