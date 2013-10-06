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
package fr.stefanutti.metrics.aspectj.samples.se.util;

import com.codahale.metrics.MetricRegistry;

import java.util.HashSet;
import java.util.Set;

public final class MetricsUtil {

    private MetricsUtil() {
    }

    public static Set<String> absoluteMetricNameSet(Class<?> clazz, String... names) {
        Set<String> set = new HashSet<String>(names.length);
        for (String name : names) {
            set.add(absoluteMetricName(clazz, name));
        }

        return set;
    }

    public static String absoluteMetricName(Class<?> clazz, String name) {
        return MetricRegistry.name(clazz, name);
    }
}
