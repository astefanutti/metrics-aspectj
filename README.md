metrics-aspectj
===============

[AspectJ](http://eclipse.org/aspectj/) integration for [Yammer's Metrics](http://metrics.codahale.com/)
with [Expression Language 3.0 (JSR-341)](http://jcp.org/en/jsr/detail?id=341) support.

## Getting Started

### Setting Up Maven

Add the `metrics-aspectj` library as a dependency:
```xml
<dependencies>
    <dependency>
        <groupId>fr.stefanutti</groupId>
        <artifactId>metrics-aspectj</artifactId>
        <version>${metrics.aspectj.version}</version>
    </dependency>
</dependencies>
```
And configure the `maven-aspectj-plugin` to compile-time weave (CTW) the `metrics-aspectj` aspects into your project:
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>aspectj-maven-plugin</artifactId>
            <version>1.5</version>
            <configuration>
                <aspectLibraries>
                    <aspectLibrary>
                        <groupId>fr.stefanutti</groupId>
                        <artifactId>metrics-aspectj</artifactId>
                    </aspectLibrary>
                </aspectLibraries>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>compile</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```
### The `@Metrics` Annotation and Registry Resolution

The `@Metrics` annotation has to be added for the aspects to be weaved into the class code.
The `@Metrics.registry` mandatory attribute must be a valid EL expression that evaluates either to
the registry name or registry instance. The result of that EL expression evaluation is a `MetricRegistry`
that is used to register the `Metric` created each time a Metrics annotation
is present on that class methods into.

The `MetricRegistry` can be resolved with an EL expression that evaluates to a `String`.
In that case the registry is resolved using `SharedMetricRegistries.getOrCreate(String name)`:
```java
import com.codahale.metrics.annotation.Timed;
import fr.stefanutti.metrics.aspectj.Metrics;

@Metrics(registry = "'registryName'")
public class TimedMethodWithRegistryByName {

    @Timed(name = "'timerName'")
    public void timedMethod() {
    }
}
```

The `MetricRegistry` can be resolved with an EL expression that evaluates to a bean property of type `MetricRegistry`:
```java
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import fr.stefanutti.metrics.aspectj.Metrics;

@Metrics(registry = "this.registry")
public class TimedMethodWithRegistryFromProperty {

    private final MetricRegistry registry;

    public TimedMethodWithRegistryFromProperty(MetricRegistry registry) {
        this.registry = registry;
    }

    public MetricRegistry getRegistry() {
        return registry;
    }

    @Timed(name = "'timerName'")
    public void timedMethod() {
    }
}
```

Or the `MetricRegistry` can be resolved with an EL expression that directly accesses to the `SharedMetricRegistries` class:
```java
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import fr.stefanutti.metrics.aspectj.Metrics;

@Metrics(registry = "SharedMetricRegistries.getOrCreate('staticRegistry')")
public class TimedMethodWithSharedMetricRegistry {

    @Timed(name = "'timerName'")
    public void timedMethod() {
    }
}
```

## Limitations

The Metrics annotations are not inherited whether these are declared on a parent class or on an implemented
interface.

The root causes of that limitation, according to the Java language specification, are:
+ Non-type annotations are not inherited
+ Annotations on types are only inherited if they have the `@Inherited` meta-annotation
+ Annotations on interfaces are not inherited irrespective to having the `@Inherited` meta-annotation

See the [`@Inherited`](http://docs.oracle.com/javase/7/docs/api/java/lang/annotation/Inherited.html) Javadoc
and [Annotation types](http://docs.oracle.com/javase/specs/jls/se7/html/jls-9.html#jls-9.6) from the
Java language specification.

AspectJ is following the Java language specification and has documented to what extent it's impacted
in [Annotation inheritance][1] and [Annotation inheritance and pointcut matching][2].

There would have been ways of working around that though:
+ That would have been working around the Java language specification in the first place
+ Plus that would have required to rely on a combination of [Expression-based pointcuts][3], [Runtime type matching][4]
  and [Reflective access][5] to define conditional pointcut expressions which:
    + Would have widen the scope of matching joint points thus introducing side-effects in addition to being inefficient
    + Would have been evaluated at runtime for each candidate join point relying on the Java Reflection API
      thus impacting the application performance and incidentally voiding the non-intrusive benefit of AOP

[1]: http://eclipse.org/aspectj/doc/next/adk15notebook/printable.html#annotation-inheritance
[2]: http://eclipse.org/aspectj/doc/released/adk15notebook/annotations-pointcuts-and-advice.html#annotation-inheritance-and-pointcut-matching
[3]: http://eclipse.org/aspectj/doc/released/progguide/semantics-pointcuts.html#d0e5549
[4]: http://eclipse.org/aspectj/doc/released/adk15notebook/annotations-pointcuts-and-advice.html#runtime-type-matching-and-context-exposure
[5]: http://eclipse.org/aspectj/doc/released/progguide/semantics-advice.html#reflective-access-to-the-join-point

## Spring AOP vs. AspectJ

Spring AOP and AspectJ provides Aspect Oriented Programming in two very different ways:
+ AspectJ provides a full-fledged aspect definition and support both compile time waving (CTW)
  and load time waving (LTW) (with a Java agent) and implements AOP with class instrumentation (byte code manipulation)
+ Spring AOP does not support the whole AspectJ aspect definition and does not support compile time waving
+ Spring AOP implements AOP either using (see [Spring proxying mechanisms][1]):
    + JDK dynamic proxies, which add little runtime overhead, increase stack traces,
      and can be incompatible with other Spring functionality like Spring JMX (for dynamic MBean export for example)
    + Or CGLIB (byte code manipulation), that has to be added as a runtime dependency:
        + It dynamically extends classes thus it is incompatible with `final` classes or methods
        + CGLIB development isn't active, Hibernate has been deprecating it in favor of Javassist (see [Deprecated CGLIB support][2])
+ AJDT (AspectJ Development Tools) provides deep integration between AspectJ and the Eclipse platform
  which is not possible with Spring AOP due to the runtime / dynamic nature of the AOP implementation

Further details can be found in [Choosing which AOP declaration style to use][3] from the Spring documentation.

[1]: http://docs.spring.io/spring/docs/4.0.x/spring-framework-reference/html/aop.html#aop-proxying
[2]: http://relation.to/16658.lace
[3]: http://docs.spring.io/spring/docs/4.0.x/spring-framework-reference/html/aop.html#aop-choosing

License
-------

Copyright (c) 2013 Antonin Stefanutti

Published under Apache Software License 2.0, see LICENSE