package fr.stefanutti.metrics.aspectj.test;

import com.codahale.metrics.annotation.Timed;
import fr.stefanutti.metrics.aspectj.Metrics;

@Metrics(registry = "'singleTimerRegistry'")
public class SingleTimedMethod {

    @Timed(name = "'singleTimedMethod'")
    public void singleTimedMethod() {

    }
}
