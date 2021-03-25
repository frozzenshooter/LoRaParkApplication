package de.uniulm.loraparkapplication.engines;

import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceStateMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.jamsesso.jsonlogic.evaluator.JsonLogicEvaluationException;
import io.github.jamsesso.jsonlogic.evaluator.expressions.PreEvaluatedArgumentsExpression;

public class GeofenceExpression implements PreEvaluatedArgumentsExpression {
    public static final GeofenceExpression INSTANCE = new GeofenceExpression();
    private FenceStateMap fenceStateMap;

    private GeofenceExpression() {
        // use INSTANCE instead
    }

    @Override
    public String key() {
        return "geofence";
    }

    @Override
    public Object evaluate(List arguments, Object data) throws JsonLogicEvaluationException {
        if (arguments.size() != 1 || !(arguments.get(0) instanceof String)) {
            throw new JsonLogicEvaluationException("geofence operator 1 string argument");
        }

        if(fenceStateMap == null) {
            return null;
        }

        String geofenceID = (String) arguments.get(0);
        FenceState fenceState = fenceStateMap.getFenceState(geofenceID);

        if(fenceState == null) {
            return null;
        }

        return fenceState.getCurrentState() == FenceState.TRUE;
    }

    public void setFenceList(FenceStateMap fenceStateMap) {
        this.fenceStateMap = fenceStateMap;
    }
}
