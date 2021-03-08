package de.uniulm.loraparkapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.SensorDetail;
import de.uniulm.loraparkapplication.viewmodels.SensorDetailViewModel;
import de.uniulm.loraparkapplication.views.KeyValueView;

public class SensorDetailActivity extends AppCompatActivity {

    public final static String DESCRIPTION_EXTRA = "DESCRIPTION_EXTRA";
    public final static String NAME_EXTRA = "NAME_EXTRA";
    public final static String ID_EXTRA = "ID_EXTRA";

    private final static float REDUCED_TEXT_SIZE = 14;
    private static final String SENSOR_DETAIL_ACTIVITY_CLASSNAME = SensorDetailActivity.class.getName();

    protected SensorDetailViewModel mSensorDetailViewModel;

    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_detail);

        //Set the toolbar as the activity's app bar - to be able to show up button
        Toolbar toolbar = (Toolbar) findViewById(R.id.sensor_detail_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Get relevant sensor data from intent
        Bundle extras = getIntent().getExtras();

        this.layout = this.findViewById(R.id.details_view);

        if(extras!=null)
        {
            String id = (String) extras.get(ID_EXTRA);
            String name = (String) extras.get(NAME_EXTRA);
            String description = (String) extras.get(DESCRIPTION_EXTRA);

            this.addSensorDescription(name, description);

            if(id != null){
                // sensor has an id and therefore there should be sensor values which can be displayed

                mSensorDetailViewModel = new ViewModelProvider(this).get(SensorDetailViewModel.class);
                mSensorDetailViewModel.init(id);

                mSensorDetailViewModel.getSensorValues().observe(this, new Observer<Resource<List<SensorDetail>>>() {

                    @Override
                    public void onChanged(@Nullable Resource<List<SensorDetail>> sensorDetailsResource) {

                        if(sensorDetailsResource.status == Resource.Status.SUCCESS) {

                            // all correct -> add the values to the GUI
                            addSensorDetails(sensorDetailsResource.data);

                        }else if (sensorDetailsResource.status == Resource.Status.ERROR){

                            // Failure to retrieve or parse the data
                            String message = getResources().getString(R.string.error_sensor_values_not_loaded) + " ("+ sensorDetailsResource.message +")";
                            Toast.makeText(SensorDetailActivity.this, message, Toast.LENGTH_LONG).show();

                        }else{
                            // Data loading: future TODO: add loading animation
                        }
                    }

                });
            }
        }
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

    //region Detail creation

    private void addSensorDescription(String name, String description){

        if(name != null && !name.isEmpty() && !name.equals("null")){
            KeyValueView kv = createKeyValueView("Name", name, null, false);
            this.layout.addView(kv);
        }

        if(description != null && !description.isEmpty() && !description.equals("null")){
            KeyValueView kv = createKeyValueView("Description", description, null, true);
            this.layout.addView(kv);
        }
    }

    private void addSensorDetails(@Nullable List<SensorDetail> sensorDetails){
        if(sensorDetails != null){
            for(SensorDetail sv: sensorDetails){
                this.addSensorDetail(sv);
            }
        }
    }

    private void addSensorDetail(SensorDetail sensorDetail){
        String name = sensorDetail.getName();
        String value = sensorDetail.getValue();

        if(value != null && !value.isEmpty() && !value.equals("null") && name != null && !name.isEmpty() && !name.equals("null")){
            KeyValueView kv = createKeyValueView(name, value, sensorDetail.getUnit(), false);
            this.layout.addView(kv);
        }
    }

    private KeyValueView createKeyValueView(@NonNull String key,@NonNull String value,@Nullable String unit, boolean reduceTextSize){

        KeyValueView kv = new KeyValueView(this);
        kv.setValues(key, value, unit);
        if(reduceTextSize){
            kv.setValueTextSize(REDUCED_TEXT_SIZE);
        }

        return kv;
    }

    //endregion
}