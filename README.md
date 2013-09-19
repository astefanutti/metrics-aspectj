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

## Spring AOP vs. AspectJ

Spring AOP and AspectJ provides Aspect Oriented Programming in two very different ways:
+ AspectJ provides a full-fledged aspect definition and support both compile time waving (CTW)
  and load time waving (LTW) (with a Java agent) and implements AOP with class instrumentation (byte code manipulation)
+ Spring AOP does not support the whole AspectJ aspect definition and does not support compile time waving
+ Spring AOP implements AOP either using (see [Spring proxying mechanisms][1]):
  + JDK dynamic proxies, which add little runtime overhead, increase stack traces,
    and can be incompatible with other Spring functionality like Spring JMX (for dynamic MBean export for example)
  + Or CGLIB (byte code manipulation), that has to be added as a runtime dependency:
    + It dynamically extends classes thus it is incompatible with `final` classes
    + CGLIB development isn't active, Hibernate has been deprecating it in favor of Javassist (see [Deprecated CGLIB support][2])
+ AJDT (AspectJ Development Tools) provides deep integration between AspectJ and the Eclipse platform
  which is not possible with Spring AOP due to the runtime / dynamic nature of the AOP implementation

[1]: http://docs.spring.io/spring/docs/4.0.x/spring-framework-reference/html/aop.html#aop-proxying
[2]: http://relation.to/16658.lace

License
-------

Copyright (c) 2013 Antonin Stefanutti

Published under Apache Software License 2.0, see LICENSE