# Rengine for Deploy Standalone

## Deploy on Docker (compose)

```bash
git clone https://github.com/wl4g/rengine.git

./tools/build/run.sh run-standalone -U --prune-all-volumes
```

## Tests

- [**Optional**] Init HBase table with phoenix. (see: https://github.com/wl4g/docker-hbase)

```bash
docker exec -it rengine_hbase bash

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

- [**Optional**] Init Kafka topic.

```bash
docker exec -it rengine_kafka kafka-topics.sh --zookeeper 127.0.0.1:2181 --create --topic rengine_event --partitions 10 --replication-factor 1
```

- Manual publish events to Kafka

```bash
docker exec -it rengine_kafka kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic rengine_event --property parse.key=true --property key.separator=:

rengine_event:{"source":{"time":1665847350487,"principals":["jameswong1234@gmail.com"],"location":{"ipAddress":"1.1.1.1","ipv6":false,"isp":null,"domain":null,"country":null,"region":null,"city":null,"latitude":null,"longitude":null,"timezone":null,"zipcode":"20500","elevation":null}},"type":"iot_temp_warn","observedTime":1665847350490,"body":{"temp":"52"},"attributes":{}}
```

- Manual subscribe event from Kafka

```bash
docker exec -it rengine_kafka kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic rengine_event
```
