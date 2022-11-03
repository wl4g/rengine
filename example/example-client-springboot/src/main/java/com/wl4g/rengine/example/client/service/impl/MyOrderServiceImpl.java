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
package com.wl4g.rengine.example.client.service.impl;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wl4g.rengine.client.core.RengineClient.DefaultFailback;
import com.wl4g.rengine.client.springboot.intercept.REvaluation;
import com.wl4g.rengine.common.model.EvaluationResult;
import com.wl4g.rengine.example.client.risk.RengineRiskHandler;
import com.wl4g.rengine.example.client.service.MyOrderService;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link MyOrderServiceImpl}
 * 
 * @author James Wong
 * @version 2022-11-01
 * @since v1.0.0
 */
@Slf4j
@Service
public class MyOrderServiceImpl implements MyOrderService {

    private final RengineRiskHandler riskHandler;

    private @Value("${scenes_configs.createOrder}") String createOrderScenesCode;

    public MyOrderServiceImpl(@Autowired RengineRiskHandler riskHandler) {
        this.riskHandler = riskHandler;
    }

    @Override
    public Map<String, String> create(String userId, String goodId, String address, Integer count) {
        Map<String, String> args = new HashMap<>();
        args.put("userId", userId);
        args.put("goodId", goodId);
        args.put("count", valueOf(count));
        riskHandler.checkRiskFor(createOrderScenesCode, args);

        log.info("Creating to order ...");

        // ...
        // some order creating logical
        // ...

        Map<String, String> orderInfo = new HashMap<>();
        orderInfo.put("orderId", UUID.randomUUID().toString());
        orderInfo.put("status", "1");
        orderInfo.put("timestamp", valueOf(currentTimeMillis()));
        orderInfo.put("address", address);

        return orderInfo;
    }

    @REvaluation(scenesCode = "${scenes_configs.createOrder}", bestEffort = true, paramsTemplate = "{{userId=#0,goodId=#1}}",
            failback = MyFailback.class)
    @Override
    public Map<String, String> create2(String userId, String goodId, String address, Integer count) {
        log.info("Creating2 to order ...");

        // ...
        // some order creating logical
        // ...

        Map<String, String> orderInfo = new HashMap<>();
        orderInfo.put("orderId", UUID.randomUUID().toString());
        orderInfo.put("status", "1");
        orderInfo.put("timestamp", valueOf(currentTimeMillis()));
        orderInfo.put("address", address);

        return orderInfo;
    }

    @Slf4j
    public static class MyFailback extends DefaultFailback {
        @Override
        public EvaluationResult apply(Throwable t) {
            log.warn(format(":::Failed to evaluation of reason: %s", t.getMessage()));
            return EvaluationResult.builder().errorCount(Integer.MAX_VALUE).build();
        }
    }

}
