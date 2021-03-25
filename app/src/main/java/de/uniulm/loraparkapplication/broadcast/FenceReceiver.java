package de.uniulm.loraparkapplication.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.awareness.fence.FenceState;

import de.uniulm.loraparkapplication.BackgroundeJobService;
import de.uniulm.loraparkapplication.repositories.GeofenceRepository;

public class FenceReceiver extends BroadcastReceiver {

    private final static String TAG = FenceReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        FenceState fenceState = FenceState.extract(intent);

        String fenceKey = fenceState.getFenceKey();

        String fenceStateStr;
        switch (fenceState.getCurrentState()) {
            case FenceState.TRUE:
                fenceStateStr = "true";
                break;
            case FenceState.FALSE:
                fenceStateStr = "false";
                break;
            case FenceState.UNKNOWN:
                fenceStateStr = "unknown";
                break;
            default:
                fenceStateStr = "unknown value";
        }
        Log.i(TAG,"Fence state from fence '"+fenceKey+"': " + fenceStateStr);

        //TODO: schedule the Job to evaluate rules
        //BackgroundeJobService.scheduleJob(context);
    }
}