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
package com.wl4g.rengine.common.entity.graph;

import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.lang.System.out;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static java.util.Objects.nonNull;

import org.junit.Test;

import com.wl4g.rengine.common.entity.Workflow.WorkflowEngine;
import com.wl4g.rengine.common.entity.graph.FlinkCepGraph.AfterMatchSkipStrategyType;
import com.wl4g.rengine.common.entity.graph.FlinkCepGraph.AfterMatchStrategy;
import com.wl4g.rengine.common.entity.graph.FlinkCepGraph.AviatorCondition;
import com.wl4g.rengine.common.entity.graph.FlinkCepGraph.ClassCondition;
import com.wl4g.rengine.common.entity.graph.FlinkCepGraph.ConditionType;
import com.wl4g.rengine.common.entity.graph.FlinkCepGraph.ConsumingStrategy;
import com.wl4g.rengine.common.entity.graph.FlinkCepGraph.Edge;
import com.wl4g.rengine.common.entity.graph.FlinkCepGraph.Node;
import com.wl4g.rengine.common.entity.graph.FlinkCepGraph.PatternNodeType;
import com.wl4g.rengine.common.entity.graph.FlinkCepGraph.Quantifier;
import com.wl4g.rengine.common.entity.graph.FlinkCepGraph.QuantifierProperty;
import com.wl4g.rengine.common.entity.graph.FlinkCepGraph.Time;
import com.wl4g.rengine.common.entity.graph.FlinkCepGraph.Times;
import com.wl4g.rengine.common.entity.graph.FlinkCepGraph.Window;
import com.wl4g.rengine.common.entity.graph.FlinkCepGraph.WithinType;

/**
 * Refer to {@link com.wl4g.rengine.job.cep.pattern.PatternTests}
 * 
 * @author James Wong
 * @date 2023-03-13
 * @since v1.0.0
 */
public class FlinkCepGraphTests {

    @Test
    public void testCepPatternSerialize() throws Exception {
        final FlinkCepGraph flinkCepGraph = FlinkCepGraph.builder()
                .name("root")
                .engine(WorkflowEngine.FLINK_CEP_GRAPH.name())
                .quantifier(Quantifier.builder()
                        .consumingStrategy(ConsumingStrategy.SKIP_TILL_NEXT)
                        .details(asList(QuantifierProperty.SINGLE))
                        .times(Times.of(3, Time.minutes(5)))
                        .untilCondition(null)
                        .build())
                .condition(null)
                .nodes(asList(
                        Node.builder()
                                .name("start")
                                .type(PatternNodeType.ATOMIC)
                                .quantifier(Quantifier.builder()
                                        .consumingStrategy(ConsumingStrategy.SKIP_TILL_NEXT)
                                        .details(asList(QuantifierProperty.SINGLE))
                                        .times(null)
                                        .untilCondition(null)
                                        .build())
                                .condition(ClassCondition.builder()
                                        .type(ConditionType.CLASS)
                                        .className("org.apache.flink.cep.pattern.conditions.RichAndCondition")
                                        .build())
                                .attributes(singletonMap("top", "10px"))
                                .build(),
                        Node.builder()
                                .name("middle")
                                .type(PatternNodeType.ATOMIC)
                                .quantifier(Quantifier.builder()
                                        .consumingStrategy(ConsumingStrategy.SKIP_TILL_NEXT)
                                        .details(asList(QuantifierProperty.SINGLE))
                                        .times(null)
                                        .untilCondition(null)
                                        .build())
                                .condition(
                                        ClassCondition.builder()
                                                .type(ConditionType.CLASS)
                                                .className("org.apache.flink.cep.pattern.conditions.RichAndCondition")
                                                .nestedConditions(asList(ClassCondition.builder()
                                                        .type(ConditionType.CLASS)
                                                        .className("org.apache.flink.cep.pattern.conditions.SubtypeCondition")
                                                        .subClassName("com.wl4g.rengine.common.event.RengineEvent")
                                                        .build(),
                                                        AviatorCondition.builder()
                                                                .type(ConditionType.AVIATOR)
                                                                .expression("body.level == 'ERROR' || body.level == 'FATAL'")
                                                                .build()))
                                                .build())
                                .attributes(singletonMap("top", "20px"))
                                .build(),
                        Node.builder()
                                .name("end")
                                .type(PatternNodeType.ATOMIC)
                                .quantifier(Quantifier.builder()
                                        .consumingStrategy(ConsumingStrategy.SKIP_TILL_NEXT)
                                        .details(asList(QuantifierProperty.SINGLE))
                                        .times(null)
                                        .untilCondition(null)
                                        .build())
                                .condition(AviatorCondition.builder()
                                        .type(ConditionType.AVIATOR)
                                        .expression("body.level == 'ERROR' || body.level == 'FATAL'")
                                        .build())
                                .attributes(singletonMap("top", "10px"))
                                .build()))
                .edges(asList(Edge.builder().source("start").target("middle").type(ConsumingStrategy.SKIP_TILL_NEXT).build(),
                        Edge.builder().source("middle").target("end").type(ConsumingStrategy.SKIP_TILL_NEXT).build()))
                .window(Window.builder().type(WithinType.PREVIOUS_AND_CURRENT).time(Time.minutes(5)).build())
                .afterMatchStrategy(AfterMatchStrategy.builder()
                        .type(AfterMatchSkipStrategyType.SKIP_PAST_LAST_EVENT)
                        .patternName(null)
                        .build())
                .type(PatternNodeType.COMPOSITE)
                .version(1)
                .build()
                .validate();

        System.out.println("flinkCepGraph : " + toJSONString(flinkCepGraph));
    }

