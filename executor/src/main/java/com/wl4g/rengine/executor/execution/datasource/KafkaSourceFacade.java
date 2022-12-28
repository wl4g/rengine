/**
 * Copyright 2017 ~ 2025 the original author or authors. James Wong <jameswong1376@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.executor.execution.datasource;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_datasource_facade_failure;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_datasource_facade_success;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_datasource_facade_total;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.wl4g.rengine.common.entity.DataSourceProperties.DataSourcePropertiesBase;
import com.wl4g.rengine.common.entity.DataSourceProperties.DataSourceType;
import com.wl4g.rengine.common.entity.DataSourceProperties.KafkaDataSourceProperties;
import com.wl4g.rengine.executor.execution.ExecutionConfig;
import com.wl4g.rengine.executor.metrics.MeterUtil;

import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link KafkaSourceFacade}
 *
 * @author James Wong
 * @version 2022-10-10
 * @since v1.0.0
 */
@Getter
@CustomLog
@AllArgsConstructor
public class KafkaSourceFacade implements DataSourceFacade {

    final static String METHOD_PUBLISH = "publish";

    final ExecutionConfig executionConfig;
    final String dataSourceName;
    final KafkaProducer<String, String> kafkaProducer;

    @Override
    public void close() throws IOException {
        if (nonNull(kafkaProducer)) {
            log.info("Closing to kafka data source for {} ...", dataSourceName);
            kafkaProducer.close();
        }
    }

    public void publish(final @NotBlank String topic, final @NotEmpty Map<String, Object> record) {
        publish(topic, singletonList(record));
    }

    public void publish(final @NotBlank String topic, final @NotEmpty List<Map<String, Object>> records) {
        hasTextOf(topic, "topic");
        notEmptyOf(records, "records");
        MeterUtil.counter(execution_datasource_facade_total, dataSourceName, DataSourceType.KAFKA, METHOD_PUBLISH);

        records.forEach(r -> {
            final ProducerRecord<String, String> record = new ProducerRecord<>(topic, toJSONString(r));
            kafkaProducer.send(record, (metadata, ex) -> {
                // TODO local padding storage process
                if (nonNull(ex)) {
                    log.warn(format("Failed to publish to kafka of {}", r), ex);
                    MeterUtil.counter(execution_datasource_facade_failure, dataSourceName, DataSourceType.KAFKA, METHOD_PUBLISH);
                } else {
                    log.debug(format("Published to kafka of {}", r));
                    MeterUtil.counter(execution_datasource_facade_success, dataSourceName, DataSourceType.KAFKA, METHOD_PUBLISH);
                }
            });
        });
    }

    @Singleton
    public static class KafkaSourceFacadeBuilder implements DataSourceFacadeBuilder {

        @Override
        public DataSourceFacade newInstnace(
                final @NotNull ExecutionConfig config,
                final @NotBlank String dataSourceName,
                final @NotNull DataSourcePropertiesBase dataSourceProperties) {
            notNullOf(config, "properties");
            hasTextOf(dataSourceName, "dataSourceName");

            final Map<String, Object> configMap = ((KafkaDataSourceProperties) dataSourceProperties).toConfigMap();
            return new KafkaSourceFacade(config, dataSourceName, new KafkaProducer<>(configMap));
        }

        @Override
        public DataSourceType type() {
            return DataSourceType.KAFKA;
        }
    }

}
