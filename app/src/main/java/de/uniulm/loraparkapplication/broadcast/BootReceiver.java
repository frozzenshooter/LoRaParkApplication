package de.uniulm.loraparkapplication.broadcast;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.uniulm.loraparkapplication.BackgroundeJobService;
import de.uniulm.loraparkapplication.repositories.RuleHandler;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BootReceiver extends BroadcastReceiver {
    public static final String BACKGROUND_JOB_SERVICE_START_RECEIVER_TAG = BootReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(BACKGROUND_JOB_SERVICE_START_RECEIVER_TAG, "received boot");

        RuleHandler.getInstance((Application) context.getApplicationContext()).loadGeofences().subscribeOn(Schedulers.io()).subscribe();

        BackgroundeJobService.scheduleJobAutoRefresh(context);
    }
}
