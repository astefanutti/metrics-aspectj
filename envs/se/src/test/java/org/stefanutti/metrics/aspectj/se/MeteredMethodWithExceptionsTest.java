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
package org.stefanutti.metrics.aspectj.se;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import org.stefanutti.metrics.aspectj.se.util.MetricsUtil;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class MeteredMethodWithExceptionsTest {

    private final static String REGISTRY_NAME = "exceptionMeterRegistry";

    private final static String[] METER_NAMES = {"illegalArgumentExceptionMeteredMethod", "exceptionMeteredMethod"};

    private MeteredMethodWithExceptions instance;

    private Set<String> absoluteMetricNames() {
        return MetricsUtil.absoluteMetricNames(MeteredMethodWithExceptions.class, METER_NAMES);
    }

    private String absoluteMetricName(int index) {
        return MetricsUtil.absoluteMetricName(MeteredMethodWithExceptions.class, METER_NAMES[index]);
    }

    @Before
    public void createExceptionMeteredInstance() {
        instance = new MeteredMethodWithExceptions();
    }

    @After
    public void clearSharedMetricRegistries() {
        SharedMetricRegistries.clear();
    }

    @Test
    public void exceptionMeteredMethodsNotCalledYet() {
        assertThat("Shared metric registry is not created", SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);
        assertThat("Meters are not registered correctly", registry.getMeters().keySet(), is(equalTo(absoluteMetricNames())));

        // Make sure that all the meters haven't been called yet
        assertThat("Meter counts are incorrect", registry.getMeters().values(), everyItem(Matchers.<Meter>hasProperty("count", equalTo(0L))));
    }

    @Test
    public void callExceptionMeteredMethodsOnceWithoutThrowing() {
        assertThat("Shared metric registry is not created", SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);
        assertThat("Meters are not registered correctly", registry.getMeters().keySet(), is(equalTo(absoluteMetricNames())));

        Runnable runnableThatDoesNoThrowExceptions = new Runnable() {
            @Override
            public void run() {
            }
        };

        // Call the metered methods and assert they haven't been marked
        instance.illegalArgumentExceptionMeteredMethod(runnableThatDoesNoThrowExceptions);
        instance.exceptionMeteredMethod(runnableThatDoesNoThrowExceptions);
        assertThat("Meter counts are incorrect", registry.getMeters().values(), everyItem(Matchers.<Meter>hasProperty("count", equalTo(0L))));
    }

    @Test
    public void callExceptionMeteredMethodOnceWithThrowingExpectedException() {
        assertThat("Shared metric registry is not created", SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);
        assertThat("Meters are not registered correctly", registry.getMeters().keySet(), is(equalTo(absoluteMetricNames())));

        final RuntimeException exception = new IllegalArgumentException("message");
        Runnable runnableThatThrowsIllegalArgumentException = new Runnable() {
            @Override
            public void run() {
                throw exception;
            }
        };

        // Call the metered method and assert it's been marked and that the original exception has been rethrown
        try {
            instance.illegalArgumentExceptionMeteredMethod(runnableThatThrowsIllegalArgumentException);
        } catch (RuntimeException cause) {
            assertThat("Meter count is incorrect", registry.getMeters().get(absoluteMetricName(0)).getCount(), is(equalTo(1L)));
            assertThat("Meter count is incorrect", registry.getMeters().get(absoluteMetricName(1)).getCount(), is(equalTo(0L)));
            assertSame("Exception thrown is incorrect", cause, exception);
            return;
        }

        fail("No exception has been re-thrown!");
    }

    @Test
    public void callExceptionMeteredMethodOnceWithThrowingNonExpectedException() {
        assertThat("Shared metric registry is not created", SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);
        assertThat("Meters are not registered correctly", registry.getMeters().keySet(), is(equalTo(absoluteMetricNames())));

        final RuntimeException exception = new IllegalStateException("message");
        Runnable runnableThatThrowsIllegalStateException = new Runnable() {
            @Override
            public void run() {
                throw exception;
            }
        };

        // Call the metered method and assert it hasn't been marked and that the original exception has been rethrown
        try {
            instance.illegalArgumentExceptionMeteredMethod(runnableThatThrowsIllegalStateException);
        } catch (RuntimeException cause) {
            assertThat("Meter counts are incorrect", registry.getMeters().values(), everyItem(Matchers.<Meter>hasProperty("count", equalTo(0L))));
            assertSame("Exception thrown is incorrect", cause, exception);
            return;
        }

        fail("No exception has been re-thrown!");
    }

    @Test
    public void callExceptionMeteredMethodOnceWithThrowingInstanceOfExpectedException() {
        assertThat("Shared metric registry is not created", SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);
        assertThat("Meters are not registered correctly", registry.getMeters().keySet(), is(equalTo(absoluteMetricNames())));

        final RuntimeException exception = new IllegalStateException("message");
        Runnable runnableThatThrowsIllegalStateException = new Runnable() {
            @Override
            public void run() {
                throw exception;
            }
        };

        // Call the metered method and assert it's been marked and that the original exception has been rethrown
        try {
            instance.exceptionMeteredMethod(runnableThatThrowsIllegalStateException);
        } catch (RuntimeException cause) {
            assertThat("Meter count is incorrect", registry.getMeters().get(absoluteMetricName(0)).getCount(), is(equalTo(0L)));
            assertThat("Meter count is incorrect", registry.getMeters().get(absoluteMetricName(1)).getCount(), is(equalTo(1L)));
            assertSame("Exception thrown is incorrect", cause, exception);
            return;
        }

        fail("No exception has been re-thrown!");
    }
}
