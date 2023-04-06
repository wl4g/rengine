package com.wl4g.rengine.job.cep;
/*
 * Copyright 2017 ~ 2025 the original author or authors. James Wong <jameswong1376@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static com.wl4g.infra.common.lang.EnvironmentUtil.getProperty;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Collections.singleton;
import static java.util.Collections.synchronizedList;
import static java.util.Objects.nonNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.RandomUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.wl4g.infra.common.codec.Encodes;
import com.wl4g.infra.common.lang.Assert2;
import com.wl4g.infra.common.lang.ThreadUtils2;
import com.wl4g.infra.common.lang.tuples.Tuple3;
import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.job.kafka.RengineKafkaFlinkCepStreaming;

import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.logs.v1.InstrumentationLibraryLogs;
import io.opentelemetry.proto.logs.v1.LogRecord;
import io.opentelemetry.proto.logs.v1.ResourceLogs;

/**
 * Refer to {@link com.wl4g.rengine.job.kafka.RengineKafkaFlinkCepStreamingTests}
 * 
 * @author James Wong
 * @version 2023-03-12
 * @since v1.0.0
 */
public class RengineKafkaFlinkCepStreamingIT {

    static final String IT_MOCK_KAFKA_BROKERS = getProperty("IT_KAFKA_BROKERS", "localhost:9092");
    // static final int IT_MOCK_SUFFIX = RandomUtils.nextInt(1, 10000);
    static final int IT_MOCK_SUFFIX = 0;
    static final String IT_MOCK_EVENT_TOPIC = getProperty("IT_MOCK_EVENT_TOPIC_PREFIX", "test_wl4g_rengine_otlp_logs") + "_"
            + IT_MOCK_SUFFIX;
    static final String IT_MOCK_ALERTS_TOPIC = getProperty("IT_MOCK_ALERTS_TOPIC_PREFIX", "test_wl4g_rengine_alert") + "_"
            + IT_MOCK_SUFFIX;
    static final Tuple3[] MOCK_LOGS_RECORDS = { new Tuple3("2023-03-22T14:13:33.697+08:00", "", "") };
    static final AtomicBoolean running = new AtomicBoolean(true);
    static final Long alertsConsumingTimeoutMs = 5 * 1000L;
    static final Deserializer<String> defaultDeserializer = new StringDeserializer();
    static final List<RengineEvent> alerts = synchronizedList(new ArrayList<>());
    static KafkaProducer<String, byte[]> producer;
    static KafkaConsumer<String, byte[]> consumer;

    public static void main(String[] args) throws Exception {
        // LogManager.getLogger("org.apache.flink").setLevel(Level.DEBUG);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown for " + RengineKafkaFlinkCepStreamingIT.class.getSimpleName() + " ...");
            running.set(false);
            try {
                producer.close();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            try {
                consumer.close();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            assertion();
        }));

        startMockLogsProducing();

