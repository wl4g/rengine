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
package com.wl4g.rengine.job.cep.example;

import static java.lang.String.format;

import java.util.Date;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.wl4g.infra.common.codec.Encodes;
import com.wl4g.infra.common.lang.DateUtils2;
import com.wl4g.infra.common.lang.ThreadUtils2;
import com.wl4g.rengine.job.cep.RengineKafkaFlinkCepStreaming;

import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.logs.v1.InstrumentationLibraryLogs;
import io.opentelemetry.proto.logs.v1.LogRecord;
import io.opentelemetry.proto.logs.v1.ResourceLogs;

/**
 * {@link RengineKafkaFlinkCepStreamingTests}
 * 
 * @author James Wong
 * @version 2023-03-12
 * @since v1.0.0
 */
public class RengineKafkaFlinkCepStreamingTests {

    public static void main(String[] args) throws Exception {
        // LogManager.getLogger("org.apache.flink").setLevel(Level.DEBUG);
        startMockLogsGenerating();
        RengineKafkaFlinkCepStreaming.main(DEFAULT_ARGS);
    }

    static void startMockLogsGenerating() {
        new Thread(() -> {
            final Properties props = new Properties();
            props.put("bootstrap.servers", "localhost:9092");
            props.put("acks", "0");
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

            try (KafkaProducer<String, byte[]> producer = new KafkaProducer<>(props);) {
                for (int i = 0; i < 1000; i++) {

                    String logMessage = format(LOG_MESSAGE, DateUtils2.formatDate(new Date(), "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
                    System.out.println("Send : " + logMessage);

                    ExportLogsServiceRequest request = ExportLogsServiceRequest.newBuilder()
                            .addResourceLogs(ResourceLogs.newBuilder()
                                    .addInstrumentationLibraryLogs(InstrumentationLibraryLogs.newBuilder()
                                            .addLogs(LogRecord.newBuilder()
                                                    .setBody(AnyValue.newBuilder().setStringValue(logMessage).build())
                                                    .build())
                                            .build())
                                    .build())
                            .build();
                    producer.send(new ProducerRecord<String, byte[]>("wl4g_otlp_logs", request.toByteArray()));

                    ThreadUtils2.sleepRandom(2000, 5000);
                }
            }
        }).start();
    }

    // @formatter:off
    static final String DEFAULT_CEP_PATTERN_JSON = "{"
            + "   \"name\": \"end\","
            + "   \"quantifier\": {"
            + "       \"consumingStrategy\": \"SKIP_TILL_NEXT\","
            + "       \"properties\": [\"SINGLE\"],"
            + "       \"times\": null,"
            + "       \"untilCondition\": null"
            + "   },"
            + "   \"condition\": null,"
            + "   \"nodes\": [{"
            + "       \"name\": \"end\","
            + "       \"quantifier\": {"
            + "           \"consumingStrategy\": \"SKIP_TILL_NEXT\","
            + "           \"properties\": [\"SINGLE\"],"
            + "           \"times\": null,"
            + "           \"untilCondition\": null"
            + "       },"
            + "       \"condition\": {"
            + "           \"expression\": \"type == login_success\","
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
            + "               \"expression\": \"type == login_tail\","
            + "               \"type\": \"AVIATOR\""
            + "           }],"
            + "           \"type\": \"CLASS\","
            + "           \"className\": \"org.apache.flink.cep.pattern.conditions.RichAndCondition\""
            + "       },"
            + "       \"type\": \"ATOMIC\""
            + "   }, {"
            + "       \"name\": \"start\","
            + "       \"quantifier\": {"
            + "           \"consumingStrategy\": \"SKIP_TILL_NEXT\","
            + "           \"properties\": [\"SINGLE\"],"
            + "           \"times\": null,"
            + "           \"untilCondition\": null"
            + "       },"
            + "       \"condition\": {"
            + "           \"expression\": \"type == login_tail\","
            + "           \"type\": \"AVIATOR\""
            + "       },"
            + "       \"type\": \"ATOMIC\""
            + "   }],"
            + "   \"edges\": [{"
            + "       \"source\": \"middle\","
            + "       \"target\": \"end\","
            + "       \"type\": \"SKIP_TILL_ANY\""
            + "   }, {"
            + "       \"source\": \"start\","
            + "       \"target\": \"middle\","
            + "       \"type\": \"SKIP_TILL_ANY\""
            + "   }],"
            + "   \"window\": null,"
            + "   \"afterMatchStrategy\": {"
            + "       \"type\": \"NO_SKIP\","
            + "       \"patternName\": null"
            + "   },"
            + "   \"type\": \"COMPOSITE\","
            + "   \"version\": 1"
            + "}";

    static final String[] DEFAULT_ARGS = {
            "-G", "rengine_test",
            "-T", "wl4g_otlp_logs",
            //"-O", "1",
            "-D", "com.wl4g.rengine.job.kafka.OtlpLogKafkaDeserializationSchema",
            "-P", Encodes.encodeBase64(DEFAULT_CEP_PATTERN_JSON)
            };

    static final String LOG_MESSAGE = "{\"@timestamp\":\"%s\",\"@version\":\"1\",\"message\":[\"Completed initialization in 16 ms\"],\"logger_name\":\"o.s.w.s.DispatcherServlet\",\"thread_name\":\"http-nio-0.0.0.0-28001-exec-1\",\"level\":\"INFO\",\"level_value\":20000,\"caller_class_name\":\"org.springframework.web.servlet.FrameworkServlet\",\"caller_method_name\":\"initServletBean\",\"caller_file_name\":\"FrameworkServlet.java\",\"caller_line_number\":547,\"service\":\"rengine-apiserver\",\"logger_group\":\"main\"}";
    // @formatter:on

}
