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
package com.wl4g.rengine.job.rabbitmq;

/**
 * {@link RabbitMQUtil}
 * 
 * @author James Wong
 * @version 2022-09-08
 * @since v1.0.0
 */
public abstract class RabbitMQUtil {

    // /**
    // * Create RabbitMQ FLINK stream source
    // *
    // * @param program
    // * @return
    // */
    // @SuppressWarnings("unchecked")
    // public static <T, S extends SourceSplit, E> Source<T, S, E>
    // createRabbitMQSource(RengineFlinkStreamingBase program) {
    // // TODO
    // RabbitMQSource<RengineEventAnalytical> source =
    // RabbitMQSource.<RengineEventAnalytical> builder()
    // .setServiceUrl(program.getBrokers())
    // // .setGroupId(program.getGroupId())
    // .setTopicPattern(Pattern.compile(program.getTopicPattern()))
    // // TODO
    // // .setStartingOffsets(offsets)
    // // .setClientIdPrefix(program.getJobName())
    // // .setProperties(program.getProps())
    // // .setDeserializer(new RengineKafkaRecordDeserializationSchema())
    // .build();
    // return (Source<T, S, E>) source;
    // }

}