        RengineKafkaFlinkCepStreaming.main((nonNull(args) && args.length > 0) ? args : DEFAULT_ARGS);
    }

    static void startMockLogsProducing() {
        new Thread(() -> {
            System.out.println("Mock logs producing for waiting init sleep 10s ...");
            ThreadUtils2.sleep(10_000);

            final Properties props = new Properties();
            props.put("bootstrap.servers", IT_MOCK_KAFKA_BROKERS);
            props.put("acks", "0");
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
            producer = new KafkaProducer<>(props);
            try {
                Tuple3[] mockLogRecords = { new Tuple3("1970-01-01T08:03:33.001+08:00", "INFO", "order-service"),
                        new Tuple3("1970-01-01T08:03:44.002+08:00", "INFO", "order-service"),
                        new Tuple3("1970-01-01T08:03:52.003+08:00", "ERROR", "order-service"),
                        new Tuple3("1970-01-01T08:03:59.004+08:00", "INFO", "order-service"),
                        new Tuple3("1970-01-01T08:04:24.005+08:00", "FATAL", "order-service"),
                        new Tuple3("1970-01-01T08:04:54.006+08:00", "INFO", "order-service"),
                        new Tuple3("1970-01-01T08:05:34.007+08:00", "WARN", "order-service"),
                        new Tuple3("1970-01-01T08:05:56.008+08:00", "DEBUG", "order-service"),
                        new Tuple3("1970-01-01T08:06:43.009+08:00", "INFO", "order-service"),
                        new Tuple3("1970-01-01T08:07:12.010+08:00", "INFO", "order-service") };

                for (int i = 0; i < mockLogRecords.length; i++) {
                    Map<String, Object> logRecord = new LinkedHashMap<>();
                    logRecord.put("@timestamp", mockLogRecords[i].getItem1());
                    logRecord.put("@version", "1");
                    logRecord.put("level", mockLogRecords[i].getItem2());
                    logRecord.put("service", mockLogRecords[i].getItem3());
                    logRecord.put("message", format("This is the %sth log message for completed started on 16 ms.", i));
                    logRecord.put("logger_name", "o.s.w.s.DispatcherServlet");
                    logRecord.put("thread_name", "http-nio-0.0.0.0-28001-exec-1");
                    logRecord.put("level_value", 20000);
                    logRecord.put("caller_class_name", "org.springframework.web.servlet.FrameworkServlet");
                    logRecord.put("caller_method_name", "initServletBean");
                    logRecord.put("caller_file_name", "FrameworkServlet.java");
                    logRecord.put("caller_line_number", 547);
                    logRecord.put("logger_group", "main");

                    String logMsg = toJSONString(logRecord);
                    System.out.println(format("Send log (%s) : %s", IT_MOCK_EVENT_TOPIC, logMsg));

                    ExportLogsServiceRequest request = buildOtlpLogsRequest(logMsg);
                    producer.send(new ProducerRecord<String, byte[]>(IT_MOCK_EVENT_TOPIC, request.toByteArray()));
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }

            System.out.println("Mock completed.");
            startAlertsConsuming();

        }).start();
    }

    static void startAlertsConsuming() {
        new Thread(() -> {
            System.out.println("Start alerts consuming for waiting init sleep 3s ...");
            ThreadUtils2.sleep(3_000);

            final Properties props = new Properties();
            props.put("bootstrap.servers", IT_MOCK_KAFKA_BROKERS);
            props.put("group.id", RengineKafkaFlinkCepStreamingIT.class.getSimpleName() + "_alerts");
            props.put("enable.auto.commit", "true");
            props.put("auto.offset.reset", "latest");
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
            consumer = new KafkaConsumer<>(props);
            consumer.subscribe(singleton(IT_MOCK_ALERTS_TOPIC));
            try {
                int c = 0;
                while (++c < 20) {
                    ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofSeconds(1));
                    System.out.println(new Date() + " - " + IT_MOCK_ALERTS_TOPIC + " - consumed records: " + records.count());
                    if (!records.isEmpty()) {
                        for (ConsumerRecord<String, byte[]> record : records) {
                            final RengineEvent alert = RengineEvent
                                    .fromJson(defaultDeserializer.deserialize(record.topic(), record.value()));
                            alerts.add(alert);
                            System.out.println("Alert : " + alert);
                        }
                    }
                    if (alerts.size() >= 1) {
                        break;
                    }
                    Thread.sleep(500L);
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            } finally {
                consumer.commitSync();
                System.exit(0);
            }
        }).start();
    }

    @SuppressWarnings({ "rawtypes" })
    static void assertion() {
        // for example:
        // System.out.println("---------- alerts ----------");
        // alerts.forEach(System.out::println);

        // @formatter:off
        //
        // RengineEvent(id=1679473958849@test_wl4g_rengine_alert_0, type=test_wl4g_rengine_alert_0, observedTime=1679473958849, body={description=The matched alert event msg., match={middle=[{@timestamp=2023-03-22T14:13:52.003+08:00, @version=1, level=ERROR, service=order-service, message=This is the 2th log message for completed started on 16 ms., logger_name=o.s.w.s.DispatcherServlet, thread_name=http-nio-0.0.0.0-28001-exec-1, level_value=20000, caller_class_name=org.springframework.web.servlet.FrameworkServlet, caller_method_name=initServletBean, caller_file_name=FrameworkServlet.java, caller_line_number=547, logger_group=main}], start=[{@timestamp=2023-03-22T14:13:33.001+08:00, @version=1, level=INFO, service=order-service, message=This is the 0th log message for completed started on 16 ms., logger_name=o.s.w.s.DispatcherServlet, thread_name=http-nio-0.0.0.0-28001-exec-1, level_value=20000, caller_class_name=org.springframework.web.servlet.FrameworkServlet, caller_method_name=initServletBean, caller_file_name=FrameworkServlet.java, caller_line_number=547, logger_group=main}]}}, labels=null)
        // RengineEvent(id=1679473958871@test_wl4g_rengine_alert_0, type=test_wl4g_rengine_alert_0, observedTime=1679473958871, body={description=The matched alert event msg., match={middle=[{@timestamp=2023-03-22T14:13:52.003+08:00, @version=1, level=ERROR, service=order-service, message=This is the 2th log message for completed started on 16 ms., logger_name=o.s.w.s.DispatcherServlet, thread_name=http-nio-0.0.0.0-28001-exec-1, level_value=20000, caller_class_name=org.springframework.web.servlet.FrameworkServlet, caller_method_name=initServletBean, caller_file_name=FrameworkServlet.java, caller_line_number=547, logger_group=main}], start=[{@timestamp=2023-03-22T14:13:44.002+08:00, @version=1, level=INFO, service=order-service, message=This is the 1th log message for completed started on 16 ms., logger_name=o.s.w.s.DispatcherServlet, thread_name=http-nio-0.0.0.0-28001-exec-1, level_value=20000, caller_class_name=org.springframework.web.servlet.FrameworkServlet, caller_method_name=initServletBean, caller_file_name=FrameworkServlet.java, caller_line_number=547, logger_group=main}]}}, labels=null)
        // RengineEvent(id=1679473958882@test_wl4g_rengine_alert_0, type=test_wl4g_rengine_alert_0, observedTime=1679473958882, body={description=The matched alert event msg., match={middle=[{@timestamp=2023-03-22T14:14:24.005+08:00, @version=1, level=FATAL, service=order-service, message=This is the 4th log message for completed started on 16 ms., logger_name=o.s.w.s.DispatcherServlet, thread_name=http-nio-0.0.0.0-28001-exec-1, level_value=20000, caller_class_name=org.springframework.web.servlet.FrameworkServlet, caller_method_name=initServletBean, caller_file_name=FrameworkServlet.java, caller_line_number=547, logger_group=main}], start=[{@timestamp=2023-03-22T14:13:59.004+08:00, @version=1, level=INFO, service=order-service, message=This is the 3th log message for completed started on 16 ms., logger_name=o.s.w.s.DispatcherServlet, thread_name=http-nio-0.0.0.0-28001-exec-1, level_value=20000, caller_class_name=org.springframework.web.servlet.FrameworkServlet, caller_method_name=initServletBean, caller_file_name=FrameworkServlet.java, caller_line_number=547, logger_group=main}]}}, labels=null)
        //
        // @formatter:on

        Assert2.isTrue(alerts.size() == 3, "alerts.size(%s) == 3", alerts.size());

        Map body0 = alerts.get(0).getBody();
        Assert2.notEmpty(body0, "body0");
        Map match0 = (Map) body0.get("match");
        Assert2.notEmpty(match0, "match0");
        List middle0 = (List) match0.get("middle");
        Assert2.notEmpty(middle0, "middle0");
        Map middelMap0 = (Map) middle0.get(0);
        Assert2.notEmpty(middelMap0, "middelMap0");
        Assert2.isTrueOf("ERROR".equals(valueOf(middelMap0.get("level"))), "level == 'ERROR'");

        Map body2 = alerts.get(2).getBody();
        Assert2.notEmpty(body2, "body2");
        Map match2 = (Map) body2.get("match");
        Assert2.notEmpty(match2, "match2");
        List middle2 = (List) match2.get("middle");
        Assert2.notEmpty(middle2, "middle2");
        Map middelMap2 = (Map) middle2.get(0);
        Assert2.notEmpty(middelMap2, "middelMap2");
        Assert2.isTrueOf("FATAL".equals(valueOf(middelMap2.get("level"))), "level == 'FATAL'");

    }

    static ExportLogsServiceRequest buildOtlpLogsRequest(String logMsg) {
        return ExportLogsServiceRequest.newBuilder()
                .addResourceLogs(ResourceLogs.newBuilder()
                        .addInstrumentationLibraryLogs(InstrumentationLibraryLogs.newBuilder()
                                .addLogs(LogRecord.newBuilder()
                                        .setBody(AnyValue.newBuilder().setStringValue(logMsg).build())
                                        .build())
                                .build())
                        .build())
                .build();
    }

    // @formatter:off
    static final String PATTERN_ARRAY_JSON_1 = "[{"
            + "    \"name\": \"end\","
            + "    \"engine\": \"FLINK_CEP_GRAPH\","
            + "    \"quantifier\": {"
            + "        \"consumingStrategy\": \"SKIP_TILL_NEXT\","
            + "        \"times\": {"
            + "             \"from\": 1,"
            + "             \"to\": 3,"
            + "             \"windowTime\": {"
            + "                 \"unit\": \"MINUTES\","
            + "                 \"size\": 5"
            + "             }"
            + "         },"
            + "        \"untilCondition\": null,"
            + "        \"properties\": [\"SINGLE\"]"
            + "    },"
            + "    \"condition\": null,"
            + "    \"nodes\": [{"
            + "        \"name\": \"middle\","
            + "        \"quantifier\": {"
            + "            \"consumingStrategy\": \"SKIP_TILL_NEXT\","
            + "        \"times\": {"
            + "             \"from\": 1,"
            + "             \"to\": 3,"
            + "             \"windowTime\": {"
            + "                 \"unit\": \"MINUTES\","
            + "                 \"size\": 5"
            + "             }"
            + "         },"
            + "            \"untilCondition\": null,"
            + "            \"properties\": [\"SINGLE\"]"
            + "        },"
            + "        \"condition\": {"
            + "            \"nestedConditions\": [{"
            + "                \"expression\": \"body.level == 'ERROR'\","
            + "                \"type\": \"AVIATOR\""
            + "            }, {"
            + "                \"expression\": \"body.level == 'FATAL'\","
            + "                \"type\": \"AVIATOR\""
            + "            }],"
            + "            \"type\": \"CLASS\","
            + "            \"className\": \"org.apache.flink.cep.pattern.conditions.RichOrCondition\""
            + "        },"
            + "        \"type\": \"ATOMIC\""
            + "    }, {"
            + "        \"name\": \"start\","
            + "        \"quantifier\": {"
            + "            \"consumingStrategy\": \"SKIP_TILL_NEXT\","
            + "            \"times\": null,"
            + "            \"untilCondition\": null,"
            + "            \"properties\": [\"SINGLE\"]"
            + "        },"
            + "        \"condition\": {"
            + "            \"expression\": \"body.level == 'TRACE' || body.level == 'DEBUG' || body.level == 'INFO' || body.level == 'WARN'\","
            + "            \"type\": \"AVIATOR\""
            + "        },"
            + "        \"type\": \"ATOMIC\""
            + "    }],"
            + "    \"edges\": [{"
            + "        \"source\": \"start\","
            + "        \"target\": \"middle\","
            + "        \"type\": \"SKIP_TILL_NEXT\""
            + "    }],"
            + "    \"window\": {"
            + "        \"type\": \"PREVIOUS_AND_CURRENT\","
            + "        \"time\": {"
            + "            \"unit\": \"MINUTES\","
            + "            \"size\": 5"
            + "        }"
            + "    },"
            + "    \"afterMatchStrategy\": {"
            + "        \"type\": \"NO_SKIP\","
            + "        \"patternName\": null"
            + "    },"
            + "    \"type\": \"COMPOSITE\","
            + "    \"version\": 1"
            + "}]";
    // @formatter:on

    // @formatter:off
    static final String[] DEFAULT_ARGS = {
            "--checkpointDir", "file:///tmp/flink-checkpoint",
            "--groupId", "rengine_test_" + RandomUtils.nextInt(),
            "--eventTopicPattern", IT_MOCK_EVENT_TOPIC,
            "--inProcessingTime", "true",
            "--alertTopic", IT_MOCK_ALERTS_TOPIC,
            //"--fromOffsetTime", "1",
            "--deserializerClass", "com.wl4g.rengine.job.kafka.schema.OtlpLogKafkaDeserializationSchema",
            //"--keyByExprPath", "type", // grouping by logs event type.(default)
            "--keyByExprPath", "body.service", // grouping by logs application name.
            "--cepPatterns", Encodes.encodeBase64(PATTERN_ARRAY_JSON_1)
            };
    // @formatter:on

}
