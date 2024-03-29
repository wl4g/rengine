/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wl4g.elasticjob.example.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.dataflow.job.DataflowJob;
import org.springframework.stereotype.Component;

import com.wl4g.elasticjob.example.bean.Foo;
import com.wl4g.elasticjob.example.repository.FooRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SpringBootDataflowJob implements DataflowJob<Foo> {

    @Resource
    private FooRepository fooRepository;

    @Override
    public List<Foo> fetchData(final ShardingContext shardingContext) {
        log.info("Item: {} | Time: {} | Thread: {} | {}", shardingContext.getShardingItem(),
                new SimpleDateFormat("HH:mm:ss").format(new Date()), Thread.currentThread().getId(), "DATAFLOW FETCH");
        return fooRepository.findTodoData(shardingContext.getShardingParameter(), 10);
    }

    @Override
    public void processData(final ShardingContext shardingContext, final List<Foo> data) {
        log.info("Item: {} | Time: {} | Thread: {} | {}", shardingContext.getShardingItem(),
                new SimpleDateFormat("HH:mm:ss").format(new Date()), Thread.currentThread().getId(), "DATAFLOW PROCESS");
        for (Foo each : data) {
            fooRepository.setCompleted(each.getId());
        }
    }
}
