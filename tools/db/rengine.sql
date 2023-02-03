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

 Date: 03/02/2023 23:25:52
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
    key: "sdk_datasource_kafka",
    value: "{\"MONGO\":[{\"type\":\"string\",\"name\":\"connectionString\",\"defaultValue\":\"localhost:9092\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null}],\"JDBC\":[{\"type\":\"int\",\"name\":\"fetchDirection\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"fetchSize\",\"defaultValue\":10000,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maxFieldSize\",\"defaultValue\":64,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maxRows\",\"defaultValue\":1024,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"queryTimeoutMs\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"driverClassName\",\"defaultValue\":\"com.mysql.cj.jdbc.Driver\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"jdbcUrl\",\"defaultValue\":\"jdbc:mysql://localhost:3306/mysql?useUnicode=true&serverTimezone=UTC&characterEncoding=utf-8&useSSL=false\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"username\",\"defaultValue\":\"root\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"password\",\"defaultValue\":\"123456\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"connectionTimeout\",\"defaultValue\":\"123456\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"validationTimeout\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"validationTestSql\",\"defaultValue\":\"SELECT 1\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"long\",\"name\":\"idleTimeout\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"long\",\"name\":\"softMinIdleTimeout\",\"defaultValue\":-1,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"long\",\"name\":\"maxConnLifeTime\",\"defaultValue\":-1,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"long\",\"name\":\"evictionRunsBetweenTime\",\"defaultValue\":-1,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"initPoolSize\",\"defaultValue\":5,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maximumPoolSize\",\"defaultValue\":20,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"minimumIdle\",\"defaultValue\":1,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"autoCommit\",\"defaultValue\":true,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"cacheState\",\"defaultValue\":true,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testOnBorrow\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testOnCreate\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testOnReturn\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testWhileIdle\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null}],\"REDIS\":[{\"type\":\"object\",\"name\":\"jedisConfig\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null,\"childrens\":[{\"type\":\"string\",\"name\":\"nodes\",\"defaultValue\":\"localhost:6379,localhost:6380,localhost:6381,localhost:7379,localhost:7380,localhost:7381\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"username\",\"defaultValue\":null,\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"password\",\"defaultValue\":\"123456\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"clientName\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"connTimeout\",\"defaultValue\":10000,\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"soTimeout\",\"defaultValue\":10000,\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maxAttempts\",\"defaultValue\":3,\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"database\",\"defaultValue\":0,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"safeMode\",\"defaultValue\":\"\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"object\",\"name\":\"poolConfig\",\"defaultValue\":\"\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null,\"childrens\":[{\"type\":\"boolean\",\"name\":\"lifo\",\"defaultValue\":true,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"fairness\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"numTestsPerEvictionRun\",\"defaultValue\":-1,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testOnCreate\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testOnBorrow\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testOnReturn\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testWhileIdle\",\"defaultValue\":true,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maxTotal\",\"defaultValue\":60000,\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maxIdle\",\"defaultValue\":100,\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"minIdle\",\"defaultValue\":10,\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"duration\",\"name\":\"maxWaitDuration\",\"defaultValue\":\"10s\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"duration\",\"name\":\"minEvictableIdleDuration\",\"defaultValue\":\"\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"duration\",\"name\":\"evictorShutdownTimeoutDuration\",\"defaultValue\":10000,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"duration\",\"name\":\"softMinEvictableIdleDuration\",\"defaultValue\":-1,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"duration\",\"name\":\"durationBetweenEvictionRuns\",\"defaultValue\":-1,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null}]}]}],\"KAFKA\":[{\"type\":\"string\",\"name\":\"bootstrapServers\",\"defaultValue\":\"localhost: 9092\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"keySerializer\",\"defaultValue\":\"org.apache.kafka.common.serialization.StringSerializer\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"bufferMemory\",\"defaultValue\":\"32 * 1024 * 1024\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"retries\",\"defaultValue\":\"0\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"acks\",\"defaultValue\":\"all\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"compressionType \",\"defaultValue\":\"none\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"batchSize\",\"defaultValue\":16384,\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"lingerMs\",\"defaultValue\":16384,\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"deliveryTimeoutMs\",\"defaultValue\":120000,\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"sendBuffer\",\"defaultValue\":131072,\"required\":false,\"maxValue\":null,\"minValue\":-1,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"receiveBuffer\",\"defaultValue\":32768,\"required\":false,\"maxValue\":null,\"minValue\":-1,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maxRequestSize\",\"defaultValue\":1048576,\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"reconnectBackoffMs\",\"defaultValue\":50,\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"reconnectBackoffMs\",\"defaultValue\":50,\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"reconnectBackoffMaxMs\",\"defaultValue\":1000,\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"retryBackoffMs\",\"defaultValue\":100,\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maxBlockMs\",\"defaultValue\":60000,\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"requestTimeoutMs\",\"defaultValue\":30000,\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"metadataMaxAge\",\"defaultValue\":300000,\"required\":false,\"maxValue\":null,\"minValue\":5000,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"metricsSampleWindowMs\",\"defaultValue\":3000,\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"metricsRecordingLevel\",\"defaultValue\":\"INFO\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maxInFlightRequestsPerConnection\",\"defaultValue\":5,\"required\":false,\"maxValue\":null,\"minValue\":1,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maxInFlightRequestsPerConnection\",\"defaultValue\":5,\"required\":false,\"maxValue\":null,\"minValue\":1,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"connectionsMaxIdleMs\",\"defaultValue\":540000,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"transactionTimeout\",\"defaultValue\":60000,\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null}]}",
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
        corpId: "dingbhyrzjxx6qjhjcdr"
    },
    orgCode: "string",
    remark: "local test",
    createDate: ISODate("2023-01-07T11:22:48.314Z"),
    updateDate: ISODate("2023-01-07T11:22:48.314Z"),
    delFlag: NumberInt("0")
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
    delFlag: NumberInt("0")
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
db.getCollection("t_schedule_job_logs").insert([ {
    _id: NumberLong("6333792344211456"),
    triggerId: NumberLong("62208697108652458"),
    startupTime: ISODate("2023-02-03T10:09:40.042Z"),
    finishedTime: ISODate("2023-02-03T10:09:40.658Z"),
    success: true,
    detail: {
        results: [
            {
                requestId: "84b33dd9-5eb2-4da6-88da-64bba4ddcbe3",
                results: [
                    {
                        scenesCode: "ecommerce_trade_gift",
                        success: true,
                        valueMap: {
                            jdbcResult: [
                                {
                                    Host: "%",
                                    User: "root",
                                    "Select_priv": "Y",
                                    "Insert_priv": "Y",
                                    "Update_priv": "Y",
                                    "Delete_priv": "Y",
                                    "Create_priv": "Y",
                                    "Drop_priv": "Y",
                                    "Reload_priv": "Y",
                                    "Shutdown_priv": "Y",
                                    "Process_priv": "Y",
                                    "File_priv": "Y",
                                    "Grant_priv": "Y",
                                    "References_priv": "Y",
                                    "Index_priv": "Y",
                                    "Alter_priv": "Y",
                                    "Show_db_priv": "Y",
                                    "Super_priv": "Y",
                                    "Create_tmp_table_priv": "Y",
                                    "Lock_tables_priv": "Y",
                                    "Execute_priv": "Y",
                                    "Repl_slave_priv": "Y",
                                    "Repl_client_priv": "Y",
                                    "Create_view_priv": "Y",
                                    "Show_view_priv": "Y",
                                    "Create_routine_priv": "Y",
                                    "Alter_routine_priv": "Y",
                                    "Create_user_priv": "Y",
                                    "Event_priv": "Y",
                                    "Trigger_priv": "Y",
                                    "Create_tablespace_priv": "Y",
                                    "ssl_type": "",
                                    "ssl_cipher": "",
                                    "x509_issuer": "",
                                    "x509_subject": "",
                                    "max_questions": NumberInt("0"),
                                    "max_updates": NumberInt("0"),
                                    "max_connections": NumberInt("0"),
                                    "max_user_connections": NumberInt("0"),
                                    plugin: "mysql_native_password",
                                    "authentication_string": "*E5B181C8229925C680B0E7ECDDF1CF8F29D97257",
                                    "password_expired": "N",
                                    "password_last_changed": "2020-07-20T22:42:29.000+00:00",
                                    "password_lifetime": null,
                                    "account_locked": "N"
                                }
                            ],
                            dateHolderResult: "2023-02-03T10:09:40.628+00:00",
                            codingResult: "75M7MfQsC4p2rUNe7dDjXK",
                            hashingResult: "z/EfkNDTK/okjQ+jyOQ+vI22o9zFjK38cTtL9r9sEy1a5thhkF4b9sr6LZdR1yE4wIPwNIzApzX7mBcKg0nT+Q==",
                            aesResult: "abcdefghijklmnopqrstuvwxyz",
                            rsaResult: "abcdefghijklmnopqrstuvwxyz"
                        }
                    }
                ]
            }
        ],
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleJobLog$ClientScheduleJobLog"
    },
    enable: NumberInt("1"),
    createDate: ISODate("2023-02-03T10:09:40.041Z"),
    updateDate: ISODate("2023-02-03T10:09:40.659Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleJobLog"
} ]);
db.getCollection("t_schedule_job_logs").insert([ {
    _id: NumberLong("6333792344244224"),
    triggerId: NumberLong("62208697108652456"),
    startupTime: ISODate("2023-02-03T10:09:40.045Z"),
    finishedTime: ISODate("2023-02-03T10:09:40.459Z"),
    success: true,
    detail: {
        results: [
            {
                requestId: "65b0731b-8a17-4c11-9fc9-9052fec6868a",
                results: [
                    {
                        scenesCode: "ecommerce_trade_gift",
                        success: true,
                        valueMap: {
                            jdbcResult: [
                                {
                                    Host: "%",
                                    User: "root",
                                    "Select_priv": "Y",
                                    "Insert_priv": "Y",
                                    "Update_priv": "Y",
                                    "Delete_priv": "Y",
                                    "Create_priv": "Y",
                                    "Drop_priv": "Y",
                                    "Reload_priv": "Y",
                                    "Shutdown_priv": "Y",
                                    "Process_priv": "Y",
                                    "File_priv": "Y",
                                    "Grant_priv": "Y",
                                    "References_priv": "Y",
                                    "Index_priv": "Y",
                                    "Alter_priv": "Y",
                                    "Show_db_priv": "Y",
                                    "Super_priv": "Y",
                                    "Create_tmp_table_priv": "Y",
                                    "Lock_tables_priv": "Y",
                                    "Execute_priv": "Y",
                                    "Repl_slave_priv": "Y",
                                    "Repl_client_priv": "Y",
                                    "Create_view_priv": "Y",
                                    "Show_view_priv": "Y",
                                    "Create_routine_priv": "Y",
                                    "Alter_routine_priv": "Y",
                                    "Create_user_priv": "Y",
                                    "Event_priv": "Y",
                                    "Trigger_priv": "Y",
                                    "Create_tablespace_priv": "Y",
                                    "ssl_type": "",
                                    "ssl_cipher": "",
                                    "x509_issuer": "",
                                    "x509_subject": "",
                                    "max_questions": NumberInt("0"),
                                    "max_updates": NumberInt("0"),
                                    "max_connections": NumberInt("0"),
                                    "max_user_connections": NumberInt("0"),
                                    plugin: "mysql_native_password",
                                    "authentication_string": "*E5B181C8229925C680B0E7ECDDF1CF8F29D97257",
                                    "password_expired": "N",
                                    "password_last_changed": "2020-07-20T22:42:29.000+00:00",
                                    "password_lifetime": null,
                                    "account_locked": "N"
                                }
                            ],
                            dateHolderResult: "2023-02-03T10:09:40.418+00:00",
                            codingResult: "75M7MfQsC4p2rUNe7dDjXK",
                            hashingResult: "z/EfkNDTK/okjQ+jyOQ+vI22o9zFjK38cTtL9r9sEy1a5thhkF4b9sr6LZdR1yE4wIPwNIzApzX7mBcKg0nT+Q==",
                            aesResult: "abcdefghijklmnopqrstuvwxyz",
                            rsaResult: "abcdefghijklmnopqrstuvwxyz"
                        }
                    }
                ]
            }
        ],
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleJobLog$ClientScheduleJobLog"
    },
    enable: NumberInt("1"),
    createDate: ISODate("2023-02-03T10:09:40.044Z"),
    updateDate: ISODate("2023-02-03T10:09:40.46Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleJobLog"
} ]);
db.getCollection("t_schedule_job_logs").insert([ {
    _id: NumberLong("6333792344276992"),
    triggerId: NumberLong("62208697108652459"),
    startupTime: ISODate("2023-02-03T10:09:40.046Z"),
    finishedTime: ISODate("2023-02-03T10:09:40.459Z"),
    success: true,
    detail: {
        results: [
            {
                requestId: "d1ceb13f-f1ad-43ab-af89-c1c7fe26ff72",
                results: [
                    {
                        scenesCode: "ecommerce_trade_gift",
                        success: true,
                        valueMap: {
                            jdbcResult: [
                                {
                                    Host: "%",
                                    User: "root",
                                    "Select_priv": "Y",
                                    "Insert_priv": "Y",
                                    "Update_priv": "Y",
                                    "Delete_priv": "Y",
                                    "Create_priv": "Y",
                                    "Drop_priv": "Y",
                                    "Reload_priv": "Y",
                                    "Shutdown_priv": "Y",
                                    "Process_priv": "Y",
                                    "File_priv": "Y",
                                    "Grant_priv": "Y",
                                    "References_priv": "Y",
                                    "Index_priv": "Y",
                                    "Alter_priv": "Y",
                                    "Show_db_priv": "Y",
                                    "Super_priv": "Y",
                                    "Create_tmp_table_priv": "Y",
                                    "Lock_tables_priv": "Y",
                                    "Execute_priv": "Y",
                                    "Repl_slave_priv": "Y",
                                    "Repl_client_priv": "Y",
                                    "Create_view_priv": "Y",
                                    "Show_view_priv": "Y",
                                    "Create_routine_priv": "Y",
                                    "Alter_routine_priv": "Y",
                                    "Create_user_priv": "Y",
                                    "Event_priv": "Y",
                                    "Trigger_priv": "Y",
                                    "Create_tablespace_priv": "Y",
                                    "ssl_type": "",
                                    "ssl_cipher": "",
                                    "x509_issuer": "",
                                    "x509_subject": "",
                                    "max_questions": NumberInt("0"),
                                    "max_updates": NumberInt("0"),
                                    "max_connections": NumberInt("0"),
                                    "max_user_connections": NumberInt("0"),
                                    plugin: "mysql_native_password",
                                    "authentication_string": "*E5B181C8229925C680B0E7ECDDF1CF8F29D97257",
                                    "password_expired": "N",
                                    "password_last_changed": "2020-07-20T22:42:29.000+00:00",
                                    "password_lifetime": null,
                                    "account_locked": "N"
                                }
                            ],
                            dateHolderResult: "2023-02-03T10:09:40.213+00:00",
                            codingResult: "75M7MfQsC4p2rUNe7dDjXK",
                            hashingResult: "z/EfkNDTK/okjQ+jyOQ+vI22o9zFjK38cTtL9r9sEy1a5thhkF4b9sr6LZdR1yE4wIPwNIzApzX7mBcKg0nT+Q==",
                            aesResult: "abcdefghijklmnopqrstuvwxyz",
                            rsaResult: "abcdefghijklmnopqrstuvwxyz"
                        }
                    }
                ]
            }
        ],
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleJobLog$ClientScheduleJobLog"
    },
    enable: NumberInt("1"),
    createDate: ISODate("2023-02-03T10:09:40.045Z"),
    updateDate: ISODate("2023-02-03T10:09:40.46Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleJobLog"
} ]);
db.getCollection("t_schedule_job_logs").insert([ {
    _id: NumberLong("6333792344309760"),
    triggerId: NumberLong("62208697108652457"),
    startupTime: ISODate("2023-02-03T10:09:40.049Z"),
    finishedTime: ISODate("2023-02-03T10:09:40.457Z"),
    success: true,
    detail: {
        results: [
            {
                requestId: "4092a43c-bbb5-4fab-b222-03932375e855",
                results: [
                    {
                        scenesCode: "ecommerce_trade_gift",
                        success: true,
                        valueMap: {
                            jdbcResult: [
                                {
                                    Host: "%",
                                    User: "root",
                                    "Select_priv": "Y",
                                    "Insert_priv": "Y",
                                    "Update_priv": "Y",
                                    "Delete_priv": "Y",
                                    "Create_priv": "Y",
                                    "Drop_priv": "Y",
                                    "Reload_priv": "Y",
                                    "Shutdown_priv": "Y",
                                    "Process_priv": "Y",
                                    "File_priv": "Y",
                                    "Grant_priv": "Y",
                                    "References_priv": "Y",
                                    "Index_priv": "Y",
                                    "Alter_priv": "Y",
                                    "Show_db_priv": "Y",
                                    "Super_priv": "Y",
                                    "Create_tmp_table_priv": "Y",
                                    "Lock_tables_priv": "Y",
                                    "Execute_priv": "Y",
                                    "Repl_slave_priv": "Y",
                                    "Repl_client_priv": "Y",
                                    "Create_view_priv": "Y",
                                    "Show_view_priv": "Y",
                                    "Create_routine_priv": "Y",
                                    "Alter_routine_priv": "Y",
                                    "Create_user_priv": "Y",
                                    "Event_priv": "Y",
                                    "Trigger_priv": "Y",
                                    "Create_tablespace_priv": "Y",
                                    "ssl_type": "",
                                    "ssl_cipher": "",
                                    "x509_issuer": "",
                                    "x509_subject": "",
                                    "max_questions": NumberInt("0"),
                                    "max_updates": NumberInt("0"),
                                    "max_connections": NumberInt("0"),
                                    "max_user_connections": NumberInt("0"),
                                    plugin: "mysql_native_password",
                                    "authentication_string": "*E5B181C8229925C680B0E7ECDDF1CF8F29D97257",
                                    "password_expired": "N",
                                    "password_last_changed": "2020-07-20T22:42:29.000+00:00",
                                    "password_lifetime": null,
                                    "account_locked": "N"
                                }
                            ],
                            dateHolderResult: "2023-02-03T10:09:40.416+00:00",
                            codingResult: "75M7MfQsC4p2rUNe7dDjXK",
                            hashingResult: "z/EfkNDTK/okjQ+jyOQ+vI22o9zFjK38cTtL9r9sEy1a5thhkF4b9sr6LZdR1yE4wIPwNIzApzX7mBcKg0nT+Q==",
                            aesResult: "abcdefghijklmnopqrstuvwxyz",
                            rsaResult: "abcdefghijklmnopqrstuvwxyz"
                        }
                    }
                ]
            }
        ],
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleJobLog$ClientScheduleJobLog"
    },
    enable: NumberInt("1"),
    createDate: ISODate("2023-02-03T10:09:40.047Z"),
    updateDate: ISODate("2023-02-03T10:09:40.458Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleJobLog"
} ]);
db.getCollection("t_schedule_job_logs").insert([ {
    _id: NumberLong("6333792426082304"),
    triggerId: NumberLong("62208697108652456"),
    startupTime: ISODate("2023-02-03T10:09:45.04Z"),
    finishedTime: ISODate("2023-02-03T10:09:45.728Z"),
    success: true,
    detail: {
        results: [
            {
                requestId: "a07b9ab0-c016-4739-bdc1-611dc4152fdf",
                results: [
                    {
                        scenesCode: "ecommerce_trade_gift",
                        success: true,
                        valueMap: {
                            jdbcResult: [
                                {
                                    Host: "%",
                                    User: "root",
                                    "Select_priv": "Y",
                                    "Insert_priv": "Y",
                                    "Update_priv": "Y",
                                    "Delete_priv": "Y",
                                    "Create_priv": "Y",
                                    "Drop_priv": "Y",
                                    "Reload_priv": "Y",
                                    "Shutdown_priv": "Y",
                                    "Process_priv": "Y",
                                    "File_priv": "Y",
                                    "Grant_priv": "Y",
                                    "References_priv": "Y",
                                    "Index_priv": "Y",
                                    "Alter_priv": "Y",
                                    "Show_db_priv": "Y",
                                    "Super_priv": "Y",
                                    "Create_tmp_table_priv": "Y",
                                    "Lock_tables_priv": "Y",
                                    "Execute_priv": "Y",
                                    "Repl_slave_priv": "Y",
                                    "Repl_client_priv": "Y",
                                    "Create_view_priv": "Y",
                                    "Show_view_priv": "Y",
                                    "Create_routine_priv": "Y",
                                    "Alter_routine_priv": "Y",
                                    "Create_user_priv": "Y",
                                    "Event_priv": "Y",
                                    "Trigger_priv": "Y",
                                    "Create_tablespace_priv": "Y",
                                    "ssl_type": "",
                                    "ssl_cipher": "",
                                    "x509_issuer": "",
                                    "x509_subject": "",
                                    "max_questions": NumberInt("0"),
                                    "max_updates": NumberInt("0"),
                                    "max_connections": NumberInt("0"),
                                    "max_user_connections": NumberInt("0"),
                                    plugin: "mysql_native_password",
                                    "authentication_string": "*E5B181C8229925C680B0E7ECDDF1CF8F29D97257",
                                    "password_expired": "N",
                                    "password_last_changed": "2020-07-20T22:42:29.000+00:00",
                                    "password_lifetime": null,
                                    "account_locked": "N"
                                }
                            ],
                            dateHolderResult: "2023-02-03T10:09:45.547+00:00",
                            codingResult: "75M7MfQsC4p2rUNe7dDjXK",
                            hashingResult: "z/EfkNDTK/okjQ+jyOQ+vI22o9zFjK38cTtL9r9sEy1a5thhkF4b9sr6LZdR1yE4wIPwNIzApzX7mBcKg0nT+Q==",
                            aesResult: "abcdefghijklmnopqrstuvwxyz",
                            rsaResult: "abcdefghijklmnopqrstuvwxyz"
                        }
                    }
                ]
            }
        ],
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleJobLog$ClientScheduleJobLog"
    },
    enable: NumberInt("1"),
    createDate: ISODate("2023-02-03T10:09:45.038Z"),
    updateDate: ISODate("2023-02-03T10:09:45.73Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleJobLog"
} ]);
db.getCollection("t_schedule_job_logs").insert([ {
    _id: NumberLong("6333792426180608"),
    triggerId: NumberLong("62208697108652458"),
    startupTime: ISODate("2023-02-03T10:09:45.046Z"),
    finishedTime: ISODate("2023-02-03T10:09:45.416Z"),
    success: true,
    detail: {
        results: [
            {
                requestId: "4781fad4-2d6d-43be-b84c-d06bd1ee22f6",
                results: [
                    {
                        scenesCode: "ecommerce_trade_gift",
                        success: true,
                        valueMap: {
                            jdbcResult: [
                                {
                                    Host: "%",
                                    User: "root",
                                    "Select_priv": "Y",
                                    "Insert_priv": "Y",
                                    "Update_priv": "Y",
                                    "Delete_priv": "Y",
                                    "Create_priv": "Y",
                                    "Drop_priv": "Y",
                                    "Reload_priv": "Y",
                                    "Shutdown_priv": "Y",
                                    "Process_priv": "Y",
                                    "File_priv": "Y",
                                    "Grant_priv": "Y",
                                    "References_priv": "Y",
                                    "Index_priv": "Y",
                                    "Alter_priv": "Y",
                                    "Show_db_priv": "Y",
                                    "Super_priv": "Y",
                                    "Create_tmp_table_priv": "Y",
                                    "Lock_tables_priv": "Y",
                                    "Execute_priv": "Y",
                                    "Repl_slave_priv": "Y",
                                    "Repl_client_priv": "Y",
                                    "Create_view_priv": "Y",
                                    "Show_view_priv": "Y",
                                    "Create_routine_priv": "Y",
                                    "Alter_routine_priv": "Y",
                                    "Create_user_priv": "Y",
                                    "Event_priv": "Y",
                                    "Trigger_priv": "Y",
                                    "Create_tablespace_priv": "Y",
                                    "ssl_type": "",
                                    "ssl_cipher": "",
                                    "x509_issuer": "",
                                    "x509_subject": "",
                                    "max_questions": NumberInt("0"),
                                    "max_updates": NumberInt("0"),
                                    "max_connections": NumberInt("0"),
                                    "max_user_connections": NumberInt("0"),
                                    plugin: "mysql_native_password",
                                    "authentication_string": "*E5B181C8229925C680B0E7ECDDF1CF8F29D97257",
                                    "password_expired": "N",
                                    "password_last_changed": "2020-07-20T22:42:29.000+00:00",
                                    "password_lifetime": null,
                                    "account_locked": "N"
                                }
                            ],
                            dateHolderResult: "2023-02-03T10:09:45.389+00:00",
                            codingResult: "75M7MfQsC4p2rUNe7dDjXK",
                            hashingResult: "z/EfkNDTK/okjQ+jyOQ+vI22o9zFjK38cTtL9r9sEy1a5thhkF4b9sr6LZdR1yE4wIPwNIzApzX7mBcKg0nT+Q==",
                            aesResult: "abcdefghijklmnopqrstuvwxyz",
                            rsaResult: "abcdefghijklmnopqrstuvwxyz"
                        }
                    }
                ]
            }
        ],
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleJobLog$ClientScheduleJobLog"
    },
    enable: NumberInt("1"),
    createDate: ISODate("2023-02-03T10:09:45.044Z"),
    updateDate: ISODate("2023-02-03T10:09:45.416Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleJobLog"
} ]);
db.getCollection("t_schedule_job_logs").insert([ {
    _id: NumberLong("6333792426229760"),
    triggerId: NumberLong("62208697108652459"),
    startupTime: ISODate("2023-02-03T10:09:45.05Z"),
    finishedTime: ISODate("2023-02-03T10:09:45.414Z"),
    success: true,
    detail: {
        results: [
            {
                requestId: "95454643-5bcf-4d57-bf94-8369cea8c46c",
                results: [
                    {
                        scenesCode: "ecommerce_trade_gift",
                        success: true,
                        valueMap: {
                            jdbcResult: [
                                {
                                    Host: "%",
                                    User: "root",
                                    "Select_priv": "Y",
                                    "Insert_priv": "Y",
                                    "Update_priv": "Y",
                                    "Delete_priv": "Y",
                                    "Create_priv": "Y",
                                    "Drop_priv": "Y",
                                    "Reload_priv": "Y",
                                    "Shutdown_priv": "Y",
                                    "Process_priv": "Y",
                                    "File_priv": "Y",
                                    "Grant_priv": "Y",
                                    "References_priv": "Y",
                                    "Index_priv": "Y",
                                    "Alter_priv": "Y",
                                    "Show_db_priv": "Y",
                                    "Super_priv": "Y",
                                    "Create_tmp_table_priv": "Y",
                                    "Lock_tables_priv": "Y",
                                    "Execute_priv": "Y",
                                    "Repl_slave_priv": "Y",
                                    "Repl_client_priv": "Y",
                                    "Create_view_priv": "Y",
                                    "Show_view_priv": "Y",
                                    "Create_routine_priv": "Y",
                                    "Alter_routine_priv": "Y",
                                    "Create_user_priv": "Y",
                                    "Event_priv": "Y",
                                    "Trigger_priv": "Y",
                                    "Create_tablespace_priv": "Y",
                                    "ssl_type": "",
                                    "ssl_cipher": "",
                                    "x509_issuer": "",
                                    "x509_subject": "",
                                    "max_questions": NumberInt("0"),
                                    "max_updates": NumberInt("0"),
                                    "max_connections": NumberInt("0"),
                                    "max_user_connections": NumberInt("0"),
                                    plugin: "mysql_native_password",
                                    "authentication_string": "*E5B181C8229925C680B0E7ECDDF1CF8F29D97257",
                                    "password_expired": "N",
                                    "password_last_changed": "2020-07-20T22:42:29.000+00:00",
                                    "password_lifetime": null,
                                    "account_locked": "N"
                                }
                            ],
                            dateHolderResult: "2023-02-03T10:09:45.233+00:00",
                            codingResult: "75M7MfQsC4p2rUNe7dDjXK",
                            hashingResult: "z/EfkNDTK/okjQ+jyOQ+vI22o9zFjK38cTtL9r9sEy1a5thhkF4b9sr6LZdR1yE4wIPwNIzApzX7mBcKg0nT+Q==",
                            aesResult: "abcdefghijklmnopqrstuvwxyz",
                            rsaResult: "abcdefghijklmnopqrstuvwxyz"
                        }
                    }
                ]
            }
        ],
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleJobLog$ClientScheduleJobLog"
    },
    enable: NumberInt("1"),
    createDate: ISODate("2023-02-03T10:09:45.047Z"),
    updateDate: ISODate("2023-02-03T10:09:45.414Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleJobLog"
} ]);
db.getCollection("t_schedule_job_logs").insert([ {
    _id: NumberLong("6333792426246144"),
    triggerId: NumberLong("62208697108652457"),
    startupTime: ISODate("2023-02-03T10:09:45.056Z"),
    finishedTime: ISODate("2023-02-03T10:09:45.728Z"),
    success: true,
    detail: {
        results: [
            {
                requestId: "520b1046-2c7a-4605-8467-1740a90eed06",
                results: [
                    {
                        scenesCode: "ecommerce_trade_gift",
                        success: true,
                        valueMap: {
                            jdbcResult: [
                                {
                                    Host: "%",
                                    User: "root",
                                    "Select_priv": "Y",
                                    "Insert_priv": "Y",
                                    "Update_priv": "Y",
                                    "Delete_priv": "Y",
                                    "Create_priv": "Y",
                                    "Drop_priv": "Y",
                                    "Reload_priv": "Y",
                                    "Shutdown_priv": "Y",
                                    "Process_priv": "Y",
                                    "File_priv": "Y",
                                    "Grant_priv": "Y",
                                    "References_priv": "Y",
                                    "Index_priv": "Y",
                                    "Alter_priv": "Y",
                                    "Show_db_priv": "Y",
                                    "Super_priv": "Y",
                                    "Create_tmp_table_priv": "Y",
                                    "Lock_tables_priv": "Y",
                                    "Execute_priv": "Y",
                                    "Repl_slave_priv": "Y",
                                    "Repl_client_priv": "Y",
                                    "Create_view_priv": "Y",
                                    "Show_view_priv": "Y",
                                    "Create_routine_priv": "Y",
                                    "Alter_routine_priv": "Y",
                                    "Create_user_priv": "Y",
                                    "Event_priv": "Y",
                                    "Trigger_priv": "Y",
                                    "Create_tablespace_priv": "Y",
                                    "ssl_type": "",
                                    "ssl_cipher": "",
                                    "x509_issuer": "",
                                    "x509_subject": "",
                                    "max_questions": NumberInt("0"),
                                    "max_updates": NumberInt("0"),
                                    "max_connections": NumberInt("0"),
                                    "max_user_connections": NumberInt("0"),
                                    plugin: "mysql_native_password",
                                    "authentication_string": "*E5B181C8229925C680B0E7ECDDF1CF8F29D97257",
                                    "password_expired": "N",
                                    "password_last_changed": "2020-07-20T22:42:29.000+00:00",
                                    "password_lifetime": null,
                                    "account_locked": "N"
                                }
                            ],
                            dateHolderResult: "2023-02-03T10:09:45.702+00:00",
                            codingResult: "75M7MfQsC4p2rUNe7dDjXK",
                            hashingResult: "z/EfkNDTK/okjQ+jyOQ+vI22o9zFjK38cTtL9r9sEy1a5thhkF4b9sr6LZdR1yE4wIPwNIzApzX7mBcKg0nT+Q==",
                            aesResult: "abcdefghijklmnopqrstuvwxyz",
                            rsaResult: "abcdefghijklmnopqrstuvwxyz"
                        }
                    }
                ]
            }
        ],
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleJobLog$ClientScheduleJobLog"
    },
    enable: NumberInt("1"),
    createDate: ISODate("2023-02-03T10:09:45.048Z"),
    updateDate: ISODate("2023-02-03T10:09:45.729Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleJobLog"
} ]);
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
    cron: "0/5 * * * * ?",
    monitorExecution: true,
    failover: true,
    misfire: true,
    timeZone: "GMT+08:00",
    maxTimeDiffSeconds: NumberInt("-1"),
    reconcileIntervalMinutes: NumberInt("0"),
    maxTimeoutMs: NumberLong("30000"),
    runState: "SUCCESS",
    properties: {
        requests: [
            {
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
            }
        ],
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleTrigger$ClientScheduleConfig"
    },
    enable: NumberInt("1"),
    labels: [
        "safecloud",
        "production",
        "monitor",
        "node",
        "vm"
    ],
    remark: "safecloud monitor for vm",
    updateDate: ISODate("2023-02-03T10:09:45.721Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleTrigger"
} ]);
db.getCollection("t_schedule_triggers").insert([ {
    _id: NumberLong("62208697108652457"),
    cron: "0/5 * * * * ?",
    monitorExecution: true,
    failover: true,
    misfire: true,
    timeZone: "GMT+08:00",
    maxTimeDiffSeconds: NumberInt("-1"),
    reconcileIntervalMinutes: NumberInt("0"),
    maxTimeoutMs: NumberLong("30000"),
    runState: "SUCCESS",
    properties: {
        requests: [
            {
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
            }
        ],
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleTrigger$ClientScheduleConfig"
    },
    enable: NumberInt("1"),
    labels: [
        "safecloud",
        "production",
        "monitor",
        "app",
        "mysql",
        "elec_bill_history"
    ],
    remark: "safecloud monitor for mysql",
    updateDate: ISODate("2023-02-03T10:09:45.721Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleTrigger"
} ]);
db.getCollection("t_schedule_triggers").insert([ {
    _id: NumberLong("62208697108652458"),
    cron: "0/5 * * * * ?",
    monitorExecution: true,
    failover: true,
    misfire: true,
    timeZone: "GMT+08:00",
    maxTimeDiffSeconds: NumberInt("-1"),
    reconcileIntervalMinutes: NumberInt("0"),
    maxTimeoutMs: NumberLong("30000"),
    runState: "SUCCESS",
    properties: {
        requests: [
            {
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
            }
        ],
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleTrigger$ClientScheduleConfig"
    },
    enable: NumberInt("1"),
    labels: [
        "safecloud",
        "production",
        "monitor",
        "app",
        "mysql",
        "water_bill_history"
    ],
    remark: "safecloud monitor for mysql",
    updateDate: ISODate("2023-02-03T10:09:45.41Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleTrigger"
} ]);
db.getCollection("t_schedule_triggers").insert([ {
    _id: NumberLong("62208697108652459"),
    cron: "0/5 * * * * ?",
    monitorExecution: true,
    failover: true,
    misfire: true,
    timeZone: "GMT+08:00",
    maxTimeDiffSeconds: NumberInt("-1"),
    reconcileIntervalMinutes: NumberInt("0"),
    maxTimeoutMs: NumberLong("30000"),
    runState: "SUCCESS",
    properties: {
        requests: [
            {
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
            }
        ],
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleTrigger$ClientScheduleConfig"
    },
    enable: NumberInt("1"),
    labels: [
        "safecloud",
        "production",
        "monitor",
        "emr",
        "spark",
        "history_stream_job"
    ],
    remark: "safecloud monitor for emr",
    updateDate: ISODate("2023-02-03T10:09:45.408Z"),
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
