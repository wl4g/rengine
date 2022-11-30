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
package com.wl4g.rengine.example.client.service.impl;

import static java.lang.String.valueOf;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wl4g.rengine.client.springboot.intercept.REvaluation;
import com.wl4g.rengine.example.client.model.BehaviorReport;
import com.wl4g.rengine.example.client.risk.MyFailback;
import com.wl4g.rengine.example.client.risk.RengineRiskHandler;
import com.wl4g.rengine.example.client.service.MyUserBehaviorService;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link MyUserBehaviorServiceImpl}
 * 
 * @author James Wong
 * @version 2022-11-03
 * @since v3.0.0
 */
@Slf4j
@Service
public class MyUserBehaviorServiceImpl implements MyUserBehaviorService {

    private @Autowired RengineRiskHandler riskHandler;
    private @Value("${scenes_configs.behaviorReport}") String behaviorReportScenesCode;

    @REvaluation(scenesCode = "${scenes_configs.behaviorReport}", bestEffort = true,
            paramsTemplate = "{{userId=#0,operationType=#1,observedTime=#1}}", failback = MyFailback.class)
    @Override
    public Map<String, String> report(BehaviorReport report) {
        log.info("Reporting to behavior ...");
        // some reporting logical
        // ...

        Map<String, String> orderInfo = new HashMap<>();
        orderInfo.put("result", "success");

        return orderInfo;
    }

    @Override
    public Map<String, String> report2(BehaviorReport report) {
        Map<String, Object> args = new HashMap<>();
        args.put("userId", report.getUserId());
        args.put("operationType", report.getOperationType());
        args.put("observedTime", valueOf(report.getObservedTime()));
        riskHandler.checkRiskFor(behaviorReportScenesCode, args);

        log.info("Reporting to behavior ...");
        // some reporting logical
        // ...

        Map<String, String> orderInfo = new HashMap<>();
        orderInfo.put("result", "success");

        return orderInfo;
    }

}
