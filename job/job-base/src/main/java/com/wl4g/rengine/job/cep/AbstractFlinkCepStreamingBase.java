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
import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseToNode;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.ParseException;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternFlatSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.dynamic.impl.json.util.CepJsonUtils;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.util.Collector;

import com.fasterxml.jackson.databind.JsonNode;
import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.common.event.RengineEvent.EventSource;
import com.wl4g.rengine.job.AbstractFlinkStreamingBase;

import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link AbstractFlinkCepStreamingBase} </br>
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

    private String cepPatterns;
    private String alertTopic;

    public AbstractFlinkCepStreamingBase() {
        super();
        this.builder.mustOption("P", "cepPatterns", "he cep patterns array json with base64 encode.")
                .longOption("alertTopic", "rengine_alerts",
                        "Topic for producer the alerts message of Flink CEP match generated.");
    }

    @Override
    protected AbstractFlinkStreamingBase parse(String[] args) throws ParseException {
        super.parse(args);
        this.cepPatterns = line.get("cepPatterns");
        this.alertTopic = line.get("alertTopic");
        return this;
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    protected DataStream<?> customStream(DataStream<RengineEvent> dataStreamSource) {
        final DataStream<RengineEvent> keyedStreamSource = (DataStream<RengineEvent>) super.customStream(dataStreamSource);

        final JsonNode cepPatternNode = parseToNode(decodeBase64String(cepPatterns));
        final List<Pattern<RengineEvent, RengineEvent>> mergeCePatterns = new ArrayList<>(cepPatternNode.size());
        cepPatternNode.forEach(
                jn -> mergeCePatterns.add((Pattern<RengineEvent, RengineEvent>) CepJsonUtils.toPattern(toJSONString(jn))));

        DataStream<RengineEvent> mergeSelectStream = null;
        for (Pattern<RengineEvent, RengineEvent> pattern : mergeCePatterns) {
            log.info("Using cep pattern : {}", pattern);

            final PatternStream<RengineEvent> patternStream = CEP.pattern(keyedStreamSource,
                    (Pattern<RengineEvent, RengineEvent>) pattern);
            // Matching and union streams.
            final SingleOutputStreamOperator<RengineEvent> selectStream = patternStream
                    .flatSelect(new GenericEventMatcher(alertTopic));
            if (nonNull(mergeSelectStream)) {
                mergeSelectStream = mergeSelectStream.union(selectStream);
            } else {
                mergeSelectStream = selectStream;
            }
        }

        return mergeSelectStream;
    }

    @Getter
    @AllArgsConstructor
    public static class GenericEventMatcher
            implements /* PatternSelectFunction , */ PatternFlatSelectFunction<RengineEvent, RengineEvent> {
        private static final long serialVersionUID = 1L;

        private String alertTopic;

        @Override
        public void flatSelect(Map<String, List<RengineEvent>> pattern, Collector<RengineEvent> out) throws Exception {
            final EventSource source = EventSource.builder().build();

            // TODO using alertTopic as event type ?
            final RengineEvent alertEvent = RengineEvent.builder().type(alertTopic).source(source).build();
            alertEvent.getBody().put("warningMsg", "The matched warning msg.");

            // add alerts properties.
            safeMap(pattern).entrySet().stream().forEach(e -> {
                // source.setTime(null);
                alertEvent.getBody().put("MATCH_".concat(trimToEmpty(e.getKey())), toJSONString(e.getValue()));
            });

            out.collect(alertEvent);
        }
    }

}
