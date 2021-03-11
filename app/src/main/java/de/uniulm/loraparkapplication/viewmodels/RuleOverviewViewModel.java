package de.uniulm.loraparkapplication.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.repositories.RuleDataRepository;

public class RuleOverviewViewModel extends AndroidViewModel {

    //TODO: https://developer.android.com/topic/libraries/architecture/viewmodel.html#sharing implement it like this and hold the data in here
    private RuleDataRepository mRuleDataRepository;

    private LiveData<Resource<List<Rule>>> mAllRules;
    private LiveData<Resource<List<Rule>>> mActiveRules;
    private LiveData<Resource<List<Rule>>> mInactiveRules;

    private MutableLiveData<Integer> mRefreshCount;
    private Integer counter;

    public RuleOverviewViewModel(@NonNull Application application) {
        super(application);
        mRuleDataRepository = RuleDataRepository.getInstance(application);
        mRefreshCount = new MutableLiveData<Integer>();
        mRefreshCount.setValue(0);
        counter = 0;
    }

    public LiveData<Resource<List<Rule>>> getAllRules(){
        if(this.mAllRules == null){
            //this.mAllRules = this.mRuleDataRepository.getAllRules();
            this.mAllRules = Transformations.switchMap(mRefreshCount, count -> this.mRuleDataRepository.getAllRules());
        }

        return this.mAllRules;
    }

    public LiveData<Resource<List<Rule>>> getActiveRules(){
        if(this.mActiveRules == null){
            //this.mActiveRules = mRuleDataRepository.getRules(true);
            this.mActiveRules = Transformations.switchMap(mRefreshCount, count -> this.mRuleDataRepository.getRules(true));
        }

        return this.mActiveRules;
    }

    public LiveData<Resource<List<Rule>>> getInactiveRules(){
        if(this.mInactiveRules == null){
            //this.mInactiveRules = mRuleDataRepository.getRules(false);
            this.mInactiveRules = Transformations.switchMap(mRefreshCount, count -> this.mRuleDataRepository.getRules(false));
        }

        return this.mInactiveRules;
    }

    public void refresh() {
        //TODO: this refresh destroys the observers and it won't be refreshing the Recyclerview
        //TODO: possible solution migth be the usage of Transformations.switchmap: https://stackoverflow.com/questions/47610676/how-and-where-to-use-transformations-switchmap
        //TODO: https://stackoverflow.com/questions/51154786/android-implement-search-with-view-model-and-live-data
        //TODO: other solution migth be: https://stackoverflow.com/questions/53467820/how-to-properly-reload-livedata-manually-in-android

        //TODO: FOR NOW WORKAROUND BASED ON COUNTER

        //this.mInactiveRules = this.mRuleDataRepository.getRules(false);
        //this.mActiveRules = this.mRuleDataRepository.getRules(true);
        //this.mAllRules = this.mRuleDataRepository.getAllRules();
        if(this.counter > 1000000){
            this.counter = 0;
        }
        this.counter = this.counter + 1;
        this.mRefreshCount.setValue(this.counter);
    }

    public void insertRule(@NonNull Rule rule){
        mRuleDataRepository.insertRule(rule);
    }

    public void deleteAllRules(){
        mRuleDataRepository.deleteAllRules();
    }
}