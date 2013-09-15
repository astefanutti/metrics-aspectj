metrics-aspectj
===============

AspectJ integration for [Yammer's Metrics](http://metrics.codahale.com/)
with [Expression Language 3.0 (JSR-341)](http://jcp.org/en/jsr/detail?id=341) support.

## Getting Started

### Setting Up Maven

Add the `metrics-library` library as a dependency:
```xml
<dependencies>
    <dependency>
        <groupId>fr.stefanutti</groupId>
        <artifactId>metrics-aspectj</artifactId>
        <version>${metrics.aspectj.version}</version>
    </dependency>
</dependencies>
```
And configure the `maven-aspectj-plugin` to weave the `metrics-aspectj` aspects into your project:
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
### Basic Usage

```java
import com.codahale.metrics.annotation.Timed;
import fr.stefanutti.metrics.aspectj.Metrics;

@Metrics(registry = "'registryName'")
public class SingleTimedMethod {

    @Timed(name = "'timerName'")
    public void timedMethod() {
    }
}
```