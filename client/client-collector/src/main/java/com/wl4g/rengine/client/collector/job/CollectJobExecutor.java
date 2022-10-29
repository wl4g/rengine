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
package com.wl4g.rengine.client.collector.job;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.ElasticJob;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;
import org.apache.shardingsphere.elasticjob.executor.item.impl.TypedJobItemExecutor;
import org.apache.shardingsphere.elasticjob.lite.internal.storage.JobNodePath;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;

import com.wl4g.infra.common.log.SmartLogger;
import com.wl4g.infra.common.log.SmartLoggerFactory;
import com.wl4g.infra.context.utils.SpringContextHolder;
import com.wl4g.rengine.client.collector.config.CollectorProperties;
import com.wl4g.rengine.client.collector.config.CollectorProperties.SSHScrapeJobProperties;
import com.wl4g.rengine.client.collector.config.CollectorProperties.ScrapeJobProperties;
import com.wl4g.rengine.client.collector.config.CollectorProperties.SimpleHttpScrapeJobProperties;
import com.wl4g.rengine.client.collector.config.CollectorProperties.SimpleJdbcScrapeJobProperties;
import com.wl4g.rengine.client.collector.config.CollectorProperties.SimpleRedisScrapeJobProperties;
import com.wl4g.rengine.client.collector.config.CollectorProperties.SimpleTcpScrapeJobProperties;
import com.wl4g.rengine.client.eventbus.RengineEventBusService;
import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.common.event.RengineEvent.EventLocation;
import com.wl4g.rengine.common.event.RengineEvent.EventSource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The job abstract executor that actively collects time for scraping events.
 * scrape jobs configuration similar to prometheus. </br>
 * </br>
 * Distributed automatic fragmentation scraping task based on zookeeper, more
 * smart than prometheus.
 * 
 * @author James Wong
 * @version 2022-10-20
 * @since v1.0.0
 */
@Getter
public abstract class CollectJobExecutor<P extends CollectJobExecutor.JobParamBase> implements TypedJobItemExecutor {
    protected final SmartLogger log = SmartLoggerFactory.getLogger(getClass());
    protected final CollectorProperties config;
    protected final CoordinatorRegistryCenter regCenter;
    @SuppressWarnings("rawtypes")
    protected final Collection<RengineEventBusService> eventbusServices;

    public CollectJobExecutor() {
        this.config = SpringContextHolder.getBean(CollectorProperties.class);
        this.regCenter = SpringContextHolder.getBean(CoordinatorRegistryCenter.class);
        this.eventbusServices = safeMap(SpringContextHolder.getBeans(RengineEventBusService.class)).values();
    }

