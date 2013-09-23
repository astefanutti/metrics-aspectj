package fr.stefanutti.metrics.aspectj.test;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.fest.reflect.core.Reflection.method;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class TimedMethodWithMultipleSignaturesTest {

    private TimedMethodWithMultipleSignatures instance;

    @Before
    public void createAtMetricsInstance() {
        instance = new TimedMethodWithMultipleSignatures();
    }

    @After
    public void clearSharedMetricRegistries() {
        SharedMetricRegistries.clear();
    }

    @Test
    public void assertTimersWithZeroCount() {
        assertThat(SharedMetricRegistries.names(), hasItem("signatureTimerRegistry"));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate("signatureTimerRegistry");
        assertThat(registry.getTimers().keySet(), containsInAnyOrder("signatureTimedMethodWithNoArguments", "signatureTimedMethodWithStringArgument", "signatureTimedMethodWithListOfStringArgument", "signatureTimedMethodWithObjectArgument"));

        // Make sure that all the timers haven't been called yet
        assertThat(registry.getTimers().values(), everyItem(Matchers.<Timer>hasProperty("count", equalTo(0L))));
    }

    @Test
    public void assertTimersWithOneCountAfterMethodInvocations() {
        MetricRegistry registry = SharedMetricRegistries.getOrCreate("signatureTimerRegistry");

        // Call the timed methods and assert they've all been timed once
        instance.signatureTimedMethod();
        instance.signatureTimedMethod("string");
        instance.signatureTimedMethod(new Object());
        instance.signatureTimedMethod(Arrays.asList("string", "string"));
        assertThat(registry.getTimers().values(), everyItem(Matchers.<Timer>hasProperty("count", equalTo(1L))));
    }
}
