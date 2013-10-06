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

import com.codahale.metrics.annotation.ExceptionMetered;
import fr.stefanutti.metrics.aspectj.Metrics;
import fr.stefanutti.metrics.aspectj.Registry;

@Metrics @Registry("'exceptionMeterRegistry'")
public class MeteredMethodWithExceptions {

    @ExceptionMetered(name = "'illegalArgumentExceptionMeteredMethod'", cause = IllegalArgumentException.class)
    public void illegalArgumentExceptionMeteredMethod(Runnable runnable) {
        runnable.run();
    }

    @ExceptionMetered(name = "'exceptionMeteredMethod'", cause = Exception.class)
    public void exceptionMeteredMethod(Runnable runnable) {
        runnable.run();
    }
}
