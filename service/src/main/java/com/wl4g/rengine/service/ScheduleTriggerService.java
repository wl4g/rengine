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
 * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.service;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Objects.isNull;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.collection.CollectionUtils2;
import com.wl4g.rengine.common.entity.ScheduleTrigger;
import com.wl4g.rengine.service.model.DeleteScheduleTrigger;
import com.wl4g.rengine.service.model.DeleteScheduleTriggerResult;
import com.wl4g.rengine.service.model.QueryScheduleTrigger;
import com.wl4g.rengine.service.model.SaveScheduleTriggerResult;

/**
 * {@link ScheduleTriggerService}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
public interface ScheduleTriggerService {

    default ScheduleTrigger get(Long triggerId) {
        notNullOf(triggerId, "triggerId");
        final PageHolder<ScheduleTrigger> result = query(QueryScheduleTrigger.builder().triggerId(triggerId).build());
        if (isNull(result) || CollectionUtils2.isEmpty(result.getRecords())) {
            throw new IllegalArgumentException(format("No found schedule trigger by %s", triggerId));
        }
        return result.getRecords().get(0);
    }

    PageHolder<ScheduleTrigger> query(@NotNull QueryScheduleTrigger model);

    List<ScheduleTrigger> findWithSharding(
            @NotNull QueryScheduleTrigger model,
            @NotNull Integer divisor,
            @NotNull Integer remainder);

    SaveScheduleTriggerResult save(@NotNull ScheduleTrigger model);

    DeleteScheduleTriggerResult delete(@NotNull DeleteScheduleTrigger model);

}
