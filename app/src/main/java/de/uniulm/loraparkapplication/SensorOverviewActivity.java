package de.uniulm.loraparkapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorOverviewActivity extends AppCompatActivity {

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_overview);

        //Set the toolbar as the activity's app bar - to be able to show up button
        Toolbar toolbar = (Toolbar) findViewById(R.id.sensor_overview_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Request needed permissions
        checkPermissions();

        this.map = (MapView) findViewById(R.id.map);
        setupMapView();

        createPin(this.map);
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
            case android.R.id.home:
                finish();
                return true;
            default:

                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();

                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                // Check result of permission requests
                Boolean locationPermissionGranted = perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                Boolean storagePermissionGranted = perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;


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

    //region Internal functionalities

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

    private void createPin(MapView mMapView){
        GeoPoint geoPoint = new GeoPoint(48.396426, 9.990453);

        OverlayItem overlayItem = new OverlayItem("San Fransisco", "California", geoPoint);
        Drawable markerDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker_default);
        overlayItem.setMarker(markerDrawable);

        ArrayList<OverlayItem> overlayItemArrayList = new ArrayList<>();
        overlayItemArrayList.add(overlayItem);
        ItemizedOverlay<OverlayItem> locationOverlay = new ItemizedIconOverlay<>(overlayItemArrayList, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int i, OverlayItem overlayItem) {

                Toast.makeText(SensorOverviewActivity.this, "Item's Title : "+overlayItem.getTitle() +"\nItem's Desc : "+overlayItem.getSnippet(), Toast.LENGTH_SHORT).show();
                return true; // Handled this event.
            }

            @Override
            public boolean onItemLongPress(int i, OverlayItem overlayItem) {
                return false;
            }
        }, getApplicationContext());

        mMapView.getOverlays().add(locationOverlay);

        MyLocationNewOverlay myLocationoverlay = new MyLocationNewOverlay(this.map);
        myLocationoverlay.enableMyLocation();

        mMapView.getOverlays().add(myLocationoverlay);
    }

    private void setupMapView(){
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        this.map.setTileSource(TileSourceFactory.MAPNIK);

        // Zoom buttons
        this.map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        this.map.setMultiTouchControls(true);
        this.map.setClickable(true);

        // Default map zoom level:
        int MAP_DEFAULT_ZOOM = 19;
        this.map.getController().setZoom(MAP_DEFAULT_ZOOM);

        // Default start point in the center of Ulm
        GeoPoint startPoint = new GeoPoint(48.396426, 9.990453);
        this.map.getController().setCenter(startPoint);
    }

    //endregion
}