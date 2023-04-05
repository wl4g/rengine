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
package org.apache.flink.cep.dynamic.condition;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.singletonList;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.common.event.RengineEvent.EventLocation;
import com.wl4g.rengine.common.event.RengineEvent.EventSource;

/**
 * {@link AviatorConditionTests}
 * 
 * @author James Wong
 * @version 2023-03-14
 * @since v1.0.0
 */
public class AviatorConditionTests {

    @Test
    public void testLogsEvaluateExpression() throws Throwable {
        final Map<String, Object> logRecord = new HashMap<>();
        logRecord.put("@timestamp", currentTimeMillis() + RandomUtils.nextInt(1000, 9999) + "");
        logRecord.put("@version", "1");
        logRecord.put("message", "Completed initialization in 16 ms");
        logRecord.put("logger_name", "o.s.w.s.DispatcherServlet");
        logRecord.put("thread_name", "http-nio-0.0.0.0-28001-exec-1");
        logRecord.put("level", "INFO");
        logRecord.put("level_value", "20000");
        logRecord.put("caller_class_name", "org.springframework.web.servlet.FrameworkServlet");
        logRecord.put("caller_method_name", "initServletBean");
        logRecord.put("caller_file_name", "FrameworkServlet.java");
        logRecord.put("caller_line_number", "547");
        logRecord.put("service", "myapp");
        logRecord.put("logger_group", "main");
        final RengineEvent event = RengineEvent.builder()
                .type("login")
                .source(EventSource.builder()
                        .time(currentTimeMillis() + 11321)
                        .principals(singletonList("jameswong1234@gmail.com"))
                        .location(EventLocation.builder().ipAddress("1.1.1.1").zipcode("20500").build())
                        .build())
                .body(logRecord)
                .build();

        assert new AviatorCondition<RengineEvent>("body.service == 'myapp'").filter(event);
        assert new AviatorCondition<RengineEvent>("body.service == 'myapp' || body.level == 'INFO'").filter(event);
    }

}
