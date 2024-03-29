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
import static java.util.Collections.singletonList;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wl4g.rengine.client.core.RengineClient;
import com.wl4g.rengine.common.exception.RengineException;
import com.wl4g.rengine.common.model.WorkflowExecuteResult;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link RengineRiskHandler}
 * 
 * @author James Wong
 * @date 2022-11-02
 * @since v3.0.0
 */
@Slf4j
@Component
public class RengineRiskHandler {

    private @Autowired RengineClient rengineClient;

    public void checkRiskFor(String scenesCode, Map<String, Object> args) {
        log.info("Risk checking for : {} => {}", scenesCode, args);

        WorkflowExecuteResult result = rengineClient.execute(singletonList(scenesCode), true, args);
        log.info("Risk checked for result: {}, {} => {}", result, scenesCode, args);

        // Assertion result.
        if (((Number) result.firstValueMap().getOrDefault("riskScore", 0d)).doubleValue() > 50d) {
            throw new RengineException(format("Denied to operation, detected risk in your environment."));
        } else {
            log.debug("Check passed.");
        }
    }

}
