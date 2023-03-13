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

import static com.wl4g.infra.common.lang.DateUtils2.formatDate;
import static com.wl4g.infra.common.lang.EnvironmentUtil.getProperty;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static java.lang.String.format;
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
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.wl4g.infra.common.codec.Encodes;
import com.wl4g.infra.common.lang.ThreadUtils2;
import com.wl4g.rengine.common.event.RengineEvent;

import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.logs.v1.InstrumentationLibraryLogs;
import io.opentelemetry.proto.logs.v1.LogRecord;
import io.opentelemetry.proto.logs.v1.ResourceLogs;

/**
 * Refer to {@link com.wl4g.rengine.job.cep.RengineKafkaFlinkCepStreamingTests}
 * 
 * @author James Wong
 * @version 2023-03-12
 * @since v1.0.0
 */
public class RengineKafkaFlinkCepStreamingIT {

    static final String IT_MOCK_KAFKA_BROKERS = getProperty("IT_KAFKA_BROKERS", "localhost:9092");
    static final String IT_MOCK_LOGS_SERVICE_NAME = getProperty("IT_MOCK_LOGS_SERVICE_NAME", "order-service");
    static final String IT_MOCK_EVENT_TOPIC = getProperty("IT_MOCK_EVENT_TOPIC", "test_wl4g_rengine_otlp_logs");
    static final String IT_MOCK_ALERTS_TOPIC = getProperty("IT_MOCK_ALERTS_TOPIC", "test_wl4g_rengine_alert");
    static final Map<Integer, String> MOCK_LOGS_LEVELS = new LinkedHashMap<Integer, String>() {
        private static final long serialVersionUID = 1L;
        {
            put(0, "INFO");
            put(1, "INFO");
            put(2, "ERROR");
            put(3, "INFO");
            put(4, "ERROR");
            put(5, "WARN");
            put(6, "INFO");
            put(7, "INFO");
            put(8, "FATAL");
            put(9, "INFO");
        }
    };

    static final AtomicBoolean running = new AtomicBoolean(true);
    static final List<RengineEvent> alerts = synchronizedList(new ArrayList<>());
    static KafkaProducer<String, byte[]> producer;
    static KafkaConsumer<String, String> consumer;

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
        startAlertsConsuming();

        RengineKafkaFlinkCepStreaming.main((nonNull(args) && args.length > 0) ? args : DEFAULT_ARGS);

