/*
 * Copyright 2017 ~ 2025 the original author or authors. James Wong <jameswong1376@gmail.com>
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
package com.wl4g.rengine.job.cep;

import static java.util.Collections.singletonMap;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.dynamic.impl.json.util.CepJsonUtils;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.Test;

import com.wl4g.infra.common.lang.tuples.Tuple2;
import com.wl4g.infra.common.lang.tuples.Tuple3;
import com.wl4g.rengine.common.event.RengineEvent;

/**
 * Refer to {@link com.wl4g.rengine.job.cep.pattern.PatternTests} and
 * {@link com.wl4g.rengine.job.kafka.RengineKafkaFlinkCepStreamingIT}
 * 
 * @author James Wong
 * @version 2023-03-16
 * @since v1.0.0
 */
public class RengineKafkaFlinkCepStreamingTests {

    @Test
    public void testSimplePatternCEP() throws Exception {
        System.out.println("SIMPLE_PATTERN_JSON : " + SIMPLE_PATTERN_JSON);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<RengineEvent> input = env.fromElements(
                RengineEvent.builder().id("1").type("barfoo").body(singletonMap("price", 1d)).build(),
                RengineEvent.builder().id("2").type("start").body(singletonMap("price", 1d)).build(),
                RengineEvent.builder().id("3").type("foobar").body(singletonMap("price", 1d)).build(),
                RengineEvent.builder().id("4").type("foo").body(singletonMap("foo", new Tuple2(4d, 1d))).build(),
                RengineEvent.builder().id("5").type("middle1").body(singletonMap("price", 5d)).build(),
                RengineEvent.builder().id("6").type("middle2").body(singletonMap("price", new Tuple2(6d, 2d))).build(),
                RengineEvent.builder().id("7").type("bar").body(singletonMap("bar", new Tuple2(3d, 3d))).build(),
                RengineEvent.builder().id("42").type("abc").body(singletonMap("price", 1d)).build(),
                RengineEvent.builder().id("8").type("end").body(singletonMap("price", 1d)).build());

        @SuppressWarnings("unchecked")
        Pattern<RengineEvent, ?> pattern = (Pattern<RengineEvent, ?>) CepJsonUtils.toPattern(SIMPLE_PATTERN_JSON);

        DataStream<String> resultDS = CEP.pattern(input, pattern).inProcessingTime().flatSelect((p, o) -> {
            StringBuilder builder = new StringBuilder();

            builder.append(p.get("start").get(0).getId())
                    .append(",")
                    .append(p.get("middle").get(0).getId())
                    .append(",")
                    .append(p.get("end").get(0).getId());

            o.collect(builder.toString());
        }, Types.STRING);

        List<String> resultList = new ArrayList<>();

        // DataStreamUtils.collect(resultDS).forEachRemaining(resultList::add);
        resultDS.executeAndCollect().forEachRemaining(resultList::add);

        assertEquals(Arrays.asList("2,5,8", "2,6,8"), resultList);
    }

