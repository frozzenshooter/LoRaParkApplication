package de.uniulm.loraparkapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.ColumnInfo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.FenceQueryRequest;
import com.google.android.gms.awareness.fence.FenceQueryResponse;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceStateMap;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import de.uniulm.loraparkapplication.models.Geofence;
import de.uniulm.loraparkapplication.models.GeofenceTracker;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.SensorDetail;
import de.uniulm.loraparkapplication.viewmodels.FenceTestViewModel;
import de.uniulm.loraparkapplication.viewmodels.SensorDetailViewModel;

/**
 * Activity to test the fence API
 */
public class FenceTestActivity extends AppCompatActivity {

    private FenceTestViewModel mFenceTestViewModel;
    private TextView overviewTextView;
    private EditText geofenceIdEditView;
    private EditText latitudeEditView;
    private EditText longitudeEditView;
    private EditText radiusEditView;
    private FusedLocationProviderClient locationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fence_test);

        //Set the toolbar as the activity's app bar - to be able to show up button
        Toolbar toolbar = (Toolbar) findViewById(R.id.fence_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.geofenceIdEditView = findViewById(R.id.edit_view_geofence_id);
        this.latitudeEditView = findViewById(R.id.edit_view_lat);
        this.longitudeEditView = findViewById(R.id.edit_view_long);
        this.radiusEditView = findViewById(R.id.edit_view_radius);

        this.overviewTextView = findViewById(R.id.text_view_geofence_tracker_overview);

        this.locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        this.mFenceTestViewModel = new ViewModelProvider(this).get(FenceTestViewModel.class);
        this.mFenceTestViewModel.getGeofenceTrackers().observe(this, new Observer<List<GeofenceTracker>>() {

            @Override
            public void onChanged(@Nullable List<GeofenceTracker> geofenceTrackers) {

                if (geofenceTrackers != null) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Geofence trackers: \n");
                    builder.append("--------------------------------------------------------");
                    builder.append("\n");
                    for (GeofenceTracker geofenceTracker : geofenceTrackers) {

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS", Locale.ENGLISH);
                        String insertionTime = dateFormat.format(geofenceTracker.getInsertionTime());
                        builder.append("[" + insertionTime + "]: ");

                        builder.append("Id: " + geofenceTracker.getGeofenceId() + ";\n");

                        if (geofenceTracker.getDeleted()) {
                            builder.append("Deleted! \n");
                        } else if (geofenceTracker.getInserted()) {
                            builder.append("Inserted! \n");
                        } else {

                            String currentState = getFenceStateAsString(geofenceTracker.getFenceState());
                            builder.append("CurrentState: " + currentState + ";\n");

                            String prevState = getFenceStateAsString(geofenceTracker.getPreviousFenceState());
                            builder.append("PreviousState: " + prevState + ";\n");

                            String updateTime = dateFormat.format(geofenceTracker.getLastUpdated());
                            builder.append("Last updated: " + updateTime + ";\n");

                            String wasTriggeredManuallyStr = geofenceTracker.getWasTriggerdeManually() ? "true" : "false";
                            builder.append("Manual triggered: " + wasTriggeredManuallyStr + ";\n");

                            builder.append("Latitude: " + geofenceTracker.getLatitude() + ";\n");
                            builder.append("Longitude: " + geofenceTracker.getLongitude() + ";\n");
                        }

                        builder.append("--------------------------------------------------------");
                        builder.append("\n");
                    }
                    String overview = builder.toString();
                    overviewTextView.setText(overview);
                }
            }
        });
    }

    private String getFenceStateAsString(Integer fenceState) {
        String fenceStateStr;
        switch (fenceState) {
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
        return fenceStateStr;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:

                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteAllGeofenceTrackers(View view) {
        this.mFenceTestViewModel.deleteAllGeofenceTrackers();
    }

    private String randomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'

        int targetStringLength = 15;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }

        return buffer.toString();
    }


    public void addGeofence(View view) {

        String geofenceId = this.geofenceIdEditView.getText().toString();
        String latStr = this.latitudeEditView.getText().toString();
        String longitudeStr = this.longitudeEditView.getText().toString();
        String radiusStr = this.radiusEditView.getText().toString();


        if ("".equals(geofenceId)) {
            geofenceId = randomString();
        }

        if ("".equals(radiusStr)) {
            radiusStr = "100";
        }

        if ("".equals(latStr) || "".equals(longitudeStr)) {
            latStr = "48.679921";
            longitudeStr = "9.931987";
        }

        try {
            double lat = Double.parseDouble(latStr);
            double longitude = Double.parseDouble(longitudeStr);
            int radius = Integer.parseInt(radiusStr);

            Geofence geofence = new Geofence();
            geofence.setGeofenceId(geofenceId);
            geofence.setRadius(radius);
            geofence.setLongitude(longitude);
            geofence.setLatitude(lat);

            this.mFenceTestViewModel.addGeofence(geofence);
        } catch (Exception ex) {
            String message = "Problem adding the fence: " + ex.getMessage();
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteGeofence(View view) {
        String geofenceId = this.geofenceIdEditView.getText().toString();

        if (!"".equals(geofenceId)) {
            this.mFenceTestViewModel.deleteGeofence(geofenceId);
        } else {
            String message = "GeofenceId is empty";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void queryGeofence(View view) {

        String fenceKey = this.geofenceIdEditView.getText().toString();

        Awareness.getFenceClient(this)
                .queryFences(FenceQueryRequest.forFences(Arrays.asList(fenceKey)))
                .addOnSuccessListener(new OnSuccessListener<FenceQueryResponse>() {
                    @Override
                    public void onSuccess(FenceQueryResponse response) {
                        FenceStateMap map = response.getFenceStateMap();
                        for (String fenceKey : map.getFenceKeys()) {

                            FenceState fenceState = map.getFenceState(fenceKey);

                            GeofenceTracker geofenceTracker = new GeofenceTracker();

                            geofenceTracker.setInsertionTime(System.currentTimeMillis());
                            geofenceTracker.setLastUpdated(fenceState.getLastFenceUpdateTimeMillis());
                            geofenceTracker.setFenceState(fenceState.getCurrentState());
                            geofenceTracker.setPreviousFenceState(fenceState.getPreviousState());
                            geofenceTracker.setInserted(false);
                            geofenceTracker.setDeleted(false);
                            geofenceTracker.setWasTriggerdeManually(true);
                            geofenceTracker.setGeofenceId(fenceKey);


                            if (ActivityCompat.checkSelfPermission(FenceTestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                    && ActivityCompat.checkSelfPermission(FenceTestActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // dont add location
                                geofenceTracker.setLatitude(0.0);
                                geofenceTracker.setLongitude(0.0);
                                FenceTestActivity.this.mFenceTestViewModel.insertGeofenceTracker(geofenceTracker);
                            } else {

                                // Get current location to insert into tracker
                                FenceTestActivity.this.locationProviderClient.getLastLocation()
                                        .addOnSuccessListener(FenceTestActivity.this, new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(Location location) {

                                                if (location != null) {
                                                    // Logic to handle location object
                                                    geofenceTracker.setLatitude(location.getLatitude());
                                                    geofenceTracker.setLongitude(location.getLongitude());
                                                }
                                                FenceTestActivity.this.mFenceTestViewModel.insertGeofenceTracker(geofenceTracker);
                                            }
                                        });
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String message = "Could not query fence: " + fenceKey;
                        Toast.makeText(FenceTestActivity.this, message, Toast.LENGTH_LONG).show();
                        return;
                    }
                });
    }
}