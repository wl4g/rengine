//package com.wl4g.rengine.controller.job.listener;
//
//import org.apache.shardingsphere.elasticjob.infra.listener.ElasticJobListener;
//import org.apache.shardingsphere.elasticjob.infra.listener.ShardingContexts;
//
//import com.wl4g.rengine.controller.job.AbstractJobExecutor.ScheduleJobType;
//
///**
// * {@link MyLocalJobListener}
// * 
// * @author James Wong
// * @version 2023-02-05
// * @since v1.0.0
// * @see https://shardingsphere.apache.org/elasticjob/current/en/user-manual/elasticjob-lite/usage/job-listener/listener-interface/
// */
//public class MyLocalJobListener implements ElasticJobListener {
//
//    @Override
//    public String getType() {
//        return ScheduleJobType.GLOBAL_ENGINE_CONTROLLER.name();
//    }
//
//    @Override
//    public void beforeJobExecuted(ShardingContexts shardingContexts) {
//        // do something ...
//    }
//
//    @Override
//    public void afterJobExecuted(ShardingContexts shardingContexts) {
//        // do something ...
//    }
//
//}