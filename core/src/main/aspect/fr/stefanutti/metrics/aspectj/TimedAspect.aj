package fr.stefanutti.metrics.aspectj;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import com.codahale.metrics.annotation.Timed;

import javax.el.ELProcessor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public aspect TimedAspect {

    declare parents : (@Metrics *) implements Profiled;

    private final Map<String, Timer> Profiled.timers = new ConcurrentHashMap<String, Timer>();

    pointcut profiled(Profiled object) : execution((@Metrics *).new(..)) && this(object);

    after(Profiled object) : profiled(object)  {
        for (Method method : object.getClass().getDeclaredMethods()) {
            Timed timed = method.getAnnotation(Timed.class);
            if (timed != null) {
                ELProcessor elp = new ELProcessor();
                elp.defineBean("this", object);
                elp.getELManager().importClass(SharedMetricRegistries.class.getName());
                Metrics metrics = object.getClass().getAnnotation(Metrics.class);
                Object eval = elp.eval(metrics.registry());
                MetricRegistry registry = null;
                if (eval instanceof String) {
                    registry = SharedMetricRegistries.getOrCreate((String) eval);
                } else if (eval instanceof MetricRegistry) {
                    registry = (MetricRegistry) eval;
                } else {
                    throw new IllegalStateException("Unable to resolve metrics registry from expression [" + metrics.registry() + "]");
                }
                Timer timer = registry.timer((String) elp.eval(timed.name()));
                // TODO: be more specific for the key to avoid clashes
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