# Massive log collection based on OTEL(otlp) standard protocol, real-time abnormal analysis and alarm.

- Preparing CEP pattern json (example)

```json
cat << EOF > /tmp/cep-pattern-for-log-error.json
{
    "name": "end",
    "quantifier": {
        "consumingStrategy": "SKIP_TILL_NEXT",
        "properties": ["SINGLE"],
        "times": null,
        "untilCondition": null
    },
    "condition": null,
    "nodes": [{
        "name": "end",
        "quantifier": {
            "consumingStrategy": "SKIP_TILL_NEXT",
            "properties": ["SINGLE"],
            "times": null,
            "untilCondition": null
        },
        "condition": {
            "expression": "type == login_success",
            "type": "AVIATOR"
        },
        "type": "ATOMIC"
    }, {
        "name": "middle",
        "quantifier": {
            "consumingStrategy": "SKIP_TILL_NEXT",
            "properties": ["SINGLE"],
            "times": null,
            "untilCondition": null
        },
        "condition": {
            "nestedConditions": [{
                "className": "org.apache.flink.cep.pattern.conditions.SubtypeCondition",
                "subClassName": "com.wl4g.rengine.common.event.RengineEvent",
                "type": "CLASS"
            }, {
                "expression": "type == login_tail",
                "type": "AVIATOR"
            }],
            "type": "CLASS",
            "className": "org.apache.flink.cep.pattern.conditions.RichAndCondition"
        },
        "type": "ATOMIC"
    }, {
        "name": "start",
        "quantifier": {
            "consumingStrategy": "SKIP_TILL_NEXT",
            "properties": ["SINGLE"],
            "times": null,
            "untilCondition": null
        },
        "condition": {
            "expression": "type == login_tail",
            "type": "AVIATOR"
        },
        "type": "ATOMIC"
    }],
    "edges": [{
        "source": "middle",
        "target": "end",
        "type": "SKIP_TILL_ANY"
    }, {
        "source": "start",
        "target": "middle",
        "type": "SKIP_TILL_ANY"
    }],
    "window": null,
    "afterMatchStrategy": {
        "type": "NO_SKIP",
        "patternName": null
    },
    "type": "COMPOSITE",
    "version": 1
}
EOF
```

- Start CEP job

```bash

```

