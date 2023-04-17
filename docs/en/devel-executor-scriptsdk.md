# Definition and use cases of built-in script SDKs

## Definitions

- Basic SDKs

  - [ScriptContext](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/ScriptContext.java)
  - [ScriptResult](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/ScriptResult.java)
  - [ScriptDataService](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/ScriptDataService.java)
  - [ScriptExecutor](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/ScriptExecutor.java)
  
- Common SDKs

  - [ScriptS3Client](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/ScriptS3Client.java)
  - [ScriptHttpClient](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/ScriptHttpClient.java)
  - [ScriptProcessClient](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/ScriptProcessClient.java)
  - [ScriptRedisLockClient](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/ScriptRedisLockClient.java)
  - [ScriptSSHClient](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/ScriptSSHClient.java)
  - [ScriptTCPClient](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/ScriptTCPClient.java)

- Tool SDKs

  - [Assert](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/tools/Assert.java)
  - [DateHolder](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/tools/DateHolder.java)
  - [UUID](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/tools/UUID.java)
  - [PrometheusParser](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/tools/PrometheusParser.java)
  - [RandomHolder](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/tools/RandomHolder.java)
  - [RengineEvent](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/tools/RengineEvent.java)
  - [Files](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/tools/Files.java)
  - [JSON](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/tools/JSON.java)
  - [Coding](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/tools/Coding.java)
  - [Hashing](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/tools/Hashing.java)
  - [AES](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/tools/AES.java)
  - [RSA](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/tools/RSA.java)

- Notifier SDKs

  - [DingtalkScriptMessageNotifier](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/notifier/DingtalkScriptMessageNotifier.java)
  - [EmailScriptMessageNotifier](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/notifier/EmailScriptMessageNotifier.java)

- DataSource SDKs

  - [JDBCSourceFacade](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/datasource/JDBCSourceFacade.java)
  - [MongoSourceFacade](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/datasource/MongoSourceFacade.java)
  - [RedisSourceFacade](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/datasource/RedisSourceFacade.java)
  - [OpenTSDBSourceFacade](../../executor/src/main/java/com/wl4g/rengine/executor/execution/sdk/datasource/OpenTSDBSourceFacade.java) (**progressing**)

## More script SDKs using examples

- [test-sdk-all-examples-v1.js](../../service/src/main/resources/example/rulescript/0/test-sdk-all-examples/test-sdk-all-examples-v1.js)
