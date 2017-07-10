# AspectJ for Metrics

[![Build Status][Travis badge]][Travis build] [![Coverage Status][Coveralls badge]][Coveralls build] [![Dependency Status][VersionEye badge]][VersionEye build] [![Maven Central][Maven Central badge]][Maven Central build]

[Travis badge]: https://travis-ci.org/astefanutti/metrics-aspectj.svg?branch=master
[Travis build]: https://travis-ci.org/astefanutti/metrics-aspectj
[Coveralls badge]: https://coveralls.io/repos/astefanutti/metrics-aspectj/badge.svg
[Coveralls build]: https://coveralls.io/github/astefanutti/metrics-aspectj
[VersionEye badge]: https://www.versioneye.com/user/projects/52a63b6b632bacd22f000024/badge.svg
[VersionEye build]: https://www.versioneye.com/user/projects/52a63b6b632bacd22f000024
[Maven Central badge]: http://img.shields.io/maven-central/v/io.astefanutti.metrics.aspectj/metrics-aspectj.svg
[Maven Central build]: http://central.maven.org/maven2/io/astefanutti/metrics/aspectj/metrics-aspectj/1.2.0/

[AspectJ][] integration for Dropwizard [Metrics][] with optional [Expression Language 3.0 (JSR-341)][] support.

[AspectJ]: http://eclipse.org/aspectj/
[Metrics]: http://metrics.codahale.com/
[Expression Language 3.0 (JSR-341)]: http://jcp.org/en/jsr/detail?id=341

## About

_Metrics AspectJ_ provides support for the [_Metrics_ annotations][Metrics annotations] in Java SE environments using _AspectJ_ to perform AOP instrumentation. It implements the contract specified by these annotations with the following level of functionality:
+ Intercept invocations of instance and class methods annotated with [`@ExceptionMetered`][], [`@Metered`][] and [`@Timed`][],
+ Create [`Gauge`][] metrics for instance and class methods annotated with [`@Gauge`][],
+ Register / retrieve the [`Metric`][] instances in the resolved [`MetricRegistry`][] instance,
+ Resolve the [`MetricRegistry`][] instance by looking up into the [`SharedMetricRegistries`][] class or optionally by dynamically evaluating EL expressions.

_Metrics AspectJ_ is compatible with _Metrics_ version `3.0.0`+ and requires Java 6 or higher.

[Metrics annotations]: http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/annotation/package-summary.html
[`@ExceptionMetered`]: http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/annotation/ExceptionMetered.html
[`@Gauge`]: http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/annotation/Gauge.html
[`@Metered`]: http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/annotation/Metered.html
[`@Timed`]: http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/annotation/Timed.html
[`Gauge`]: http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/Gauge.html
[`Metric`]: http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/Metric.html
[`MetricRegistry`]: http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/MetricRegistry.html
[`SharedMetricRegistries`]: http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/SharedMetricRegistries.html

## Getting Started

#### Using Maven

Add the `metrics-aspectj` library as a dependency:

```xml
<dependency>
    <groupId>io.astefanutti.metrics.aspectj</groupId>
    <artifactId>metrics-aspectj</artifactId>
    <version>1.2.0</version>
</dependency>
```

And configure the `maven-aspectj-plugin` to compile-time weave (CTW) the `metrics-aspectj` aspects into your project:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>aspectj-maven-plugin</artifactId>
    <configuration>
        <aspectLibraries>
            <aspectLibrary>
                <groupId>io.astefanutti.metrics.aspectj</groupId>
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
```

More information can be found in the [Maven AspectJ plugin][] documentation.

[Maven AspectJ plugin]: http://mojo.codehaus.org/aspectj-maven-plugin/

#### Using Ant

Use the [AjcTask][] (`iajc`) Ant task:

```xml
<target name="{target}" >
    <iajc sourceroots="${basedir}/src"
          classpath="${basedir}/lib/aspectjrt.jar"
          outjar="${basedir}/build/${ant.project.name}.jar">
        ...
        <aspectpath>
            <pathelement location="${basedir}/lib/metrics-aspectj.jar"/>
        </aspectpath>
        ...
    </iajc>
</target>
```

Other options are detailed in the [AspectJ Ant tasks][] documentation.

[AjcTask]: http://www.eclipse.org/aspectj/doc/next/devguide/antTasks-iajc.html
[AspectJ Ant tasks]: http://www.eclipse.org/aspectj/doc/next/devguide/antTasks.html

#### Using Gradle

[gradle example]: https://github.com/paegun/metrics-aspectj-gradle-example
A working [gradle example][] is available, but each integration point is described here.

##### build.gradle snippets

```java
buildscript {
    // ensure the gradle-aspectj integration is w/i the build classpath
    dependencies {
        classpath 'nl.eveoh:gradle-aspectj:1.6'
    }
}

