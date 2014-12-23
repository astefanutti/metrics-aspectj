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
import io.astefanutti.metrics.aspectj.Metrics;
import io.astefanutti.metrics.aspectj.Metrics;

@Metrics(registry = "visibilityTimerStaticRegistry")
public class TimedStaticMethodWithVisibilityModifiers {

    private TimedStaticMethodWithVisibilityModifiers(){}

    @Timed(name = "publicTimedStaticMethod")
    public static void publicTimedStaticMethod() {
    }

    @Timed(name = "packagePrivateTimedStaticMethod")
    static void packagePrivateTimedStaticMethod() {
    }

    @Timed(name = "protectedTimedStaticMethod")
    protected static void protectedTimedStaticMethod() {
    }

    @Timed(name = "privateTimedStaticMethod")
    private static void privateTimedStaticMethod() {
    }
}
