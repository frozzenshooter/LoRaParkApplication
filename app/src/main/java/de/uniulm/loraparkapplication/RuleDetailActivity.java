package de.uniulm.loraparkapplication;

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
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.models.SensorDetail;
import de.uniulm.loraparkapplication.viewmodels.RuleDetailViewModel;
import de.uniulm.loraparkapplication.viewmodels.SensorDetailViewModel;

public class RuleDetailActivity extends AppCompatActivity {

    public final static String RULE_ID_EXTRA = "RULE_ID_EXTRA";

    private static final String RULE_DETAIL_ACTIVITY_CLASSNAME = RuleDetailActivity.class.getName();

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

    private void handleNewRuleData(@NotNull Rule rule){
        //TODO: Update GUI
        TextView textView = findViewById(R.id.rule_detail_text_view);

        StringBuilder detailsBuilder = new StringBuilder();

        detailsBuilder.append("Rule id: ");
        detailsBuilder.append(rule.getId());
        detailsBuilder.append("\n");

        if(rule.getName() != null){
            detailsBuilder.append("Rule name: ");
            detailsBuilder.append(rule.getName());
            detailsBuilder.append("\n");
        }

        if(rule.getDescription() != null){
            detailsBuilder.append("Rule description: ");
            detailsBuilder.append(rule.getDescription());
            detailsBuilder.append("\n");
        }

        if(rule.getIsActive() != null){
            detailsBuilder.append("Rule active: ");
            detailsBuilder.append(rule.getIsActive() ? "true" : "false");
            detailsBuilder.append("\n");
        }

        textView.setText(detailsBuilder.toString());
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