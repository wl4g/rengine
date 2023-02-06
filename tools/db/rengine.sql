/*
 Navicat Premium Data Transfer

 Source Server         : mongo#local
 Source Server Type    : MongoDB
 Source Server Version : 40406
 Source Host           : localhost:27017
 Source Schema         : rengine

 Target Server Type    : MongoDB
 Target Server Version : 40406
 File Encoding         : 65001

 Date: 07/02/2023 00:21:31
*/


// ----------------------------
// Collection structure for sys_dicts
// ----------------------------
db.getCollection("sys_dicts").drop();
db.createCollection("sys_dicts");

// ----------------------------
// Documents of sys_dicts
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405952"),
    type: "ENGINE_EXECUTION_CUSTOM_RESP_TPL",
    key: "dingtalk",
    value: "{\"msg_signature\":\"%s\",\"timeStamp\":\"%s\",\"nonce\":\"%s\",\"encrypt\":\"%s\"}",
    sort: NumberInt("0"),
    orgCode: "string",
    enable: NumberInt("1"),
    labels: [
        "string"
    ],
    remark: "see:https://open.dingtalk.com/document/org/push-events",
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.model.SaveDict"
} ]);
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405953"),
    type: "API_CONFIG_DEFINITION",
    key: "sdk_datasource_mongo",
    value: "[{\"type\":\"string\",\"name\":\"connectionString\",\"defaultValue\":\"localhost:9092\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null}]",
    sort: NumberInt("0"),
    orgCode: "string",
    enable: NumberInt("1"),
    labels: [
        "string"
    ],
    remark: null,
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.model.SaveDict"
} ]);
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405954"),
    type: "API_CONFIG_DEFINITION",
    key: "sdk_datasource_redis",
    value: "[{\"type\":\"array\",\"name\":\"nodes\",\"defaultValue\":\"localhost:6379,localhost:6380,localhost:6381,localhost:7379,localhost:7380,localhost:7381\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"username\",\"defaultValue\":null,\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"password\",\"defaultValue\":null,\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"clientName\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"connTimeout\",\"defaultValue\":\"10000\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"soTimeout\",\"defaultValue\":\"10000\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maxAttempts\",\"defaultValue\":3,\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"database\",\"defaultValue\":\"0\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"safeMode\",\"defaultValue\":\"true\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"object\",\"name\":\"poolConfig\",\"defaultValue\":null,\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null,\"childrens\":[{\"type\":\"int\",\"name\":\"maxIdle\",\"defaultValue\":\"5\",\"required\":false,\"maxValue\":1000,\"minValue\":1,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"minIdle\",\"defaultValue\":\"\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maxTotal\",\"defaultValue\":\"10\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"lifo\",\"defaultValue\":true,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"fairness\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"maxWait\",\"defaultValue\":\"10000\",\"required\":true,\"maxValue\":null,\"minValue\":1,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"minEvictableIdleMs\",\"defaultValue\":\"1800000\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"evictorShutdownTimeoutMs\",\"defaultValue\":\"10000\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"softMinEvictableIdleMs\",\"defaultValue\":\"-1\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"numTestsPerEvictionRun\",\"defaultValue\":\"3\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},,{\"type\":\"int64\",\"name\":\"durationBetweenEvictionRunsMs\",\"defaultValue\":\"-1\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null}{\"type\":\"boolean\",\"name\":\"testOnCreate\",\"defaultValue\":\"false\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testOnBorrow\",\"defaultValue\":\"false\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testOnReturn\",\"defaultValue\":\"false\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testWhileIdle\",\"defaultValue\":\"true\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"blockWhenExhausted\",\"defaultValue\":\"true\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null}]}]}]",
    sort: NumberInt("0"),
    orgCode: "string",
    enable: NumberInt("1"),
    labels: [
        "string"
    ],
    remark: null,
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.model.SaveDict"
} ]);
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405955"),
    type: "API_CONFIG_DEFINITION",
    key: "sdk_datasource_jdbc",
    value: "[{\"type\":\"int\",\"name\":\"fetchDirection\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"fetchSize\",\"defaultValue\":10000,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maxFieldSize\",\"defaultValue\":64,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maxRows\",\"defaultValue\":1024,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"queryTimeoutMs\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"driverClassName\",\"defaultValue\":\"com.mysql.cj.jdbc.Driver\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"jdbcUrl\",\"defaultValue\":\"jdbc:mysql://localhost:3306/mysql?useUnicode=true&serverTimezone=UTC&characterEncoding=utf-8&useSSL=false\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"username\",\"defaultValue\":\"root\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"password\",\"defaultValue\":\"123456\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"connectionTimeout\",\"defaultValue\":\"123456\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"validationTimeout\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"validationTestSql\",\"defaultValue\":\"SELECT 1\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"long\",\"name\":\"idleTimeout\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"long\",\"name\":\"softMinIdleTimeout\",\"defaultValue\":-1,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"long\",\"name\":\"maxConnLifeTime\",\"defaultValue\":-1,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"long\",\"name\":\"evictionRunsBetweenTime\",\"defaultValue\":-1,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"initPoolSize\",\"defaultValue\":5,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maximumPoolSize\",\"defaultValue\":20,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"minimumIdle\",\"defaultValue\":1,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"autoCommit\",\"defaultValue\":true,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"cacheState\",\"defaultValue\":true,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testOnBorrow\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testOnCreate\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testOnReturn\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testWhileIdle\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null}]",
    sort: NumberInt("0"),
    orgCode: "string",
    enable: NumberInt("1"),
    labels: [
        "string"
    ],
    remark: null,
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.model.SaveDict"
} ]);
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405956"),
    type: "API_CONFIG_DEFINITION",
    key: "sdk_datasource_kafka",
    value: "[{\"type\":\"string\",\"name\":\"key_serializer\",\"defaultValue\":\"org.apache.kafka.common.serialization.StringSerializer\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"bootstrap_servers\",\"defaultValue\":\"localhost:9092\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"client_dns_lookup\",\"defaultValue\":\"use_all_dns_ips\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"metadata_max_age_ms\",\"defaultValue\":\"300000\",\"required\":true,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"send_buffer_bytes\",\"defaultValue\":\"131072\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"receive_buffer_bytes\",\"defaultValue\":\"65536\",\"required\":true,\"maxValue\":null,\"minValue\":-1,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"clientId\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"clientRack\",\"defaultValue\":\"\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"reconnect_backoff_ms\",\"defaultValue\":\"50\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"reconnect_backoff_max_ms\",\"defaultValue\":\"1000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"retries\",\"defaultValue\":\"50\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"retry_backoff_ms\",\"defaultValue\":\"1000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"metrics_sample_window_ms\",\"defaultValue\":\"3000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"metricsNumSamples\",\"defaultValue\":\"2\",\"required\":false,\"maxValue\":1,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"metrics_recording_level\",\"defaultValue\":\"INFO\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"array\",\"name\":\"metric_reporters\",\"defaultValue\":null,\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"security_protocol\",\"defaultValue\":\"PLAINTEXT\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"socket_connection_setup_timeout_ms\",\"defaultValue\":\"10000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"socket_connection_setup_timeout_max_ms\",\"defaultValue\":\"30000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"connections_max_idle_ms\",\"defaultValue\":\"540000\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"request_timeout_ms\",\"defaultValue\":\"30000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"group_id\",\"defaultValue\":\"default-rengine-controller\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"group_instance_id\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"max_poll_interval_ms\",\"defaultValue\":\"300000\",\"required\":false,\"maxValue\":null,\"minValue\":1,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"rebalance_timeout_ms\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"session_timeout_ms\",\"defaultValue\":\"45000\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"heartbeat_interval_ms\",\"defaultValue\":\"3000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"default_api_timeout_ms\",\"defaultValue\":\"60000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"buffer_memory\",\"defaultValue\":\"33554432\",\"required\":false,\"maxValue\":null,\"minValue\":5000,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"acks\",\"defaultValue\":\"all\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"compression_type\",\"defaultValue\":\"none\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"batch_size\",\"defaultValue\":\"16384\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"linger_ms\",\"defaultValue\":\"0\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"delivery_timeout_ms\",\"defaultValue\":\"120000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"send_buffer\",\"defaultValue\":\"131072\",\"required\":false,\"maxValue\":null,\"minValue\":-1,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"receive_buffer\",\"defaultValue\":\"32768\",\"required\":false,\"maxValue\":null,\"minValue\":-1,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"max_request_size\",\"defaultValue\":\"1048576\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"max_block_ms\",\"defaultValue\":\"60000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"metadata_max_idle_ms\",\"defaultValue\":\"300000\",\"required\":false,\"maxValue\":null,\"minValue\":5000,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"max_in_flight_requests_per_connection\",\"defaultValue\":\"5\",\"required\":false,\"maxValue\":null,\"minValue\":1,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"transaction_timeout_ms\",\"defaultValue\":\"60000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null}]",
    sort: NumberInt("0"),
    orgCode: "string",
    enable: NumberInt("1"),
    labels: [
        "string"
    ],
    remark: "see:org.apache.kafka.clients.producer.ProducerConfig",
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.model.SaveDict"
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for sys_global_sequences
// ----------------------------
db.getCollection("sys_global_sequences").drop();
db.createCollection("sys_global_sequences");

