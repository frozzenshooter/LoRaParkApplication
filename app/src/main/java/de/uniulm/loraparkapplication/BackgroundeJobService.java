package de.uniulm.loraparkapplication;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.util.Log;

import de.uniulm.loraparkapplication.engines.RuleEngine;

public class BackgroundeJobService extends JobService {
    private static final String BACKGROUND_JOB_SERVICE_TAG = BackgroundeJobService.class.getName();

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(BACKGROUND_JOB_SERVICE_TAG, "executing service");

        RuleEngine ruleEngine = RuleEngine.getInstance(getApplication());
        ruleEngine.trigger();

        if(params.getExtras().getBoolean("auto_refresh", false)) {
            BackgroundeJobService.scheduleJob(getApplicationContext());
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    public static void scheduleJob(Context context) {
        // TODO adjust min/max Latency
        scheduleJob(context, 60000, 300000, true);

    }

    public static void scheduleJob(Context context, int minLatencyMillis, int maxDelayMillis, boolean auto_refresh) {
        Log.i(BACKGROUND_JOB_SERVICE_TAG, "scheduling job");

        ComponentName serviceComponent = new ComponentName(context, BackgroundeJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(minLatencyMillis); // wait at least one Minute
        builder.setOverrideDeadline(maxDelayMillis); // maximum delay five Minutes

        PersistableBundle extras = new PersistableBundle();
        extras.putBoolean("auto_refresh", auto_refresh);
        builder.setExtras(extras);

        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not

        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

}
