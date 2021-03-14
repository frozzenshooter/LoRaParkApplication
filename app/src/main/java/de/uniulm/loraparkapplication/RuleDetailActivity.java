package de.uniulm.loraparkapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.models.SensorDetail;
import de.uniulm.loraparkapplication.viewmodels.RuleDetailViewModel;
import de.uniulm.loraparkapplication.viewmodels.SensorDetailViewModel;
import de.uniulm.loraparkapplication.views.KeyValueView;

public class RuleDetailActivity extends AppCompatActivity {

    public final static String RULE_ID_EXTRA = "RULE_ID_EXTRA";

    private static final String RULE_DETAIL_ACTIVITY_CLASSNAME = RuleDetailActivity.class.getName();
    private final static float REDUCED_TEXT_SIZE = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_detail);

        //Set the toolbar as the activity's app bar - to be able to show up button
        Toolbar toolbar = (Toolbar) findViewById(R.id.rule_detail_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        Bundle extras = getIntent().getExtras();
        String id = null;
        if(extras!=null) {
            id = (String) extras.get(RULE_ID_EXTRA);
        }

        if(id != null){
            
            RuleDetailViewModel mRuleDetailViewModel = new ViewModelProvider(this).get(RuleDetailViewModel.class);
            mRuleDetailViewModel.init(id);

            mRuleDetailViewModel.getRule().observe(this, new Observer<Rule>() {
                @Override
                public void onChanged(@Nullable Rule rule) {
                    if(rule!= null){
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
    private void handleNewRuleData(@NotNull Rule rule){

        if(rule.getName() != null){
            KeyValueView kv = findViewById(R.id.rule_details_name);
            kv.setValues("Name", rule.getName(), null);
        }

        if(rule.getDescription() != null){

            KeyValueView kv = findViewById(R.id.rule_details_description);
            kv.setValueTextSize(REDUCED_TEXT_SIZE);
            kv.setValues("Description", rule.getDescription(), null);
        }

        if(rule.getIsActive() != null){
            CheckBox cb = findViewById(R.id.rule_details_is_active);
            cb.setChecked(rule.getIsActive());
        }

        //TODO: show map with geofence or the relevant sensor ??
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