// ----------------------------
// Documents of sys_global_sequences
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("sys_global_sequences").insert([ {
    _id: "RuleScript.revision",
    seq: NumberInt("2")
} ]);
db.getCollection("sys_global_sequences").insert([ {
    _id: "WorkflowGraph.revision",
    seq: NumberInt("2")
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for sys_notifications
// ----------------------------
db.getCollection("sys_notifications").drop();
db.createCollection("sys_notifications");

// ----------------------------
// Documents of sys_notifications
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("sys_notifications").insert([ {
    _id: NumberLong("6295643646476288"),
    enable: NumberInt("1"),
    labels: [
        "string"
    ],
    properties: {
        type: "DINGTALK",
        appKey: "dingbhyrzjxx6qjhjcdr",
        appSecret: "9N8nPoRB-gyeYXUJMHDU3YauNIwHVk5wtUEVROp5XgDsPjlu47bKl_xN067lQPzK",
        token: "H792gCFQzB2BP",
        aesKey: "bnGUZ3cqz6GgsG7B8LxBQeLcwYFDXKGMwXczNXBwCg9",
        corpId: "dingbhyrzjxx6qjhjcdr",
        users: [
            {
                mobile: "180xxxxxxxx",
                userId: "6165471647114842627"
            }
        ],
        scenesGroups: [
            {
                title: "SAFECLOUD自动化监控告警测试群1",
                templateId: "4ba6847f-b9b0-42ca-96ea-22c4ed8a3fbd",
                chatId: "chat7b43308b68ec835f9ba9a5e440a4cce6",
                openConversationId: "cide3M7a7Ldu5TG9+8BH75JWA==",
                ownerUserId: "6165471647114842627",
                adminUserIds: [
                    "6165471647114842627"
                ],
                userIds: [
                    "6165471647114842627"
                ],
                uuid: null,
                icon: null,
                mentionAllAuthority: null,
                showHistoryType: null,
                validationType: null,
                searchable: null,
                chatVannedType: null,
                managementType: null,
                onlyAdminCanDing: null,
                allMembersCanCreateMcsConf: null,
                allMembersCanCreateCalendar: null,
                groupEmailDisabled: null,
                onlyAdminCanSetMsgTop: null,
                addFriendForbidden: null,
                groupLiveSwitch: null,
                membersToAdminChat: null
            }
        ]
    },
    orgCode: "string",
    remark: "local test",
    createDate: ISODate("2023-01-07T11:22:48.314Z"),
    updateDate: ISODate("2023-01-07T11:22:48.314Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Notification$DingtalkConfig"
} ]);
db.getCollection("sys_notifications").insert([ {
    _id: NumberLong("6295643646476289"),
    enable: NumberInt("1"),
    labels: [
        "string"
    ],
    properties: {
        type: "EMAIL",
        protocol: "smtp",
        host: "smtp.exmail.qq.com",
        port: 465,
        username: "sysnotification01@sunwuu.com",
        password: "CAGd9ZHZMCUHXEVV",
        properties: { }
    },
    orgCode: "string",
    remark: "local test",
    createDate: ISODate("2023-01-07T11:22:48.314Z"),
    updateDate: ISODate("2023-01-07T11:22:48.314Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Notification$DingtalkConfig"
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for t_aggregates
// ----------------------------
db.getCollection("t_aggregates").drop();
db.createCollection("t_aggregates");

// ----------------------------
// Documents of t_aggregates
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("t_aggregates").insert([ {
    _id: NumberLong("6220869710864384"),
    eventType: "ecommerce_trade_gift",
    aggregated: {
        type: "SUM",
        keyBy: "trade_ammount"
    },
    value: "99",
    labels: [
        "foo",
        "bar"
    ],
    remark: "test",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: 0
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for t_datasources
// ----------------------------
db.getCollection("t_datasources").drop();
db.createCollection("t_datasources");

// ----------------------------
// Documents of t_datasources
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("t_datasources").insert([ {
    _id: NumberLong("6220869710864222"),
    name: "default",
    enable: NumberInt("1"),
    labels: [
        "foo",
        "bar"
    ],
    properties: {
        type: "MONGO",
        connectionString: "mongodb://localhost:27017/rengine"
    },
    remark: "local test",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0")
} ]);
db.getCollection("t_datasources").insert([ {
    _id: NumberLong("6220869710864223"),
    name: "default",
    enable: NumberInt("1"),
    labels: [
        "foo",
        "bar"
    ],
    properties: {
        type: "JDBC",
        fetchSize: 1024,
        driverClassName: "com.mysql.cj.jdbc.Driver",
        jdbcUrl: "jdbc:mysql://localhost:3306/mysql?useUnicode=true&serverTimezone=UTC&characterEncoding=utf-8&useSSL=false",
        username: "root",
        password: "zzx!@#$%",
        maximumPoolSize: 10,
        validationTimeout: 3000,
        connectionTimeout: 30000
    },
    remark: "local test",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0")
} ]);
db.getCollection("t_datasources").insert([ {
    _id: NumberLong("6220869710864224"),
    name: "default",
    enable: NumberInt("1"),
    labels: [
        "foo",
        "bar"
    ],
    properties: {
        type: "REDIS",
        jedisConfig: {
            nodes: [
                "localhost:6379",
                "localhost:6380",
                "localhost:6381",
                "localhost:7379",
                "localhost:7380",
                "localhost:7381"
            ],
            password: "zzx!@#$%",
            connTimeout: 10000
        }
    },
    remark: "local test",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0")
} ]);
db.getCollection("t_datasources").insert([ {
    _id: NumberLong("6220869710864225"),
    name: "default",
    enable: NumberInt("1"),
    labels: [
        "foo",
        "bar"
    ],
    properties: {
        type: "KAFKA",
        bootstrapServers: "localhost:9092"
    },
    remark: "local test",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0")
} ]);
db.getCollection("t_datasources").insert([ {
    _id: NumberLong("6220869710864227"),
    name: "sc-uat-redis",
    enable: NumberInt("1"),
    labels: [
        "foo",
        "bar"
    ],
    properties: {
        type: "REDIS",
        jedisConfig: {
            nodes: [
                "owner-node5:6379",
                "owner-node5:6380",
                "owner-node5:6381",
                "owner-node5:7379",
                "owner-node5:7380",
                "owner-node5:7381"
            ],
            password: "zzx!@#$%",
            connTimeout: 10000
        }
    },
    remark: "test",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0")
} ]);
db.getCollection("t_datasources").insert([ {
    _id: NumberLong("6220869710864228"),
    name: "sc-uat-mysql",
    enable: NumberInt("1"),
    labels: [
        "foo",
        "bar"
    ],
    properties: {
        type: "JDBC",
        fetchSize: 1024,
        driverClassName: "com.mysql.cj.jdbc.Driver",
        jdbcUrl: "jdbc:mysql://owner-node5:3306/mysql?useUnicode=true&serverTimezone=UTC&characterEncoding=utf-8&useSSL=false",
        username: "root",
        password: "zzx!@#$%"
    },
    remark: "test",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0")
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for t_rule_scripts
// ----------------------------
db.getCollection("t_rule_scripts").drop();
db.createCollection("t_rule_scripts");

// ----------------------------
// Documents of t_rule_scripts
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("t_rule_scripts").insert([ {
    _id: NumberLong("6150868953455678"),
    revision: NumberLong("1"),
    ruleId: NumberLong("6150869239922688"),
    uploadIds: [
        NumberLong("6150869710864384"),
        NumberLong("6150869710864385")
    ],
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "test"
    ],
    remark: "string",
    createDate: ISODate("2022-09-27T04:50:22.304Z"),
    updateDate: ISODate("2022-09-27T04:50:22.304Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Rule$RuleScript"
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for t_rules
// ----------------------------
db.getCollection("t_rules").drop();
db.createCollection("t_rules");

// ----------------------------
// Documents of t_rules
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("t_rules").insert([ {
    _id: NumberLong("6150869239922688"),
    name: "default",
    engine: "JS",
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "test"
    ],
    remark: "string",
    createDate: ISODate("2022-09-27T04:50:39.789Z"),
    updateDate: ISODate("2022-09-27T04:50:39.789Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Rule"
} ]);
db.getCollection("t_rules").insert([ {
    _id: NumberLong("6150869239922689"),
    name: "default",
    engine: "JS",
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "test"
    ],
    remark: "string",
    createDate: ISODate("2022-09-27T04:50:39.789Z"),
    updateDate: ISODate("2022-09-27T04:50:39.789Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Rule"
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for t_sceneses
// ----------------------------
db.getCollection("t_sceneses").drop();
db.createCollection("t_sceneses");

// ----------------------------
// Documents of t_sceneses
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("t_sceneses").insert([ {
    _id: NumberLong("6150865561468928"),
    name: "Iot通用设备温度告警",
    scenesCode: "iot_temp_warning",
    orgCode: "root",
    enable: NumberInt("1"),
    labels: [
        "test"
    ],
    remark: "test1",
    createDate: ISODate("2022-09-27T04:46:55.274Z"),
    updateDate: ISODate("2022-09-27T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Scenes"
} ]);
db.getCollection("t_sceneses").insert([ {
    _id: NumberLong("6150865561468929"),
    name: "电商充值赠送活动",
    scenesCode: "ecommerce_trade_gift",
    orgCode: "root",
    enable: NumberInt("1"),
    labels: [
        "test"
    ],
    remark: "test2",
    createDate: ISODate("2022-09-27T04:46:55.274Z"),
    updateDate: ISODate("2022-09-27T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Scenes"
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for t_schedule_job_logs
// ----------------------------
db.getCollection("t_schedule_job_logs").drop();
db.createCollection("t_schedule_job_logs");

// ----------------------------
// Documents of t_schedule_job_logs
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for t_schedule_triggers
// ----------------------------
db.getCollection("t_schedule_triggers").drop();
db.createCollection("t_schedule_triggers");

// ----------------------------
// Documents of t_schedule_triggers
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("t_schedule_triggers").insert([ {
    _id: NumberLong("62208697108652456"),
    name: "monitor_node_vm",
    enable: NumberInt("0"),
    labels: [
        "safecloud",
        "production",
        "monitor",
        "node",
        "vm"
    ],
    monitorExecution: true,
    failover: true,
    misfire: true,
    timeZone: "GMT+08:00",
    maxTimeoutMs: 30000,
    runState: null,
    properties: {
        type: "EXECUTION_SCHEDULER",
        cron: "0/5 * * * * ?",
        requests: [
            {
                clientId: "JVqEpEwIaqkEkeD5",
                clientSecret: "Uf6nJDyJQHKRP43ycl9vZ9zs7s1nyu77",
                scenesCodes: [
                    "ecommerce_trade_gift"
                ],
                bestEffort: false,
                timeout: 10000,
                args: {
                    foo1: "bar1"
                }
            }
        ],
        _class: "com.wl4g.rengine.common.entity.ScheduleTrigger$ExecutionScheduleConfig"
    },
    remark: "safecloud monitor for vm",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleTrigger"
} ]);
db.getCollection("t_schedule_triggers").insert([ {
    _id: NumberLong("62208697108652457"),
    name: "monitor_app_mysql_elec_bill_history",
    enable: NumberInt("0"),
    labels: [
        "safecloud",
        "production",
        "monitor",
        "app",
        "mysql",
        "elec_bill_history"
    ],
    monitorExecution: true,
    failover: true,
    misfire: true,
    timeZone: "GMT+08:00",
    maxTimeoutMs: 30000,
    runState: null,
    properties: {
        type: "EXECUTION_SCHEDULER",
        cron: "0/5 * * * * ?",
        requests: [
            {
                clientId: "JVqEpEwIaqkEkeD5",
                clientSecret: "Uf6nJDyJQHKRP43ycl9vZ9zs7s1nyu77",
                scenesCodes: [
                    "ecommerce_trade_gift"
                ],
                bestEffort: false,
                timeout: 10000,
                args: {
                    foo1: "bar1"
                }
            }
        ],
        _class: "com.wl4g.rengine.common.entity.ScheduleTrigger$ExecutionScheduleConfig"
    },
    remark: "safecloud monitor for mysql",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleTrigger"
} ]);
db.getCollection("t_schedule_triggers").insert([ {
    _id: NumberLong("62208697108652458"),
    name: "monitor_app_mysql_water_bill_history",
    enable: NumberInt("0"),
    labels: [
        "safecloud",
        "production",
        "monitor",
        "app",
        "mysql",
        "water_bill_history"
    ],
    monitorExecution: true,
    failover: true,
    misfire: true,
    timeZone: "GMT+08:00",
    maxTimeoutMs: 30000,
    runState: null,
    properties: {
        type: "EXECUTION_SCHEDULER",
        cron: "0/5 * * * * ?",
        requests: [
            {
                clientId: "JVqEpEwIaqkEkeD5",
                clientSecret: "Uf6nJDyJQHKRP43ycl9vZ9zs7s1nyu77",
                scenesCodes: [
                    "ecommerce_trade_gift"
                ],
                bestEffort: false,
                timeout: 10000,
                args: {
                    foo1: "bar1"
                }
            }
        ],
        _class: "com.wl4g.rengine.common.entity.ScheduleTrigger$ExecutionScheduleConfig"
    },
    remark: "safecloud monitor for mysql",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleTrigger"
} ]);
db.getCollection("t_schedule_triggers").insert([ {
    _id: NumberLong("62208697108652459"),
    name: "monitor_emr_spark_history_stream_job",
    enable: NumberInt("0"),
    labels: [
        "safecloud",
        "production",
        "monitor",
        "emr",
        "spark",
        "history_stream_job"
    ],
    monitorExecution: true,
    failover: true,
    misfire: true,
    timeZone: "GMT+08:00",
    maxTimeoutMs: 30000,
    runState: null,
    properties: {
        type: "EXECUTION_SCHEDULER",
        cron: "0/5 * * * * ?",
        requests: [
            {
                clientId: "JVqEpEwIaqkEkeD5",
                clientSecret: "Uf6nJDyJQHKRP43ycl9vZ9zs7s1nyu77",
                scenesCodes: [
                    "ecommerce_trade_gift"
                ],
                bestEffort: false,
                timeout: 10000,
                args: {
                    foo1: "bar1"
                }
            }
        ],
        _class: "com.wl4g.rengine.common.entity.ScheduleTrigger$ExecutionScheduleConfig"
    },
    remark: "safecloud monitor for emr",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleTrigger"
} ]);
db.getCollection("t_schedule_triggers").insert([ {
    _id: NumberLong("62208697108652460"),
    name: "subscribe_kafka_notification_warning",
    monitorExecution: true,
    failover: true,
    misfire: true,
    timeZone: "GMT+08:00",
    maxTimeDiffSeconds: NumberInt("-1"),
    reconcileIntervalMinutes: NumberInt("0"),
    maxTimeoutMs: NumberLong("30000"),
    runState: "RUNNING",
    properties: {
        topics: [
            "test_topic"
        ],
        concurrency: NumberInt("1"),
        autoAcknowledgment: true,
        request: {
            clientId: "JVqEpEwIaqkEkeD5",
            clientSecret: "Uf6nJDyJQHKRP43ycl9vZ9zs7s1nyu77",
            scenesCodes: [
                "ecommerce_trade_gift"
            ],
            timeout: NumberLong("10000"),
            bestEffort: false,
            args: {
                foo1: "bar1"
            }
        },
        consumerOptions: {
            keySerializer: "org.apache.kafka.common.serialization.StringDeserializer",
            valueSerializer: "org.apache.kafka.common.serialization.StringDeserializer",
            bootstrapServers: "localhost:9092",
            clientDnsLookup: "use_all_dns_ips",
            metadataMaxAgeMs: NumberLong("300000"),
            sendBufferBytes: NumberInt("131072"),
            receiveBufferBytes: NumberInt("65536"),
            clientRack: "",
            reconnectBackoffMs: NumberLong("50"),
            reconnectBackoffMaxMs: NumberLong("1000"),
            retries: NumberInt("2147483647"),
            retryBackoffMs: NumberLong("100"),
            metricsSampleWindowMs: NumberLong("3000"),
            metricsNumSamples: NumberInt("2"),
            metricsRecordingLevel: "INFO",
            metricsReporters: [ ],
            securityProtocol: "PLAINTEXT",
            socketConnectionSetupTimeoutMs: NumberLong("10000"),
            socketConnectionSetupTimeoutMaxMs: NumberLong("30000"),
            connectionsMaxIdleMs: NumberLong("540000"),
            requestTimeoutMs: NumberInt("30000"),
            groupId: "default-rengine-controller-subscriber",
            maxPollIntervalMs: NumberInt("300000"),
            sessionTimeoutMs: NumberInt("45000"),
            heartbeatIntervalMs: NumberInt("3000"),
            defaultApiTimeoutMs: NumberInt("60000"),
            enableAutoCommit: false,
            autoCommitIntervalMs: NumberInt("5000"),
            autoOffsetReset: "latest",
            fetchMinBytes: NumberInt("1"),
            fetchMaxBytes: NumberInt("52428800"),
            fetchMaxWaitMs: NumberInt("500")
        },
        type: "KAFKA_SUBSCRIBE_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleTrigger$KafkaSubscribeScheduleConfig"
    },
    enable: NumberInt("1"),
    labels: [
        "safecloud",
        "production",
        "kafka subscribe",
        "notification"
    ],
    remark: "safecloud subscribe for kafka",
    updateDate: ISODate("2023-02-06T08:49:01.296Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleTrigger"
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for t_uploads
// ----------------------------
db.getCollection("t_uploads").drop();
db.createCollection("t_uploads");

// ----------------------------
// Documents of t_uploads
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("t_uploads").insert([ {
    _id: NumberLong("6150869710864384"),
    uploadType: "USER_LIBRARY_WITH_JS",
    objectPrefix: "library/js/commons-lang-3.0.0.js",
    filename: "commons-lang-3.0.0.js",
    extension: ".js",
    size: NumberLong("1"),
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "test"
    ],
    remark: "test",
    updateDate: ISODate("2022-09-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.UploadObject"
} ]);
db.getCollection("t_uploads").insert([ {
    _id: NumberLong("6150869710864385"),
    uploadType: "USER_LIBRARY_WITH_JS",
    objectPrefix: "library/js/test-sdk-all-examples.js",
    filename: "test-sdk-all-examples.js",
    extension: ".js",
    size: NumberLong("1"),
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "test"
    ],
    remark: "test",
    updateDate: ISODate("2022-09-27T04:51:08.533Z"),
    _class: "com.wl4g.rengine.common.entity.UploadObject",
    delFlag: NumberInt("0")
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for t_workflow_graphs
// ----------------------------
db.getCollection("t_workflow_graphs").drop();
db.createCollection("t_workflow_graphs");

// ----------------------------
// Documents of t_workflow_graphs
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("t_workflow_graphs").insert([ {
    _id: NumberLong("6150868953448888"),
    revision: NumberLong("1"),
    workflowId: NumberLong("6150868953448448"),
    nodes: [
        {
            "@type": "BOOT",
            id: "0",
            name: "The Boot",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            },
            ruleId: null
        },
        {
            "@type": "PROCESS",
            id: "11",
            name: "预处理(如篡改当前时间以用于测试目的)",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            },
            ruleId: NumberLong("6150869239922688")
        },
        {
            "@type": "RELATION",
            id: "21",
            name: "当前时间是否满足(10.1~10.8)",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            },
            ruleId: NumberLong("6150869239922689")
        },
        {
            "@type": "LOGICAL",
            id: "31",
            name: "ALL_AND逻辑运算",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            },
            logical: "ALL_AND",
            ruleId: null
        },
        {
            "@type": "LOGICAL",
            id: "41",
            name: "AND逻辑运算",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            },
            logical: "AND",
            ruleId: null
        },
        {
            "@type": "LOGICAL",
            id: "42",
            name: "AND逻辑运算",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            },
            logical: "AND",
            ruleId: null
        },
        {
            "@type": "RELATION",
            id: "51",
            name: "充值是否>=120元",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            },
            ruleId: NumberLong("6150869239922688")
        },
        {
            "@type": "LOGICAL",
            id: "52",
            name: "AND逻辑运算",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            },
            logical: "AND",
            ruleId: null
        },
        {
            "@type": "RELATION",
            id: "53",
            name: "当前时间是否满足(10.5~10.8)",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            },
            ruleId: NumberLong("6150869239922689")
        },
        {
            "@type": "RELATION",
            id: "54",
            name: "充值是否>=50元",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            },
            ruleId: NumberLong("6150869239922689")
        },
        {
            "@type": "FAILBACK",
            id: "62",
            name: "如果赠送余额失败则执行回退规则",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            },
            ruleId: NumberLong("6150869239922689")
        },
        {
            "@type": "RUN",
            id: "63",
            name: "赠送20积分",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            },
            ruleId: NumberLong("6150869239922689")
        },
        {
            "@type": "RUN",
            id: "71",
            name: "赠送10元余额",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            },
            ruleId: NumberLong("6150869239922689")
        }
    ],
    connections: [
        {
            name: "Unnamed Connection",
            to: "11",
            from: "0",
            attributes: null
        },
        {
            name: "Unnamed Connection",
            to: "21",
            from: "11",
            attributes: null
        },
        {
            name: "Unnamed Connection",
            to: "31",
            from: "21",
            attributes: null
        },
        {
            name: "Unnamed Connection",
            to: "41",
            from: "31",
            attributes: null
        },
        {
            name: "Unnamed Connection",
            to: "42",
            from: "31",
            attributes: null
        },
        {
            name: "Unnamed Connection",
            to: "51",
            from: "41",
            attributes: null
        },
        {
            name: "Unnamed Connection",
            to: "52",
            from: "41",
            attributes: null
        },
        {
            name: "Unnamed Connection",
            to: "53",
            from: "42",
            attributes: null
        },
        {
            name: "Unnamed Connection",
            to: "54",
            from: "42",
            attributes: null
        },
        {
            name: "Unnamed Connection",
            to: "61",
            from: "51",
            attributes: null
        },
        {
            name: "Unnamed Connection",
            to: "62",
            from: "52",
            attributes: null
        },
        {
            name: "Unnamed Connection",
            to: "63",
            from: "54",
            attributes: null
        },
        {
            name: "Unnamed Connection",
            to: "71",
            from: "62",
            attributes: null
        }
    ],
    attributes: {
        foo: "bar",
        openConversationId: "cidG+niQ3Ny/NwUc5KE7mANUQ==",
        robotCode: "dingbhyrzjxx6qjhjcdr",
        scenesGroupV2OwnerUserId: "6165471647114842627",
        scenesGroupV2AdminUserIds: "6165471647114842627",
        scenesGroupV2UserIds: "6165471647114842627"
    },
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "test"
    ],
    remark: "string",
    createDate: ISODate("2022-09-27T04:50:22.304Z"),
    updateDate: ISODate("2022-09-27T04:50:22.304Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.WorkflowGraph"
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for t_workflows
// ----------------------------
db.getCollection("t_workflows").drop();
db.createCollection("t_workflows");

// ----------------------------
// Documents of t_workflows
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("t_workflows").insert([ {
    _id: NumberLong("6150868953448448"),
    name: "电商充值赠送活动流程",
    scenesId: NumberLong("6150865561468929"),
    engine: "JS",
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "test"
    ],
    remark: "string",
    createDate: ISODate("2022-09-27T04:50:22.304Z"),
    updateDate: ISODate("2022-09-27T04:50:22.304Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Workflow"
} ]);
session.commitTransaction(); session.endSession();
