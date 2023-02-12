# Rengine

> Unified and flexible rules engine platform, naturally suitable for scenarios where rules change frequently, such as real-time or near-real-time financial risk control, e-commerce promotion rules, operation and maintenance monitoring, IoT device alarms, online data cleaning and filtering, etc.

[![Build](https://github.com/wl4g/rengine/actions/workflows/build_on_any.yaml/badge.svg)](https://github.com/wl4g/rengine/actions/workflows/build_on_any.yaml)
[![License](https://img.shields.io/badge/license-Apache2.0+-green.svg)
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
