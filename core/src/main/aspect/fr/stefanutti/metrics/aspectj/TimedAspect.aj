package fr.stefanutti.metrics.aspectj;

import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import com.codahale.metrics.annotation.Timed;

import javax.el.ELProcessor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public aspect TimedAspect {

    declare parents : (@Metrics *) implements Profiled;

    private final Map<String, Timer> Profiled.timers = new HashMap<String, Timer>();

    pointcut profiled(Profiled object) : execution((@Metrics *).new(..)) && this(object);

    after(Profiled object) : profiled(object)  {
        for (Method method : object.getClass().getDeclaredMethods()) {
            Timed timed = method.getAnnotation(Timed.class);
            if (timed != null) {
                ELProcessor el = new ELProcessor();
                String name = (String) el.eval(timed.name());
                Metrics metrics = object.getClass().getAnnotation(Metrics.class);
                String registry = (String) el.eval(metrics.registry());
                Timer timer = SharedMetricRegistries.getOrCreate(registry).timer(name);
                object.timers.put(method.getName(), timer);
            }
        }
    }

    pointcut timed(Profiled object) : execution(@Timed * Profiled+.*(..)) && this(object);

    Object around(Profiled object) : timed(object) {
        Timer timer = object.timers.get(thisJoinPoint.getSignature().getName());
        Timer.Context context = timer.time();
        try {
            return proceed(object);
        } finally {
            context.stop();
        }
    }
}