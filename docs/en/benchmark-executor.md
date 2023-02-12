# Benchmarks for Rengine Executor 

## Benchmarking 1 (by [ApacheBench](https://httpd.apache.org/docs/2.4/programs/ab.html)):

- Prepares

  - [benchmark1.json](../../tools/benchmark/benchmark1.json)
  - [benchmark1.js](../../tools/benchmark/benchmark1.js)

- Benchmarking

```bash
$ ab -p /tmp/benchmark1.json -T application/json -n 1000 -c 200 http://localhost:28002/execute/internal/workflow

This is ApacheBench, Version 2.3 <$Revision: 1843412 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking localhost (be patient)
Completed 100 requests
Completed 200 requests
Completed 300 requests
Completed 400 requests
Completed 500 requests
Completed 600 requests
Completed 700 requests
Completed 800 requests
Completed 900 requests
Completed 1000 requests
Finished 1000 requests

Server Software:        
Server Hostname:        localhost
Server Port:            28002

Document Path:          /execute/internal/workflow
Document Length:        692 bytes

Concurrency Level:      200
Time taken for tests:   28.518 seconds
Complete requests:      1000
Failed requests:        0
Total transferred:      778000 bytes
Total body sent:        458000
HTML transferred:       692000 bytes
Requests per second:    35.07 [#/sec] (mean)
Time per request:       5703.568 [ms] (mean)
Time per request:       28.518 [ms] (mean, across all concurrent requests)
Transfer rate:          26.64 [Kbytes/sec] received
                        15.68 kb/s sent
                        42.33 kb/s total

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    1   3.1      0      14
Processing:    24 4296 2333.9   4356   10812
Waiting:       23 4296 2333.9   4356   10811
Total:         24 4298 2334.3   4356   10812

Percentage of the requests served within a certain time (ms)
  50%   4356
  66%   5104
  75%   6560
  80%   6623
  90%   7295
  95%   7568
  98%   9407
  99%   9468
 100%  10812 (longest request)
```

- Benchmarks Results (JVM)

> rengine-executor 1.0.0 on JVM (powered by Quarkus 2.12.2.Final) started in 9.848s. Listening on: http://0.0.0.0:28002

| Resources |  Share  |  Concurrency  | Complete Requests |  Failed Requests  |  Requests per second(mean) | Time per request(mean) | Total Costs | Executor Configuration (core) |
| --------- | ---------- | --------- | --------- | ---------- | ---------- | ---------- | ---------- | ---------- |
| 4C 4G Ubuntu 20.04 jdk11  |     Y     |      200    |   1000    |     0   |  35.07 |  5703.568ms | 28.518s |  quarkus.redis.timeout=10s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 4C 4G Ubuntu 20.04 jdk11  |     Y     |      300    |   1000    |     0   |  71.80 |  4178.132ms | 13.927s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 4C 4G Ubuntu 20.04 jdk11  |     Y     |      350    |   1000    |     0   |  70.35 |  4975.389ms | 14.215s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 4C 4G Ubuntu 20.04 jdk11  |     Y     |      400    |   1000    |     0   |  70.99 |  5634.527ms | 14.086s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 4C 4G Ubuntu 20.04 jdk11  |     Y     |      500    |   1000    |     0   |  72.26 |  6919.900ms | 13.840s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 4C 4G Ubuntu 20.04 jdk11  |     Y     |      600    |   1000    |     0   |  65.12 |  9214.438ms | 15.357s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 4C 4G Ubuntu 20.04 jdk11  |     Y     |      1000   |   1000    |     0   |  77.60 |  12887.029ms | 12.887s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 4C 4G Ubuntu 20.04 jdk11  |     Y     |      2000   |   1000    |    167  |  62.27 |  16058.901ms | 32.118s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 4C 4G Ubuntu 20.04 jdk11  |     Y     |      2000   |   1000    |    160  |  59.57 |  16787.159ms | 33.574 |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=768</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |

- Benchmarks Results (Native)

> rengine-executor 1.0.0 native (powered by Quarkus 2.12.2.Final) started in 0.121s. Listening on: http://0.0.0.0:28002"

| Resources |  Share  |  Concurrency  | Complete Requests |  Failed Requests  |  Requests per second(mean) | Time per request(mean) | Total Costs | Executor Configuration (core) |
| --------- | ---------- | --------- | --------- | ---------- | ---------- | ---------- | ---------- | ---------- |
| 4C 4G Ubuntu 20.04 |     Y     |      200    |   1000    |     0   |  30.25 |  6610.981ms | 33.055s |  quarkus.redis.timeout=10s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 4C 4G Ubuntu 20.04 |     Y     |      300    |   1000    |     0   |  35.59 |  8429.738ms | 28.099s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 4C 4G Ubuntu 20.04 |     Y     |      350    |   1000    |     0   |  35.38 |  9893.169ms | 28.266s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 4C 4G Ubuntu 20.04 |     Y     |      400    |   1000    |     0   |  34.50 |  11593.168ms| 28.983s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 4C 4G Ubuntu 20.04 |     Y     |      500    |   1000    |     0   |  35.31 |  14161.944ms| 28.324s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 4C 4G Ubuntu 20.04 |     Y     |      600    |   1000    |     0   |  33.64 |  17834.290ms| 29.724s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 4C 4G Ubuntu 20.04 |     Y     |      1000   |   1000    |    92   |  38.58 |  25920.483ms| 25.920s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 4C 4G Ubuntu 20.04 |     Y     |      2000   |   1000    |    117  |  38.12 |  26230.928ms| 52.462s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 4C 4G Ubuntu 20.04 |     Y     |      2000   |   1000    |    128  |  39.75 |  25157.858ms| 50.316s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=768</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |

> Summary (not a strict conclusion): Native mode starts very fast, at least 80 times faster than JVM mode, but the runtime performance of JVM mode is better than native mode, which seems a bit like the current unoptimized JVM 5.0 era. But similar to the official test conclusion of quarkus see: [https://quarkus.io/guides/native-reference#why-is-runtime-performance-of-a-native-executable-inferior-compared-to-jvm-mode](https://quarkus.io/guides/native-reference#why-is-runtime-performance-of-a-native-executable-inferior-compared-to-jvm-mode)

> Notice: The above case benchmark 1 is only a simple mini performance test report of the development environment (the host it uses is shared). For more rigorous performance testing, please refer to the following cases.

- Analyzing error logs.

```bash
export LOG_PATH="/mnt/disk1/log/rengine-executor/rengine-executor.log"
tail -F $LOG_PATH | jq -r '.level="ERROR"' > /tmp/error.log
```

## Benchmarking 2

- TODO
