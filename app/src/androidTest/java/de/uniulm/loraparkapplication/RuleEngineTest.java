package de.uniulm.loraparkapplication;

import android.app.Application;
import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import de.uniulm.loraparkapplication.engines.NotificationAction;
import de.uniulm.loraparkapplication.engines.RuleEngine;
import de.uniulm.loraparkapplication.engines.TTSAction;
import de.uniulm.loraparkapplication.engines.TestAssertAction;
import de.uniulm.loraparkapplication.models.Action;
import de.uniulm.loraparkapplication.models.CompleteRule;
import de.uniulm.loraparkapplication.models.Rule;
import de.uniulm.loraparkapplication.repositories.RuleHandler;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@RunWith(AndroidJUnit4.class)
public class RuleEngineTest {
    final Context appContext;
    final Application application;
    final RuleEngine ruleEngine;
    final Action assertAction;

    // TODO test sensor operation
    // TODO test geofence operation

    public RuleEngineTest() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        application = (Application) appContext.getApplicationContext();

        ruleEngine = RuleEngine.getInstance(application);
        ruleEngine.addOperation(TestAssertAction.INSTANCE);

        assertAction = new Action();
        assertAction.setAction(TestAssertAction.INSTANCE.key());
        assertAction.setData("");
    }

    @org.junit.Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    /* Action tests */
    @Test
    public void assertActionRule() {
        assert !TestAssertAction.INSTANCE.getTriggered();
        TestAssertAction.INSTANCE.trigger(appContext, new HashMap<>());
        assert TestAssertAction.INSTANCE.getTriggered();
        Boolean triggered = TestAssertAction.INSTANCE.getAndResetTriggered();
        assert triggered;
        assert !TestAssertAction.INSTANCE.getTriggered();
    }

    @Test
    public void notificationActionRule() {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "notificationActionRule");
        data.put("text", "default channel");

        NotificationAction.INSTANCE.trigger(appContext, data);

        data.put("channel", "TestChannel");
        data.put("text", "TestChannel");

        NotificationAction.INSTANCE.trigger(appContext, data);
    }

    @Test
    public void notificationActionRuleWidthAction() {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "notificationActionRuleWidthAction");
        data.put("text", "with action");

        data.put("buttons", Collections.singletonList(
                Map.of(
                        "title", "Fire",
                        "action", "notification",
                        "data", Map.of(
                                "title", "notificationActionRuleWidthAction",
                                "text", "working")
                )
        ));

        NotificationAction.INSTANCE.trigger(appContext, data);
    }

    @Test
    public void ttsActionRuleWidthAction() throws InterruptedException {
        Map<String, Object> data = new HashMap<>();
        data.put("lang", "en");
        data.put("text", "Hello");

        TTSAction.INSTANCE.trigger(appContext, data);
        Thread.sleep(3000); // otherwise TTS Service gets killed
    }

    /* Rules tests */

    @Test
    public void inactiveCompleteRule() {
        CompleteRule testCompleteRule = new CompleteRule();

        Rule testRule = new Rule();
        testRule.setIsActive(false);
        testRule.setCondition("true");
        testRule.setWasTriggered(false);
        testCompleteRule.setRule(testRule);

        assert ruleEngine.evaluateRule(testCompleteRule) == null;
    }

    @Test
    public void trueCompleteRule() {
        CompleteRule testCompleteRule = new CompleteRule();

        Rule testRule = new Rule();
        testRule.setIsActive(true);
        testRule.setCondition("true");
        testRule.setWasTriggered(false);
        testCompleteRule.setRule(testRule);

        // test with action
        testCompleteRule.addAction(assertAction);
        ruleEngine.evaluateRule(testCompleteRule);
        Boolean triggered = TestAssertAction.INSTANCE.getAndResetTriggered();
        assert triggered;
    }

    @Test
    public void falseCompleteRule() {
        CompleteRule testCompleteRule = new CompleteRule();

        Rule testRule = new Rule();
        testRule.setIsActive(true);
        testRule.setCondition("false");
        testRule.setWasTriggered(false);
        testCompleteRule.setRule(testRule);

        // test with action
        testCompleteRule.addAction(assertAction);
        ruleEngine.evaluateRule(testCompleteRule);
        Boolean triggered = TestAssertAction.INSTANCE.getAndResetTriggered();
        assert !triggered;
    }

    @Test
    public void failingCompleteRule() {
        CompleteRule testCompleteRule = new CompleteRule();

        Rule testRule = new Rule();
        testRule.setIsActive(true);
        testRule.setCondition("{adfdsfds}}");
        testRule.setWasTriggered(false);
        testCompleteRule.setRule(testRule);

        // test without action
        assert ruleEngine.evaluateRule(testCompleteRule) == null;

        // test with action
        testCompleteRule.addAction(assertAction);
        ruleEngine.evaluateRule(testCompleteRule);
        Boolean triggered = TestAssertAction.INSTANCE.getAndResetTriggered();
        assert !triggered;
    }

    @Test
    public void edgeCompleteRule() {
        CompleteRule testCompleteRule = new CompleteRule();

        Rule testRule = new Rule();
        testRule.setIsActive(true);
        testRule.setCondition("true");
        testRule.setWasTriggered(false);
        testCompleteRule.setRule(testRule);

        assert !testRule.getWasTriggered();
        assert ruleEngine.evaluateRule(testCompleteRule);
        assert testRule.getWasTriggered();

        assert !ruleEngine.evaluateRule(testCompleteRule);
        assert testRule.getWasTriggered();

        assert !ruleEngine.evaluateRule(testCompleteRule);
        assert testRule.getWasTriggered();

        testRule.setCondition("false");
        assert !ruleEngine.evaluateRule(testCompleteRule);
        assert !testRule.getWasTriggered();

        testRule.setCondition("true");

        assert ruleEngine.evaluateRule(testCompleteRule);
        assert testRule.getWasTriggered();
        assert !ruleEngine.evaluateRule(testCompleteRule);
        assert testRule.getWasTriggered();
        assert !ruleEngine.evaluateRule(testCompleteRule);
        assert testRule.getWasTriggered();
    }

    @Test
    public void notificationActionCompleteRule() {
        CompleteRule testCompleteRule = new CompleteRule();

        Rule testRule = new Rule();
        testRule.setIsActive(true);
        testRule.setCondition("true");
        testRule.setWasTriggered(false);
        testCompleteRule.setRule(testRule);

        Action testAction = new Action();
        testAction.setAction(NotificationAction.INSTANCE.key());
        testAction.setData("{\"title\": \"notificationActionCompleteRule\", \"text\": \"success\", \"channel\": \"notificationActionCompleteRule\"}");
        testCompleteRule.addAction(testAction);

        ruleEngine.evaluateRule(testCompleteRule);
    }

    /*@Test(timeout = 5000)
    public void rulesFromDatabase() throws InterruptedException, ExecutionException {
        RuleHandler ruleHandler = RuleHandler.getInstance(application);

        CompleteRule testCompleteRule = new CompleteRule();

        Rule testRule = new Rule();
        testRule.setId("testRule");
        testRule.setName("testRule");
        testRule.setIsActive(true);
        testRule.setCondition("true");
        testRule.setWasTriggered(false);
        testCompleteRule.setRule(testRule);

        Action assertAction = new Action();
        assertAction.setAction(TestAssertAction.INSTANCE.key());
        assertAction.setData("");
        assertAction.setRuleId("testRule");

        testCompleteRule.addAction(assertAction);

        CompletableFuture<Boolean> triggered = new CompletableFuture<>();

        Completable insertCompleteRuleSave = ruleHandler.insertCompleteRuleSave(testCompleteRule);
        insertCompleteRuleSave.subscribe(item -> {
            ruleEngine.evaluateRules().observeForever(completeRules -> {
                triggered.complete(TestAssertAction.INSTANCE.getAndResetTriggered());
            });
        }, throwable -> {
            throw throwable;
        });

        assert triggered.get();
    }*/
}