    @Test
    public void testErrorLogsPatternCEP() throws Exception {
        System.out.println("LOG_ERROR_PATTERN_JSON : " + LOG_ERROR_PATTERN_JSON);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<RengineEvent> input = env.fromElements(
                RengineEvent.builder()
                        .id("0")
                        .type("APP_LOG")
                        .body(singletonMap("logRecord", new Tuple3("user-service", "INFO", "The mock log message 1")))
                        .build(),
                RengineEvent.builder()
                        .id("1")
                        .type("APP_LOG")
                        .body(singletonMap("logRecord", new Tuple3("order-service", "INFO", "The mock log message 1")))
                        .build(),
                RengineEvent.builder()
                        .id("2")
                        .type("APP_LOG")
                        .body(singletonMap("logRecord", new Tuple3("order-service", "INFO", "The mock log message 2")))
                        .build(),
                RengineEvent.builder()
                        .id("3")
                        .type("APP_LOG")
                        .body(singletonMap("logRecord", new Tuple3("order-service", "ERROR", "The mock log message 3")))
                        .build(),
                RengineEvent.builder()
                        .id("4")
                        .type("APP_LOG")
                        .body(singletonMap("logRecord", new Tuple3("order-service", "INFO", "The mock log message 4")))
                        .build(),
                RengineEvent.builder()
                        .id("5")
                        .type("APP_LOG")
                        .body(singletonMap("logRecord", new Tuple3("order-service", "FATAL", "The mock log message 5")))
                        .build(),
                RengineEvent.builder()
                        .id("6")
                        .type("APP_LOG")
                        .body(singletonMap("logRecord", new Tuple3("order-service", "INFO", "The mock log message 6")))
                        .build(),
                RengineEvent.builder()
                        .id("7")
                        .type("APP_LOG")
                        .body(singletonMap("logRecord", new Tuple3("order-service", "WARN", "The mock log message 7")))
                        .build(),
                RengineEvent.builder()
                        .id("8")
                        .type("APP_LOG")
                        .body(singletonMap("logRecord", new Tuple3("order-service", "DEBUG", "The mock log message 8")))
                        .build(),
                RengineEvent.builder()
                        .id("9")
                        .type("APP_LOG")
                        .body(singletonMap("logRecord", new Tuple3("pay-service", "INFO", "The mock log message 9")))
                        .build(),
                RengineEvent.builder()
                        .id("10")
                        .type("APP_LOG")
                        .body(singletonMap("logRecord", new Tuple3("user-service", "DEBUG", "The mock log message 10")))
                        .build());

        @SuppressWarnings("unchecked")
        Pattern<RengineEvent, ?> pattern = (Pattern<RengineEvent, ?>) CepJsonUtils.toPattern(LOG_ERROR_PATTERN_JSON);

        final KeyedStream<RengineEvent, String> keyedInput = input.keyBy(event -> {
            final String keyBy = event.atAsText("body.logRecord.item1");
            return isBlank(keyBy) ? event.getType() : keyBy;
        });

        DataStream<String> resultDS = CEP.pattern(keyedInput, pattern).inProcessingTime().flatSelect((p, o) -> {
            StringBuilder builder = new StringBuilder();
            builder.append(p.get("start").get(0).getId()).append(",").append(p.get("middle").get(0).getId());
            o.collect(builder.toString());
        }, Types.STRING);

        List<String> resultList = new ArrayList<>();

        // DataStreamUtils.collect(resultDS).forEachRemaining(resultList::add);
        resultDS.executeAndCollect().forEachRemaining(resultList::add);

        assertEquals(Arrays.asList("1,3", "2,3", "4,5"), resultList);
    }

    // @formatter:off
    static final String SIMPLE_PATTERN_JSON = "{"
            + "    \"engine\": \"FLINK_CEP_GRAPH\","
            + "    \"name\": \"root\","
            + "    \"quantifier\": {"
            + "        \"consumingStrategy\": \"SKIP_TILL_NEXT\","
            + "        \"times\": {"
            + "             \"from\": 1,"
            + "             \"to\": 3,"
            + "             \"windowTime\": {"
            + "                 \"unit\": \"MINUTES\","
            + "                 \"size\": 5"
            + "             }"
            + "         },"
            + "        \"untilCondition\": null,"
            + "        \"properties\": [\"SINGLE\"]"
            + "    },"
            + "    \"condition\": null,"
            + "    \"nodes\": [{"
            + "        \"name\": \"end\","
            + "        \"quantifier\": {"
            + "            \"consumingStrategy\": \"SKIP_TILL_NEXT\","
            + "            \"times\": null,"
            + "            \"untilCondition\": null,"
            + "            \"properties\": [\"SINGLE\"]"
            + "        },"
            + "        \"condition\": {"
            + "            \"expression\": \"type == 'end'\","
            + "            \"type\": \"AVIATOR\""
            + "        },"
            + "        \"attributes\": {"
            + "            \"top\": \"10px\""
            + "        },"
            + "        \"type\": \"ATOMIC\""
            + "    }, {"
            + "        \"name\": \"middle\","
            + "        \"quantifier\": {"
            + "            \"consumingStrategy\": \"SKIP_TILL_NEXT\","
            + "            \"times\": null,"
            + "            \"untilCondition\": null,"
            + "            \"properties\": [\"SINGLE\"]"
            + "        },"
            + "        \"condition\": {"
            + "            \"nestedConditions\": [{"
            + "                \"expression\": \"type == 'middle1'\","
            + "                \"type\": \"AVIATOR\""
            + "            }, {"
            + "                \"expression\": \"type == 'middle2'\","
            + "                \"type\": \"AVIATOR\""
            + "            }],"
            + "            \"type\": \"CLASS\","
            + "            \"className\": \"org.apache.flink.cep.pattern.conditions.RichOrCondition\""
            + "        },"
            + "        \"attributes\": {"
            + "            \"top\": \"20px\""
            + "        },"
            + "        \"type\": \"ATOMIC\""
            + "    }, {"
            + "        \"name\": \"start\","
            + "        \"quantifier\": {"
            + "            \"consumingStrategy\": \"SKIP_TILL_NEXT\","
            + "            \"times\": null,"
            + "            \"untilCondition\": null,"
            + "            \"properties\": [\"SINGLE\"]"
            + "        },"
            + "        \"condition\": {"
            + "            \"expression\": \"type == 'start'\","
            + "            \"type\": \"AVIATOR\""
            + "        },"
            + "        \"attributes\": {"
            + "            \"top\": \"20px\""
            + "        },"
            + "        \"type\": \"ATOMIC\""
            + "    }],"
            + "    \"edges\": [{"
            + "        \"source\": \"middle\","
            + "        \"target\": \"end\","
            + "        \"type\": \"SKIP_TILL_ANY\""
            + "    }, {"
            + "        \"source\": \"start\","
            + "        \"target\": \"middle\","
            + "        \"type\": \"SKIP_TILL_ANY\""
            + "    }],"
            + "    \"window\": {"
            + "        \"type\": \"PREVIOUS_AND_CURRENT\","
            + "        \"time\": {"
            + "            \"unit\": \"MINUTES\","
            + "            \"size\": 5"
            + "        }"
            + "    },"
            + "    \"afterMatchStrategy\": {"
            + "        \"type\": \"NO_SKIP\","
            + "        \"patternName\": null"
            + "    },"
            + "    \"type\": \"COMPOSITE\","
            + "    \"version\": 1"
            + "}";
    // @formatter:on

