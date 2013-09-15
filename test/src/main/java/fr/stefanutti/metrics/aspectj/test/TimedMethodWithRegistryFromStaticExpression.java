package fr.stefanutti.metrics.aspectj.test;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import fr.stefanutti.metrics.aspectj.Metrics;

@Metrics(registry = "SharedMetricRegistries.getOrCreate('staticRegistry')")
public class TimedMethodWithRegistryFromStaticExpression {

    @Timed(name = "'singleTimedMethod'")
    public void singleTimedMethod() {
    }
}
