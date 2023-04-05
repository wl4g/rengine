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
        "details": ["SINGLE"]
    },
    "condition": null,
    "nodes": [{
        "name": "start",
        "quantifier": {
            "consumingStrategy": "SKIP_TILL_NEXT",
            "times": null,
            "untilCondition": null,
            "details": ["SINGLE"]
        },
        "condition": {
            "type": "CLASS",
            "className": "org.apache.flink.cep.pattern.conditions.RichAndCondition",
            "nestedConditions": null,
            "subClassName": null
        },
        "type": "ATOMIC",
        "attributes": {
            "top": "10px"
        }
    }, {
        "name": "middle",
        "quantifier": {
            "consumingStrategy": "SKIP_TILL_NEXT",
            "times": null,
            "untilCondition": null,
            "details": ["SINGLE"]
        },
        "condition": {
            "type": "CLASS",
            "className": "org.apache.flink.cep.pattern.conditions.RichAndCondition",
            "nestedConditions": [{
                "type": "CLASS",
                "className": "org.apache.flink.cep.pattern.conditions.SubtypeCondition",
                "nestedConditions": null,
                "subClassName": "com.wl4g.rengine.common.event.RengineEvent"
            }, {
                "type": "AVIATOR",
                "expression": "body.level=='ERROR'||body.level=='FATAL'"
            }],
            "subClassName": null
        },
        "type": "ATOMIC",
        "attributes": {
            "top": "20px"
        }
    }, {
        "name": "end",
        "quantifier": {
            "consumingStrategy": "SKIP_TILL_NEXT",
            "times": null,
            "untilCondition": null,
            "details": ["SINGLE"]
        },
        "condition": {
            "type": "AVIATOR",
            "expression": "body.level=='ERROR'||body.level=='FATAL'"
        },
        "type": "ATOMIC",
        "attributes": {
            "top": "10px"
        }
    }],
    "edges": [{
        "source": "start",
        "target": "middle",
        "type": "SKIP_TILL_NEXT",
        "attributes": {}
    }, {
        "source": "middle",
        "target": "end",
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
        "type": "SKIP_PAST_LAST_EVENT",
        "patternName": null
    },
    "type": "COMPOSITE",
    "version": 1
}
]
EOF
```

## Start job for Docker

```bash
docker run \
--rm \
--security-opt=seccomp:unconfined \
wl4g/rengine-job:1.0.0 \
flink run-application \
--target kubernetes-application \
-Dkubernetes.cluster-id=rengine-job-1 \
-Dkubernetes.container.image=wl4g/rengine-job:1.0.0 \
local:///opt/flink/usrlib/rengine-job-1.0.0.jar,local:///opt/flink/usrlib/rengine-job-1.0.0-jar-with-dependencies.jar
```

## Start job for JVM

- Print help

```bash
export JAVA_HOME=/usr/local/jdk-11.0.10/
export JOB_CLASSPATH="job/job-base/target/rengine-job-base-1.0.0-jar-with-dependencies.jar"

$JAVA_HOME/bin/java -cp $JOB_CLASSPATH com.wl4g.rengine.job.cep.RengineKafkaFlinkCepStreaming \
--groupId rengine_test \
--cepPatterns $(cat /tmp/cep-pattern-for-log-alarm.json | base64 -w 0)
```

- using flink k8s cli.
  - [flink for docker doc](https://nightlies.apache.org/flink/flink-docs-release-1.14/zh/docs/deployment/resource-providers/standalone/docker/#application-mode-on-docker)
  - [flink native kubernetes doc](https://nightlies.apache.org/flink/flink-docs-release-1.14/zh/docs/deployment/resource-providers/native_kubernetes/#deployment-modes)
  - [KubernetesSessionCliTest.java](https://github1s.com/apache/flink/blob/release-1.14/flink-kubernetes/src/test/java/org/apache/flink/kubernetes/cli/KubernetesSessionCliTest.java)

```bash
$JAVA_HOME/bin/java -cp $JOB_CLASSPATH org.apache.flink.client.cli.CliFrontend run-application \
--target kubernetes-application \
-Dkubernetes.cluster-id=rengine-base-job-cluster-1 \
-Dkubernetes.container.image=flink:1.14.4-scala_2.11-java11 \
local://job/job-base/target/rengine-job-base-1.0.0-jar-with-dependencies.jar
```
