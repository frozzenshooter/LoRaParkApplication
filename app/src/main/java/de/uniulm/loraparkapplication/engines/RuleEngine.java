package de.uniulm.loraparkapplication.engines;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uniulm.loraparkapplication.R;
import de.uniulm.loraparkapplication.SensorDetailActivity;
import de.uniulm.loraparkapplication.database.RuleDao;
import de.uniulm.loraparkapplication.database.RuleDatabase;
import de.uniulm.loraparkapplication.models.Action;
import de.uniulm.loraparkapplication.models.CompleteRule;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.models.SensorDetail;
import de.uniulm.loraparkapplication.repositories.RuleHandler;
import de.uniulm.loraparkapplication.repositories.SensorValuesRepository;
import io.github.jamsesso.jsonlogic.JsonLogic;
import io.github.jamsesso.jsonlogic.JsonLogicException;

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
        /*MediatorLiveData liveDataMerger = new MediatorLiveData<>();
        liveDataMerger.addSource(pullSensorValues(), value -> evaluateRules());*/

        MutableLiveData<Resource<Map<String, Map<String, Map<String, Object>>>>> liveData = pullSensorValues();
        liveData.observeForever(new Observer<Resource<Map<String, Map<String, Map<String, Object>>>>>() {
            @Override
            public void onChanged(Resource<Map<String, Map<String, Map<String, Object>>>> resource) {
                if(resource.status == Resource.Status.ERROR) {
                    Log.i(RULE_ENGINE_CLASSNAME, "error loading sensor values");
                    liveData.removeObserver(this);
                } else if (resource.status == Resource.Status.SUCCESS) {
                    evaluateRules();

                    liveData.removeObserver(this);
                }
            }
        });
    }

    public MutableLiveData<Resource<Map<String, Map<String, Map<String, Object>>>>> pullSensorValues() {
        // TODO sensorIds
        MutableLiveData<Resource<Map<String, Map<String, Map<String, Object>>>>> liveData = SensorValuesRepository.getInstance().getSensorValues(Arrays.asList("davis-013d4d"));

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

    public LiveData<List<CompleteRule>> evaluateRules() {
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

        return liveData;
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
                ruleHandler.updateRule(rule).subscribe();
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

    public void triggerAction(@NonNull RuleAction action, Map<String, Object> data) {
        action.trigger(mApplication, data);
    }
}