    // @formatter:off
    static final String LOG_ERROR_PATTERN_JSON = "{"
            + "    \"engine\": \"FLINK_CEP_GRAPH\","
            + "    \"name\": \"root\","
            + "    \"quantifier\": {"
            + "        \"consumingStrategy\": \"SKIP_TILL_NEXT\","
            + "        \"times\": {"
            + "            \"from\": 1,"
            + "            \"to\": 3,"
            + "            \"windowTime\": {"
            + "                \"unit\": \"MINUTES\","
            + "                \"size\": 5"
            + "            }"
            + "        },"
            + "        \"untilCondition\": null,"
            + "        \"properties\": [\"SINGLE\"]"
            + "    },"
            + "    \"condition\": null,"
            + "    \"nodes\": [{"
            + "        \"name\": \"middle\","
            + "        \"quantifier\": {"
            + "            \"consumingStrategy\": \"SKIP_TILL_NEXT\","
            + "            \"times\": null,"
            + "            \"untilCondition\": null,"
            + "            \"properties\": [\"SINGLE\"]"
            + "        },"
            + "        \"condition\": {"
            + "            \"nestedConditions\": [{"
            + "                \"expression\": \"body.level == 'ERROR'\","
            + "                \"type\": \"AVIATOR\""
            + "            }, {"
            + "                \"expression\": \"body.level == 'FATAL'\","
            + "                \"type\": \"AVIATOR\""
            + "            }],"
            + "            \"type\": \"CLASS\","
            + "            \"className\": \"org.apache.flink.cep.pattern.conditions.RichOrCondition\""
            + "        },"
            + "        \"attributes\": {"
            + "            \"top\": \"10px\""
            + "        },"
            + "        \"type\": \"ATOMIC\""
            + "    }, {"
            + "        \"name\": \"start\","
            + "        \"quantifier\": {"
            + "            \"consumingStrategy\": \"SKIP_TILL_NEXT\","
            + "            \"times\": null,"
            + "            \"untilCondition\": null,"
            + "            \"properties\": [\"SINGLE\"]"
            + "        },"
            + "        \"condition\": {"
            + "            \"expression\": \"body.level == 'TRACE' || body.level == 'DEBUG' || body.level == 'INFO' || body.level == 'WARN'\","
            + "            \"type\": \"AVIATOR\""
            + "        },"
            + "        \"attributes\": {"
            + "            \"top\": \"20px\""
            + "        },"
            + "        \"type\": \"ATOMIC\""
            + "    }],"
            + "    \"edges\": [{"
            + "        \"source\": \"start\","
            + "        \"target\": \"middle\","
            + "        \"type\": \"SKIP_TILL_NEXT\","
            + "        \"attributes\": {}"
            + "    }],"
            + "    \"window\": {"
            + "        \"type\": \"PREVIOUS_AND_CURRENT\","
            + "        \"time\": {"
            + "            \"unit\": \"MINUTES\","
            + "            \"size\": 5"
            + "        }"
            + "    },"
            + "    \"afterMatchStrategy\": {"
            + "        \"type\": \"NO_SKIP\","
            + "        \"patternName\": null"
            + "    },"
            + "    \"type\": \"COMPOSITE\","
            + "    \"version\": 1"
            + "}";
    // @formatter:on

}
