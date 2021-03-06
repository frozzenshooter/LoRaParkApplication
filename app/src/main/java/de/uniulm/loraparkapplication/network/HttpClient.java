package de.uniulm.loraparkapplication.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpClient {

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
}
