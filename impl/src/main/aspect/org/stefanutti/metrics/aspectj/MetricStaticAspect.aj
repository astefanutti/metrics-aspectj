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

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Gauge;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final aspect MetricStaticAspect extends AbstractMetricAspect {

    static final Map<String, AnnotatedMetric<com.codahale.metrics.Gauge>> GAUGES = new ConcurrentHashMap<String, AnnotatedMetric<com.codahale.metrics.Gauge>>();

    static final Map<String, AnnotatedMetric<Meter>> METERS = new ConcurrentHashMap<String, AnnotatedMetric<Meter>>();

    static final Map<String, AnnotatedMetric<Timer>> TIMERS = new ConcurrentHashMap<String, AnnotatedMetric<Timer>>();

    pointcut profiled() : staticinitialization(@Metrics *);

    after() : profiled() {
        final Class<?> clazz = thisJoinPointStaticPart.getSignature().getDeclaringType();
        final MetricStrategy strategy = MetricStrategyFactory.newInstance(clazz);

        for (final Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers()) && !method.isSynthetic()) {
                final MetricRegistry registry = strategy.resolveMetricRegistry(clazz.getAnnotation(Metrics.class).registry());

                AnnotatedMetric<Meter> exception = metricAnnotation(method, ExceptionMetered.class, registry, new MetricFactory<Meter>() {
                    @Override
                    public Meter metric(MetricRegistry registry, String name, boolean absolute) {
                        String finalName = name.isEmpty() ? method.getName() + "." + ExceptionMetered.DEFAULT_NAME_SUFFIX : strategy.resolveMetricName(name);
                        return registry.meter(absolute ? finalName : MetricRegistry.name(clazz, finalName));
                    }
                });
                if (exception.isPresent())
                    METERS.put(method.toString(), exception);

                AnnotatedMetric<com.codahale.metrics.Gauge> gauge = metricAnnotation(method, Gauge.class, registry, new MetricFactory<com.codahale.metrics.Gauge>() {
                    @Override
                    public com.codahale.metrics.Gauge metric(MetricRegistry registry, String name, boolean absolute) {
                        String finalName = name.isEmpty() ? method.getName() : strategy.resolveMetricName(name);
                        return registry.register(absolute ? finalName : MetricRegistry.name(clazz, finalName), new ForwardingGauge(method, clazz));
                    }
                });
                if (gauge.isPresent())
                   GAUGES.put(method.toString(), gauge);

                AnnotatedMetric<Meter> meter = metricAnnotation(method, Metered.class, registry, new MetricFactory<Meter>() {
                    @Override
                    public Meter metric(MetricRegistry registry, String name, boolean absolute) {
                        String finalName = name.isEmpty() ? method.getName() : strategy.resolveMetricName(name);
                        return registry.meter(absolute ? finalName : MetricRegistry.name(clazz, finalName));
                    }
                });
                if (meter.isPresent())
                    METERS.put(method.toString(), meter);

                AnnotatedMetric<Timer> timer = metricAnnotation(method, Timed.class, registry, new MetricFactory<Timer>() {
                    @Override
                    public Timer metric(MetricRegistry registry, String name, boolean absolute) {
                        String finalName = name.isEmpty() ? method.getName() : strategy.resolveMetricName(name);
                        return registry.timer(absolute ? finalName : MetricRegistry.name(clazz, finalName));
                    }
                });
                if (timer.isPresent())
                    TIMERS.put(method.toString(), timer);
            }
        }
    }
}