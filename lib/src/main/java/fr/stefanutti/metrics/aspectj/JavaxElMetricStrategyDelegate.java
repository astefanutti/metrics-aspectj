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

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;

import javax.el.ELProcessor;

/* packaged-protected */ class JavaxElMetricStrategyDelegate implements MetricStrategy {

    private final ELProcessor processor;

    JavaxElMetricStrategyDelegate(Object object) {
        processor = new ELProcessor();
        processor.defineBean("this", object);
    }

    JavaxElMetricStrategyDelegate(Class<?> clazz) {
        processor = new ELProcessor();
        processor.getELManager().importClass(clazz.getName());
    }

    @Override
    public MetricRegistry resolveMetricRegistry(String registry) {
        Object evaluation = processor.eval(registry);
        if (evaluation instanceof String)
            return SharedMetricRegistries.getOrCreate((String) evaluation);
        else if (evaluation instanceof MetricRegistry)
            return (MetricRegistry) evaluation;
        else
            throw new IllegalStateException("Unable to resolve metrics registry from expression [" + registry + "]");
    }

    @Override
    public String resolveMetricName(String name) {
        return (String) processor.eval(name);
    }
}
