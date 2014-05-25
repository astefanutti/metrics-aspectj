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

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import org.stefanutti.metrics.aspectj.Metrics;

@Metrics(registry = "defaultNameRegistry")
public class MetricMethodWithDefaultNames {

    @Timed
    public void defaultNameTimedMethod() {
    }

    @Metered
    public void defaultNameMeteredMethod() {
    }

    @ExceptionMetered
    public void defaultNameExceptionMeteredMethod() {
    }

    @Timed(absolute = true)
    public void absoluteDefaultNameTimedMethod() {
    }

    @Metered(absolute = true)
    public void absoluteDefaultNameMeteredMethod() {
    }

    @ExceptionMetered(absolute = true)
    public void absoluteDefaultNameExceptionMeteredMethod() {
    }
}
