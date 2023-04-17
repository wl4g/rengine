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
package com.wl4g.rengine.example.client.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.rengine.example.client.model.CreateOrder;
import com.wl4g.rengine.example.client.service.MyOrderService;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link MyOrderController}
 * 
 * @author James Wong
 * @date 2022-08-28
 * @since v1.0.0
 */
@Slf4j
@ResponseBody
@RestController
@RequestMapping("/order")
public class MyOrderController {

    private final MyOrderService myOrderService;

    public MyOrderController(@Autowired MyOrderService myOrderService) {
        this.myOrderService = myOrderService;
    }

    @RequestMapping("/create")
    public Map<String, Object> create(@RequestBody CreateOrder order, Integer count) {
        log.info("Creating to order ... - {}, count: {}", order, count);

        Map<String, Object> resp = new HashMap<>();
        resp.put("data", myOrderService.create(order, count));

        return resp;
    }

    @RequestMapping("/create2")
    public Map<String, Object> create2(@RequestBody CreateOrder order, Integer count) {
        log.info("Creating2 to order ... - {}, count: {}", order, count);

        Map<String, Object> resp = new HashMap<>();
        resp.put("data", myOrderService.create2(order, count));

        return resp;
    }

}
