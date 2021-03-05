package de.uniulm.loraparkapplication.repositories;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import de.uniulm.loraparkapplication.BuildConfig;
import de.uniulm.loraparkapplication.models.Location;
import de.uniulm.loraparkapplication.models.SensorDescription;

public class SensorDescriptionRepository {

    private static SensorDescriptionRepository instance;
    private final ArrayList<SensorDescription> dataSet = new ArrayList<>();

    public static SensorDescriptionRepository getInstance() {
        if(instance == null){
            instance = new SensorDescriptionRepository();
        }
        return instance;
    }


    public MutableLiveData<List<SensorDescription>> getSensorDescriptions() {
        createTestData();
        MutableLiveData<List<SensorDescription>> data = new MutableLiveData<>();
        data.setValue(dataSet);
        return data;
    }

    private void createTestData() {

        SensorDescription sd1 = new SensorDescription();
        sd1.setId("01");
        sd1.setDescription("Test sensor 01");
        sd1.setAddress("Test address 01");
        sd1.setName("Test sensor 01");
        Location l1 = new Location();
        l1.setLatitude(48.396426);
        l1.setLongitude(9.990453);
        sd1.setLocation(l1);
        dataSet.add(sd1);

        SensorDescription sd2 = new SensorDescription();
        sd2.setId("02");
        sd2.setDescription("Test sensor 02");
        sd2.setAddress("Test address 02");
        sd2.setName("Test sensor 02");
        Location l2 = new Location();
        l2.setLatitude(48.397206);
        l2.setLongitude(9.991628);
        sd2.setLocation(l2);

        dataSet.add(sd2);
    }
}
