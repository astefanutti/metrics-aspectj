/**
 * Copyright © 2013 Antonin Stefanutti (antonin.stefanutti@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.astefanutti.metrics.aspectj.el;

import com.codahale.metrics.MetricRegistry;
import org.junit.Before;
import org.junit.Test;
import io.astefanutti.metrics.aspectj.se.util.MetricsUtil;

import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MultipleMetricsMethodWithRegistryFromBeanPropertyTest {

    private final static String[] METRIC_NAMES = {"exception", "gauge", "meter", "timer"};

    private Set<String> absoluteMetricNames() {
        return MetricsUtil.absoluteMetricNames(MultipleMetricsMethodWithRegistryFromBeanProperty.class, METRIC_NAMES);
    }

    private String absoluteMetricName(String name) {
        return MetricsUtil.absoluteMetricName(MultipleMetricsMethodWithRegistryFromBeanProperty.class, name);
    }

    private MultipleMetricsMethodWithRegistryFromBeanProperty instance;

    @Before
    public void createTimedInstance() {
        MetricRegistry registry = new MetricRegistry();
        instance = new MultipleMetricsMethodWithRegistryFromBeanProperty(registry);
    }

    @Test
    public void metricsMethodNotCalledYet() {
        MetricRegistry registry = instance.getRegistry();
        assertThat("Metrics are not registered correctly", registry.getMetrics().keySet(), is(equalTo(absoluteMetricNames())));

        // Make sure that the metrics haven't been called yet
        assertThat("Meter count is incorrect", registry.getMeters().get(absoluteMetricName("exception")).getCount(), is(equalTo(0L)));
        assertThat("Meter count is incorrect", registry.getMeters().get(absoluteMetricName("meter")).getCount(), is(equalTo(0L)));
        assertThat("Timer count is incorrect", registry.getTimers().get(absoluteMetricName("timer")).getCount(), is(equalTo(0L)));
    }

    @Test
    public void callMetricsMethodOnce() {
        MetricRegistry registry = instance.getRegistry();
        assertThat("Metrics are not registered correctly", registry.getMetrics().keySet(), is(equalTo(absoluteMetricNames())));

        // Call the monitored method and assert it's been instrumented
        instance.metricsMethod();

        // Make sure that the metrics have been called
        assertThat("Meter count is incorrect", registry.getMeters().get(absoluteMetricName("exception")).getCount(), is(equalTo(0L)));
        assertThat("Meter count is incorrect", registry.getMeters().get(absoluteMetricName("meter")).getCount(), is(equalTo(1L)));
        assertThat("Timer count is incorrect", registry.getTimers().get(absoluteMetricName("timer")).getCount(), is(equalTo(1L)));
        assertThat("Gauge value is incorrect", registry.getGauges().get(absoluteMetricName("gauge")).getValue(), hasToString((equalTo("value"))));
    }
}
