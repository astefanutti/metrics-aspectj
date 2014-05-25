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
import org.stefanutti.metrics.aspectj.Metrics;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TimedMethodWithRegistryFromElExpressionTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void throwExceptionOnInitialization() {
        exception.expect(UnsupportedOperationException.class);
        String name = TimedMethodWithRegistryFromElExpression.class.getAnnotation(Metrics.class).registry();
        exception.expectMessage("Unsupported EL expression [" + name + "] evaluation as no EL implementation is available");

        MetricRegistry registry = new MetricRegistry();
        TimedMethodWithRegistryFromElExpression instance = new TimedMethodWithRegistryFromElExpression(registry);
    }
}
