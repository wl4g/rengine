# Rengine for Architectures

- ![Global](../shots/rengine_architecture.png)

## Collector/Eventbus collect Event to MQ (Model definition)

- for example1: (see: [com/wl4g/rengine/common/event/RengineEvent.java](../../common/src/main/java/com/wl4g/rengine/common/event/RengineEvent.java))

```json
{
    "type": "iot_temp_warn",
    "observedTime": 1665931162234,
    "body": "69(℃)",
    "source": {
        "time": 1665931161234,
        "principals": [
            "jameswong1234@gmail.com"
        ],
        "location": {
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
    "attributes": {
        "org": "AS15169 Google LLC",
        "asn": "AS15169",
        "email": "network-abuse@google.com",
        "phone": "+1-650-253-0000"
    }
}
```

## Flink directly sink to ODS Table (HBase for phoenix)

```sql
select * from "rengine"."t_ods_event" where "ROW"='487221015232230:jameswong1234@gmail.com:iot_temp_warn:US:California:Mountain_View' limit 1;

+-----------------------------------------------------------------------------------+--------------+------+--------------+-------------------------+-------------------+--------------+----------------------------------------+ ...
|                                       ROW                                         | observedTime | body |  sourceTime  |    sourcePrincipals     | locationIpAddress | locationIpv6 |               locationIsp              |
+-----------------------------------------------------------------------------------+--------------+------+--------------+-------------------------+-------------------+--------------+----------------------------------------+ ...
| 234221016223921:jameswong1234@gmail.com:iot_temp_warn:US:California:Mountain_View | 221016223922 | 52   | 221016223921 | jameswong1234@gmail.com | 8.8.8.1           | 0            | Google Cloud (US) Technology Co., Ltd. |
+-----------------------------------------------------------------------------------+--------------+------+--------------+-------------------------+-------------------+--------------+----------------------------------------+ ...
1 row selected (0.017 seconds)
```

## Flink aggregated sink to DWS Table (HBase for phoenix)

```sql
select * from "rengine"."t_dws_event_with_aggregated_of_hour" where "ROW"='22101523:jameswong1234@gmail.com:iot_temp_warn' limit 1;

+----------------------------------------------------------+-------+-----+-------+-------+----------+-------------------------+ ...
|                           ROW                            | count | avg |  max  |  min  | variance |    sourcePrincipals     |
+----------------------------------------------------------+-------+-----+-------+-------+----------+-------------------------+ ...
|      22101523:jameswong1234@gmail.com:iot_temp_warn      | 199   | 68  |  84   |  63   |    9     | jameswong1234@gmail.com |
+----------------------------------------------------------+-------------+-------+-------+----------+-------------------------+ ...
1 row selected (0.019 seconds)
```

## Evaluator DWS aggregates data + rules workflow computing

```bash
#TODO
```

