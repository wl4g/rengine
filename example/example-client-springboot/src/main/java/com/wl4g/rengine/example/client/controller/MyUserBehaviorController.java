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

import com.wl4g.rengine.example.client.model.BehaviorReport;
import com.wl4g.rengine.example.client.service.MyUserBehaviorService;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link MyUserBehaviorController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Slf4j
@ResponseBody
@RestController
@RequestMapping("/userBehavior")
public class MyUserBehaviorController {

    private final MyUserBehaviorService userBehaviorService;

    public MyUserBehaviorController(@Autowired MyUserBehaviorService userBehaviorService) {
        this.userBehaviorService = userBehaviorService;
    }

    @RequestMapping("/report")
    public Map<String, Object> report(@RequestBody BehaviorReport report) {
        log.info("Reporting to behavior ... - {}", report);

        Map<String, Object> resp = new HashMap<>();
        resp.put("data", userBehaviorService.report(report));

        return resp;
    }

    @RequestMapping("/report2")
    public Map<String, Object> report2(@RequestBody BehaviorReport report) {
        log.info("Reporting to behavior ... - {}", report);

        Map<String, Object> resp = new HashMap<>();
        resp.put("data", userBehaviorService.report2(report));

        return resp;
    }

}
