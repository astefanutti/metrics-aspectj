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
import com.codahale.metrics.annotation.Gauge;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final aspect MetricAspect extends AbstractMetricAspect {

    declare precedence: MetricStaticAspect, MetricAspect, *;

    declare parents : (@Metrics *) implements Profiled;

    /* packaged-protected */
    final Map<String, AnnotatedMetric> Profiled.metrics = new ConcurrentHashMap<String, AnnotatedMetric>();

    pointcut profiled(Profiled object) : execution((@Metrics Profiled+).new(..)) && this(object);

    after(final Profiled object) : profiled(object) {
        final MetricStrategy strategy = MetricStrategyFactory.newInstance(object);
        for (final Method method : object.getClass().getDeclaredMethods()) {
            // Skip advising static methods
            if (Modifier.isStatic(method.getModifiers()))
                continue;

            metricAnnotation(object, method, strategy, ExceptionMetered.class, new MetricFactory() {
                @Override
                public Metric metric(MetricRegistry registry, String name, boolean absolute) {
                    String finalName = name.isEmpty() ? method.getName() + "." + ExceptionMetered.DEFAULT_NAME_SUFFIX : strategy.resolveMetricName(name);
                    return registry.meter(absolute ? finalName : MetricRegistry.name(object.getClass(), finalName));
                }
            });
            metricAnnotation(object, method, strategy, Gauge.class, new MetricFactory() {
                @Override
                public Metric metric(MetricRegistry registry, String name, boolean absolute) {
                    String finalName = name.isEmpty() ? method.getName() : strategy.resolveMetricName(name);
                    return registry.register(absolute ? finalName : MetricRegistry.name(object.getClass(), finalName), new ForwardingGauge(method, object));
                }
            });
            metricAnnotation(object, method, strategy, Metered.class, new MetricFactory() {
                @Override
                public Metric metric(MetricRegistry registry, String name, boolean absolute) {
                    String finalName = name.isEmpty() ? method.getName() : strategy.resolveMetricName(name);
                    return registry.meter(absolute ? finalName : MetricRegistry.name(object.getClass(), finalName));
                }
            });
            metricAnnotation(object, method, strategy, Timed.class, new MetricFactory() {
                @Override
                public Metric metric(MetricRegistry registry, String name, boolean absolute) {
                    String finalName = name.isEmpty() ? method.getName() : strategy.resolveMetricName(name);
                    return registry.timer(absolute ? finalName : MetricRegistry.name(object.getClass(), finalName));
                }
            });
        }
    }

    private void metricAnnotation(Profiled object, Method method, MetricStrategy strategy, Class<? extends Annotation> clazz, MetricFactory factory) {
        if (method.isAnnotationPresent(clazz)) {
            MetricRegistry registry = strategy.resolveMetricRegistry(object.getClass().getAnnotation(Registry.class).value());
            Annotation annotation = method.getAnnotation(clazz);
            Metric metric = factory.metric(registry, metricAnnotationName(annotation), metricAnnotationAbsolute(annotation));
            object.metrics.put(method.toString(), new AnnotatedMetric(metric, annotation));
        }
    }
}