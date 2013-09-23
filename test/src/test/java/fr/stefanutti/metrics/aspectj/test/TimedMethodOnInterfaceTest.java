package fr.stefanutti.metrics.aspectj.test;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TimedMethodOnInterfaceTest {

    private TimedMethodOnInterface instance;

    @Before
    public void createAtMetricsInstance() {
        instance = new TimedMethodOnInterfaceImpl();
    }

    @After
    public void clearSharedMetricRegistries() {
        SharedMetricRegistries.clear();
    }

    @Test
    public void timedMethodOnInterfaceNotCalledYet() {
        assertThat(SharedMetricRegistries.names(), hasItem("interfaceTimerRegistry"));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate("interfaceTimerRegistry");
        assertThat(registry.getTimers(), hasKey("interfaceTimedMethod"));
        Timer timer = registry.getTimers().get("interfaceTimedMethod");

        // Make sure that the timer hasn't been called yet
        assertThat(timer.getCount(), is(equalTo(0L)));
    }

    @Test
    public void callTimedMethodOnInterfaceOnce() {
        assertThat(SharedMetricRegistries.names(), hasItem("interfaceTimerRegistry"));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate("interfaceTimerRegistry");
        assertThat(registry.getTimers(), hasKey("interfaceTimedMethod"));
        Timer timer = registry.getTimers().get("interfaceTimedMethod");

        // Call the timed method and assert it's been timed
        instance.interfaceTimedMethod();
        assertThat(timer.getCount(), is(equalTo(1L)));
        System.out.println(timer.getSnapshot().getMin());

    }
}
