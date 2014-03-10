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

import java.util.regex.Matcher;

/* packaged-private */ final class JavaSeMetricStrategy implements MetricStrategy {

    private final MetricStrategy seDelegate = new JavaSeMetricStrategyDelegate();

    @Override
    public MetricRegistry resolveMetricRegistry(String registry) {
        Matcher matcher = EL.matcher(registry);
        if (matcher.matches())
            throw new UnsupportedOperationException("Unsupported EL expression [" + registry + "] evaluation as no EL implementation is available");
        else
            return seDelegate.resolveMetricRegistry(registry);
    }

    @Override
    public String resolveMetricName(String name) {
        Matcher matcher = EL.matcher(name);
        if (matcher.matches())
            throw new UnsupportedOperationException("Unsupported EL expression [" + name + "] evaluation as no EL implementation is available");
        else
            return seDelegate.resolveMetricName(name);
    }
}
