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
package com.wl4g.rengine.example.client.service;

import java.util.Map;

import com.wl4g.rengine.example.client.model.CreateOrder;

/**
 * {@link MyOrderService}
 * 
 * @author James Wong
 * @date 2022-11-01
 * @since v1.0.0
 */
public interface MyOrderService {

    Map<String, String> create(CreateOrder order, Integer count);

    Map<String, String> create2(CreateOrder order, Integer count);

}
