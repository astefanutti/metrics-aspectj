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
package org.stefanutti.metrics.aspectj;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Gauge;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final aspect MetricStaticAspect extends AbstractMetricAspect {

    /* packaged-private */
    final static ConcurrentMap<String, AnnotatedMetric> metrics = new ConcurrentHashMap<String, AnnotatedMetric>();

    pointcut profiled() : staticinitialization(@Metrics *);

    after() : profiled() {
        final Class<?> clazz = thisJoinPointStaticPart.getSignature().getDeclaringType();
        final MetricStrategy strategy = MetricStrategyFactory.newInstance(clazz);
        for (final Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers()) && !method.isSynthetic()) {
                metricAnnotation(clazz, method, strategy, ExceptionMetered.class, new MetricFactory() {
                    @Override
                    public Metric metric(MetricRegistry registry, String name, boolean absolute) {
                        String finalName = name.isEmpty() ? method.getName() + "." + ExceptionMetered.DEFAULT_NAME_SUFFIX : strategy.resolveMetricName(name);
                        return registry.meter(absolute ? finalName : MetricRegistry.name(clazz, finalName));
                    }
                });
                metricAnnotation(clazz, method, strategy, Gauge.class, new MetricFactory() {
                    @Override
                    public Metric metric(MetricRegistry registry, String name, boolean absolute) {
                        String finalName = name.isEmpty() ? method.getName() : strategy.resolveMetricName(name);
                        return registry.register(absolute ? finalName : MetricRegistry.name(clazz, finalName), new ForwardingGauge(method, clazz));
                    }
                });
                metricAnnotation(clazz, method, strategy, Metered.class, new MetricFactory() {
                    @Override
                    public Metric metric(MetricRegistry registry, String name, boolean absolute) {
                        String finalName = name.isEmpty() ? method.getName() : strategy.resolveMetricName(name);
                        return registry.meter(absolute ? finalName : MetricRegistry.name(clazz, finalName));
                    }
                });
                metricAnnotation(clazz, method, strategy, Timed.class, new MetricFactory() {
                    @Override
                    public Metric metric(MetricRegistry registry, String name, boolean absolute) {
                        String finalName = name.isEmpty() ? method.getName() : strategy.resolveMetricName(name);
                        return registry.timer(absolute ? finalName : MetricRegistry.name(clazz, finalName));
                    }
                });
            }
        }
    }

    private void metricAnnotation(Class<?> annotated, Method method, MetricStrategy strategy, Class<? extends Annotation> clazz, MetricFactory factory) {
        if (method.isAnnotationPresent(clazz)) {
            MetricRegistry registry = strategy.resolveMetricRegistry(annotated.getAnnotation(Registry.class).value());
            Annotation annotation = method.getAnnotation(clazz);
            Metric metric = factory.metric(registry, metricAnnotationName(annotation), metricAnnotationAbsolute(annotation));
            metrics.put(method.toString(), new AnnotatedMetric(metric, annotation));
        }
    }
}