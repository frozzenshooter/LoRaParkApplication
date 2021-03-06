package de.uniulm.loraparkapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.TilesOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uniulm.loraparkapplication.models.Location;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.SensorDescription;
import de.uniulm.loraparkapplication.viewmodels.SensorOverviewViewModel;

import static android.content.res.Configuration.UI_MODE_NIGHT_MASK;
import static android.content.res.Configuration.UI_MODE_NIGHT_NO;
import static android.content.res.Configuration.UI_MODE_NIGHT_UNDEFINED;
import static android.content.res.Configuration.UI_MODE_NIGHT_YES;

public class SensorOverviewActivity extends AppCompatActivity {

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private static final String SENSOR_OVERVIEW_ACTIVITY_CLASSNAME = SensorOverviewActivity.class.getName();
    protected SensorOverviewViewModel mSensorOverviewViewModel;
    private MapView map;

    private int markerColor;

    //region Overrides
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_overview);

        //Set the toolbar as the activity's app bar - to be able to show up button
        Toolbar toolbar = (Toolbar) findViewById(R.id.sensor_overview_toolbar);
        setSupportActionBar(toolbar);

        // Request needed permissions
        checkPermissions();

        this.markerColor = fetchColor();

        this.map = (MapView) findViewById(R.id.map);
        setupMapView();

        mSensorOverviewViewModel = new ViewModelProvider(this).get(SensorOverviewViewModel.class);
        mSensorOverviewViewModel.init();

        mSensorOverviewViewModel.getSensorDescriptions().observe(this, new Observer<Resource<List<SensorDescription>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<SensorDescription>> sensorDescriptionsResource) {
                if(sensorDescriptionsResource != null){
                    if(sensorDescriptionsResource.status == Resource.Status.SUCCESS && sensorDescriptionsResource.data != null) {
                        // all correct -> update the markers for the sensors
                        updateMarkersOnMap(sensorDescriptionsResource.data);
                    }else if (sensorDescriptionsResource.status == Resource.Status.ERROR){
                        // Failure to retrieve or parse the data
                        String message = getResources().getString(R.string.error_sensor_descriptions_not_loaded) + " ("+ sensorDescriptionsResource.message +")";
                        Toast.makeText(SensorOverviewActivity.this, message, Toast.LENGTH_LONG).show();
                    }else{
                        // Data loading: future TODO: add loading animation
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.map != null)
            this.map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (map != null)
            map.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_show_rules:
                Intent intent = new Intent(SensorOverviewActivity.this, RuleOverviewActivity.class);
                SensorOverviewActivity.this.startActivity(intent);

                return true;
            case android.R.id.home:
                finish();
                return true;
            default:

                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sensor_overview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();

                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                // Check result of permission requests
                boolean locationPermissionGranted = true;
                if(perms.containsKey(Manifest.permission.ACCESS_FINE_LOCATION)){
                    try{
                        locationPermissionGranted = perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                    }catch (Exception ex){
                        Log.e(SENSOR_OVERVIEW_ACTIVITY_CLASSNAME, "Error accessing permissions");
                    }
                }

                boolean storagePermissionGranted = true;
                if(perms.containsKey(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    try{
                        storagePermissionGranted = perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                    }catch (Exception ex){
                        Log.e(SENSOR_OVERVIEW_ACTIVITY_CLASSNAME, "Error accessing permissions");
                    }
                }

                if (!locationPermissionGranted && !storagePermissionGranted) {
                    Toast.makeText(this, this.getResources().getString(R.string.label_storage_access) + "\n" + this.getResources().getString(R.string.label_location_access), Toast.LENGTH_LONG).show();
                }else if(!locationPermissionGranted){
                    Toast.makeText(this, this.getResources().getString(R.string.label_location_access), Toast.LENGTH_LONG).show();
                }else if(!storagePermissionGranted){
                    Toast.makeText(this, this.getResources().getString(R.string.label_storage_access), Toast.LENGTH_LONG).show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //endregion

    //region Activity setup

    /**
     * Checks the permissions needed for the map view
     */
    private void checkPermissions() {

        List<String> permissions = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissions.isEmpty()) {

            String[] params = permissions.toArray(new String[0]);
            requestPermissions(params, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

        } // else: We already have permissions, so handle as normal
    }

    /**
     * Setup of the map
     */
    private void setupMapView(){
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        this.map.setTileSource(TileSourceFactory.MAPNIK);

        // Zoom buttons
        this.map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        this.map.setMultiTouchControls(true);
        this.map.setClickable(true);


        // Nightmode: simply invert the color -alternative swap to other tiles provider
        int nightModeFlags =  this.getResources().getConfiguration().uiMode & UI_MODE_NIGHT_MASK;

        switch (nightModeFlags){
            case UI_MODE_NIGHT_YES:
                this.map.getOverlayManager().getTilesOverlay().setColorFilter(TilesOverlay.INVERT_COLORS);
                break;
            case UI_MODE_NIGHT_NO:
            case UI_MODE_NIGHT_UNDEFINED:
            default:
                break;
        }

        // Default map zoom level:
        int MAP_DEFAULT_ZOOM = 19;
        this.map.getController().setZoom(MAP_DEFAULT_ZOOM);

        //TODO: SET THE STARTPOINT OF THE MAP TO THE CENTER OF THE SENSORS -> OTHER COORDINATES

        // Default start point in the center of Ulm
        GeoPoint startPoint = new GeoPoint(48.396426, 9.990453);
        this.map.getController().setCenter(startPoint);
    }

    //endregion

    //region Marker creation

    /**
     * Creytes the markers for the sensors on the map
     *
     * @param sensorDescriptions list of all descriptions of all available sensors
     */
    private void updateMarkersOnMap(List<SensorDescription> sensorDescriptions) {

        // Remove previous markers
        this.map.getOverlays().clear();

        for(SensorDescription sd : sensorDescriptions){
            createSensorMarker(sd);
        }

        // Force the refresh of the view
        this.map.invalidate();
    }

    /**
     * Loads the current accent color
     *
     * @return color as int
     */
    private int fetchColor() {
        TypedValue typedValue = new TypedValue();

        TypedArray a = this.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorOnBackground });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    /**
     * Creates a marker for a single sensor
     *
     * @param sensorDescription the description of the sensor
     */
    private void createSensorMarker(@NotNull SensorDescription sensorDescription){
        Location loc = sensorDescription.getLocation();
        GeoPoint geoPoint = new GeoPoint(loc.getLatitude(), loc.getLongitude());

        OverlayItem overlayItem = new OverlayItem(sensorDescription.getName(), sensorDescription.getDescription(), geoPoint);

        // Setting the tint directly won't work - only with the compat wrapper
        Drawable unwrappedMarkerDrawable = AppCompatResources.getDrawable(this, R.drawable.outline_room_36);

        if(unwrappedMarkerDrawable != null){

                Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedMarkerDrawable);
                DrawableCompat.setTint(wrappedDrawable, this.markerColor);

                overlayItem.setMarker(wrappedDrawable);

                ArrayList<OverlayItem> overlayItemArrayList = new ArrayList<>();
                overlayItemArrayList.add(overlayItem);

                ItemizedOverlay<OverlayItem> locationOverlay = new ItemizedIconOverlay<>(overlayItemArrayList, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(int i, OverlayItem overlayItem) {

                        Intent intent = new Intent(SensorOverviewActivity.this, SensorDetailActivity.class);

                        intent.putExtra(SensorDetailActivity.ID_EXTRA, sensorDescription.getId());
                        intent.putExtra(SensorDetailActivity.NAME_EXTRA, sensorDescription.getName());
                        intent.putExtra(SensorDetailActivity.DESCRIPTION_EXTRA, sensorDescription.getDescription());

                        SensorOverviewActivity.this.startActivity(intent);

                        return true; // Handled this event.
                    }

                    @Override
                    public boolean onItemLongPress(int i, OverlayItem overlayItem) {
                        return false;
                    }
                }, getApplicationContext());

                this.map.getOverlays().add(locationOverlay);
        }
    }

    //endregion

}