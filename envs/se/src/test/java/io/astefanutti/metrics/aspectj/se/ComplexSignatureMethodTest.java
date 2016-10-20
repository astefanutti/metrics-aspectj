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
package io.astefanutti.metrics.aspectj.se;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ComplexSignatureMethodTest {

    private MetricRegistry registry = SharedMetricRegistries.getOrCreate("complexSignatureRegistry");
    private ComplexSignatureMethod instance = new ComplexSignatureMethod();

    @Before
    public  void before() {
        assertEquals("Not all metrics were registered", 6, registry.getMetrics().size());
    }

    @Test
    public void callMeteredStaticMethodOnce() {
        ComplexSignatureMethod.meteredStaticMethod(null, null);
    }

    @Test
    public void callTimedStaticMethodOnce() {
        ComplexSignatureMethod.timedStaticMethod(null, null);
    }

    @Test
    public void callExceptionMeteredStaticMethodOnce() {
        ComplexSignatureMethod.exceptionMeteredStaticMethod(null, null);
    }

    @Test
    public void callTimedMethodOnce() {
        instance.timedMethod(null, null);
    }

    @Test
    public void callMeteredMethodOnce() {
        instance.meteredMethod(null, null);
    }

    @Test
    public void callExceptionMeteredMethodOnce() {
        instance.exceptionMeteredMethod(null, null);
    }
}