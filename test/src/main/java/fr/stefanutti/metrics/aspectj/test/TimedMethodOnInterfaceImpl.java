package fr.stefanutti.metrics.aspectj.test;

import com.codahale.metrics.annotation.Timed;

public class TimedMethodOnInterfaceImpl implements TimedMethodOnInterface {

    @Override
    public void interfaceTimedMethod() {
    }
}
