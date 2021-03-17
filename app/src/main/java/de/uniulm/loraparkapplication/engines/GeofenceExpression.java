package de.uniulm.loraparkapplication.engines;

import java.util.List;

import io.github.jamsesso.jsonlogic.evaluator.JsonLogicEvaluationException;
import io.github.jamsesso.jsonlogic.evaluator.expressions.PreEvaluatedArgumentsExpression;

public class GeofenceExpression implements PreEvaluatedArgumentsExpression {
    public static final GeofenceExpression INSTANCE = new GeofenceExpression();

    private GeofenceExpression() {
        // Use INSTANCE instead.
    }

    @Override
    public String key() {
        return "geofence";
    }

    @Override
    public Object evaluate(List arguments, Object data) throws JsonLogicEvaluationException {
        if (arguments.size() != 3 || !(arguments.get(0) instanceof Double && arguments.get(1) instanceof Double && arguments.get(2) instanceof Double)) {
            throw new JsonLogicEvaluationException("geofence operator 3 double arguments");
        }

        // TODO geofence
        // lat arguments.get(0)
        // lon arguments.get(1)
        // radius arguments.get(2)


        return false;
    }
}
