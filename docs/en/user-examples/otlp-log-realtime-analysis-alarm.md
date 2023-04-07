# Massive log collection based on OTEL(otlp) standard protocol, real-time abnormal analysis and alarm.

## Preparing CEP pattern json (example)

```json
cat << EOF > /tmp/cep-pattern-for-log-alarm.json
[
{
    "engine": "FLINK_CEP_GRAPH",
    "name": "root",
    "quantifier": {
        "consumingStrategy": "SKIP_TILL_NEXT",
        "times": {
            "from": 1,
            "to": 3,
            "windowTime": {
                "unit": "MINUTES",
                "size": 5
            }
        },
        "untilCondition": null,
        "properties": ["SINGLE"]
    },
    "condition": null,
    "nodes": [{
        "name": "middle",
        "quantifier": {
            "consumingStrategy": "SKIP_TILL_NEXT",
            "times": null,
            "untilCondition": null,
            "properties": ["SINGLE"]
        },
        "condition": {
            "nestedConditions": [{
                "expression": "body.logRecord.item2 == 'ERROR'",
                "type": "AVIATOR"
            }, {
                "expression": "body.logRecord.item2 == 'FATAL'",
                "type": "AVIATOR"
            }],
            "type": "CLASS",
            "className": "org.apache.flink.cep.pattern.conditions.RichOrCondition"
        },
        "attributes": {
            "top": "10px"
        },
        "type": "ATOMIC"
    }, {
        "name": "start",
        "quantifier": {
            "consumingStrategy": "SKIP_TILL_NEXT",
            "times": null,
            "untilCondition": null,
            "properties": ["SINGLE"]
        },
        "condition": {
            "expression": "body.logRecord.item2 == 'TRACE' || body.logRecord.item2 == 'DEBUG' || body.logRecord.item2 == 'INFO' || body.logRecord.item2 == 'WARN'",
            "type": "AVIATOR"
        },
        "attributes": {
            "top": "20px"
        },
        "type": "ATOMIC"
    }],
    "edges": [{
        "source": "start",
        "target": "middle",
        "type": "SKIP_TILL_NEXT",
        "attributes": {}
    }],
    "window": {
        "type": "PREVIOUS_AND_CURRENT",
        "time": {
            "unit": "MINUTES",
            "size": 5
        }
    },
    "afterMatchStrategy": {
        "type": "NO_SKIP",
        "patternName": null
    },
    "type": "COMPOSITE",
    "version": 1
}
]
EOF
```

## Start job on Docker

- [flink application-mode-on-docker](https://nightlies.apache.org/flink/flink-docs-release-1.14/zh/docs/deployment/resource-providers/standalone/docker/#application-mode-on-docker)

```bash
# e.g: --fromSavepoint file:///tmp/flinksavepoint
docker run \
  --name=rengine_job_1 \
  --rm \
  --env FLINK_PROPERTIES="${FLINK_PROPERTIES}" \
  --network host \
  --security-opt=seccomp:unconfined \
  wl4g/rengine-job:1.0.0 standalone-job \
  --job-classname com.wl4g.rengine.job.kafka.RengineKafkaFlinkCepStreaming \
  --allowNonRestoredState \
  --groupId rengine_test \
  --checkpointDir=file:///tmp/flinksavepoint \
  --cepPatterns $(cat /tmp/cep-pattern-for-log-alarm.json | base64 -w 0)
```

## Start job on Kubernetes

- [flink native kubernetes deployment-modes](https://nightlies.apache.org/flink/flink-docs-release-1.14/zh/docs/deployment/resource-providers/native_kubernetes/#deployment-modes)

- [KubernetesSessionCliTest.java](https://github1s.com/apache/flink/blob/release-1.14/flink-kubernetes/src/test/java/org/apache/flink/kubernetes/cli/KubernetesSessionCliTest.java)

```bash
docker run \
  --name=rengine_job_1 \
  --rm \
  --security-opt=seccomp:unconfined \
  wl4g/rengine-job:1.0.0 \
  flink run-application \
  --target kubernetes-application \
  -Dkubernetes.cluster-id=rengine-job-1 \
  -Dkubernetes.container.image=wl4g/rengine-job:1.0.0 \
  local:///opt/flink/usrlib/rengine-job-1.0.0-jar-with-dependencies.jar \
  --groupId rengine_test \
  --checkpointDir=file:///tmp/flinksavepoint \
  --cepPatterns $(cat /tmp/cep-pattern-for-log-alarm.json | base64 -w 0)
```

## Start job on VM

- directly run main class

```bash
export JAVA_HOME=/usr/local/jdk-11.0.10/
export JOB_CLASSPATH="job/target/rengine-job-1.0.0-jar-with-dependencies.jar"

$JAVA_HOME/bin/java -cp $JOB_CLASSPATH com.wl4g.rengine.job.kafka.RengineKafkaFlinkCepStreaming \
  --groupId rengine_test \
  --cepPatterns $(cat /tmp/cep-pattern-for-log-alarm.json | base64 -w 0)
```

- using flink k8s cli.

```bash
$JAVA_HOME/bin/java -cp $JOB_CLASSPATH org.apache.flink.client.cli.CliFrontend run-application \
  --target kubernetes-application \
  -Dkubernetes.cluster-id=rengine-base-job-cluster-1 \
  -Dkubernetes.container.image=flink:1.14.4-scala_2.11-java11 \
  local://job/job-base/target/rengine-job-base-1.0.0-jar-with-dependencies.jar
```
