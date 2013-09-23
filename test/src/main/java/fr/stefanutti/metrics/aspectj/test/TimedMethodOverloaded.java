package fr.stefanutti.metrics.aspectj.test;

import com.codahale.metrics.annotation.Timed;
import fr.stefanutti.metrics.aspectj.Metrics;

import java.util.List;

@Metrics(registry = "'overloadedTimerRegistry'")
public class TimedMethodOverloaded {

    @Timed(name = "'overloadedTimedMethodWithNoArguments'")
    public void overloadedTimedMethod() {
    }

    @Timed(name = "'overloadedTimedMethodWithStringArgument'")
    public void overloadedTimedMethod(String string) {
    }

    @Timed(name = "'overloadedTimedMethodWithListOfStringArgument'")
    public void overloadedTimedMethod(List<String> strings) {
    }

    @Timed(name = "'overloadedTimedMethodWithObjectArgument'")
    public void overloadedTimedMethod(Object object) {
    }
}
