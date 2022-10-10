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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.job.analytic.core.hbase;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.singletonList;

import org.apache.hadoop.hbase.client.Mutation;
import org.junit.Test;

import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.common.event.RengineEvent.EventSource;
import com.wl4g.rengine.common.event.RengineEvent.EventLocation;
import com.wl4g.rengine.job.analytic.core.model.RengineEventAnalytical;

/**
 * {@link EventToMutationConverterTests}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-06-08 v3.0.0
 * @since v3.0.0
 */
public class EventToMutationConverterTests {

    @Test
    public void testEventToMutationConverter() {
        RengineEventAnalytical model = new RengineEventAnalytical(new RengineEvent("device_temp_warning",
                EventSource.builder()
                        .time(currentTimeMillis())
                        .principals(singletonList("admin"))
                        .location(EventLocation.builder()
                                .ipAddress("1.1.1.1")
                                .city("Washington")
                                .region("Pennsylvania Avenue")
                                .zipcode("20500")
                                .build())
                        .build(),
                "A serious alarm occurs when the device temperature is greater than 52℃"));

        EventToMutationConverter converter = new EventToMutationConverter();
        converter.open();
        Mutation mutation = converter.convertToMutation(model);
        System.out.println(mutation);
    }

}