    @Override
    public String getType() {
        notNull(type(), "The event job type is not allowed to be empty and must be implemented. - ", this);
        return type().name();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(ElasticJob elasticJob, JobConfiguration jobConfig, JobFacade jobFacade, ShardingContext context) {
        log.info("ShardingContext: {}", toJSONString(context));

        final int currentShardingTotalCount = determineShardingTotalCount(elasticJob, jobConfig, jobFacade, context);
        final List<JobParamBase> shardingParams = new ArrayList<>();
        final List<? extends JobParamBase> params = safeList(jobConfig.getJobParams());
        for (int i = 0; i < params.size(); i++) {
            if (i % currentShardingTotalCount == context.getShardingItem()) {
                shardingParams.add(params.get(i));
            }
        }

        // The parallel execution jobs.
        shardingParams.parallelStream().forEach(p -> {
            try {
                execute((P) p, currentShardingTotalCount, jobConfig, jobFacade, context);
            } catch (Exception e) {
                log.error(format("Failed to collect job execution. - %s", p), e);
            }
        });
    }

    protected abstract void execute(
            P param,
            int currentShardingTotalCount,
            JobConfiguration jobConfig,
            JobFacade jobFacade,
            ShardingContext context) throws Exception;

    /**
     * Offer result data to event-bus channel.
     * 
     * @param shardingParam
     * @param jobConfig
     * @param jobFacade
     * @param shardingContext
     * @param result
     */
    protected void offer(
            P shardingParam,
            JobConfiguration jobConfig,
            JobFacade jobFacade,
            ShardingContext shardingContext,
            Object result) {

        // Transform to event.
        RengineEvent event = new RengineEvent(getEventType(shardingParam, jobConfig, shardingContext),
                getObservedTime(shardingParam, jobConfig, shardingContext),
                EventSource.builder()
                        .time(getEventTime(shardingParam, jobConfig, shardingContext))
                        .principals(getPrincipals(shardingParam, jobConfig, shardingContext))
                        .location(getEventLocation(shardingParam, jobConfig, shardingContext))
                        .build(),
                getBodyConverter(shardingParam, jobConfig, shardingContext).convert(result),
                getEventAttributes(shardingParam, jobConfig, shardingContext));
        event.validate();

        // Offer to event-bus channel.
        safeList(eventbusServices).forEach(eventbus -> eventbus.publish(event));
    }

    protected int determineShardingTotalCount(
            ElasticJob elasticJob,
            JobConfiguration jobConfig,
            JobFacade jobFacade,
            ShardingContext context) {

        // When setup true, the shardingTotalCount will be ignored, and the will
        // be automatically allocated according to the number of cluster nodes
        // priority.
        if (nonNull(jobConfig.isAutoShardingTotalCount()) && !jobConfig.isAutoShardingTotalCount()) {
            return jobConfig.getShardingTotalCount();
        }

        // It is dynamically calculated according to the number of cluster
        // nodes.
        /*
         * Only the instance path indicates the current number of online nodes
         * (temporary), and the service path indicates the cumulative number of
         * online + offline nodes (permanent). for example:
         * /rengine/node-exporter-job/instances => [10.0.0.114@-@195117]
         */
        List<String> serverNames = regCenter.getChildrenKeys(new JobNodePath(jobConfig.getJobName()).getInstancesNodePath());
        int shardingTotalCount = (nonNull(serverNames) && serverNames.size() > 0) ? serverNames.size()
                : jobConfig.getShardingTotalCount();

        log.debug("Assigned the shards dynamic accroding to cluster nodes: {}", shardingTotalCount);
        return shardingTotalCount;
    }

    protected abstract EventJobType type();

    protected String getEventType(P shardingParam, JobConfiguration jobConfig, ShardingContext shardingContext) {
        return isBlank(jobConfig.getEventType()) ? getType() : jobConfig.getEventType();
    }

    protected long getObservedTime(P shardingParam, JobConfiguration jobConfig, ShardingContext shardingContext) {
        return currentTimeMillis();
    }

    protected abstract BodyConverter getBodyConverter(
            P shardingParam,
            JobConfiguration jobConfig,
            ShardingContext shardingContext);

    protected long getEventTime(P shardingParam, JobConfiguration jobConfig, ShardingContext shardingContext) {
        return currentTimeMillis();
    }

    protected List<String> getPrincipals(P shardingParam, JobConfiguration jobConfig, ShardingContext shardingContext) {
        return singletonList(shardingParam.getName());
    }

    protected EventLocation getEventLocation(P shardingParam, JobConfiguration jobConfig, ShardingContext shardingContext) {
        return EventLocation.builder().build();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Map<String, String> getEventAttributes(
            P shardingParam,
            JobConfiguration jobConfig,
            ShardingContext shardingContext) {
        // return
        // ParamsUtils.parseToMap(jobConfig.getProps().getProperty(EVENT_ATTRIBUTES_KEY));
        return (Map) jobConfig.getProps();
    }

    public static interface BodyConverter {
        String convert(Object fromResult);

        public static final BodyConverter DEFAULT_STRING = fromResult -> fromResult.toString();
    }

    @Getter
    @AllArgsConstructor
    public static enum EventJobType {

        SIMPLE_HTTP(SimpleHttpScrapeJobProperties.class, SimpleHttpCollectJobExecutor.class),

        // TODO Notice: For example, PrometheusCollectJobExecutor inherits the
        // SimpleHttpCollectJobExecutor and is temporarily treated as the
        // latter class.
        PROMETHEUS(SimpleHttpScrapeJobProperties.class, PrometheusCollectJobExecutor.class),

        SIMPLE_JDBC(SimpleJdbcScrapeJobProperties.class, SimpleJdbcCollectJobExecutor.class),

        SIMPLE_REDIS(SimpleRedisScrapeJobProperties.class, SimpleRedisCollectJobExecutor.class),

        SIMPLE_TCP(SimpleTcpScrapeJobProperties.class, SimpleTcpCollectJobExecutor.class),

        SSH(SSHScrapeJobProperties.class, SSHCollectJobExecutor.class);

        private final Class<? extends ScrapeJobProperties<? extends JobParamBase>> jobConfigClass;
        private final Class<? extends CollectJobExecutor<? extends JobParamBase>> jobClass;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public abstract static class JobParamBase {

        /**
         * The collect parameter for name.
         */
        private @NotBlank String name;

        /**
         * The collect parameter for connect timeout.
         */
        private @NotNull long connectTimeoutMs = Duration.ofSeconds(3).toMillis();

        /**
         * The collect parameter for read timeout.
         */
        private @NotNull long readTimeoutMs = Duration.ofSeconds(10).toMillis();
    }

}
