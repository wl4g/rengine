# Rengine for Deploy Standalone

## Deploy on Docker (compose)

```bash
git clone https://github.com/wl4g/rengine.git
cd rengine
./run.sh run-standalone -U --prune-all-volumes
```

## Initial

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

## Run examples

- [User examples](./user-examples/user-examples.md)
