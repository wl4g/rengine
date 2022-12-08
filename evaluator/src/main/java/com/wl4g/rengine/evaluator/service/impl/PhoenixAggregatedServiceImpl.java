//@formatter:off
///**
// * Copyright 2017 ~ 2025 the original author or authors. James Wong <jameswong1376@gmail.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.wl4g.rengine.evaluator.service.impl;
//
//import java.util.List;
//import java.util.Map;
//
//import javax.inject.Inject;
//import javax.inject.Singleton;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotNull;
//
//import com.wl4g.rengine.evaluator.repository.PhoenixRepository;
//import com.wl4g.rengine.evaluator.service.PhoenixAggregatedService;
//
//import lombok.extern.slf4j.Slf4j;
//
///**
// * {@link PhoenixAggregatedServiceImpl}
// *
// * @author James Wong
// * @version 2022-10-10
// * @since v1.0.0
// */
//@Slf4j
//@Singleton
//public class PhoenixAggregatedServiceImpl implements PhoenixAggregatedService {
//
//    @Inject
//    PhoenixRepository phoenixRepository;
//
//    @Override
//    public List<Map<String, Object>> findList(@NotBlank String sql, @NotNull Map<String, Object> queryParams) {
//        log.debug("Find '{}' by '{}'", tableName, queryParams);
//
//        // TODO Auto-generated method stub
//
//        return null;
//    }
//
//}
//@formatter:on