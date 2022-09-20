# Rengine Evaluator

## Quick Start

### Deploy on Docker

```bash
docker run -d \
--name=rengine-evaluator1 \
--network=host \
--restart=no \
-e QUARKUS_HTTP_PORT="8080" \
-e QUARKUS_MONGODB_CONNECTION_STRING="mongodb://localhost:27017" \
-e QUARKUS_EXTENSION_MINIO_ENDPOINT="http://localhost:9000" \
-e QUARKUS_EXTENSION_MINIO_TENANTACCESSKEY="rengine" \
-e QUARKUS_EXTENSION_MINIO_TENANTSECRETKEY="12345678" \
wl4g/rengine-evaluator
```

- Manual evaluating testing

```bash
curl -v -XPOST \
-H 'Content-Type: application/json' \
'localhost:28002/evaluator/evaluate' \
-d '{
  "scenes": "iot_warn",
  "service": "collector"
}'
```

- [More Configuration: quarkus.io/guides/all-config](https://quarkus.io/guides/all-config)

## Development Guide

### Build for JVM

- First fully build the dependent modules.

```bash
git clone git@github.com/wl4g/rengine.git
cd rengine

./mvnw clean install -DskipTests -Dmaven.test.skip=true -T 2C
```

### Build for native image

- Then build as a native image.

- [Quarkus CLI](https://quarkus.io/guides/cli-tooling))

- [Quarkus building-native-image#container-runtime](https://quarkus.io/guides/building-native-image#container-runtime)

```bash
# Should use java11+
export JAVA_HOME=/usr/local/jdk-11.0.10/
./mvnw package -f evaluator/pom.xml \
-Dmaven.test.skip=true -DskipTests -Dnative \
-Dquarkus.native.container-build=true \
-Dquarkus.native.container-runtime=docker \
-Dquarkus.native.builder-image=quay.io/quarkus/ubi-quarkus-native-image:22.2-java11
```

### Build for container image

- Then build as a container image.

```bash
docker build -f build/docker/Dockerfile.jvm -t wl4g/rengine-evaluator .
docker build -f build/docker/Dockerfile.native -t wl4g/rengine-evaluator .
```

## Operation Guide

### Manual Telemetry Troubleshooting

- Health

```bash
curl -v localhost:28002/healthz/live
curl -v localhost:28002/healthz/ready
curl -v localhost:28002/healthz/started
```

- Metrics

```bash
curl -v localhost:28002/metrics
```

### Native Troubleshooting

- https://quarkus.io/guides/native-reference#profiling

## FAQ

### Want to try integrating groovy as a dynamic script execution engine?

- Open [./pom.xml](./pom.xml) groovy dependencies.

```xml
<!-- START: Integration for Groovy -->
<dependency>
    <groupId>org.apache.groovy</groupId>
    <artifactId>groovy</artifactId>
</dependency>
<!-- fix: native build error of: org.codehaus.groovy.control.SourceUnit is registered for linking at image build time by command line ...
caused by: org.codehaus.groovy.control.XStreamUtils.serialze(String,Object) not found? see:https://github.com/quarkusio/quarkus/issues/2720
and see:https://github.com/apache/groovy/blob/GROOVY_4_0_5/build.gradle#L98 -->
<dependency>
    <groupId>com.thoughtworks.xstream</groupId>
    <artifactId>xstream</artifactId>
    <version>1.4.19</version>
    <exclusions>
        <exclusion>
            <groupId>xpp3</groupId>
            <artifactId>xpp3_min</artifactId>
        </exclusion>
        <exclusion>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
        </exclusion>
        <exclusion>
            <groupId>jmock</groupId>
            <artifactId>jmock</artifactId>
        </exclusion>
        <exclusion>
            <groupId>xmlpull</groupId>
            <artifactId>xmlpull</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<!-- fix: native build error of: NoClassDefFoundError: jnr/unixsocket/Unix Socket -->
<dependency>
    <groupId>com.github.jnr</groupId>
    <artifactId>jnr-unixsocket</artifactId>
    <version>0.18</version>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
<!-- END: Integration for Groovy -->
```

- Open [TestGroovyResource.java](src/main/java/com/wl4g/rengine/evaluator/rest/TestGroovyResource.java) restful type.

- Build to native image.

```bash
# Should use java11+
export JAVA_HOME=/usr/local/jdk-11.0.10/
./mvnw package -f evaluator/pom.xml \
-Dmaven.test.skip=true -DskipTests -Dnative \
-Dquarkus.native.container-build=true \
-Dquarkus.native.container-runtime=docker \
-Dquarkus.native.builder-image=quay.io/quarkus/ubi-quarkus-native-image:21.2-java16
```

- Run native image.

```bash
./target/rengine-evaluator-native
```

- Generate testing groovy script to local path.

```bash
cat <<'EOF' >/tmp/TestFunction.groovy
import java.util.List;
import java.util.function.Function;
class TestFunction implements Function<List<String>, String> {
    String apply(List<String> args) {
        System.out.println("Input args: "+ args);
        return "ok, This is the result of the function executed ..";
    }
}
EOF
```

- Mocking request execution

```bash
curl -v -XPOST -H 'Content-Type: application/json' 'http://localhost:28002/test/groovy/execution' -d '{
    "scriptPath": "file:///tmp/TestFunction.groovy",
    "args": ["jack01", "66"]
}'
```

- Tail logs

```bash
tail -f /tmp/rengine/evaluator.log | jq -r '.message'
```

### What are the problems if I want to integrate quarkus+groovy and build a native image?

- ***Limitations***: Groovy is not a first class citizen for GraalVM’s ahead-of-time compilation by design, and that is why you can’t expect that your Groovy program will compile to the native image successfully. Below is the list of the major limitations that cannot be avoided: GraalVM’s SubstrateVM does not support dynamic class loading, dynamic class generation, and bytecode InvokeDynamic. This limitation makes dynamic Groovy scripts and classes almost 99% incompatible with building native images, So if you want to run on the native image, you can only open the static compilation mode of groovy. see: [https://e.printstacktrace.blog/graalvm-and-groovy-how-to-start/](https://e.printstacktrace.blog/graalvm-and-groovy-how-to-start/)

- Related difficult refer to see: [github.com/quarkusio/quarkus/issues/2720](https://github.com/quarkusio/quarkus/issues/2720)

