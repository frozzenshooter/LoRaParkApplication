package de.uniulm.loraparkapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.SensorValue;
import de.uniulm.loraparkapplication.util.KeyResolver;
import de.uniulm.loraparkapplication.viewmodels.SensorDetailViewModel;
import de.uniulm.loraparkapplication.views.KeyValueView;

public class SensorDetailActivity extends AppCompatActivity {

    public final static String DESCRIPTION_EXTRA = "DESCRIPTION_EXTRA";
    public final static String NAME_EXTRA = "NAME_EXTRA";
    public final static String ID_EXTRA = "ID_EXTRA";

    private final static float REDUCED_TEXT_SIZE = 14;
    private static final String SENSOR_DETAIL_ACTIVITY_CLASSNAME = SensorDetailActivity.class.getName();

    protected SensorDetailViewModel mSensorDetailViewModel;

    private String id;
    private String name;
    private String description;

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

        String message = "";
        if(extras!=null)
        {
            this.id =(String) extras.get(ID_EXTRA);
            this.name =(String) extras.get(NAME_EXTRA);
            this.description =(String) extras.get(DESCRIPTION_EXTRA);

            message = "Id: "+id+"\nName: "+name+"\nDescription: "+description;
        }

        this.createDetails();

        if(this.id != null){
            // sensor has an id and therefore there should be sensor values which can be displayed

            mSensorDetailViewModel = new ViewModelProvider(this).get(SensorDetailViewModel.class);
            mSensorDetailViewModel.init(id);

            mSensorDetailViewModel.getSensorValues().observe(this, new Observer<Resource<List<SensorValue>>>() {
                @Override
                public void onChanged(@Nullable Resource<List<SensorValue>> sensorDescriptionsResource) {
                    Log.i(SENSOR_DETAIL_ACTIVITY_CLASSNAME, "Data changed");
                }
            });


        }else{
            // IoT devices without sensor values, only the description should be displayed
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

    private void createDetails(){

        LinearLayout layout = this.findViewById(R.id.details_view);

        KeyResolver keyResolver = KeyResolver.getInstance();

        if(this.name != null && !this.name.isEmpty() && !this.name.equals("null")){
            KeyValueView kv = createKeyValueView(keyResolver.resolveKey(this, "name"), this.name, null, false);
            layout.addView(kv);
        }

        if(this.description != null && !this.description.isEmpty() && !this.description.equals("null")){
            KeyValueView kv = createKeyValueView(keyResolver.resolveKey(this, "description"), this.description, null, true);
            layout.addView(kv);
        }

        //TODO: REMOVE EXAMPLES LATER
        KeyValueView example = createKeyValueView("Temperature", "25", "°C", false);
        layout.addView(example);

        KeyValueView example2 = createKeyValueView("CO2", "250", "ppm", false);
        layout.addView(example2);
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