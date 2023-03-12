# Rengine configuration for Job

- Common Configurations

| Options | Description |
| - | - |
| -B,--brokers <default=localhost:9092>                                                           | Connect MQ brokers addresses. default is local kafka brokers
|    --bufferTimeoutMillis <default=-1>                                                           | Parallelism for this operator, if <=0, it will not be setup and keep the default behavior.
|    --checkpointDir <default=hdfs:///tmp/flink-checkpoint>                                       | Checkpoint execution interval millis, only valid when checkpointMode is sets.
|    --checkpointIntervalMs <default=null>                                                        | Checkpoint execution interval millis, only valid when heckpointMode is sets.
|    --checkpointMaxConcurrent <default=null>                                                     | Sets the maximum number of checkpoint attempts that may be in progress at the same time. If this value is n, then no checkpoints will be triggered while n checkpoint attempts are currently in flight. For the next checkpoint to be triggered, one checkpoint attempt would need to finish or expire.
|    --checkpointMinPauseBetween <default=null>                                                   | The minimum time interval between two checkpoints.
|    --checkpointMode <default=AT_LEAST_ONCE>                                                     | Sets the checkpoint mode, the default is null means not enabled. options: [EXACTLY_ONCE, AT_LEAST_ONCE]
|    --checkpointTimeout <default=null>                                                           | Checkpoint timeout millis.
| -D,--deserializerClass <default=com.wl4g.rengine.job.kafka.GenericKafkaDeserializationSchema>   | Deserializer class for Flink-streaming to consuming from MQ.
|    --externalizedCheckpointCleanup <default=null>                                               | The program is closed, an extra checkpoint is triggered.
| -F,--forceUsePrintSink <default=false>                                                          | Force override set to stdout print sink function.
| -G,--groupId <required>                                                                         | Flink source consumer group id.
|    --idleTimeoutMillis <default=30000>                                                          | The timeout millis for the idleness detection.
| -J,--jobName <default=RengineKafkaFlinkCepStreamingJob>                                         | Flink connect MQ source streaming job name.
|    --maxParallelism <default=-1>                                                                | The maximum parallelism for operator. if <=0, it will not be setup and keep the default behavior.
| -O,--fromOffsetTime <default=-1>                                                                | Start consumption from the first record with a timestamp greater than or equal to a certain timestamp. if <=0, it will not be setup and keep the default behavior.
|    --outOfOrdernessMillis <default=120000>                                                      | The maximum millis out-of-orderness watermark generator assumes.
| -p,--parallelism <default=-1>                                                                   | The parallelism for operator. if <=0, it will not be setup and keep the default behavior.
| -P,--patternJsonBase64 <required>                                                               | The cep pattern json base64.
|    --partitionDiscoveryIntervalMs <default=30000>                                               | The per millis for discover new partitions interval.
| -R,--runtimeMode <default=STREAMING>                                                            | Set the job execution mode. default is: STREAMING
|    --restartAttempts <default=3>                                                                | Set the maximum number of failed restart attempts. default is: 3
|    --restartDelaySeconds <default=15>                                                           | Set the maximum number of failed interval between each restart. default is: 15
| -T,--topicPattern <default=rengine_event>                                                       | MQ topic regex pattern.

## Flink consuming kafka with CEP

- Commands example

```bash
export JAVA_HOME=/usr/local/jdk-11.0.10
$JAVA_HOME/bin/java -cp \
job/job-base/target/rengine-job-base-1.0.0.jar:\
job/job-base/target/rengine-job-base-1.0.0-jar-with-dependencies.jar \
com.wl4g.rengine.job.cep.RengineKafkaFlinkCepStreaming
```

- Extra configurations

| Options | Description |
| - | - |
| -K,--keyByExpression <default=.source.principals[0]>                                            | The jq expression to extract the grouping key, it extraction from the rengine event object.

## Flink consuming kafka to HBase

- Commands example

```bash
export JAVA_HOME=/usr/local/jdk-11.0.10
$JAVA_HOME/bin/java -cp \
job/job-kafka-hbase/target/rengine-job-kafka-hbase-1.0.0.jar:\
job/job-kafka-hbase/target/rengine-job-kafka-hbase-1.0.0-jar-with-dependencies.jar \
com.wl4g.rengine.job.RengineKafka2HBaseStreaming
```

- Extra configurations

| Options | Description |
| - | - |
|    --hbaseZkAddrs <default=localhost:2181>                                                      | HBase zookeeper quorum addresses.
|    --hTableName <default=t_ods_event>                                                           | Sink to HBase table name.
|    --hTableNamespace <default=rengine>                                                          | Sink to HBase table namespace.
|    --bufferFlushIntervalMillis <default=5000>                                                   | Sink to HBase write flush time interval. if <=0, it will not be setup and keep the default behavior.
|    --bufferFlushMaxRows <default=128>                                                           | Sink to HBase write flush max size. if <=0, it will not be setup and keep the default behavior.
|    --bufferFlushMaxSizeInBytes <default=8192>                                                   | Sink to HBase write flush max buffer size. f <=0, it will not be setup and keep the default behavior.

