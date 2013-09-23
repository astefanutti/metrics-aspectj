package fr.stefanutti.metrics.aspectj.test;

import com.codahale.metrics.annotation.Timed;
import fr.stefanutti.metrics.aspectj.Metrics;

@Metrics(registry = "'visibilityTimerRegistry'")
public class TimedMethodWithVisibilityModifiers {

    @Timed(name = "'publicTimedMethod'")
    public void publicTimedMethod() {
    }

    @Timed(name = "'packagePrivateTimedMethod'")
    void packagePrivateTimedMethod() {
    }

    @Timed(name = "'protectedTimedMethod'")
    protected void protectedTimedMethod() {
    }

    @Timed(name = "'privateTimedMethod'")
    private void privateTimedMethod() {
    }
}
