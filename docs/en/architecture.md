# Rengine for Architectures

- ![Global](../shots/rengine_architecture.png)

## Collector/Eventbus collect Event to MQ (Model definition)

- for example1: (see: [com/wl4g/rengine/common/event/RengineEvent.java](../../common/src/main/java/com/wl4g/rengine/common/event/RengineEvent.java))

```json
{
    "type": "iot_temp_warn", // Custom event type. (too long is not allowed)
    "observedTime": 1665931162567, // Time when the event was observed.
    "body": "69(℃)", // Event content details, which can be any string or json.
    "source": {
        "time": 1665931161234, // Time of occurrence of the event source.
        "principals": [ // Event principal or primary dimension, such as can be user id or any object id.
            "jameswong1234@gmail.com"
        ],
        "location": { // Geographic location of the event.
            "ipAddress": "8.8.8.1",
            "ipv6": false,
            "isp": "Google Cloud (US) Technology Co., Ltd.",
            "domain": "google.com",
            "elevation": null,
            "latitude": 37.4056,
            "longitude": -122.0775,
            "timezone": "GMT-7",
            "zipcode": "94043",
            "city": "Mountain View",
            "region": "California",
            "country": "US"
        }
    },
    "attributes": { // Event any additional attributes.
        "org": "AS15169 Google LLC",
        "asn": "AS15169",
        "email": "network-abuse@google.com",
        "phone": "+1-650-253-0000"
    }
}
```

## Flink sink original to ODS Table (HBase for phoenix)

```sql
select * from "rengine"."t_ods_event" where "ROW"='487221015232230:jameswong1234@gmail.com:iot_temp_warn:US:California:Mountain_View' limit 1;

+-----------------------------------------------------------------------------------+--------------+------+--------------+-------------------------+-------------------+--------------+----------------------------------------+ ...
|                                       ROW                                         | observedTime | body |  sourceTime  |    sourcePrincipals     | locationIpAddress | locationIpv6 |               locationIsp              |
+-----------------------------------------------------------------------------------+--------------+------+--------------+-------------------------+-------------------+--------------+----------------------------------------+ ...
| 234221016223921:jameswong1234@gmail.com:iot_temp_warn:US:California:Mountain_View | 221016223922 | 52   | 221016223921 | jameswong1234@gmail.com | 8.8.8.1           | 0            | Google Cloud (US) Technology Co., Ltd. |
+-----------------------------------------------------------------------------------+--------------+------+--------------+-------------------------+-------------------+--------------+----------------------------------------+ ...
1 row selected (0.017 seconds)
```

## Flink sink aggregated to DWS Table (HBase for phoenix)

```sql
select * from "rengine"."t_dws_event_with_aggregated_of_10m" where "ROW"='2210162230:jameswong1234@gmail.com:iot_temp_warn' limit 1;

+----------------------------------------------------------+--------------+-------+-----+-------+-------+----------+----------+----------------+-------------------------+ ...
|                           ROW                            |  updateTime  | count | avg |  max  |  min  | variance |  matches | matchesVersion |        principals       |
+----------------------------------------------------------+--------------+-------+-----+-------+-------+----------+----------+----------------+-------------------------+ ...
|     2210162230:jameswong1234@gmail.com:iot_temp_warn     | 221016224005 | 199   | 68  |  84   |  63   |    9     |    21    |     10001:9    | jameswong1234@gmail.com |
+----------------------------------------------------------+--------------+-------------+-------+-------+----------+----------+----------------+-------------------------+ ...
1 row selected (0.019 seconds)
```

- Notice: The `matchesVersion` represents the workflow version number corresponding to the aggregation computing, which can be used history to calculate the backtracking. format as: (**workflowId:version**)

## Evaluator DWS aggregates data + rules workflow computing

```bash
#TODO
```

## FAQ

### storage aggregated write to mongo ??? (TODO)

