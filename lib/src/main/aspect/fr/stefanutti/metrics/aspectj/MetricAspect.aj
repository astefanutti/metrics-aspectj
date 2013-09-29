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
import com.codahale.metrics.annotation.Gauge;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;

import javax.el.ELProcessor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final aspect MetricAspect {

    declare precedence: MetricAspect, *;

    declare parents : (@Metrics *) implements Profiled;

    /* packaged-protected */
    final Map<String, AnnotatedMetric> Profiled.metrics = new ConcurrentHashMap<String, AnnotatedMetric>();

    pointcut profiled(Profiled object) : execution((@Metrics Profiled+).new(..)) && this(object);

    after(final Profiled object) : profiled(object) {
        final ELProcessor elp = newELProcessor(object);
        for (final Method method : object.getClass().getDeclaredMethods()) {
            metricAnnotation(object, method, elp, ExceptionMetered.class, new MetricFactory() {
                @Override
                public Metric metric(MetricRegistry registry, String name, boolean absolute) {
                    String finalName = name.isEmpty() ? method.getName() + "." + ExceptionMetered.DEFAULT_NAME_SUFFIX : (String) elp.eval(name);
                    return registry.meter(absolute ? finalName : MetricRegistry.name(object.getClass(), finalName));
                }
            });
            metricAnnotation(object, method, elp, Metered.class, new MetricFactory() {
                @Override
                public Metric metric(MetricRegistry registry, String name, boolean absolute) {
                    String finalName = name.isEmpty() ? method.getName() : (String) elp.eval(name);
                    return registry.meter(absolute ? finalName : MetricRegistry.name(object.getClass(), finalName));
                }
            });
            metricAnnotation(object, method, elp, Timed.class, new MetricFactory() {
                @Override
                public Metric metric(MetricRegistry registry, String name, boolean absolute) {
                    String finalName = name.isEmpty() ? method.getName() : (String) elp.eval(name);
                    return registry.timer(absolute ? finalName : MetricRegistry.name(object.getClass(), finalName));
                }
            });
        }
    }

    private void metricAnnotation(Profiled object, Method method, ELProcessor elp, Class<? extends Annotation> clazz, MetricFactory factory) {
        if (method.isAnnotationPresent(clazz)) {
            MetricRegistry registry = metricRegistry(object.getClass().getAnnotation(Registry.class), elp);
            Annotation annotation = method.getAnnotation(clazz);
            Metric metric = factory.metric(registry, metricAnnotationName(annotation), metricAnnotationAbsolute(annotation));
            object.metrics.put(method.toString(), new AnnotatedMetric(metric, annotation));
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

    private interface MetricFactory {
        Metric metric(MetricRegistry registry, String name, boolean absolute);
    }

    private static String metricAnnotationName(Annotation annotation) {
        if (Gauge.class.isInstance(annotation))
           return ((Gauge) annotation).name();
        else if (ExceptionMetered.class.isInstance(annotation))
           return ((ExceptionMetered) annotation).name();
        else if (Metered.class.isInstance(annotation))
            return ((Metered) annotation).name();
        else if (Timed.class.isInstance(annotation))
            return ((Timed) annotation).name();
        else
            throw new IllegalArgumentException("Unsupported Metrics annotation [" + annotation.getClass().getName() + "]");
    }

    private static boolean metricAnnotationAbsolute(Annotation annotation) {
        if (Gauge.class.isInstance(annotation))
            return ((Gauge) annotation).absolute();
        else if (ExceptionMetered.class.isInstance(annotation))
            return ((ExceptionMetered) annotation).absolute();
        else if (Metered.class.isInstance(annotation))
            return ((Metered) annotation).absolute();
        else if (Timed.class.isInstance(annotation))
            return ((Timed) annotation).absolute();
        else
            throw new IllegalArgumentException("Unsupported Metrics annotation [" + annotation.getClass().getName() + "]");
    }
}