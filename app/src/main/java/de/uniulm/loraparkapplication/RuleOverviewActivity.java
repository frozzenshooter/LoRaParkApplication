package de.uniulm.loraparkapplication;

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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Random;

import de.uniulm.loraparkapplication.fragments.ActiveRulesFragment;
import de.uniulm.loraparkapplication.fragments.AllRulesFragment;
import de.uniulm.loraparkapplication.fragments.InactiveRulesFragment;
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

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(15);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

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
               // Intent intent = new Intent(RuleOverviewActivity.this, DownloadRuleActivity.class);
               // RuleOverviewActivity.this.startActivity(intent);
               RuleOverviewActivity.this.refreshAllRules = true;
               RuleOverviewActivity.this.refreshActiveRules = true;
               RuleOverviewActivity.this.refreshInactiveRules = true;


                Rule rule = new Rule();
                rule.setName("New Rule");
                rule.setDescription("Rule description");
                rule.setId(random());
                rule.setCondition("Condition");
                rule.setIsActive(true);

                Rule rule2 = new Rule();
                rule2.setName("New Rule");
                rule2.setDescription("Rule description");
                rule2.setId(random());
                rule2.setCondition("Condition");
                rule2.setIsActive(false);


                RuleOverviewActivity.this.mRuleOverviewViewModel.insertRule(rule);
                RuleOverviewActivity.this.mRuleOverviewViewModel.insertRule(rule2);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_add_rule:
                RuleOverviewActivity.this.mRuleOverviewViewModel.deleteAllRules();
                Intent intent = new Intent(this, DownloadRuleActivity.class);
                startActivity(intent);
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