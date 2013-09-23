package fr.stefanutti.metrics.aspectj.test;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class TimedMethodOverloadedTest {

    private TimedMethodOverloaded instance;

    @Before
    public void createAtMetricsInstance() {
        instance = new TimedMethodOverloaded();
    }

    @After
    public void clearSharedMetricRegistries() {
        SharedMetricRegistries.clear();
    }

    @Test
    public void overloadedTimedMethodNotCalledYet() {
        assertThat(SharedMetricRegistries.names(), hasItem("overloadedTimerRegistry"));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate("overloadedTimerRegistry");
        assertThat(registry.getTimers().keySet(), containsInAnyOrder("overloadedTimedMethodWithNoArguments", "overloadedTimedMethodWithStringArgument", "overloadedTimedMethodWithListOfStringArgument", "overloadedTimedMethodWithObjectArgument"));

        // Make sure that all the timers haven't been called yet
        assertThat(registry.getTimers().values(), everyItem(Matchers.<Timer>hasProperty("count", equalTo(0L))));
    }

    @Test
    public void callOverloadedTimedMethodOnce() {
        MetricRegistry registry = SharedMetricRegistries.getOrCreate("overloadedTimerRegistry");

        // Call the timed methods and assert they've all been timed once
        instance.overloadedTimedMethod();
        instance.overloadedTimedMethod("string");
        instance.overloadedTimedMethod(new Object());
        instance.overloadedTimedMethod(Arrays.asList("string", "string"));
        assertThat(registry.getTimers().values(), everyItem(Matchers.<Timer>hasProperty("count", equalTo(1L))));
    }
}
