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
package com.wl4g.rengine.common.entity;

import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.lang.System.out;
import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;

import org.junit.Test;

import com.wl4g.rengine.common.entity.CepPattern.AfterMatchSkipStrategyType;
import com.wl4g.rengine.common.entity.CepPattern.AfterMatchStrategy;
import com.wl4g.rengine.common.entity.CepPattern.AviatorCondition;
import com.wl4g.rengine.common.entity.CepPattern.ClassCondition;
import com.wl4g.rengine.common.entity.CepPattern.ConditionType;
import com.wl4g.rengine.common.entity.CepPattern.ConsumingStrategy;
import com.wl4g.rengine.common.entity.CepPattern.Edge;
import com.wl4g.rengine.common.entity.CepPattern.Node;
import com.wl4g.rengine.common.entity.CepPattern.PatternNodeType;
import com.wl4g.rengine.common.entity.CepPattern.Quantifier;
import com.wl4g.rengine.common.entity.CepPattern.QuantifierProperty;
import com.wl4g.rengine.common.entity.CepPattern.Time;
import com.wl4g.rengine.common.entity.CepPattern.Window;

/**
 * Refer to {@link com.wl4g.rengine.job.cep.pattern.PatternTests}
 * 
 * @author James Wong
 * @version 2023-03-13
 * @since v1.0.0
 */
public class CepPatternTests {

    @Test
    public void testCepPatternSerialize() throws Exception {
        final CepPattern cepPattern = CepPattern.builder()
                .name("end")
                .quantifier(Quantifier.builder()
                        .consumingStrategy(ConsumingStrategy.SKIP_TILL_NEXT)
                        .details(asList(QuantifierProperty.SINGLE))
                        .times(null)
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
                                .build()))
                .edges(asList(Edge.builder().source("start").target("middle").type(ConsumingStrategy.SKIP_TILL_NEXT).build(),
                        Edge.builder().source("middle").target("end").type(ConsumingStrategy.SKIP_TILL_NEXT).build()))
                .window(Window.builder().time(Time.minutes(5)).build())
                .afterMatchStrategy(AfterMatchStrategy.builder()
                        .type(AfterMatchSkipStrategyType.SKIP_PAST_LAST_EVENT)
                        .patternName(null)
                        .build())
                .type(PatternNodeType.COMPOSITE)
                .version(1)
                .build()
                .validate();

        System.out.println(toJSONString(cepPattern));
    }

    @Test
    public void testCepPatternDeserialize() throws Throwable {
        // @formatter:off
        final String patternJson = "{"
                + "    \"name\": \"end\","
                + "    \"quantifier\": {"
                + "        \"consumingStrategy\": \"SKIP_TILL_NEXT\","
                + "        \"times\": null,"
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
                + "        \"type\": \"ATOMIC\""
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
                + "        \"type\": \"ATOMIC\""
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
                + "        \"type\": \"ATOMIC\""
                + "    }],"
                + "    \"edges\": [{"
                + "        \"source\": \"start\","
                + "        \"target\": \"middle\","
                + "        \"type\": \"SKIP_TILL_NEXT\""
                + "    }, {"
                + "        \"source\": \"middle\","
                + "        \"target\": \"end\","
                + "        \"type\": \"SKIP_TILL_NEXT\""
                + "    }],"
                + "    \"window\": {"
                + "        \"type\": null,"
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
            final CepPattern pattern = parseJSON(patternJson, CepPattern.class);
            out.println("pattern : " + pattern);
            assert nonNull(pattern);
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw ex;
        }

    }

}
