package de.uniulm.loraparkapplication.engines;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.awareness.fence.FenceQueryResponse;
import com.google.android.gms.awareness.fence.FenceStateMap;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.uniulm.loraparkapplication.models.Action;
import de.uniulm.loraparkapplication.models.CompleteRule;
import de.uniulm.loraparkapplication.models.Geofence;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.models.Sensor;
import de.uniulm.loraparkapplication.repositories.GeofenceRepository;
import de.uniulm.loraparkapplication.repositories.RuleHandler;
import de.uniulm.loraparkapplication.repositories.SensorValuesRepository;
import io.github.jamsesso.jsonlogic.JsonLogic;
import io.github.jamsesso.jsonlogic.JsonLogicException;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RuleEngine {
    private static final String RULE_ENGINE_CLASSNAME = RuleEngine.class.getName();

    private static RuleEngine instance;

    JsonLogic jsonLogic = new JsonLogic();
    RuleHandler ruleHandler;
    Application mApplication;

    public Map<String, RuleAction> ruleActions = new HashMap<>();

    public RuleEngine(Application application) {
        // JSONlogic Operators
        jsonLogic.addOperation(GeofenceExpression.INSTANCE);
        jsonLogic.addOperation(SensorExpression.INSTANCE);

        // LoRa Actions
        this.addOperation(NotificationAction.INSTANCE);
        this.addOperation(NavigateAction.INSTANCE);
        this.addOperation(TTSAction.INSTANCE);

        // database initialization
        mApplication = application;
        ruleHandler = RuleHandler.getInstance(application);
    }

    public static RuleEngine getInstance(Application application) {
        if (instance == null) {
            instance = new RuleEngine(application);
        }
        return instance;
    }

    public void addOperation(RuleAction ruleAction) {
        ruleActions.put(ruleAction.key(), ruleAction);
    }

    public void trigger() {
        LiveData<List<CompleteRule>> liveDataRules = ruleHandler.getCompleteRules(true);

        liveDataRules.observeForever(new Observer<List<CompleteRule>>() {
            @Override
            public void onChanged(List<CompleteRule> completeRules) {
                List<String> sensorIds = completeRules.stream()
                        .map(CompleteRule::getSensors)
                        .flatMap(List::stream)
                        .map(Sensor::getSensorId)
                        .distinct()
                        .collect(Collectors.toList());

                List<String> fenceIds = completeRules.stream()
                        .map(CompleteRule::getGeofences)
                        .flatMap(List::stream)
                        .map(Geofence::getGeofenceId)
                        .distinct()
                        .collect(Collectors.toList());

                MutableLiveData<Resource<Map<String, Map<String, Map<String, Object>>>>> liveDataSensors = pullSensorValues(sensorIds);
                liveDataSensors.observeForever(new Observer<Resource<Map<String, Map<String, Map<String, Object>>>>>() {
                    @Override
                    public void onChanged(Resource<Map<String, Map<String, Map<String, Object>>>> resourceSensor) {
                        if (resourceSensor.status == Resource.Status.ERROR) {
                            Log.i(RULE_ENGINE_CLASSNAME, "error loading sensor values");
                            liveDataSensors.removeObserver(this);
                        } else if (resourceSensor.status == Resource.Status.SUCCESS) {
                            MutableLiveData<Resource<FenceStateMap>> liveDataFences = pullFenceValues(fenceIds);
                            liveDataFences.observeForever(new Observer<Resource<FenceStateMap>>() {
                                @Override
                                public void onChanged(Resource<FenceStateMap> resourceFence) {
                                    if (resourceSensor.status == Resource.Status.ERROR) {
                                        Log.i(RULE_ENGINE_CLASSNAME, "error loading fence values");
                                        liveDataFences.removeObserver(this);
                                    } else if (resourceSensor.status == Resource.Status.SUCCESS) {
                                        evaluateRules(completeRules);

                                        liveDataFences.removeObserver(this);
                                    }
                                }
                            });
                            liveDataSensors.removeObserver(this);
                        }
                    }
                });

                liveDataRules.removeObserver(this);
            }
        });
    }

    public MutableLiveData<Resource<FenceStateMap>> pullFenceValues(List<String> fenceIds) {
        GeofenceRepository geofenceRepository = GeofenceRepository.getInstance(mApplication);
        Task<FenceQueryResponse> task = geofenceRepository.queryGeofences(fenceIds);

        MutableLiveData<Resource<FenceStateMap>> liveData = new MutableLiveData<>();
        task.addOnFailureListener(error -> liveData.postValue(Resource.error(error.getMessage(), null)));
        task.addOnSuccessListener(result -> {
            FenceStateMap fenceStateMap = result.getFenceStateMap();
            GeofenceExpression.INSTANCE.setFenceList(fenceStateMap);
            liveData.postValue(Resource.success(fenceStateMap));
        });

        return liveData;
    }

    public MutableLiveData<Resource<Map<String, Map<String, Map<String, Object>>>>> pullSensorValues(List<String> sensorIds) {
        MutableLiveData<Resource<Map<String, Map<String, Map<String, Object>>>>> liveData = SensorValuesRepository.getInstance().getSensorValues(sensorIds);

        liveData.observeForever(new Observer<Resource<Map<String, Map<String, Map<String, Object>>>>>() {
            @Override
            public void onChanged(Resource<Map<String, Map<String, Map<String, Object>>>> resource) {
                if(resource.status == Resource.Status.ERROR) {
                    Log.i(RULE_ENGINE_CLASSNAME, "error loading sensor values");
                    liveData.removeObserver(this);
                } else if (resource.status == Resource.Status.SUCCESS) {
                    SensorExpression.INSTANCE.setSensorValues(resource.data);
                    liveData.removeObserver(this);
                }
            }
        });

        return liveData;
    }

    public void evaluateRules(List<CompleteRule> completeRules) {
        LiveData<List<CompleteRule>> liveData = ruleHandler.getCompleteRules(true);

        liveData.observeForever(new Observer<List<CompleteRule>>() {
            @Override
            public void onChanged(List<CompleteRule> completeRules) {
                for (CompleteRule completeRule : completeRules) {
                    RuleEngine.this.evaluateRule(completeRule);
                }

                liveData.removeObserver(this);
            }
        });
    }

    public Boolean evaluateRule(@NonNull CompleteRule completeRule) {
        Rule rule = completeRule.getRule();
        // only evaluate active rules
        if (!rule.getIsActive()) {
            return null;
        }

        try {
            Object conditionObj = jsonLogic.apply(rule.getCondition(), null);
            boolean condition = JsonLogic.truthy(conditionObj);
            boolean triggered = false;
            if (condition && !rule.getWasTriggered()) { // rising edge
                triggered = true;

                for (Action action : completeRule.getActions()) {
                    this.triggerAction(action.getAction(), action.getData());
                }
            }

            try {
                rule.setWasTriggered(condition);
                ruleHandler.updateRule(rule).subscribeOn(Schedulers.io()).subscribe();
            } catch (Exception ex) {
                Log.e(RULE_ENGINE_CLASSNAME, "could not set triggered");
            }

            return triggered;
        } catch (JsonLogicException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public void triggerAction(String action, String data) {
        RuleAction ruleAction = ruleActions.get(action);

        if (ruleAction == null) {
            Log.i(RULE_ENGINE_CLASSNAME, "unknown action type \"" + action + "\"");
        } else {
            // TODO maybe parse Arguments somewhere else
            try {
                Gson gson = new Gson();
                HashMap<String, Object> dataHashMap = gson.fromJson(data, HashMap.class);

                this.triggerAction(ruleAction, dataHashMap);
            } catch (Exception ex) {
                // TODO error handling
                ex.printStackTrace();
            }
        }
    }

    public void triggerAction(String action, Map<String, Object> data) {
        RuleAction ruleAction = ruleActions.get(action);

        if (ruleAction == null) {
            Log.i(RULE_ENGINE_CLASSNAME, "unknown action type \"" + action + "\"");
        } else {
            this.triggerAction(ruleAction, data);
        }
    }

    public void triggerAction(@NonNull RuleAction action, Map<String, Object> data) {
        try {
            action.trigger(mApplication, data);
        } catch (Exception ex) {
            Log.w(RULE_ENGINE_CLASSNAME, "Error executing action", ex);
        }
    }
}
