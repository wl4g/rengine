# Rengine for Deploy Standalone

## 1. Deploy on Docker (compose)

```bash
git clone https://github.com/wl4g/rengine.git
cd tools/deploy/compose
docker-compose up -d
```

## 2. Deploy on Docker (manual, step-to-step)

- Deploy standalone HBase

```bash
mkdir -p /mnt/disk1/log/hbase-standalone/ # Logs directory
mkdir -p /mnt/disk1/hbase-standalone/data/ # Temporary data directory

docker run -d \
--name hbase1 \
--network host \
-v /mnt/disk1/hbase-standalone/data/:/tmp/ \
-v /mnt/disk1/log/hbase-standalone/:/opt/apps/ecm/hbase/logs/ \
wl4g/hbase:hbase-2.1.0-phoenix-5.1.1 \
/bin/sh -c "hbase-daemon.sh start master; tail -f /dev/null"
```

- Deploy standalone Kafka

```bash
docker run -d --name kafka1 --network host -e ALLOW_PLAINTEXT_LISTENER=yes bitnami/kafka:2.2.0
```

- Deploy standalone Flink(optional, for session mode)

```bash
docker run -d --name flink-jm1 --network host flink:1.14.4-scala_2.11-java11 jobmanager
docker run -d --name flink-tm1 --network host flink:1.14.4-scala_2.11-java11 taskmanager
```

- Browser accessing: http://localhost:8081

- Deploy standalone Rengine ApiServer

```bash
docker run -d \
--name=rengine-apiserver \
--network=host \
--restart=no \
-e SPRING_HIKARI_JDBCURL='jdbc:mysql://127.0.0.1:3306/rengine?useunicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false' \
-e SPRING_DATA_MONGODB_URI='mongodb://127.0.0.1:27017/rengine' \
-e MINIO_ENDPOINT='http://127.0.0.1:19000' \
-e MINIO_TENANTACCESSKEY='rengine' \
-e MINIO_TENANTSECRETKEY='12345678' \
wl4g/rengine-apiserver
```

- Deploy standalone Rengine Executor

```bash
docker run -d \
--name=rengine-evaluator \
--network=host \
--restart=no \
-e QUARKUS_HTTP_PORT="28002" \
-e QUARKUS_MONGODB_CONNECTION_STRING="mongodb://localhost:27017" \
-e MINIO_ENDPOINT="http://localhost:9000" \
-e MINIO_TENANTACCESSKEY="rengine" \
-e MINIO_TENANTSECRETKEY="12345678" \
wl4g/rengine-evaluator-native
```

## Initial & Testing

- Init HBase table with phoenix. (see: https://github.com/wl4g/docker-hbase)

```bash
docker exec -it hbase1 bash

sqlline.py

CREATE SCHEMA IF NOT EXISTS "rengine";

CREATE TABLE IF NOT EXISTS "rengine"."t_ods_event" (
    "ROW" VARCHAR PRIMARY KEY,
    "info"."observedTime" VARCHAR(20),
    "info"."body" VARCHAR(1024),
    "info"."sourceTime" VARCHAR(20),
    "info"."sourcePrincipals" VARCHAR(128),
    "info"."locationIpAddress" VARCHAR(32),
    "info"."locationIpv6" VARCHAR(32),
    "info"."locationIsp" VARCHAR(16),
    "info"."locationDomain" VARCHAR(32),
    "info"."locationElevation" VARCHAR(32),
    "info"."locationLatitude" VARCHAR(15),
    "info"."locationLongitude" VARCHAR(16),
    "info"."locationZipcode" VARCHAR(16),
    "info"."locationTimezone" VARCHAR(8),
    "info"."locationCity" VARCHAR(8),
    "info"."locationRegion" VARCHAR(8),
    "info"."locationCountry" VARCHAR(2),
    "info"."attributes" VARCHAR(1024)
) COLUMN_ENCODED_BYTES=0;

!describe "rengine"."t_ods_event";

select * from "rengine"."t_ods_event" limit 10;
```

- Init Kafka topic.

```bash
docker exec -it kafka1 kafka-topics.sh --zookeeper 127.0.0.1:2181 --create --topic rengine_event --partitions 10 --replication-factor 1
```

- Manual publish events to Kafka

```bash
docker exec -it kafka1 kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic rengine_event --property parse.key=true --property key.separator=:

rengine_event:{"source":{"time":1665847350487,"principals":["jameswong1234@gmail.com"],"location":{"ipAddress":"1.1.1.1","ipv6":false,"isp":null,"domain":null,"country":null,"region":null,"city":null,"latitude":null,"longitude":null,"timezone":null,"zipcode":"20500","elevation":null}},"type":"iot_temp_warn","observedTime":1665847350490,"body":"52","attributes":{}}
```

- Manual subscribe event from Kafka

```bash
docker exec -it kafka1 kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic rengine_event
```
