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
package fr.stefanutti.metrics.aspectj.samples;

import com.codahale.metrics.annotation.Timed;
import fr.stefanutti.metrics.aspectj.Metrics;

import java.util.List;

@Metrics(registry = "'overloadedTimerRegistry'")
public class TimedMethodOverloaded {

    @Timed(name = "'overloadedTimedMethodWithNoArguments'")
    public void overloadedTimedMethod() {
    }

    @Timed(name = "'overloadedTimedMethodWithStringArgument'")
    public void overloadedTimedMethod(String string) {
    }

    @Timed(name = "'overloadedTimedMethodWithListOfStringArgument'")
    public void overloadedTimedMethod(List<String> strings) {
    }

    @Timed(name = "'overloadedTimedMethodWithObjectArgument'")
    public void overloadedTimedMethod(Object object) {
    }
}
