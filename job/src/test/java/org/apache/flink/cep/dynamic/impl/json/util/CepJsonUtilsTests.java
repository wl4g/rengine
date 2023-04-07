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
package org.apache.flink.cep.dynamic.impl.json.util;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static java.lang.System.out;
import static java.util.Objects.nonNull;

import org.apache.flink.cep.dynamic.condition.AviatorCondition;
import org.apache.flink.cep.nfa.aftermatch.AfterMatchSkipStrategy;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.junit.Test;

import com.wl4g.rengine.common.event.RengineEvent;

/**
 * {@link CepJsonUtilsTests}
 * 
 * @author James Wong
 * @version 2023-02-25
 * @since v1.0.0
 */
@SuppressWarnings("serial")
public class CepJsonUtilsTests {

    @Test
    public void testPatternSerialize() throws Exception {
        Pattern<RengineEvent, ?> pattern1 = Pattern.<RengineEvent> begin("start")
                .where(new StartCondition("action == 0"))
                .followedByAny("middle")
                .subtype(RengineEvent.class)
                .where(new MiddleCondition())
                .followedByAny("end")
                .where(new EndCondition());
        final String patternJson1 = CepJsonUtils.toJson(pattern1);
        out.println("patternJson1 : " + patternJson1);

        // show how to print test pattern in json format
        Pattern<RengineEvent, RengineEvent> pattern2 = Pattern
                .<RengineEvent> begin("start", AfterMatchSkipStrategy.skipPastLastEvent())
                .where(new StartCondition("action == 0"))
                .timesOrMore(3)
                .followedBy("end")
                .where(new EndCondition());

        final String patternJson2 = CepJsonUtils.toJson(pattern2);
        out.println("patternJson2 : " + patternJson2);
    }

    @Test
    public void testPatternDeserialize() throws Throwable {
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
                + "                \"expression\": \"body.logRecord.item2 == 'ERROR'\","
                + "                \"type\": \"AVIATOR\""
                + "            }, {"
                + "                \"expression\": \"body.logRecord.item2 == 'FATAL'\","
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
                + "            \"expression\": \"body.logRecord.item2 == 'TRACE' || body.logRecord.item2 == 'DEBUG' || body.logRecord.item2 == 'INFO' || body.logRecord.item2 == 'WARN'\","
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
        out.println("patternJson : " + patternJson);

        Throwable error = null;
        try {
            final Pattern<?, ?> pattern1 = CepJsonUtils.toPattern(patternJson);
            out.println("pattern1 : " + pattern1);
            assert nonNull(pattern1);
        } catch (Throwable ex) {
            ex.printStackTrace();
            error = ex;
        }

        if (nonNull(error)) {
            throw error;
        }
    }

    public static class StartCondition extends AviatorCondition<RengineEvent> {
        public StartCondition(String expression) {
            super(expression);
        }
    }

    public static class MiddleCondition extends SimpleCondition<RengineEvent> {
        @Override
        public boolean filter(RengineEvent value) throws Exception {
            return safeMap(value.getLabels()).keySet().contains("middle");
        }
    }

    public static class EndCondition extends SimpleCondition<RengineEvent> {
        @Override
        public boolean filter(RengineEvent value) throws Exception {
            return value.getObservedTime() > 1677342912;
        }
    }

}
