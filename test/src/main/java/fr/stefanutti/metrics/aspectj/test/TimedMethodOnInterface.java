package fr.stefanutti.metrics.aspectj.test;

import com.codahale.metrics.annotation.Timed;
import fr.stefanutti.metrics.aspectj.Metrics;

@Metrics(registry = "'interfaceTimerRegistry'")
public interface TimedMethodOnInterface {

    @Timed(name = "'interfaceTimedMethod'")
    public void interfaceTimedMethod();
}
