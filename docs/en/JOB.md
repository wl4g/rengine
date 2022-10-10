# Rengine Job

## Quick start

### Deploy on Docker (Recommended for local dev/test environments)

- Deploy local HBase

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

- Deploy local Kafka

```bash
docker run -d --name kafka1 --network host -e ALLOW_PLAINTEXT_LISTENER=yes bitnami/kafka:2.2.0
```

- Deploy local Flink(optional, for session mode)

```bash
docker run -d --name flink-jm1 --network host flink:1.14.4-scala_2.11-java11 jobmanager
docker run -d --name flink-tm1 --network host flink:1.14.4-scala_2.11-java11 taskmanager
```

- Browser accessing: http://localhost:8081

### Deploy on Kubernetes (Recommended for production environments)

- (first:) Deploy HBase distributed production Cluster with CDH. refer: [blogs.wl4g.com/archives/3368](https://blogs.wl4g.com/archives/3368)

- Deploy Flink jobs to Kubernetes

```bash
#TODO
```

### Testing

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
    "info"."locationCity" VARCHAR(16),
    "info"."locationRegion" VARCHAR(16),
    "info"."locationCountry" VARCHAR(8),
    "info"."attributes" VARCHAR(1024)
) COLUMN_ENCODED_BYTES=0;

!describe "rengine"."t_ods_event";

select * from "rengine"."t_ods_event" limit 10;
```

- Init Kafka topic.

```bash
docker exec -it kafka1 bash

kafka-topics.sh --zookeeper 127.0.0.1:2181 --create --topic rengine_event --partitions 10 --replication-factor 1
```

- Manual publish events to Kafka

```bash
kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic rengine_event --property parse.key=true --property key.separator=:

rengine_event:{"source":{"time":1665847350487,"principals":["admin"],"location":{"ipAddress":"1.1.1.1","ipv6":false,"isp":null,"domain":null,"country":null,"region":null,"city":null,"latitude":null,"longitude":null,"timezone":null,"zipcode":"20500","elevation":null}},"type":"iot_generic_device_temp_warning","observedTime":1665847350490,"body":"52℃","attributes":{}}
```

- Manual subscribe event from Kafka

```bash
kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic rengine_event
```

## FAQ

### Run phoenix client error of: `java.sql.SQLException: ERROR 726 (43M10):  Inconsistent namespace mapping properties. Cannot initiate connection as SYSTEM:CATALOG is found but client does not have phoenix.schema.isNamespaceMappingEnabled enabled`

- Refer to:
  - [https://phoenix.apache.org/namspace_mapping.html](https://phoenix.apache.org/namspace_mapping.html)
  - [https://issues.apache.org/jira/secure/attachment/12792283/PHOENIX-1311_v1.patch](https://issues.apache.org/jira/secure/attachment/12792283/PHOENIX-1311_v1.patch)
  - [https://github.com/apache/phoenix/blob/v5.0.0-HBase-2.0/phoenix-core/src/main/java/org/apache/phoenix/util/SchemaUtil.java#L700](https://github.com/apache/phoenix/blob/v5.0.0-HBase-2.0/phoenix-core/src/main/java/org/apache/phoenix/util/SchemaUtil.java#L700)

- Resolved: The new version of hbase2.x, the default namespace and table name separator is `":"`, but Phoenix uses `"."` by default, but from Phoenix4.8+,
namespace mapping to hbase is supported, but you must manually configure the HBase server and Phoenix clients configure it, that is, add configuration items
in hbase-site.xml (need restart) e.g:

```xml
    <property>
        <name>phoenix.connection.isNamespaceMappingEnabled</name>
        <value>true</value>
    </property>
    <property>
        <name>phoenix.schema.isNamespaceMappingEnabled=true</name>
        <value>true</value>
    </property>
```

