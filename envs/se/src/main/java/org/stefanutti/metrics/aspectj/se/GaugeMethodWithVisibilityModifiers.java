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
package org.stefanutti.metrics.aspectj.se;

import com.codahale.metrics.annotation.Gauge;
import org.stefanutti.metrics.aspectj.Metrics;

@Metrics(registry = "visibilityGaugeRegistry")
public class GaugeMethodWithVisibilityModifiers {

    private long publicGauge;

    private long packagePrivateGauge;

    private long protectedGauge;

    private long privateGauge;

    @Gauge(name = "publicGaugeMethod")
    public long getPublicGaugeMethod() {
        return publicGauge;
    }

    public void setPublicGauge(long gauge) {
        publicGauge = gauge;
    }

    @Gauge(name = "packagePrivateGaugeMethod")
    long getPackagePrivateGauge() {
        return packagePrivateGauge;
    }

    void setPackagePrivateGauge(long gauge) {
        packagePrivateGauge = gauge;
    }

    @Gauge(name = "protectedGaugeMethod")
    protected long getProtectedGauge() {
        return protectedGauge;
    }

    protected void setProtectedGauge(long gauge) {
        protectedGauge = gauge;
    }

    @Gauge(name = "privateGaugeMethod")
    private long getPrivateGauge() {
        return privateGauge;
    }

    private void setPrivateGauge(long gauge) {
        privateGauge = gauge;
    }
}
