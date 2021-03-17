package de.uniulm.loraparkapplication.engines;

import android.content.Context;
import androidx.annotation.NonNull;

import java.util.Map;

public class TestAssertAction implements RuleAction {
    public static final TestAssertAction INSTANCE = new TestAssertAction();

    private Boolean triggered = false;

    private TestAssertAction() {
        // Use INSTANCE instead.
    }

    @Override
    public String key() {
        return "assert";
    }

    @Override
    public void trigger(Context context, @NonNull Map<String, Object> data) {
        triggered = true;
    }

    public Boolean getAndResetTriggered() {
        Boolean returnTriggered = getTriggered();
        triggered = false;
        return returnTriggered;
    }

    public Boolean getTriggered() {
        return triggered;
    }
}
