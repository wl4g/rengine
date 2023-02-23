# Development guide for Rengine Controller

## Building

```bash
git clone git@github.com/wl4g/rengine.git
cd rengine/
export JAVA_HOME=/usr/local/jdk-11.0.10/
./mvnw clean install -DskipTests -Dmaven.test.skip=true -U -T 4C
```

## Directories

```bash
+ rengine/
  + controller/
    # The auto configuration entry of the controller.
    + src/main/java/com/wl4g/rengine/controller/config/
      + RengineControllerAutoConfiguration.java

    + src/main/java/com/wl4g/rengine/job/
      # The cluster sharding job type of proactive collect tasks.
      + job/
        + AbstractJobExecutor.java # Abstract job base.

        # Global Master controller, responsible for scanning the schedule configuration 
        # table, and allocating and starting different types of execution controllers.
        + GlobalEngineMasterController.java

        # The according to the configuration, parallel(distributed job) invoke workflow graph.
        + EngineGenericExecutionController.java

        # The consumpting data from Kafka in parallel(distributed job), and invoke workflow graph.
        + EngineKafkaExecutionController.java
        + ...
```
