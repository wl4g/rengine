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
 * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.eventbus;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.eventbus.RabbitmqEventBusService.ProducerResult;
import com.wl4g.rengine.eventbus.config.ClientEventBusConfig;
import com.wl4g.rengine.eventbus.recorder.EventRecorder;

import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link RabbitmqEventBusService}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-05-30 v3.0.0
 * @since v1.0.0
 */
@CustomLog
@Getter
public class RabbitmqEventBusService extends AbstractEventBusService<ProducerResult> implements Closeable {

    private final ClientEventBusConfig eventBusConfig;
    private final ConnectionFactory factory;

    public RabbitmqEventBusService(ClientEventBusConfig eventBusConfig, EventRecorder recorder) {
        super(eventBusConfig, recorder);
        this.eventBusConfig = notNullOf(eventBusConfig, "eventBusConfig");
        this.factory = new ConnectionFactory();
        this.factory.load(eventBusConfig.getRabbitmq().getProperties(), "");
    }

    @Override
    public Object getOriginal() {
        return factory;
    }

    @Override
    public void close() throws IOException {
        if (nonNull(factory)) {
            try {
                factory.clone();
            } catch (Exception e) {
                log.warn("Unable to closing rabbitmq connection factory.", e);
            }
        }
    }

    @Override
    public List<ProducerResult> doPublish(final List<RengineEvent> events) throws Exception {
        try (Connection connection = factory.newConnection();) {
            log.debug("Sending : {}", toJSONString(events));

            List<ProducerResult> results = new ArrayList<>(events.size());
            safeList(events)/* .parallelStream() */.forEach(event -> {
                try (Channel channel = connection.createChannel();) {
                    /* Queue.DeclareOk ok = */ channel.queueDeclare(eventBusConfig.getTopic(), true, true, false, emptyMap());
                    channel.addReturnListener(reply -> {
                        // see:https://www.rabbitmq.com/amqp-0-9-1-reference.html#domain.reply-code
                        if (reply.getReplyCode() == AMQP.REPLY_SUCCESS) {
                            recorder.completed(singletonList(event));
                        }
                        results.add(new ProducerResult(reply, event));
                    });
                } catch (Exception e) {
                    log.warn(format("Unable to send event. - %s", event), e);
                }
            });

            return results;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ProducerResult {
        private Object recordMetadata;
        private RengineEvent event;
    }

}
