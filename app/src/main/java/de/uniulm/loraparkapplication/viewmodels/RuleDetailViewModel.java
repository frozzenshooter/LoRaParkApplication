package de.uniulm.loraparkapplication.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.repositories.RuleDataRepository;
import de.uniulm.loraparkapplication.repositories.RuleHandler;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * ViewModel that handles the data for the rule detail activity
 */
public class RuleDetailViewModel extends AndroidViewModel {

    private LiveData<Rule> mRule;
    private final RuleHandler mRuleHandler;
    private Boolean isInitialized = false;
    private CompositeDisposable disposables;

    public RuleDetailViewModel(@NonNull Application application) {
        super(application);
        this.mRuleHandler = RuleHandler.getInstance(application);
        this.disposables = new CompositeDisposable();
    }

    public void init(@NonNull String ruleId){
        this.mRule = this.mRuleHandler.getRule(ruleId);
        this.isInitialized = true;
    }

    public Completable activateRule(@NonNull String ruleId){
        return this.mRuleHandler.activateRule(ruleId);
    }

    public Completable deactivateRule(@NonNull String ruleId){
        return this.mRuleHandler.deactivateRule(ruleId);
    }

    public LiveData<Rule> getRule(){
        if(!this.isInitialized){
            throw new IllegalStateException();
        }
        return this.mRule;
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

        //TODO: use ConnectableObservable to be able to reconnect again
        disposables.clear();
    }
}
