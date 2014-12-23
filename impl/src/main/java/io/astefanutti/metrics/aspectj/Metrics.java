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
package io.astefanutti.metrics.aspectj;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that activates Metrics AspectJ weaving.
 * <p/>
 * Metrics AspectJ will scan all the declared methods of the annotated class that are annotated with
 * some Metrics annotations, then create and register the corresponding Metric instances and finally weave
 * its aspects around these methods, so that at runtime, these Metric instances get called according
 * to the Metrics annotations specification.
 *
 * <p/>
 * For example, given the following class declaration:
 * <pre><code>
 * {@literal @}Metrics(registry = "${this.registry}")
 * public class TimedMethodWithRegistryFromProperty {
 *
 *     private final MetricRegistry registry;
 *
 *     public TimedMethodWithRegistryFromProperty(MetricRegistry registry) {
 *         this.registry = registry;
 *     }
 *
 *     public MetricRegistry getRegistry() {
 *         return registry;
 *     }
 *
 *     {@literal @}Timed(name = "timerName")
 *     public void timedMethod() {
 *     }
 * }
 * </code></pre>
 * A {@code Timer} instance will be registered in the provided {@code MetricRegistry}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Metrics {

    /**
     * The expression that is used to resolve the {@code MetricRegistry} to be used to register metrics into.
     *  <p/>
     * Its value can either be:
     * <ul>
     *     <li>a string literal that identifies a {@code MetricRegistry} accessible
     *     from the {@code SharedMetricRegistries} class,</li>
     *     <li>or a valid EL expression that evaluates
     *     to the registry name or the {@code MetricRegistry} instance.</li>
     * </ul>
     * <p/>
     * The resultant {@code MetricRegistry} is used to register the {@code Metric} instances into.
     */
    String registry() default "metrics-registry";

}