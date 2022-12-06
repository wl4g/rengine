# Rengine for Collector Development Guide

## Building

```bash
git clone git@github.com/wl4g/rengine.git
cd rengine/
export JAVA_HOME=/usr/local/jdk-11.0.10/
./mvnw clean install -DskipTests -Dmaven.test.skip=true -Pwith:eventbus-kafka -T 2C
```

- ***Notice:*** For the eventbus module, one of the following MQs is supported. build profiles are: `with:eventbus-kafka` (the default), `with:eventbus-pulsar`, `with:eventbus-rabbitmq`

## Directories

```bash
+ rengine/
  + collector/
    # The auto configuration entry of the collector.
    + src/main/java/com/wl4g/rengine/collector/config/
      + CollectorAutoConfiguration.java

    + src/main/java/com/wl4g/rengine/collector/
      # The cluster sharding job type of proactive collect tasks.
      + job/
        + CollectJobExecutor.java # Collect job base.

        # The support proactive fetch from prometheus exporters, convert them to general events,
        # and then send them to MQ via eventbus.
        + PrometheusCollectJobExecutor.java

        # In the same way, actively fetch event from remote http servers.
        + SimpleHttpCollectJobExecutor.java

        # In the same way, actively fetch event from database(eg: MySQL).
        + SimpleJdbcCollectJobExecutor.java

        # In the same way, actively fetch event from redis(single, cluster).
        + SimpleRedisCollectJobExecutor.java

        # In the same way, actively fetch event from node via SSH script.
        + SimpleSSHCollectJobExecutor.java

        # In the same way, actively fetch event from node via send(base64)/receive TCP message packet.
        + SimpleTcpCollectJobExecutor.java
        + ...
```
