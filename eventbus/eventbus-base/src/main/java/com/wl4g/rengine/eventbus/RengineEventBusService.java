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
package com.wl4g.rengine.eventbus;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static java.util.Collections.singletonList;

import java.util.List;

import com.wl4g.rengine.common.event.RengineEvent;

/**
 * {@link RengineEventBusService}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-05-26 v3.0.0
 * @since v1.0.0
 */
public interface RengineEventBusService<R> {

    Object getOriginal();

    default R publish(RengineEvent event) {
        return safeList(publish(singletonList(event))).stream().findFirst().orElse(null);
    }

    List<R> publish(List<RengineEvent> events);

}
