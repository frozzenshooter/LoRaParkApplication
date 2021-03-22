package de.uniulm.loraparkapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.fence.FenceState;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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

        this.overviewTextView = findViewById(R.id.text_view_geofence_tracker_overview);

        this.mFenceTestViewModel = new ViewModelProvider(this).get(FenceTestViewModel.class);
        this.mFenceTestViewModel.getGeofenceTrackers().observe(this, new Observer<List<GeofenceTracker>>() {

            @Override
            public void onChanged(@Nullable List<GeofenceTracker> geofenceTrackers) {

                if(geofenceTrackers != null) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Geofence trackers: \n");
                    builder.append("--------------------------------------------------------");
                    builder.append("\n");
                    for (GeofenceTracker geofenceTracker : geofenceTrackers) {

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS", Locale.ENGLISH);
                        String insertionTime = dateFormat.format(geofenceTracker.getInsertionTime());
                        builder.append("[" + insertionTime + "]: ");

                        builder.append("Id: " + geofenceTracker.getGeofenceId() + ";\n");

                        String currentState = getFenceStateAsString(geofenceTracker.getFenceState());
                        builder.append("CurrentState: " + currentState + ";\n");

                        String prevState = getFenceStateAsString(geofenceTracker.getPreviousFenceState());
                        builder.append("PreviousState: " + prevState + ";\n");

                        String updateTime = dateFormat.format(geofenceTracker.getLastUpdated());
                        builder.append("Last updated: " + updateTime + ";\n");

                        String wasTriggeredManuallyStr = geofenceTracker.getWasTriggerdeManually() ? "true" : "false";
                        builder.append("Manual triggered: " + wasTriggeredManuallyStr + ";\n");

                        builder.append("--------------------------------------------------------");
                        builder.append("\n");
                    }
                    String overview = builder.toString();
                    overviewTextView.setText(overview);
                }
            }
        });
    }

    private String getFenceStateAsString(Integer fenceState){
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
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
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
}