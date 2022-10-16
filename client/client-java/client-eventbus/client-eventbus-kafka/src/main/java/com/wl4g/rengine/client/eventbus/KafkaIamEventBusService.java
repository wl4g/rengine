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
package com.wl4g.rengine.client.eventbus;

import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;

import java.util.Map;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import com.wl4g.rengine.client.eventbus.config.RengineEventBusProperties;
import com.wl4g.rengine.client.eventbus.config.KafkaEventBusProperties;
import com.wl4g.rengine.common.event.RengineEvent;

import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link LoggingRengineEventBusService}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-05-30 v3.0.0
 * @since v1.0.0
 */
@Getter
@CustomLog
@AllArgsConstructor
public class KafkaIamEventBusService implements RengineEventBusService<SendResult<String, String>>, InitializingBean {

    private final RengineEventBusProperties eventBusConfig;
    private final KafkaEventBusProperties kafkaEventBusConfig;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        kafkaTemplate.setMicrometerEnabled(true);
    }

    @Override
    public Object getOriginal() {
        return kafkaTemplate;
    }

    public Map<MetricName, ? extends Metric> metrics() {
        return kafkaTemplate.metrics();
    }

    @Override
    public Future<SendResult<String, String>> publish(RengineEvent event) {
        ProducerRecord<String, String> record = new ProducerRecord<>(eventBusConfig.getEventTopic(), toJSONString(event));
        log.debug("Send: {}", record);
        return kafkaTemplate.send(record);
    }

}