// specify the aspectjVersion, used by gradle-aspectj
project.ext {
    aspectjVersion = '1.8.10'
}

// specify the Dropwizard Metrics version (metricsVer)
//  and the aspect-oriented metrics version (metricsAspectVer, this solution)
ext {
    metricsVer = '3.2.2'
    metricsAspectVer = '1.2.0'
}

// via the gradle-aspectj integration, run "aspect weaving"
apply plugin: 'aspectj'

// ensure Dropwizard Metrics as well as the aspect-oriented metrics (astefanutti.metrics.aspectj)
//  runtime dependencies of your solution are satisfied.
dependencies {
    compile "io.astefanutti.metrics.aspectj:metrics-aspectj:${metricsAspectVer}"
    // add a path for the gradle-aspectj "aspect weaving" (AspectJ Compiler compile)
    aspectpath "io.astefanutti.metrics.aspectj:metrics-aspectj:${metricsAspectVer}"

    compile "io.dropwizard.metrics:metrics-core:${metricsVer}"
    compile "io.dropwizard.metrics:metrics-annotation:${metricsVer}"
    compile "io.dropwizard.metrics:metrics-jersey2:${metricsVer}"
}
```

#### Using the AspectJ Compiler

The AspectJ compiler can be used directly by executing the following command:

```
ajc -aspectpath metrics-aspectj.jar [Options] [file...]
```

More information can be found in the [AspectJ compiler / weaver][] documentation.

[AspectJ compiler / weaver]: http://www.eclipse.org/aspectj/doc/next/devguide/ajc-ref.html

#### Required Dependencies

Besides depending on _Metrics_ (`metrics-core` and `metrics-annotation` modules), _Metrics AspectJ_ requires the AspectJ `aspectjrt` module:

```xml
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjrt</artifactId>
</dependency>
```

These three modules are transitive dependencies of the `metrics-aspectj` Maven module.

Alternatively, the `metrics-aspectj-deps` artifact that re-packages the `metrics-annotation` and the `aspectjrt` modules can be used so that the only required dependency is `metrics-core`:

```xml
<dependency>
    <groupId>io.astefanutti.metrics.aspectj</groupId>
    <artifactId>metrics-aspectj-deps</artifactId>
</dependency>
```

#### Optional Dependencies

In addition to that, _Metrics AspectJ_ optional support of EL 3.0 expression for `MetricRegistry` resolution and `Metric` name evaluation requires an implementation of [Expression Language 3.0 (JSR-341)][] to be present at runtime. For example, the [`metrics-aspectj-el`][] module is using the [GlassFish reference implementation][] as `test` dependency for its unit tests execution:

```xml
<dependency>
    <groupId>org.glassfish</groupId>
    <artifactId>javax.el</artifactId>
</dependency>
```

[`metrics-aspectj-el`]: https://github.com/astefanutti/metrics-aspectj/tree/master/envs/el
[GlassFish reference implementation]: https://glassfish.java.net/downloads/ri/

## Usage

#### _Metrics AspectJ_ Activation

In order to activate _Metrics AspectJ_ for a particular class, it must be annotated with the `@Metrics` annotation:

```java
import com.codahale.metrics.annotation.Timed;

import io.astefanutti.metrics.aspectj.Metrics;

@Metrics
class TimedMethod {

    @Timed(name = "timerName")
    void timedMethod() {} // Timer name => TimedMethod.timerName
}
```

At weaving time, _Metrics AspectJ_ will detect the `@Metrics` annotation, scan all the declared methods of the target class that are annotated with _Metrics_ annotations, then create and register the corresponding `Metric` instances and weave its aspects around these methods. At runtime, these `Metric` instances will eventually get called according to the _Metrics_ annotations specification.

Note that _Metrics_ annotations won't be inherited if declared on an interface or a parent class method. More details are available in the [Limitations](#limitations) section.

#### The _Metrics_ Annotations

_Metrics_ comes with the [`metrics-annotation`][] module that contains a set of annotations and provides a standard way to integrate _Metrics_ with frameworks supporting Aspect Oriented Programming (AOP). These annotations are supported by _Metrics AspectJ_ that implements their contract as documented in their Javadoc.

[`metrics-annotation`]: https://github.com/dropwizard/metrics/tree/master/metrics-annotation

For example, a method can be annotated with the `@Timed` annotation so that its execution can be monitored using _Metrics_:

```java
import com.codahale.metrics.annotation.Timed;

