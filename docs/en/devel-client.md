# Development guide for Rengine Client

## Building

```bash
git clone git@github.com/wl4g/rengine.git
cd rengine/
export JAVA_HOME=/usr/local/jdk1.8.0_281/
./mvnw clean install -DskipTests -Dmaven.test.skip=true -Pwith:eventbus-kafka -T 2C
```

- ***Notice:*** For the eventbus module, one of the following MQs is supported. build profiles are: `with:eventbus-kafka` (the default), `with:eventbus-pulsar`, `with:eventbus-rabbitmq`

## Directories

```bash
+ rengine/
  + client/
    + client-go/
    + client-java/
      + client-core/
      + client-spring-boot-starter/
        + src/main/java/com/wl4g/rengine/client/springboot/
          # The spring boot auto configuration entry of client and eventbus modules.
          + config/
            + RengineClientAutoConfiguration.java
            + RengineEventbusAutoConfiguration.java # optional
            + KafkaEventBusAutoConfiguration.java # by default
            + PulsarEventBusAutoConfiguration.java # optional
            + RabbitmqEventBusAutoConfiguration.java # optional
            + ...

          + intercept/
            # The processing entry for auto evaluation of methods with spring aop.
            + REvaluationAdvice.java
            + ...
```
