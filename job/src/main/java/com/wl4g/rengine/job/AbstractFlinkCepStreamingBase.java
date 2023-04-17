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

import static com.wl4g.infra.common.codec.Encodes.decodeBase64String;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseToNode;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.ParseException;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.dynamic.impl.json.util.CepJsonUtils;
import org.apache.flink.cep.functions.PatternProcessFunction;
import org.apache.flink.cep.functions.TimedOutPartialMatchHandler;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import com.fasterxml.jackson.databind.JsonNode;
import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.common.event.RengineEvent.EventSource;

import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link AbstractFlinkCepStreamingBase} </br>
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @date 2022-05-31 v3.0.0
 * @since v1.0.0
 * @see https://stackoverflow.com/questions/69765972/migrating-from-flinkkafkaconsumer-to-kafkasource-no-windows-executed
 * @see https://github1s.com/RealtimeCompute/ververica-cep-demo/blob/master/src/main/java/com/alibaba/ververica/cep/demo/CepDemo.java
 */
@Getter
@CustomLog
public abstract class AbstractFlinkCepStreamingBase extends AbstractFlinkStreamingBase {

    private String cepPatterns;
    private Boolean inProcessingTime;
    private String alertTopic;

    public AbstractFlinkCepStreamingBase() {
        super();
        this.builder.mustOption("P", "cepPatterns", "he cep patterns array json with base64 encode.")
                .longOption("inProcessingTime", "false",
                        "Use the pattern stream for processing time, event source time will be ignored.")
                .longOption("alertTopic", "rengine_alert",
                        "Topic for producer the alerts message of Flink CEP match generated.");
    }

    @Override
    protected AbstractFlinkStreamingBase parse(String[] args) throws ParseException {
        super.parse(args);
        this.cepPatterns = line.get("cepPatterns");
        this.inProcessingTime = line.getBoolean("inProcessingTime");
        this.alertTopic = line.get("alertTopic");
        return this;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected DataStream<?> customStream(DataStream<RengineEvent> dataStreamSource) {
        final DataStream<RengineEvent> keyedStreamSource = (DataStream<RengineEvent>) super.customStream(dataStreamSource);

        final JsonNode cepPatternNode = parseToNode(decodeBase64String(cepPatterns));
        final List<Pattern<RengineEvent, RengineEvent>> mergeCePatterns = new ArrayList<>(cepPatternNode.size());
        cepPatternNode.forEach(jn -> mergeCePatterns.add((Pattern) CepJsonUtils.toPattern(toJSONString(jn))));

        DataStream<RengineEvent> mergeSelectStream = null;
        for (Pattern<RengineEvent, RengineEvent> pattern : mergeCePatterns) {
            log.info("Using cep pattern : {}", pattern);

            PatternStream<RengineEvent> patternStream = CEP.pattern(keyedStreamSource, (Pattern) pattern);
            // Default use event time.
            if (this.inProcessingTime) {
                patternStream = patternStream.inProcessingTime();
            } else {
                patternStream = patternStream.inEventTime();
            }
            // Match and union streams.
            SingleOutputStreamOperator<RengineEvent> selectStream = patternStream.process(new GenericEventMatcher(alertTopic));
            if (nonNull(mergeSelectStream)) {
                mergeSelectStream = mergeSelectStream.union(selectStream);
            } else {
                mergeSelectStream = selectStream;
            }
        }

        return mergeSelectStream;
    }

    /**
     * @see https://nightlies.apache.org/flink/flink-docs-release-1.14/zh/docs/libs/cep/#从模式中选取
     */
    @Getter
    public static class GenericEventMatcher extends PatternProcessFunction<RengineEvent, RengineEvent> implements
            /* PatternFlatSelectFunction<RengineEvent, RengineEvent>, */ TimedOutPartialMatchHandler<RengineEvent> {
        private static final long serialVersionUID = 1L;

        private final String alertTopic;
        private final OutputTag<RengineEvent> outputTag;

        @SuppressWarnings("serial")
        public GenericEventMatcher(String alertTopic) {
            this.alertTopic = hasTextOf(alertTopic, "alertTopic");
            this.outputTag = new OutputTag<RengineEvent>(alertTopic.concat("-timeout-stream")) {
            };
        }

        @Override
        public void processMatch(Map<String, List<RengineEvent>> match, Context ctx, Collector<RengineEvent> out)
                throws Exception {
            final RengineEvent alertEvent = RengineEvent.builder()
                    .type(alertTopic)
                    .source(EventSource.builder().time(ctx.timestamp()).build())
                    .build();
            alertEvent.getBody().put("description", "The matched alert event msg.");
            alertEvent.getBody()
                    .put("match", safeMap(match).entrySet()
                            .stream()
                            .collect(toMap(e -> e.getKey(),
                                    e -> safeList(e.getValue()).stream().map(event -> event.getBody()).collect(toList()))));

            log.debug("Generated alert: {}", alertEvent);
            out.collect(alertEvent);
        }

        @Override
        public void processTimedOutMatch(Map<String, List<RengineEvent>> match, Context ctx) throws Exception {
            final RengineEvent alertEvent = RengineEvent.builder()
                    // TODO using config?
                    .type(alertTopic.concat("_TIMEOUT"))
                    .observedTime(ctx.currentProcessingTime())
                    .source(EventSource.builder().time(ctx.timestamp()).build())
                    .build();
            alertEvent.getBody().put("desc", "The timed out match event msg.");
            alertEvent.getBody()
                    .put("match",
                            safeMap(match).entrySet().stream().collect(toMap(e -> e.getKey(), e -> toJSONString(e.getValue()))));

            ctx.output(outputTag, alertEvent);
        }

    }

}
