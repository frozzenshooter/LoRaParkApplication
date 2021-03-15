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

    private final static String sensorDescriptionsURL = "https://raw.githubusercontent.com/frozzenshooter/LoRaParkApplication/main/sensor_descriptions/sensors.json";
    private final static String sensorDetailsURL = "https://raw.githubusercontent.com/frozzenshooter/LoRaParkApplication/main/sensor_descriptions/sensor_values.json";
    private final static String downloadRuleURL = "https://raw.githubusercontent.com/frozzenshooter/LoRaParkApplication/main/rules/rules.json";
    private final static String rule1URL = "https://raw.githubusercontent.com/frozzenshooter/LoRaParkApplication/rule_download/rules/rule.json";
    private final static String rule2URL = " https://raw.githubusercontent.com/frozzenshooter/LoRaParkApplication/rule_download/rules/rule2.json";
    private final static String rule3URL = "https://raw.githubusercontent.com/frozzenshooter/LoRaParkApplication/rule_download/rules/rule3.json";

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

        Request req = new Request.Builder().url(HttpClient.sensorDetailsURL).build();
        return req;
    }

    public static Request getDownloadRuleRequest(){
        Request req = new Request.Builder().url(HttpClient.downloadRuleURL).build();
        return req;
    }

    public static Request getRule(@NonNull String ruleId) {
        //TODO: CORRETC URL

        if ("rule2".equals(ruleId)) {
            return new Request.Builder().url(HttpClient.rule2URL).build();
        } else if ("rule3".equals(ruleId)) {
            return new Request.Builder().url(HttpClient.rule3URL).build();
        } else if ("rule4".equals(ruleId)){
            return new Request.Builder().url(HttpClient.rule3URL+"rule4.json").build();
        }else{
            return new Request.Builder().url(HttpClient.rule1URL).build();
        }
    }
}
