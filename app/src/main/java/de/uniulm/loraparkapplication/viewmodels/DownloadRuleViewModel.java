package de.uniulm.loraparkapplication.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import de.uniulm.loraparkapplication.models.DownloadRule;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.repositories.DownloadRuleRepository;

public class DownloadRuleViewModel extends AndroidViewModel {

    private MutableLiveData<Resource<List<DownloadRule>>> mDownloadRules;
    private DownloadRuleRepository mDownloadRuleRepository;

    public DownloadRuleViewModel(@NonNull Application application) {
        //TODO: androidviewmodel to viewmodel -> and create it as singleton
        super(application);
        this.mDownloadRuleRepository = DownloadRuleRepository.getInstance();

        this.mDownloadRules = this.mDownloadRuleRepository.getDownloadRules();
    }

    public LiveData<Resource<List<DownloadRule>>> getDownloadRules(){
        return mDownloadRules;
    }

    /*public Completable downloadRules(List<String> ruleIds){
        //DisposableCompletableObserver
        return Completable.complete().delay(5, TimeUnit.SECONDS, Schedulers.io());
    }*/
}
