package de.uniulm.loraparkapplication.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.repositories.RuleDataRepository;

public class RuleDetailViewModel extends AndroidViewModel {

    private LiveData<Rule> mRule;
    private final RuleDataRepository mRuleDataRepository;
    private Boolean isInitialized = false;

    public RuleDetailViewModel(@NonNull Application application) {
        super(application);
        this.mRuleDataRepository = RuleDataRepository.getInstance(application);

    }

    public void init(@NonNull String ruleId){
        this.mRule = this.mRuleDataRepository.getRule(ruleId);
        this.isInitialized = true;
    }

    public LiveData<Rule> getRule(){
        if(!this.isInitialized){
            throw new IllegalStateException();
        }
        return this.mRule;
    }
}
