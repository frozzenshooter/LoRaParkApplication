package de.uniulm.loraparkapplication.models;

import java.util.ArrayList;

public class CompleteRule {
    private Rule rule;
    private ArrayList<Sensor> sensors;
    private ArrayList<Geofence> geofences;
    private ArrayList<Action> actions;


    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public ArrayList<Sensor> getSensors() {
        if(sensors == null){
            sensors = new ArrayList<>();
        }
        return sensors;
    }

    public void setSensors(ArrayList<Sensor> sensors) {
        this.sensors = sensors;
    }

    public void addSensor(Sensor sensor){

        if(this.sensors== null){
            this.sensors = new ArrayList<>();
        }

        this.sensors.add(sensor);
    }

    public ArrayList<Geofence> getGeofences() {
        if(geofences == null){
            geofences = new ArrayList<>();
        }
        return geofences;
    }

    public void setGeofences(ArrayList<Geofence> geofences) {
        this.geofences = geofences;
    }

    public void addGeofence(Geofence geofence){

        if(this.geofences== null){
            this.geofences = new ArrayList<>();
        }

        this.geofences.add(geofence);
    }

    public ArrayList<Action> getActions() {
        if(actions == null){
            actions = new ArrayList<>();
        }
        return actions;
    }

    public void setActions(ArrayList<Action> actions) {
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

        return builder.toString();
    }
}
