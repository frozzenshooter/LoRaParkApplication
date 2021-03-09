package de.uniulm.loraparkapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;

import de.uniulm.loraparkapplication.fragments.ActiveRulesFragment;
import de.uniulm.loraparkapplication.fragments.AllRulesFragment;
import de.uniulm.loraparkapplication.fragments.InactiveRulesFragment;

public class RuleOverviewActivity extends AppCompatActivity {

    public final static int ACTIVE_RULE_TAB_INDEX = 0;
    public final static int INACTIVE_RULE_TAB_INDEX = 1;
    public final static int ALL_RULE_TAB_INDEX = 2;


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
}