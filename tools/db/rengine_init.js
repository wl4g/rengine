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

 Date: 16/04/2023 01:04:53
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
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-02-24 19:42:25",
    updateBy: NumberInt("-1"),
    updateDate: "2023-02-24 19:42:31",
    delFlag: NumberInt("0"),
    controllerId: NumberLong("62208697108652460"),
    jobName: null,
    startupTime: NumberLong("1677238945178"),
    finishedTime: NumberLong("1677238951075"),
    success: false,
    details: {
        type: "STANDARD_EXECUTION",
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
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416154309148672"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-02 22:32:35",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-02 22:32:35",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652460"),
    jobName: null,
    startupTime: NumberLong("1680445955448"),
    finishedTime: null,
    success: null,
    details: {
        type: "KAFKA_SUBSCRIBER",
        result: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416154385317888"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-02 22:32:40",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-02 22:32:40",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652455"),
    jobName: null,
    startupTime: NumberLong("1680445960095"),
    finishedTime: NumberLong("1680445960291"),
    success: false,
    details: {
        type: "STANDARD_EXECUTION",
        results: [
            {
                requestId: "8f27b71f-ce77-4460-ad5e-51d83a245b8a",
                results: [
                    {
                        scenesCode: "test_script_sdk_example",
                        success: false,
                        valueMap: null,
                        reason: "java.net.ConnectException: Failed to connect to localhost/0.0.0.0:28002"
                    }
                ]
            }
        ]
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416154466910208"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-02 22:32:45",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-02 22:32:45",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652455"),
    jobName: null,
    startupTime: NumberLong("1680445965074"),
    finishedTime: NumberLong("1680445965115"),
    success: false,
    details: {
        type: "STANDARD_EXECUTION",
        results: [
            {
                requestId: "a700da21-ef58-4264-a606-0b75f38dad2b",
                results: [
                    {
                        scenesCode: "test_script_sdk_example",
                        success: false,
                        valueMap: null,
                        reason: "java.net.ConnectException: Failed to connect to localhost/0.0.0.0:28002"
                    }
                ]
            }
        ]
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416161949679616"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-02 22:40:21",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-02 22:40:21",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652460"),
    jobName: null,
    startupTime: NumberLong("1680446421796"),
    finishedTime: null,
    success: null,
    details: {
        type: "KAFKA_SUBSCRIBER",
        result: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416162004369408"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-02 22:40:25",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-02 22:40:25",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652455"),
    jobName: null,
    startupTime: NumberLong("1680446425126"),
    finishedTime: NumberLong("1680446425315"),
    success: false,
    details: {
        type: "STANDARD_EXECUTION",
        results: [
            {
                requestId: "92c3d7de-d4f3-43a4-ba95-b214d64426e3",
                results: [
                    {
                        scenesCode: "test_script_sdk_example",
                        success: false,
                        valueMap: null,
                        reason: "java.net.ConnectException: Failed to connect to localhost/0.0.0.0:28002"
                    }
                ]
            }
        ]
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416162085519360"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-02 22:40:30",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-02 22:40:30",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652455"),
    jobName: null,
    startupTime: NumberLong("1680446430079"),
    finishedTime: NumberLong("1680446430128"),
    success: false,
    details: {
        type: "STANDARD_EXECUTION",
        results: [
            {
                requestId: "e3f9f7db-435d-470d-8310-f8ba59ce3c5f",
                results: [
                    {
                        scenesCode: "test_script_sdk_example",
                        success: false,
                        valueMap: null,
                        reason: "java.net.ConnectException: Failed to connect to localhost/0.0.0.0:28002"
                    }
                ]
            }
        ]
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416162167341056"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-02 22:40:35",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-02 22:40:35",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652455"),
    jobName: null,
    startupTime: NumberLong("1680446435073"),
    finishedTime: NumberLong("1680446435123"),
    success: false,
    details: {
        type: "STANDARD_EXECUTION",
        results: [
            {
                requestId: "a3b0b118-d687-43be-aa0d-562c747eacb7",
                results: [
                    {
                        scenesCode: "test_script_sdk_example",
                        success: false,
                        valueMap: null,
                        reason: "java.net.ConnectException: Failed to connect to localhost/0.0.0.0:28002"
                    }
                ]
            }
        ]
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416162925346816"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-02 22:41:21",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-02 22:41:21",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680446481382"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416163908337664"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-02 22:42:21",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-02 22:42:21",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680446541394"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416164972740608"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-02 22:43:26",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-02 22:43:26",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680446606357"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416165299585024"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-02 22:43:46",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-02 22:43:46",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680446626261"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416169396830208"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-02 22:47:56",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-02 22:47:56",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680446876374"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416171033624576"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-02 22:49:36",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-02 22:49:36",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680446976273"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416172346376192"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-02 22:50:56",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-02 22:50:56",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680447056402"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416173902561280"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-02 22:52:31",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-02 22:52:31",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680447151379"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416175703769088"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-02 22:54:21",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-02 22:54:21",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680447261313"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416177751179264"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-02 22:56:26",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-02 22:56:26",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680447386275"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416217892847616"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-02 23:37:16",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-02 23:37:16",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680449836326"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416220596240384"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-02 23:40:01",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-02 23:40:01",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680450001333"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416226659254272"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-02 23:46:11",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-02 23:46:11",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680450371389"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416267454185472"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-03 00:27:41",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-03 00:27:41",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680452861318"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416268765560832"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-03 00:29:01",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-03 00:29:01",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680452941356"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416269830848512"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-03 00:30:06",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-03 00:30:06",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680453006382"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416272779165696"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-03 00:33:06",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-03 00:33:06",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680453186324"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416275072172032"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-03 00:35:26",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-03 00:35:26",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680453326273"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6416277529575424"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-03 00:37:56",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-03 00:37:56",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680453476266"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6417204382957568"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-03 16:20:46",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-03 16:36:42",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680510046911"),
    finishedTime: NumberLong("1680511002016"),
    success: false,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6417238945775616"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-03 16:55:56",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-03 16:55:56",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680512156455"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6418902255534080"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-04 21:07:56",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-04 21:07:58",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680613676858"),
    finishedTime: NumberLong("1680613677999"),
    success: false,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6418903152705536"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-04 21:08:51",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-04 21:08:51",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680613731585"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6418905443336192"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-04 21:11:11",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-04 21:11:11",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680613871397"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6418907981742080"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-04 21:13:46",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-04 21:13:46",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680614026325"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6418924448841728"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-04 21:30:31",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-04 21:30:31",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680615031403"),
    finishedTime: NumberLong("1680615031790"),
    success: false,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6418926087340032"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-04 21:32:11",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-04 21:32:11",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680615131406"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6418931743129600"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-04 21:37:56",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-04 21:37:56",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680615476626"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6418931747487744"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-04 21:37:56",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-04 21:37:56",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652460"),
    jobName: null,
    startupTime: NumberLong("1680615476836"),
    finishedTime: null,
    success: null,
    details: {
        type: "KAFKA_SUBSCRIBER",
        result: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6418931801571328"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-04 21:38:00",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-04 21:38:00",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652455"),
    jobName: null,
    startupTime: NumberLong("1680615480133"),
    finishedTime: NumberLong("1680615480360"),
    success: false,
    details: {
        type: "STANDARD_EXECUTION",
        results: [
            {
                requestId: "7c8d5d11-7087-42f2-a108-078110e10362",
                results: [
                    {
                        scenesCode: "test_script_sdk_example",
                        success: false,
                        valueMap: null,
                        reason: "java.net.ConnectException: Failed to connect to localhost/0.0.0.0:28002"
                    }
                ]
            }
        ]
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6418931882622976"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-04 21:38:05",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-04 21:38:05",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652455"),
    jobName: null,
    startupTime: NumberLong("1680615485079"),
    finishedTime: NumberLong("1680615485131"),
    success: false,
    details: {
        type: "STANDARD_EXECUTION",
        results: [
            {
                requestId: "075d98f2-2abe-4d43-88b4-cf1378f45800",
                results: [
                    {
                        scenesCode: "test_script_sdk_example",
                        success: false,
                        valueMap: null,
                        reason: "java.net.ConnectException: Failed to connect to localhost/0.0.0.0:28002"
                    }
                ]
            }
        ]
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6418931964592128"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-04 21:38:10",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-04 21:38:10",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652455"),
    jobName: null,
    startupTime: NumberLong("1680615490080"),
    finishedTime: NumberLong("1680615490157"),
    success: false,
    details: {
        type: "STANDARD_EXECUTION",
        results: [
            {
                requestId: "8b7c6d74-b3bb-4393-9cb7-e6646d6e81af",
                results: [
                    {
                        scenesCode: "test_script_sdk_example",
                        success: false,
                        valueMap: null,
                        reason: "java.net.ConnectException: Failed to connect to localhost/0.0.0.0:28002"
                    }
                ]
            }
        ]
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6418932728610816"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-04 21:38:56",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-04 21:38:56",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680615536776"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6418932733050880"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-04 21:38:56",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-04 21:38:56",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652460"),
    jobName: null,
    startupTime: NumberLong("1680615536987"),
    finishedTime: null,
    success: null,
    details: {
        type: "KAFKA_SUBSCRIBER",
        result: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6418932784545792"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-04 21:39:00",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-04 21:39:00",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652455"),
    jobName: null,
    startupTime: NumberLong("1680615540128"),
    finishedTime: NumberLong("1680615540397"),
    success: false,
    details: {
        type: "STANDARD_EXECUTION",
        results: [
            {
                requestId: "327ed502-43ce-4518-b7e3-6cae16fca902",
                results: [
                    {
                        scenesCode: "test_script_sdk_example",
                        success: false,
                        valueMap: null,
                        reason: "java.net.ConnectException: Failed to connect to localhost/0.0.0.0:28002"
                    }
                ]
            }
        ]
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6418932865826816"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-04 21:39:05",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-04 21:39:05",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652455"),
    jobName: null,
    startupTime: NumberLong("1680615545089"),
    finishedTime: NumberLong("1680615545141"),
    success: false,
    details: {
        type: "STANDARD_EXECUTION",
        results: [
            {
                requestId: "1294f26b-7039-4910-b5a5-087b58eeebab",
                results: [
                    {
                        scenesCode: "test_script_sdk_example",
                        success: false,
                        valueMap: null,
                        reason: "java.net.ConnectException: Failed to connect to localhost/0.0.0.0:28002"
                    }
                ]
            }
        ]
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6418932947779584"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-04 21:39:10",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-04 21:39:10",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652455"),
    jobName: null,
    startupTime: NumberLong("1680615550090"),
    finishedTime: NumberLong("1680615550143"),
    success: false,
    details: {
        type: "STANDARD_EXECUTION",
        results: [
            {
                requestId: "827a6f1e-8c5e-4501-8922-63e860e66d4c",
                results: [
                    {
                        scenesCode: "test_script_sdk_example",
                        success: false,
                        valueMap: null,
                        reason: "java.net.ConnectException: Failed to connect to localhost/0.0.0.0:28002"
                    }
                ]
            }
        ]
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6418942405361664"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-04 21:48:47",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-04 21:48:47",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680616127382"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6418943380537344"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-04 21:49:46",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-04 21:49:46",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680616186901"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
db.getCollection("re_controller_logs").insert([ {
    _id: NumberLong("6418945754890240"),
    enable: NumberInt("1"),
    labels: null,
    remark: null,
    createBy: NumberInt("-1"),
    createDate: "2023-04-04 21:52:11",
    updateBy: NumberInt("-1"),
    updateDate: "2023-04-04 21:52:11",
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: null,
    tenantId: null,
    controllerId: NumberLong("62208697108652461"),
    jobName: null,
    startupTime: NumberLong("1680616331833"),
    finishedTime: null,
    success: null,
    details: {
        type: "FLINK_SUBMITTER",
        jarId: null,
        jobArgs: null,
        jobId: null,
        name: null,
        isStoppable: null,
        state: null,
        startTime: null,
        endTime: null,
        duration: null,
        now: null,
        timestamps: null,
        statusCounts: null
    }
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for re_controllers
// ----------------------------
db.getCollection("re_controllers").drop();
db.createCollection("re_controllers");

// ----------------------------
// Documents of re_controllers
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("re_controllers").insert([ {
    _id: NumberLong("62208697108652455"),
    nameEn: "Tests built Script SDK Example",
    nameZh: "测试内置Script SDK示例",
    tenantId: NumberLong("0"),
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
        type: "STANDARD_EXECUTION",
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
    _class: "com.wl4g.rengine.common.entity.Controller"
} ]);
db.getCollection("re_controllers").insert([ {
    _id: NumberLong("62208697108652456"),
    nameEn: "vm_health_detecter",
    nameZh: "vm_health_detecter",
    tenantId: NumberLong("0"),
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
        type: "STANDARD_EXECUTION",
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
        ]
    },
    remark: "Example monitoring for VM",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Controller"
} ]);
db.getCollection("re_controllers").insert([ {
    _id: NumberLong("62208697108652457"),
    nameEn: "db_records_yesterday_validator",
    nameZh: "db_records_yesterday_validator",
    tenantId: NumberLong("0"),
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
        type: "STANDARD_EXECUTION",
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
        ]
    },
    remark: "Example monitoring for DB(mysql,pg,oracle)",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Controller"
} ]);
db.getCollection("re_controllers").insert([ {
    _id: NumberLong("62208697108652459"),
    nameEn: "emr_spark_history_stream_job_monitor",
    nameZh: "emr_spark_history_stream_job_monitor",
    tenantId: NumberLong("0"),
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
        type: "STANDARD_EXECUTION",
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
        ]
    },
    remark: "Generic monitoring for EMR spark",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Controller"
} ]);
db.getCollection("re_controllers").insert([ {
    _id: NumberLong("62208697108652460"),
    nameEn: "kafka_subscribe_notification_warning",
    nameZh: "kafka_subscribe_notification_warning",
    tenantId: NumberLong("0"),
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
        }
    },
    remark: "Subscribe to notification for kafka",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Controller"
} ]);
db.getCollection("re_controllers").insert([ {
    _id: NumberLong("62208697108652461"),
    nameEn: "log warning flink cep job",
    nameZh: "log warning flink cep job",
    tenantId: NumberLong("0"),
    enable: NumberInt("1"),
    labels: [
        "example",
        "flink",
        "cep",
        "job"
    ],
    monitorExecution: true,
    failover: true,
    misfire: true,
    timeZone: "GMT+08:00",
    maxTimeoutMs: 30000,
    runState: null,
    details: {
        type: "FLINK_SUBMITTER",
        jobJarUrls: [
            "s3ref://rengine/__rengine_jars/rengine-job-base-1.0.0-jar-with-dependencies.jar"
        ],
        jobArgs: {
            brokers: "localhost:9092",
            eventTopic: "rengine_event",
            groupId: "rengine_job_default",
            fromOffsetTime: null,
            deserializerClass: "com.wl4g.rengine.job.kafka.OtlpLogKafkaDeserializationSchema",
            keyByExprPath: "body.service",
            runtimeMode: "STREAMING",
            restartAttempts: 3,
            restartDelaySeconds: 15,
            pipelineJars: "file:///tmp/flink-web-355c1015-b3da-432c-83d7-1e7405f4363a/flink-web-upload/9ee79db2-d22d-4362-bfb1-8fa3eab76f51_rengine-job-base-1.0.0-jar-with-dependencies.jar",
            checkpointDir: "file:///tmp/flink-checkpoint",
            checkpointMode: "AT_LEAST_ONCE",
            checkpointIntervalMs: null,
            checkpointTimeout: null,
            checkpointMinPauseBetween: null,
            checkpointMaxConcurrent: null,
            externalizedCheckpointCleanup: null,
            parallelis: -1,
            maxParallelism: -1,
            bufferTimeoutMillis: NumberLong("-1"),
            outOfOrdernessMillis: NumberLong("120000"),
            idleTimeoutMillis: NumberLong("30000"),
            forceUsePrintSink: false,
            jobName: "RengineKafkaFlinkCepStreamingJob",
            cepPatterns: null,
            inProcessingTime: false,
            alertTopic: "rengine_alert",
            offsetResetStrategy: "LATEST",
            partitionDiscoveryIntervalMs: null,
            entryClass: "com.wl4g.rengine.job.cep.RengineKafkaFlinkCepStreaming"
        }
    },
    remark: "The flink job submitter",
    updateDate: ISODate("2022-12-27T04:51:08.533Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.Controller"
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
        NumberLong("650869710864311")
    ],
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
                ruleId: NumberLong("650869239922658")
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
    tenantId: NumberLong("0"),
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
                ruleId: NumberLong("650869239922100"),
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
                ruleId: NumberLong("650869239922100"),
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
                ruleId: NumberLong("650869239922100"),
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
                ruleId: NumberLong("650869239922100"),
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
                ruleId: NumberLong("650869239922100"),
                color: "#5f5f5f"
            },
            {
                "@type": "PROCESS",
                id: "61",
                name: "如何库存<=100",
                attributes: {
                    top: "660px",
                    color: "#5f5f5f",
                    left: "250px"
                },
                ruleId: NumberLong("650869239922100"),
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
                ruleId: NumberLong("650869239922100"),
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
                ruleId: NumberLong("650869239922100"),
                color: "#5f5f5f"
            },
            {
                "@type": "RUN",
                id: "71",
                name: "赠送10元余额",
                attributes: {
                    top: "560px",
                    color: "#5f5f5f",
                    left: "430px"
                },
                ruleId: NumberLong("650869239922100"),
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
    tenantId: NumberLong("0"),
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
                ruleId: NumberLong("650869239922100")
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
    tenantId: NumberLong("0"),
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
                ruleId: NumberLong("650869239922100")
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
    tenantId: NumberLong("0"),
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
                ruleId: NumberLong("650869239922100")
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
db.getCollection("re_workflow_graphs").insert([ {
    _id: NumberLong("6407414061334528"),
    enable: NumberInt("1"),
    labels: null,
    remark: "string",
    createBy: NumberInt("-1"),
    createDate: "2023-03-27 18:21:33",
    updateDate: null,
    delFlag: NumberInt("0"),
    tenantId: NumberInt("0"),
    revision: NumberInt("19"),
    workflowId: NumberLong("650868953448439"),
    details: {
        engine: "STANDARD_GRAPH",
        nodes: [
            {
                "@type": "BOOT",
                id: "0",
                priority: null,
                name: "The Boot",
                attributes: {
                    top: "-40px",
                    color: "#5f5f5f",
                    left: "400px"
                }
            },
            {
                "@type": "PROCESS",
                id: "11",
                priority: null,
                name: "预处理(如篡改当前时间以用于测试目的)",
                attributes: {
                    top: "60px",
                    color: "#5f5f5f",
                    left: "380px"
                },
                ruleId: NumberLong("650869239922100")
            },
            {
                "@type": "RELATION",
                id: "21",
                priority: null,
                name: "当前时间是否满足(10.1~10.8)",
                attributes: {
                    top: "160px",
                    color: "#5f5f5f",
                    left: "380px"
                },
                ruleId: NumberLong("650869239922100")
            },
            {
                "@type": "LOGICAL",
                id: "31",
                priority: null,
                name: "ALL_AND逻辑运算",
                attributes: {
                    top: "260px",
                    color: "#5f5f5f",
                    left: "380px"
                },
                logical: "ALL_AND"
            },
            {
                "@type": "LOGICAL",
                id: "41",
                priority: null,
                name: "AND逻辑运算",
                attributes: {
                    top: "280px",
                    color: "#5f5f5f",
                    left: "580px"
                },
                logical: "AND"
            },
            {
                "@type": "LOGICAL",
                id: "42",
                priority: null,
                name: "AND逻辑运算",
                attributes: {
                    top: "300px",
                    color: "#5f5f5f",
                    left: "200px"
                },
                logical: "AND"
            },
            {
                "@type": "RELATION",
                id: "51",
                priority: null,
                name: "充值是否>=120元",
                attributes: {
                    top: "420px",
                    color: "#5f5f5f",
                    left: "680px"
                },
                ruleId: NumberLong("650869239922100")
            },
            {
                "@type": "LOGICAL",
                id: "52",
                priority: null,
                name: "AND逻辑运算",
                attributes: {
                    top: "400px",
                    color: "#5f5f5f",
                    left: "500px"
                },
                logical: "AND"
            },
            {
                "@type": "RELATION",
                id: "53",
                priority: null,
                name: "当前时间是否满足(10.5~10.8)",
                attributes: {
                    top: "440px",
                    color: "#5f5f5f",
                    left: "120px"
                },
                ruleId: NumberLong("650869239922100")
            },
            {
                "@type": "RELATION",
                id: "54",
                priority: null,
                name: "充值是否>=50元",
                attributes: {
                    top: "460px",
                    color: "#5f5f5f",
                    left: "260px"
                },
                ruleId: NumberLong("650869239922100")
            },
            {
                "@type": "PROCESS",
                id: "61",
                priority: null,
                name: "如何库存<=100",
                attributes: {
                    top: "600px",
                    color: "#5f5f5f",
                    left: "340px"
                },
                ruleId: NumberLong("650869239922100")
            },
            {
                "@type": "FAILBACK",
                id: "62",
                priority: null,
                name: "如果赠送余额失败则执行回退规则",
                attributes: {
                    top: "500px",
                    color: "#5f5f5f",
                    left: "540px"
                },
                ruleId: NumberLong("650869239922100")
            },
            {
                "@type": "RUN",
                id: "63",
                priority: null,
                name: "赠送20积分",
                attributes: {
                    top: "580px",
                    color: "#5f5f5f",
                    left: "220px"
                },
                ruleId: NumberLong("650869239922100")
            },
            {
                "@type": "RUN",
                id: "71",
                priority: null,
                name: "赠送10元余额",
                attributes: {
                    top: "500px",
                    color: "#5f5f5f",
                    left: "520px"
                },
                ruleId: NumberLong("650869239922100")
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
    }
} ]);
db.getCollection("re_workflow_graphs").insert([ {
    _id: NumberLong("6407705370181632"),
    enable: NumberInt("1"),
    labels: null,
    remark: "string",
    createBy: NumberInt("-1"),
    createDate: "2023-03-27 23:17:53",
    updateDate: null,
    delFlag: NumberInt("0"),
    tenantId: NumberInt("0"),
    revision: NumberInt("20"),
    workflowId: NumberLong("650868953448439"),
    details: {
        engine: "STANDARD_GRAPH",
        nodes: [
            {
                "@type": "BOOT",
                id: "0",
                priority: null,
                name: "The Boot",
                attributes: {
                    top: "-40px",
                    color: "#5f5f5f",
                    left: "400px"
                }
            },
            {
                "@type": "PROCESS",
                id: "11",
                priority: null,
                name: "预处理(如篡改当前时间以用于测试目的)",
                attributes: {
                    top: "60px",
                    color: "#5f5f5f",
                    left: "380px"
                },
                ruleId: NumberLong("650869239922100")
            },
            {
                "@type": "RELATION",
                id: "21",
                priority: null,
                name: "当前时间是否满足(10.1~10.8)",
                attributes: {
                    top: "160px",
                    color: "#5f5f5f",
                    left: "380px"
                },
                ruleId: NumberLong("650869239922100")
            },
            {
                "@type": "LOGICAL",
                id: "31",
                priority: null,
                name: "ALL_AND逻辑运算",
                attributes: {
                    top: "260px",
                    color: "#5f5f5f",
                    left: "380px"
                },
                logical: "ALL_AND"
            },
            {
                "@type": "LOGICAL",
                id: "41",
                priority: null,
                name: "AND逻辑运算",
                attributes: {
                    top: "280px",
                    color: "#5f5f5f",
                    left: "580px"
                },
                logical: "AND"
            },
            {
                "@type": "LOGICAL",
                id: "42",
                priority: null,
                name: "AND逻辑运算",
                attributes: {
                    top: "300px",
                    color: "#5f5f5f",
                    left: "200px"
                },
                logical: "AND"
            },
            {
                "@type": "RELATION",
                id: "51",
                priority: null,
                name: "充值是否>=120元",
                attributes: {
                    top: "420px",
                    color: "#5f5f5f",
                    left: "680px"
                },
                ruleId: NumberLong("650869239922100")
            },
            {
                "@type": "LOGICAL",
                id: "52",
                priority: null,
                name: "AND逻辑运算",
                attributes: {
                    top: "400px",
                    color: "#5f5f5f",
                    left: "500px"
                },
                logical: "AND"
            },
            {
                "@type": "RELATION",
                id: "53",
                priority: null,
                name: "当前时间是否满足(10.5~10.8)",
                attributes: {
                    top: "440px",
                    color: "#5f5f5f",
                    left: "120px"
                },
                ruleId: NumberLong("650869239922100")
            },
            {
                "@type": "RELATION",
                id: "54",
                priority: null,
                name: "充值是否>=50元",
                attributes: {
                    top: "460px",
                    color: "#5f5f5f",
                    left: "260px"
                },
                ruleId: NumberLong("650869239922100")
            },
            {
                "@type": "PROCESS",
                id: "61",
                priority: null,
                name: "如何库存<=100",
                attributes: {
                    top: "600px",
                    color: "#5f5f5f",
                    left: "340px"
                },
                ruleId: NumberLong("650869239922100")
            },
            {
                "@type": "FAILBACK",
                id: "62",
                priority: null,
                name: "如果赠送余额失败则执行回退规则",
                attributes: {
                    top: "500px",
                    color: "#5f5f5f",
                    left: "540px"
                },
                ruleId: NumberLong("650869239922100")
            },
            {
                "@type": "RUN",
                id: "63",
                priority: null,
                name: "赠送20积分",
                attributes: {
                    top: "580px",
                    color: "#5f5f5f",
                    left: "220px"
                },
                ruleId: NumberLong("650869239922100")
            },
            {
                "@type": "RUN",
                id: "71",
                priority: null,
                name: "赠送10元余额",
                attributes: {
                    top: "500px",
                    color: "#5f5f5f",
                    left: "520px"
                },
                ruleId: NumberLong("650869239922100")
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
    }
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    tenantId: NumberLong("0"),
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
    isDefault: null,
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
    isDefault: null,
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
    isDefault: null,
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
    isDefault: null,
    labels: [ ],
    remark: "Engine type for GROOVY",
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.entity.Dict"
} ]);
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405959"),
    type: "ENGINE_TYPE",
    key: "PYTHON",
    value: "PYTHON",
    sort: NumberInt("0"),
    name: "Python",
    enable: NumberInt("1"),
    isDefault: null,
    labels: [ ],
    remark: "Engine type for Python",
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.entity.Dict"
} ]);
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405999"),
    type: "ENGINE_TYPE",
    key: "R",
    value: "R",
    sort: NumberInt("0"),
    name: "R",
    enable: NumberInt("1"),
    isDefault: null,
    labels: [ ],
    remark: "Engine type for R",
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.entity.Dict"
} ]);
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405998"),
    type: "ENGINE_TYPE",
    key: "RUBY",
    value: "RUBY",
    sort: NumberInt("0"),
    name: "RUBY",
    enable: NumberInt("1"),
    isDefault: null,
    labels: [ ],
    remark: "Engine type for RUBY",
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
    isDefault: null,
    labels: [ ],
    remark: "see:https://open.dingtalk.com/document/org/push-events",
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.entity.Dict"
} ]);
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405953"),
    type: "DATASOURCE_API_CONFIG_SCHEMA",
    key: "MONGO",
    value: "[{\"type\":\"string\",\"name\":\"connectionString\",\"defaultValue\":\"localhost:9092\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null}]",
    sort: NumberInt("0"),
    name: "MongoDB",
    enable: NumberInt("1"),
    isDefault: null,
    labels: [ ],
    remark: null,
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.entity.Dict"
} ]);
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405954"),
    type: "DATASOURCE_API_CONFIG_SCHEMA",
    key: "REDIS",
    value: "[{\"type\":\"array\",\"name\":\"nodes\",\"defaultValue\":\"localhost:6379,localhost:6380,localhost:6381,localhost:7379,localhost:7380,localhost:7381\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"username\",\"defaultValue\":null,\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"password\",\"defaultValue\":null,\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"clientName\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"connTimeout\",\"defaultValue\":\"10000\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"soTimeout\",\"defaultValue\":\"10000\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maxAttempts\",\"defaultValue\":3,\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"database\",\"defaultValue\":\"0\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"safeMode\",\"defaultValue\":\"true\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"object\",\"name\":\"poolConfig\",\"defaultValue\":null,\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null,\"childrens\":[{\"type\":\"int\",\"name\":\"maxIdle\",\"defaultValue\":\"5\",\"required\":false,\"maxValue\":1000,\"minValue\":1,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"minIdle\",\"defaultValue\":\"\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maxTotal\",\"defaultValue\":\"10\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"lifo\",\"defaultValue\":true,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"fairness\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"maxWait\",\"defaultValue\":\"10000\",\"required\":true,\"maxValue\":null,\"minValue\":1,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"minEvictableIdleMs\",\"defaultValue\":\"1800000\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"evictorShutdownTimeoutMs\",\"defaultValue\":\"10000\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"softMinEvictableIdleMs\",\"defaultValue\":\"-1\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"numTestsPerEvictionRun\",\"defaultValue\":\"3\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"durationBetweenEvictionRunsMs\",\"defaultValue\":\"-1\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testOnCreate\",\"defaultValue\":\"false\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testOnBorrow\",\"defaultValue\":\"false\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testOnReturn\",\"defaultValue\":\"false\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testWhileIdle\",\"defaultValue\":\"true\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"blockWhenExhausted\",\"defaultValue\":\"true\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null}]}]",
    sort: NumberInt("0"),
    name: "Redis",
    enable: NumberInt("1"),
    isDefault: null,
    labels: [ ],
    remark: null,
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.entity.Dict"
} ]);
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405955"),
    type: "DATASOURCE_API_CONFIG_SCHEMA",
    key: "JDBC",
    value: "[{\"type\":\"int\",\"name\":\"fetchDirection\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"fetchSize\",\"defaultValue\":10000,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maxFieldSize\",\"defaultValue\":64,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maxRows\",\"defaultValue\":1024,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"queryTimeoutMs\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"driverClassName\",\"defaultValue\":\"com.mysql.cj.jdbc.Driver\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"jdbcUrl\",\"defaultValue\":\"jdbc:mysql://localhost:3306/mysql?useUnicode=true&serverTimezone=UTC&characterEncoding=utf-8&useSSL=false\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"username\",\"defaultValue\":\"root\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"password\",\"defaultValue\":\"123456\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"connectionTimeout\",\"defaultValue\":\"123456\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"validationTimeout\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"validationTestSql\",\"defaultValue\":\"SELECT 1\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"long\",\"name\":\"idleTimeout\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"long\",\"name\":\"softMinIdleTimeout\",\"defaultValue\":-1,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"long\",\"name\":\"maxConnLifeTime\",\"defaultValue\":-1,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"long\",\"name\":\"evictionRunsBetweenTime\",\"defaultValue\":-1,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"initPoolSize\",\"defaultValue\":5,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"maximumPoolSize\",\"defaultValue\":20,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"minimumIdle\",\"defaultValue\":1,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"autoCommit\",\"defaultValue\":true,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"cacheState\",\"defaultValue\":true,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testOnBorrow\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testOnCreate\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testOnReturn\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"boolean\",\"name\":\"testWhileIdle\",\"defaultValue\":false,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null}]",
    sort: NumberInt("0"),
    name: "JDBC",
    enable: NumberInt("1"),
    isDefault: null,
    labels: [ ],
    remark: null,
    createDate: ISODate("2023-01-14T09:48:39.86Z"),
    updateDate: ISODate("2023-01-14T09:48:39.86Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.service.entity.Dict"
} ]);
db.getCollection("sys_dicts").insert([ {
    _id: NumberLong("6305460145405956"),
    type: "DATASOURCE_API_CONFIG_SCHEMA",
    key: "KAFKA",
    value: "[{\"type\":\"string\",\"name\":\"key_serializer\",\"defaultValue\":\"org.apache.kafka.common.serialization.StringSerializer\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"bootstrap_servers\",\"defaultValue\":\"localhost:9092\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"client_dns_lookup\",\"defaultValue\":\"use_all_dns_ips\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"metadata_max_age_ms\",\"defaultValue\":\"300000\",\"required\":true,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"send_buffer_bytes\",\"defaultValue\":\"131072\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"receive_buffer_bytes\",\"defaultValue\":\"65536\",\"required\":true,\"maxValue\":null,\"minValue\":-1,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"clientId\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"clientRack\",\"defaultValue\":\"\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"reconnect_backoff_ms\",\"defaultValue\":\"50\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"reconnect_backoff_max_ms\",\"defaultValue\":\"1000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"retries\",\"defaultValue\":\"50\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"retry_backoff_ms\",\"defaultValue\":\"1000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"metrics_sample_window_ms\",\"defaultValue\":\"3000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"metricsNumSamples\",\"defaultValue\":\"2\",\"required\":false,\"maxValue\":1,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"metrics_recording_level\",\"defaultValue\":\"INFO\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"array\",\"name\":\"metric_reporters\",\"defaultValue\":null,\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"security_protocol\",\"defaultValue\":\"PLAINTEXT\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"socket_connection_setup_timeout_ms\",\"defaultValue\":\"10000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"socket_connection_setup_timeout_max_ms\",\"defaultValue\":\"30000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"connections_max_idle_ms\",\"defaultValue\":\"540000\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"request_timeout_ms\",\"defaultValue\":\"30000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"group_id\",\"defaultValue\":\"default-rengine-controller\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"group_instance_id\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"max_poll_interval_ms\",\"defaultValue\":\"300000\",\"required\":false,\"maxValue\":null,\"minValue\":1,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"rebalance_timeout_ms\",\"defaultValue\":null,\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"session_timeout_ms\",\"defaultValue\":\"45000\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"heartbeat_interval_ms\",\"defaultValue\":\"3000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"default_api_timeout_ms\",\"defaultValue\":\"60000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"buffer_memory\",\"defaultValue\":\"33554432\",\"required\":false,\"maxValue\":null,\"minValue\":5000,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"acks\",\"defaultValue\":\"all\",\"required\":true,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"string\",\"name\":\"compression_type\",\"defaultValue\":\"none\",\"required\":false,\"maxValue\":null,\"minValue\":null,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"batch_size\",\"defaultValue\":\"16384\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"linger_ms\",\"defaultValue\":\"0\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"delivery_timeout_ms\",\"defaultValue\":\"120000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"send_buffer\",\"defaultValue\":\"131072\",\"required\":false,\"maxValue\":null,\"minValue\":-1,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"receive_buffer\",\"defaultValue\":\"32768\",\"required\":false,\"maxValue\":null,\"minValue\":-1,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"max_request_size\",\"defaultValue\":\"1048576\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"max_block_ms\",\"defaultValue\":\"60000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"metadata_max_idle_ms\",\"defaultValue\":\"300000\",\"required\":false,\"maxValue\":null,\"minValue\":5000,\"help\":\"\",\"unit\":null},{\"type\":\"int\",\"name\":\"max_in_flight_requests_per_connection\",\"defaultValue\":\"5\",\"required\":false,\"maxValue\":null,\"minValue\":1,\"help\":\"\",\"unit\":null},{\"type\":\"int64\",\"name\":\"transaction_timeout_ms\",\"defaultValue\":\"60000\",\"required\":false,\"maxValue\":null,\"minValue\":0,\"help\":\"\",\"unit\":null}]",
    sort: NumberInt("0"),
    name: "Kafka",
    enable: NumberInt("1"),
    isDefault: null,
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
    _id: "650869239922100",
    type: "RuleScript",
    seq: NumberLong("15")
} ]);
db.getCollection("sys_global_sequences").insert([ {
    _id: "650869239922658",
    type: "RuleScript",
    seq: NumberLong("52")
} ]);
db.getCollection("sys_global_sequences").insert([ {
    _id: "650869239922659",
    type: "RuleScript",
    seq: NumberLong("1")
} ]);
db.getCollection("sys_global_sequences").insert([ {
    _id: "650869239922660",
    type: "RuleScript",
    seq: NumberLong("6")
} ]);
db.getCollection("sys_global_sequences").insert([ {
    _id: "650869239922661",
    type: "RuleScript",
    seq: NumberLong("2")
} ]);
db.getCollection("sys_global_sequences").insert([ {
    _id: "650869239922662",
    type: "RuleScript",
    seq: NumberLong("1")
} ]);
db.getCollection("sys_global_sequences").insert([ {
    _id: "650868953448438",
    type: "WorkflowGraph",
    seq: NumberLong("11")
} ]);
db.getCollection("sys_global_sequences").insert([ {
    _id: "650868953448439",
    type: "WorkflowGraph",
    seq: NumberLong("20")
} ]);
db.getCollection("sys_global_sequences").insert([ {
    _id: "650868953448440",
    type: "WorkflowGraph",
    seq: NumberLong("1")
} ]);
db.getCollection("sys_global_sequences").insert([ {
    _id: "650868953448441",
    type: "WorkflowGraph",
    seq: NumberLong("1")
} ]);
db.getCollection("sys_global_sequences").insert([ {
    _id: "650868953448442",
    type: "WorkflowGraph",
    seq: NumberLong("1")
} ]);
db.getCollection("sys_global_sequences").insert([ {
    _id: "650868953448443",
    type: "WorkflowGraph",
    seq: NumberLong("1")
} ]);
db.getCollection("sys_global_sequences").insert([ {
    _id: "global",
    type: "UploadObject",
    seq: NumberInt("7")
} ]);
db.getCollection("sys_global_sequences").insert([ {
    _id: "test-sdk-all-examples",
    type: "UploadObject",
    seq: NumberInt("32")
} ]);
db.getCollection("sys_global_sequences").insert([ {
    _id: "print",
    type: "UploadObject",
    seq: NumberInt("12")
} ]);
db.getCollection("sys_global_sequences").insert([ {
    _id: "vm-health-detecter",
    type: "UploadObject",
    seq: NumberInt("5")
} ]);
db.getCollection("sys_global_sequences").insert([ {
    _id: "print.js",
    type: "UploadObject",
    seq: NumberInt("1")
} ]);
db.getCollection("sys_global_sequences").insert([ {
    _id: "test-sdk-all-examples.js",
    type: "UploadObject",
    seq: NumberInt("11")
} ]);
db.getCollection("sys_global_sequences").insert([ {
    _id: "vm-process-restart-watcher",
    type: "UploadObject",
    seq: NumberInt("1")
} ]);
db.getCollection("sys_global_sequences").insert([ {
    _id: "commons-lang",
    type: "UploadObject",
    seq: NumberInt("3")
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
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691471355909"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("2007"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691471355904"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("2030"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691471355906"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("3010"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691471355910"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("2040"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691471355908"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("2080"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691471355911"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("3050"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691471863808"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("3030"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691471863810"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("2070"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691471863811"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("3000"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691471355907"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("2060"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691471863809"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("2004"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691471863812"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("224"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691471355905"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("2003"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691471945728"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("3040"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691471962112"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("2090"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691471962113"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("2001"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691471962114"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("3070"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691471994881"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("2020"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691471994882"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("3060"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691472027648"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("2000"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
} ]);
db.getCollection("sys_menu_roles").insert([ {
    _id: NumberLong("6411691471994880"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    menuId: NumberInt("3020"),
    roleId: NumberLong("6411691272044544"),
    roles: null,
    menus: null
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
    enable: NumberInt("1"),
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
    pageLocation: "/rengine/scenes/scenes",
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
    nameEn: "Scenes Edit",
    nameZh: "场景配置",
    type: 3,
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
    nameEn: "Controllers",
    nameZh: "调度控制",
    type: 1,
    status: 0,
    level: 2,
    parentId: NumberLong("2020"),
    permissions: [
        "arn:rengine:controller:read:v1"
    ],
    pageLocation: "/rengine/controller/controller",
    routePath: "/rengine/controller",
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
    enable: NumberInt("1"),
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
    pageLocation: "/rengine/controller/controllerlog/controllerlog",
    routePath: "/rengine/controller/controllerlog",
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
    nameEn: "Controller Edit",
    nameZh: "调度编辑",
    type: 3,
    status: 0,
    level: 3,
    parentId: NumberLong("2004"),
    permissions: [
        "arn:rengine:controller:read:v1"
    ],
    pageLocation: "/rengine/controller/edit/controlleredit",
    routePath: "/rengine/controller/edit",
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
    level: 3,
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
    enable: NumberInt("1"),
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
    pageLocation: "/rengine/rule/edit/ruleedit",
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
    enable: NumberInt("1"),
    classify: "A",
    nameEn: "Users",
    nameZh: "用户配置",
    type: 3,
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
    enable: NumberInt("1"),
    classify: "A",
    nameEn: "Roles",
    nameZh: "角色配置",
    type: 3,
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
    pageLocation: "/sys/access/tenant/edit/tenantedit",
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
db.getCollection("sys_menus").insert([ {
    _id: NumberLong("1121"),
    enable: NumberInt("1"),
    classify: "A",
    nameEn: "Users",
    nameZh: "用户删除",
    type: 3,
    status: 0,
    level: 3,
    parentId: NumberLong("11"),
    permissions: [
        "arn:sys:user:delete:v1"
    ],
    pageLocation: "/sys/access/user/delete",
    routePath: "/sys/access/user/delete",
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
    _id: NumberLong("1122"),
    enable: NumberInt("1"),
    classify: "A",
    nameEn: "Users",
    nameZh: "分配角色",
    type: 3,
    status: 0,
    level: 3,
    parentId: NumberLong("11"),
    permissions: [
        "arn:sys:user:role:write:v1"
    ],
    pageLocation: "/sys/access/user/assignrole",
    routePath: "/sys/access/user/assignrole",
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
db.getCollection("sys_roles").insert([ {
    _id: NumberLong("6411691272044544"),
    enable: NumberInt("1"),
    labels: null,
    remark: "",
    createBy: NumberInt("-1"),
    createDate: "2023-03-30 18:52:33",
    updateBy: null,
    updateDate: null,
    delFlag: NumberInt("0"),
    nameEn: "testrole",
    nameZh: "testrole",
    tenantId: null,
    roleCode: "r:test",
    type: null,
    users: null,
    menus: null,
    userRoles: null,
    menuRoles: null
} ]);
db.getCollection("sys_roles").insert([ {
    _id: NumberLong("6411737748946944"),
    enable: NumberInt("0"),
    labels: null,
    remark: "12",
    createBy: NumberInt("-1"),
    createDate: "2023-03-30 19:39:49",
    updateBy: null,
    updateDate: null,
    delFlag: NumberInt("0"),
    nameEn: "12",
    nameZh: "12",
    tenantId: null,
    type: NumberInt("1"),
    roleCode: "12",
    userRoles: null,
    menuRoles: null
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
    _id: NumberLong("0"),
    nameEn: "default tenant",
    nameZh: "默认租户",
    enable: NumberInt("1"),
    labels: [
        "default"
    ],
    remark: null,
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Tenant"
} ]);
db.getCollection("sys_tenants").insert([ {
    _id: NumberLong("6508655614689781"),
    nameEn: "tenant 1",
    nameZh: "租户 1",
    enable: NumberInt("1"),
    labels: [
        "t1"
    ],
    remark: null,
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Tenant"
} ]);
db.getCollection("sys_tenants").insert([ {
    _id: NumberLong("6508655614689791"),
    nameEn: "tenant 2",
    nameZh: "租户 2",
    enable: NumberInt("1"),
    labels: [
        "t2"
    ],
    remark: null,
    createDate: ISODate("2023-02-02T04:46:55.274Z"),
    updateDate: ISODate("2023-02-02T04:46:55.274Z"),
    delFlag: NumberInt("0"),
    _class: "com.wl4g.rengine.common.entity.sys.Tenant"
} ]);
session.commitTransaction(); session.endSession();

// ----------------------------
// Collection structure for sys_uploads
// ----------------------------
db.getCollection("sys_uploads").drop();
db.createCollection("sys_uploads");

// ----------------------------
// Documents of sys_uploads
// ----------------------------
session = db.getMongo().startSession();
session.startTransaction();
db = session.getDatabase("rengine");
db.getCollection("sys_uploads").insert([ {
    _id: NumberLong("650869710864309"),
    uploadType: "TESTCSV",
    objectPrefix: "testcsv/0/test/test-v1.csv",
    filename: "test",
    extension: ".csv",
    size: NumberLong("1"),
    tenantId: NumberLong("0"),
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
db.getCollection("sys_uploads").insert([ {
    _id: NumberLong("650869710864310"),
    uploadType: "LIBJS",
    objectPrefix: "libjs/0/print/print-v1.js",
    filename: "print",
    extension: ".js",
    size: NumberLong("1"),
    tenantId: NumberLong("0"),
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
db.getCollection("sys_uploads").insert([ {
    _id: NumberLong("650869710864311"),
    uploadType: "LIBJS",
    objectPrefix: "libjs/0/commons-lang/commons-lang-v1.js",
    filename: "commons-lang",
    extension: ".js",
    size: NumberLong("1"),
    tenantId: NumberLong("0"),
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
db.getCollection("sys_uploads").insert([ {
    _id: NumberLong("650869710864381"),
    uploadType: "LIBJS",
    objectPrefix: "libjs/0/test-sdk-all-examples/test-sdk-all-examples-v1.js",
    filename: "test-sdk-all-examples",
    extension: ".js",
    size: NumberLong("1"),
    tenantId: NumberLong("0"),
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
db.getCollection("sys_uploads").insert([ {
    _id: NumberLong("650869710864382"),
    uploadType: "LIBJS",
    objectPrefix: "libjs/0/vm-health-detecter/vm-health-detecter-v1.js",
    filename: "vm-health-detecter",
    extension: ".js",
    size: NumberLong("1"),
    tenantId: NumberLong("0"),
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
db.getCollection("sys_uploads").insert([ {
    _id: NumberLong("650869710864383"),
    uploadType: "LIBJS",
    objectPrefix: "libjs/0/vm-process-restart-watcher/vm-process-restart-watcher-v1.js",
    filename: "vm-process-restart-watcher",
    extension: ".js",
    size: NumberLong("1"),
    tenantId: NumberLong("0"),
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
db.getCollection("sys_uploads").insert([ {
    _id: NumberLong("650869710864384"),
    uploadType: "LIBJS",
    objectPrefix: "libjs/0/db-records-yesterday-validator/db-records-yesterday-validator-v1.js",
    filename: "db-records-yesterday-validator",
    extension: ".js",
    size: NumberLong("1"),
    tenantId: NumberLong("0"),
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
db.getCollection("sys_user_roles").insert([ {
    _id: NumberLong("6411231094587393"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    userId: NumberLong("6508655614689001"),
    roleId: NumberLong("6508655614612342"),
    users: null,
    roles: null
} ]);
db.getCollection("sys_user_roles").insert([ {
    _id: NumberLong("6411231094587392"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    userId: NumberLong("6508655614689000"),
    roleId: NumberLong("6508655614612342"),
    users: null,
    roles: null
} ]);
db.getCollection("sys_user_roles").insert([ {
    _id: NumberLong("6411698112856064"),
    createDate: null,
    updateDate: null,
    nameEn: null,
    nameZh: null,
    tenantId: null,
    userId: NumberLong("6411690973429760"),
    roleId: NumberLong("6411691272044544"),
    users: null,
    roles: null
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
    password: "$2y$13$omIlVnJQbPgVxt4cz9eXpeLpJ2so3SCaXqdJSVc.0p5AZmBxtABAy",
    accountNonExpired: false,
    accountNonLocked: false,
    credentialsNonExpired: false,
    authorities: null,
    attributes: null,
    userRoles: null
} ]);
db.getCollection("sys_users").insert([ {
    _id: NumberLong("6411690973429760"),
    enable: NumberInt("1"),
    labels: null,
    remark: "e",
    createBy: NumberInt("-1"),
    createDate: "2023-03-30 18:52:15",
    updateBy: null,
    updateDate: null,
    delFlag: NumberInt("0"),
    nameEn: null,
    nameZh: "test",
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
    username: "test",
    password: "$2y$13$omIlVnJQbPgVxt4cz9eXpeLpJ2so3SCaXqdJSVc.0p5AZmBxtABAy",
    accountNonExpired: false,
    accountNonLocked: false,
    credentialsNonExpired: false,
    authorities: null,
    attributes: null,
    userRoles: null
} ]);
db.getCollection("sys_users").insert([ {
    _id: NumberLong("6411714691366912"),
    enable: NumberInt("1"),
    labels: null,
    remark: "12",
    createBy: NumberInt("-1"),
    createDate: "2023-03-30 19:16:22",
    updateBy: null,
    updateDate: null,
    delFlag: NumberInt("1"),
    nameEn: null,
    nameZh: "12",
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
    username: "12",
    password: null,
    accountNonExpired: false,
    accountNonLocked: false,
    credentialsNonExpired: false,
    authorities: null,
    attributes: null,
    userRoles: null
} ]);
session.commitTransaction(); session.endSession();
