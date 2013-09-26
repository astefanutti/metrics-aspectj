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
package fr.stefanutti.metrics.aspectj;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;

import javax.el.ELProcessor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final aspect MetricAspect {

    declare precedence: MetricAspect, *;

    declare parents : (@Metrics *) implements Profiled;

    /* packaged-protected */
    final Map<String, Metric> Profiled.metrics = new ConcurrentHashMap<String, Metric>();

    pointcut profiled(Profiled object) : execution(Profiled+.new(..)) && this(object);

    after(Profiled object) : profiled(object) {
        ELProcessor elp = newELProcessor(object);
        for (Method method : object.getClass().getDeclaredMethods()) {
            Metric metric;
            if (method.isAnnotationPresent(Timed.class)) {
                MetricRegistry registry = metricRegistry(object.getClass().getAnnotation(Metrics.class), elp);
                Timed timed = method.getAnnotation(Timed.class);
                String name = (String) elp.eval(timed.name());
                metric = registry.timer(timed.absolute() ? name : MetricRegistry.name(object.getClass(), name));
            } else if (method.isAnnotationPresent(Metered.class)) {
                MetricRegistry registry = metricRegistry(object.getClass().getAnnotation(Metrics.class), elp);
                Metered metered = method.getAnnotation(Metered.class);
                String name = (String) elp.eval(metered.name());
                metric = registry.meter(metered.absolute() ? name : MetricRegistry.name(object.getClass(), name));
            } else {
                continue;
            }

            object.metrics.put(method.toString(), metric);
        }
    }

    private MetricRegistry metricRegistry(Metrics metrics, ELProcessor elp) {
        Object evaluation = elp.eval(metrics.registry());
        if (evaluation instanceof String) {
            return SharedMetricRegistries.getOrCreate((String) evaluation);
        } else if (evaluation instanceof MetricRegistry) {
            return (MetricRegistry) evaluation;
        } else {
            throw new IllegalStateException("Unable to resolve metrics registry from expression [" + metrics.registry() + "]");
        }
    }

    private ELProcessor newELProcessor(Profiled object) {
        ELProcessor elp = new ELProcessor();
        elp.defineBean("this", object);
        elp.getELManager().importClass(SharedMetricRegistries.class.getName());
        return elp;
    }
}