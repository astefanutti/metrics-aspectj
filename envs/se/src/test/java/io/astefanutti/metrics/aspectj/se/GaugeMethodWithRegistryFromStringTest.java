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
package io.astefanutti.metrics.aspectj.se;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class GaugeMethodWithRegistryFromStringTest {

    private final static String REGISTRY_NAME = "singleGaugeRegistry";

    private final static String GAUGE_NAME = MetricRegistry.name(GaugeMethodWithRegistryFromString.class, "singleGaugeMethod");

    private GaugeMethodWithRegistryFromString instance;

    @Before
    public void createGaugeInstance() {
        instance = new GaugeMethodWithRegistryFromString();
    }

    @After
    public void clearSharedMetricRegistries() {
        SharedMetricRegistries.clear();
    }

    @Test
    public void gaugeCalledWithDefaultValue() {
        assertThat("Shared metric registry is not created", SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);
        assertThat("Gauge is not registered correctly", registry.getGauges(), hasKey(GAUGE_NAME));
        @SuppressWarnings("unchecked")
        Gauge<Long> gauge = registry.getGauges().get(GAUGE_NAME);

        // Make sure that the gauge has the expected value
        assertThat("Gauge value is incorrect", gauge.getValue(), is(equalTo(0L)));
    }

    @Test
    public void callGaugeAfterSetterCall() {
        assertThat("Shared metric registry is not created", SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);
        assertThat("Gauge is not registered correctly", registry.getGauges(), hasKey(GAUGE_NAME));
        @SuppressWarnings("unchecked")
        Gauge<Long> gauge = registry.getGauges().get(GAUGE_NAME);

        // Call the setter method and assert the gauge is up-to-date
        long value = Math.round(Math.random() * Long.MAX_VALUE);
        instance.setSingleGauge(value);
        assertThat("Gauge value is incorrect", gauge.getValue(), is(equalTo(value)));
    }
}
