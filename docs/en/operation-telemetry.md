# Rengine for Operation telemetry

- Get health

```bash
curl -v localhost:28001/actuator/health
```

- Get metrics

```bash
curl -v localhost:28001/actuator/prometheus
```

## Deploy Fluent bit

```bash
# Configuration.
sudo mkdir -p /mnt/disk1/fluent-bit/{data,conf}
sudo chmod -R 777 /mnt/disk1/fluent-bit

curl -L -o /mnt/disk1/fluent-bit/conf/fluent-bit.conf \
'https://raw.githubusercontent.com/wl4g/rengine/master/tools/operation/fluent-bit/fluent-bit.conf'

# Run container.
docker run -d \
--name fluent-bit \
--restart no \
-v /mnt/disk1/fluent-bit/conf/fluent-bit.conf:/fluent-bit/etc/fluent-bit.conf \
-v /var/lib/docker/containers:/containers \
-p 2020:2020 \
fluent/fluent-bit:2.0.8

docker logs -f --tail 99 fluent-bit
```

## Deploy OTel collector

```bash
# Configuration.
sudo mkdir -p /etc/otel
curl -L -o /etc/otel/collector.yaml \
'https://raw.githubusercontent.com/wl4g/rengine/master/tools/operation/otel/collector.yaml'

# for debugging
sudo mkdir -p /tmp/otel; sudo chmod 777 -R /tmp/otel

# Run container.
docker run -d \
--name=otel-collector \
--network=host \
--restart=no \
-v /etc/otel/collector.yaml:/etc/otelcol-contrib/config.yaml \
-v /var/log:/var/log \
-v /tmp/otel:/tmp/otel \
otel/opentelemetry-collector-contrib:0.72.0

docker logs -f --tail 99 otel-collector
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
