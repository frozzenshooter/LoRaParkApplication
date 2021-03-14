package de.uniulm.loraparkapplication.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.repositories.RuleDataRepository;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RuleOverviewViewModel extends AndroidViewModel {

    //TODO: https://developer.android.com/topic/libraries/architecture/viewmodel.html#sharing implement it like this and hold the data in here
    private final RuleDataRepository mRuleDataRepository;

    private final LiveData<List<Rule>> mAllRules;
    private final LiveData<List<Rule>> mActiveRules;
    private final LiveData<List<Rule>> mInactiveRules;

    public RuleOverviewViewModel(@NonNull Application application) {
        super(application);
        this.mRuleDataRepository = RuleDataRepository.getInstance(application);

        this.mAllRules = this.mRuleDataRepository.getLiveAllRules();
        this.mActiveRules = this.mRuleDataRepository.getRules(true);
        this.mInactiveRules = this.mRuleDataRepository.getRules(false);
    }

    public LiveData<List<Rule>> getAllRules(){
        return this.mAllRules;
    }

    public LiveData<List<Rule>> getActiveRules(){
        return this.mActiveRules;
    }

    public LiveData<List<Rule>> getInactiveRules(){
        return this.mInactiveRules;
    }

    public void downloadRule(){
        this.mRuleDataRepository.downloadNewRule("");
    }


    //TODO: find a way to hand over the status of the background task (e.g deletion/...)

    public LiveData<Resource<String>> deleteAllRules(){

        //TODO: DEACTIVATE RULES BEFORE DELETING
        return this.mRuleDataRepository.deleteAllRules();
    }

    public LiveData<Resource<String>> insertRule(@NonNull Rule rule){
        return this.mRuleDataRepository.insertRule(rule);
    }

    public LiveData<Resource<String>> downloadRules(List<String> ruleIds){

        // mRuleDataRepository.downloadNewRules(ruleIds);
        //TODO: handle the down load in the background -> Download and parse the data with a repository
        //TODO: use RXJava to save the resulting rules in the database
        //TODO: perhaps not on the android main thread -> use other threading possibilities

        Flowable t = Flowable.fromCallable(() -> Resource.success("Worked") ).delay(5, TimeUnit.SECONDS, Schedulers.io());
        LiveData<Resource<String>> ld = LiveDataReactiveStreams.fromPublisher(t);

        return ld;
    }
}
