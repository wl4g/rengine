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

 Date: 07/03/2023 10:42:28
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
    _id: NumberLong("6305460145405950"),
    type: "ENGINE_TYPE",
    key: "JS",
    value: "JS",
    sort: NumberInt("0"),
    orgCode: "string",
    enable: NumberInt("1"),
    labels: [
        "string"
    ],
    remark: "Engine type for JS (graal.js)",
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.entity.Dict"
} ]);
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405951"),
    type: "ENGINE_TYPE",
    key: "GROOVY",
    value: "GROOVY",
    sort: NumberInt("0"),
    orgCode: "string",
    enable: NumberInt("1"),
    labels: [
        "string"
    ],
    remark: "Engine type for GROOVY",
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.entity.Dict"
} ]);
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
    _class: "com.wl4g.rengine.service.entity.Dict"
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
    _class: "com.wl4g.rengine.service.entity.Dict"
} ]);
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405954"),
    type: "API_CONFIG_DEFINITION",
    key: "sdk_datasource_redis",
    value: "[{\"type\": \"array\", \t\"name\": \"nodes\", \t\"defaultValue\": \"localhost:6379,localhost:6380,localhost:6381,localhost:7379,localhost:7380,localhost:7381\", \t\"required\": true, \t\"maxValue\": null, \t\"minValue\": null, \t\"help\": \"\", \t\"unit\": null }, { \t\"type\": \"string\", \t\"name\": \"username\", \t\"defaultValue\": null, \t\"required\": true, \t\"maxValue\": null, \t\"minValue\": null, \t\"help\": \"\", \t\"unit\": null }, { \t\"type\": \"string\", \t\"name\": \"password\", \t\"defaultValue\": null, \t\"required\": true, \t\"maxValue\": null, \t\"minValue\": null, \t\"help\": \"\", \t\"unit\": null }, { \t\"type\": \"string\", \t\"name\": \"clientName\", \t\"defaultValue\": null, \t\"required\": false, \t\"maxValue\": null, \t\"minValue\": null, \t\"help\": \"\", \t\"unit\": null }, { \t\"type\": \"int\", \t\"name\": \"connTimeout\", \t\"defaultValue\": \"10000\", \t\"required\": true, \t\"maxValue\": null, \t\"minValue\": null, \t\"help\": \"\", \t\"unit\": null }, { \t\"type\": \"int\", \t\"name\": \"soTimeout\", \t\"defaultValue\": \"10000\", \t\"required\": true, \t\"maxValue\": null, \t\"minValue\": null, \t\"help\": \"\", \t\"unit\": null }, { \t\"type\": \"int\", \t\"name\": \"maxAttempts\", \t\"defaultValue\": 3, \t\"required\": true, \t\"maxValue\": null, \t\"minValue\": null, \t\"help\": \"\", \t\"unit\": null }, { \t\"type\": \"int\", \t\"name\": \"database\", \t\"defaultValue\": \"0\", \t\"required\": false, \t\"maxValue\": null, \t\"minValue\": null, \t\"help\": \"\", \t\"unit\": null }, { \t\"type\": \"boolean\", \t\"name\": \"safeMode\", \t\"defaultValue\": \"true\", \t\"required\": false, \t\"maxValue\": null, \t\"minValue\": null, \t\"help\": \"\", \t\"unit\": null }, { \t\"type\": \"object\", \t\"name\": \"poolConfig\", \t\"defaultValue\": null, \t\"required\": true, \t\"maxValue\": null, \t\"minValue\": null, \t\"help\": \"\", \t\"unit\": null, \t\"childrens\": [{ \t\t\"type\": \"int\", \t\t\"name\": \"maxIdle\", \t\t\"defaultValue\": \"5\", \t\t\"required\": false, \t\t\"maxValue\": 1000, \t\t\"minValue\": 1, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"int\", \t\t\"name\": \"minIdle\", \t\t\"defaultValue\": \"\", \t\t\"required\": true, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"int\", \t\t\"name\": \"maxTotal\", \t\t\"defaultValue\": \"10\", \t\t\"required\": true, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"boolean\", \t\t\"name\": \"lifo\", \t\t\"defaultValue\": true, \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"boolean\", \t\t\"name\": \"fairness\", \t\t\"defaultValue\": false, \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"int64\", \t\t\"name\": \"maxWait\", \t\t\"defaultValue\": \"10000\", \t\t\"required\": true, \t\t\"maxValue\": null, \t\t\"minValue\": 1, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"int64\", \t\t\"name\": \"minEvictableIdleMs\", \t\t\"defaultValue\": \"1800000\", \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"int64\", \t\t\"name\": \"evictorShutdownTimeoutMs\", \t\t\"defaultValue\": \"10000\", \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"int64\", \t\t\"name\": \"softMinEvictableIdleMs\", \t\t\"defaultValue\": \"-1\", \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"int\", \t\t\"name\": \"numTestsPerEvictionRun\", \t\t\"defaultValue\": \"3\", \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"int64\", \t\t\"name\": \"durationBetweenEvictionRunsMs\", \t\t\"defaultValue\": \"-1\", \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"boolean\", \t\t\"name\": \"testOnCreate\", \t\t\"defaultValue\": \"false\", \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"boolean\", \t\t\"name\": \"testOnBorrow\", \t\t\"defaultValue\": \"false\", \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"boolean\", \t\t\"name\": \"testOnReturn\", \t\t\"defaultValue\": \"false\", \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"boolean\", \t\t\"name\": \"testWhileIdle\", \t\t\"defaultValue\": \"true\", \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"boolean\", \t\t\"name\": \"blockWhenExhausted\", \t\t\"defaultValue\": \"true\", \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}] }]",
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
    _class: "com.wl4g.rengine.service.entity.Dict"
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
    _class: "com.wl4g.rengine.service.entity.Dict"
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
    _class: "com.wl4g.rengine.service.entity.Dict"
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
// Collection structure for sys_identity_providers
// ----------------------------
db.getCollection("sys_identity_providers").drop();
db.createCollection("sys_identity_providers");

