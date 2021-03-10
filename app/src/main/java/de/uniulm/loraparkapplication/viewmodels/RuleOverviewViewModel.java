package de.uniulm.loraparkapplication.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.repositories.RuleDataRepository;

public class RuleOverviewViewModel extends AndroidViewModel {

    //TODO: https://developer.android.com/topic/libraries/architecture/viewmodel.html#sharing implemnt it like this and hold the data in here
    private RuleDataRepository mRuleDataRepository;

    public RuleOverviewViewModel(@NonNull Application application) {
        super(application);
        mRuleDataRepository = RuleDataRepository.getInstance(application);
    }

    public LiveData<Resource<List<Rule>>> getAllRules(){
        return mRuleDataRepository.getAllRules();
    }

    public LiveData<Resource<List<Rule>>> getRules(Boolean isActive){
        return mRuleDataRepository.getRules(isActive);
    }

    public void insertRule(@NonNull Rule rule){
        mRuleDataRepository.insertRule(rule);
    }

    public void deleteAllRules(){
        mRuleDataRepository.deleteAllRules();
    }
}