import io.astefanutti.metrics.aspectj.Metrics;

@Metrics
class TimedMethod {

    @Timed(name = "timerName")
    void timedMethod() {} // Timer name => TimedMethod.timerName
}
```

In that example, _Metrics AspectJ_ will instrument all the constructors of the `TimedMethod` class by injecting Java bytecode that will automatically create a `Timer` instance with the provided `name` (or retrieve an existing `Timer` with the same `name` already registered in the `MetricRegistry`) right after the instantiation of the `TimedMethod` class and inline the method invocation around with the needed code to time the method execution using that `Timer` instance.

A `static` method can also be annotated with the `@Timed` annotation so that its execution can be monitored using _Metrics_:

```java
import com.codahale.metrics.annotation.Timed;

import io.astefanutti.metrics.aspectj.Metrics;

@Metrics
class TimedMethod {

    @Timed(name = "timerName")
    static void timedStaticMethod() {} // Timer name => TimedMethod.timerName
}
```

In that example, _Metrics AspectJ_ will instrument the `TimedMethod` class so that, when it's loaded, a `Timer` instance with the provided `name` will be created (or an existing `Timer` with the same `name` already registered in the `MetricRegistry` will be retrieved) and inline the method invocation around with the needed code to time the method execution using that `Timer` instance.

Optionally, the `Metric` name can be resolved with an EL expression that evaluates to a `String`:

```java
import com.codahale.metrics.annotation.Timed;

import io.astefanutti.metrics.aspectj.Metrics;

@Metrics
class TimedMethod {

    private long id;

    public long getId() {
        return id;
    }

    @Timed(name = "timerName ${this.id}")
    void timedMethod() {} // Timer name => TimedMethod.timerName <id>
}
```

In that example, _Metrics AspectJ_ will automatically create a `Timer` instance (respectively retrieve an existing `Timer` instance with the same `name` already registered in the `MetricRegistry`) right after the instantiation of the `TimedMethod` class and evaluate the EL expression based on the value of the `id` attribute of that newly created `TimedMethod` instance to name the `Timer` instance (respectively resolve the `Timer` instance registered in the `MetricRegistry`). If the value of the `id` attribute changes over time, the `name` of the `Timer` instance won't be re-evaluated.

Note that these annotations won't be inherited if they are placed on interface or parent class methods. Indeed, according to the Java language specification, non-type annotations are not inherited. It is discussed in more details in the [Limitations](#limitations) section.

#### _Metrics_ Registry Resolution

The `Metrics.registry` annotation attribute provides the way to declare the `MetricRegistry` to register the generated `Metric` instances into. Its value can either be a string literal that identifies a `MetricRegistry` accessible by name from the [`SharedMetricRegistries`][] class or a valid EL expression that evaluates to the registry name or the registry instance. The resultant `MetricRegistry` is used to register the `Metric` instantiated into each time a _Metrics_ annotation is present on that class methods. It defaults to the string literal `metrics-registry`.

The `MetricRegistry` can thus be resolved by name relying on the [`SharedMetricRegistries.getOrCreate(String name)`][] method:

```java
import com.codahale.metrics.annotation.Metered;

import io.astefanutti.metrics.aspectj.Metrics;

@Metrics(registry = "registryName")
class MeteredMethodWithRegistryByName {

    @Metered(name = "meterName")
    void meteredMethod() {} // Registry => SharedMetricRegistries.getOrCreate("registryName")
}
```

Or with an EL expression that evaluates to a bean property of type `MetricRegistry`:

```java
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Metered;

import io.astefanutti.metrics.aspectj.Metrics;

@Metrics(registry = "${this.registry}")
class MeteredMethodWithRegistryFromProperty {

    final MetricRegistry registry;

    MeteredMethodWithRegistryFromProperty(MetricRegistry registry) {
        this.registry = registry;
    }

    MetricRegistry getRegistry() {
        return registry;
    }

