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

import static java.util.Objects.nonNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.eventbus.PulsarEventBusService.ProducerResult;
import com.wl4g.rengine.eventbus.config.ClientEventBusConfig;
import com.wl4g.rengine.eventbus.recorder.EventRecorder;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * {@link PulsarEventBusService}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-05-30 v3.0.0
 * @since v1.0.0
 */
@Getter
public class PulsarEventBusService extends AbstractEventBusService<ProducerResult> implements Closeable {

    // TODO
    // private final PulsarProducer<String, String> pulsarProducer;

    public PulsarEventBusService(ClientEventBusConfig eventBusConfig, EventRecorder recorder) {
        super(eventBusConfig, recorder);
        // TODO
        // this.pulsarProducer = new
        // PulsarProducer<>(eventBusConfig.getPulsar().getProperties());
    }

    @Override
    public Object getOriginal() {
        // TODO
        // return pulsarProducer;
        return null;
    }

    @Override
    public void close() throws IOException {
        // TODO
        // if (nonNull(pulsarProducer)) {
        // try {
        // pulsarProducer.close(eventBusConfig.getPulsar().getClosingTimeout());
        // } catch (Exception e) {
        // log.warn("Unable to closing pulsar producer.", e);
        // }
        // }
    }

    @Override
    public List<Future<ProducerResult>> doPublish(final List<RengineEvent> events) {
        // TODO
        // ProducerRecord<String, String> record = new
        // ProducerRecord<>(eventBusConfig.getEventTopic(),
        // toJSONString(events));
        // log.debug("Sending : {}", record);
        //
        // List<Future<ProducerResult>> results = new
        // ArrayList<>(events.size());
        // safeList(events).parallelStream().forEach(event -> {
        // Future<RecordMetadata> future = pulsarProducer.send(record,
        // (metadata, exception) -> {
        // if (isNull(exception)) {
        // recorder.addCompleted(singletonList(event));
        // }
        // });
        // results.add(new ProducerFuture(future, event));
        // });
        //
        // return results;
        return null;
    }

    @Getter
    @AllArgsConstructor
    public static class ProducerFuture implements Future<ProducerResult> {
        // TODO
        // private Future<RecordMetadata> future;
        private Future<Object> future;
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
            // TODO
            // RecordMetadata recordMetadata = future.get();
            Object recordMetadata = future.get();
            if (nonNull(recordMetadata)) {
                return new ProducerResult(recordMetadata, event);
            }
            return null;
        }

        @Override
        public ProducerResult get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            // TODO
            // RecordMetadata recordMetadata = future.get(timeout, unit);
            Object recordMetadata = future.get(timeout, unit);
            if (nonNull(recordMetadata)) {
                return new ProducerResult(recordMetadata, event);
            }
            return null;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ProducerResult {
        // TODO
        // private RecordMetadata recordMetadata;
        private Object recordMetadata;
        private RengineEvent event;
    }

}
