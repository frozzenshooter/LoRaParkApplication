package de.uniulm.loraparkapplication.viewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import de.uniulm.loraparkapplication.models.DownloadRule;
import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.repositories.DownloadRuleRepository;

/**
 * ViewModel that handles the data for the download rule activity
 */
public class DownloadRuleViewModel extends ViewModel {

    private final MutableLiveData<Resource<List<DownloadRule>>> mDownloadRules;

    public DownloadRuleViewModel() {
        super();
        DownloadRuleRepository mDownloadRuleRepository = DownloadRuleRepository.getInstance();

        this.mDownloadRules = mDownloadRuleRepository.getDownloadRules();
    }

    public LiveData<Resource<List<DownloadRule>>> getDownloadRules(){
        return mDownloadRules;
    }
}