    @Metered(name = "meterName")
    void meteredMethod() {} // Registry => this.getRegistry()
}
```

Or with an EL expression that evaluates to a `String`. In that case the registry is resolved by name using the [`SharedMetricRegistries.getOrCreate(String name)`][] method.

[`SharedMetricRegistries.getOrCreate(String name)`]: http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/SharedMetricRegistries.html#getOrCreate%28java.lang.String%29

## Limitations

The _Metrics_ annotations are not inherited whether these are declared on a parent class or an implemented interface method. The root causes of that limitation, according to the Java language specification, are:
+ Non-type annotations are not inherited,
+ Annotations on types are only inherited if they have the `@Inherited` meta-annotation,
+ Annotations on interfaces are not inherited irrespective to having the `@Inherited` meta-annotation.

See the [`@Inherited`][] Javadoc and [Annotation types][] from the Java language specification for more details.

AspectJ follows the Java language specification and has documented to what extent it's impacted in [Annotation inheritance][] and [Annotation inheritance and pointcut matching][]. There would have been ways of working around that though:
+ That would have been working around the Java language specification in the first place,
+ Plus that would have required to rely on a combination of [Expression-based pointcuts][], [Runtime type matching][] and [Reflective access][] to define conditional pointcut expressions which:
    + Would have widen the scope of matching joint points thus introducing side-effects in addition to being inefficient,
    + Would have been evaluated at runtime for each candidate join point relying on the Java Reflection API thus impacting the application performance and incidentally voiding the non-intrusive benefit of AOP in a larger sense.

[`@Inherited`]: http://docs.oracle.com/javase/7/docs/api/java/lang/annotation/Inherited.html
[Annotation types]: http://docs.oracle.com/javase/specs/jls/se7/html/jls-9.html#jls-9.6
[Annotation inheritance]: http://eclipse.org/aspectj/doc/next/adk15notebook/printable.html#annotation-inheritance
[Annotation inheritance and pointcut matching]: http://eclipse.org/aspectj/doc/released/adk15notebook/annotations-pointcuts-and-advice.html#annotation-inheritance-and-pointcut-matching
[Expression-based pointcuts]: http://eclipse.org/aspectj/doc/released/progguide/semantics-pointcuts.html#d0e5549
[Runtime type matching]: http://eclipse.org/aspectj/doc/released/adk15notebook/annotations-pointcuts-and-advice.html#runtime-type-matching-and-context-exposure
[Reflective access]: http://eclipse.org/aspectj/doc/released/progguide/semantics-advice.html#reflective-access-to-the-join-point

## Spring AOP vs. AspectJ

[Spring AOP][] and [AspectJ][] provides Aspect Oriented Programming (AOP) in two very different ways:
+ AspectJ provides a full-fledged aspect definition and support both Compile Time Weaving (CTW) and Load Time Weaving (LTW) (with a Java agent) and implements AOP with class instrumentation (byte code manipulation),
+ Spring AOP does not support the whole AspectJ aspect definition and does not support Compile Time Weaving,
+ Spring AOP implements AOP either using (see [Spring proxying mechanisms][]):
    + JDK dynamic proxies, which add little runtime overhead, clutter stack traces and can be incompatible with other Spring functionality like Spring JMX (for dynamic MBean export for example),
    + Or [CGLIB][] (byte code manipulation), that has to be added as a runtime dependency:
        + It dynamically extends classes thus it is incompatible with `final` classes or methods,
        + CGLIB development isn't active, Hibernate has been deprecating it in favor of [Javassist][] (see [Deprecated CGLIB support][]),
+ [AJDT (AspectJ Development Tools)][AJDT] provides deep integration between AspectJ and the Eclipse platform which is not possible with Spring AOP due to the runtime / dynamic nature of its AOP implementation.

Further details can be found in [Choosing which AOP declaration style to use][] from the Spring framework documentation. The [Spring AOP vs AspectJ][] question on Stack Overflow provides some insights as well.

[Spring AOP]: http://docs.spring.io/spring/docs/4.0.x/spring-framework-reference/html/aop.html
[AspectJ]: http://eclipse.org/aspectj/
[CGLIB]: http://cglib.sourceforge.net/
[Javassist]: http://www.csg.ci.i.u-tokyo.ac.jp/~chiba/javassist/
[AJDT]: http://www.eclipse.org/ajdt/
[Spring proxying mechanisms]: http://docs.spring.io/spring/docs/4.0.x/spring-framework-reference/html/aop.html#aop-proxying
[Deprecated CGLIB support]: http://relation.to/16658.lace
[Choosing which AOP declaration style to use]: http://docs.spring.io/spring/docs/4.0.x/spring-framework-reference/html/aop.html#aop-choosing
[Spring AOP vs AspectJ]: http://stackoverflow.com/questions/1606559/spring-aop-vs-aspectj

## License

Copyright © 2013-2016, Antonin Stefanutti

Published under Apache Software License 2.0, see LICENSE
