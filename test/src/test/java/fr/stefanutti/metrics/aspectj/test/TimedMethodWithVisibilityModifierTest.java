package fr.stefanutti.metrics.aspectj.test;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import com.codahale.metrics.annotation.Timed;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import static org.fest.reflect.core.Reflection.method;

public class TimedMethodWithVisibilityModifierTest {

    private TimedMethodWithVisibilityModifier instance;

    @Before
    public void createAtMetricsInstance() {
        instance = new TimedMethodWithVisibilityModifier();
    }

    @After
    public void clearSharedMetricRegistries() {
        SharedMetricRegistries.clear();
    }

    @Test
    public void assertFourTimersWithZeroCount() {
        assertThat(SharedMetricRegistries.names(), hasItem("visibilityTimerRegistry"));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate("visibilityTimerRegistry");
        assertThat(registry.getTimers().keySet(), containsInAnyOrder("publicTimedMethod", "packagePrivateTimedMethod", "protectedTimedMethod", "privateTimedMethod"));

        // Make sure that all the timers haven't been called yet
        assertThat(registry.getTimers().values(), everyItem(Matchers.<Timer>hasProperty("count", equalTo(0L))));
    }

    @Test
    public void assertTimersWithOneCountAfterMethodInvocations() {
        MetricRegistry registry = SharedMetricRegistries.getOrCreate("visibilityTimerRegistry");

        // Call the timed methods and assert they've all been timed once
        instance.publicTimedMethod();
        instance.protectedTimedMethod();
        instance.packagePrivateTimedMethod();
        method("privateTimedMethod").in(instance).invoke();
        assertThat(registry.getTimers().values(), everyItem(Matchers.<Timer>hasProperty("count", equalTo(1L))));
    }
}
