package fr.stefanutti.metrics.aspectj.test;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import fr.stefanutti.metrics.aspectj.Metrics;

@Metrics(registry = "this.registry")
public class TimedMethodWithRegistryFromExpression {

    private final MetricRegistry registry;

    public TimedMethodWithRegistryFromExpression(MetricRegistry registry) {
        this.registry = registry;
    }

    public MetricRegistry getRegistry() {
        return registry;
    }

    @Timed(name = "'singleTimedMethod'")
    public void singleTimedMethod() {
    }
}
