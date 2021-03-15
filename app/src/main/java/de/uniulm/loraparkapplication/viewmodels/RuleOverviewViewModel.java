package de.uniulm.loraparkapplication.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;


import de.uniulm.loraparkapplication.models.CompleteRule;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.repositories.RuleHandler;

/**
 * ViewModel that handles the data for the rule overview activity
 *
 * This ViewModel implements the AndroidViewModel because the application context is needed in order to access the database
 */
public class RuleOverviewViewModel extends AndroidViewModel {

    //TODO: use an intermediate "use case" class to access the different repos (e.g. to save delete a rule you have to deactivate beforehand)
    //TODO: this allows to handle the differnent steps in a cental and abstract way and not in the viewmodel


    //TODO: https://developer.android.com/topic/libraries/architecture/viewmodel.html#sharing implement it like this and hold the data in here

    private final LiveData<List<Rule>> mAllRules;
    private final LiveData<List<Rule>> mActiveRules;
    private final LiveData<List<Rule>> mInactiveRules;

    private final RuleHandler ruleHandler;

    public RuleOverviewViewModel(@NonNull Application application) {
        super(application);
        this.ruleHandler = new RuleHandler(application);

        this.mAllRules = this.ruleHandler.getAllRules();
        this.mActiveRules = this.ruleHandler.getActiveRules();
        this.mInactiveRules = this.ruleHandler.getInactiveRules();
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

    public LiveData<Resource<String>> deleteAllRules(){
        return this.ruleHandler.deleteAllRules();
    }

    public void downloadRules(List<String> ruleIds){
        this.ruleHandler.downloadRules(ruleIds);
    }
}
