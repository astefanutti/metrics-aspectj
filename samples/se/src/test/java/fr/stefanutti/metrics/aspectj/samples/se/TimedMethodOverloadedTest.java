/**
 * Copyright (C) 2013 Antonin Stefanutti (antonin.stefanutti@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.stefanutti.metrics.aspectj.samples.se;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import fr.stefanutti.metrics.aspectj.samples.se.util.MetricsUtil;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class TimedMethodOverloadedTest {

    private final static String REGISTRY_NAME = "overloadedTimerRegistry";

    private final static String[] TIMER_NAMES = {"overloadedTimedMethodWithNoArguments", "overloadedTimedMethodWithStringArgument", "overloadedTimedMethodWithListOfStringArgument", "overloadedTimedMethodWithObjectArgument"};

    private TimedMethodOverloaded instance;

    private Set<String> absoluteMetricNames() {
        return MetricsUtil.absoluteMetricNameSet(TimedMethodOverloaded.class, TIMER_NAMES);
    }

    @Before
    public void createTimedInstance() {
        instance = new TimedMethodOverloaded();
    }

    @After
    public void clearSharedMetricRegistries() {
        SharedMetricRegistries.clear();
    }

    @Test
    public void overloadedTimedMethodNotCalledYet() {
        assertThat(SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);
        assertThat(registry.getTimers().keySet(), is(equalTo(absoluteMetricNames())));

        // Make sure that all the timers haven't been called yet
        assertThat(registry.getTimers().values(), everyItem(Matchers.<Timer>hasProperty("count", equalTo(0L))));
    }

    @Test
    public void callOverloadedTimedMethodOnce() {
        assertThat(SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);

        // Call the timed methods and assert they've all been timed once
        instance.overloadedTimedMethod();
        instance.overloadedTimedMethod("string");
        instance.overloadedTimedMethod(new Object());
        instance.overloadedTimedMethod(Arrays.asList("string1", "string2"));
        assertThat(registry.getTimers().values(), everyItem(Matchers.<Timer>hasProperty("count", equalTo(1L))));
    }
}
