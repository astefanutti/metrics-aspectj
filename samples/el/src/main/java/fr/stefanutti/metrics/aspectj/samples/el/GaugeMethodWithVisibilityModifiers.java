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
package fr.stefanutti.metrics.aspectj.samples.el;

import com.codahale.metrics.annotation.Gauge;
import com.codahale.metrics.annotation.Timed;
import fr.stefanutti.metrics.aspectj.Metrics;
import fr.stefanutti.metrics.aspectj.Registry;

@Metrics @Registry("'visibilityGaugeRegistry'")
public class GaugeMethodWithVisibilityModifiers {

    private int publicGauge;

    private int packagePrivateGauge;

    private int protectedGauge;

    private int privateGauge;

    @Gauge(name = "'publicGaugeMethod'")
    public int getPublicGaugeMethod() {
        return publicGauge;
    }

    public void setPublicGauge(int gauge) {
        publicGauge = gauge;
    }

    @Gauge(name = "'packagePrivateGaugeMethod'")
    int getPackagePrivateGauge() {
        return packagePrivateGauge;
    }

    void setPackagePrivateGauge(int gauge) {
        packagePrivateGauge = gauge;
    }

    @Gauge(name = "'protectedGaugeMethod'")
    protected int getProtectedGauge() {
        return protectedGauge;
    }

    protected void setProtectedGauge(int gauge) {
        protectedGauge = gauge;
    }

    @Gauge(name = "'privateGaugeMethod'")
    private int getPrivateGauge() {
        return privateGauge;
    }

    private void setPrivateGauge(int gauge) {
        privateGauge = gauge;
    }
}
