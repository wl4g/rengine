# Benchmarks for Rengine Executor 

## Benchmarking 1 (by apache `ab`):

- prepares

  - [benchmark1.json](../../tools/benchmark/benchmark1.json)
  - [benchmark1.js](../../tools/benchmark/benchmark1.js)

- benchmarking (example)

```bash
$ ab -p /tmp/benchmark1.json -T application/json -n 1000 -c 200 http://localhost:28002/execution/execute/internal

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

Document Path:          /execution/execute/internal
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

- results

| Resources |  Share  |  Concurrency  | Complete Requests |  Failed Requests  |  Requests per second(mean) | Time per request(mean) | Total Costs | Executor Configuration (core) |
| --------- | ---------- | --------- | --------- | ---------- | ---------- | ---------- | ---------- | ---------- |
| 8C 16G Ubuntu 20.04 jdk11  |     Y     |      200    |   1000    |     0   |  35.07 |  5703.568ms | 28.518s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 8C 16G Ubuntu 20.04 jdk11  |     Y     |      300    |   1000    |     0   |  71.80 |  4178.132ms | 13.927s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 8C 16G Ubuntu 20.04 jdk11  |     Y     |      350    |   1000    |     0   |  70.35 |  4975.389ms | 14.215s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 8C 16G Ubuntu 20.04 jdk11  |     Y     |      400    |   1000    |     0   |  70.99 |  5634.527ms | 14.086s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 8C 16G Ubuntu 20.04 jdk11  |     Y     |      500    |   1000    |     0   |  72.26 |  6919.900ms | 13.840s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 8C 16G Ubuntu 20.04 jdk11  |     Y     |      600    |   1000    |     0   |  65.12 |  9214.438ms | 15.357s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |
| 8C 16G Ubuntu 20.04 jdk11  |     Y     |      1000    |   1000   |     0   |  77.60 |  12887.029ms | 12.887s |  quarkus.redis.timeout=8s</br>quarkus.redis.max-pool-size=512</br>quarkus.redis.max-pool-waiting=512</br>rengine.executor.engine.executor-thread-pools=20</br>rengine.executor.engine.executor-accept-queue=20 |

- analyzing error logs.

```bash
export LOG_PATH="/mnt/disk1/log/rengine-executor/rengine-executor.log"
tail -F $LOG_PATH | jq -r '.level="ERROR"' > /tmp/error.log
```

## Benchmarking 2

- TODO
