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
package com.wl4g.rengine.client.collector.util;

import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.startsWith;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.wl4g.infra.common.lang.DateUtils2;

/**
 * {@link SpelVariablesTests}
 * 
 * @author James Wong
 * @version 2022-10-22
 * @since v3.0.0
 */
public class SpelVariablesTests {

    @Test
    public void testFromAndResolve() {
        Map<String, String> variables = new HashMap<>();
        variables.put("getYesterday", "#{T(com.wl4g.infra.common.lang.DateUtils2).getDateOf(5,-1,\"yyyy-MM-dd\")}");

        SpelVariables spelVariables = new SpelVariables().from(variables);
        List<String> resolved = spelVariables.resolve(singletonList("myprefix-{{getYesterday}}"));

        String yesterday = DateUtils2.getDateOf(Calendar.DAY_OF_MONTH, -1, "yyyy-MM-dd");
        Assertions.assertTrue(startsWith(resolved.get(0), "myprefix-" + yesterday));
    }

}
