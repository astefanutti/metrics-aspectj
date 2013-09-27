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

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;

import javax.el.ELProcessor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static fr.stefanutti.metrics.aspectj.AnnotatedMetric.newAnnotatedMetric;

final aspect MetricAspect {

    declare precedence: MetricAspect, *;

    declare parents : (@Metrics *) implements Profiled;

    /* packaged-protected */
    final Map<String, AnnotatedMetric> Profiled.metrics = new ConcurrentHashMap<String, AnnotatedMetric>();

    pointcut profiled(Profiled object) : execution((@Metrics Profiled+).new(..)) && this(object);

    after(Profiled object) : profiled(object) {
        ELProcessor elp = newELProcessor(object);
        for (Method method : object.getClass().getDeclaredMethods()) {
            // TODO: handle the case when name is equal to empty String according to Metrics Javadoc
            // TODO: factorize the repeating logic
            if (method.isAnnotationPresent(Timed.class)) {
                MetricRegistry registry = metricRegistry(object.getClass().getAnnotation(Registry.class), elp);
                Timed timed = method.getAnnotation(Timed.class);
                String name = (String) elp.eval(timed.name());
                Timer timer = registry.timer(timed.absolute() ? name : MetricRegistry.name(object.getClass(), name));
                object.metrics.put(method.toString(), newAnnotatedMetric(timer, timed));
            }
            if (method.isAnnotationPresent(Metered.class)) {
                MetricRegistry registry = metricRegistry(object.getClass().getAnnotation(Registry.class), elp);
                Metered metered = method.getAnnotation(Metered.class);
                String name = (String) elp.eval(metered.name());
                Meter meter = registry.meter(metered.absolute() ? name : MetricRegistry.name(object.getClass(), name));
                object.metrics.put(method.toString(), newAnnotatedMetric(meter, metered));
            }
            if (method.isAnnotationPresent(ExceptionMetered.class)) {
                MetricRegistry registry = metricRegistry(object.getClass().getAnnotation(Registry.class), elp);
                ExceptionMetered metered = method.getAnnotation(ExceptionMetered.class);
                String name = (String) elp.eval(metered.name());
                Meter meter = registry.meter(metered.absolute() ? name : MetricRegistry.name(object.getClass(), name));
                object.metrics.put(method.toString(), newAnnotatedMetric(meter, metered));
            }
        }
    }

    private MetricRegistry metricRegistry(Registry registry, ELProcessor elp) {
        Object evaluation = elp.eval(registry.value());
        if (evaluation instanceof String) {
            return SharedMetricRegistries.getOrCreate((String) evaluation);
        } else if (evaluation instanceof MetricRegistry) {
            return (MetricRegistry) evaluation;
        } else {
            throw new IllegalStateException("Unable to resolve metrics registry from expression [" + registry.value() + "]");
        }
    }

    private ELProcessor newELProcessor(Profiled object) {
        ELProcessor elp = new ELProcessor();
        elp.defineBean("this", object);
        elp.getELManager().importClass(SharedMetricRegistries.class.getName());
        return elp;
    }
}