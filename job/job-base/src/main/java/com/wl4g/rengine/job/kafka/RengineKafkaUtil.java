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

import java.util.regex.Pattern;

import org.apache.flink.api.connector.source.Source;
import org.apache.flink.api.connector.source.SourceSplit;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import com.wl4g.rengine.job.AbstractFlinkStreamingBase;
import com.wl4g.rengine.common.event.RengineEvent;

/**
 * {@link RenginePulsarUtil}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-06-07 v3.0.0
 * @since v1.0.0
 */
public abstract class RengineKafkaUtil {

    /**
     * Create KAFKA FLINK stream source
     * 
     * @param streaming
     * @return
     */
    public static <T, S extends SourceSplit, E> Source<T, S, E> createKafkaSource(AbstractFlinkStreamingBase streaming) {
        return createKafkaSource(OffsetResetStrategy.LATEST, streaming);
    }

    /**
     * Create KAFKA FLINK stream source
     * 
     * @param streaming
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T, S extends SourceSplit, E> Source<T, S, E> createKafkaSource(
            OffsetResetStrategy defaultOffsetResetStrategy,
            AbstractFlinkStreamingBase streaming) {
        // see:https://github.com/apache/flink/blob/release-1.14.4/docs/content/docs/connectors/datastream/kafka.md#starting-offset
        // Start from committed offset, also use EARLIEST as reset strategy if
        // committed offset doesn't exist
        OffsetsInitializer offsets = OffsetsInitializer.committedOffsets(defaultOffsetResetStrategy);
        if (streaming.getFromOffsetTime() > 0) { // By-default
            // Start from the first record whose timestamp is greater than
            // or equals a timestamp.
            offsets = OffsetsInitializer.timestamp(streaming.getFromOffsetTime());
        }
        KafkaSource<RengineEvent> source = KafkaSource.<RengineEvent> builder()
                .setBootstrapServers(streaming.getBrokers())
                .setGroupId(streaming.getGroupId())
                .setTopicPattern(Pattern.compile(streaming.getTopicPattern()))
                .setStartingOffsets(offsets)
                .setClientIdPrefix(streaming.getJobName())
                .setProperties(streaming.getProps())
                .setDeserializer(new RengineKafkaRecordDeserializationSchema())
                .build();
        return (Source<T, S, E>) source;
    }

}
