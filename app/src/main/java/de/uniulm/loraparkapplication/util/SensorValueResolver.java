package de.uniulm.loraparkapplication.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import de.uniulm.loraparkapplication.R;

public class SensorValueResolver {

    private static SensorValueResolver instance;
    private static final String KEY_RESOLVER_CLASSNAME = SensorValueResolver.class.getName();

    //region Available keys

    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";

    //endregion

    public static SensorValueResolver getInstance(){
        if(instance == null){
            instance = new SensorValueResolver();
        }
        return instance;
    }

    /**
     * Resolves a key and returns the appropriate label for the UI
     *
     * @param ctx context is needed because by saving a context in a singelton you could create a memory leak
     * @param key the key that shall be resolved to get the label (based on the resource files)
     * @return label
     */
    public String resolveKey(Context ctx, String key){

        String label = "";

        key = key.toLowerCase();

        switch (key){
            case KEY_ID:
                label = getResource(ctx, R.string.label_key_id);
                break;
            case KEY_NAME:
                label = getResource(ctx, R.string.label_key_name);
                break;
            case KEY_DESCRIPTION:
                label = getResource(ctx, R.string.label_key_description);
                break;

            default:
                label = key;
                break;
        }

        return label;
    }

    /**
     * Fetches the resource for the resource id
     *
     * @param ctx context
     * @param id id of the resource
     * @return label - if no resource is found, the label will say 'Label not found!'
     */
    private String getResource(@NonNull Context ctx, @StringRes int id){
        String label = "";

        try{
           label = ctx.getResources().getString(id);
        }catch(Resources.NotFoundException ex){
            Log.e(KEY_RESOLVER_CLASSNAME, "Label not found: " + ex.getMessage());
            label = "Label not found!";
        }

        return label;
    }

    public String resolveUnit(String key){

        String unit = null;

        if(key != null && !key.isEmpty() && !key.equals("null")) {

            key = key.toLowerCase();

            switch (key) {
                case KEY_ID:
                    unit = null;
                    break;
                case KEY_NAME:
                    unit = null;
                    break;
                case KEY_DESCRIPTION:
                    unit = null;
                    break;
                default:
                    unit = null;
                    break;
            }
        }

        return unit;
    }
}
