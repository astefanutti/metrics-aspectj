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

import com.codahale.metrics.annotation.Timed;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TimedMethodWithNameFromElExpressionTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void throwExceptionOnInitialization() throws NoSuchMethodException {
        exception.expect(UnsupportedOperationException.class);
        String name = TimedMethodWithNameFromElExpression.class.getMethod("expressionTimedMethod").getAnnotation(Timed.class).name();
        exception.expectMessage("Unsupported EL expression [" + name + "] evaluation as no EL implementation is available");

        TimedMethodWithNameFromElExpression instance = new TimedMethodWithNameFromElExpression("timer");
    }
}
