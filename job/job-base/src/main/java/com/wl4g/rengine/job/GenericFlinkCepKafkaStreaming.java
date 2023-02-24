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

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.ParseException;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.api.connector.source.SourceSplit;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternFlatSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.connector.kafka.source.KafkaSourceOptions;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import com.wl4g.rengine.common.event.RengineEvent.EventSource;
import com.wl4g.rengine.job.kafka.RengineKafkaUtil;
import com.wl4g.rengine.job.model.RengineEventWrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link GenericFlinkCepKafkaStreaming}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-05-31 v3.0.0
 * @since v1.0.0
 * @see https://stackoverflow.com/questions/69765972/migrating-from-flinkkafkaconsumer-to-kafkasource-no-windows-executed
 */
@Getter
public class GenericFlinkCepKafkaStreaming extends AbstractFlinkStreamingBase {

    private String partitionDiscoveryIntervalMs;

    public static void main(String[] args) throws Exception {
        new GenericFlinkCepKafkaStreaming().parse(args).run();
    }

    public GenericFlinkCepKafkaStreaming() {
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
    protected void customProps(Properties props) {
        super.customProps(props);
        // props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,brokers);
        // props.setProperty(ConsumerConfig.GROUP_ID_CONFIG,groupId);
        // see:https://github.com/apache/flink/blob/release-1.14.4/docs/content/docs/connectors/datastream/kafka.md#consumer-offset-committing
        // props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"false");
        // props.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"10");
        // props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        // props.setProperty(FlinkKafkaConsumerBase.KEY_DISABLE_METRICS,"true");
        // see:https://github.com/apache/flink/blob/release-1.14.4/docs/content/docs/connectors/datastream/kafka.md#dynamic-partition-discovery
        props.setProperty(KafkaSourceOptions.PARTITION_DISCOVERY_INTERVAL_MS.key(), partitionDiscoveryIntervalMs);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected AbstractFlinkStreamingBase customStream(DataStreamSource<RengineEventWrapper> dataStreamSource) {

        KeyedStream<RengineEventWrapper, String> keyedStreamSource = dataStreamSource.map(event -> {
            // TODO
            return event;
        }).assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<RengineEventWrapper>(Time.seconds(5)) {
            private static final long serialVersionUID = 1L;

            @Override
            public long extractTimestamp(RengineEventWrapper event) {
                return event.getObservedTime(); // event-time
            }
        }).keyBy(new KeySelector<RengineEventWrapper, String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getKey(RengineEventWrapper event) throws Exception {
                // TODO
                return ((EventSource) event.getSource()).getPrincipals().get(0);
            }
        });

        Pattern<RengineEventWrapper, RengineEventWrapper> cepPattern = Pattern.begin("first");
        cepPattern = cepPattern.where(new IterativeCondition<RengineEventWrapper>() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean filter(RengineEventWrapper event, Context<RengineEventWrapper> ctx) throws Exception {
                // TODO
                return event.getType().equalsIgnoreCase("fail");
            }
        }).followedBy("next1").where(new IterativeCondition<RengineEventWrapper>() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean filter(RengineEventWrapper event, Context<RengineEventWrapper> ctx) throws Exception {
                // TODO
                return event.getType().equalsIgnoreCase("fail");
            }
            // n秒内连续失败的次数在 from 与 to 之间，则标记为匹配
        }).within(Time.milliseconds(1001)).oneOrMore()/* .times(1, 10) */;

        // 3. 在事件流上应用模式，得到一个pattern stream
        PatternStream<RengineEventWrapper> patternStream = CEP.pattern(keyedStreamSource, cepPattern);

        // 4. 从pattern stream上应用select function，检出匹配事件序列
        //@formatter:off
//        SingleOutputStreamOperator<Warning> loginFailDataStream = patternStream.select(new LoginFailMatch());
        //@formatter:on
        SingleOutputStreamOperator<WarningEvent> loginFailDataStream = patternStream.flatSelect(new LoginFailMatch());

        loginFailDataStream.print();

        return this;
    }

    @Override
    protected <T, S extends SourceSplit, E> Source<T, S, E> createSource() {
        return RengineKafkaUtil.createKafkaSource(this);
    }

    public static class LoginFailMatch
            implements /* PatternSelectFunction , */ PatternFlatSelectFunction<RengineEventWrapper, WarningEvent> {
        private static final long serialVersionUID = 1L;

        @Override
        public void flatSelect(Map<String, List<RengineEventWrapper>> pattern, Collector<WarningEvent> out) throws Exception {
            // 从map中按照名称取出对应的事件
            RengineEventWrapper firstFail = pattern.get("first").iterator().next();
            // Could't get of next???
            RengineEventWrapper next1Fail = pattern.get("next1").iterator().next();
            out.collect(
                    new WarningEvent(firstFail.getId(), firstFail.getObservedTime(), next1Fail.getObservedTime(), "login fail!"));
        }

        //@formatter:off
        // @Override
        // public WarningEvent select(Map<String, List<RengineEventWrapper>> pattern) throws Exception {
        //     // 从map中按照名称取出对应的事件
        //     RengineEventWrapper firstFail = pattern.get("first").iterator().next();
        //     // Could't get of next???
        //     // RengineEventWrapper lastFail = pattern.get("next").iterator().next();
        //     return new WarningEvent(firstFail.userId, firstFail.eventTime,
        //             /* lastFail.eventTime */-0L, "login fail!");
        // }
        //@formatter:on

    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarningEvent {
        private String eventId;
        private Long firstFailTime;
        private Long lastFailTime;
        private String warningMsg;
    }

}
