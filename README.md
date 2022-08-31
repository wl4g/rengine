# Rengine

A generic real-time rule engine, such as bank transfer real-time risk control, temperature real-time alarm.

## Requirements

- JDK 11.x +

- Maven 3.6 +

- GraalVM-java11-22.1.0 + (If Necessary)

- MySQL 5.7 + (If Necessary)

- MongoDB 4.x + (If Necessary)

- MinIO 2021.x + (If Necessary)

- Flink 1.14.4 + (If Necessary)

- HBase 2.2.x + (If Necessary)

- HDFS 3.0.x + (If Necessary)

- Docker 20.x + (If Necessary)

- Kubernetes 1.21 + (If Necessary)

## Features

- Support large-scale MMP parallel computing based on FLINK-CEP.

- Supports highly flexible dynamic writing rule templates based on WEBIDE.

- Supports WebIDE uploading of custom class libraries and automatically completes code prompts.

- Supports automatic analysis of hit rate reports.

## Quick Start

- Deploy on Docker

```bash
git clone https://github.com/wl4g/rengine.git
cd tools/deploy/compose
docker-compose up -d
```

- Deploy on Kubernetes with Helm

```bash
# TODO
```

## Development Guide

TODO

## Operation Guide

TODO

## FAQ

TODO
