package de.uniulm.loraparkapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import de.uniulm.loraparkapplication.fragments.ActiveRulesFragment;
import de.uniulm.loraparkapplication.fragments.AllRulesFragment;
import de.uniulm.loraparkapplication.fragments.InactiveRulesFragment;
import de.uniulm.loraparkapplication.models.Action;
import de.uniulm.loraparkapplication.models.CompleteRule;
import de.uniulm.loraparkapplication.models.Geofence;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.models.Sensor;
import de.uniulm.loraparkapplication.viewmodels.RuleOverviewViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RuleOverviewActivity extends AppCompatActivity {

    public final static int ACTIVE_RULE_TAB_INDEX = 0;
    public final static int INACTIVE_RULE_TAB_INDEX = 1;
    public final static int ALL_RULE_TAB_INDEX = 2;

    private RuleOverviewViewModel mRuleOverviewViewModel;

    /**
     * Create a random string - use for testing to create random ids for rules
     *
     * @return random string
     */
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
                startDownloadRuleActivity();
            }
        });
    }

    /**
     * Start the startDownloadRuleActivity
     */
    private void startDownloadRuleActivity(){
        Intent intent = new Intent(this, DownloadRuleActivity.class);
        startActivityForResult(intent, DownloadRuleActivity.REQUEST_ID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_delete_all_rules:

                DisposableCompletableObserver d = new DisposableCompletableObserver() {

                   @Override
                    public void onComplete() {
                       //TODO: use localized text
                       String message = "Everything deleted";
                       Toast.makeText(RuleOverviewActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        //TODO: use localized text
                        String message = "Failure deleting the data";
                        Toast.makeText(RuleOverviewActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                };

                this.mRuleOverviewViewModel.addDisposable(d);

                RuleOverviewActivity.this.mRuleOverviewViewModel.deleteAllRules()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(d);

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

            //TODO: delete toast later
            StringBuilder build = new StringBuilder();
            for(int i=0 ; i < rulesToDownload.size(); i++){
                build.append(rulesToDownload.get(i));
                build.append("; ");
            }

            Toast.makeText(this, build.toString(), Toast.LENGTH_SHORT).show();

            if(rulesToDownload != null && rulesToDownload.size() > 0) {

                this.mRuleOverviewViewModel.downloadRules(rulesToDownload)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<CompleteRule>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                mRuleOverviewViewModel.addDisposable(d);
                            }

                            @Override
                            public void onNext(@NonNull CompleteRule s) {
                               // String message = "Saved data: "+s;
                                //Toast.makeText(RuleOverviewActivity.this, message, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {

                                //TODO: remove exception message and replace with localized string
                                String message = "Failure saving one of the rules: " + e.getMessage();
                                Toast.makeText(RuleOverviewActivity.this, message, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rule_overview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Fragment adapter for the tab view
     */
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