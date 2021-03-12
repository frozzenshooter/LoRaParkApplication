package de.uniulm.loraparkapplication.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

import de.uniulm.loraparkapplication.models.DownloadRule;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.repositories.DownloadRuleRepository;
import de.uniulm.loraparkapplication.repositories.RuleDataRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DownloadRuleViewModel extends AndroidViewModel {

    private MutableLiveData<Resource<List<DownloadRule>>> mDownloadRules;
    private DownloadRuleRepository mDownloadRuleRepository;
    private RuleDataRepository mRuleDataRepository;

    public DownloadRuleViewModel(@NonNull Application application) {
        super(application);
        this.mDownloadRuleRepository = DownloadRuleRepository.getInstance();
        this.mRuleDataRepository = RuleDataRepository.getInstance(application);
        this.mDownloadRules = this.mDownloadRuleRepository.getDownloadRules();
    }

    public LiveData<Resource<List<DownloadRule>>> getDownloadRules(){
        return mDownloadRules;
    }

    public Completable downloadRules(List<String> ruleIds){

        return Completable.complete().delay(5, TimeUnit.SECONDS, Schedulers.io());
    }
}
