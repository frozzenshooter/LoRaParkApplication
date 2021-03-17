package de.uniulm.loraparkapplication.engines;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.jamsesso.jsonlogic.evaluator.JsonLogicEvaluationException;
import io.github.jamsesso.jsonlogic.evaluator.expressions.PreEvaluatedArgumentsExpression;

public class SensorExpression implements PreEvaluatedArgumentsExpression {
    public static final SensorExpression INSTANCE = new SensorExpression();

    private Map<String, Map<String, Map<String, Object>>> sensorValues = new HashMap<>();

    private SensorExpression() {
        // Use INSTANCE instead.
    }

    @Override
    public String key() {
        return "sensor";
    }

    @Override
    public Object evaluate(List arguments, Object data) throws JsonLogicEvaluationException {
        if (arguments != null & arguments.size() != 3 || !(arguments.get(0) instanceof String && arguments.get(1) instanceof String && arguments.get(2) instanceof String)) {
            throw new JsonLogicEvaluationException("sensor operator expects 3 arguments");
        }

        // TODO sensor
        // domain arguments.get(0)
        // id arguments.get(1)
        // value arguments.get(2)

        Map<String, Map<String, Object>> domain = sensorValues.get(arguments.get(1));
        if(domain == null) {
            return null;
        }
        Map<String, Object> values = domain.get(arguments.get(0));
        if(values == null) {
            return null;
        }

        Object value = values.get(arguments.get(2));
        Log.i(key(), value.toString()); // TODO remove this - just for debugging
        return value;
    }

    public void setSensorValues(Map<String, Map<String, Map<String, Object>>> sensorValues) {
        this.sensorValues = sensorValues;
    }
}
