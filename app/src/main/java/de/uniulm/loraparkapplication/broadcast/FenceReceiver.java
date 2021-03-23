package de.uniulm.loraparkapplication.broadcast;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import de.uniulm.loraparkapplication.FenceTestActivity;
import de.uniulm.loraparkapplication.models.GeofenceTracker;
import de.uniulm.loraparkapplication.repositories.FenceTestRepository;
import de.uniulm.loraparkapplication.repositories.GeofenceRepository;

public class FenceReceiver extends BroadcastReceiver {


    private class ThreadPerTaskExecutor implements Executor {
        public void execute(Runnable r) {
            new Thread(r).start();
        }
    }

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
        geofenceTracker.setDeleted(false);
        geofenceTracker.setInserted(false);

        FenceTestRepository fenceTestRepository = FenceTestRepository.getInstance((Application) context.getApplicationContext());

        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient((Application) context.getApplicationContext());


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            fenceTestRepository.insertGeofenceTracker(geofenceTracker);
            return;
        }else{

            locationProviderClient.getLastLocation()
                    .addOnSuccessListener(new ThreadPerTaskExecutor(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null) {
                                // Logic to handle location object
                                geofenceTracker.setLatitude(location.getLatitude());
                                geofenceTracker.setLongitude(location.getLongitude());
                            }

                            fenceTestRepository.insertGeofenceTracker(geofenceTracker);
                        }
                    });
        }

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