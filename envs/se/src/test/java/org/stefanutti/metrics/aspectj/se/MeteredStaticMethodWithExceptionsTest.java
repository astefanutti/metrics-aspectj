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

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import org.stefanutti.metrics.aspectj.se.util.MetricsUtil;
import org.junit.Test;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class MeteredStaticMethodWithExceptionsTest {

    private final static String REGISTRY_NAME = "exceptionMeterStaticRegistry";

    private final static String[] METER_NAMES = {"illegalArgumentExceptionMeteredStaticMethod", "exceptionMeteredStaticMethod"};

    private final static AtomicLong[] METER_COUNTS = {new AtomicLong(), new AtomicLong()};

    private static Set<String> absoluteMetricNames() {
        return MetricsUtil.absoluteMetricNameSet(MeteredStaticMethodWithExceptions.class, METER_NAMES);
    }

    private static String absoluteMetricName(int index) {
        return MetricsUtil.absoluteMetricName(MeteredStaticMethodWithExceptions.class, METER_NAMES[index]);
    }

    @Test
    public void callExceptionMeteredStaticMethodsOnceWithoutThrowing() {
        Runnable runnableThatDoesNoThrowExceptions = new Runnable() {
            @Override
            public void run() {
            }
        };

        // Call the metered methods and assert they haven't been marked
        MeteredStaticMethodWithExceptions.illegalArgumentExceptionMeteredStaticMethod(runnableThatDoesNoThrowExceptions);
        MeteredStaticMethodWithExceptions.exceptionMeteredStaticMethod(runnableThatDoesNoThrowExceptions);

        assertThat("Shared metric registry is not created", SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);
        assertThat("Meters are not registered correctly", registry.getMeters().keySet(), is(equalTo(absoluteMetricNames())));
        assertThat("Meter count is incorrect", registry.getMeters().get(absoluteMetricName(0)).getCount(), is(equalTo(METER_COUNTS[0].get())));
        assertThat("Meter count is incorrect", registry.getMeters().get(absoluteMetricName(1)).getCount(), is(equalTo(METER_COUNTS[1].get())));
    }

    @Test
    public void callExceptionMeteredStaticMethodOnceWithThrowingExpectedException() {
        final RuntimeException exception = new IllegalArgumentException("message");
        Runnable runnableThatThrowsIllegalArgumentException = new Runnable() {
            @Override
            public void run() {
                throw exception;
            }
        };

        // Call the metered method and assert it's been marked and that the original exception has been rethrown
        try {
            MeteredStaticMethodWithExceptions.illegalArgumentExceptionMeteredStaticMethod(runnableThatThrowsIllegalArgumentException);
        } catch (RuntimeException cause) {
            assertThat("Shared metric registry is not created", SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
            MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);
            assertThat("Meters are not registered correctly", registry.getMeters().keySet(), is(equalTo(absoluteMetricNames())));
            assertThat("Meter count is incorrect", registry.getMeters().get(absoluteMetricName(0)).getCount(), is(equalTo(METER_COUNTS[0].incrementAndGet())));
            assertThat("Meter count is incorrect", registry.getMeters().get(absoluteMetricName(1)).getCount(), is(equalTo(METER_COUNTS[1].get())));
            assertSame("Exception thrown is incorrect", cause, exception);
            return;
        }

        fail("No exception has been re-thrown!");
    }

    @Test
    public void callExceptionMeteredStaticMethodOnceWithThrowingNonExpectedException() {
        final RuntimeException exception = new IllegalStateException("message");
        Runnable runnableThatThrowsIllegalStateException = new Runnable() {
            @Override
            public void run() {
                throw exception;
            }
        };

        // Call the metered method and assert it hasn't been marked and that the original exception has been rethrown
        try {
            MeteredStaticMethodWithExceptions.illegalArgumentExceptionMeteredStaticMethod(runnableThatThrowsIllegalStateException);
        } catch (RuntimeException cause) {
            assertThat("Shared metric registry is not created", SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
            MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);
            assertThat("Meters are not registered correctly", registry.getMeters().keySet(), is(equalTo(absoluteMetricNames())));
            assertThat("Meter count is incorrect", registry.getMeters().get(absoluteMetricName(0)).getCount(), is(equalTo(METER_COUNTS[0].get())));
            assertThat("Meter count is incorrect", registry.getMeters().get(absoluteMetricName(1)).getCount(), is(equalTo(METER_COUNTS[1].get())));
            assertSame("Exception thrown is incorrect", cause, exception);
            return;
        }

        fail("No exception has been re-thrown!");
    }

    @Test
    public void callExceptionMeteredStaticMethodOnceWithThrowingInstanceOfExpectedException() {
        final RuntimeException exception = new IllegalStateException("message");
        Runnable runnableThatThrowsIllegalStateException = new Runnable() {
            @Override
            public void run() {
                throw exception;
            }
        };

        // Call the metered method and assert it's been marked and that the original exception has been rethrown
        try {
            MeteredStaticMethodWithExceptions.exceptionMeteredStaticMethod(runnableThatThrowsIllegalStateException);
        } catch (RuntimeException cause) {
            assertThat("Shared metric registry is not created", SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
            MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);
            assertThat("Meters are not registered correctly", registry.getMeters().keySet(), is(equalTo(absoluteMetricNames())));
            assertThat("Meter count is incorrect", registry.getMeters().get(absoluteMetricName(0)).getCount(), is(equalTo(METER_COUNTS[0].get())));
            assertThat("Meter count is incorrect", registry.getMeters().get(absoluteMetricName(1)).getCount(), is(equalTo(METER_COUNTS[1].incrementAndGet())));
            assertSame("Exception thrown is incorrect", cause, exception);
            return;
        }

        fail("No exception has been re-thrown!");
    }
}
