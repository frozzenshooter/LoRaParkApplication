package de.uniulm.loraparkapplication.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;

/**
 * Custom deserializer that converts a json rule to a rule object
 */
public class RuleDeserializer implements JsonDeserializer<CompleteRule> {

    @Override
    public CompleteRule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jObject = json.getAsJsonObject();

        // region Rule data

        // Id
        JsonElement idElement = jObject.get("id");
        if(idElement == null){
            throw new JsonParseException("No id found");
        }

        String ruleId = idElement.getAsString();

        // Name
        JsonElement nameElement = jObject.get("name");

        String name = "";
        if(idElement != null){
            name = nameElement.getAsString();
        }

        // Description
        JsonElement descriptionElement = jObject.get("description");

        String description = "";
        if(descriptionElement != null){
            description = descriptionElement.getAsString();
        }

        // Condition
        JsonElement conditionElement = jObject.get("condition");

        String condition = "";
        if(conditionElement != null) {
            //TODO: IT MIGHT BE NECESSARY TO ESCAPE THE " IN THE RESULT
            condition  = conditionElement.getAsJsonObject().toString();
        }

        //endregion

        // region Sensors
        JsonElement sensorElement = jObject.get("sensors");
        ArrayList<Sensor> sensorList = new ArrayList<>();
        if(sensorElement != null){
            try {
                JsonArray sensorArray = sensorElement.getAsJsonArray();
                for (int i = 0; i < sensorArray.size(); i++) {
                    JsonObject sensorObject = sensorArray.get(i).getAsJsonObject();

                    String domain = sensorObject.get("domain").getAsString();
                    String sensorId = sensorObject.get("id").getAsString();
                    String value = sensorObject.get("value").getAsString();
                    String ruleSensorId = sensorObject.get("ruleSensorId").getAsString();

                    Sensor sensor = new Sensor();
                    sensor.setDomain(domain);
                    sensor.setSensorId(sensorId);
                    sensor.setValue(value);
                    sensor.setRuleId(ruleId);
                    sensor.setRuleSensorId(ruleSensorId);

                    sensorList.add(sensor);
                }

            }catch(Exception ex){
                throw new JsonParseException("Couldn't parse sensor: " + ex.getMessage());
            }
        }

        //endregion

        //region Geofences
        JsonElement geofenceElement = jObject.get("geofences");
        ArrayList<Geofence> geofenceList = new ArrayList<>();
        if(geofenceElement != null) {
            try {
                JsonArray geofenceArray = geofenceElement.getAsJsonArray();
                for (int i = 0; i < geofenceArray.size(); i++) {
                    JsonObject geofenceObject = geofenceArray.get(i).getAsJsonObject();

                    String geofenceId = geofenceObject.get("id").getAsString();
                    Integer radius = geofenceObject.get("radius").getAsInt();
                    JsonObject locationObject = geofenceObject.get("location").getAsJsonObject();
                    Double lat = locationObject.get("latitude").getAsDouble();
                    Double lon = locationObject.get("longitude").getAsDouble();

                    Geofence geofence = new Geofence();
                    geofence.setGeofenceId(geofenceId);
                    geofence.setRadius(radius);
                    geofence.setLatitude(lat);
                    geofence.setLongitude(lon);
                    geofence.setRuleId(ruleId);

                    geofenceList.add(geofence);
                }

            }catch(Exception ex){
                throw new JsonParseException("Couldn't parse geofence: " + ex.getMessage());
            }
        }

        //endregion

        //region Actions

        JsonElement actionElement = jObject.get("actions");
        ArrayList<Action> actionList = new ArrayList<>();
        if(actionElement != null) {
            try {
                JsonArray actionArray = actionElement.getAsJsonArray();
                for (int i = 0; i < actionArray.size(); i++) {
                    JsonObject actionObject = actionArray.get(i).getAsJsonObject();

                    String actionString = actionObject.get("action").getAsString();
                    String data = actionObject.get("data").getAsJsonObject().toString();

                    Action action = new Action();
                    action.setAction(actionString);
                    action.setData(data);
                    action.setRuleId(ruleId);

                    actionList.add(action);
                }

            }catch(Exception ex){
                throw new JsonParseException("Couldn't parse action: " + ex.getMessage());
            }
        }

        //endregion

        // region Rule creation
        Rule rule = new Rule();

        rule.setId(ruleId);
        rule.setName(name);
        rule.setDescription(description);
        rule.setCondition(condition);
        rule.setIsActive(false);
        rule.setLastTriggered(Long.MIN_VALUE);
        rule.setWasTriggered(false);

        //endregion

        CompleteRule completeRule = new CompleteRule();
        completeRule.setRule(rule);
        completeRule.setSensors(sensorList);
        completeRule.setGeofences(geofenceList);
        completeRule.setActions(actionList);

        return completeRule;
    }
}
