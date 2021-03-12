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

    private final MutableLiveData<Resource<List<Rule>>> mAllRules;
    private final MutableLiveData<Resource<List<Rule>>> mActiveRules;
    private final MutableLiveData<Resource<List<Rule>>> mInactiveRules;

    public RuleOverviewViewModel(@NonNull Application application) {
        super(application);
        this.mRuleDataRepository = RuleDataRepository.getInstance(application);

        this.mAllRules = new MutableLiveData<>();
        this.mActiveRules = new MutableLiveData<>();
        this.mInactiveRules = new MutableLiveData<>();

        refresh();
    }

    public LiveData<Resource<List<Rule>>> getAllRules(){
        return this.mAllRules;
    }

    public LiveData<Resource<List<Rule>>> getActiveRules(){
        return this.mActiveRules;
    }

    public LiveData<Resource<List<Rule>>> getInactiveRules(){
        return this.mInactiveRules;
    }

    //region Refresh methods

    private void refreshAllRules(){
        Observable<List<Rule>> observableAllRules = this.mRuleDataRepository.getAllRules();

        observableAllRules.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<List<Rule>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        mAllRules.postValue(Resource.loading(null));
                    }

                    @Override
                    public void onNext(List<Rule> rules) {
                        mAllRules.postValue(Resource.success(rules));
                    }

                    @Override
                    public void onError(Throwable e) {
                        mAllRules.postValue(Resource.error(e.getMessage(), null));
                    }

                    @Override
                    public void onComplete() {
                        //cleaning up tasks
                    }
                });
    }

    private void refreshActiveRules(){
        Observable<List<Rule>> observableActiveRules = this.mRuleDataRepository.getRules(true);

        observableActiveRules.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<List<Rule>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        mActiveRules.postValue(Resource.loading(null));
                    }

                    @Override
                    public void onNext(List<Rule> rules) {
                        mActiveRules.postValue(Resource.success(rules));
                    }

                    @Override
                    public void onError(Throwable e) {
                        mActiveRules.postValue(Resource.error(e.getMessage(), null));
                    }

                    @Override
                    public void onComplete() {
                        //cleaning up tasks
                    }
                });
    }

    private void refreshInactiveRules(){
        Observable<List<Rule>> observableInactiveRules = this.mRuleDataRepository.getRules(false);

        observableInactiveRules.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<List<Rule>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        mInactiveRules.postValue(Resource.loading(null));
                    }

                    @Override
                    public void onNext(List<Rule> rules) {
                        mInactiveRules.postValue(Resource.success(rules));
                    }

                    @Override
                    public void onError(Throwable e) {
                        mInactiveRules.postValue(Resource.error(e.getMessage(), null));
                    }

                    @Override
                    public void onComplete() {
                        //cleaning up tasks
                    }
                });
    }

    public void refresh() {
        this.refreshActiveRules();
        this.refreshInactiveRules();
        this.refreshAllRules();
    }

    //endregion

    //TODO: find a way to hand over the status of the background task (e.g deletion/...)

    public void deleteAllRules(){

        Observable<Resource<Boolean>> deleteAllRulesObservable = this.mRuleDataRepository.deleteAllRules();

        deleteAllRulesObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<Resource<Boolean>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        // nothing to do here
                    }

                    @Override
                    public void onNext(Resource<Boolean> data) {
                        //Possible additional handling for the future
                    }

                    @Override
                    public void onError(Throwable e) {
                        //TODO: error handling
                    }

                    @Override
                    public void onComplete() {
                        refresh();
                    }
                });
    }

    public void insertRule(@NonNull Rule rule){

        Observable insertRule = this.mRuleDataRepository.insertRule(rule);

        insertRule.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<Resource<Boolean>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        // nothing to do here
                    }

                    @Override
                    public void onNext(Resource<Boolean> data) {
                        //Possible additional handling for the future
                    }

                    @Override
                    public void onError(Throwable e) {
                        //TODO: error handling
                    }

                    @Override
                    public void onComplete() {
                        refresh();
                    }
                });
    }

    public LiveData<Resource<String>> downloadRules(List<String> ruleIds){

        // mRuleDataRepository.downloadNewRules(ruleIds);
        Flowable t = Flowable.fromCallable(() -> Resource.success("Worked") ).delay(5, TimeUnit.SECONDS, Schedulers.io());
        LiveData<Resource<String>> ld = LiveDataReactiveStreams.fromPublisher(t);

        this.refresh();

        //TODO: to solve the refresh problem: you load the data over here and push it in the Livedata (has to be changed into mutuable live data)
        return ld;
    }
}
