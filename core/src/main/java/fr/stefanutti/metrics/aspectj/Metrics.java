package fr.stefanutti.metrics.aspectj;

import java.lang.annotation.*;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Metrics {

    String registry();
}