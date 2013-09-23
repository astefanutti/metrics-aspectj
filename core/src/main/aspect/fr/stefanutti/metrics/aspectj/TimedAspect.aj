package fr.stefanutti.metrics.aspectj;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import com.codahale.metrics.annotation.Timed;
import org.aspectj.lang.JoinPoint;

import javax.el.ELProcessor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public aspect TimedAspect {

    declare parents : (@Metrics *) extends Profiled;

    private final Map<String, Timer> Profiled.timers = new ConcurrentHashMap<String, Timer>();

    pointcut profiled(Profiled object) : execution(Profiled+.new(..)) && this(object);

    after(Profiled object) : profiled(object)  {
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Timed.class)) {
                Timer timer = timerFromAnnotation(object, object.getClass().getAnnotation(Metrics.class), method.getAnnotation(Timed.class));
                object.timers.put(method.toString(), timer);
            } else {
                for (Class<?> itf : object.getClass().getInterfaces()) {
                    if (itf.isAnnotationPresent(Metrics.class)) {
                        try {
                            Method override = itf.getDeclaredMethod(method.getName(), method.getParameterTypes());
                            if (override.isAnnotationPresent(Timed.class)) {
                                Timer timer = timerFromAnnotation(object, itf.getAnnotation(Metrics.class), override.getAnnotation(Timed.class));
                                object.timers.put(method.toString(), timer);
                            }
                        } catch (NoSuchMethodException cause) {
                            // Swallow as there is no overridden for that method
                        }
                    }
                }
            }
        }
    }

    private Timer timerFromAnnotation(Profiled object, Metrics metrics, Timed timed) {
        ELProcessor elp = new ELProcessor();
        elp.defineBean("this", object);
        elp.getELManager().importClass(SharedMetricRegistries.class.getName());
        Object eval = elp.eval(metrics.registry());
        MetricRegistry registry = null;
        if (eval instanceof String) {
            registry = SharedMetricRegistries.getOrCreate((String) eval);
        } else if (eval instanceof MetricRegistry) {
            registry = (MetricRegistry) eval;
        } else {
            throw new IllegalStateException("Unable to resolve metrics registry from expression [" + metrics.registry() + "]");
        }
        return registry.timer((String) elp.eval(timed.name()));
    }

    pointcut timed(Profiled object) : execution(* Profiled+.*(..)) && if(isTimedMethod(thisJoinPoint)) && this(object);

    Object around(Profiled object) : timed(object) {
        Timer timer = object.timers.get(thisJoinPoint.getSignature().toLongString());
        Timer.Context context = timer.time();
        try {
            return proceed(object);
        } finally {
            context.stop();
        }
    }

    private static boolean isTimedMethod(JoinPoint joinPoint) {
        if (isTimedMethod(joinPoint.getTarget().getClass(), joinPoint))
            return true;

        for (Class<?> itf : joinPoint.getTarget().getClass().getInterfaces()) {
            if (isTimedMethod(itf, joinPoint))
                return true;
        }
        return false;
    }

    private static boolean isTimedMethod(Class<?> clazz, JoinPoint joinPoint) {
        if (clazz.isAnnotationPresent(Metrics.class)) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Timed.class)) {
                    // TODO: should take the arguments into consideration for the comparison
                    if (method.getName().equals(joinPoint.getSignature().getName()))
                        return true;
                }
            }
        }
        return false;
    }
}