        assertion();
    }

    static void startMockLogsProducing() {
        new Thread(() -> {
            System.out.println("Mock logs producing for waiting init sleep 6s ...");
            ThreadUtils2.sleep(6_000);

            final Properties props = new Properties();
            props.put("bootstrap.servers", IT_MOCK_KAFKA_BROKERS);
            props.put("acks", "0");
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
            producer = new KafkaProducer<>(props);
            try {
                final AtomicInteger count = new AtomicInteger(0);
                MOCK_LOGS_LEVELS.entrySet().forEach(e -> {
                    String level = e.getValue();
                    final String logTime = formatDate(new Date(), "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                    final String logMsg = format(LOG_MESSAGE, logTime, level, IT_MOCK_LOGS_SERVICE_NAME, count.getAndIncrement());
                    System.out.println("Send log : " + logMsg);

                    ExportLogsServiceRequest request = ExportLogsServiceRequest.newBuilder()
                            .addResourceLogs(ResourceLogs.newBuilder()
                                    .addInstrumentationLibraryLogs(InstrumentationLibraryLogs.newBuilder()
                                            .addLogs(LogRecord.newBuilder()
                                                    .setBody(AnyValue.newBuilder().setStringValue(logMsg).build())
                                                    .build())
                                            .build())
                                    .build())
                            .build();
                    producer.send(new ProducerRecord<String, byte[]>(IT_MOCK_EVENT_TOPIC, request.toByteArray()));

                    ThreadUtils2.sleepRandom(1000, 3000);
                });
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    static void startAlertsConsuming() {
        new Thread(() -> {
            final Properties props = new Properties();
            props.put("bootstrap.servers", IT_MOCK_KAFKA_BROKERS);
            props.put("group.id", RengineKafkaFlinkCepStreamingIT.class.getSimpleName() + "_alerts");
            props.put("enable.auto.commit", "true");
            props.put("auto.offset.reset", "latest");
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
            consumer = new KafkaConsumer<>(props);
            consumer.subscribe(singleton(IT_MOCK_ALERTS_TOPIC));
            while (true) {
                try {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                    if (!records.isEmpty()) {
                        for (ConsumerRecord<String, String> record : records) {
                            final RengineEvent alert = parseJSON(record.value(), RengineEvent.class);
                            alerts.add(alert);
                            System.out.println("Alert : " + alert);
                        }
                    }
                    ThreadUtils2.sleep(1000);

                    if (!running.get()) {
                        break;
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    static void assertion() {
        System.out.println("---------- alerts ----------");
        System.out.println(alerts);
        // TODO

    }

    // @formatter:off
    static final String DEFAULT_CEP_PATTERNS_JSON_1 = "[{"
            + "   \"name\": \"logerror\","
            + "   \"quantifier\": {"
            + "       \"consumingStrategy\": \"SKIP_TILL_NEXT\","
            + "       \"properties\": [\"SINGLE\"],"
            + "       \"times\": null,"
            + "       \"untilCondition\": null"
            + "   },"
            + "   \"condition\": null,"
            + "   \"nodes\": [{"
            + "       \"name\": \"error\","
            + "       \"quantifier\": {"
            + "           \"consumingStrategy\": \"SKIP_TILL_NEXT\","
            + "           \"properties\": [\"SINGLE\"],"
            + "           \"times\": null,"
            + "           \"untilCondition\": null"
            + "       },"
            + "       \"condition\": {"
            + "           \"expression\": \"body.level == 'ERROR' || body.level == 'FATAL'\","
            + "           \"type\": \"AVIATOR\""
            + "       },"
            + "       \"type\": \"ATOMIC\""
            + "   }],"
            + "   \"edges\": [],"
            + "   \"window\": null,"
            + "   \"afterMatchStrategy\": {"
            + "       \"type\": \"NO_SKIP\","
            + "       \"patternName\": null"
            + "   },"
            + "   \"type\": \"COMPOSITE\","
            + "   \"version\": 1"
            + "}]";
    // @formatter:on

    // @formatter:off
    static final String DEFAULT_CEP_PATTERNS_JSON_2 = "[{"
            + "   \"name\": \"logerror\","
            + "   \"quantifier\": {"
            + "       \"consumingStrategy\": \"SKIP_TILL_NEXT\","
            + "       \"properties\": [\"SINGLE\"],"
            + "       \"times\": null,"
            + "       \"untilCondition\": null"
            + "   },"
            + "   \"condition\": null,"
            + "   \"nodes\": [{"
            + "       \"name\": \"start\","
            + "       \"quantifier\": {"
            + "           \"consumingStrategy\": \"SKIP_TILL_NEXT\","
            + "           \"properties\": [\"SINGLE\"],"
            + "           \"times\": null,"
            + "           \"untilCondition\": null"
            + "       },"
            + "       \"condition\": {"
            + "           \"expression\": \"body.level == 'TRACE' || body.level == 'DEBUG' || body.level == 'INFO' || body.level == 'WARN'\","
            + "           \"type\": \"AVIATOR\""
            + "       },"
            + "       \"type\": \"ATOMIC\""
            + "   }, {"
            + "       \"name\": \"middle\","
            + "       \"quantifier\": {"
            + "           \"consumingStrategy\": \"SKIP_TILL_NEXT\","
            + "           \"properties\": [\"SINGLE\"],"
            + "           \"times\": null,"
            + "           \"untilCondition\": null"
            + "       },"
            + "       \"condition\": {"
            + "           \"nestedConditions\": [{"
            + "               \"className\": \"org.apache.flink.cep.pattern.conditions.SubtypeCondition\","
            + "               \"subClassName\": \"com.wl4g.rengine.common.event.RengineEvent\","
            + "               \"type\": \"CLASS\""
            + "           }, {"
            + "               \"expression\": \"body.level == 'ERROR' || body.level == 'FATAL'\","
            + "               \"type\": \"AVIATOR\""
            + "           }],"
            + "           \"type\": \"CLASS\","
            + "           \"className\": \"org.apache.flink.cep.pattern.conditions.RichAndCondition\""
            + "       },"
            + "       \"type\": \"ATOMIC\""
            + "   }, {"
            + "       \"name\": \"end\","
            + "       \"quantifier\": {"
            + "           \"consumingStrategy\": \"SKIP_TILL_NEXT\","
            + "           \"properties\": [\"SINGLE\"],"
            + "           \"times\": null,"
            + "           \"untilCondition\": null"
            + "       },"
            + "       \"condition\": {"
            + "           \"expression\": \"body.level == 'TRACE' || body.level == 'DEBUG' || body.level == 'INFO' || body.level == 'WARN'\","
            + "           \"type\": \"AVIATOR\""
            + "       },"
            + "       \"type\": \"ATOMIC\""
            + "   }],"
            + "   \"edges\": [{"
            + "       \"source\": \"start\","
            + "       \"target\": \"middle\","
            + "       \"type\": \"SKIP_TILL_ANY\""
            + "   }, {"
            + "       \"source\": \"middle\","
            + "       \"target\": \"end\","
            + "       \"type\": \"SKIP_TILL_ANY\""
            + "   }],"
            + "   \"window\": null,"
            + "   \"afterMatchStrategy\": {"
            + "       \"type\": \"NO_SKIP\","
            + "       \"patternName\": null"
            + "   },"
            + "   \"type\": \"COMPOSITE\","
            + "   \"version\": 1"
            + "}]";
    // @formatter:on

    // @formatter:off
    static final String[] DEFAULT_ARGS = {
            "--groupId", "rengine_test",
            "--eventTopicPattern", IT_MOCK_EVENT_TOPIC,
            "--alertTopic", IT_MOCK_ALERTS_TOPIC,
            //"--fromOffsetTime", "1",
            "--deserializerClass", "com.wl4g.rengine.job.kafka.OtlpLogKafkaDeserializationSchema",
            //"--keyByExpression", "type", // grouping by logs event type.(default)
            "--keyByExpression", "body.service", // grouping by logs application name.
            "--cepPatterns", Encodes.encodeBase64(DEFAULT_CEP_PATTERNS_JSON_1)
            };
    // @formatter:on

    // @formatter:off
    static final String LOG_MESSAGE = "{\"@timestamp\":\"%s\",\"level\":\"%s\",\"service\":\"%s\",\"message\":[\"This is the %sth log message for completed started on 16 ms.\"],\"@version\":\"1\",\"logger_name\":\"o.s.w.s.DispatcherServlet\",\"thread_name\":\"http-nio-0.0.0.0-28001-exec-1\",\"level_value\":20000,\"caller_class_name\":\"org.springframework.web.servlet.FrameworkServlet\",\"caller_method_name\":\"initServletBean\",\"caller_file_name\":\"FrameworkServlet.java\",\"caller_line_number\":547,\"logger_group\":\"main\"}";
    // @formatter:on

}
