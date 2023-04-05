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
package com.wl4g.rengine.job.kafka.util;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;

import java.util.Properties;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

import org.apache.flink.api.connector.sink.Sink;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.api.connector.source.SourceSplit;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import com.wl4g.infra.common.lang.ClassUtils2;
import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.job.AbstractFlinkStreamingBase;
import com.wl4g.rengine.job.kafka.RengineKafkaFlinkCepStreaming;
import com.wl4g.rengine.job.kafka.schema.RengineEventKafkaSerializationSchema;
import com.wl4g.rengine.job.pulsar.util.RenginePulsarUtil;

/**
 * {@link RenginePulsarUtil}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-06-07 v3.0.0
 * @since v1.0.0
 */
@SuppressWarnings("deprecation")
public abstract class RengineKafkaUtil {

    /**
     * Create KAFKA FLINK stream source
     * 
     * @param streaming
     * @return
     */
    public static <T, S extends SourceSplit, E> Source<T, S, E> createKafkaSource(AbstractFlinkStreamingBase streaming) {
        return createKafkaSource(((RengineKafkaFlinkCepStreaming) streaming).getOffsetResetStrategy(), streaming);
    }

    /**
     * Create KAFKA FLINK stream source
     * 
     * @param streaming
     * @return
     */
    @SuppressWarnings({ "unchecked" })
    public static <T, S extends SourceSplit, E> Source<T, S, E> createKafkaSource(
            @NotNull OffsetResetStrategy offsetResetStrategy,
            @NotNull AbstractFlinkStreamingBase streaming) {
        notNullOf(offsetResetStrategy, "offsetResetStrategy");
        notNullOf(streaming, "streaming");

        // see:https://github.com/apache/flink/blob/release-1.14.4/docs/content/docs/connectors/datastream/kafka.md#starting-offset
        // Start from committed offset, also use EARLIEST as reset strategy if
        // committed offset doesn't exist
        OffsetsInitializer offsets = OffsetsInitializer.committedOffsets(offsetResetStrategy);
        if (streaming.getFromOffsetTime() > 0) { // By-default
            // Start from the first record whose timestamp is greater than
            // or equals a timestamp.
            offsets = OffsetsInitializer.timestamp(streaming.getFromOffsetTime());
        }

        KafkaRecordDeserializationSchema<RengineEvent> deserializer = null;
        try {
            final Class<?> deserialzerClass = ClassUtils2.forName(streaming.getDeserializerClass(), null);
            deserializer = (KafkaRecordDeserializationSchema<RengineEvent>) deserialzerClass
                    .getConstructor(AbstractFlinkStreamingBase.class)
                    .newInstance(streaming);
        } catch (Throwable ex) {
            throw new IllegalStateException(
                    format("Could't to load deserializer class for '%s'", streaming.getDeserializerClass()), ex);
        }

        final Properties _props = new Properties();
        safeMap(streaming.getProps()).forEach((key, value) -> _props.put(key, value));
        return (Source<T, S, E>) KafkaSource.<RengineEvent> builder()
                .setBootstrapServers(streaming.getBrokers())
                .setGroupId(streaming.getGroupId())
                .setTopicPattern(Pattern.compile(streaming.getEventTopicPattern()))
                .setStartingOffsets(offsets)
                .setClientIdPrefix(streaming.getJobName())
                .setProperties(_props)
                .setDeserializer(deserializer)
                .build();
    }

    public static <I, C, W, G> Sink<I, C, W, G> createKafkaSink(@NotNull AbstractFlinkStreamingBase streaming) {
        // TODO add producer config item.
        return createKafkaSink(new Properties() {
            private static final long serialVersionUID = 9134914495692091519L;
            {
                put("ack", "1");
            }
        }, streaming);
    }

    @SuppressWarnings("unchecked")
    public static <I, C, W, G> Sink<I, C, W, G> createKafkaSink(
            @NotNull Properties props,
            @NotNull AbstractFlinkStreamingBase streaming) {
        notNullOf(props, "props");
        notNullOf(streaming, "streaming");
        return (Sink<I, C, W, G>) KafkaSink.<RengineEvent> builder()
                .setBootstrapServers(streaming.getBrokers())
                .setRecordSerializer(new RengineEventKafkaSerializationSchema(streaming))
                .setKafkaProducerConfig(props)
                .build();
    }

}
