# Rengine

Unified and flexible rules engine platform, naturally suitable for scenarios where rules change frequently, such as real-time or near-real-time financial risk control, e-commerce promotion rules, operation and maintenance monitoring, IoT device alarms, online data cleaning and filtering, etc.

[![Build on Push](https://github.com/wl4g/rengine/actions/workflows/build_on_push.yaml/badge.svg)](https://github.com/wl4g/rengine/actions/workflows/build_on_push.yaml)
[![Build on Timing](https://github.com/wl4g/rengine/actions/workflows/build_on_timing.yaml/badge.svg)](https://github.com/wl4g/rengine/actions/workflows/build_on_timing.yaml)
[![Release on Push](https://github.com/wl4g/rengine/actions/workflows/release_on_push.yaml/badge.svg)](https://github.com/wl4g/rengine/actions/workflows/release_on_push.yaml)
[![License](https://img.shields.io/badge/license-Apache2.0+-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![GraalVM](https://img.shields.io/badge/GraalVM-22.1-green)](https://github.com/wl4g/rengine)
[![JVM](https://img.shields.io/badge/JVM-8%20and%2011%2B-green)](https://github.com/wl4g/rengine)
[![Mongo](https://img.shields.io/badge/Mongo-4.4.6%2B-green)](https://github.com/wl4g/rengine)
[![Redis](https://img.shields.io/badge/Redis%20Cluster-6%2B-green)](https://github.com/wl4g/rengine)
[![Kafka](https://img.shields.io/badge/Kafka-2%2B-green)](https://github.com/wl4g/rengine)
[![Zookeeper](https://img.shields.io/badge/Zookeeper-3.6.2%2B-green)](https://github.com/wl4g/rengine)
[![Docker](https://img.shields.io/badge/Docker-20%2B-green)](https://github.com/wl4g/rengine)
[![GithubStars](https://img.shields.io/github/stars/wl4g/rengine)](https://github.com/wl4g/rengine)
[![Dingtalk](https://img.shields.io/badge/Dingtalk%20Chat-22890022635-green)](https://qr.dingtalk.com/action/joingroup?code=v1,k1,0tSHdtPe4bTaPpynsi88zKoaPmEJCK+eb04bQzebp/E=&_dt_no_comment=1&origin=11)

## Architectures

- ![Global](./docs/shots/architecture.png)

## Requirements

- JDK 11.x +

- GraalVM java11-22.1 + (If the needs build executor native image)

- Maven 3.6 +

- Spring Boot 2.7.3 (Spring 5.3.22)

- Quarkus 2.12.2

- Redis Cluster 6 +

- Mongo 4.4.6 +

- MinIO 2021.x +

- Docker 20.x +

- Kubernetes 1.21 + (If the needs deploy to kubernetes)

- Flink 1.16.x + (If the need to run the Flink CEP job to process massive events. **Notice:** [Must >= 1.16+ can support window restrictions](https://github.com/apache/flink/blob/release-1.16/flink-libraries/flink-cep/src/main/java/org/apache/flink/cep/pattern/Quantifier.java#L193))

- HBase 2.2.x + (Optional, If the needs to store raw events so that can trace back.)

## Features

- Support online coding development rules models, and upload the custom dependency script libraries.

- Support [Executor](docs/en/devel-executor.md) run by native mode running, Get a hundred times faster startup time (milliseconds) than the traditional JVM, which supports elastic and fast startup under the faas/serverless architecture very well. see: [native-image](https://www.graalvm.org/22.1/docs/getting-started/#native-image)

- Support [Executor](docs/en/devel-executor.md) built-in multi SDKs(eg: **common,tools,datasource,notifier**), such as datasource sdk, which can be connected to multiple data-source instances by configuration. More built-in script SDKs to see: [devel-executor-scriptsdk.md](docs/en/devel-executor-scriptsdk.md)

- Support [Executor](docs/en/devel-executor.md) [JS(graal.js)](https://www.graalvm.org/22.2/reference-manual/js/FAQ/#what-is-the-difference-between-running-graalvms-javascript-in-native-image-compared-to-the-jvm)(**default**), [Python](https://www.graalvm.org/22.2/reference-manual/python/Interoperability/)(**beta**), [R](https://www.graalvm.org/22.2/reference-manual/r/Interoperability/)(**beta**), [Ruby](https://www.graalvm.org/22.2/reference-manual/ruby/Interoperability/)(**beta**), [Groovy](http://groovy-lang.org/differences.html#_default_imports)(**planning**), etc. Executor supports the unified orchestration and execution of multiple scripting language engines. This is an epoch-making technological revolution, thanks to the powerful multi language runtime of graal.

- Support [Controller](docs/en/devel-controller.md) actively scheduling invoke workflow graph.

- Support [Controller](docs/en/devel-controller.md), [ApiServer](docs/en/devel-apiserver.md) register connecting to [arthas tunnel](https://arthas.aliyun.com/en/doc/tunnel.html) for easy operator JVM troubleshooting (**Only in JVM run mode**).

- Support [Job](docs/en/devel-job.md) large-scale MMP parallel real-time state recognition based on Flink CEP.

- Support [ApiServer](docs/en/devel-apiserver.md) static password login and standard OAuth2/OIDC authentication (the configure multiple), such as [keycloak](https://www.keycloak.org/), [github](https://github.com/), etc.

- Support the multi-tenancy of based on kubernetes operator (resource shared and isolation), see: [rengine-operator](../../../rengine-operator) (**progressing**).

- Support automatic analysis of hit rate reports (**progressing**).

## Quick start

- [Architecture](./docs/en/architecture.md)

- [User examples](./docs/en/user-examples/user-examples.md)

- [Deploy for standalone](./docs/en/deploy-standalone.md)

- [Deploy for production](./docs/en/deploy-production.md)

- [Configuration for client](./docs/en/configuration-client.md)

- [Configuration for apiserver](./docs/en/configuration-apiserver.md)

- [Configuration for controller](./docs/en/configuration-controller.md)

- [Configuration for executor](./docs/en/configuration-executor.md)

- [Configuration for jobs](./docs/en/configuration-job.md)

- [Developer's quide](./docs/en/devel.md)

- [Operation's quide](./docs/en/operation.md)

- [Benchmark for executor](./docs/en/benchmark-executor.md)

## RoadMap

- [roadmap](./docs/en/roadmap-2022-23.md)
