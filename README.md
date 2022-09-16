# Rengine

A generic real-time rule engine, such as bank transfer real-time risk control, temperature real-time alarm.

## Architectures

- ![Global](docs/shots/rengine_architecture.png)

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

### Deploy on Docker compose

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

- Rengine Manager build

```bash
git clone git@github.com/wl4g/rengine.git
cd rengine
./mvnw clean install -DskipTests -Dmaven.test.skip=true -T 2C
```

- [Rengine Evaluator build here](evaluator/README.md)

## Operation Guide

### Manual Telemetry Troubleshooting

- Health

```bash
curl -v localhost:28001/actuator/health
```

- Metrics

```bash
curl -v localhost:28001/actuator/prometheus
```

### Deploy OTel collector

```bash
# Download collector configuration.
sudo mkdir -p /etc/otel
curl -L -o /etc/otel/collector.yaml 'https://raw.githubusercontent.com/wl4g/rengine/master/tools/operation/otel/collector.yaml'

# Run OTel collector.
docker run -d \
--name=otel-collector1 \
--network=host \
--restart=no \
-v /etc/otel/collector.yaml:/etc/otelcol/config.yaml \
otel/opentelemetry-collector:0.60.0
```

## FAQ

TODO
