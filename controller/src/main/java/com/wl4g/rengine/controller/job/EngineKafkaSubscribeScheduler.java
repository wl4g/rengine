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
package com.wl4g.rengine.controller.job;

import static com.wl4g.infra.common.collection.CollectionUtils2.ensureMap;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.BatchAcknowledgingMessageListener;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties.AckMode;

import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.wl4g.rengine.client.core.RengineClient;
import com.wl4g.rengine.common.entity.ScheduleJobLog;
import com.wl4g.rengine.common.entity.ScheduleJobLog.KafkaSubscribeScheduleJobLog;
import com.wl4g.rengine.common.entity.ScheduleJobLog.ResultInformation;
import com.wl4g.rengine.common.entity.ScheduleTrigger;
import com.wl4g.rengine.common.entity.ScheduleTrigger.KafkaSubscribeScheduleConfig;
import com.wl4g.rengine.common.entity.ScheduleTrigger.RunState;
import com.wl4g.rengine.common.entity.ScheduleTrigger.ScheduleType;
import com.wl4g.rengine.common.model.ExecuteRequest;
import com.wl4g.rengine.controller.lifecycle.ElasticJobBootstrapBuilder.JobParameter;

import lombok.CustomLog;
import lombok.Getter;
import lombok.ToString;

/**
 * {@link EngineKafkaSubscribeScheduler}
 * 
 * @author James Wong
 * @version 2023-01-11
 * @since v1.0.0
 */
@CustomLog
public class EngineKafkaSubscribeScheduler extends EngineExecutionScheduler {

    private static final Map<Long, ConcurrentMessageListenerContainer<String, String>> subscriberRegistry = new ConcurrentHashMap<>(
            16);

    @Override
    public String getType() {
        return ScheduleJobType.KAFKA_SUBSCRIBE_SCHEDULER.name();
    }

