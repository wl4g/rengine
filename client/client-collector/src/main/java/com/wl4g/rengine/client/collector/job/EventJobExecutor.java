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
import static java.lang.System.currentTimeMillis;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.ElasticJob;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;
import org.apache.shardingsphere.elasticjob.executor.item.impl.TypedJobItemExecutor;

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
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@Getter
public abstract class EventJobExecutor<P extends EventJobExecutor.JobParamBase> implements TypedJobItemExecutor {

    protected final CollectorProperties config;

    @SuppressWarnings("rawtypes")
    protected final Collection<RengineEventBusService> eventbusServices;

    public EventJobExecutor() {
        this.config = SpringContextHolder.getBean(CollectorProperties.class);
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

        List<JobParamBase> shardingParams = new ArrayList<>();
        List<? extends JobParamBase> params = safeList(jobConfig.getStaticParams());
        for (int i = 0; i < params.size(); i++) {
            if (i % context.getShardingTotalCount() == context.getShardingItem()) {
                shardingParams.add(params.get(i));
            }
        }

        // The parallel execution jobs.
        shardingParams.parallelStream().forEach(p -> execute((P) p, jobConfig, jobFacade, context));
    }

    protected abstract void execute(P param, JobConfiguration jobConfig, JobFacade jobFacade, ShardingContext context);

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
            String result) {

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

        // Offer to event-bus channel.
        safeList(eventbusServices).forEach(eventbus -> eventbus.publish(event));
    }

    protected abstract EventJobType type();

    protected String getEventType(P shardingParam, JobConfiguration jobConfig, ShardingContext shardingContext) {
        return getType();
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

    protected abstract List<String> getPrincipals(P shardingParam, JobConfiguration jobConfig, ShardingContext shardingContext);

    protected abstract EventLocation getEventLocation(
            P shardingParam,
            JobConfiguration jobConfig,
            ShardingContext shardingContext);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Map<String, String> getEventAttributes(
            P shardingParam,
            JobConfiguration jobConfig,
            ShardingContext shardingContext) {
        // return
        // ConfigUtils.parseToMap(jobConfig.getProps().getProperty(EVENT_ATTRIBUTES_KEY));
        return (Map) jobConfig.getProps();
    }

    public static interface BodyConverter {
        String convert(String fromResult);
    }

    @Getter
    @AllArgsConstructor
    public static enum EventJobType {

        SIMPLE_HTTP(SimpleHttpScrapeJobProperties.class, SimpleHttpEventJobExecutor.class),

        // TODO Notice: For example, PrometheusEventJobExecutor inherits the
        // SimpleHttpEventJobExecutor and is temporarily treated as the
        // latter class.
        PROMETHEUS(SimpleHttpScrapeJobProperties.class, PrometheusEventJobExecutor.class),

        SIMPLE_JDBC(SimpleJdbcScrapeJobProperties.class, SimpleJdbcEventJobExecutor.class),

        SIMPLE_REDIS(SimpleRedisScrapeJobProperties.class, SimpleRedisEventJobExecutor.class),

        SIMPLE_TCP(SimpleTcpScrapeJobProperties.class, SimpleTcpEventJobExecutor.class),

        SSH(SSHScrapeJobProperties.class, SSHEventJobExecutor.class);

        private final Class<? extends ScrapeJobProperties> jobConfigClass;
        private final Class<? extends EventJobExecutor<? extends JobParamBase>> jobClass;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public abstract static class JobParamBase {

        /**
         * The collect parameter for name.
         */
        private @Nullable String name;

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
