package de.uniulm.loraparkapplication.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.uniulm.loraparkapplication.models.Action;
import de.uniulm.loraparkapplication.models.Geofence;
import de.uniulm.loraparkapplication.models.GeofenceTracker;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.models.Sensor;

@Database(entities = {Rule.class, Sensor.class, Action.class, Geofence.class, GeofenceTracker.class}, version = 1, exportSchema = false)
public abstract class RuleDatabase extends RoomDatabase {

    public abstract RuleDao ruleDao();
    public abstract GeofenceDao geofenceDao();

    private static volatile RuleDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static RuleDatabase getDatabase(final Context context) {
        if (instance == null) {

            synchronized (RuleDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),RuleDatabase.class, "rules_database").build();
                }
            }
        }
        return instance;
    }
}
