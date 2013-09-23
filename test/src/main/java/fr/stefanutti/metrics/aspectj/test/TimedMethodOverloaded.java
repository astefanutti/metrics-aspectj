package fr.stefanutti.metrics.aspectj.test;

import com.codahale.metrics.annotation.Timed;
import fr.stefanutti.metrics.aspectj.Metrics;

import java.util.List;

@Metrics(registry = "'signatureTimerRegistry'")
public class TimedMethodOverloaded {

    @Timed(name = "'signatureTimedMethodWithNoArguments'")
    public void signatureTimedMethod() {
    }

    @Timed(name = "'signatureTimedMethodWithStringArgument'")
    public void signatureTimedMethod(String string) {
    }

    @Timed(name = "'signatureTimedMethodWithListOfStringArgument'")
    public void signatureTimedMethod(List<String> strings) {
    }

    @Timed(name = "'signatureTimedMethodWithObjectArgument'")
    public void signatureTimedMethod(Object object) {
    }
}
