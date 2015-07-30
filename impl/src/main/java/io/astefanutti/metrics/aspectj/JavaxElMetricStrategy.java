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

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;

import javax.el.ELProcessor;
import java.util.regex.Matcher;

/* package-private */ final class JavaxElMetricStrategy implements MetricStrategy {

    private final ELProcessor processor;

    JavaxElMetricStrategy(Object object) {
        processor = new ELProcessor();
        processor.defineBean("this", object);
    }

    JavaxElMetricStrategy(Class<?> clazz) {
        processor = new ELProcessor();
        processor.getELManager().importClass(clazz.getName());
    }

    @Override
    public MetricRegistry resolveMetricRegistry(String registry) {
        Matcher matcher = EL_PATTERN.matcher(registry);
        if (matcher.matches()) {
            Object evaluation = processor.eval(matcher.group(1));
            if (evaluation instanceof String)
                return SharedMetricRegistries.getOrCreate((String) evaluation);
            else if (evaluation instanceof MetricRegistry)
                return (MetricRegistry) evaluation;
            else
                throw new IllegalStateException("Unable to resolve metrics registry from expression [" + registry + "]");
        } else if (!matcher.find()) {
            return SharedMetricRegistries.getOrCreate(registry);
        } else {
            return SharedMetricRegistries.getOrCreate(evaluateCompositeExpression(matcher));
        }
    }

    @Override
    public String resolveMetricName(String name) {
        Matcher matcher = EL_PATTERN.matcher(name);
        if (!matcher.find())
            return name;
        else
            return evaluateCompositeExpression(matcher);
    }

    private String evaluateCompositeExpression(Matcher matcher) {
        StringBuffer buffer = new StringBuffer();
        do {
            Object result = processor.eval(matcher.group(1));
            matcher.appendReplacement(buffer, result != null ? String.valueOf(result) : "");
        } while (matcher.find());

        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
