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

 Date: 05/02/2023 22:09:00
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
    _id: NumberLong("6335562311041026"),
    triggerId: NumberLong("62208697108652458"),
    startupTime: ISODate("2023-02-04T16:10:10.359Z"),
    finishedTime: ISODate("2023-02-04T16:10:10.657Z"),
    success: false,
    detail: {
        results: [
            {
                requestId: "29b88044-3fc0-4850-9d63-b4c8f6b30a11",
                results: [
                    {
                        scenesCode: "ecommerce_trade_gift",
                        success: false,
                        reason: "java.net.ConnectException: Failed to connect to localhost/0.0.0.0:28002"
                    }
                ]
            }
        ],
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleJobLog$ClientScheduleJobLog"
    },
    enable: NumberInt("1"),
    createDate: ISODate("2023-02-04T16:10:10.243Z"),
    updateDate: ISODate("2023-02-04T16:10:10.662Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleJobLog"
} ]);
db.getCollection("t_schedule_job_logs").insert([ {
    _id: NumberLong("6335562311024640"),
    triggerId: NumberLong("62208697108652457"),
    startupTime: ISODate("2023-02-04T16:10:10.359Z"),
    finishedTime: ISODate("2023-02-04T16:10:10.66Z"),
    success: false,
    detail: {
        results: [
            {
                requestId: "a5315ffc-fbf3-484f-bf45-e05fbb9d2df3",
                results: [
                    {
                        scenesCode: "ecommerce_trade_gift",
                        success: false,
                        reason: "java.net.ConnectException: Failed to connect to localhost/0.0.0.0:28002"
                    }
                ]
            }
        ],
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleJobLog$ClientScheduleJobLog"
    },
    enable: NumberInt("1"),
    createDate: ISODate("2023-02-04T16:10:10.243Z"),
    updateDate: ISODate("2023-02-04T16:10:10.666Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleJobLog"
} ]);
db.getCollection("t_schedule_job_logs").insert([ {
    _id: NumberLong("6335562311041025"),
    triggerId: NumberLong("62208697108652456"),
    startupTime: ISODate("2023-02-04T16:10:10.359Z"),
    finishedTime: ISODate("2023-02-04T16:10:10.657Z"),
    success: false,
    detail: {
        results: [
            {
                requestId: "bfbc7bee-2a2b-4016-8bee-1b6f0a506947",
                results: [
                    {
                        scenesCode: "ecommerce_trade_gift",
                        success: false,
                        reason: "java.net.ConnectException: Failed to connect to localhost/0.0.0.0:28002"
                    }
                ]
            }
        ],
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleJobLog$ClientScheduleJobLog"
    },
    enable: NumberInt("1"),
    createDate: ISODate("2023-02-04T16:10:10.243Z"),
    updateDate: ISODate("2023-02-04T16:10:10.661Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleJobLog"
} ]);
db.getCollection("t_schedule_job_logs").insert([ {
    _id: NumberLong("6335562311041024"),
    triggerId: NumberLong("62208697108652459"),
    startupTime: ISODate("2023-02-04T16:10:10.369Z"),
    finishedTime: ISODate("2023-02-04T16:10:10.658Z"),
    success: false,
    detail: {
        results: [
            {
                requestId: "7dfdb469-491c-4114-884f-0ea7a7a601a1",
                results: [
                    {
                        scenesCode: "ecommerce_trade_gift",
                        success: false,
                        reason: "java.net.ConnectException: Failed to connect to localhost/0.0.0.0:28002"
                    }
                ]
            }
        ],
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleJobLog$ClientScheduleJobLog"
    },
    enable: NumberInt("1"),
    createDate: ISODate("2023-02-04T16:10:10.243Z"),
    updateDate: ISODate("2023-02-04T16:10:10.661Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleJobLog"
} ]);
db.getCollection("t_schedule_job_logs").insert([ {
    _id: NumberLong("6335562390257664"),
    triggerId: NumberLong("62208697108652456"),
    startupTime: ISODate("2023-02-04T16:10:15.087Z"),
    finishedTime: ISODate("2023-02-04T16:10:15.2Z"),
    success: false,
    detail: {
        results: [
            {
                requestId: "ed62beb6-002b-4389-a3f2-cc914a53c63f",
                results: [
                    {
                        scenesCode: "ecommerce_trade_gift",
                        success: false,
                        reason: "java.net.ConnectException: Failed to connect to localhost/0.0.0.0:28002"
                    }
                ]
            }
        ],
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleJobLog$ClientScheduleJobLog"
    },
    enable: NumberInt("1"),
    createDate: ISODate("2023-02-04T16:10:15.078Z"),
    updateDate: ISODate("2023-02-04T16:10:15.205Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleJobLog"
} ]);
db.getCollection("t_schedule_job_logs").insert([ {
    _id: NumberLong("6335562390519809"),
    triggerId: NumberLong("62208697108652458"),
    startupTime: ISODate("2023-02-04T16:10:15.101Z"),
    finishedTime: ISODate("2023-02-04T16:10:15.204Z"),
    success: false,
    detail: {
        results: [
            {
                requestId: "6c48c9d6-a051-4405-97cd-99c85d59942c",
                results: [
                    {
                        scenesCode: "ecommerce_trade_gift",
                        success: false,
                        reason: "java.net.ConnectException: Failed to connect to localhost/0.0.0.0:28002"
                    }
                ]
            }
        ],
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleJobLog$ClientScheduleJobLog"
    },
    enable: NumberInt("1"),
    createDate: ISODate("2023-02-04T16:10:15.094Z"),
    updateDate: ISODate("2023-02-04T16:10:15.205Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleJobLog"
} ]);
db.getCollection("t_schedule_job_logs").insert([ {
    _id: NumberLong("6335562390519808"),
    triggerId: NumberLong("62208697108652457"),
    startupTime: ISODate("2023-02-04T16:10:15.101Z"),
    finishedTime: ISODate("2023-02-04T16:10:15.219Z"),
    success: false,
    detail: {
        results: [
            {
                requestId: "4e504c6c-70a2-460c-af67-7862e3452000",
                results: [
                    {
                        scenesCode: "ecommerce_trade_gift",
                        success: false,
                        reason: "java.net.ConnectException: Failed to connect to localhost/0.0.0.0:28002"
                    }
                ]
            }
        ],
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleJobLog$ClientScheduleJobLog"
    },
    enable: NumberInt("1"),
    createDate: ISODate("2023-02-04T16:10:15.094Z"),
    updateDate: ISODate("2023-02-04T16:10:15.22Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleJobLog"
} ]);
db.getCollection("t_schedule_job_logs").insert([ {
    _id: NumberLong("6335562390667264"),
    triggerId: NumberLong("62208697108652459"),
    startupTime: ISODate("2023-02-04T16:10:15.108Z"),
    finishedTime: ISODate("2023-02-04T16:10:15.195Z"),
    success: false,
    detail: {
        results: [
            {
                requestId: "d50832c0-1336-4d27-b8ed-1ee840f635ea",
                results: [
                    {
                        scenesCode: "ecommerce_trade_gift",
                        success: false,
                        reason: "java.net.ConnectException: Failed to connect to localhost/0.0.0.0:28002"
                    }
                ]
            }
        ],
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleJobLog$ClientScheduleJobLog"
    },
    enable: NumberInt("1"),
    createDate: ISODate("2023-02-04T16:10:15.103Z"),
    updateDate: ISODate("2023-02-04T16:10:15.195Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleJobLog"
} ]);
db.getCollection("t_schedule_job_logs").insert([ {
    _id: NumberLong("6335564441387011"),
    triggerId: NumberLong("62208697108652457"),
    startupTime: ISODate("2023-02-04T16:12:20.358Z"),
    detail: {
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleJobLog$ClientScheduleJobLog"
    },
    enable: NumberInt("1"),
    createDate: ISODate("2023-02-04T16:12:20.269Z"),
    updateDate: ISODate("2023-02-04T16:12:20.359Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleJobLog"
} ]);
db.getCollection("t_schedule_job_logs").insert([ {
    _id: NumberLong("6335564441387010"),
    triggerId: NumberLong("62208697108652456"),
    startupTime: ISODate("2023-02-04T16:12:20.356Z"),
    detail: {
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleJobLog$ClientScheduleJobLog"
    },
    enable: NumberInt("1"),
    createDate: ISODate("2023-02-04T16:12:20.269Z"),
    updateDate: ISODate("2023-02-04T16:12:20.357Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleJobLog"
} ]);
db.getCollection("t_schedule_job_logs").insert([ {
    _id: NumberLong("6335564441387008"),
    triggerId: NumberLong("62208697108652458"),
    startupTime: ISODate("2023-02-04T16:12:20.357Z"),
    detail: {
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleJobLog$ClientScheduleJobLog"
    },
    enable: NumberInt("1"),
    createDate: ISODate("2023-02-04T16:12:20.269Z"),
    updateDate: ISODate("2023-02-04T16:12:20.358Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ScheduleJobLog"
} ]);
db.getCollection("t_schedule_job_logs").insert([ {
    _id: NumberLong("6335564441387009"),
    triggerId: NumberLong("62208697108652459"),
    startupTime: ISODate("2023-02-04T16:12:20.362Z"),
    detail: {
        type: "CLIENT_SCHEDULER",
        _class: "com.wl4g.rengine.common.entity.ScheduleJobLog$ClientScheduleJobLog"
    },
    enable: NumberInt("1"),
    createDate: ISODate("2023-02-04T16:12:20.269Z"),
    updateDate: ISODate("2023-02-04T16:12:20.363Z"),
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
    name: "monitor_node_vm",
    enable: NumberInt("1"),
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
    enable: NumberInt("1"),
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
    enable: NumberInt("1"),
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
    enable: NumberInt("1"),
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
    name: "subscribe_kafka_notification_msg",
    enable: NumberInt("1"),
    labels: [
        "safecloud",
        "production",
        "kafka subscribe",
        "notification"
    ],
    monitorExecution: true,
    failover: true,
    misfire: true,
    timeZone: "GMT+08:00",
    maxTimeoutMs: 30000,
    runState: null,
    properties: {
        type: "KAFKA_SUBSCRIBE_SCHEDULER",
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
        _class: "com.wl4g.rengine.common.entity.ScheduleTrigger$KafkaSubscribeScheduleConfig"
    },
    remark: "safecloud subscribe for kafka",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
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
