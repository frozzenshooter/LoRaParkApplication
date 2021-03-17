package de.uniulm.loraparkapplication;

import android.app.Application;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import de.uniulm.loraparkapplication.models.Resource;
import de.uniulm.loraparkapplication.repositories.SensorValuesRepository;

@RunWith(AndroidJUnit4.class)
public class SensorValuesRepositoryTest {
    final Context appContext;
    final Application application;


    public SensorValuesRepositoryTest() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        application = (Application) appContext.getApplicationContext();
    }

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void test() {
        SensorValuesRepository sensorValuesRepository = new SensorValuesRepository();

        MutableLiveData<Resource<Map<String, Map<String, Map<String, Object>>>>> liveData = sensorValuesRepository.getSensorValues(Collections.singletonList("davis-013d4d"));

        liveData.observeForever(resource -> {
            if(resource.status == Resource.Status.ERROR) {
                Assert.fail();
            } else if(resource.status == Resource.Status.SUCCESS) {
                assert resource.data == null;
            }
        });
    }
}
