package de.uniulm.loraparkapplication.repositories;

import android.Manifest;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceQueryRequest;
import com.google.android.gms.awareness.fence.FenceQueryResponse;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import java.util.List;

import de.uniulm.loraparkapplication.broadcast.FenceReceiver;
import de.uniulm.loraparkapplication.models.Geofence;

public class GeofenceRepository {

    private static GeofenceRepository instance;
    private final Application application;

    private static PendingIntent sPendingIntentInstance;

    private final static String TAG = GeofenceRepository.class.getSimpleName();

    public static GeofenceRepository getInstance(@NonNull Application application) {
        if (instance == null) {
            instance = new GeofenceRepository(application);
        }
        return instance;
    }

    private GeofenceRepository(@NonNull Application application) {
        this.application = application;
    }

    private PendingIntent getPendingIntent(){

        if(sPendingIntentInstance == null){
            Intent intent= new Intent(this.application, FenceReceiver.class);

            sPendingIntentInstance = PendingIntent.getBroadcast(this.application, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return sPendingIntentInstance;
    }

    /**
     * Creates a geofence (the result (success/failure) will be logged)
     *
     * @param geofence the geofence to add
     * @throws Exception
     */
    public void createGeofence(@NonNull Geofence geofence) throws Exception{

        if (ActivityCompat.checkSelfPermission(this.application, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.application, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            throw new Exception("Not all location permissions set");
        }

        PendingIntent mPendingIntent = getPendingIntent();

        AwarenessFence fence = LocationFence.in(geofence.getLatitude(), geofence.getLongitude(), geofence.getRadius(), 0L);

        FenceUpdateRequest fenceUpdateRequest = new FenceUpdateRequest.Builder()
                .addFence(geofence.getGeofenceId(), fence, mPendingIntent)
                .build();

        Awareness.getFenceClient(application).updateFences(fenceUpdateRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Geofence successful added: " + geofence.getGeofenceId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Geofence couldn't be added: " + geofence.getGeofenceId()+" - reason: "+e.getMessage());
                    }

                });
    }

    /**
     * Deletes a geofence (the result (success/failure) will be logged)
     *
     * @param geofenceId the id of the geofence to delete
     */
    public void deleteGeofence(@NonNull String geofenceId){

        Awareness.getFenceClient(application).updateFences(new FenceUpdateRequest.Builder()
                .removeFence(geofenceId)
                .build())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Geofence successful deleted: " + geofenceId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Geofence couldn't be deleted: " + geofenceId+" - reason: "+e.getMessage());
                    }
                });
    }


    /**
     * Querys the current state of the geofences and returns a task
     *
     * @param geofenceIds the ids of the goefences to request
     * @return a task which queries the fences
     */
    public Task<FenceQueryResponse> queryGeofences(@NonNull List<String> geofenceIds){

        return Awareness.getFenceClient(this.application).queryFences(FenceQueryRequest.forFences(geofenceIds));
    }



}
