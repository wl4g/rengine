# Rengine for ApiServer Development Guide

## Building

```bash
git clone git@github.com/wl4g/rengine.git
cd rengine/
export JAVA_HOME=/usr/local/jdk-11.0.10/
./mvnw clean install -DskipTests -Dmaven.test.skip=true -T 4C
```
