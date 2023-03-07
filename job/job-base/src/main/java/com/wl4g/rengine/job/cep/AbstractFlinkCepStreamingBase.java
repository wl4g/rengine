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
package com.wl4g.rengine.job.cep;

import static com.wl4g.infra.common.codec.Encodes.decodeBase64String;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.ParseException;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternFlatSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.dynamic.impl.json.util.CepJsonUtils;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.connector.kafka.source.KafkaSourceOptions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.util.Collector;

import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.common.event.RengineEvent.EventSource;
import com.wl4g.rengine.job.AbstractFlinkStreamingBase;

import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link AbstractFlinkCepStreamingBase} </br>
 * </br>
 * 
 * for example (cep pattern json):
 * 
 * <pre>
 *  {
 *      "name": "end",
 *      "quantifier": {
 *          "consumingStrategy": "SKIP_TILL_NEXT",
 *          "details": [
 *              "SINGLE"
 *          ],
 *          "times": null,
 *          "untilCondition": null
 *      },
 *      "condition": null,
 *      "nodes": [
 *          {
 *              "name": "end",
 *              "quantifier": {
 *                  "consumingStrategy": "SKIP_TILL_NEXT",
 *                  "details": [
 *                      "SINGLE"
 *                  ],
 *                  "times": null,
 *                  "untilCondition": null
 *              },
 *              "condition": {
 *                  "expression": "type == 'login_success'",
 *                  "type": "AVIATOR"
 *              },
 *              "type": "ATOMIC"
 *          },
 *          {
 *              "name": "middle",
 *              "quantifier": {
 *                  "consumingStrategy": "SKIP_TILL_NEXT",
 *                  "details": [
 *                      "SINGLE"
 *                  ],
 *                  "times": null,
 *                  "untilCondition": null
 *              },
 *              "condition": {
 *                  "nestedConditions": [
 *                      {
 *                          "className": "org.apache.flink.cep.pattern.conditions.SubtypeCondition",
 *                          "subClassName": "com.wl4g.rengine.common.event.RengineEvent",
 *                          "type": "CLASS"
 *                      },
 *                      {
 *                          "expression": "type == 'login_tail'",
 *                          "type": "AVIATOR"
 *                      }
 *                  ],
 *                  "type": "CLASS",
 *                  "className": "org.apache.flink.cep.pattern.conditions.RichAndCondition"
 *              },
 *              "type": "ATOMIC"
 *          },
 *          {
 *              "name": "start",
 *              "quantifier": {
 *                  "consumingStrategy": "SKIP_TILL_NEXT",
 *                  "details": [
 *                      "SINGLE"
 *                  ],
 *                  "times": null,
 *                  "untilCondition": null
 *              },
 *              "condition": {
 *                  "expression": "type == 'login_tail'",
 *                  "type": "AVIATOR"
 *              },
 *              "type": "ATOMIC"
 *          }
 *      ],
 *      "edges": [
 *          {
 *              "source": "middle",
 *              "target": "end",
 *              "type": "SKIP_TILL_ANY"
 *          },
 *          {
 *              "source": "start",
 *              "target": "middle",
 *              "type": "SKIP_TILL_ANY"
 *          }
 *      ],
 *      "window": null,
 *      "afterMatchStrategy": {
 *          "type": "NO_SKIP",
 *          "patternName": null
 *      },
 *      "type": "COMPOSITE",
 *      "version": 1
 *  }
 * </pre>
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-05-31 v3.0.0
 * @since v1.0.0
 * @see https://stackoverflow.com/questions/69765972/migrating-from-flinkkafkaconsumer-to-kafkasource-no-windows-executed
 * @see https://github1s.com/RealtimeCompute/ververica-cep-demo/blob/master/src/main/java/com/alibaba/ververica/cep/demo/CepDemo.java
 */
@Getter
@CustomLog
public abstract class AbstractFlinkCepStreamingBase extends AbstractFlinkStreamingBase {

    private String patternJsonBase64;
    private String partitionDiscoveryIntervalMs;

    public AbstractFlinkCepStreamingBase() {
        super();
        this.builder.mustOption("P", "patternJsonBase64", "The cep pattern json base64.")
                .longOption("partitionDiscoveryIntervalMs", "30000", "The per millis for discover new partitions interval.");
    }

    @Override
    protected AbstractFlinkStreamingBase parse(String[] args) throws ParseException {
        super.parse(args);
        this.patternJsonBase64 = line.get("patternJsonBase64");
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

    @SuppressWarnings({ "unchecked", "serial" })
    @Override
    protected DataStream<?> customStream(DataStreamSource<RengineEvent> dataStreamSource) {
        final KeyedStream<RengineEvent, String> keyedStreamSource = dataStreamSource
                .keyBy(new KeySelector<RengineEvent, String>() {
                    @Override
                    public String getKey(RengineEvent event) throws Exception {
                        // TODO The grouping default by principal 0.
                        return ((EventSource) event.getSource()).getPrincipals().get(0);
                    }
                });

        final Pattern<RengineEvent, RengineEvent> cepPattern = (Pattern<RengineEvent, RengineEvent>) CepJsonUtils
                .toPattern(decodeBase64String(patternJsonBase64));
        log.info("Using cep pattern : {}", cepPattern);

        final PatternStream<RengineEvent> patternStream = CEP.pattern(keyedStreamSource, cepPattern);

        final SingleOutputStreamOperator<WarningEvent> selectStream = patternStream.flatSelect(new GenericEventMatcher());

        return selectStream;
    }

    public static class GenericEventMatcher
            implements /* PatternSelectFunction , */ PatternFlatSelectFunction<RengineEvent, WarningEvent> {
        private static final long serialVersionUID = 1L;

        @Override
        public void flatSelect(Map<String, List<RengineEvent>> pattern, Collector<WarningEvent> out) throws Exception {
            out.collect(WarningEvent.builder().patternMap(pattern).warningMsg("The matched warning msg.").build());
        }
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarningEvent {
        private Map<String, List<RengineEvent>> patternMap;
        private String warningMsg;
    }

}
