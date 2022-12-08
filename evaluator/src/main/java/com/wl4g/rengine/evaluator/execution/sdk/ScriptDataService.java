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
package com.wl4g.rengine.evaluator.execution.sdk;

import org.graalvm.polyglot.HostAccess;

import com.wl4g.rengine.evaluator.service.MongoAggregatedService;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * {@link ScriptDataService}
 * 
 * @author James Wong
 * @version 2022-10-10
 * @since v1.0.0
 */
@ToString
@AllArgsConstructor
public class ScriptDataService {

    private final MongoAggregatedService mongoAggregatedService;

    public @HostAccess.Export MongoAggregatedService mongoService() {
        return mongoAggregatedService;
    }

    // public @HostAccess.Export MongoAggregatedService phoenixService() {
    // return mongoAggregatedService;
    // }

    // public @HostAccess.Export MongoAggregatedService opentsdbService() {
    // return opentsdbAggregatedService;
    // }

}
