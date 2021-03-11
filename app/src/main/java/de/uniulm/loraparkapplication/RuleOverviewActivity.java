package de.uniulm.loraparkapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Random;

import de.uniulm.loraparkapplication.fragments.ActiveRulesFragment;
import de.uniulm.loraparkapplication.fragments.AllRulesFragment;
import de.uniulm.loraparkapplication.fragments.InactiveRulesFragment;
import de.uniulm.loraparkapplication.models.DownloadRule;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.viewmodels.RuleOverviewViewModel;
import de.uniulm.loraparkapplication.viewmodels.SensorOverviewViewModel;

public class RuleOverviewActivity extends AppCompatActivity {

    public final static int ACTIVE_RULE_TAB_INDEX = 0;
    public final static int INACTIVE_RULE_TAB_INDEX = 1;
    public final static int ALL_RULE_TAB_INDEX = 2;

    private RuleOverviewViewModel mRuleOverviewViewModel;

    private Boolean refreshAllRules = false;
    private Boolean refreshActiveRules = false;
    private Boolean refreshInactiveRules = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_overview);

        //Set the toolbar as the activity's app bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.rule_overview_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Attach the SectionsPagerAdapter to the ViewPager
        RuleSectionsPagerAdapter pagerAdapter = new RuleSectionsPagerAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);

        //Attach the ViewPager to the TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);

        this.mRuleOverviewViewModel =  new ViewModelProvider(this).get(RuleOverviewViewModel.class);

        FloatingActionButton fab = findViewById(R.id.fab_add_rule);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
               startDownloadActivity();
            }
        });
    }

    private void startDownloadActivity(){
        Intent intent = new Intent(this, DownloadRuleActivity.class);
        startActivityForResult(intent, DownloadRuleActivity.REQUEST_ID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_add_rule:
                RuleOverviewActivity.this.mRuleOverviewViewModel.deleteAllRules();
                startDownloadActivity();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:

                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null && requestCode == DownloadRuleActivity.REQUEST_ID){
            ArrayList<String> rulesToDownload = data.getStringArrayListExtra(DownloadRuleActivity.SELECTED_RULES);

            StringBuilder build = new StringBuilder();
            for(int i=0 ; i < rulesToDownload.size(); i++){
                build.append(rulesToDownload.get(i));
                build.append("; ");
            }

            Toast.makeText(this, build.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rule_overview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private class RuleSectionsPagerAdapter extends FragmentPagerAdapter {
        public RuleSectionsPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case ACTIVE_RULE_TAB_INDEX:
                    return new ActiveRulesFragment();
                case INACTIVE_RULE_TAB_INDEX:
                    return new InactiveRulesFragment();
                case ALL_RULE_TAB_INDEX:
                    return new AllRulesFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case ACTIVE_RULE_TAB_INDEX:
                    return getResources().getText(R.string.label_tab_active_rules);
                case INACTIVE_RULE_TAB_INDEX:
                    return getResources().getText(R.string.label_tab_inactive_rules);
                case ALL_RULE_TAB_INDEX:
                    return getResources().getText(R.string.label_tab_all_rules);
            }
            return null;
        }
    }

    public Boolean getRefreshAllRulesFragments(){
        return this.refreshAllRules;
    }

    public void setRefreshAllRules(Boolean refresh){
        this.refreshAllRules = refresh;
    }

    public Boolean getRefreshActiveRulesFragments(){
        return this.refreshActiveRules;
    }

    public void setRefreshActiveRules(Boolean refresh){
        this.refreshActiveRules = refresh;
    }

    public Boolean getRefreshInactiveRulesFragments(){
        return this.refreshInactiveRules;
    }

    public void setRefreshInactiveRules(Boolean refresh){
        this.refreshInactiveRules = refresh;
    }
}