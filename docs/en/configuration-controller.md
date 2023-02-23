# Rengine configuration for Controller

## Configuration

- for example: [application-controller.yaml](../../controller/src/main/resources/application-controller.yaml)

|  Property  |  Default | Example |  Description |
| ------------ | ------------ | ------------ | ------------ |
|  <b>rengine.controller.zookeeper</b> |   |   |   |
|  &nbsp;&nbsp;.serviceLists |  localhost:2181 | 192.168.8.2:2181,192.168.8.3:2181,192.168.8.4:2181 |   |
|  &nbsp;.namespace  |  rengine |   |   |
|  &nbsp;.baseSleepTimeMilliseconds  |  1000 |   |   |
|  &nbsp;.maxSleepTimeMilliseconds  |  3000 |   |   |
|  &nbsp;.maxRetries  |  rengine |   |   |
|  &nbsp;.sessionTimeoutMilliseconds  |  0 |   |   |
|  &nbsp;.connectionTimeoutMilliseconds  |  5 |   |   |
|  &nbsp;.digest  |  null |   |   |
|  <b>rengine.controller.tracing</b>  |   |   |   |
|  &nbsp;.type  | RDB  |   |   |
|  &nbsp;.includeJobNames  | []  |   |   |
|  &nbsp;.excludeJobNames  | []  |   |   |
|  <b>rengine.controller.controller</b>  |   |   |   |
|  &nbsp;&nbsp;.disabled  | false |   |   |
|  &nbsp;&nbsp;.overwrite  | true |   |   |
|  &nbsp;&nbsp;.monitorExecution  | true |   |   |
|  &nbsp;&nbsp;.failover  | false |   |   |
|  &nbsp;&nbsp;.misfire  | false |   |   |
|  &nbsp;&nbsp;.cron  | 0/5 * * * * ? |   |   |
|  &nbsp;&nbsp;.timeZone  | GMT+08:00 |   |   |
|  &nbsp;&nbsp;.maxTimeDiffSeconds  | -1 |   |   |
|  &nbsp;&nbsp;.reconcileIntervalMinutes  | 0 |   |   |
|  &nbsp;&nbsp;.autoShardingTotalCount  | true |   |   |
|  &nbsp;&nbsp;.shardingTotalCount  | 1 |   |   |
|  &nbsp;&nbsp;.shardingItemParameters  | 0=Beijing,1=Shanghai |   |   |
|  &nbsp;&nbsp;.jobShardingStrategyType  | null |   |   |
|  &nbsp;&nbsp;.jobExecutorServiceHandlerType  | null |   |   |
|  &nbsp;&nbsp;.jobErrorHandlerType  | null |   |   |
|  &nbsp;&nbsp;.jobListenerTypes  | null |   |   |
|  &nbsp;&nbsp;.description  | null |   |   |
|  <b>rengine.controller.purger</b>  |   |   |   |
|  &nbsp;&nbsp;.logRetentionHour  | 168 |   |   |
|  &nbsp;&nbsp;.logRetentionCount  | 1_000_000 |   |   |
|  <b>rengine.controller.client</b>  |   |   |   |
|  &nbsp;&nbsp;.concurrency  | 10 |   |   |
|  &nbsp;&nbsp;.acceptQueue  | 2 |   |   |