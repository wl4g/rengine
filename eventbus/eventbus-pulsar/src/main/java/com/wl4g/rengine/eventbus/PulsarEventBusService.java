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
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.interceptor.ProducerInterceptor;

import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.eventbus.PulsarEventBusService.ProducerResult;
import com.wl4g.rengine.eventbus.config.ClientEventBusConfig;
import com.wl4g.rengine.eventbus.config.ClientEventBusConfig.PulsarEventBusConfig;
import com.wl4g.rengine.eventbus.recorder.EventRecorder;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * {@link PulsarEventBusService}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-05-30 v3.0.0
 * @since v1.0.0
 * @see https://pulsar.apache.org/docs/client-libraries-java#connection-urls
 * @see https://github.com/wl4g-collect/Pulsar-analysis/blob/master/pulsar-client.md
 * @see 待发送队列源码分析:https://github1s.com/apache/pulsar/blob/branch-2.11/pulsar-client/src/main/java/org/apache/pulsar/client/impl/ProducerImpl.java#L1938-L1939
 */
@Getter
public class PulsarEventBusService extends AbstractEventBusService<Future<ProducerResult>> implements Closeable {
    private final PulsarEventBusConfig pulsarConfig;
    private final PulsarClient client;
    private final Producer<RengineEvent> producer;

    public PulsarEventBusService(ClientEventBusConfig config, EventRecorder recorder) {
        super(config, recorder);
        try {
            // see:https://pulsar.apache.org/docs/client-libraries-java#connection-urls
            // see:https://pulsar.apache.org/docs/client-libraries-java#how-to-use-cluster-level-failover
            this.pulsarConfig = config.getPulsar();
            this.client = PulsarClient.builder()
                    .serviceUrl(pulsarConfig.getServiceUrl())
                    .connectionTimeout((int) pulsarConfig.getConnectionTimeout().toMillis(), TimeUnit.MILLISECONDS)
                    .connectionsPerBroker(pulsarConfig.getConnectionsPerBroker())
                    .lookupTimeout((int) pulsarConfig.getLookupTimeout().toMillis(), TimeUnit.MILLISECONDS)
                    .operationTimeout((int) pulsarConfig.getOperationTimeout().toMillis(), TimeUnit.MILLISECONDS)
                    .startingBackoffInterval((int) pulsarConfig.getStartingBackoffInterval().toMillis(), TimeUnit.MILLISECONDS)
                    .keepAliveInterval((int) pulsarConfig.getKeepAliveInterval().toMillis(), TimeUnit.MILLISECONDS)
                    .listenerThreads(pulsarConfig.getListenerThreads())
                    .ioThreads(pulsarConfig.getIoThreads())
                    .allowTlsInsecureConnection(pulsarConfig.isAllowTlsInsecureConnection())
                    .enableTlsHostnameVerification(pulsarConfig.isEnableTlsHostnameVerification())
                    .enableTcpNoDelay(pulsarConfig.isEnableTcpNoDelay())
                    .enableTransaction(pulsarConfig.isEnableTransaction())
                    .maxBackoffInterval(pulsarConfig.getMaxBackoffInterval().toNanos(), TimeUnit.NANOSECONDS)
                    .maxConcurrentLookupRequests(pulsarConfig.getMaxConcurrentLookupRequests())
                    .maxLookupRedirects(pulsarConfig.getMaxLookupRedirects())
                    .maxLookupRequests(pulsarConfig.getMaxLookupRequests())
                    .maxNumberOfRejectedRequestPerConnection(pulsarConfig.getMaxNumberOfRejectedRequestPerConnection())
                    .build();

            this.producer = client.newProducer(new RengineEventSchema())
                    .topic(config.getTopic())
                    .enableChunking(pulsarConfig.isEnableChunking())
                    .enableBatching(pulsarConfig.isEnableBatching())
                    // 待发送队列源码分析:https://github1s.com/apache/pulsar/blob/branch-2.11/pulsar-client/src/main/java/org/apache/pulsar/client/impl/ProducerImpl.java#L1938-L1939
                    .intercept(new ProducerInterceptor() {

                        @Override
                        public void close() {
                        }

                        @SuppressWarnings("rawtypes")
                        @Override
                        public boolean eligible(Message message) {
                            return true; // process all messages
                        }

                        @SuppressWarnings("rawtypes")
                        @Override
                        public Message beforeSend(Producer producer, Message message) {
                            return message;
                        }

                        @SuppressWarnings("rawtypes")
                        @Override
                        public void onSendAcknowledgement(
                                Producer producer,
                                Message message,
                                MessageId msgId,
                                Throwable exception) {
                            // When it is confirmed that the broker has received
                            // it, the setting of the local recorder is
                            // completed.
                            RengineEvent event = (RengineEvent) message.getValue();
                            log.debug("Complete acknowledgement event: {}", event);
                            recorder.completed(singletonList(event));
                        }
                    })
                    .create();

        } catch (PulsarClientException e) {
            throw new IllegalStateException("Could't to initial pulsar client.", e);
        }
    }

    @Override
    public Object getOriginal() {
        return client;
    }

    @Override
    public void close() throws IOException {
        if (nonNull(producer)) {
            try {
                producer.closeAsync().orTimeout(pulsarConfig.getClosingTimeout().toMillis(), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.warn("Unable to closing pulsar producer.", e);
            }
        }
        if (nonNull(client) && !client.isClosed()) {
            try {
                client.closeAsync().orTimeout(pulsarConfig.getClosingTimeout().toMillis(), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.warn("Unable to closing pulsar client.", e);
            }
        }
    }

    @Override
    public List<Future<ProducerResult>> doPublish(final List<RengineEvent> events) throws Exception {
        log.debug("Sending : {}", events);

        List<Future<ProducerResult>> results = new ArrayList<>(events.size());
        // The call sends asynchronously, it returns immediately and
        // consumes almost no time, so it is faster to use a serial stream
        // instead of a parallel stream.
        safeList(events)/* .parallelStream(). */.stream().forEach(event -> {
            CompletableFuture<MessageId> future = producer.newMessage().value(event).key(event.getId()).sendAsync();
            results.add(new ProducerFuture(future, event));
        });

        return results;
    }

    @Getter
    @AllArgsConstructor
    public static class ProducerFuture implements Future<ProducerResult> {
        private Future<MessageId> future;
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
            MessageId messageId = future.get();
            if (nonNull(messageId)) {
                return new ProducerResult(messageId, event);
            }
            return null;
        }

        @Override
        public ProducerResult get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            MessageId messageId = future.get(timeout, unit);
            if (nonNull(messageId)) {
                return new ProducerResult(messageId, event);
            }
            return null;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ProducerResult {
        private MessageId messageId;
        private RengineEvent event;
    }

}
