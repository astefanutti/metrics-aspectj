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

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class GaugeStaticMethodWithRegistryFromStringTest {

    private final static String REGISTRY_NAME = "singleGaugeStaticRegistry";

    private final static String GAUGE_NAME = MetricRegistry.name(GaugeStaticMethodWithRegistryFromString.class, "singleGaugeStaticMethod");

    @Test
    public void callStaticGaugeAfterSetterCall() {
        long value = Math.round(Math.random() * Long.MAX_VALUE);
        // Call the setter static method and assert the gauge is up-to-date
        GaugeStaticMethodWithRegistryFromString.setSingleGauge(value);

        assertThat("Shared metric registry is not created", SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);
        assertThat("Gauge is not registered correctly", registry.getGauges(), hasKey(GAUGE_NAME));
        @SuppressWarnings("unchecked")
        Gauge<Long> gauge = registry.getGauges().get(GAUGE_NAME);
        assertThat("Gauge value is incorrect", gauge.getValue(), is(equalTo(value)));
    }
}
