package de.uniulm.loraparkapplication;

import android.app.Application;
import android.app.job.JobParameters;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ServiceTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import de.uniulm.loraparkapplication.engines.NotificationAction;
import de.uniulm.loraparkapplication.engines.RuleEngine;
import de.uniulm.loraparkapplication.engines.TestAssertAction;
import de.uniulm.loraparkapplication.models.Action;
import de.uniulm.loraparkapplication.models.CompleteRule;
import de.uniulm.loraparkapplication.models.Sensor;
import de.uniulm.loraparkapplication.repositories.RuleHandler;
import io.reactivex.rxjava3.core.Observable;

@RunWith(AndroidJUnit4.class)
public class BackgroundeJobServiceTest {
    final Context appContext;
    final Application application;

    public BackgroundeJobServiceTest() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        application = (Application) appContext.getApplicationContext();
    }

    @Rule
    public final ServiceTestRule serviceRule = new ServiceTestRule();

    @Test(timeout=5000)
    public void insertRuleToDatabase() throws InterruptedException, ExecutionException {
        RuleHandler ruleHandler = RuleHandler.getInstance(application);

        CompleteRule testCompleteRule = new CompleteRule();

        de.uniulm.loraparkapplication.models.Rule testRule = new de.uniulm.loraparkapplication.models.Rule();
        testRule.setId("BackgroundeJobServiceRule");
        testRule.setName("BackgroundeJobServiceRule");
        testRule.setIsActive(true);
        testRule.setCondition("{\">=\": [{\"sensor\": [\"energy\", \"000003\", \"energy\"]}, 1000]}");
        testRule.setWasTriggered(false);
        testCompleteRule.setRule(testRule);

        Sensor testSensor = new Sensor();
        testSensor.setSensorId("000003");
        //testSensor.setDomain("energy");
        //testSensor.setValue("energy");
        testSensor.setRuleId("BackgroundeJobServiceRule");
        //testSensor.setRuleSensorId("energySensor");
        testCompleteRule.addSensor(testSensor);

        Action testAction = new Action();
        testAction.setAction(NotificationAction.INSTANCE.key());
        testAction.setData("{\"title\": \"BackgroundeJobService\", \"text\": \"working :)\"}");
        testAction.setRuleId("BackgroundeJobServiceRule");
        testCompleteRule.addAction(testAction);

        CompletableFuture<Boolean> inserted = new CompletableFuture<>();

        Observable<String> insertCompleteRuleSave = ruleHandler.insertCompleteRuleSave(testCompleteRule);
        insertCompleteRuleSave.subscribe(item -> {
            inserted.complete(true);
        }, throwable -> {
            throw throwable;
        });

        inserted.get();
    }

    /*@Test
    public void testBackgroundeJobServiceRule() throws TimeoutException {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        serviceRule.startService(new Intent(context, BackgroundeJobService.class));
    }*/
}
