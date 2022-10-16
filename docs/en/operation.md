# Rengine for Operation Guide

## Manual telemetry

- Get health

```bash
curl -v localhost:28001/actuator/health
```

- Get metrics

```bash
curl -v localhost:28001/actuator/prometheus
```

## Deploy OTel collector

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
