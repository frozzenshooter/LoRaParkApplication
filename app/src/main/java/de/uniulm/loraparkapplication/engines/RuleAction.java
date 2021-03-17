package de.uniulm.loraparkapplication.engines;

import android.content.Context;

import java.util.Map;

public interface RuleAction {
    String key();

    void trigger(Context context, Map<String, Object> data);
}
