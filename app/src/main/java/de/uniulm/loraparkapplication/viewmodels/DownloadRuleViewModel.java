package de.uniulm.loraparkapplication.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import de.uniulm.loraparkapplication.models.DownloadRule;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.repositories.DownloadRuleRepository;

public class DownloadRuleViewModel extends ViewModel {

    private MutableLiveData<Resource<List<DownloadRule>>> mDownloadRules;
    private DownloadRuleRepository mDownloadRuleRepository;

    public void init() {
        if(mDownloadRules != null){
            return;
        }
        this.mDownloadRuleRepository = DownloadRuleRepository.getInstance();
        this.mDownloadRules = this.mDownloadRuleRepository.getDownloadRules();
    }

    public LiveData<Resource<List<DownloadRule>>> getDownloadRules(){
        return mDownloadRules;
    }
}
