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
package com.wl4g.rengine.job.kafka.schema;

import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.IOException;

import javax.validation.constraints.NotNull;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.job.AbstractFlinkStreamingBase;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link RengineEventKafkaDeserializationSchema}
 * 
 * @author James Wong
 * @date 2022-06-03
 * @since v1.0.0
 */
@Slf4j
@Getter
public class RengineEventKafkaDeserializationSchema implements KafkaRecordDeserializationSchema<RengineEvent> {
    private static final long serialVersionUID = -3765473065594331694L;

    private static transient Deserializer<String> deserializer;

    public RengineEventKafkaDeserializationSchema(@NotNull AbstractFlinkStreamingBase streaming) {
    }

    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> record, Collector<RengineEvent> out) throws IOException {
        if (isNull(deserializer)) {
            deserializer = new StringDeserializer();
        }
        if (nonNull(record.value())) {
            final String json = deserializer.deserialize(record.topic(), record.value());
            try {
                out.collect(parseJSON(json, RengineEvent.class).validate());
            } catch (Throwable ex) {
                final String errmsg = format("Unable to parse event json. - %s", json);
                log.warn(errmsg, ex);
                throw new IllegalArgumentException(errmsg, ex);
            }
        }
    }

    @Override
    public TypeInformation<RengineEvent> getProducedType() {
        return TypeInformation.of(RengineEvent.class);
    }

}
