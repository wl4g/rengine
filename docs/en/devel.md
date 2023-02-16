# Rengine for Development Guide

## Directories

- [Development for Manager](./devel-manager.md)

- [Development for Executor](./devel-evaluator.md)

- [Development for Job](./devel-job.md)

- [Development for Collector](./devel-collector.md)

- [Development for Client](./devel-client.md)

## Building

```bash
git clone git@github.com/wl4g/rengine.git

./tools/build/run.sh

# for examples
export JAVA_VERSION=11 # Optional
export MAVEN_OPTS='-Xss64m -Xms1g -Xmx12g -XX:ReservedCodeCacheSize=1g -Dorg.slf4j.simpleLogger.defaultLogLevel=WARN' # Optional
export MAVEN_USERNAME='myuser'
export MAVEN_PASSWORD='abc'
export DOCKERHUB_USERNAME='myuser'
export DOCKERHUB_TOKEN='abc'

Usage: ./run.sh [OPTIONS] [arg1] [arg2] ...
    version                             Print maven project POM version.
    build-maven                         Build with Maven.
    build-deploy                        Build and deploy to Maven central.
    build-image                         Build component images.
                -a,--apiserver          Build image for apiserver.
                -c,--controller         Build image for controller.
                -e,--executor           Build image for executor.
                -E,--executor-native    Build image for executor (native).
                -A,--all                Build image for all components.
    push-image                          Push component images.
                -a,--apiserver          Push image for apiserver.
                -c,--controller         Push image for controller.
                -e,--executor           Push image for executor.
                -E,--executor-native    Push image for executor (native).
                -A,--all                Push image for all components.
    all                                 Build with Maven and push images for all components.
```
