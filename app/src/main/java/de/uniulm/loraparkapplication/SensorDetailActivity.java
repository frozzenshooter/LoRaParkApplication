package de.uniulm.loraparkapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class SensorDetailActivity extends AppCompatActivity {

    public final static String DESCRIPTION_EXTRA = "DESCRIPTION_EXTRA";
    public final static String NAME_EXTRA = "NAME_EXTRA";
    public final static String ID_EXTRA = "ID_EXTRA";

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

        if(extras!=null)
        {
            this.id =(String) extras.get(ID_EXTRA);
            this.name =(String) extras.get(NAME_EXTRA);
            this.description =(String) extras.get(DESCRIPTION_EXTRA);

            String message = "Id: "+id+"\nName: "+name+"\nDescription: "+description;

            TextView detailstextview = (TextView)findViewById(R.id.detailstextview);
            detailstextview.setText(message);
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
}