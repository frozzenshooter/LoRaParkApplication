package de.uniulm.loraparkapplication.engines;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.uniulm.loraparkapplication.R;
import de.uniulm.loraparkapplication.repositories.RuleHandler;

public class NotificationAction implements RuleAction  {
    public static final NotificationAction INSTANCE = new NotificationAction();

    private NotificationAction() {
        // Use INSTANCE instead.
    }

    @Override
    public String key() {
        return "notification";
    }

    @Override
    public void trigger(Context context, @NonNull Map<String, Object> data) {
        String channel = (String) data.get("channel");
        String title = (String) data.getOrDefault("title", "default");
        String text = (String) data.getOrDefault("text", "default");
        List<HashMap<String, Object>> buttons = (List<HashMap<String, Object>>) data.getOrDefault("buttons", new ArrayList<>());

        if(channel == null) {
            channel = "default";
        }

        //create channel (ignored if already present)
        createNotificationChannel(context, channel);

        // build and show notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel)
            .setSmallIcon(R.mipmap.ic_launcher) // TODO fix icon
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        for (Map<String, Object> button : buttons) {
            String buttonTitle = (String) button.get("title");
            Serializable buttonActions = (Serializable) button.get("actions");

            Intent intentAction = new Intent(context, NotificationBroadcastReceiver.class);
            intentAction.putExtra("actions", buttonActions);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,1,intentAction,PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action action = new NotificationCompat.Action.Builder(0, buttonTitle, pendingIntent).build();

            builder.addAction(action);

        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // TODO not use random id
        notificationManager.notify(new Random().nextInt(1000), builder.build());

    }

    private void createNotificationChannel(Context context, String name) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(name, name,  NotificationManager.IMPORTANCE_DEFAULT);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static class NotificationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive (Context context , Intent intent) {
            Serializable actionsSerialized = intent.getSerializableExtra( "actions");
            if (actionsSerialized instanceof List) {
                Application mApplication = ((Application)context.getApplicationContext());
                RuleEngine ruleEngine = RuleEngine.getInstance(mApplication);

                List<Map<String, Object>> actions = (List<Map<String, Object>>) actionsSerialized;

                for(Map<String, Object> action : actions) {
                    ruleEngine.triggerAction((String) action.get("action"), (Map<String, Object> )action.get("data"));
                }
            } else {
                Log.i(NotificationBroadcastReceiver.class.getName(), "actions not a list");
            }
        }
    }
}
