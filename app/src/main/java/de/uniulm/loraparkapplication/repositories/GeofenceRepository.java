package de.uniulm.loraparkapplication.repositories;

import android.Manifest;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.FenceClient;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


import de.uniulm.loraparkapplication.broadcast.FenceReceiver;
import de.uniulm.loraparkapplication.models.Geofence;
import de.uniulm.loraparkapplication.models.Location;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

public class GeofenceRepository {

    private static GeofenceRepository instance;
    private final Application application;

    public final static String GEOFENCE_ID = "geofenceId";

    public static GeofenceRepository getInstance(@NonNull Application application) {
        if (instance == null) {
            instance = new GeofenceRepository(application);
        }
        return instance;
    }

    private GeofenceRepository(@NonNull Application application) {
        this.application = application;
    }

    public Completable createGeofence(@NonNull Geofence geofence) {

        return Completable.create(emitter ->{

            if (ActivityCompat.checkSelfPermission(this.application, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this.application, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED )
            {
                emitter.onError(new Exception("Not all location permissions set"));
            }

            Intent intent= new Intent(this.application, FenceReceiver.class);
            intent.putExtra(GEOFENCE_ID, geofence.getGeofenceId());
            PendingIntent mPendingIntent = PendingIntent.getBroadcast(this.application, 0, intent, 0);

            AwarenessFence fence = LocationFence.in(geofence.getLatitude(), geofence.getLongitude(), geofence.getRadius(), 0L);

            FenceUpdateRequest fenceUpdateRequest = new FenceUpdateRequest.Builder()
                    .addFence(geofence.getGeofenceId(), fence, mPendingIntent)
                    .build();

            Awareness.getFenceClient(application).updateFences(fenceUpdateRequest)

                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            emitter.onComplete();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            emitter.onError(e);
                        }

                });
        });
    }

    public Completable deleteGeofence(@NonNull Geofence geofence){

        return Completable.create((emitter) ->{
            Awareness.getFenceClient(application).updateFences(new FenceUpdateRequest.Builder()
                    .removeFence(geofence.getGeofenceId())
                    .build())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            emitter.onComplete();;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            emitter.onError(e);
                        }
                    });
        });
    }

    public Single<Boolean> existsGeofence(){
        return Single.defer(()->{
           return Single.just(true);
        });
    }

}
