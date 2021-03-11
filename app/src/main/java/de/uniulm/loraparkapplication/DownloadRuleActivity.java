package de.uniulm.loraparkapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import de.uniulm.loraparkapplication.adapters.RuleAdapter;
import de.uniulm.loraparkapplication.adapters.RuleDownloadAdapter;
import de.uniulm.loraparkapplication.models.DownloadRule;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.models.SensorDetail;
import de.uniulm.loraparkapplication.viewmodels.DownloadRuleViewModel;
import de.uniulm.loraparkapplication.viewmodels.SensorDetailViewModel;

public class DownloadRuleActivity extends AppCompatActivity {

    private RuleDownloadAdapter adapter;
    private DownloadRuleViewModel mDownloadRuleViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_rule);

        //Set the toolbar as the activity's app bar - to be able to show up button
        Toolbar toolbar = (Toolbar) findViewById(R.id.rule_download_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);



        RecyclerView downloadRuleRecycler = (RecyclerView) findViewById(R.id.download_rules_recycler);

        DownloadRule[] downloadRules = {};

        this.adapter = new RuleDownloadAdapter(downloadRules);
        downloadRuleRecycler.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(downloadRuleRecycler.getContext(), layoutManager.getOrientation());
        downloadRuleRecycler.addItemDecoration(dividerItemDecoration);

        downloadRuleRecycler.setLayoutManager(layoutManager);


        this.mDownloadRuleViewModel = new ViewModelProvider(this).get(DownloadRuleViewModel.class);
        this.mDownloadRuleViewModel.init();

        this.mDownloadRuleViewModel.getDownloadRules().observe(this, new Observer<Resource<List<DownloadRule>>>() {

            @Override
            public void onChanged(@Nullable Resource<List<DownloadRule>> downloadRulesResource) {

                if(downloadRulesResource != null) {

                    if (downloadRulesResource.status == Resource.Status.SUCCESS) {

                        // all correct -> add the values to the GUI

                        if (downloadRulesResource.data != null && downloadRulesResource.data.size() > 0) {
                            DownloadRule[] ruleArray = downloadRulesResource.data.toArray(new DownloadRule[0]);
                            DownloadRuleActivity.this.adapter.updateRules(ruleArray);
                        }

                    } else if (downloadRulesResource.status == Resource.Status.ERROR) {

                        // Failure to retrieve or parse the data
                        String message = getResources().getString(R.string.error_rules_not_loaded) + " (" + downloadRulesResource.message + ")";
                        Toast.makeText(DownloadRuleActivity.this, message, Toast.LENGTH_LONG).show();

                    } else {
                        // Data loading: future TODO: add loading animation
                    }
                }
            }
        });
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