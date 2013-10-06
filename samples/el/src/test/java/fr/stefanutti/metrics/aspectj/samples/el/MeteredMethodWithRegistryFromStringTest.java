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
import fr.stefanutti.metrics.aspectj.samples.el.MeteredMethodWithRegistryFromString;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class MeteredMethodWithRegistryFromStringTest {

    private final static String REGISTRY_NAME = "singleMeterRegistry";

    private final static String METER_NAME = MeteredMethodWithRegistryFromString.class.getName() + "." + "singleMeteredMethod";

    private MeteredMethodWithRegistryFromString instance;

    @Before
    public void createMeteredInstance() {
        instance = new MeteredMethodWithRegistryFromString();
    }

    @After
    public void clearSharedMetricRegistries() {
        SharedMetricRegistries.clear();
    }

    @Test
    public void meteredMethodNotCalledYet() {
        assertThat(SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);
        assertThat(registry.getMeters(), hasKey(METER_NAME));
        Meter meter = registry.getMeters().get(METER_NAME);

        // Make sure that the meter hasn't been called yet
        assertThat(meter.getCount(), is(equalTo(0L)));
    }

    @Test
    public void callMeteredMethodOnce() {
        assertThat(SharedMetricRegistries.names(), hasItem(REGISTRY_NAME));
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);
        assertThat(registry.getMeters(), hasKey(METER_NAME));
        Meter meter = registry.getMeters().get(METER_NAME);

        // Call the metered method and assert it's been marked
        instance.singleMeteredMethod();
        assertThat(meter.getCount(), is(equalTo(1L)));
    }
}
