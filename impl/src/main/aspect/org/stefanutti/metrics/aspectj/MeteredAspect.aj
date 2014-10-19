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
import com.codahale.metrics.annotation.Metered;
import org.aspectj.lang.reflect.MethodSignature;

final aspect MeteredAspect {

    pointcut metered(Profiled object) : execution(@Metered !static * (@Metrics Profiled+).*(..)) && this(object);

    before(Profiled object) : metered(object) {
        String methodSignature = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod().toString();
        Meter meter = object.meters.get(methodSignature).getMetric();
        meter.mark();
    }
}