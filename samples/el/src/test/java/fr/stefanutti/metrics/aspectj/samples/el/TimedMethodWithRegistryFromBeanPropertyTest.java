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
import com.codahale.metrics.Timer;
import fr.stefanutti.metrics.aspectj.samples.el.TimedMethodWithRegistryFromBeanProperty;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class TimedMethodWithRegistryFromBeanPropertyTest {

    private final static String TIMER_NAME = TimedMethodWithRegistryFromBeanProperty.class.getName() + "." + "singleTimedMethod";

    private TimedMethodWithRegistryFromBeanProperty instance;

    @Before
    public void createTimedInstance() {
        MetricRegistry registry = new MetricRegistry();
        instance = new TimedMethodWithRegistryFromBeanProperty(registry);
    }

    @Test
    public void timedMethodNotCalledYet() {
        MetricRegistry registry = instance.getRegistry();
        assertThat(registry.getTimers(), hasKey(TIMER_NAME));
        Timer timer = registry.getTimers().get(TIMER_NAME);

        // Make sure that the timer hasn't been called yet
        assertThat(timer.getCount(), is(equalTo(0L)));
    }

    @Test
    public void callTimedMethodOnce() {
        MetricRegistry registry = instance.getRegistry();
        assertThat(registry.getTimers(), hasKey(TIMER_NAME));
        Timer timer = registry.getTimers().get(TIMER_NAME);

        // Call the timed method and assert it's been timed
        instance.singleTimedMethod();
        assertThat(timer.getCount(), is(equalTo(1L)));
    }
}
