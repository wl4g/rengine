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

 Date: 24/03/2023 18:46:55
*/


// ----------------------------
// Collection structure for gen_aggregates
// ----------------------------
db.getCollection("gen_aggregates").drop();
db.createCollection("gen_aggregates");

// ----------------------------
// Documents of gen_aggregates
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("gen_aggregates").insert([ {
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
// Collection structure for re_controller_logs
// ----------------------------
db.getCollection("re_controller_logs").drop();
db.createCollection("re_controller_logs");

// ----------------------------
// Documents of re_controller_logs
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("re_controller_logs").insert([ {
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
    scheduleId: NumberLong("62208697108652460"),
    jobName: null,
    startupTime: NumberLong("1677238945178"),
    finishedTime: NumberLong("1677238951075"),
    success: false,
    details: {
        type: "GENERIC_EXECUTION",
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
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for re_controller_schedules
// ----------------------------
db.getCollection("re_controller_schedules").drop();
db.createCollection("re_controller_schedules");

// ----------------------------
// Documents of re_controller_schedules
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("re_controller_schedules").insert([ {
    _id: NumberLong("62208697108652455"),
    nameEn: "Tests built Script SDK Example",
    nameZh: "测试内置Script SDK示例",
    tenantId: NumberLong("6508655614689771"),
    enable: NumberInt("1"),
    labels: [
        "example",
        "test",
        "print"
    ],
    remark: "Example for built SDK testing",
    createBy: null,
    createDate: null,
    updateBy: NumberInt("-1"),
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    name: "test_script_sdk_example",
    monitorExecution: true,
    failover: true,
    misfire: true,
    timeZone: "GMT+08:00",
    maxTimeDiffSeconds: NumberInt("-1"),
    reconcileIntervalMinutes: NumberInt("0"),
    maxTimeoutMs: NumberInt("30000"),
    details: {
        type: "GENERIC_EXECUTION",
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
    },
    _class: "com.wl4g.rengine.common.entity.ControllerSchedule"
} ]);
db.getCollection("re_controller_schedules").insert([ {
    _id: NumberLong("62208697108652456"),
    nameEn: "vm_health_detecter",
    nameZh: "vm_health_detecter",
    tenantId: NumberLong("6508655614689771"),
    enable: NumberInt("0"),
    labels: [
        "example",
        "vm",
        "health",
        "detecter"
    ],
    monitorExecution: true,
    failover: true,
    misfire: true,
    timeZone: "GMT+08:00",
    maxTimeoutMs: 30000,
    runState: null,
    details: {
        type: "GENERIC_EXECUTION",
        cron: "0/5 * * * * ?",
        requests: [
            {
                clientId: "JVqEpEwIaqkEkeD5",
                clientSecret: "Uf6nJDyJQHKRP43ycl9vZ9zs7s1nyu77",
                scenesCodes: [
                    "vm_health_detect"
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
    remark: "Example monitoring for VM",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ControllerSchedule"
} ]);
db.getCollection("re_controller_schedules").insert([ {
    _id: NumberLong("62208697108652457"),
    nameEn: "db_records_yesterday_validator",
    nameZh: "db_records_yesterday_validator",
    tenantId: NumberLong("6508655614689771"),
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
    details: {
        type: "GENERIC_EXECUTION",
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
db.getCollection("re_controller_schedules").insert([ {
    _id: NumberLong("62208697108652459"),
    nameEn: "emr_spark_history_stream_job_monitor",
    nameZh: "emr_spark_history_stream_job_monitor",
    tenantId: NumberLong("6508655614689771"),
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
    details: {
        type: "GENERIC_EXECUTION",
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
db.getCollection("re_controller_schedules").insert([ {
    _id: NumberLong("62208697108652460"),
    nameEn: "kafka_subscribe_notification_warning",
    nameZh: "kafka_subscribe_notification_warning",
    tenantId: NumberLong("6508655614689771"),
    enable: NumberInt("1"),
    labels: [
        "example",
        "kafka",
        "subscribe",
        "notification"
    ],
    monitorExecution: true,
    failover: true,
    misfire: true,
    timeZone: "GMT+08:00",
    maxTimeoutMs: 30000,
    runState: null,
    details: {
        type: "KAFKA_SUBSCRIBER",
        topics: [
            "test_topic"
        ],
        concurrency: 1,
        autoAcknowledgment: true,
        request: {
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
        },
        consumerOptions: {
            "group_id": "test_group"
        },
        _class: "com.wl4g.rengine.common.entity.ControllerSchedule$KafkaSubscribeScheduleConfig"
    },
    remark: "Subscribe to notification for kafka",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.ControllerSchedule"
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for re_datasources
// ----------------------------
db.getCollection("re_datasources").drop();
db.createCollection("re_datasources");

// ----------------------------
// Documents of re_datasources
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("re_datasources").insert([ {
    _id: NumberLong("6220869710864222"),
    nameEn: "default",
    nameZh: "default",
    tenantId: NumberLong("6508655614689771"),
    enable: NumberInt("1"),
    labels: [
        "foo",
        "bar"
    ],
    details: {
        type: "MONGO",
        connectionString: "mongodb://localhost:27017/rengine"
    },
    remark: "test",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.DataSourceProperties"
} ]);
db.getCollection("re_datasources").insert([ {
    _id: NumberLong("6220869710864223"),
    nameEn: "default",
    nameZh: "default",
    tenantId: NumberLong("6508655614689771"),
    enable: NumberInt("1"),
    labels: [
        "foo",
        "bar"
    ],
    details: {
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
db.getCollection("re_datasources").insert([ {
    _id: NumberLong("6220869710864228"),
    nameEn: "sc-uat-mysql",
    nameZh: "sc-uat-mysql",
    tenantId: NumberLong("6508655614689771"),
    enable: NumberInt("1"),
    labels: [
        "foo",
        "bar"
    ],
    details: {
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
db.getCollection("re_datasources").insert([ {
    _id: NumberLong("6220869710864224"),
    nameEn: "default",
    nameZh: "default",
    tenantId: NumberLong("6508655614689771"),
    enable: NumberInt("1"),
    labels: [
        "foo",
        "bar"
    ],
    details: {
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
db.getCollection("re_datasources").insert([ {
    _id: NumberLong("6220869710864227"),
    nameEn: "sc-uat-redis",
    nameZh: "sc-uat-redis",
    tenantId: NumberLong("6508655614689771"),
    enable: NumberInt("1"),
    labels: [
        "foo",
        "bar"
    ],
    details: {
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
db.getCollection("re_datasources").insert([ {
    _id: NumberLong("6220869710864225"),
    nameEn: "default",
    nameZh: "default",
    tenantId: NumberLong("6508655614689771"),
    enable: NumberInt("1"),
    labels: [
        "foo",
        "bar"
    ],
    details: {
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
// Collection structure for re_rule_scripts
// ----------------------------
db.getCollection("re_rule_scripts").drop();
db.createCollection("re_rule_scripts");

// ----------------------------
// Documents of re_rule_scripts
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("re_rule_scripts").insert([ {
    _id: NumberLong("650869239922100"),
    revision: NumberInt("1"),
    ruleId: NumberLong("650869239922100"),
    entrypointUploadId: NumberLong("650869710864310"),
    uploadIds: [
        NumberLong("650869710864310"),
        NumberLong("650869710864311")
    ],
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_rule_scripts").insert([ {
    _id: NumberLong("650869239922668"),
    revision: NumberInt("1"),
    ruleId: NumberLong("650869239922658"),
    entrypointUploadId: NumberLong("650869710864381"),
    uploadIds: [
        NumberLong("650869710864381"),
        NumberLong("650869710864310"),
        NumberLong("650869710864311")
    ],
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_rule_scripts").insert([ {
    _id: NumberLong("650869239922669"),
    revision: NumberInt("1"),
    ruleId: NumberLong("650869239922659"),
    entrypointUploadId: NumberLong("650869710864381"),
    uploadIds: [
        NumberLong("650869710864381"),
        NumberLong("650869710864310"),
        NumberLong("650869710864311")
    ],
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_rule_scripts").insert([ {
    _id: NumberLong("650869239922670"),
    revision: NumberInt("1"),
    ruleId: NumberLong("650869239922660"),
    entrypointUploadId: NumberLong("650869710864382"),
    uploadIds: [
        NumberLong("650869710864382"),
        NumberLong("650869710864310"),
        NumberLong("650869710864311")
    ],
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_rule_scripts").insert([ {
    _id: NumberLong("650869239922671"),
    revision: NumberInt("1"),
    ruleId: NumberLong("650869239922661"),
    entrypointUploadId: NumberLong("650869710864383"),
    uploadIds: [
        NumberLong("650869710864383"),
        NumberLong("650869710864310"),
        NumberLong("650869710864311")
    ],
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_rule_scripts").insert([ {
    _id: NumberLong("650869239922672"),
    revision: NumberInt("1"),
    ruleId: NumberLong("650869239922662"),
    entrypointUploadId: NumberLong("650869710864384"),
    uploadIds: [
        NumberLong("650869710864384"),
        NumberLong("650869710864310"),
        NumberLong("650869710864311")
    ],
    tenantId: NumberLong("6508655614689771"),
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
// Collection structure for re_rules
// ----------------------------
db.getCollection("re_rules").drop();
db.createCollection("re_rules");

// ----------------------------
// Documents of re_rules
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("re_rules").insert([ {
    _id: NumberLong("650869239922100"),
    nameEn: "Print rule",
    nameZh: "参数打印规则",
    engine: "JS",
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_rules").insert([ {
    _id: NumberLong("650869239922658"),
    nameEn: "Test built script SDKs example",
    nameZh: "内置脚本SDKs测试示例",
    engine: "JS",
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_rules").insert([ {
    _id: NumberLong("650869239922659"),
    nameEn: "Ecommerce Trade graft rule",
    nameZh: "电商充值赠送规则",
    engine: "JS",
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_rules").insert([ {
    _id: NumberLong("650869239922660"),
    nameEn: "VM healthy detect rule",
    nameZh: "VM节点健康监视告警规则",
    engine: "JS",
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_rules").insert([ {
    _id: NumberLong("650869239922661"),
    nameEn: "VM process watch and restart rule",
    nameZh: "VM进程监视告警并重启规则",
    engine: "JS",
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_rules").insert([ {
    _id: NumberLong("650869239922662"),
    nameEn: "Iot temp warning rule",
    nameZh: "IoT设备温度告警规则",
    engine: "JS",
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_rules").insert([ {
    _id: NumberLong("650869239922663"),
    nameEn: "Logs error alarm rule",
    nameZh: "日志异常告警规则",
    engine: "JS",
    tenantId: NumberLong("6508655614689771"),
    enable: NumberInt("1"),
    labels: [
        "example",
        "logs",
        "warning"
    ],
    remark: "Example for logs errors warning",
    createDate: ISODate("2022-09-27T04:50:39.789Z"),
    updateDate: ISODate("2022-09-27T04:50:39.789Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Rule"
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for re_sceneses
// ----------------------------
db.getCollection("re_sceneses").drop();
db.createCollection("re_sceneses");

// ----------------------------
// Documents of re_sceneses
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("re_sceneses").insert([ {
    _id: NumberLong("650865561468928"),
    nameEn: "Tests built Script SDK Example",
    nameZh: "测试内置Script SDK示例",
    scenesCode: "test_script_sdk_example",
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_sceneses").insert([ {
    _id: NumberLong("650865561468929"),
    nameEn: "电商充值活动赠送策略",
    nameZh: "电商充值活动赠送策略",
    scenesCode: "ecommerce_trade_gift",
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_sceneses").insert([ {
    _id: NumberLong("650865561468930"),
    nameEn: "VM节点健康状态探测",
    nameZh: "VM healthy detect",
    scenesCode: "vm_health_detect",
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_sceneses").insert([ {
    _id: NumberLong("650865561468931"),
    nameEn: "VM process watch and restart",
    nameZh: "VM进程监视并重启",
    scenesCode: "vm_process_watch_restart",
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_sceneses").insert([ {
    _id: NumberLong("650865561468932"),
    nameEn: "Iot device temp alarm",
    nameZh: "Iot设备温度告警",
    scenesCode: "iot_temp_warning",
    tenantId: NumberLong("6508655614689771"),
    enable: NumberInt("1"),
    labels: [
        "example",
        "iot",
        "temperature",
        "warning"
    ],
    remark: "Example for Iot device temperature warning",
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Scenes"
} ]);
db.getCollection("re_sceneses").insert([ {
    _id: NumberLong("650865561468933"),
    nameEn: "Logs error alarm",
    nameZh: "日志异常告警-流程",
    scenesCode: "iot_temp_warning",
    tenantId: NumberLong("6508655614689771"),
    enable: NumberInt("1"),
    labels: [
        "example",
        "logs",
        "warning"
    ],
    remark: "Example for logs errors warning",
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Scenes"
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for re_uploads
// ----------------------------
db.getCollection("re_uploads").drop();
db.createCollection("re_uploads");

// ----------------------------
// Documents of re_uploads
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("re_uploads").insert([ {
    _id: NumberLong("650869710864309"),
    uploadType: "DATASET",
    objectPrefix: "testset/csv/test1.csv",
    filename: "test1.csv",
    extension: ".csv",
    size: NumberLong("1"),
    tenantId: NumberLong("6508655614689771"),
    enable: NumberInt("1"),
    labels: [
        "example",
        "testdata"
    ],
    remark: "Example testdata for CSV.",
    updateDate: ISODate("2022-09-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.UploadObject"
} ]);
db.getCollection("re_uploads").insert([ {
    _id: NumberLong("650869710864310"),
    uploadType: "LIBRARY",
    objectPrefix: "library/js/print-1.0.0.js",
    filename: "print-1.0.0.js",
    extension: ".js",
    size: NumberLong("1"),
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_uploads").insert([ {
    _id: NumberLong("650869710864311"),
    uploadType: "LIBRARY",
    objectPrefix: "library/js/commons-lang-1.0.0.js",
    filename: "commons-lang-1.0.0.js",
    extension: ".js",
    size: NumberLong("1"),
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_uploads").insert([ {
    _id: NumberLong("650869710864381"),
    uploadType: "LIBRARY",
    objectPrefix: "library/js/test-sdk-all-examples-1.0.0.js",
    filename: "test-sdk-all-examples-1.0.0.js",
    extension: ".js",
    size: NumberLong("1"),
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_uploads").insert([ {
    _id: NumberLong("650869710864382"),
    uploadType: "LIBRARY",
    objectPrefix: "library/js/vm-health-detecter-1.0.0.js",
    filename: "vm-health-detecter-1.0.0.js",
    extension: ".js",
    size: NumberLong("1"),
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_uploads").insert([ {
    _id: NumberLong("650869710864383"),
    uploadType: "LIBRARY",
    objectPrefix: "library/js/vm-process-restart-watcher-1.0.0.js",
    filename: "vm-process-restart-watcher-1.0.0.js",
    extension: ".js",
    size: NumberLong("1"),
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_uploads").insert([ {
    _id: NumberLong("650869710864384"),
    uploadType: "LIBRARY",
    objectPrefix: "library/js/db-records-yesterday-validator-1.0.0.js",
    filename: "db-records-yesterday-validator-1.0.0.js",
    extension: ".js",
    size: NumberLong("1"),
    tenantId: NumberLong("6508655614689771"),
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
// Collection structure for re_workflow_graphs
// ----------------------------
db.getCollection("re_workflow_graphs").drop();
db.createCollection("re_workflow_graphs");

// ----------------------------
// Documents of re_workflow_graphs
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("re_workflow_graphs").insert([ {
    _id: NumberLong("650868953448848"),
    revision: NumberLong("1"),
    workflowId: NumberLong("650868953448438"),
    details: {
        engine: "STANDARD_GRAPH",
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
                ruleId: "650869239922658"
            }
        ],
        edges: [
            {
                name: "Unnamed Connection",
                to: "1",
                from: "0",
                attributes: null
            }
        ]
    },
    attributes: {
        foo: "bar",
        openConversationId: "cidG+niQ3Ny/NwUc5KE7mANUQ==",
        robotCode: "dingbhyrzjxx6qjhjcdr",
        scenesGroupV2OwnerUserId: "6165471647114842627",
        scenesGroupV2AdminUserIds: "6165471647114842627",
        scenesGroupV2UserIds: "6165471647114842627"
    },
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_workflow_graphs").insert([ {
    _id: NumberLong("650868953448849"),
    revision: NumberLong("1"),
    workflowId: NumberLong("650868953448439"),
    details: {
        engine: "STANDARD_GRAPH",
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
                    top: "65px",
                    color: "#5f5f5f",
                    left: "325px"
                },
                ruleId: 6.1508692399221e+15,
                color: "#5f5f5f"
            }
        ],
        edges: [
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
        ]
    },
    attributes: {
        foo: "bar"
    },
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_workflow_graphs").insert([ {
    _id: NumberLong("650868953448850"),
    revision: NumberLong("1"),
    workflowId: NumberLong("650868953448440"),
    details: {
        engine: "STANDARD_GRAPH",
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
                ruleId: "650869239922660"
            }
        ],
        edges: [
            {
                name: "Unnamed Connection",
                to: "1",
                from: "0",
                attributes: null
            }
        ]
    },
    attributes: {
        foo: "bar",
        openConversationId: "cidG+niQ3Ny/NwUc5KE7mANUQ==",
        robotCode: "dingbhyrzjxx6qjhjcdr",
        scenesGroupV2OwnerUserId: "6165471647114842627",
        scenesGroupV2AdminUserIds: "6165471647114842627",
        scenesGroupV2UserIds: "6165471647114842627"
    },
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_workflow_graphs").insert([ {
    _id: NumberLong("650868953448851"),
    revision: NumberLong("1"),
    workflowId: NumberLong("650868953448441"),
    details: {
        engine: "STANDARD_GRAPH",
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
                ruleId: "650869239922661"
            }
        ],
        edges: [
            {
                name: "Unnamed Connection",
                to: "1",
                from: "0",
                attributes: null
            }
        ]
    },
    attributes: {
        foo: "bar",
        openConversationId: "cidG+niQ3Ny/NwUc5KE7mANUQ==",
        robotCode: "dingbhyrzjxx6qjhjcdr",
        scenesGroupV2OwnerUserId: "6165471647114842627",
        scenesGroupV2AdminUserIds: "6165471647114842627",
        scenesGroupV2UserIds: "6165471647114842627"
    },
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_workflow_graphs").insert([ {
    _id: NumberLong("650868953448852"),
    revision: NumberLong("1"),
    workflowId: NumberLong("650868953448442"),
    details: {
        engine: "STANDARD_GRAPH",
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
                ruleId: "650869239922662"
            }
        ],
        edges: [
            {
                name: "Unnamed Connection",
                to: "1",
                from: "0",
                attributes: null
            }
        ]
    },
    attributes: {
        foo: "bar",
        openConversationId: "cidG+niQ3Ny/NwUc5KE7mANUQ==",
        robotCode: "dingbhyrzjxx6qjhjcdr",
        scenesGroupV2OwnerUserId: "6165471647114842627",
        scenesGroupV2AdminUserIds: "6165471647114842627",
        scenesGroupV2UserIds: "6165471647114842627"
    },
    tenantId: NumberLong("6508655614689771"),
    enable: NumberInt("1"),
    labels: [
        "example",
        "iot",
        "temperature",
        "warning"
    ],
    remark: "string",
    createDate: ISODate("2022-09-27T04:50:22.304Z"),
    updateDate: ISODate("2022-09-27T04:50:22.304Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.WorkflowGraph"
} ]);
db.getCollection("re_workflow_graphs").insert([ {
    _id: NumberLong("650868953448853"),
    revision: NumberLong("1"),
    workflowId: NumberLong("650868953448443"),
    details: {
        engine: "FLINK_CEP_GRAPH",
        name: "end",
        quantifier: {
            consumingStrategy: "SKIP_TILL_NEXT",
            times: {
                from: 3,
                to: 3,
                windowTime: {
                    unit: "MINUTES",
                    size: 5
                }
            },
            untilCondition: null,
            details: [
                "SINGLE"
            ]
        },
        condition: null,
        nodes: [
            {
                name: "start",
                quantifier: {
                    consumingStrategy: "SKIP_TILL_NEXT",
                    times: null,
                    untilCondition: null,
                    details: [
                        "SINGLE"
                    ]
                },
                condition: {
                    type: "CLASS",
                    className: "org.apache.flink.cep.pattern.conditions.RichAndCondition",
                    nestedConditions: null,
                    subClassName: null
                },
                type: "ATOMIC"
            },
            {
                name: "middle",
                quantifier: {
                    consumingStrategy: "SKIP_TILL_NEXT",
                    times: null,
                    untilCondition: null,
                    details: [
                        "SINGLE"
                    ]
                },
                condition: {
                    type: "CLASS",
                    className: "org.apache.flink.cep.pattern.conditions.RichAndCondition",
                    nestedConditions: [
                        {
                            type: "CLASS",
                            className: "org.apache.flink.cep.pattern.conditions.SubtypeCondition",
                            nestedConditions: null,
                            subClassName: "com.wl4g.rengine.common.event.RengineEvent"
                        },
                        {
                            type: "AVIATOR",
                            expression: "body.level=='ERROR'||body.level=='FATAL'"
                        }
                    ],
                    subClassName: null
                },
                type: "ATOMIC"
            },
            {
                name: "end",
                quantifier: {
                    consumingStrategy: "SKIP_TILL_NEXT",
                    times: null,
                    untilCondition: null,
                    details: [
                        "SINGLE"
                    ]
                },
                condition: {
                    type: "AVIATOR",
                    expression: "body.level=='ERROR'||body.level=='FATAL'"
                },
                type: "ATOMIC"
            }
        ],
        edges: [
            {
                source: "start",
                target: "middle",
                type: "SKIP_TILL_NEXT"
            },
            {
                source: "middle",
                target: "end",
                type: "SKIP_TILL_NEXT"
            }
        ],
        window: {
            type: null,
            time: {
                unit: "MINUTES",
                size: 5
            }
        },
        afterMatchStrategy: {
            type: "SKIP_PAST_LAST_EVENT",
            patternName: null
        },
        type: "COMPOSITE",
        version: 1
    },
    attributes: null,
    tenantId: NumberLong("6508655614689771"),
    enable: NumberInt("1"),
    labels: [
        "example",
        "flink cep",
        "logs",
        "error",
        "warning"
    ],
    remark: "The flink cep pattern graph for log error analysis alarm",
    createDate: ISODate("2022-09-27T04:50:22.304Z"),
    updateDate: ISODate("2022-09-27T04:50:22.304Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.WorkflowGraph"
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for re_workflows
// ----------------------------
db.getCollection("re_workflows").drop();
db.createCollection("re_workflows");

// ----------------------------
// Documents of re_workflows
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("re_workflows").insert([ {
    _id: NumberLong("650868953448438"),
    nameEn: "Tests built Script SDK example",
    nameZh: "测试内置Script SDK示例-流程",
    scenesId: NumberLong("650865561468928"),
    engine: "STANDARD_GRAPH",
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_workflows").insert([ {
    _id: NumberLong("650868953448439"),
    nameEn: "电商充值活动赠送策略-流程",
    nameZh: "电商充值活动赠送策略-流程",
    scenesId: NumberLong("650865561468929"),
    engine: "STANDARD_GRAPH",
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_workflows").insert([ {
    _id: NumberLong("650868953448440"),
    nameEn: "VM node healthy detect",
    nameZh: "VM节点健康状态探测-流程",
    scenesId: NumberLong("650865561468930"),
    engine: "STANDARD_GRAPH",
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_workflows").insert([ {
    _id: NumberLong("650868953448441"),
    nameEn: "VM进 process watch and restart",
    nameZh: "VM进程监视并重启-流程",
    scenesId: NumberLong("650865561468931"),
    engine: "STANDARD_GRAPH",
    tenantId: NumberLong("6508655614689771"),
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
db.getCollection("re_workflows").insert([ {
    _id: NumberLong("650868953448442"),
    nameEn: "Iot device temp alarm",
    nameZh: "Iot设备温度告警-流程",
    scenesId: NumberLong("650865561468932"),
    engine: "STANDARD_GRAPH",
    tenantId: NumberLong("6508655614689771"),
    enable: NumberInt("1"),
    labels: [
        "example",
        "iot",
        "temperature",
        "warning"
    ],
    remark: "Example for Iot device temperature warnning.",
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Workflow"
} ]);
db.getCollection("re_workflows").insert([ {
    _id: NumberLong("650868953448443"),
    nameEn: "Logs error alarm",
    nameZh: "日志异常告警-流程",
    scenesId: NumberLong("650865561468933"),
    engine: "FLINK_CEP_GRAPH",
    tenantId: NumberLong("6508655614689771"),
    enable: NumberInt("1"),
    labels: [
        "example",
        "logs",
        "warning"
    ],
    remark: "Example for Logs errors warning.",
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Workflow"
} ]);
session.commitTransaction(); session.endSession();

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
    _id: NumberLong("6305460145405948"),
    type: "MENU_CLASSIFY_TYPE",
    key: "A",
    value: "A",
    sort: NumberInt("0"),
    name: "classify A",
    enable: NumberInt("1"),
    labels: [ ],
    remark: "Menu type for search classification A",
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.entity.Dict"
} ]);
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405949"),
    type: "MENU_CLASSIFY_TYPE",
    key: "B",
    value: "B",
    sort: NumberInt("0"),
    name: "classify B",
    enable: NumberInt("1"),
    labels: [ ],
    remark: "Menu type for search classification B",
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.entity.Dict"
} ]);
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405950"),
    type: "ENGINE_TYPE",
    key: "JS",
    value: "JS",
    sort: NumberInt("0"),
    name: "JavaScript",
    enable: NumberInt("1"),
    labels: [ ],
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
    name: "Groovy",
    enable: NumberInt("1"),
    labels: [ ],
    remark: "Engine type for GROOVY",
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.entity.Dict"
} ]);
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405952"),
    type: "EXECUTOR_CUSTOM_RESP_TPL",
    key: "DINGTALK",
    value: "{\"msg_signature\":\"%s\",\"timeStamp\":\"%s\",\"nonce\":\"%s\",\"encrypt\":\"%s\"}",
    sort: NumberInt("0"),
    name: "Dingtalk",
    enable: NumberInt("1"),
    labels: [ ],
    remark: "see:https://open.dingtalk.com/document/org/push-events",
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.entity.Dict"
} ]);
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405953"),
    type: "API_CONFIG_DEFINITION",
    key: "MONGO",
    value: "[{\"type\":\"string\",\"name\":\"connectionString\",\"defaultValue\":\"localhost:9092\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null}]",
    sort: NumberInt("0"),
    name: "MongoDB",
    enable: NumberInt("1"),
    labels: [ ],
    remark: null,
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.entity.Dict"
} ]);
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405954"),
    type: "API_CONFIG_DEFINITION",
    key: "REDIS",
    value: "[{\"type\": \"array\", \t\"name\": \"nodes\", \t\"defaultValue\": \"localhost:6379,localhost:6380,localhost:6381,localhost:7379,localhost:7380,localhost:7381\", \t\"required\": true, \t\"maxValue\": null, \t\"minValue\": null, \t\"help\": \"\", \t\"unit\": null }, { \t\"type\": \"string\", \t\"name\": \"username\", \t\"defaultValue\": null, \t\"required\": true, \t\"maxValue\": null, \t\"minValue\": null, \t\"help\": \"\", \t\"unit\": null }, { \t\"type\": \"string\", \t\"name\": \"password\", \t\"defaultValue\": null, \t\"required\": true, \t\"maxValue\": null, \t\"minValue\": null, \t\"help\": \"\", \t\"unit\": null }, { \t\"type\": \"string\", \t\"name\": \"clientName\", \t\"defaultValue\": null, \t\"required\": false, \t\"maxValue\": null, \t\"minValue\": null, \t\"help\": \"\", \t\"unit\": null }, { \t\"type\": \"int\", \t\"name\": \"connTimeout\", \t\"defaultValue\": \"10000\", \t\"required\": true, \t\"maxValue\": null, \t\"minValue\": null, \t\"help\": \"\", \t\"unit\": null }, { \t\"type\": \"int\", \t\"name\": \"soTimeout\", \t\"defaultValue\": \"10000\", \t\"required\": true, \t\"maxValue\": null, \t\"minValue\": null, \t\"help\": \"\", \t\"unit\": null }, { \t\"type\": \"int\", \t\"name\": \"maxAttempts\", \t\"defaultValue\": 3, \t\"required\": true, \t\"maxValue\": null, \t\"minValue\": null, \t\"help\": \"\", \t\"unit\": null }, { \t\"type\": \"int\", \t\"name\": \"database\", \t\"defaultValue\": \"0\", \t\"required\": false, \t\"maxValue\": null, \t\"minValue\": null, \t\"help\": \"\", \t\"unit\": null }, { \t\"type\": \"boolean\", \t\"name\": \"safeMode\", \t\"defaultValue\": \"true\", \t\"required\": false, \t\"maxValue\": null, \t\"minValue\": null, \t\"help\": \"\", \t\"unit\": null }, { \t\"type\": \"object\", \t\"name\": \"poolConfig\", \t\"defaultValue\": null, \t\"required\": true, \t\"maxValue\": null, \t\"minValue\": null, \t\"help\": \"\", \t\"unit\": null, \t\"childrens\": [{ \t\t\"type\": \"int\", \t\t\"name\": \"maxIdle\", \t\t\"defaultValue\": \"5\", \t\t\"required\": false, \t\t\"maxValue\": 1000, \t\t\"minValue\": 1, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"int\", \t\t\"name\": \"minIdle\", \t\t\"defaultValue\": \"\", \t\t\"required\": true, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"int\", \t\t\"name\": \"maxTotal\", \t\t\"defaultValue\": \"10\", \t\t\"required\": true, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"boolean\", \t\t\"name\": \"lifo\", \t\t\"defaultValue\": true, \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"boolean\", \t\t\"name\": \"fairness\", \t\t\"defaultValue\": false, \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"int64\", \t\t\"name\": \"maxWait\", \t\t\"defaultValue\": \"10000\", \t\t\"required\": true, \t\t\"maxValue\": null, \t\t\"minValue\": 1, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"int64\", \t\t\"name\": \"minEvictableIdleMs\", \t\t\"defaultValue\": \"1800000\", \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"int64\", \t\t\"name\": \"evictorShutdownTimeoutMs\", \t\t\"defaultValue\": \"10000\", \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"int64\", \t\t\"name\": \"softMinEvictableIdleMs\", \t\t\"defaultValue\": \"-1\", \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"int\", \t\t\"name\": \"numTestsPerEvictionRun\", \t\t\"defaultValue\": \"3\", \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"int64\", \t\t\"name\": \"durationBetweenEvictionRunsMs\", \t\t\"defaultValue\": \"-1\", \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"boolean\", \t\t\"name\": \"testOnCreate\", \t\t\"defaultValue\": \"false\", \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"boolean\", \t\t\"name\": \"testOnBorrow\", \t\t\"defaultValue\": \"false\", \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"boolean\", \t\t\"name\": \"testOnReturn\", \t\t\"defaultValue\": \"false\", \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"boolean\", \t\t\"name\": \"testWhileIdle\", \t\t\"defaultValue\": \"true\", \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}, { \t\t\"type\": \"boolean\", \t\t\"name\": \"blockWhenExhausted\", \t\t\"defaultValue\": \"true\", \t\t\"required\": false, \t\t\"maxValue\": null, \t\t\"minValue\": null, \t\t\"help\": \"\", \t\t\"unit\": null \t}] }]",
    sort: NumberInt("0"),
    name: "Redis",
    enable: NumberInt("1"),
    labels: [ ],
    remark: null,
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.entity.Dict"
} ]);
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405955"),
    type: "API_CONFIG_DEFINITION",
    key: "JDBC",
    value: "[{\"type\":\"int\",\"name\":\"fetchDirection\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"fetchSize\",\"defaultValue\":10000,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maxFieldSize\",\"defaultValue\":64,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maxRows\",\"defaultValue\":1024,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"queryTimeoutMs\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"driverClassName\",\"defaultValue\":\"com.mysql.cj.jdbc.Driver\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"jdbcUrl\",\"defaultValue\":\"jdbc:mysql://localhost:3306/mysql?useUnicode=true&serverTimezone=UTC&characterEncoding=utf-8&useSSL=false\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"username\",\"defaultValue\":\"root\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"password\",\"defaultValue\":\"123456\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"connectionTimeout\",\"defaultValue\":\"123456\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"validationTimeout\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"validationTestSql\",\"defaultValue\":\"SELECT 1\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"long\",\"name\":\"idleTimeout\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"long\",\"name\":\"softMinIdleTimeout\",\"defaultValue\":-1,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"long\",\"name\":\"maxConnLifeTime\",\"defaultValue\":-1,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"long\",\"name\":\"evictionRunsBetweenTime\",\"defaultValue\":-1,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"initPoolSize\",\"defaultValue\":5,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maximumPoolSize\",\"defaultValue\":20,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"minimumIdle\",\"defaultValue\":1,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"autoCommit\",\"defaultValue\":true,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"cacheState\",\"defaultValue\":true,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testOnBorrow\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testOnCreate\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testOnReturn\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testWhileIdle\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null}]",
    sort: NumberInt("0"),
    name: "JDBC",
    enable: NumberInt("1"),
    labels: [ ],
    remark: null,
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.entity.Dict"
} ]);
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405956"),
    type: "API_CONFIG_DEFINITION",
    key: "KAFKA",
    value: "[{\"type\":\"string\",\"name\":\"key_serializer\",\"defaultValue\":\"org.apache.kafka.common.serialization.StringSerializer\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"bootstrap_servers\",\"defaultValue\":\"localhost:9092\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"client_dns_lookup\",\"defaultValue\":\"use_all_dns_ips\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"metadata_max_age_ms\",\"defaultValue\":\"300000\",\"required\":true,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"send_buffer_bytes\",\"defaultValue\":\"131072\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"receive_buffer_bytes\",\"defaultValue\":\"65536\",\"required\":true,\"maxValue\":null,\"minValue\":-1,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"clientId\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"clientRack\",\"defaultValue\":\"\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"reconnect_backoff_ms\",\"defaultValue\":\"50\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"reconnect_backoff_max_ms\",\"defaultValue\":\"1000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"retries\",\"defaultValue\":\"50\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"retry_backoff_ms\",\"defaultValue\":\"1000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"metrics_sample_window_ms\",\"defaultValue\":\"3000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"metricsNumSamples\",\"defaultValue\":\"2\",\"required\":false,\"maxValue\":1,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"metrics_recording_level\",\"defaultValue\":\"INFO\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"array\",\"name\":\"metric_reporters\",\"defaultValue\":null,\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"security_protocol\",\"defaultValue\":\"PLAINTEXT\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"socket_connection_setup_timeout_ms\",\"defaultValue\":\"10000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"socket_connection_setup_timeout_max_ms\",\"defaultValue\":\"30000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"connections_max_idle_ms\",\"defaultValue\":\"540000\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"request_timeout_ms\",\"defaultValue\":\"30000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"group_id\",\"defaultValue\":\"default-rengine-controller\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"group_instance_id\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"max_poll_interval_ms\",\"defaultValue\":\"300000\",\"required\":false,\"maxValue\":null,\"minValue\":1,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"rebalance_timeout_ms\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"session_timeout_ms\",\"defaultValue\":\"45000\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"heartbeat_interval_ms\",\"defaultValue\":\"3000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"default_api_timeout_ms\",\"defaultValue\":\"60000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"buffer_memory\",\"defaultValue\":\"33554432\",\"required\":false,\"maxValue\":null,\"minValue\":5000,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"acks\",\"defaultValue\":\"all\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"compression_type\",\"defaultValue\":\"none\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"batch_size\",\"defaultValue\":\"16384\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"linger_ms\",\"defaultValue\":\"0\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"delivery_timeout_ms\",\"defaultValue\":\"120000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"send_buffer\",\"defaultValue\":\"131072\",\"required\":false,\"maxValue\":null,\"minValue\":-1,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"receive_buffer\",\"defaultValue\":\"32768\",\"required\":false,\"maxValue\":null,\"minValue\":-1,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"max_request_size\",\"defaultValue\":\"1048576\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"max_block_ms\",\"defaultValue\":\"60000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"metadata_max_idle_ms\",\"defaultValue\":\"300000\",\"required\":false,\"maxValue\":null,\"minValue\":5000,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"max_in_flight_requests_per_connection\",\"defaultValue\":\"5\",\"required\":false,\"maxValue\":null,\"minValue\":1,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"transaction_timeout_ms\",\"defaultValue\":\"60000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null}]",
    sort: NumberInt("0"),
    name: "Kafka",
    enable: NumberInt("1"),
    labels: [ ],
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
    seq: NumberInt("13")
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
    nameEn: "default OIDC client",
    nameZh: "default OIDC client",
    details: {
        type: "OIDC",
        registrationId: "iam",
        clientId: "rengine",
        clientSecret: "FvLNs0sbwF3sN4BbyjZ5GBwN819QFCmF",
        authorizationGrantType: "authorization_code",
        redirectUri: "http://rengine.wl4g.io/api/login/oauth2/callback/iam",
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
// Collection structure for sys_menu_roles
// ----------------------------
db.getCollection("sys_menu_roles").drop();
db.createCollection("sys_menu_roles");

// ----------------------------
// Documents of sys_menu_roles
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6508655614574221"),
    menuId: NumberLong("1"),
    roleId: NumberLong("6508655614612341"),
    _class: "com.wl4g.rengine.common.entity.sys.MenuRole"
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6508655614574222"),
    menuId: NumberLong("11"),
    roleId: NumberLong("6508655614612341"),
    _class: "com.wl4g.rengine.common.entity.sys.MenuRole"
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6508655614574223"),
    menuId: NumberLong("12"),
    roleId: NumberLong("6508655614612341"),
    _class: "com.wl4g.rengine.common.entity.sys.MenuRole"
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6508655614574211"),
    menuId: NumberLong("2000"),
    roleId: NumberLong("6508655614612341"),
    _class: "com.wl4g.rengine.common.entity.sys.MenuRole"
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6508655614574212"),
    menuId: NumberLong("2010"),
    roleId: NumberLong("6508655614612341"),
    _class: "com.wl4g.rengine.common.entity.sys.MenuRole"
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6508655614574213"),
    menuId: NumberLong("2020"),
    roleId: NumberLong("6508655614612341"),
    _class: "com.wl4g.rengine.common.entity.sys.MenuRole"
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6508655614574214"),
    menuId: NumberLong("2030"),
    roleId: NumberLong("6508655614612341"),
    _class: "com.wl4g.rengine.common.entity.sys.MenuRole"
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6508655614574215"),
    menuId: NumberLong("2040"),
    roleId: NumberLong("6508655614612341"),
    _class: "com.wl4g.rengine.common.entity.sys.MenuRole"
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6508655614574216"),
    menuId: NumberLong("2050"),
    roleId: NumberLong("6508655614612341"),
    _class: "com.wl4g.rengine.common.entity.sys.MenuRole"
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for sys_menus
// ----------------------------
db.getCollection("sys_menus").drop();
db.createCollection("sys_menus");

// ----------------------------
// Documents of sys_menus
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("2000"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Rengine",
    nameZh: "引擎管理",
    type: 1,
    status: 0,
    level: 1,
    parentId: NumberLong("0"),
    permissions: [ ],
    pageLocation: null,
    routePath: "/rengine",
    renderTarget: "_self",
    icon: "icon-monitoring",
    sort: 90,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("2001"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Overview",
    nameZh: "概览",
    type: 1,
    status: 0,
    level: 2,
    parentId: NumberLong("2000"),
    permissions: [
        "arn:rengine:home:read:v1"
    ],
    pageLocation: "/rengine/overview/overview",
    routePath: "/rengine/overview",
    renderTarget: "_self",
    icon: "icon-zhuye",
    sort: 1,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("2002"),
    enable: NumberInt("0"),
    classify: "B",
    nameEn: "Scenes",
    nameZh: "场景管理",
    type: 1,
    status: 0,
    level: 2,
    parentId: NumberLong("2020"),
    permissions: [
        "arn:rengine:scenes:read:v1"
    ],
    pageLocation: null,
    routePath: "/rengine/scenes",
    renderTarget: "_self",
    icon: "icon-monitoring",
    sort: 2,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("224"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Scenes List",
    nameZh: "场景配置",
    type: 1,
    status: 0,
    level: 2,
    parentId: NumberLong("2020"),
    permissions: [
        "arn:rengine:scenes:write:v1"
    ],
    pageLocation: "/rengine/scenes/scenes",
    routePath: "/rengine/scenes",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("2003"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Workflows",
    nameZh: "规则编排",
    type: 1,
    status: 0,
    level: 2,
    parentId: NumberLong("2020"),
    permissions: [
        "arn:rengine:workflow:read:v1"
    ],
    pageLocation: "/rengine/workflow/workflow",
    routePath: "/rengine/workflow",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("2004"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Controller Schedulers",
    nameZh: "调度配置",
    type: 1,
    status: 0,
    level: 2,
    parentId: NumberLong("2020"),
    permissions: [
        "arn:rengine:controllerschedule:read:v1"
    ],
    pageLocation: "/rengine/controllerscheduler/controllerscheduler",
    routePath: "/rengine/controllerscheduler",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("2005"),
    enable: NumberInt("0"),
    classify: "B",
    nameEn: "Controller Logs",
    nameZh: "调度日志",
    type: 3,
    status: 0,
    level: 3,
    parentId: NumberLong("2004"),
    permissions: [
        "arn:rengine:controllerlog:read:v1"
    ],
    pageLocation: "/rengine/scheduler/schedulerlog/schedulerlog",
    routePath: "/rengine/scheduler/schedulerlog",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("2007"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Scheduler Edit",
    nameZh: "调度编辑",
    type: 3,
    status: 0,
    level: 3,
    parentId: NumberLong("2004"),
    permissions: [
        "arn:rengine:controllerschedule:read:v1"
    ],
    pageLocation: "/rengine/controllerscheduler/controllerscheduleredit/controllerscheduleredit",
    routePath: "/rengine/controllerscheduler/controllerscheduleredit",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("2008"),
    enable: NumberInt("0"),
    classify: "B",
    nameEn: "Running Histories",
    nameZh: "执行日志",
    type: 1,
    status: 0,
    level: 3,
    parentId: NumberLong("2003"),
    permissions: [
        "arn:rengine:workflowgraph:read:v1"
    ],
    pageLocation: "/rengine/workflow/workflowgraph/workflowgraph",
    routePath: "/rengine/workflow/workflowgraph",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("2030"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Workflow Graphs",
    nameZh: "编排设计",
    type: 3,
    status: 0,
    level: 3,
    parentId: NumberLong("2003"),
    permissions: [
        "arn:rengine:workflowgraph:write:v1"
    ],
    pageLocation: "/rengine/workflow/workflowgraph/workflowgraph",
    routePath: "/rengine/workflow/workflowgraph",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("2040"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Rule Models",
    nameZh: "规则模型",
    type: 1,
    status: 0,
    level: 2,
    parentId: NumberLong("2020"),
    permissions: [
        "arn:rengine:rule:read:v1"
    ],
    pageLocation: "/rengine/rule/rule",
    routePath: "/rengine/rule",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("2050"),
    enable: NumberInt("0"),
    classify: "B",
    nameEn: "Model Edit",
    nameZh: "模型编辑",
    type: 3,
    status: 0,
    level: 3,
    parentId: NumberLong("2040"),
    permissions: [
        "arn:rengine:rule:write:v1"
    ],
    pageLocation: "/rengine/rule/ruleedit/ruleedit",
    routePath: "/rengine/rule/ruleedit",
    renderTarget: "_self",
    sort: 21,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("2060"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Model Desgin",
    nameZh: "规则设计",
    type: 3,
    status: 0,
    level: 3,
    parentId: NumberLong("2040"),
    permissions: [
        "arn:rengine:rulescript:read:v1",
        "arn:rengine:rulescript:write:v1"
    ],
    pageLocation: "/rengine/rule/rulescript/rulescripteditor",
    routePath: "/rengine/rule/rulescript",
    renderTarget: "_self",
    sort: 21,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("2070"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "User Librarys",
    nameZh: "自定义库",
    type: 1,
    status: 0,
    level: 3,
    parentId: NumberLong("2020"),
    permissions: [
        "arn:rengine:upload:read:v1"
    ],
    pageLocation: "/rengine/userlibrary/userlibrary",
    routePath: "/rengine/userlibrary",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("2080"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Test Datasets",
    nameZh: "测试数据集",
    type: 1,
    status: 0,
    level: 3,
    parentId: NumberLong("2020"),
    permissions: [
        "arn:rengine:upload:read:v1"
    ],
    pageLocation: "/rengine/testdataset/testdataset",
    routePath: "/rengine/testdataset",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("2090"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Data Sources",
    nameZh: "数据源管理",
    type: 1,
    status: 0,
    level: 3,
    parentId: NumberLong("2020"),
    permissions: [
        "arn:rengine:datasource:read:v1"
    ],
    pageLocation: "/rengine/datasource/datasource",
    routePath: "/rengine/datasource",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("3000"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Data Sources",
    nameZh: "数据源配置",
    type: 3,
    status: 0,
    level: 3,
    parentId: NumberLong("2090"),
    permissions: [
        "arn:rengine:datasource:write:v1"
    ],
    pageLocation: "/rengine/datasource/datasourceedit/datasourceedit",
    routePath: "/rengine/datasource/datasourceedit",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("3010"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Analysis",
    nameZh: "数据洞察",
    type: 1,
    status: 0,
    level: 2,
    parentId: NumberLong("2000"),
    permissions: [
        "arn:rengine:analysis:read:v1"
    ],
    routePath: "/rengine/analysis",
    renderTarget: "_self",
    icon: "icon-monitoring",
    sort: 100,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu",
    pageLocation: ""
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("3020"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Task Analysis",
    nameZh: "任务分析",
    type: 1,
    status: 0,
    level: 3,
    parentId: NumberLong("3010"),
    permissions: [
        "arn:rengine:analysis:read:v1"
    ],
    pageLocation: "/rengine/analysis/taskanalysis/taskanalysis",
    routePath: "/rengine/taskanalysis",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("3030"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Hits Statistics",
    nameZh: "命中分析",
    type: 1,
    status: 0,
    level: 3,
    parentId: NumberLong("3010"),
    permissions: [
        "arn:rengine:analysis:read:v1"
    ],
    pageLocation: "/rengine/analysis/hitsstatistics/hitsstatistics",
    routePath: "/rengine/hitsstatistics",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("3040"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Event Sources",
    nameZh: "事件溯源",
    type: 1,
    status: 0,
    level: 2,
    parentId: NumberLong("2000"),
    permissions: [
        "arn:rengine:eventdatasource:read:v1"
    ],
    routePath: "/rengine/sources",
    renderTarget: "_self",
    icon: "icon-monitoring",
    sort: 100,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu",
    pageLocation: "/rengine/sources"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("3050"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Events",
    nameZh: "事件列表",
    type: 1,
    status: 0,
    level: 3,
    parentId: NumberLong("3040"),
    permissions: [
        "arn:rengine:eventdatasource:read:v1"
    ],
    pageLocation: "/rengine/analysis/mapstatistics/list/list",
    routePath: "/rengine/mapstatistics/list",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("3060"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Events",
    nameZh: "事件详情",
    type: 1,
    status: 0,
    level: 3,
    parentId: NumberLong("3040"),
    permissions: [
        "arn:rengine:eventdatasource:read:v1"
    ],
    pageLocation: "/rengine/analysis/mapstatistics/list/details/details",
    routePath: "/rengine/mapstatistics/list/details",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("3070"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Event Map",
    nameZh: "事件地图",
    type: 1,
    status: 0,
    level: 3,
    parentId: NumberLong("3040"),
    permissions: [
        "arn:rengine:eventdatasource:read:v1"
    ],
    pageLocation: "/rengine/analysis/mapstatistics/map/map",
    routePath: "/rengine/mapstatistics/map",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("1"),
    enable: NumberInt("1"),
    classify: "A",
    nameEn: "Systems",
    nameZh: "系统管理",
    type: 1,
    status: 0,
    level: 1,
    parentId: NumberLong("0"),
    permissions: [
        "arn:sys:user:read:v1"
    ],
    pageLocation: null,
    routePath: "/sys",
    renderTarget: "_self",
    icon: "icon-monitoring",
    sort: 100,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("11"),
    enable: NumberInt("1"),
    classify: "A",
    nameEn: "Accesses",
    nameZh: "访问控制",
    type: 1,
    status: 0,
    level: 2,
    parentId: NumberLong("1"),
    permissions: [
        "arn:sys:user:read:v1"
    ],
    pageLocation: "/sys/access",
    routePath: "/sys/access",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("111"),
    enable: NumberInt("1"),
    classify: "A",
    nameEn: "Users",
    nameZh: "用户管理",
    type: 1,
    status: 0,
    level: 3,
    parentId: NumberLong("11"),
    permissions: [
        "arn:sys:user:read:v1"
    ],
    pageLocation: "/sys/access/user/user",
    routePath: "/sys/access/user",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("112"),
    enable: NumberInt("0"),
    classify: "A",
    nameEn: "Users",
    nameZh: "用户配置",
    type: 1,
    status: 0,
    level: 3,
    parentId: NumberLong("11"),
    permissions: [
        "arn:sys:user:write:v1"
    ],
    pageLocation: "/sys/access/user/edit",
    routePath: "/sys/access/user/edit",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("113"),
    enable: NumberInt("1"),
    classify: "A",
    nameEn: "Roles",
    nameZh: "角色管理",
    type: 1,
    status: 0,
    level: 3,
    parentId: NumberLong("11"),
    permissions: [
        "arn:sys:role:read:v1"
    ],
    pageLocation: "/sys/access/role/role",
    routePath: "/sys/access/role",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("114"),
    enable: NumberInt("0"),
    classify: "A",
    nameEn: "Roles",
    nameZh: "角色配置",
    type: 1,
    status: 0,
    level: 3,
    parentId: NumberLong("11"),
    permissions: [
        "arn:sys:role:write:v1"
    ],
    pageLocation: "/sys/access/role/edit",
    routePath: "/sys/access/role/edit",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("115"),
    enable: NumberInt("0"),
    classify: "A",
    nameEn: "Tenants",
    nameZh: "租户管理",
    type: 1,
    status: 0,
    level: 3,
    parentId: NumberLong("11"),
    permissions: [
        "arn:sys:tenant:read:v1"
    ],
    pageLocation: "/sys/access/tenant/list",
    routePath: "/sys/access/tenant/list",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("116"),
    enable: NumberInt("0"),
    classify: "A",
    nameEn: "Tenants",
    nameZh: "租户配置",
    type: 1,
    status: 0,
    level: 3,
    parentId: NumberLong("11"),
    permissions: [
        "arn:sys:tenant:write:v1"
    ],
    pageLocation: "/sys/access/tenant/edit",
    routePath: "/sys/access/tenant/edit",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("117"),
    enable: NumberInt("1"),
    classify: "A",
    nameEn: "Menus",
    nameZh: "菜单管理",
    type: 1,
    status: 0,
    level: 3,
    parentId: NumberLong("11"),
    permissions: [
        "arn:sys:menu:read:v1"
    ],
    pageLocation: "/sys/access/menu/menu",
    routePath: "/sys/access/menu/menu",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("118"),
    enable: NumberInt("0"),
    classify: "A",
    nameEn: "Menus",
    nameZh: "菜单配置",
    type: 1,
    status: 0,
    level: 3,
    parentId: NumberLong("11"),
    permissions: [
        "arn:sys:menu:write:v1"
    ],
    pageLocation: "sys/access/menu/edit",
    routePath: "TODO",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 1,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("22"),
    enable: NumberInt("1"),
    classify: "A",
    nameEn: "Settings",
    nameZh: "系统设置",
    type: 1,
    status: 0,
    level: 2,
    parentId: NumberLong("1"),
    permissions: [
        "arn:sys:notification:read:v1",
        "arn:sys:notification:write:v1"
    ],
    pageLocation: "/sys/settings/notifications/notifications",
    routePath: "/rengine/notifications",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("221"),
    enable: NumberInt("1"),
    classify: "A",
    nameEn: "Notifications",
    nameZh: "通知管理",
    type: 1,
    status: 0,
    level: 3,
    parentId: NumberLong("22"),
    permissions: [
        "arn:sys:notification:read:v1",
        "arn:sys:notification:write:v1"
    ],
    pageLocation: "/sys/settings/notifications/notifications",
    routePath: "/sys/settings/notification/exit",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("222"),
    enable: NumberInt("1"),
    classify: "A",
    nameEn: "Authentcations",
    nameZh: "认证集成",
    type: 1,
    status: 0,
    level: 3,
    parentId: NumberLong("22"),
    permissions: [
        "arn:sys:idp:read:v1",
        "arn:sys:idp:write:v1"
    ],
    pageLocation: "/sys/settings/authentcation/authentcation",
    routePath: "/sys/settings/authentcation/edit",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("120"),
    enable: NumberInt("1"),
    classify: "A",
    nameEn: "Tenant",
    nameZh: "租户管理",
    type: 1,
    status: 0,
    level: 3,
    parentId: NumberLong("11"),
    permissions: [
        "arn:sys:menu:read:v1"
    ],
    pageLocation: "/sys/access/tenant/tenant",
    routePath: "/sys/access/tenant",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("121"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Tenant Edit",
    nameZh: "租户编辑",
    type: 3,
    status: 0,
    level: 3,
    parentId: NumberLong("120"),
    permissions: [ ],
    pageLocation: "/sys/access/tenant/tenantedit/tenantedit",
    routePath: "/sys/access/tenant/tenantedit",
    renderTarget: "_self",
    icon: "icon-codeoptimizatio",
    sort: 10,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
} ]);
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("2020"),
    enable: NumberInt("1"),
    classify: "B",
    nameEn: "Rule Devels",
    nameZh: "规则开发",
    type: 1,
    status: 0,
    level: 2,
    parentId: NumberLong("2000"),
    permissions: [
        "arn:rengine:rule:read:v1",
        "arn:rengine:rule:write:v1",
        "arn:rengine:workflow:read:v1",
        "arn:rengine:workflow:write:v1"
    ],
    pageLocation: null,
    routePath: "/rengine/ruledevel",
    renderTarget: "_self",
    icon: "icon-monitoring",
    sort: 2,
    createBy: null,
    createDate: "2019-11-26 10:42:01",
    updateBy: null,
    updateDate: "2021-04-23 14:27:39",
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Menu"
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
    tenantId: NumberLong("61508655614689771"),
    enable: NumberInt("1"),
    labels: [
        "string"
    ],
    details: {
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
    remark: "local test",
    createDate: ISODate("2023-01-07T11:22:48.314Z"),
    updateDate: ISODate("2023-01-07T11:22:48.314Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Notification"
} ]);
db.getCollection("sys_notifications").insert([ {
    _id: NumberLong("6295643646476289"),
    tenantId: NumberLong("61508655614689771"),
    enable: NumberInt("1"),
    labels: [
        "string"
    ],
    details: {
        type: "EMAIL",
        protocol: "smtp",
        host: "smtp.exmail.qq.com",
        port: 465,
        username: "sysnotification01@sunwuu.com",
        password: "CAGd9ZHZMCUHXEVV",
        properties: { }
    },
    remark: "local test",
    createDate: ISODate("2023-01-07T11:22:48.314Z"),
    updateDate: ISODate("2023-01-07T11:22:48.314Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Notification"
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for sys_roles
// ----------------------------
db.getCollection("sys_roles").drop();
db.createCollection("sys_roles");

// ----------------------------
// Documents of sys_roles
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("sys_roles").insert([ {
    _id: NumberLong("6508655614612341"),
    nameEn: "admin",
    nameZh: "超级管理员",
    roleCode: "r:admin",
    tenantId: NumberLong("6508655614689771"),
    enable: NumberInt("1"),
    labels: [
        "admin"
    ],
    remark: "超级管理员",
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Role"
} ]);
db.getCollection("sys_roles").insert([ {
    _id: NumberLong("6508655614612342"),
    nameEn: "view",
    nameZh: "普通用户",
    roleCode: "r:view",
    tenantId: NumberLong("6508655614689771"),
    enable: NumberInt("1"),
    labels: [
        "view"
    ],
    remark: "普通用户",
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Role"
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for sys_tenants
// ----------------------------
db.getCollection("sys_tenants").drop();
db.createCollection("sys_tenants");

// ----------------------------
// Documents of sys_tenants
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("sys_tenants").insert([ {
    _id: NumberLong("6508655614689771"),
    nameEn: "tenant 1",
    nameZh: "租户 1",
    enable: NumberInt("1"),
    labels: [
        "top"
    ],
    remark: null,
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Tenant"
} ]);
db.getCollection("sys_tenants").insert([ {
    _id: NumberLong("6508655614689781"),
    nameEn: "tenant 2",
    nameZh: "租户 2",
    enable: NumberInt("1"),
    labels: [
        "crd"
    ],
    remark: null,
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Tenant"
} ]);
db.getCollection("sys_tenants").insert([ {
    _id: NumberLong("6508655614689791"),
    nameEn: "tenant 3",
    nameZh: "租户 3",
    enable: NumberInt("1"),
    labels: [
        "test"
    ],
    remark: null,
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Tenant"
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for sys_user_roles
// ----------------------------
db.getCollection("sys_user_roles").drop();
db.createCollection("sys_user_roles");

// ----------------------------
// Documents of sys_user_roles
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("sys_user_roles").insert([ {
    _id: NumberLong("6508655614574231"),
    userId: NumberLong("6508655614689001"),
    roleId: NumberLong("6508655614612341"),
    _class: "com.wl4g.rengine.common.entity.sys.UserRole"
} ]);
db.getCollection("sys_user_roles").insert([ {
    _id: NumberLong("6508655614574232"),
    userId: NumberLong("6508655614689001"),
    roleId: NumberLong("6508655614612342"),
    _class: "com.wl4g.rengine.common.entity.sys.UserRole"
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
    _id: NumberLong("6508655614689000"),
    username: "root",
    password: "$2y$13$omIlVnJQbPgVxt4cz9eXpeLpJ2so3SCaXqdJSVc.0p5AZmBxtABAy",
    accountNonExpired: false,
    accountNonLocked: false,
    credentialsNonExpired: false,
    enable: NumberInt("1"),
    attributes: { },
    tenantId: NumberLong("6508655614689771"),
    labels: [
        "admin"
    ],
    remark: "超级管理员",
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.User"
} ]);
db.getCollection("sys_users").insert([ {
    _id: NumberLong("6508655614689001"),
    enable: NumberInt("1"),
    labels: null,
    remark: "string",
    createBy: null,
    createDate: null,
    updateBy: NumberInt("-1"),
    updateDate: "2023-03-24 18:43:30",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    subject: null,
    name: null,
    givenName: null,
    familyName: null,
    middleName: null,
    nickname: null,
    preferredUsername: null,
    gender: null,
    locale: null,
    birthdate: null,
    picture: null,
    zoneinfo: null,
    username: "huangliq1",
    password: null,
    accountNonExpired: false,
    accountNonLocked: false,
    credentialsNonExpired: false,
    authorities: null,
    attributes: null,
    userRoles: null
} ]);
session.commitTransaction(); session.endSession();
