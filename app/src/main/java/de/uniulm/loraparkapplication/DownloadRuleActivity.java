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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import de.uniulm.loraparkapplication.adapters.RuleDownloadAdapter;
import de.uniulm.loraparkapplication.models.DownloadRule;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.viewmodels.DownloadRuleViewModel;

public class DownloadRuleActivity extends AppCompatActivity {

    public final static String SELECTED_RULES = "SELECTED_RULES";
    public final static Integer REQUEST_ID = 1;

    private RuleDownloadAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_rule);

        //Set the toolbar as the activity's app bar - to be able to show up button
        Toolbar toolbar = (Toolbar) findViewById(R.id.rule_download_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView downloadRuleRecycler = (RecyclerView) findViewById(R.id.download_rules_recycler);

        DownloadRule[] downloadRules = {};

        this.adapter = new RuleDownloadAdapter(downloadRules);
        downloadRuleRecycler.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(downloadRuleRecycler.getContext(), layoutManager.getOrientation());
        downloadRuleRecycler.addItemDecoration(dividerItemDecoration);

        downloadRuleRecycler.setHasFixedSize(true);

        downloadRuleRecycler.setLayoutManager(layoutManager);

        // Retrieve the selected items from previous creation (e.g. after device rotation the selection has to be set again)
        HashSet<String> selectedRules = new HashSet<>();
        if(savedInstanceState != null){
            final String[] checkRuleIds = savedInstanceState.getStringArray(SELECTED_RULES);

            selectedRules.addAll(Arrays.asList(checkRuleIds));
        }

        DownloadRuleViewModel mDownloadRuleViewModel = new ViewModelProvider(this).get(DownloadRuleViewModel.class);

        mDownloadRuleViewModel.getDownloadRules().observe(this, new Observer<Resource<List<DownloadRule>>>() {

            @Override
            public void onChanged(@Nullable Resource<List<DownloadRule>> downloadRulesResource) {

                if(downloadRulesResource != null) {

                    if (downloadRulesResource.status == Resource.Status.SUCCESS) {

                        // all correct -> add the values to the GUI

                        if (downloadRulesResource.data != null && downloadRulesResource.data.size() > 0) {
                            DownloadRule[] ruleArray = downloadRulesResource.data.toArray(new DownloadRule[0]);

                            DownloadRuleActivity.this.adapter.updateRules(ruleArray, selectedRules);
                        }

                    } else if (downloadRulesResource.status == Resource.Status.ERROR) {

                        // Failure to retrieve or parse the data
                        String message = getResources().getString(R.string.error_rules_not_loaded) + " (" + downloadRulesResource.message + ")";
                        Toast.makeText(DownloadRuleActivity.this, message, Toast.LENGTH_LONG).show();

                    } else {
                        // Data loading - TODO: add loading animation
                    }
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        final List<String> selectedRules= this.adapter.getSelectedRuleIds();

        savedInstanceState.putStringArray(SELECTED_RULES, selectedRules.toArray(new String[0]));
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

    /**
     * Finishes the activity and sets the result of the activity (list of selected rules)
     *
     * If no rule is selected a toast will be created with a info message.
     */
    public void downloadRules(@Nullable View view) {

        List<String> selectedRules = adapter.getSelectedRuleIds();

        if(selectedRules.size() == 0){
            Toast.makeText(DownloadRuleActivity.this, getResources().getString(R.string.info_no_rule_selected), Toast.LENGTH_LONG).show();
        }

        // hand over the ids of the selected rules that will be downloaded
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra(SELECTED_RULES, new ArrayList<String>(selectedRules));
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}