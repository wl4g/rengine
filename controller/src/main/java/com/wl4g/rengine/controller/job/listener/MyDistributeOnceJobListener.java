///*
// * Copyright 2017 ~ 2025 the original author or authors. James Wong <jameswong1376@gmail.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.wl4g.rengine.controller.job.listener;
//
//import org.apache.shardingsphere.elasticjob.infra.listener.ShardingContexts;
//import org.apache.shardingsphere.elasticjob.lite.api.listener.AbstractDistributeOnceElasticJobListener;
//
//import com.wl4g.rengine.controller.job.AbstractJobExecutor.ControllerType;
//
///**
// * {@link MyDistributeOnceJobListener}
// * 
// * @author James Wong
// * @date 2023-02-05
// * @since v1.0.0
// * @see https://shardingsphere.apache.org/elasticjob/current/en/user-manual/elasticjob-lite/usage/job-listener/listener-interface/
// */
//public class MyDistributeOnceJobListener extends AbstractDistributeOnceElasticJobListener {
//
//    public MyDistributeOnceJobListener(long startTimeoutMills, long completeTimeoutMills) {
//        super(startTimeoutMills, completeTimeoutMills);
//    }
//
//    @Override
//    public String getType() {
//        return ControllerType.GLOBAL_BOOTSTRAPER.name();
//    }
//
//    @Override
//    public void doBeforeJobExecutedAtLastStarted(ShardingContexts shardingContexts) {
//        // do something ...
//    }
//
//    @Override
//    public void doAfterJobExecutedAtLastCompleted(ShardingContexts shardingContexts) {
//        // do something ...
//    }
//
//}
