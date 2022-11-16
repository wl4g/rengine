/*
 * Copyright 2017 ~ 2025 the original authors James Wong.
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
package com.wl4g.rengine.eventbus;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.serialization.StringSerializer;

import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.eventbus.KafkaEventBusService.ProducerResult;
import com.wl4g.rengine.eventbus.config.ClientEventBusConfig;
import com.wl4g.rengine.eventbus.recorder.EventRecorder;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * {@link LoggingEventBusService}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-05-30 v3.0.0
 * @since v1.0.0
 */
@Getter
public class KafkaEventBusService extends AbstractEventBusService<Future<ProducerResult>> implements Closeable {

    private final KafkaProducer<String, String> kafkaProducer;

    public KafkaEventBusService(ClientEventBusConfig config, EventRecorder recorder) {
        super(config, recorder);
        this.kafkaProducer = new KafkaProducer<>(config.getKafka().getProperties(), new StringSerializer(),
                new StringSerializer());
    }

    @Override
    public Object getOriginal() {
        return kafkaProducer;
    }

    public Map<MetricName, ? extends Metric> metrics() {
        return kafkaProducer.metrics();
    }

    @Override
    public void close() throws IOException {
        if (nonNull(kafkaProducer)) {
            try {
                kafkaProducer.close(config.getKafka().getClosingTimeout());
            } catch (Exception e) {
                log.warn("Unable to closing kafka producer.", e);
            }
        }
    }

    @Override
    public List<Future<ProducerResult>> doPublish(final List<RengineEvent> events) throws Exception {
        List<Future<ProducerResult>> results = new ArrayList<>(events.size());

        safeList(events).parallelStream().forEach(event -> {
            ProducerRecord<String, String> record = new ProducerRecord<>(config.getTopic(), toJSONString(event));
            log.debug("Sending : {}", record);
            Future<RecordMetadata> future = kafkaProducer.send(record, (metadata, exception) -> {
                if (isNull(exception)) {
                    recorder.completed(singletonList(event));
                }
            });
            results.add(new ProducerFuture(future, event));
        });

        return results;
    }

    @Getter
    @AllArgsConstructor
    public static class ProducerFuture implements Future<ProducerResult> {
        private Future<RecordMetadata> future;
        private RengineEvent event;

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return future.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return future.isCancelled();
        }

        @Override
        public boolean isDone() {
            return future.isDone();
        }

        @Override
        public ProducerResult get() throws InterruptedException, ExecutionException {
            RecordMetadata recordMetadata = future.get();
            if (nonNull(recordMetadata)) {
                return new ProducerResult(recordMetadata, event);
            }
            return null;
        }

        @Override
        public ProducerResult get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            RecordMetadata recordMetadata = future.get(timeout, unit);
            if (nonNull(recordMetadata)) {
                return new ProducerResult(recordMetadata, event);
            }
            return null;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ProducerResult {
        private RecordMetadata recordMetadata;
        private RengineEvent event;
    }

}
