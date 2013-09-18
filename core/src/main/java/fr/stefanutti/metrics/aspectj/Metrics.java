package fr.stefanutti.metrics.aspectj;

import java.lang.annotation.*;
import java.lang.annotation.Retention;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Metrics {

    String registry();
}