# Rengine for Job Development Guide

## Building

```bash
git clone git@github.com/wl4g/rengine.git
cd rengine/job
#export JAVA_HOME=/usr/local/jdk-11.0.10/
export JAVA_HOME=/usr/local/jdk1.8.0_281/
./mvnw clean install -DskipTests -Dmaven.test.skip=true -T 2C
```
