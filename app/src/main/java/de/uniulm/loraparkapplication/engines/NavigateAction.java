package de.uniulm.loraparkapplication.engines;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.Map;

public class NavigateAction implements RuleAction  {
    public static final NavigateAction INSTANCE = new NavigateAction();

    private NavigateAction() {
        // Use INSTANCE instead.
    }

    @Override
    public String key() {
        return "navigate";
    }

    @Override
    public void trigger(Context context, @NonNull Map<String, Object> data) {
        String query = (String) data.getOrDefault("query", ""); //  see https://developers.google.com/maps/documentation/urls/get-started

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(query));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
