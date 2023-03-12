/*
 * Copyright 2017 ~ 2025 the original authors James Wong.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.job.kafka;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseMapObject;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.wl4g.infra.common.lang.DateUtils2;
import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.common.event.RengineEvent.EventLocation;
import com.wl4g.rengine.common.event.RengineEvent.EventSource;
import com.wl4g.rengine.job.AbstractFlinkStreamingBase;

import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest;
import io.opentelemetry.proto.logs.v1.InstrumentationLibraryLogs;
import io.opentelemetry.proto.logs.v1.LogRecord;
import io.opentelemetry.proto.logs.v1.ResourceLogs;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link OtlpLogKafkaDeserializationSchema}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-06-03 v3.0.0
 * @since v1.0.0
 */
@Slf4j
@Getter
public class OtlpLogKafkaDeserializationSchema implements KafkaRecordDeserializationSchema<RengineEvent> {
    private static final long serialVersionUID = -3765473065594331694L;

    private @NotBlank final String eventType;
    private @NotEmpty final List<String> timeFieldNames;
    private @NotEmpty final List<String> principalFieldNames;

    public OtlpLogKafkaDeserializationSchema(@NotNull AbstractFlinkStreamingBase streaming) {
        // TODO using config?
        this(streaming.getJobName().concat("_OTLP_LOG"), DEFAULT_TIME_FIELD_NAMES, DEFAULT_PRINCIPAL_FIELD_NAMES);
    }

    public OtlpLogKafkaDeserializationSchema(@NotBlank String eventType, @NotEmpty List<String> timeFieldNames,
            List<String> principalFieldNames) {
        this.eventType = hasTextOf(eventType, "eventType");
        this.timeFieldNames = notEmptyOf(timeFieldNames, "timeFieldNames");
        this.principalFieldNames = notEmptyOf(principalFieldNames, "principalFieldNames");
    }

    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> record, Collector<RengineEvent> out) throws IOException {
        if (nonNull(record.value())) {
            try {
                final ExportLogsServiceRequest export = ExportLogsServiceRequest.parseFrom(record.value());
                for (ResourceLogs rlogs : safeList(export.getResourceLogsList())) {
                    // logs.writeDelimitedTo(System.out);
                    for (InstrumentationLibraryLogs ilogs : safeList(rlogs.getInstrumentationLibraryLogsList())) {
                        for (LogRecord logRecord : ilogs.getLogsList()) {
                            log.debug("Otlp logRecord: {}", logRecord);

                            // event body.
                            final Map<String, Object> body = parseMapObject(logRecord.getBody().getStringValue());

                            // event labels.
                            final Map<String, String> labels = safeList(logRecord.getAttributesList()).stream()
                                    .collect(toMap(kv -> kv.getKey(), kv -> kv.getValue().getStringValue()));

                            // event timestamp.
                            final Long timestamp = safeMap(body).entrySet()
                                    .stream()
                                    .filter(e -> timeFieldNames.contains(e.getKey()))
                                    .map(e -> {
                                        Object timeObj = e.getValue();
                                        if (timeObj instanceof String) {
                                            try {
                                                return DateUtils2.parseDate((String) timeObj, DEFAULT_TIME_PATTERNS).getTime();
                                            } catch (ParseException ex) {
                                                throw new IllegalArgumentException(
                                                        format("Unable to parse log record timestamp for %s", timeObj), ex);
                                            }
                                        } else if (timeObj instanceof Number) {
                                            return ((Number) timeObj).longValue();
                                        } else if (timeObj instanceof Date) {
                                            return ((Date) timeObj).getTime();
                                        } else if (timeObj instanceof Instant) {
                                            return ((Instant) timeObj).toEpochMilli();
                                        }
                                        return null;
                                    })
                                    .filter(t -> nonNull(t))
                                    .findFirst()
                                    .orElse(null);

                            // event principals.
                            final List<String> principals = safeMap(body).entrySet()
                                    .stream()
                                    .filter(e -> principalFieldNames.contains(e.getKey()))
                                    .map(e -> {
                                        final Object principalObj = e.getValue();
                                        return nonNull(principalObj) ? principalObj.toString() : null;
                                    })
                                    .collect(toList());

                            out.collect(new RengineEvent(eventType,
                                    EventSource.builder()
                                            .time(timestamp)
                                            .principals(principals)
                                            .location(EventLocation.builder().build())
                                            .build(),
                                    body, labels));
                        }
                    }
                }
            } catch (Throwable ex) {
                log.warn(format("Unable to parse OTLP logs event."), ex);
            }
        }
    }

    @Override
    public TypeInformation<RengineEvent> getProducedType() {
        return TypeInformation.of(RengineEvent.class);
    }

    public static final String[] DEFAULT_TIME_PATTERNS = { "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy/MM/dd HH:mm:ss.SSS", "dd/MM/yyyy HH:mm:ss.SSS", "dd/MM/yyyy HH:mm:ss.SSS", "yyyyMMddHHmmssSSS" };
    public static final List<String> DEFAULT_TIME_FIELD_NAMES = asList("timestamp", "@timestamp", "time", "@time");
    public static final List<String> DEFAULT_PRINCIPAL_FIELD_NAMES = asList("principal", "user", "username");

}
