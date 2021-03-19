package de.uniulm.loraparkapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.viewmodels.RuleDetailViewModel;
import de.uniulm.loraparkapplication.views.KeyValueView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class RuleDetailActivity extends AppCompatActivity {

    public final static String RULE_ID_EXTRA = "RULE_ID_EXTRA";

    private static final String RULE_DETAIL_ACTIVITY_CLASSNAME = RuleDetailActivity.class.getName();
    private final static float REDUCED_TEXT_SIZE = 14;
    private Rule rule;
    private RuleDetailViewModel mRuleDetailViewModel;
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 324;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_detail);

        //Set the toolbar as the activity's app bar - to be able to show up button
        Toolbar toolbar = (Toolbar) findViewById(R.id.rule_detail_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        Bundle extras = getIntent().getExtras();
        String id = null;
        if (extras != null) {
            id = (String) extras.get(RULE_ID_EXTRA);
        }

        this.rule = null;

        if (id != null) {

            this.mRuleDetailViewModel = new ViewModelProvider(this).get(RuleDetailViewModel.class);
            this.mRuleDetailViewModel.init(id);

            mRuleDetailViewModel.getRule().observe(this, new Observer<Rule>() {
                @Override
                public void onChanged(@Nullable Rule rule) {
                    if (rule != null) {
                        RuleDetailActivity.this.rule = rule;
                        handleNewRuleData(rule);
                    }
                }
            });

        }
    }

    /**
     * Updates the UI with the data from the rule
     *
     * @param rule the rule containing the data for the UI
     */
    private void handleNewRuleData(@NotNull Rule rule) {

        if (rule.getName() != null) {
            KeyValueView kv = findViewById(R.id.rule_details_name);
            kv.setValues("Name", rule.getName(), null);
        }

        if (rule.getDescription() != null) {

            KeyValueView kv = findViewById(R.id.rule_details_description);
            kv.setValueTextSize(REDUCED_TEXT_SIZE);
            kv.setValues("Description", rule.getDescription(), null);
        }

        if (rule.getIsActive() != null) {
            CheckBox cb = findViewById(R.id.rule_details_is_active);
            cb.setChecked(rule.getIsActive());
        }

        //TODO: show map with geofence or the relevant sensor ??
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

    public void activateRule(View view) {
        List<String> permissions = checkPermissions();

        CheckBox cb = findViewById(R.id.rule_details_is_active);
        if (!permissions.isEmpty()) {
            // Request the needed permissions and reset the checkbox
            Boolean isChecked = cb.isChecked();
            cb.setChecked(!isChecked);

            String[] params = permissions.toArray(new String[0]);
            requestPermissions(params, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

        } else{
            if (this.rule != null && this.rule.getIsActive() != null && this.mRuleDetailViewModel != null) {

                if (!cb.isChecked()) {
                    // Not checked anymore -> deactivate the rules

                    DisposableCompletableObserver d = new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                            //TODO: use localized text
                            String message = "Deactivated";
                            Toast.makeText(RuleDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            //TODO: use localized text
                            String message = "Failure by deactivation: try again by activating and deactivating";
                            Toast.makeText(RuleDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    };

                    this.mRuleDetailViewModel.addDisposable(d);

                    this.mRuleDetailViewModel.deactivateRule(rule.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(d);
                } else {
                    DisposableCompletableObserver d = new DisposableCompletableObserver() {

                        @Override
                        public void onComplete() {
                            //TODO: use localized text
                            String message = "Activated";
                            Toast.makeText(RuleDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            //TODO: use localized text
                            String message = "Failure by activation: try again by deactivating and activating: " +e.getMessage();
                            Toast.makeText(RuleDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    };

                    this.mRuleDetailViewModel.addDisposable(d);

                    this.mRuleDetailViewModel.activateRule(rule.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(d);
                }
            }
        }
    }


    private List<String> checkPermissions(){
        List<String> permissions = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }
        }
        return permissions;
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
                if(perms.containsKey(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    try {
                        locationPermissionGranted = perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                    } catch (Exception ex) {
                        Log.e(RULE_DETAIL_ACTIVITY_CLASSNAME, "Error accessing permissions");
                    }
                }



                boolean backgroundLocationPermissionGranted = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if(perms.containsKey(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        try {
                            backgroundLocationPermissionGranted = perms.get(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
                        } catch (Exception ex) {
                            Log.e(RULE_DETAIL_ACTIVITY_CLASSNAME, "Error accessing permissions");
                        }
                    }
                }



                if (!locationPermissionGranted || !backgroundLocationPermissionGranted) {
                    Toast.makeText(this, "Location needed", Toast.LENGTH_LONG).show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}