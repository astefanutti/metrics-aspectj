package fr.stefanutti.metrics.aspectj;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import com.codahale.metrics.annotation.Timed;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import javax.el.ELProcessor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

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

    pointcut timed(Profiled object) : execution(@Timed * Profiled+.*(..)) && this(object);

    pointcut timedInherited(Profiled object) : execution(!@Timed * Profiled+.*(..)) && if(isTimedMethod(thisJoinPoint)) && this(object);

    Object around(Profiled object) : timed(object) || timedInherited(object) {
        Timer timer = object.timers.get(thisJoinPointStaticPart.getSignature().toLongString());
        Timer.Context context = timer.time();
        try {
            return proceed(object);
        } finally {
            context.stop();
        }
    }

    private static boolean isTimedMethod(JoinPoint joinPoint) {
        if (!(joinPoint.getSignature() instanceof MethodSignature))
            return false;

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        for (Class<?> itf : joinPoint.getTarget().getClass().getInterfaces()) {
            if (isTimedMethod(itf, method))
                return true;
        }

        // TODO: support inheritance and iterate over parent classes

        return false;
    }

    private static boolean isTimedMethod(Class<?> clazz, Method method) {
        if (clazz.isAnnotationPresent(Metrics.class)) {
            for (Method declared : clazz.getDeclaredMethods()) {
                if (declared.isAnnotationPresent(Timed.class)) {
                    if (isOverriddenMethod(declared, method))
                        return true;
                }
            }
        }

        return false;
    }

    private static boolean isOverriddenMethod(Method method1, Method method2) {
        if (!method1.getName().equals(method2.getName()))
            return false;
        if (!method1.getReturnType().equals(method2.getReturnType()))
            return false;

        Class<?>[] params1 = method1.getParameterTypes();
        Class<?>[] params2 = method2.getParameterTypes();
        if (params1.length == params2.length) {
            for (int i = 0; i < params1.length; i++) {
                if (params1[i] != params2[i])
                    return false;
            }
            return true;
        }

        return false;
    }
}