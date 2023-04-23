# Rengine for Developoper's Guide

## Directories

- [Developer's for ApiServer](./devel-apiserver.md)

- [Developer's for Executor](./devel-executor.md)

- [Developer's for Controller](./devel-controller.md)

- [Developer's for Job](./devel-job.md)

- [Developer's for Client](./devel-client.md)

## Building

```bash
$ git clone git@github.com/wl4g/rengine.git
$ cd rengine

$ ./run.sh

Rengine devel build tests and release & deploy chore Tools.

# for examples
export JAVA_HOME=/usr/local/jdk-11.0.10/ # Recommands
export MAVEN_OPTS='-Xss64m -Xms1g -Xmx12g -XX:ReservedCodeCacheSize=1g -Dorg.slf4j.simpleLogger.defaultLogLevel=WARN' # Optional
# requirements (if necessary)
export MAVEN_USERNAME='myuser'
export MAVEN_PASSWORD='abc'
export MAVEN_GPG_PRIVATE_KEY='-----BEGIN PGP PRIVATE KEY BLOCK-----\n...'
export MAVEN_GPG_PASSPHRASE='abc'
export DOCKERHUB_REGISTRY='docker.io/wl4g' # eg: docker.io/wl4g(default), ghcr.io/wl4g, registry.cn-shenzhen.aliyuncs.com/wl4g, ccr.ccs.tencentyun.com/wl4g
export DOCKERHUB_USERNAME='myuser'
export DOCKERHUB_TOKEN='abc'

Usage: ./$(basename $0) [OPTIONS] [arg1] [arg2] ...
    version                                         Print maven project POM version.
    gpg-verify                                      Verifying for GPG.
    build-maven                                     Build with Maven.
    build-deploy                                    Build and deploy to Maven central.
    build-image                                     Build component images.
                        -a,--apiserver              Build image for apiserver.
                           --skip-build             Skip recompile build before building image.
                        -c,--controller             Build image for controller.
                           --skip-build             Skip recompile build before building image.
                        -j,--job                    Build image for job.
                           --skip-build             Skip recompile build before building image.
                        -e,--executor               Build image for executor.
                           --skip-build             Skip recompile build before building image.
                        -E,--executor-native        Build image for executor (native).
                           --skip-build             Skip recompile build before building image.
                        -u,--ui                     Build image for UI.
                           --skip-build             Skip recompile build before building image.
                        -d,--initdb                 Build image for initdb.
                           --skip-build             Skip recompile build before building image.
                        -A,--all                    Build image for all components (but excludes the executor-native).
                           --skip-build             Skip recompile build before building image.
    push-image                                      Push component images.
                        -a,--apiserver              Push image for apiserver.
                        -c,--controller             Push image for controller.
                        -j,--job                    Push image for job.
                        -e,--executor               Push image for executor.
                        -E,--executor-native        Push image for executor (native).
                        -u,--ui                     Push image for UI.
                        -d,--initdb                 Push image for initdb.
                        -A,--all                    Push image for all components.
    build-push                                      Build with Maven and push images for all components.
    prune-image                                     Prune unused all images. (tag=none)
    deploy-standalone                               Deploy all services with docker standalone mode.
                        -S,--status                 Display status for all services.
                        -U,--up                     Startup to all services.
                           --prune-all-volumes      Remove all data volumes before per initial deploy. Note: be careful!
                        -D,--down                   Shuwdown to all services.
                           --prune-all-volumes      Remove all data volumes after per destory deploy. Note: be careful!
```
