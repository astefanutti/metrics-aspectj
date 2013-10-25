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

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class TimedStaticMethodWithRegistryFromStaticPropertyTest {

    private final static String TIMER_NAME = MetricRegistry.name(TimedStaticMethodWithRegistryFromStaticProperty.class, "singleTimedStaticMethod");

    @Test
    public void callTimedStaticMethodOnce() {
        // Call the timed static method and assert it's been timed once
        TimedStaticMethodWithRegistryFromStaticProperty.singleTimedStaticMethod();

        MetricRegistry registry = TimedStaticMethodWithRegistryFromStaticProperty.REGISTRY;

        assertThat("Timer is not registered correctly", registry.getTimers(), hasKey(TIMER_NAME));
        Timer timer = registry.getTimers().get(TIMER_NAME);

        assertThat("Timer count is incorrect", timer.getCount(), is(equalTo(1L)));
    }
}
