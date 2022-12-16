# Rengine configuration for Scheduler

## Configuration

- for example:
  - [collect-job.yaml](../../scheduler/src/main/resources/scheduler-job.yaml)
  - [collect-job-safecloud.yaml](../../scheduler/src/main/resources/scheduler-job-safecloud.yaml)

|  Property  |  Default | Example |  Description |
| ------------ | ------------ | ------------ | ------------ |
|  <b>rengine.client.scheduler.zookeeper</b> |   |   |   |
|  &nbsp;&nbsp;.serviceLists |  localhost:2181 | 192.168.8.2:2181,192.168.8.3:2181,192.168.8.4:2181 |   |
|  &nbsp;.namespace  |  rengine |   |   |
|  &nbsp;.baseSleepTimeMilliseconds  |  1000 |   |   |
|  &nbsp;.maxSleepTimeMilliseconds  |  3000 |   |   |
|  &nbsp;.maxRetries  |  rengine |   |   |
|  &nbsp;.sessionTimeoutMilliseconds  |  0 |   |   |
|  &nbsp;.connectionTimeoutMilliseconds  |  5 |   |   |
|  &nbsp;.digest  |  null |   |   |
|  <b>rengine.client.scheduler.tracing</b>  |   |   |   |
|  &nbsp;.type  | RDB  |   |   |
|  &nbsp;.includeJobNames  | []  |   |   |
|  &nbsp;.excludeJobNames  | []  |   |   |
|  <b>rengine.client.scheduler.globalScrapeJobConfig</b>  |   |   |   |
|  &nbsp;&nbsp;.eventType  | PROM |   |   |
|  &nbsp;&nbsp;.eventAttributes  | {}  |   |   |
|  &nbsp;&nbsp;.disabled  | false |   |   |
|  &nbsp;&nbsp;.overwrite  | true |   |   |
|  &nbsp;&nbsp;.monitorExecution  | true |   |   |
|  &nbsp;&nbsp;.failover  | false |   |   |
|  &nbsp;&nbsp;.misfire  | false |   |   |
|  &nbsp;&nbsp;.cron  | 0/10 * * * * ? |   |   |
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
|  <b>&nbsp;&nbsp;.jobVariables</b>  |  |  |  The define global variables of SPEL expression, which can be used for job dynamic input arguemnts. see:com.wl4g.rengine.client.scheduler.job.CollectJobExecutor#resolveVariables() <b>Note: If you run in grailvm native image mode, you can only call META-INF/native-image/reflect-config.json defined fields and methods, because the SPEL needs reflection calls, or the user-defined extension reflect-config.json recompiles and packages.</b> |
|  &nbsp;&nbsp;&nbsp;&nbsp;.yesterday  | "#{T(com.wl4g.infra.common.lang.DateUtils2).getDateOf(5,-1,\\"yyyy-MM-dd\\")}" |   |   |
|  <b>&nbsp;&nbsp;.jobParamConfigs</b>  |   |   |   |
|  <b>&nbsp;&nbsp;&nbsp;&nbsp;.simpleHttp</b>  |   |   |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.url  | "http://localhost:8080/event" |  "http://localhost:9100/metrics" |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.method  | "GET" |   |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.headers  | {} |   |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.body  | null |   |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.connectTimeoutMs  | 3000 |   |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.readTimeoutMs  | 5000 |   |   |
|  <b>&nbsp;&nbsp;&nbsp;&nbsp;.simpleJdbc</b>  |   |   |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.ignoreWarnings  | true |   |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.fetchSize  | -1 |   |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.maxRows  | -1 |   |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.queryTimeout  | -1 |   |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.skipResultsProcessing  | false |   |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.skipUndeclaredResults  | false |   |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.resultsMapCaseInsensitive  | false |   |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.sql  | "SELECT 1" |   |   |
|  <b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.hikariConfig</b>  | null |   |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.jdbcUrl  | null |  "jdbc:mysql://localhost:3306/test?useunicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&autoReconnect=true" |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.username  | null |  "root" |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.password  | null |  "123456" |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ...  | null |   |   |
|  <b>&nbsp;&nbsp;&nbsp;&nbsp;simpleRedis</b>  |  |   |   |
|  <b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.jedisConfig</b>  | null |   |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.nodes  | [] |   |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.passwd  | "" |   |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.luaScript  | "return 'Hello'" | "return string.format('Hello, yesterday is: %s', KEYS[1])" |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.luaKeys  | [] |  ["myprefix"]  |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.luaArgs  | [] |  ["{{yesterday}}"] | Use double braces to reference dynamic variables. such as: {{my_variable}} |
|  <b>&nbsp;&nbsp;&nbsp;&nbsp;.simpleTcp</b>  |   |   |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.host  | null | "localhost"  |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.port  | 32000 | 1883  |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.base64Message  | null | "SGVsbG8K"  |   |
|  <b>&nbsp;&nbsp;&nbsp;&nbsp;.simpleSsh</b>  |   |   |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.host  | null | 192.168.8.8  |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.port  | 22 | 22  |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.user  | null | "root"  |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.password  | null | "123456"  |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.privateKey  | null |   |   |
|  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.command  |  null | "echo 'hello'"  |   |
