package fr.stefanutti.metrics.aspectj.test;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import fr.stefanutti.metrics.aspectj.Metrics;

@Metrics(registry = "this.registry")
public class TimedMethodWithRegistryFromBeanProperty {

    private final MetricRegistry registry;

    public TimedMethodWithRegistryFromBeanProperty(MetricRegistry registry) {
        this.registry = registry;
    }

    public MetricRegistry getRegistry() {
        return registry;
    }

    @Timed(name = "'singleTimedMethod'")
    public void singleTimedMethod() {
    }
}
