- https://docs.oracle.com/en/graalvm/enterprise/21/docs/reference-manual/native-image/Agent/

- Example commands:

```bash
$GRAALVM_HOME/bin/java -agentlib:native-image-agent=config-output-dir=/tmp/graalGenReflectConf/ -Dgraaljs.allowAllAccess=true
```
