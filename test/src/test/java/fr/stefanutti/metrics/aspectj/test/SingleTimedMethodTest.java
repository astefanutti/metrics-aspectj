package fr.stefanutti.metrics.aspectj.test;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class SingleTimedMethodTest {

    @Test
    public void timedPublicMethodWithStaticSharedRegistry() {
        SingleTimedMethod instance = new SingleTimedMethod();
        assertThat(SharedMetricRegistries.names(), hasItem("singleTimerRegistry"));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate("singleTimerRegistry");
        assertThat(registry.getTimers(), hasKey("singleTimedMethod"));
        Timer timer = registry.getTimers().get("singleTimedMethod");

        // Make sure that the timer hasn't been called yet
        assertThat(timer.getCount(), is(equalTo(0L)));

        // Call the timed method and assert it's been timed
        instance.singleTimedMethod();
        assertThat(timer.getCount(), is(equalTo(1L)));
    }
}
