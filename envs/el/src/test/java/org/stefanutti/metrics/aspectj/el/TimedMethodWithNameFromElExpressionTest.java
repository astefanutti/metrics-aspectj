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
package org.stefanutti.metrics.aspectj.el;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

public class TimedMethodWithNameFromElExpressionTest {

    private final static String REGISTRY_NAME = "timerWithElRegistry";

    private String timerName;

    private TimedMethodWithNameFromElExpression instance;

    @Before
    public void createTimedInstance() {
        long id = Math.round(Math.random() * Long.MAX_VALUE);
        instance = new TimedMethodWithNameFromElExpression(id);
        timerName = MetricRegistry.name(TimedMethodWithNameFromElExpression.class, "timer " + id);
    }

    @After
    public void clearSharedMetricRegistries() {
        SharedMetricRegistries.clear();
    }

    @Test
    public void timedMethodNotCalledYet() {
        assertThat("Shared metric registry is not created", SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);
        assertThat("Timer is not registered correctly", registry.getTimers(), hasKey(timerName));
        Timer timer = registry.getTimers().get(timerName);

        // Make sure that the timer hasn't been called yet
        assertThat("Timer count is incorrect", timer.getCount(), is(equalTo(0L)));
    }

    @Test
    public void callExpressionTimedMethodOnce() {
        assertThat("Shared metric registry is not created", SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);
        assertThat("Timer is not registered correctly", registry.getTimers(), hasKey(timerName));
        Timer timer = registry.getTimers().get(timerName);

        // Call the timed methods and assert they've been timed
        instance.expressionTimedMethod();
        instance.compositeExpressionTimedMethod();
        instance.lambdaExpressionTimedMethod();
        assertThat("Timer count is incorrect", timer.getCount(), is(equalTo(3L)));
    }
}
