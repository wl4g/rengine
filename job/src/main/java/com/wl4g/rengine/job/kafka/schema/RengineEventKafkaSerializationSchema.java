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

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isInstanceOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.util.Objects.nonNull;

import javax.validation.constraints.NotNull;

import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.job.AbstractFlinkCepStreamingBase;
import com.wl4g.rengine.job.AbstractFlinkStreamingBase;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link RengineEventKafkaSerializationSchema}
 * 
 * @author James Wong
 * @version 2023-03-16
 * @since v1.0.0
 */
@Slf4j
@Getter
public class RengineEventKafkaSerializationSchema implements KafkaRecordSerializationSchema<RengineEvent> {
    private static final long serialVersionUID = -3765473115594331694L;

    private final String alertTopic;

    public RengineEventKafkaSerializationSchema(@NotNull AbstractFlinkStreamingBase streaming) {
        notNullOf(streaming, "streaming");
        isInstanceOf(AbstractFlinkCepStreamingBase.class, streaming);
        final AbstractFlinkCepStreamingBase cepStreaming = (AbstractFlinkCepStreamingBase) streaming;
        this.alertTopic = hasTextOf(cepStreaming.getAlertTopic(), "alertTopic");
    }

    @Override
    public ProducerRecord<byte[], byte[]> serialize(RengineEvent event, KafkaSinkContext context, Long timestamp) {
        if (nonNull(event)) {
            try {
                return new ProducerRecord<>(alertTopic, toJSONString(event).getBytes(UTF_8));
            } catch (Throwable ex) {
                log.warn(format("Unable to serialze event. -> %s", event), ex);
            }
        }
        return null;
    }

}
