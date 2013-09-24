package fr.stefanutti.metrics.aspectj.test;

import java.util.List;

public class TimedMethodOnInterfaceOverloadedImpl implements TimedMethodOnInterfaceOverloaded {

    @Override
    public void overloadedTimedMethod() {
    }

    @Override
    public void overloadedTimedMethod(String string) {
    }

    @Override
    public void overloadedTimedMethod(List<String> strings, String string, Object object) {
    }
}
