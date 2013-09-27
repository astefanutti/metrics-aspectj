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

import java.lang.annotation.Annotation;

/* packaged-protected */ class AnnotatedMetric {

    private final Metric metric;

    private final Annotation annotation;

    private AnnotatedMetric(Metric metric, Annotation annotation) {
        this.metric = metric;
        this.annotation = annotation;
    }

    static AnnotatedMetric newAnnotatedMetric(Metric metric, Annotation annotation) {
        return new AnnotatedMetric(metric, annotation);
    }

    @SuppressWarnings("unchecked")
    <T extends Metric> T getMetric(Class<T> clazz) {
        return (T) metric;
    }

    @SuppressWarnings("unchecked")
    <A extends Annotation> A getAnnotation(Class<A> clazz) {
        return (A) annotation;
    }
}
