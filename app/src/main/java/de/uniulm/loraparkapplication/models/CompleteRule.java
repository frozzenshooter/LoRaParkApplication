package de.uniulm.loraparkapplication.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CompleteRule {
    @Embedded
    private Rule rule;

    @Relation(parentColumn = "id", entityColumn = "rule_id", entity = Sensor.class)
    private List<Sensor> sensors;

    @Relation(parentColumn = "id", entityColumn = "rule_id", entity = Geofence.class)
    private List<Geofence> geofences;

    @Relation(parentColumn = "id", entityColumn = "rule_id", entity = Action.class)
    private List<Action> actions;


    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public List<Sensor> getSensors() {
        if(sensors == null){
            sensors = new ArrayList<>();
        }
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    public void addSensor(Sensor sensor){

        if(this.sensors== null){
            this.sensors = new ArrayList<>();
        }

        this.sensors.add(sensor);
    }

    public List<Geofence> getGeofences() {
        if(geofences == null){
            geofences = new ArrayList<>();
        }
        return geofences;
    }

    public void setGeofences(List<Geofence> geofences) {
        this.geofences = geofences;
    }

    public void addGeofence(Geofence geofence){

        if(this.geofences== null){
            this.geofences = new ArrayList<>();
        }

        this.geofences.add(geofence);
    }

    public List<Action> getActions() {
        if(actions == null){
            actions = new ArrayList<>();
        }
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public void addAction(Action action){
        if(this.actions== null){
            this.actions = new ArrayList<>();
        }

        this.actions.add(action);
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();

        builder.append("Rule (id): ");
        builder.append(rule.getId());
        builder.append("\n");

        builder.append("Rule (name): ");
        builder.append(rule.getName());
        builder.append("\n");

        builder.append("Rule (description): ");
        builder.append(rule.getDescription());
        builder.append("\n");

        builder.append("Rule (condition): ");
        builder.append(rule.getCondition());
        builder.append("\n");

        builder.append("Rule (isActive): ");
        builder.append(rule.getIsActive()? "true": "false");
        builder.append("\n");

        builder.append("Rule (last triggered): ");
        if(rule.getLastTriggered() > 0){
            Timestamp triggered = new Timestamp(rule.getLastTriggered());
            builder.append(triggered.toString());
        }else{
            builder.append("no timestamp set");
        }
        builder.append("\n");

        builder.append("Rule (wasTriggered): ");
        builder.append(rule.getWasTriggered()? "true": "false");
        builder.append("\n");

        for(Sensor sensor : this.getSensors()){
            builder.append("Sensor (sensorId): ");
            builder.append(sensor.getSensorId());
            builder.append("\n");

            builder.append("Sensor (domain): ");
            builder.append(sensor.getDomain());
            builder.append("\n");

            builder.append("Sensor (value): ");
            builder.append(sensor.getValue());
            builder.append("\n");

            builder.append("Sensor (ruleId): ");
            builder.append(sensor.getRuleId());
            builder.append("\n");

            builder.append("Sensor (ruleSensorId): ");
            builder.append(sensor.getRuleSensorId());
            builder.append("\n");
        }

        for(Geofence geofence: this.getGeofences()){
            builder.append("Geofence (geofenceId): ");
            builder.append(geofence.getGeofenceId());
            builder.append("\n");

            builder.append("Geofence (radius): ");
            builder.append(geofence.getRadius());
            builder.append("\n");

            builder.append("Geofence (lat): ");
            builder.append(geofence.getLatitude());
            builder.append("\n");

            builder.append("Geofence (lon): ");
            builder.append(geofence.getLongitude());
            builder.append("\n");

            builder.append("Geofence (ruleId): ");
            builder.append(geofence.getRuleId());
            builder.append("\n");
        }

        for(Action action: this.getActions()){
            builder.append("Action (action): ");
            builder.append(action.getAction());
            builder.append("\n");

            builder.append("Action (data): ");
            builder.append(action.getData());
            builder.append("\n");

            builder.append("Action (ruleId): ");
            builder.append(action.getRuleId());
            builder.append("\n");
        }

        builder.append("---------------------------------------------------------");

        return builder.toString();
    }
}
