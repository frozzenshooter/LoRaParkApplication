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
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * ViewModel that handles the data for the rule overview activity
 *
 * This ViewModel implements the AndroidViewModel because the application context is needed in order to access the database
 */
public class RuleOverviewViewModel extends AndroidViewModel {

    private final LiveData<List<Rule>> mAllRules;
    private final LiveData<List<Rule>> mActiveRules;
    private final LiveData<List<Rule>> mInactiveRules;

    private CompositeDisposable disposables;

    private final RuleHandler ruleHandler;

    public RuleOverviewViewModel(@NonNull Application application) {
        super(application);
        this.ruleHandler = new RuleHandler(application);

        this.mAllRules = this.ruleHandler.getAllRules();
        this.mActiveRules = this.ruleHandler.getActiveRules();
        this.mInactiveRules = this.ruleHandler.getInactiveRules();

        this.disposables = new CompositeDisposable();
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

    public Completable deleteAllRules(){

        return this.ruleHandler.deleteAllRules();
    }

    public Observable<String> downloadRules(List<String> ruleIds){
        return this.ruleHandler.downloadRules(ruleIds);
    }

    /**
     * Call this method from every subscriber to be sure it will be reset
     *
     * @param d
     */
    public void addDisposable(Disposable d){
        this.disposables.add(d);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        //needed to clear the subscribers that aren't needed anymore
        disposables.clear();
    }
}
