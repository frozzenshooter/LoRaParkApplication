package de.uniulm.loraparkapplication.engines;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Map;
import java.util.Random;

import de.uniulm.loraparkapplication.R;

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
}
