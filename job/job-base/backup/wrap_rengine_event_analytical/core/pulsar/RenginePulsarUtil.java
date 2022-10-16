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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.job.analytic.core.pulsar;

import java.util.regex.Pattern;

import org.apache.flink.api.connector.source.Source;
import org.apache.flink.api.connector.source.SourceSplit;
import org.apache.flink.connector.pulsar.source.PulsarSource;

import com.wl4g.rengine.job.analytic.core.RengineFlinkStreamingBase;
import com.wl4g.rengine.job.analytic.core.model.RengineEventAnalytical;

/**
 * {@link RenginePulsarUtil}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-06-07 v3.0.0
 * @since v3.0.0
 */
public abstract class RenginePulsarUtil {

    /**
     * Create PULSAR FLINK stream source
     * 
     * @param program
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T, S extends SourceSplit, E> Source<T, S, E> createPulsarSource(RengineFlinkStreamingBase program) {
        // TODO
        PulsarSource<RengineEventAnalytical> source = PulsarSource.<RengineEventAnalytical> builder()
                .setServiceUrl(program.getBrokers())
                // .setGroupId(program.getGroupId())
                .setTopicPattern(Pattern.compile(program.getTopicPattern()))
                // TODO
                // .setStartingOffsets(offsets)
                // .setClientIdPrefix(program.getJobName())
                // .setProperties(program.getProps())
                // .setDeserializer(new RengineKafkaRecordDeserializationSchema())
                .build();
        return (Source<T, S, E>) source;
    }

}
