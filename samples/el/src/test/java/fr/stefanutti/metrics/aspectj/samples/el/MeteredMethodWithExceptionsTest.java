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
package fr.stefanutti.metrics.aspectj.samples.el;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import fr.stefanutti.metrics.aspectj.samples.el.util.MetricsUtil;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class MeteredMethodWithExceptionsTest {

    private final static String REGISTRY_NAME = "exceptionMeterRegistry";

    private final static String[] METER_NAMES = {"illegalArgumentExceptionMeteredMethod", "exceptionMeteredMethod"};

    private MeteredMethodWithExceptions instance;

    private Set<String> absoluteMetricNames() {
        return MetricsUtil.absoluteMetricNameSet(MeteredMethodWithExceptions.class, METER_NAMES);
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
        assertThat(SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);
        assertThat(registry.getMeters().keySet(), is(equalTo(absoluteMetricNames())));

        // Make sure that all the meters haven't been called yet
        assertThat(registry.getMeters().values(), everyItem(Matchers.<Meter>hasProperty("count", equalTo(0L))));
    }

    @Test
    public void callExceptionMeteredMethodsOnceWithoutThrowing() throws Exception {
        assertThat(SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);

        Runnable runnableThatDoesNoThrowExceptions = new Runnable() {
            @Override
            public void run() {
            }
        };

        // Call the metered methods and assert they haven't been marked
        instance.illegalArgumentExceptionMeteredMethod(runnableThatDoesNoThrowExceptions);
        instance.exceptionMeteredMethod(runnableThatDoesNoThrowExceptions);
        assertThat(registry.getMeters().values(), everyItem(Matchers.<Meter>hasProperty("count", equalTo(0L))));
    }

    @Test
    public void callExceptionMeteredMethodOnceWithThrowingExpectedException() {
        assertThat(SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);

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
            assertThat(registry.getMeters().get(absoluteMetricName(0)).getCount(), is(equalTo(1L)));
            assertThat(registry.getMeters().get(absoluteMetricName(1)).getCount(), is(equalTo(0L)));
            assertSame(cause, exception);
            return;
        }

        fail("No exception has been re-thrown!");
    }

    @Test
    public void callExceptionMeteredMethodOnceWithThrowingNonExpectedException() {
        assertThat(SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);

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
            assertThat(registry.getMeters().get(absoluteMetricName(0)).getCount(), is(equalTo(0L)));
            assertThat(registry.getMeters().get(absoluteMetricName(1)).getCount(), is(equalTo(0L)));
            assertSame(cause, exception);
            return;
        }

        fail("No exception has been re-thrown!");
    }

    @Test
    public void callExceptionMeteredMethodOnceWithThrowingInstanceOfExpectedException() {
        assertThat(SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);

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
            assertThat(registry.getMeters().get(absoluteMetricName(0)).getCount(), is(equalTo(0L)));
            assertThat(registry.getMeters().get(absoluteMetricName(1)).getCount(), is(equalTo(1L)));
            assertSame(cause, exception);
            return;
        }

        fail("No exception has been re-thrown!");
    }
}
