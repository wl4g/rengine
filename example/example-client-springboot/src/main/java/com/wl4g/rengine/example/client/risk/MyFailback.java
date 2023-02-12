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
package com.wl4g.rengine.example.client.risk;

import static java.lang.String.format;

import com.wl4g.rengine.client.core.RengineClient.DefaultFailback;
import com.wl4g.rengine.client.core.RengineClient.FailbackInfo;
import com.wl4g.rengine.common.model.WorkflowExecuteResult;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link MyFailback}
 * 
 * @author James Wong
 * @version 2022-11-03
 * @since v1.0.0
 */
@Slf4j
public class MyFailback extends DefaultFailback {

    @Override
    public WorkflowExecuteResult apply(FailbackInfo f) {
        log.warn(format(":::Failed to evaluation of reason: %s", f.getError().getMessage()));
        return WorkflowExecuteResult.builder().description("Failed to execution").build();
    }

}