    @Override
    public void close() throws IOException {
        super.close();
        subscriberRegistry.entrySet().forEach(e -> {
            try {
                e.getValue().stop();
            } catch (Throwable ex) {
                log.warn(format("Unable to closing subscriber for triggerId: %s", e.getKey()), ex);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void execute(
            int currentShardingTotalCount,
            @NotNull JobConfiguration jobConfig,
            @NotNull JobFacade jobFacade,
            @NotNull ShardingContext context) throws Exception {

        final JobParameter jobParameter = notNullOf(parseJSON(jobConfig.getJobParameter(), JobParameter.class), "jobParameter");
        final Long triggerId = notNullOf(jobParameter.getTriggerId(), "triggerId");

        updateTriggerRunState(triggerId, RunState.RUNNING);
        final ScheduleJobLog jobLog = upsertSchedulingLog(triggerId, null, true, false, null, null);

        final ScheduleTrigger trigger = notNullOf(getScheduleTriggerService().get(triggerId), "trigger");
        try {
            log.info("Consuming kafka to engine execution scheduling for : {}", trigger);

            if (subscriberRegistry.containsKey(trigger.getId())) {
                log.warn(format("Registered kafka subscribe to execution job for %s", trigger.getId()));
                return;
            }

            // Register to subscriber registry.
            final KafkaSubscribeScheduleConfig kssc = ((KafkaSubscribeScheduleConfig) trigger.getProperties()).validate();
            final ConcurrentMessageListenerContainer<String, String> subscriber = new KafkaSubscribeController(kssc.toConfigMap())
                    .buildSubscriber(kssc.getTopics(), generateGroupId(trigger), kssc.getConcurrency(),
                            (records, acknowledgment) -> {
                                // Build for execution jobs.
                                final List<ExecutionWorker> jobs = singletonList(
                                        new KafkaSubscribeExecutionWorker(currentShardingTotalCount, context, trigger.getId(),
                                                jobLog.getId(), getRengineClient(), kssc.getRequest(), records));

                                // Submit execute requests job wait for
                                // completed
                                final ScheduleJobLog finishedJobLog = doExecuteRequestJobs(trigger, jobLog, jobs,
                                        resultAndJobLog -> {
                                            final Set<ResultInformation> results = (Set<ResultInformation>) resultAndJobLog
                                                    .getItem1();
                                            results.stream().forEach(rd -> rd.validate());
                                            if (!results.isEmpty()) {
                                                final ResultInformation result = results.iterator().next();
                                                final ScheduleJobLog _jobLog = (ScheduleJobLog) resultAndJobLog.getItem2();
                                                ((KafkaSubscribeScheduleJobLog) _jobLog.getDetail()).setResult(result);
                                            }
                                        });

                                // Acknowledgment is only required when
                                // configured in manual acknowledgment mode.
                                if (nonNull(acknowledgment)) {
                                    if (kssc.getAutoAcknowledgment()) {
                                        log.info("Automatically committing acknowledgement of triggerId: {}", trigger.getId());
                                        acknowledgment.acknowledge();
                                    } else if (nonNull(finishedJobLog.getSuccess() && finishedJobLog.getSuccess())) {
                                        log.info("Manual committing acknowledgement of triggerId: {}", trigger.getId());
                                        acknowledgment.acknowledge();
                                    }
                                }
                            });

            subscriberRegistry.put(trigger.getId(), subscriber);
            subscriber.start();

        } catch (Throwable ex) {
            final String errmsg = format(
                    "Failed to executing requests job of currentShardingTotalCount: %s, context: %s, triggerId: %s, jobLogId: %s",
                    currentShardingTotalCount, context, trigger.getId(), jobLog.getId());
            if (log.isDebugEnabled()) {
                log.error(errmsg, ex);
            } else {
                log.error(format("%s. - reason: %s", errmsg, ex.getMessage()));
            }

            updateTriggerRunState(triggerId, RunState.FAILED);
            upsertSchedulingLog(triggerId, jobLog.getId(), false, true, false, null);
        }
    }

    @Override
    protected ScheduleJobLog newDefaultScheduleJobLog(final Long triggerId) {
        return ScheduleJobLog.builder()
                .triggerId(triggerId)
                .detail(KafkaSubscribeScheduleJobLog.builder().type(ScheduleType.KAFKA_SUBSCRIBE_SCHEDULER.name()).build())
                .build();
    }

    public static String generateGroupId(ScheduleTrigger trigger) {
        return EngineKafkaSubscribeScheduler.class.getSimpleName() + "-" + trigger.getId();
    }

    @Getter
    static class KafkaSubscribeController {
        final ConcurrentKafkaListenerContainerFactory<String, String> factory;

        public KafkaSubscribeController(@NotNull Map<String, Object> consumerProps) {
            this.factory = newKafkaListenerContainerFactory(notNullOf(consumerProps, "consumerProps"));
        }

        public ConcurrentMessageListenerContainer<String, String> buildSubscriber(
                final @NotEmpty List<String> topics,
                final @NotBlank String groupId,
                final @NotNull Integer concurrency,
                final @NotNull BatchAcknowledgingMessageListener<String, String> listener) {
            notEmptyOf(topics, "topics");
            hasTextOf(groupId, "groupId");
            notNullOf(listener, "listener");
            // see:org.springframework.kafka.listener.ConcurrentMessageListenerContainer#doStart()
            final ConcurrentMessageListenerContainer<String, String> container = factory
                    .createContainer(topics.toArray(new String[0]));
            // see:org.springframework.kafka.listener.KafkaMessageListenerContainer.ListenerConsumer#doInvokeOnMessage()
            // see:org.springframework.kafka.listener.KafkaMessageListenerContainer.ListenerConsumer#doInvokeBatchOnMessage()
            // see:org.springframework.kafka.listener.BatchMessageListener
            // see:org.springframework.kafka.listener.GenericMessageListener
            // see:org.springframework.kafka.listener.MessageListener
            container.getContainerProperties().setMessageListener(listener);
            container.getContainerProperties().setGroupId(groupId);
            // see:org.springframework.kafka.listener.KafkaMessageListenerContainer.ListenerConsumer#isAnyManualAck
            // see:org.springframework.kafka.listener.KafkaMessageListenerContainer.ListenerConsumer#doInvokeBatchOnMessage
            container.getContainerProperties().setAckMode(AckMode.MANUAL);
            container.setBeanName(groupId.concat("_Bean"));
            container.setConcurrency(concurrency);
            return container;
        }

        /**
         * @see https://docs.spring.io/spring-kafka/reference/html/#with-java-configuration-no-spring-boot
         * @see https://docs.spring.io/spring-kafka/reference/html/#dynamic-containers
         */
        private ConcurrentKafkaListenerContainerFactory<String, String> newKafkaListenerContainerFactory(
                Map<String, Object> consumerProps) {
            consumerProps.putIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            consumerProps.putIfAbsent(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            consumerProps.putIfAbsent(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            consumerProps.putIfAbsent(ConsumerConfig.GROUP_ID_CONFIG, KafkaSubscribeScheduleConfig.DEFAULT_GROUP_ID);
            consumerProps.putIfAbsent(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
            consumerProps.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); // none,latest,earliest
            final ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
            consumerProps = safeMap(consumerProps).entrySet()
                    .stream()
                    .filter(e -> !isBlank(e.getKey()) && nonNull(e.getValue()))
                    .collect(toMap(e -> e.getKey(), e -> e.getValue()));
            factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerProps));
            factory.setBatchListener(true);
            factory.afterPropertiesSet();
            return factory;
        }
    }

    @Getter
    @ToString(callSuper = true)
    public static class KafkaSubscribeExecutionWorker extends ExecutionWorker {
        final List<ConsumerRecord<String, String>> records;

        public KafkaSubscribeExecutionWorker(int currentShardingTotalCount, ShardingContext context, Long triggerId,
                Long jobLogId, RengineClient rengineClient, ExecuteRequest request,
                List<ConsumerRecord<String, String>> records) {
            super(currentShardingTotalCount, context, triggerId, jobLogId, rengineClient, request);
            this.records = notNullOf(records, "records");
        }

        @Override
        protected ExecuteRequest beforeExecution(ExecuteRequest request) {
            final Map<String, Object> args = ensureMap(request.getArgs());
            // Merge all records to args.
            safeList(records).stream().forEach(record -> args.put(record.topic(), record.value()));
            request.setArgs(args);
            return super.beforeExecution(request);
        }
    }

}
