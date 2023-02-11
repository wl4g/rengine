# Rengine

A real-time, near real-time general rule engine platform, suitable for such as financial risk control, real-time alarm of Iot temperature, online data cleaning and filtering, etc.

## Architectures

- ![Global](./docs/shots/architecture.png)

## Requirements

- JDK 11.x +

- GraalVM java11-22.3 + (Recommands)

- Maven 3.6 +

- Spring Boot 2.7.3 (Spring 5.3.22)

- Quarkus 2.12.2

- Redis Cluster 6 +

- MongoDB 4.4.6 +

- MinIO 2021.x +

- Docker 20.x +

- Kubernetes 1.21 + (Recommands)

- Flink 1.14.4 + (Recommands)

- HBase 2.2.x + (Optional)

## Features

- Supports large-scale MMP parallel near real-time aggregation computing based on Flink/CEP.

- Supports multi-language scripting engine for [Groovy](http://groovy-lang.org/differences.html#_default_imports), [JS(graal.js)](https://www.graalvm.org/22.2/reference-manual/js/FAQ/#what-is-the-difference-between-running-graalvms-javascript-in-native-image-compared-to-the-jvm).

- Supports highly flexible dynamic writing rule templates based on WebIDE.

- Supports WebIDE uploading of custom class libraries and automatically completes code prompts.

- Supports automatic analysis of hit rate reports.

- Support [graalvm native mode](https://www.graalvm.org/22.1/docs/getting-started/#native-image) operation (currently supported services: manager/evaluator/collector, future plans to support: flink jobs).

- Supports connecting to [arthas tunnel](https://arthas.aliyun.com/en/doc/tunnel.html) for easy administrator JVM troubleshooting (**Only in JVM run mode**).

## Quick start

- [Rengine Architecture](./docs/en/architecture.md)

- [Deploy for standalone](./docs/en/deploy-standalone.md)

- [Deploy for production](./docs/en/deploy-production.md)

- [Configuration for Clients](./docs/en/configuration-client.md)

- [Configuration for ApiServer](./docs/en/configuration-apiserver.md)

- [Configuration for Controller](./docs/en/configuration-controller.md)

- [Configuration for Executor](./docs/en/configuration-executor.md)

- [Configuration for Job](./docs/en/configuration-job.md)

- [Benchmark for Executor](./docs/en/benchmark-executor.md)

- [Best Cases examples](./docs/en/best-cases.md)

- [Development Quide](./docs/en/devel.md)

- [Operation Quide](./docs/en/operation.md)

## RoadMap

- [roadmap](./docs/en/roadmap-2022-23.md)
