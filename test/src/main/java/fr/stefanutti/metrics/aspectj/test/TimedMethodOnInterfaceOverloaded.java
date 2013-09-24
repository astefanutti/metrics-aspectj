package fr.stefanutti.metrics.aspectj.test;

import com.codahale.metrics.annotation.Timed;
import fr.stefanutti.metrics.aspectj.Metrics;

import java.util.List;

@Metrics(registry = "'overloadedInterfaceTimerRegistry'")
public interface TimedMethodOnInterfaceOverloaded {

    @Timed(name = "'overloadedTimedMethodWithNoArguments'")
    public void overloadedTimedMethod();

    public void overloadedTimedMethod(String string);

    public void overloadedTimedMethod(List<String> strings, String string, Object object);
}
