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
package com.wl4g.rengine.job.pulsar.util;

import java.util.regex.Pattern;

import org.apache.flink.api.connector.source.Source;
import org.apache.flink.api.connector.source.SourceSplit;
import org.apache.flink.connector.pulsar.source.PulsarSource;

import com.wl4g.rengine.job.AbstractFlinkStreamingBase;
import com.wl4g.rengine.common.event.RengineEvent;

/**
 * {@link RenginePulsarUtil}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @date 2022-06-07 v3.0.0
 * @since v1.0.0
 */
public abstract class RenginePulsarUtil {

    /**
     * Create PULSAR FLINK stream source
     * 
     * @param program
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T, S extends SourceSplit, E> Source<T, S, E> createPulsarSource(AbstractFlinkStreamingBase program) {
        // TODO
        PulsarSource<RengineEvent> source = PulsarSource.<RengineEvent> builder()
                .setServiceUrl(program.getBrokers())
                // .setGroupId(program.getGroupId())
                .setTopicPattern(Pattern.compile(program.getEventTopic()))
                // TODO
                // .setStartingOffsets(offsets)
                // .setClientIdPrefix(program.getJobName())
                // .setProperties(program.getProps())
                // .setDeserializer(new AbstractDeserializationSchema())
                .build();
        return (Source<T, S, E>) source;
    }

}
