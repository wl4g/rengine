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

import java.util.List;

import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.entity.SchedulingTrigger;
import com.wl4g.rengine.service.model.DeleteSchedulingTrigger;
import com.wl4g.rengine.service.model.DeleteSchedulingTriggerResult;
import com.wl4g.rengine.service.model.QuerySchedulingTrigger;
import com.wl4g.rengine.service.model.SaveSchedulingTrigger;
import com.wl4g.rengine.service.model.SaveSchedulingTriggerResult;

/**
 * {@link SchedulingTriggerService}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
public interface SchedulingTriggerService {

    PageHolder<SchedulingTrigger> query(@NotNull QuerySchedulingTrigger model);

    List<SchedulingTrigger> findWithSharding(
            @NotNull QuerySchedulingTrigger model,
            @NotNull Integer divisor,
            @NotNull Integer remainder);

    SaveSchedulingTriggerResult save(@NotNull SaveSchedulingTrigger model);

    DeleteSchedulingTriggerResult delete(@NotNull DeleteSchedulingTrigger model);

}
