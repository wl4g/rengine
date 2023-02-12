# Development guide for Rengine Executor

## Build for JAR

- First fully build the dependent modules.

```bash
git clone git@github.com/wl4g/rengine.git
cd rengine/executor
export JAVA_HOME=/usr/local/jdk-11.0.10/
./mvnw clean install -DskipTests -Dmaven.test.skip=true -U -T 4C
```

## Build for native image

- Then build as a native image.

- [Quarkus CLI](https://quarkus.io/guides/cli-tooling))

- [Quarkus building-native-image#container-runtime](https://quarkus.io/guides/building-native-image#container-runtime)

- **Notice:** As of `GraalVM 22.2` the `language:js` plugin has been removed from the default plugin list, and needs to be installed manually by run `$GRAALVM_HOME/bin/gu install js`

- **Notice:** It is recommended that the build host has a memory larger than 6G~8G, because building native requires a lot of memory to pre-run analysis methods, and the first build may take 15-20min (due to the need to download the build image), please wait patiently, and the subsequent build will take about 5-10min.

```bash
export JAVA_HOME=/usr/local/jdk-11.0.10/ # Must java11+
./mvnw clean install -DskipTests -Dmaven.test.skip=true -U -T 4C

./mvnw package -f executor/pom.xml \
-Dmaven.test.skip=true \
-DskipTests \
-Dnative \
-Dquarkus.native.container-build=true \
-Dquarkus.native.container-runtime=docker
```

## Build for container(native) image

- Case1: Automatic build with quarkus plugin. [quarkus.io/guides/container-image#building](https://quarkus.io/guides/container-image#building)

```bash
export JAVA_HOME=/usr/local/jdk-11.0.10/ # Must java11+
./mvnw clean install -DskipTests -Dmaven.test.skip=true -U -T 4C

./mvnw package -f executor/pom.xml \
-Dmaven.test.skip=true \
-DskipTests \
-Dnative \
-Dquarkus.native.container-build=true \
-Dquarkus.native.container-runtime=docker \
-Dquarkus.container-image.build=true
```

- Case2: Build with raw commands.

```bash
docker build -f build/docker/Dockerfile.jvm -t wl4g/rengine-executor .
docker build -f build/docker/Dockerfile.native -t wl4g/rengine-executor .
```

## Build for container(JVM) image

```bash
./mvnw package -f executor/pom.xml \
-Dmaven.test.skip=true \
-DskipTests \
-Dquarkus.container-image.build=true
```

## Manual telemetry

- Health

```bash
curl -v localhost:28002/healthz
curl -v localhost:28002/healthz/live
curl -v localhost:28002/healthz/ready
curl -v localhost:28002/healthz/started
```

- Metrics

```bash
curl -v localhost:28002/metrics
```

- Testing for API execute. [EngineExecutionEndpoint.java](../../executor/src/main/java/com/wl4g/rengine/executor/rest/EngineExecutionEndpoint.java)

```bash
curl -v -XPOST \
-H 'Content-Type: application/json' \
'localhost:28002/execute/internal/workflow' \
-d '{
  "requestId": "b9bc3e0e-d705-4ff2-9edf-970dcf95dea5",
  "clientId": "JVqEpEwIaqkEkeD5",
  "clientSecret": "Uf6nJDyJQHKRP43ycl9vZ9zs7s1nyu77",
  "scenesCodes": ["ecommerce_trade_gift"],
  "timeout": 3000,
  "bestEffort": true,
  "args": {
    "userId": "u10010101",
    "foo": "bar"
  }
}'

- Testing for API execute/custom (POST)

```bash
curl -v -XPOST \
-H 'Content-Type: application/json' \
-d '{
  "requestId": "b9bc3e0e-d705-4ff2-9edf-970dcf95dea5",
  "clientId": "JVqEpEwIaqkEkeD5",
  "clientSecret": "Uf6nJDyJQHKRP43ycl9vZ9zs7s1nyu77",
  "scenesCodes": ["ecommerce_trade_gift"],
  "timeout": 3000,
  "bestEffort": true,
  "args": {
    "userId": "u10010101",
    "foo": "bar"
  }
}' 'localhost:28002/execute/custom?reqSettings=eyJmb28iOiJiYXIifQo=&respSettings=eyJ0ZW1wbGF0ZUtleSI6ImRpbmd0YWxrIn0K' \
```

- Testing for API execute/custom (GET)

```bash
#echo '{"foo":"bar"}' | base64 -w 999 # => eyJmb28iOiJiYXIifQo=

curl -v "http://localhost:28002/execute/custom?\
clientId=JVqEpEwIaqkEkeD5\
&clientSecret=Uf6nJDyJQHKRP43ycl9vZ9zs7s1nyu77\
&scenesCodes=dingtalk_event_callback\
&bestEffort=true\
&timeout=60000\
&args=eyJmb28iOiJiYXIifQo=\
&reqSettings=eyJmb28iOiJiYXIifQo=\
&respSettings=eyJ0ZW1wbGF0ZUtleSI6ImRpbmd0YWxrIn0K"
```

- [More Configuration: quarkus.io/guides/all-config](https://quarkus.io/guides/all-config)

- [Native Troubleshooting: quarkus.io/guides/native-reference#profiling](https://quarkus.io/guides/native-reference#profiling)

## FAQ

### Mock testing dynamic `groovy` script execution?

- Source codes see: [HelloGroovyEndpoint.java](../../executor/src/main/java/com/wl4g/rengine/executor/rest/hello/HelloGroovyEndpoint.java)

- Generate testing script to local path.

```bash
curl -L -o /tmp/test.groovy 'https://raw.githubusercontent.com/wl4g/rengine/master/executor/testdata/scripts/test.groovy'
```

- Run native

```bash
./executor/target/rengine-executor-native -Dtest.rest=true
```

- Mocking request execution

```bash
curl -v -XPOST -H 'Content-Type: application/json' 'http://localhost:28002/hello/groovy/execute' -d '{
    "scriptPath": "file:///tmp/test.groovy",
    "args": ["jack01", "66"]
}'
```

- Tail logs

```bash
tail -f /tmp/rengine/executor.log | jq -r '.message'
```

### Mock testing dynamic `js` script execution?

- Source codes see: [HelloGraalJSEndpoint.java](../../executor/src/main/java/com/wl4g/rengine/executor/rest/hello/HelloGraalJSEndpoint.java)

- Generate testing script to local path.

```bash
curl -L -o /tmp/test-js2java.js 'https://raw.githubusercontent.com/wl4g/rengine/master/executor/testdata/scripts/test-sdk-all-examples.js'
```

- Run native

```bash
./executor/target/rengine-executor-native -Dtest.rest=true
```

- Mocking request execution

```bash
curl -v -XPOST -H 'Content-Type: application/json' 'http://localhost:28002/hello/graaljs/execute' -d '{
    "scriptPath": "file:///tmp/test-js2java.js",
    "args": ["jack01", "66"]
}'
```

- Tail logs

```bash
tail -f /tmp/rengine/executor.log | jq -r '.message'
```

### Use the Groovy execution engine support native executable mode ?

- Currently not supported.

- ***Limitations***: Groovy is not a first class citizen for GraalVM’s ahead-of-time compilation by design, and that is why you can’t expect that your Groovy program will compile to the native image successfully. Below is the list of the major limitations that cannot be avoided: GraalVM’s SubstrateVM does not support dynamic class loading, dynamic class generation, and bytecode InvokeDynamic. This limitation makes dynamic Groovy scripts and classes almost 99% incompatible with building native images, So if you want to run on the native image, you can only open the static compilation mode of groovy. see: [https://e.printstacktrace.blog/graalvm-and-groovy-how-to-start/](https://e.printstacktrace.blog/graalvm-and-groovy-how-to-start/)

- Related difficult refer to see: [github.com/quarkusio/quarkus/issues/2720](https://github.com/quarkusio/quarkus/issues/2720)


### Use the JS execution engine support JVM mode and native executable mode?

- Yes, both are supported, thanks to GraalVM's multi-language support, but the --language:js option must be added when building native, and the version is best to use `GraalVM 22.1`, because the js plugin has been removed by default from `GraalVM 22.2`

- ***Limitations***:
  - a. Multiple threads are not allowed to call the same js script context.
  - b. The native runtime does not support interactive calls to java methods

## FAQ

### If you use the groovy environment, pay attention to the version situation between the components
  - 1. The **groovy-4.0.5**(current latest) that the rengine-evaluator module depends on only supports **jdk1.8/9/10/16**
  - 2. The **spring-native-0.12.1*** (current latest) that the rengine-manager module depends on only supports jdk11+
  - 3. Refer source code: [github.com/apache/groovy/blob/GROOVY_4_0_5/src/main/java/org/codehaus/groovy/vmplugin/VMPluginFactory.java#L39](https://github.com/apache/groovy/blob/GROOVY_4_0_5/src/main/java/org/codehaus/groovy/vmplugin/VMPluginFactory.java#L39)