// ----------------------------
// Documents of sys_identity_providers
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("sys_identity_providers").insert([ {
    _id: NumberLong("6150865561468928"),
    name: "default OIDC client",
    details: {
        type: "OIDC",
        registrationId: "oidc0",
        clientId: "rengine",
        clientSecret: "FvLNs0sbwF3sN4BbyjZ5GBwN819QFCmF",
        authorizationGrantType: "authorization_code",
        redirectUri: "http://rengine.wl4g.io/api/login/oauth2/callback/oidc0",
        scopes: [
            "openid",
            "email",
            "profile",
            "roles"
        ],
        authorizationUri: "https://iam.wl4g.com/realms/master/protocol/openid-connect/auth",
        tokenUri: "https://iam.wl4g.com/realms/master/protocol/openid-connect/token",
        userInfoEndpoint: {
            uri: "https://iam.wl4g.com/realms/master/protocol/openid-connect/userinfo",
            authenticationMethod: "header",
            userNameAttributeName: "preferred_username"
        },
        jwkSetUri: "https://iam.wl4g.com/realms/master/protocol/openid-connect/certs",
        issuerUri: "https://iam.wl4g.com/realms/master",
        configurationMetadata: { }
    },
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [ ],
    remark: "see:https://iam.wl4g.com/realms/master/.well-known/openid-configuration",
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.IdentityProvider"
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
    _class: "com.wl4g.rengine.common.entity.Notification"
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
    _class: "com.wl4g.rengine.common.entity.Notification"
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for sys_users
// ----------------------------
db.getCollection("sys_users").drop();
db.createCollection("sys_users");

// ----------------------------
// Documents of sys_users
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("sys_users").insert([ {
    _id: NumberLong("61508655614689001"),
    username: "root",
    password: "$2y$13$omIlVnJQbPgVxt4cz9eXpeLpJ2so3SCaXqdJSVc.0p5AZmBxtABAy",
    accountNonExpired: false,
    accountNonLocked: false,
    credentialsNonExpired: false,
    enable: NumberInt("1"),
    attributes: { },
    orgCode: "top",
    labels: [
        "admin"
    ],
    remark: "超级管理员",
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.User"
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
// Collection structure for t_controller_logs
// ----------------------------
db.getCollection("t_controller_logs").drop();
db.createCollection("t_controller_logs");

// ----------------------------
// Documents of t_controller_logs
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("t_controller_logs").insert([ {
    _id: NumberLong("6363610652180480"),
    orgCode: null,
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-02-24 19:42:25",
    updateBy: NumberInt("-1"),
    updateDate: "2023-02-24 19:42:31",
    delFlag: NumberInt("0"),
    scheduleId: NumberLong("62208697108652456"),
    jobName: null,
    startupTime: NumberLong("1677238945178"),
    finishedTime: NumberLong("1677238951075"),
    success: false,
    detail: {
        type: "GENERIC_EXECUTION_CONTROLLER",
        results: [
            {
                requestId: "510c46e0-6957-4f8a-872a-9a06e91cbc3d",
                results: [
                    {
                        scenesCode: "vm_health_detect",
                        success: false,
                        valueMap: null,
                        reason: "Failed to execution workflow graph of reason: com.wl4g.rengine.common.exception.EvaluationException: Failed to execute script"
                    }
                ]
            }
        ]
    }
} ]);
db.getCollection("t_controller_logs").insert([ {
    _id: NumberLong("6363610815283200"),
    orgCode: null,
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-02-24 19:42:35",
    updateBy: NumberInt("-1"),
    updateDate: "2023-02-24 19:42:35",
    delFlag: NumberInt("0"),
    scheduleId: NumberLong("62208697108652456"),
    jobName: null,
    startupTime: NumberLong("1677238955088"),
    finishedTime: NumberLong("1677238955244"),
    success: false,
    detail: {
        type: "GENERIC_EXECUTION_CONTROLLER",
        results: [
            {
                requestId: "c37125b8-c574-46ad-a13a-45bfd9ed3723",
                results: [
                    {
                        scenesCode: "vm_health_detect",
                        success: false,
                        valueMap: null,
                        reason: "Failed to execution workflow graph of reason: com.wl4g.rengine.common.exception.EvaluationException: Failed to execute script"
                    }
                ]
            }
        ]
    }
} ]);
db.getCollection("t_controller_logs").insert([ {
    _id: NumberLong("6363610819018752"),
    orgCode: null,
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-02-24 19:42:35",
    updateBy: NumberInt("-1"),
    updateDate: "2023-02-24 19:42:35",
    delFlag: NumberInt("0"),
    scheduleId: NumberLong("62208697108652456"),
    jobName: null,
    startupTime: NumberLong("1677238955316"),
    finishedTime: NumberLong("1677238955429"),
    success: false,
    detail: {
        type: "GENERIC_EXECUTION_CONTROLLER",
        results: [
            {
                requestId: "e3e3e895-a128-419a-8703-40ca46b9d39e",
                results: [
                    {
                        scenesCode: "vm_health_detect",
                        success: false,
                        valueMap: null,
                        reason: "Failed to execution workflow graph of reason: com.wl4g.rengine.common.exception.EvaluationException: Failed to execute script"
                    }
                ]
            }
        ]
    }
} ]);
db.getCollection("t_controller_logs").insert([ {
    _id: NumberLong("6363610896891904"),
    orgCode: null,
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-02-24 19:42:40",
    updateBy: NumberInt("-1"),
    updateDate: "2023-02-24 19:42:40",
    delFlag: NumberInt("0"),
    scheduleId: NumberLong("62208697108652456"),
    jobName: null,
    startupTime: NumberLong("1677238960070"),
    finishedTime: NumberLong("1677238960166"),
    success: false,
    detail: {
        type: "GENERIC_EXECUTION_CONTROLLER",
        results: [
            {
                requestId: "5ac9d43f-fe63-45cb-96b0-672c73d7fb9d",
                results: [
                    {
                        scenesCode: "vm_health_detect",
                        success: false,
                        valueMap: null,
                        reason: "Failed to execution workflow graph of reason: com.wl4g.rengine.common.exception.EvaluationException: Failed to execute script"
                    }
                ]
            }
        ]
    }
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for t_controller_schedules
// ----------------------------
db.getCollection("t_controller_schedules").drop();
db.createCollection("t_controller_schedules");

// ----------------------------
// Documents of t_controller_schedules
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("t_controller_schedules").insert([ {
    _id: NumberLong("62208697108652455"),
    orgCode: null,
    enable: NumberInt("0"),
    labels: [
        "example",
        "test",
        "print"
    ],
    remark: "Example for built SDK testing",
    createBy: null,
    createDate: null,
    updateBy: NumberInt("-1"),
    updateDate: "2023-02-24 17:27:40",
    delFlag: NumberInt("0"),
    name: "test_script_sdk_example",
    monitorExecution: true,
    failover: true,
    misfire: true,
    timeZone: "GMT+08:00",
    maxTimeDiffSeconds: NumberInt("-1"),
    reconcileIntervalMinutes: NumberInt("0"),
    maxTimeoutMs: NumberInt("30000"),
    runState: "FAILED",
    properties: {
        type: "GENERIC_EXECUTION_CONTROLLER",
        cron: "0/5 * * * * ?",
        requests: [
            {
                requestId: null,
                clientId: "JVqEpEwIaqkEkeD5",
                clientSecret: "Uf6nJDyJQHKRP43ycl9vZ9zs7s1nyu77",
                trace: true,
                timeout: NumberInt("10000"),
                bestEffort: false,
                args: {
                    foo1: "bar1"
                },
                scenesCodes: [
                    "test_script_sdk_example"
                ]
            }
        ]
    }
} ]);
db.getCollection("t_controller_schedules").insert([ {
    _id: NumberLong("62208697108652456"),
    orgCode: null,
    enable: NumberInt("1"),
    labels: [
        "example",
        "vm",
        "health",
        "detecter"
    ],
    remark: "Example monitoring for VM",
    createBy: null,
    createDate: null,
    updateBy: NumberInt("-1"),
    updateDate: "2023-02-24 19:42:40",
    delFlag: NumberInt("0"),
    name: "vm_health_detecter",
    monitorExecution: true,
    failover: true,
    misfire: true,
    timeZone: "GMT+08:00",
    maxTimeDiffSeconds: NumberInt("-1"),
    reconcileIntervalMinutes: NumberInt("0"),
    maxTimeoutMs: NumberInt("30000"),
    runState: "FAILED",
    properties: {
        type: "GENERIC_EXECUTION_CONTROLLER",
        cron: "0/5 * * * * ?",
        requests: [
            {
                requestId: null,
                clientId: "JVqEpEwIaqkEkeD5",
                clientSecret: "Uf6nJDyJQHKRP43ycl9vZ9zs7s1nyu77",
                trace: true,
                timeout: NumberInt("10000"),
                bestEffort: false,
                args: {
                    foo1: "bar1"
                },
                scenesCodes: [
                    "vm_health_detect"
                ]
            }
        ]
    }
} ]);
db.getCollection("t_controller_schedules").insert([ {
    _id: NumberLong("62208697108652457"),
    name: "db_records_yesterday_validator",
    enable: NumberInt("0"),
    labels: [
        "example",
        "db",
        "yesterday",
        "records",
        "validator"
    ],
    monitorExecution: true,
    failover: true,
    misfire: true,
    timeZone: "GMT+08:00",
    maxTimeoutMs: 30000,
    runState: null,
    properties: {
        type: "GENERIC_EXECUTION_CONTROLLER",
        cron: "0/5 * * * * ?",
        requests: [
            {
                clientId: "JVqEpEwIaqkEkeD5",
                clientSecret: "Uf6nJDyJQHKRP43ycl9vZ9zs7s1nyu77",
                scenesCodes: [
                    "vm_process_watch_restart"
                ],
                bestEffort: false,
                timeout: 10000,
                args: {
                    foo1: "bar1"
                }
            }
        ],
        _class: "com.wl4g.rengine.common.entity.ControllerSchedule$ExecutionControllerConfig"
    },
    remark: "Example monitoring for DB(mysql,pg,oracle)",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ControllerSchedule"
} ]);
db.getCollection("t_controller_schedules").insert([ {
    _id: NumberLong("62208697108652459"),
    name: "emr_spark_history_stream_job_monitor",
    enable: NumberInt("0"),
    labels: [
        "example",
        "emr",
        "spark",
        "history_stream_job",
        "monitor"
    ],
    monitorExecution: true,
    failover: true,
    misfire: true,
    timeZone: "GMT+08:00",
    maxTimeoutMs: 30000,
    runState: null,
    properties: {
        type: "GENERIC_EXECUTION_CONTROLLER",
        cron: "0/5 * * * * ?",
        requests: [
            {
                clientId: "JVqEpEwIaqkEkeD5",
                clientSecret: "Uf6nJDyJQHKRP43ycl9vZ9zs7s1nyu77",
                scenesCodes: [
                    "test_script_sdk_example"
                ],
                bestEffort: false,
                timeout: 10000,
                args: {
                    foo1: "bar1"
                }
            }
        ],
        _class: "com.wl4g.rengine.common.entity.ControllerSchedule$ExecutionControllerConfig"
    },
    remark: "Generic monitoring for EMR spark",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ControllerSchedule"
} ]);
db.getCollection("t_controller_schedules").insert([ {
    _id: NumberLong("62208697108652460"),
    orgCode: null,
    enable: NumberInt("0"),
    labels: [
        "example",
        "kafka",
        "subscribe",
        "notification"
    ],
    remark: "Subscribe to notification for kafka",
    createBy: null,
    createDate: null,
    updateBy: NumberInt("-1"),
    updateDate: "2023-02-24 17:19:56",
    delFlag: NumberInt("0"),
    name: "kafka_subscribe_notification_warning",
    monitorExecution: true,
    failover: true,
    misfire: true,
    timeZone: "GMT+08:00",
    maxTimeDiffSeconds: NumberInt("-1"),
    reconcileIntervalMinutes: NumberInt("0"),
    maxTimeoutMs: NumberInt("30000"),
    runState: "RUNNING",
    properties: {
        type: "KAFKA_EXECUTION_CONTROLLER",
        topics: [
            "test_topic"
        ],
        concurrency: NumberInt("1"),
        autoAcknowledgment: true,
        request: {
            requestId: null,
            clientId: "JVqEpEwIaqkEkeD5",
            clientSecret: "Uf6nJDyJQHKRP43ycl9vZ9zs7s1nyu77",
            trace: true,
            timeout: NumberInt("10000"),
            bestEffort: false,
            args: {
                foo1: "bar1"
            },
            scenesCodes: [
                "test_script_sdk_example"
            ]
        },
        consumerOptions: {
            "key_deserializer": "org.apache.kafka.common.serialization.StringDeserializer",
            "value_deserializer": "org.apache.kafka.common.serialization.StringDeserializer",
            "bootstrap_servers": "localhost:9092",
            "client_dns_lookup": "use_all_dns_ips",
            "metadata_max_age_ms": NumberInt("300000"),
            "send_buffer_bytes": NumberInt("131072"),
            "receive_buffer_bytes": NumberInt("65536"),
            "client_id": null,
            "client_rack": "",
            "reconnect_backoff_ms": NumberInt("50"),
            "reconnect_backoff_max_ms": NumberInt("1000"),
            retries: NumberInt("2147483647"),
            "retry_backoff_ms": NumberInt("100"),
            "metrics_sample_window_ms": NumberInt("3000"),
            "metrics_num_samples": NumberInt("2"),
            "metrics_recording_level": "INFO",
            "metric_reporters": [ ],
            "security_protocol": "PLAINTEXT",
            "socket_connection_setup_timeout_ms": NumberInt("10000"),
            "socket_connection_setup_timeout_max_ms": NumberInt("30000"),
            "connections_max_idle_ms": NumberInt("540000"),
            "request_timeout_ms": NumberInt("30000"),
            "group_id": "test_group",
            "group_instance_id": null,
            "max_poll_interval_ms": NumberInt("300000"),
            "rebalance_timeout_ms": null,
            "session_timeout_ms": NumberInt("45000"),
            "heartbeat_interval_ms": NumberInt("3000"),
            "default_api_timeout_ms": NumberInt("60000"),
            "enable_auto_commit": false,
            "auto_commit_interval_ms": NumberInt("5000"),
            "auto_offset_reset": "latest",
            "fetch_min_bytes": NumberInt("1"),
            "fetch_max_bytes": NumberInt("52428800"),
            "fetch_max_wait_ms": NumberInt("500")
        }
    }
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
    remark: "test",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.DataSourceProperties"
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
        password: "zzx!@#$%"
    },
    remark: "test",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.DataSourceProperties"
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
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.DataSourceProperties"
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
        nodes: [
            "localhost:6379",
            "localhost:6380",
            "localhost:6381",
            "localhost:7379",
            "localhost:7380",
            "localhost:7381"
        ],
        password: "zzx!@#$%",
        connTimeout: 10000,
        soTimeout: 10000,
        poolConfig: {
            maxTotal: 10,
            maxWait: 10000
        }
    },
    remark: "test",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.DataSourceProperties"
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
        nodes: [
            "owner-node5:6379",
            "owner-node5:6380",
            "owner-node5:6381",
            "owner-node5:7379",
            "owner-node5:7380",
            "owner-node5:7381"
        ],
        password: "zzx!@#$%",
        connTimeout: 10000,
        soTimeout: 10000,
        poolConfig: {
            maxTotal: 10,
            maxWait: 10000
        }
    },
    remark: "test",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.DataSourceProperties"
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
    remark: "test",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.DataSourceProperties"
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
    _id: NumberLong("6150869239922100"),
    revision: NumberInt("1"),
    ruleId: NumberLong("6150869239922100"),
    entrypointUploadId: NumberLong("6150869710864310"),
    uploadIds: [
        NumberLong("6150869710864310"),
        NumberLong("6150869710864311")
    ],
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "generic",
        "lang",
        "print"
    ],
    remark: "Generic for lang printer",
    createDate: ISODate("2022-09-27T04:50:22.304Z"),
    updateDate: ISODate("2022-09-27T04:50:22.304Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Rule$RuleScript"
} ]);
db.getCollection("t_rule_scripts").insert([ {
    _id: NumberLong("6150869239922668"),
    revision: NumberInt("1"),
    ruleId: NumberLong("6150869239922658"),
    entrypointUploadId: NumberLong("6150869710864381"),
    uploadIds: [
        NumberLong("6150869710864381"),
        NumberLong("6150869710864310"),
        NumberLong("6150869710864311")
    ],
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "test",
        "script",
        "sdk",
        "example"
    ],
    remark: "Example for built script SDKs testing",
    createDate: ISODate("2022-09-27T04:50:22.304Z"),
    updateDate: ISODate("2022-09-27T04:50:22.304Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Rule$RuleScript"
} ]);
db.getCollection("t_rule_scripts").insert([ {
    _id: NumberLong("6150869239922669"),
    revision: NumberInt("1"),
    ruleId: NumberLong("6150869239922659"),
    entrypointUploadId: NumberLong("6150869710864381"),
    uploadIds: [
        NumberLong("6150869710864381"),
        NumberLong("6150869710864310"),
        NumberLong("6150869710864311")
    ],
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "example",
        "ecommerce",
        "trade",
        "none",
        "mock"
    ],
    remark: "Example for Ecommerce Trade None process(mock)",
    createDate: ISODate("2022-09-27T04:50:22.304Z"),
    updateDate: ISODate("2022-09-27T04:50:22.304Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Rule$RuleScript"
} ]);
db.getCollection("t_rule_scripts").insert([ {
    _id: NumberLong("6150869239922670"),
    revision: NumberInt("1"),
    ruleId: NumberLong("6150869239922660"),
    entrypointUploadId: NumberLong("6150869710864382"),
    uploadIds: [
        NumberLong("6150869710864382"),
        NumberLong("6150869710864310"),
        NumberLong("6150869710864311")
    ],
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "exmaple"
    ],
    remark: "Example for VM health detector",
    createDate: ISODate("2022-09-27T04:50:22.304Z"),
    updateDate: ISODate("2022-09-27T04:50:22.304Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Rule$RuleScript"
} ]);
db.getCollection("t_rule_scripts").insert([ {
    _id: NumberLong("6150869239922671"),
    revision: NumberInt("1"),
    ruleId: NumberLong("6150869239922661"),
    entrypointUploadId: NumberLong("6150869710864383"),
    uploadIds: [
        NumberLong("6150869710864383"),
        NumberLong("6150869710864310"),
        NumberLong("6150869710864311")
    ],
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "example",
        "vm",
        "process",
        "restart",
        "watch"
    ],
    remark: "Example for VM process restart watcher",
    createDate: ISODate("2022-09-27T04:50:22.304Z"),
    updateDate: ISODate("2022-09-27T04:50:22.304Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Rule$RuleScript"
} ]);
db.getCollection("t_rule_scripts").insert([ {
    _id: NumberLong("6150869239922672"),
    revision: NumberInt("1"),
    ruleId: NumberLong("6150869239922662"),
    entrypointUploadId: NumberLong("6150869710864384"),
    uploadIds: [
        NumberLong("6150869710864384"),
        NumberLong("6150869710864310"),
        NumberLong("6150869710864311")
    ],
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "example",
        "iot",
        "temperature",
        "warnning"
    ],
    remark: "Example for Iot temperature warnning",
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
    _id: NumberLong("6150869239922100"),
    name: "None Printer",
    engine: "JS",
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "default",
        "none",
        "print"
    ],
    remark: "Default none print log. (e.g: for debugging)",
    createDate: ISODate("2022-09-27T04:50:39.789Z"),
    updateDate: ISODate("2022-09-27T04:50:39.789Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Rule"
} ]);
db.getCollection("t_rules").insert([ {
    _id: NumberLong("6150869239922658"),
    name: "Test built script SDK example",
    engine: "JS",
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "test",
        "script",
        "sdk",
        "example"
    ],
    remark: "Example for ecommerce trade gift None process(mock)",
    createDate: ISODate("2022-09-27T04:50:39.789Z"),
    updateDate: ISODate("2022-09-27T04:50:39.789Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Rule"
} ]);
db.getCollection("t_rules").insert([ {
    _id: NumberLong("6150869239922659"),
    name: "Ecommerce Trade None process(mock)",
    engine: "JS",
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "example",
        "ecommerce",
        "trade",
        "mock"
    ],
    remark: "Example for ecommerce trade gift None process(mock)",
    createDate: ISODate("2022-09-27T04:50:39.789Z"),
    updateDate: ISODate("2022-09-27T04:50:39.789Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Rule"
} ]);
db.getCollection("t_rules").insert([ {
    _id: NumberLong("6150869239922660"),
    name: "VM Health Detector 1",
    engine: "JS",
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "example",
        "vm",
        "helath",
        "detect"
    ],
    remark: "Example for VM Health Detector 1",
    createDate: ISODate("2022-09-27T04:50:39.789Z"),
    updateDate: ISODate("2022-09-27T04:50:39.789Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Rule"
} ]);
db.getCollection("t_rules").insert([ {
    _id: NumberLong("6150869239922661"),
    name: "VM Process Watch Restart 1",
    engine: "JS",
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "example",
        "vm",
        "process",
        "watch",
        "restart"
    ],
    remark: "Example for VM Process Watch Restart 1",
    createDate: ISODate("2022-09-27T04:50:39.789Z"),
    updateDate: ISODate("2022-09-27T04:50:39.789Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Rule"
} ]);
db.getCollection("t_rules").insert([ {
    _id: NumberLong("6150869239922662"),
    name: "Iot Temp Warnning 1",
    engine: "JS",
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "example",
        "iot",
        "temperature",
        "warnning"
    ],
    remark: "Example for Iot Temp Warnning 1",
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
    name: "测试内置Script SDK示例",
    scenesCode: "test_script_sdk_example",
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "test",
        "script",
        "sdk",
        "example"
    ],
    remark: "Testing for built script SDKs example.",
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Scenes"
} ]);
db.getCollection("t_sceneses").insert([ {
    _id: NumberLong("6150865561468929"),
    name: "电商充值活动赠送策略",
    scenesCode: "ecommerce_trade_gift",
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "example",
        "ecommerce",
        "trade",
        "gift"
    ],
    remark: "Example for ecommerce trade gift",
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Scenes"
} ]);
db.getCollection("t_sceneses").insert([ {
    _id: NumberLong("6150865561468930"),
    name: "VM节点健康状态探测",
    scenesCode: "vm_health_detect",
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "example",
        "vm",
        "health",
        "detect"
    ],
    remark: "Example for VM health detect.",
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Scenes"
} ]);
db.getCollection("t_sceneses").insert([ {
    _id: NumberLong("6150865561468931"),
    name: "VM进程监视并重启",
    scenesCode: "vm_process_watch_restart",
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "example",
        "vm",
        "process",
        "watch",
        "restart"
    ],
    remark: "Example for VM process watch and restart.",
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Scenes"
} ]);
db.getCollection("t_sceneses").insert([ {
    _id: NumberLong("6150865561468932"),
    name: "Iot设备温度告警",
    scenesCode: "iot_temp_warning",
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "example",
        "iot",
        "temperature",
        "warnning"
    ],
    remark: "Example for Iot device temperature warnning",
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Scenes"
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
    _id: NumberLong("6150869710864310"),
    uploadType: "USER_LIBRARY_WITH_JS",
    objectPrefix: "library/js/print-1.0.0.js",
    filename: "print-1.0.0.js",
    extension: ".js",
    size: NumberLong("1"),
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "generic",
        "print",
        "library"
    ],
    remark: "Generic print library.",
    updateDate: ISODate("2022-09-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.UploadObject"
} ]);
db.getCollection("t_uploads").insert([ {
    _id: NumberLong("6150869710864311"),
    uploadType: "USER_LIBRARY_WITH_JS",
    objectPrefix: "library/js/commons-lang-1.0.0.js",
    filename: "commons-lang-1.0.0.js",
    extension: ".js",
    size: NumberLong("1"),
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "generic",
        "lang",
        "library"
    ],
    remark: "Generic lang library.",
    updateDate: ISODate("2022-09-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.UploadObject"
} ]);
db.getCollection("t_uploads").insert([ {
    _id: NumberLong("6150869710864381"),
    uploadType: "USER_LIBRARY_WITH_JS",
    objectPrefix: "library/js/test-sdk-all-examples-1.0.0.js",
    filename: "test-sdk-all-examples-1.0.0.js",
    extension: ".js",
    size: NumberLong("1"),
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "testing",
        "example",
        "sdk"
    ],
    remark: "Testing for all SDK examples.",
    updateDate: ISODate("2022-09-27T04:51:08.533Z"),
    _class: "com.wl4g.rengine.common.entity.UploadObject",
    delFlag: NumberInt("0")
} ]);
db.getCollection("t_uploads").insert([ {
    _id: NumberLong("6150869710864382"),
    uploadType: "USER_LIBRARY_WITH_JS",
    objectPrefix: "library/js/vm-health-detecter-1.0.0.js",
    filename: "vm-health-detecter-1.0.0.js",
    extension: ".js",
    size: NumberLong("1"),
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "example",
        "vm",
        "health",
        "detecter"
    ],
    remark: "Example VM health detecter js script.",
    updateDate: ISODate("2022-09-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.UploadObject"
} ]);
db.getCollection("t_uploads").insert([ {
    _id: NumberLong("6150869710864383"),
    uploadType: "USER_LIBRARY_WITH_JS",
    objectPrefix: "library/js/vm-process-restart-watcher-1.0.0.js",
    filename: "vm-process-restart-watcher-1.0.0.js",
    extension: ".js",
    size: NumberLong("1"),
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "example",
        "vm",
        "process",
        "restart",
        "watcher"
    ],
    remark: "Example VM process restart watcher js script.",
    updateDate: ISODate("2022-09-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.UploadObject"
} ]);
db.getCollection("t_uploads").insert([ {
    _id: NumberLong("6150869710864384"),
    uploadType: "USER_LIBRARY_WITH_JS",
    objectPrefix: "library/js/db-records-yesterday-validator-1.0.0.js",
    filename: "db-records-yesterday-validator-1.0.0.js",
    extension: ".js",
    size: NumberLong("1"),
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "example",
        "db",
        "records",
        "yesterday",
        "validator"
    ],
    remark: "Example DB records yesterday validator js script.",
    updateDate: ISODate("2022-09-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.UploadObject"
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
    _id: NumberLong("6150868953448848"),
    revision: NumberLong("1"),
    workflowId: NumberLong("6150868953448438"),
    nodes: [
        {
            "@type": "BOOT",
            id: "0",
            name: "The Boot",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            }
        },
        {
            "@type": "PROCESS",
            id: "1",
            name: "执行测试内置Script SDK示例",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            },
            ruleId: "6150869239922658"
        }
    ],
    connections: [
        {
            name: "Unnamed Connection",
            to: "1",
            from: "0",
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
        "example",
        "vm",
        "health"
    ],
    remark: "string",
    createDate: ISODate("2022-09-27T04:50:22.304Z"),
    updateDate: ISODate("2022-09-27T04:50:22.304Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.WorkflowGraph"
} ]);
db.getCollection("t_workflow_graphs").insert([ {
    _id: NumberLong("6150868953448849"),
    revision: NumberLong("1"),
    workflowId: NumberLong("6150868953448439"),
    nodes: [
        {
            "@type": "BOOT",
            id: "0",
            name: "The Boot",
            attributes: {
                top: "35px",
                color: "#5f5f5f",
                left: "310px"
            },
            color: "#5f5f5f"
        },
        {
            "@type": "PROCESS",
            id: "11",
            name: "预处理(如篡改当前时间以用于测试目的)",
            attributes: {
                top: "130px",
                color: "#5f5f5f",
                left: "300px"
            },
            ruleId: 6.1508692399221e+15,
            color: "#5f5f5f"
        },
        {
            "@type": "RELATION",
            id: "21",
            name: "当前时间是否满足(10.1~10.8)",
            attributes: {
                top: "220px",
                color: "#5f5f5f",
                left: "305px"
            },
            ruleId: 6.1508692399221e+15,
            color: "#5f5f5f"
        },
        {
            "@type": "LOGICAL",
            id: "31",
            name: "ALL_AND逻辑运算",
            attributes: {
                top: "330px",
                color: "#5f5f5f",
                left: "290px"
            },
            logical: "ALL_AND",
            color: "#5f5f5f"
        },
        {
            "@type": "LOGICAL",
            id: "41",
            name: "AND逻辑运算",
            attributes: {
                top: "355px",
                color: "#5f5f5f",
                left: "505px"
            },
            logical: "AND",
            color: "#5f5f5f"
        },
        {
            "@type": "LOGICAL",
            id: "42",
            name: "AND逻辑运算",
            attributes: {
                top: "375px",
                color: "#5f5f5f",
                left: "120px"
            },
            logical: "AND",
            color: "#5f5f5f"
        },
        {
            "@type": "RELATION",
            id: "51",
            name: "充值是否>=120元",
            attributes: {
                top: "480px",
                color: "#5f5f5f",
                left: "595px"
            },
            ruleId: 6.1508692399221e+15,
            color: "#5f5f5f"
        },
        {
            "@type": "LOGICAL",
            id: "52",
            name: "AND逻辑运算",
            attributes: {
                top: "460px",
                color: "#5f5f5f",
                left: "420px"
            },
            logical: "AND",
            color: "#5f5f5f"
        },
        {
            "@type": "RELATION",
            id: "53",
            name: "当前时间是否满足(10.5~10.8)",
            attributes: {
                top: "510px",
                color: "#5f5f5f",
                left: "35px"
            },
            ruleId: 6.1508692399221e+15,
            color: "#5f5f5f"
        },
        {
            "@type": "RELATION",
            id: "54",
            name: "充值是否>=50元",
            attributes: {
                top: "535px",
                color: "#5f5f5f",
                left: "170px"
            },
            ruleId: 6.1508692399221e+15,
            color: "#5f5f5f"
        },
        {
            "@type": "FAILBACK",
            id: "62",
            name: "如果赠送余额失败则执行回退规则",
            attributes: {
                top: "560px",
                color: "#5f5f5f",
                left: "450px"
            },
            ruleId: 6.1508692399221e+15,
            color: "#5f5f5f"
        },
        {
            "@type": "RUN",
            id: "63",
            name: "赠送20积分",
            attributes: {
                top: "640px",
                color: "#5f5f5f",
                left: "130px"
            },
            ruleId: 6.1508692399221e+15,
            color: "#5f5f5f"
        },
        {
            "@type": "RUN",
            id: "71",
            name: "赠送10元余额",
            attributes: {
                top: "615px",
                color: "#5f5f5f",
                left: "325px"
            },
            ruleId: 6.1508692399221e+15,
            color: "#5f5f5f"
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
        foo: "bar"
    },
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "example",
        "ecommerce",
        "trade"
    ],
    remark: "string",
    createDate: ISODate("2022-09-27T04:50:22.304Z"),
    updateDate: ISODate("2022-09-27T04:50:22.304Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.WorkflowGraph"
} ]);
db.getCollection("t_workflow_graphs").insert([ {
    _id: NumberLong("6150868953448850"),
    revision: NumberLong("1"),
    workflowId: NumberLong("6150868953448440"),
    nodes: [
        {
            "@type": "BOOT",
            id: "0",
            name: "The Boot",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            }
        },
        {
            "@type": "PROCESS",
            id: "1",
            name: "执行探测VM健康状态",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            },
            ruleId: "6150869239922660"
        }
    ],
    connections: [
        {
            name: "Unnamed Connection",
            to: "1",
            from: "0",
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
        "example",
        "vm",
        "health"
    ],
    remark: "string",
    createDate: ISODate("2022-09-27T04:50:22.304Z"),
    updateDate: ISODate("2022-09-27T04:50:22.304Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.WorkflowGraph"
} ]);
db.getCollection("t_workflow_graphs").insert([ {
    _id: NumberLong("6150868953448851"),
    revision: NumberLong("1"),
    workflowId: NumberLong("6150868953448441"),
    nodes: [
        {
            "@type": "BOOT",
            id: "0",
            name: "The Boot",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            }
        },
        {
            "@type": "PROCESS",
            id: "1",
            name: "执行VM进程监视(并重启)",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            },
            ruleId: "6150869239922661"
        }
    ],
    connections: [
        {
            name: "Unnamed Connection",
            to: "1",
            from: "0",
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
        "example",
        "vm",
        "process",
        "watch",
        "restart"
    ],
    remark: "string",
    createDate: ISODate("2022-09-27T04:50:22.304Z"),
    updateDate: ISODate("2022-09-27T04:50:22.304Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.WorkflowGraph"
} ]);
db.getCollection("t_workflow_graphs").insert([ {
    _id: NumberLong("6150868953448852"),
    revision: NumberLong("1"),
    workflowId: NumberLong("6150868953448442"),
    nodes: [
        {
            "@type": "BOOT",
            id: "0",
            name: "The Boot",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            }
        },
        {
            "@type": "PROCESS",
            id: "1",
            name: "执行检查Iot设备温度是否告警",
            attributes: {
                top: "10px",
                color: "#5f5f5f",
                left: "10px"
            },
            ruleId: "6150869239922662"
        }
    ],
    connections: [
        {
            name: "Unnamed Connection",
            to: "1",
            from: "0",
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
        "example",
        "iot",
        "temperature",
        "warnning"
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
    _id: NumberLong("6150868953448438"),
    name: "测试内置Script SDK示例-流程",
    scenesId: NumberLong("6150865561468928"),
    engine: "JS",
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "test",
        "script",
        "sdk",
        "example"
    ],
    remark: "Example for ecommerce trade gift",
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Workflow"
} ]);
db.getCollection("t_workflows").insert([ {
    _id: NumberLong("6150868953448439"),
    name: "电商充值活动赠送策略-流程",
    scenesId: NumberLong("6150865561468929"),
    engine: "JS",
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "example",
        "ecommerce",
        "trade"
    ],
    remark: "Example for ecommerce trade gift",
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Workflow"
} ]);
db.getCollection("t_workflows").insert([ {
    _id: NumberLong("6150868953448440"),
    name: "VM节点健康状态探测-流程",
    scenesId: NumberLong("6150865561468930"),
    engine: "JS",
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "example",
        "vm",
        "health",
        "detect"
    ],
    remark: "Example for VM health detect.",
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Workflow"
} ]);
db.getCollection("t_workflows").insert([ {
    _id: NumberLong("6150868953448441"),
    name: "VM进程监视并重启-流程",
    scenesId: NumberLong("6150865561468931"),
    engine: "JS",
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "example",
        "vm",
        "process",
        "watch",
        "restart"
    ],
    remark: "Example for VM process watch and restart.",
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Workflow"
} ]);
db.getCollection("t_workflows").insert([ {
    _id: NumberLong("6150868953448442"),
    name: "Iot设备温度告警-流程",
    scenesId: NumberLong("6150865561468932"),
    engine: "JS",
    orgCode: "top",
    enable: NumberInt("1"),
    labels: [
        "example",
        "iot",
        "temperature",
        "warnning"
    ],
    remark: "Example for Iot device temperature warnning.",
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Workflow"
} ]);
session.commitTransaction(); session.endSession();
