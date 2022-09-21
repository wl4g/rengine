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
  "service": "collector",
  "attachment": {}
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

### Manual testing Groovy as a dynamic script execution engine?

- Source codes see: [TestGroovyResource.java](src/main/java/com/wl4g/rengine/evaluator/rest/TestGroovyResource.java)

- Generate testing script to local path.

```bash
curl -L -o /tmp/test.groovy 'https://raw.githubusercontent.com/wl4g/rengine/master/evaluator/testdata/test.groovy'
```

- Mocking request execution

```bash
curl -v -XPOST -H 'Content-Type: application/json' 'http://localhost:28002/test/groovy/execution' -d '{
    "scriptPath": "file:///tmp/test.groovy",
    "args": ["jack01", "66"]
}'
```

- Tail logs

```bash
tail -f /tmp/rengine/evaluator.log | jq -r '.message'
```

### Manual testing Javascript as a dynamic script execution engine?

- Source codes see: [TestJavascriptResource.java](src/main/java/com/wl4g/rengine/evaluator/rest/TestJavascriptResource.java)

- Generate testing script to local path.

```bash
curl -L -o /tmp/test.js 'https://raw.githubusercontent.com/wl4g/rengine/master/evaluator/testdata/test.js'
```

- Mocking request execution

```bash
curl -v -XPOST -H 'Content-Type: application/json' 'http://localhost:28002/test/javascript/execution' -d '{
    "scriptPath": "file:///tmp/test.js",
    "scriptMain": "primesMain",
    "scriptEngine": "graal.js",
    "args": []
}'
```

- Tail logs

```bash
tail -f /tmp/rengine/evaluator.log | jq -r '.message'
```

### What are the notice with using the quarkus+groovy scripting engine?

- ***Limitations***: Groovy is not a first class citizen for GraalVM’s ahead-of-time compilation by design, and that is why you can’t expect that your Groovy program will compile to the native image successfully. Below is the list of the major limitations that cannot be avoided: GraalVM’s SubstrateVM does not support dynamic class loading, dynamic class generation, and bytecode InvokeDynamic. This limitation makes dynamic Groovy scripts and classes almost 99% incompatible with building native images, So if you want to run on the native image, you can only open the static compilation mode of groovy. see: [https://e.printstacktrace.blog/graalvm-and-groovy-how-to-start/](https://e.printstacktrace.blog/graalvm-and-groovy-how-to-start/)

- Related difficult refer to see: [github.com/quarkusio/quarkus/issues/2720](https://github.com/quarkusio/quarkus/issues/2720)

