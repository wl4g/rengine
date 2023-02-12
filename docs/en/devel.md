# Rengine for Development Guide

## Directories

- [Development for Manager](./devel-manager.md)

- [Development for Executor](./devel-evaluator.md)

- [Development for Job](./devel-job.md)

- [Development for Collector](./devel-collector.md)

- [Development for Client](./devel-client.md)

## Building

```bash
git clone git@github.com/wl4g/rengine.git
cd rengine/
export JAVA_HOME=/usr/local/jdk1.8.0_281/
./mvnw clean install -DskipTests -Dmaven.test.skip=true -U -T 4C
```
