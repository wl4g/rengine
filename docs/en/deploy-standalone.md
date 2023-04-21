# Rengine for Deploy Standalone

## Deploy on Docker (compose)

```bash
git clone https://github.com/wl4g/rengine.git
cd rengine

$ ./run.sh run-standalone -U --prune-all-volumes

$ ./run.sh run-standalone -S
f0779f017ff1   rengine_controller        wl4g/rengine-controller:1.0.0                                     Up 2 minutes
0e7e01ede490   rengine_ui                wl4g/rengine-ui:1.0.0                                             Up 2 minutes
c62284e09d59   rengine_apiserver         wl4g/rengine-apiserver:1.0.0                                      Up 2 minutes
7dc3d38ca03c   rengine_mongodb_express   mongo-express:0.54.0                                              Up 2 minutes
ecccb6bf5bab   rengine_executor          wl4g/rengine-executor-native:1.0.0                                Up 2 minutes
5a85d28db4e0   rengine_mongodb           bitnami/mongodb:4.4.6                                             Up 2 minutes
6bba5cfb1b0c   rengine_job_tm_default    wl4g/rengine-job:1.0.0                                            Up 2 minutes
66b5df0e5cd3   rengine_job_jm_default    wl4g/rengine-job:1.0.0                                            Up 2 minutes
935c57e75ea7   rengine_kafka_manager     registry.cn-shenzhen.aliyuncs.com/wl4g/kafka-manager:v3.0.0.6-2   Up 2 minutes
74f2e3edaf76   rengine_kafka             bitnami/kafka:2.2.0                                               Up 2 minutes
d5344bed2674   rengine_redis_node_5      bitnami/redis-cluster:7.0                                         Up 2 minutes
cd48ed90c734   rengine_hbase             wl4g/hbase:hbase-2.1.0-phoenix-5.1.1                              Up 2 minutes
828cd427c341   rengine_redis_node_3      bitnami/redis-cluster:7.0                                         Up 2 minutes
9f3fae3d4b15   rengine_zookeeper         bitnami/zookeeper:3.6.2                                           Up 2 minutes
03a0b3f1b15f   rengine_redis_node_4      bitnami/redis-cluster:7.0                                         Up 2 minutes
6341acf3ef3c   rengine_minio             minio/minio:RELEASE.2022-08-26T19-53-15Z                          Up 2 minutes
eb0e9192aeb5   rengine_redis_node_0      bitnami/redis-cluster:7.0                                         Up 2 minutes
f71b4e187d7d   rengine_redis_node_2      bitnami/redis-cluster:7.0                                         Up 2 minutes
16c56797b29c   rengine_redis_node_1      bitnami/redis-cluster:7.0                                         Up 2 minutes
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
