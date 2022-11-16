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
package com.wl4g.rengine.example.client.risk;

import static java.lang.String.format;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wl4g.rengine.client.core.RengineClient;
import com.wl4g.rengine.common.exception.RengineException;
import com.wl4g.rengine.common.model.EvaluationResult;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link RengineRiskHandler}
 * 
 * @author James Wong
 * @version 2022-11-02
 * @since v3.0.0
 */
@Slf4j
@Component
public class RengineRiskHandler {

    private @Autowired RengineClient rengineClient;

    public void checkRiskFor(String scenesCode, Map<String, String> args) {
        log.info("Risk checking for : {} => {}", scenesCode, args);

        EvaluationResult result = rengineClient.evaluate(scenesCode, true, args);
        log.info("Risk checked for result: {}, {} => {}", result, scenesCode, args);

        // Assertion risk evaluation result.
        if (result.getErrorCount() > 0) {
            throw new RengineException(format("Unable to operation, detected risk in your environment."));
        }
    }

}
