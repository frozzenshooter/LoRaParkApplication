package de.uniulm.loraparkapplication.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.uniulm.loraparkapplication.BackgroundeJobService;

public class BackgroundJobServiceStartReceiver extends BroadcastReceiver {
    public static final String BACKGROUND_JOB_SERVICE_START_RECEIVER_TAG = BackgroundJobServiceStartReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(BACKGROUND_JOB_SERVICE_START_RECEIVER_TAG, "received boot");
        BackgroundeJobService.scheduleJob(context);
    }
}
