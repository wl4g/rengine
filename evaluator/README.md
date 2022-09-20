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
-Dquarkus.native.builder-image=quay.io/quarkus/ubi-quarkus-native-image:21.2-java16
```

### Testing request groovy api with run native image

- Generate groovy script to server local path.

```bash
cat <<'EOF' >/tmp/TestFunction.groovy
import java.util.List;
import java.util.function.Function;
class TestFunction implements Function<List<String>, String> {
    @Override
    String apply(List<String> args) {
        System.out.println("Input args: "+ args);
        return "ok, This is the result of the function executed ..";
    }
}
EOF
```

- Request execution grovvy script

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

### Quarkus+Groovy support build native-image mode?

- After a lot of hardships and N times of failed test configuration and construction, I finally successfully built a native image that integrates groovy and can run normally (currently only recommended to run in the test environment), related difficult questions see: [github.com/quarkusio/quarkus/issues/2720](https://github.com/quarkusio/quarkus/issues/2720)

