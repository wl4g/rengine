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
package com.wl4g.rengine.job;

import java.util.Map;

import org.apache.commons.cli.ParseException;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.api.connector.source.SourceSplit;
import org.apache.flink.connector.kafka.source.KafkaSourceOptions;

import com.wl4g.rengine.job.cep.AbstractFlinkCepStreamingBase;
import com.wl4g.rengine.job.hbase.HBaseFlinkStreamingSupport;
import com.wl4g.rengine.job.kafka.RengineKafkaUtil;

import lombok.Getter;

/**
 * {@link AbstractFlinkCepStreamingBase}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-05-31 v3.0.0
 * @since v1.0.0
 * @see https://stackoverflow.com/questions/69765972/migrating-from-flinkkafkaconsumer-to-kafkasource-no-windows-executed
 */
@Getter
public class RengineKafka2HBaseStreaming extends HBaseFlinkStreamingSupport {

    private String partitionDiscoveryIntervalMs;

    public static void main(String[] args) throws Exception {
        new RengineKafka2HBaseStreaming().parse(args).run();
    }

    public RengineKafka2HBaseStreaming() {
        super();
        builder.longOption("partitionDiscoveryIntervalMs", "30000", "The per millis for discover new partitions interval.");
    }

    @Override
    protected AbstractFlinkStreamingBase parse(String[] args) throws ParseException {
        super.parse(args);
        this.partitionDiscoveryIntervalMs = line.get("partitionDiscoveryIntervalMs");
        return this;
    }

    @Override
    protected void customProps(Map<String, String> props) {
        super.customProps(props);

        // props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,brokers);
        // props.setProperty(ConsumerConfig.GROUP_ID_CONFIG,groupId);
        // see:https://github.com/apache/flink/blob/release-1.14.4/docs/content/docs/connectors/datastream/kafka.md#consumer-offset-committing
        // props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"false");
        // props.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"10");
        // props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        // props.setProperty(FlinkKafkaConsumerBase.KEY_DISABLE_METRICS,"true");
        // see:https://github.com/apache/flink/blob/release-1.14.4/docs/content/docs/connectors/datastream/kafka.md#dynamic-partition-discovery
        props.put(KafkaSourceOptions.PARTITION_DISCOVERY_INTERVAL_MS.key(), partitionDiscoveryIntervalMs);
    }

    @Override
    protected <T, S extends SourceSplit, E> Source<T, S, E> createSource() {
        return RengineKafkaUtil.createKafkaSource(this);
    }

}
