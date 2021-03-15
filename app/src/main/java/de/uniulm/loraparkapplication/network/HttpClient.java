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
    private final static String rule1URL = "https://raw.githubusercontent.com/frozzenshooter/LoRaParkApplication/main/rules/rule.json";
    private final static String rule2URL = " https://raw.githubusercontent.com/frozzenshooter/LoRaParkApplication/main/rules/rule2.json";
    private final static String rule3URL = "https://raw.githubusercontent.com/frozzenshooter/LoRaParkApplication/main/rules/rule3.json";
    private final static String rule4URL = "https://raw.githubusercontent.com/frozzenshooter/LoRaParkApplication/main/rules/rule4.json";
    private final static String rule5URL = "https://raw.githubusercontent.com/frozzenshooter/LoRaParkApplication/main/rules/rule5.json";
    private final static String rule6URL = "https://raw.githubusercontent.com/frozzenshooter/LoRaParkApplication/main/rules/rule6.json";
    private final static String rule7URL = "https://raw.githubusercontent.com/frozzenshooter/LoRaParkApplication/main/rules/rule7.json";




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
        Request req;
        switch(ruleId) {
            case "rule1":
                req = new Request.Builder().url(HttpClient.rule1URL).build();
                break;
            case "rule2":
                req = new Request.Builder().url(HttpClient.rule2URL).build();
                break;
            case "rule3":
                req = new Request.Builder().url(HttpClient.rule3URL).build();
                break;
            case "rule4":
                req = new Request.Builder().url(HttpClient.rule4URL).build();
                break;
            case "rule5":
                req = new Request.Builder().url(HttpClient.rule5URL).build();
                break;
            case "rule6":
                req = new Request.Builder().url(HttpClient.rule6URL).build();
                break;
            case "rule7":
                req = new Request.Builder().url(HttpClient.rule7URL).build();
                break;
            default:
                req = new Request.Builder().url(HttpClient.rule1URL).build();
                break;
        }

        return req;
    }
}
