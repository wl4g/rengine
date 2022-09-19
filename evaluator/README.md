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

### build for JVM

- First fully build the dependent modules.

```bash
git clone git@github.com/wl4g/rengine.git
cd rengine

./mvnw clean install -DskipTests -Dmaven.test.skip=true -T 2C
```

### build for native image

- Then build as a native image.

- [Quarkus CLI](https://quarkus.io/guides/cli-tooling))

- [Quarkus building-native-image#container-runtime](https://quarkus.io/guides/building-native-image#container-runtime)

```bash
export JAVA_HOME=/usr/local/jdk-11.0.10/
../mvnw package -f evaluator/pom.xml \
-Dnative \
-Dquarkus.native.container-build=true \
-Dquarkus.native.container-runtime=docker
```

### build for container image

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

### If you are a developer who wants to change the JDK and other dependencies versions?

- Yes, but please notice: that the dependent **groovy-4.0.5** only supports **jdk8/9/10/16**, so you must use a version-compatible quarkus-graalvm build environment, which currently passes the tested The build images are: **quay.io/quarkus/ubi-quarkus-native-image:22.2-java17**

- Refer source code: [github.com/apache/groovy/blob/GROOVY_4_0_5/src/main/java/org/codehaus/groovy/vmplugin/VMPluginFactory.java#L39](https://github.com/apache/groovy/blob/GROOVY_4_0_5/src/main/java/org/codehaus/groovy/vmplugin/VMPluginFactory.java#L39)
