# Developer's guide for Rengine ApiServer

## Building

```bash
git clone git@github.com/wl4g/rengine.git
cd rengine/
export JAVA_HOME=/usr/local/jdk-11.0.10/
./mvnw clean install -DskipTests -Dmaven.test.skip=true -T 4C
```

## FAQ

### If you use the groovy environment, pay attention to the version situation between the components
  - 1. The **groovy-4.0.5**(current latest) that the rengine-evaluator module depends on only supports **jdk1.8/9/10/16**
  - 2. The **spring-native-0.12.1*** (current latest) that the rengine apiserver module depends on only supports jdk11+
  - 3. Refer source code: [github.com/apache/groovy/blob/GROOVY_4_0_5/src/main/java/org/codehaus/groovy/vmplugin/VMPluginFactory.java#L39](https://github.com/apache/groovy/blob/GROOVY_4_0_5/src/main/java/org/codehaus/groovy/vmplugin/VMPluginFactory.java#L39)