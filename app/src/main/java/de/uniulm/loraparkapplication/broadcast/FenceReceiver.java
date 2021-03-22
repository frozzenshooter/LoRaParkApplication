package de.uniulm.loraparkapplication.broadcast;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.awareness.fence.FenceState;

import de.uniulm.loraparkapplication.models.GeofenceTracker;
import de.uniulm.loraparkapplication.repositories.FenceTestRepository;
import de.uniulm.loraparkapplication.repositories.GeofenceRepository;

public class FenceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        FenceState fenceState = FenceState.extract(intent);

        GeofenceTracker geofenceTracker = new GeofenceTracker();

        geofenceTracker.setGeofenceId(fenceState.getFenceKey());
        geofenceTracker.setWasTriggerdeManually(false);
        geofenceTracker.setFenceState(fenceState.getCurrentState());
        geofenceTracker.setPreviousFenceState(fenceState.getPreviousState());
        geofenceTracker.setLastUpdated(fenceState.getLastFenceUpdateTimeMillis());
        geofenceTracker.setInsertionTime(System.currentTimeMillis());

        FenceTestRepository fenceTestRepository = FenceTestRepository.getInstance((Application) context.getApplicationContext());

        fenceTestRepository.insertGeofenceTracker(geofenceTracker);

        //String geofenceId = intent.getStringExtra(GeofenceRepository.GEOFENCE_ID);

        //if(geofenceId != null){

           // if (TextUtils.equals(fenceState.getFenceKey(), geofenceId)) {
         /*   String fenceStateStr;
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

        String prevFenceStatStr;
        switch (fenceState.getPreviousState()) {
            case FenceState.TRUE:
                prevFenceStatStr = "true";
                break;
            case FenceState.FALSE:
                prevFenceStatStr = "false";
                break;
            case FenceState.UNKNOWN:
                prevFenceStatStr = "unknown";
                break;
            default:
                prevFenceStatStr = "unknown value";
        }
        */

            Log.e("FENCE_RECEIVER","ID: "+fenceState.getFenceKey()+"!");
            //}
      /*  }else{
            Log.e("FENCE_RECEIVER","BROADCAST called, but not string");
        }*/
    }
}