    @Test
    public void testCepPatternDeserialize() throws Throwable {
        // @formatter:off
        final String patternJson = "{"
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
                + "        \"details\": [\"SINGLE\"]"
                + "    },"
                + "    \"condition\": null,"
                + "    \"nodes\": [{"
                + "        \"name\": \"start\","
                + "        \"quantifier\": {"
                + "            \"consumingStrategy\": \"SKIP_TILL_NEXT\","
                + "            \"times\": null,"
                + "            \"untilCondition\": null,"
                + "            \"details\": [\"SINGLE\"]"
                + "        },"
                + "        \"condition\": {"
                + "            \"type\": \"CLASS\","
                + "            \"className\": \"org.apache.flink.cep.pattern.conditions.RichAndCondition\","
                + "            \"nestedConditions\": null,"
                + "            \"subClassName\": null"
                + "        },"
                + "        \"type\": \"ATOMIC\","
                + "        \"attributes\": {"
                + "            \"top\": \"10px\""
                + "        }"
                + "    }, {"
                + "        \"name\": \"middle\","
                + "        \"quantifier\": {"
                + "            \"consumingStrategy\": \"SKIP_TILL_NEXT\","
                + "            \"times\": null,"
                + "            \"untilCondition\": null,"
                + "            \"details\": [\"SINGLE\"]"
                + "        },"
                + "        \"condition\": {"
                + "            \"type\": \"CLASS\","
                + "            \"className\": \"org.apache.flink.cep.pattern.conditions.RichAndCondition\","
                + "            \"nestedConditions\": [{"
                + "                \"type\": \"CLASS\","
                + "                \"className\": \"org.apache.flink.cep.pattern.conditions.SubtypeCondition\","
                + "                \"nestedConditions\": null,"
                + "                \"subClassName\": \"com.wl4g.rengine.common.event.RengineEvent\""
                + "            }, {"
                + "                \"type\": \"AVIATOR\","
                + "                \"expression\": \"body.level == 'ERROR' || body.level == 'FATAL'\""
                + "            }],"
                + "            \"subClassName\": null"
                + "        },"
                + "        \"type\": \"ATOMIC\","
                + "        \"attributes\": {"
                + "            \"top\": \"20px\""
                + "        }"
                + "    }, {"
                + "        \"name\": \"end\","
                + "        \"quantifier\": {"
                + "            \"consumingStrategy\": \"SKIP_TILL_NEXT\","
                + "            \"times\": null,"
                + "            \"untilCondition\": null,"
                + "            \"details\": [\"SINGLE\"]"
                + "        },"
                + "        \"condition\": {"
                + "            \"type\": \"AVIATOR\","
                + "            \"expression\": \"body.level == 'ERROR' || body.level == 'FATAL'\""
                + "        },"
                + "        \"type\": \"ATOMIC\","
                + "        \"attributes\": {"
                + "            \"top\": \"10px\""
                + "        }"
                + "    }],"
                + "    \"edges\": [{"
                + "        \"source\": \"start\","
                + "        \"target\": \"middle\","
                + "        \"type\": \"SKIP_TILL_NEXT\","
                + "        \"attributes\": {}"
                + "    }, {"
                + "        \"source\": \"middle\","
                + "        \"target\": \"end\","
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
                + "        \"type\": \"SKIP_PAST_LAST_EVENT\","
                + "        \"patternName\": null"
                + "    },"
                + "    \"type\": \"COMPOSITE\","
                + "    \"version\": 1"
                + "}";
        // @formatter:on
        out.println("patternJson : " + patternJson.replace(" ", ""));

        try {
            final FlinkCepGraph pattern = parseJSON(patternJson, FlinkCepGraph.class);
            out.println("pattern : " + pattern);
            assert nonNull(pattern);
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw ex;
        }

    }

}
