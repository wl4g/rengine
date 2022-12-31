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
package com.wl4g.rengine.eventbus.config;

import java.time.Duration;
import java.util.Properties;

import com.wl4g.infra.common.store.MapStoreConfig.EhCacheStoreConfig;
import com.wl4g.infra.common.store.MapStoreConfig.RedisStoreConfig;
import com.wl4g.infra.common.store.MapStoreConfig.RocksDBStoreConfig;
import com.wl4g.rengine.common.constants.RengineConstants;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link ClientEventBusConfig}
 * 
 * @author James Wong &lt;jameswong1376@gmail.com&gt;
 * @version 2022-05-30 v3.0.0
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
public class ClientEventBusConfig {

    private @Default String topic = RengineConstants.DEFAULT_EVENTBUS_TOPIC;

    private @Default EventStoreConfig recorder = new EventStoreConfig();

    private @Default KafkaEventBusConfig kafka = new KafkaEventBusConfig();

    private @Default PulsarEventBusConfig pulsar = new PulsarEventBusConfig();

    private @Default RabbitmqEventBusConfig rabbitmq = new RabbitmqEventBusConfig();

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    public static class EventStoreConfig {

        private @Default EventRecorderProvider provider = EventRecorderProvider.ROCKSDB;

        private @Default CompactionConfig compaction = new CompactionConfig();

        private @Default RocksDBStoreConfig rocksdb = new RocksDBStoreConfig();

        private @Default EhCacheStoreConfig ehcache = new EhCacheStoreConfig();

        private @Default RedisStoreConfig redis = new RedisStoreConfig();

        @Getter
        @Setter
        @ToString
        public static class CompactionConfig {
            private int threadPools = 1;
            private int delaySeconds = 120;
        }

        public static enum EventRecorderProvider {
            ROCKSDB, EHCACHE, REDIS;
        }
    }

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    public static class KafkaEventBusConfig {
        private @Default Duration closingTimeout = Duration.ofMinutes(1);
        private @Default Properties properties = new Properties() {
            {
                put("bootstrap.servers", "localhost:9092");
                // see:https://kafka.apache.org/documentation/#producerconfigs_acks
                put("acks", "1"); // Default by all,
                put("send.buffer.bytes", "-1");
                put("batch.size", "16384");// Default by 16KiB
                put("buffer.memory", "33554432");// Default by 32MiB
                put("retries", "3");
                put("retry.backoff.ms", "5000");// Default by 100ms
                put("linger.ms", "20");// Default by 0ms.
                // The maximum blocking time after the total sending buffer is
                // slow, and an exception will be thrown after the time is
                // exceeded.
                put("max.block.ms", "60000"); // Default by 1m
                // The maximum size of a single request packet body, which
                // cannot exceed the maximum value of the accepted packet body
                // set by the server.
                put("max.request.size", "1048576");// Default by 1048576
                put("compression.type", "gzip");
            }
        };
    }

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    public static class PulsarEventBusConfig {
        private @Default Duration closingTimeout = Duration.ofMinutes(1);
        private @Default String serviceUrl = "pulsar://localhost:6650";
        private @Default Duration connectionTimeout = Duration.ofSeconds(3);
        private @Default int connectionsPerBroker = 100;
        private @Default Duration lookupTimeout = Duration.ofSeconds(30);
        private @Default Duration operationTimeout = Duration.ofSeconds(5);
        private @Default Duration startingBackoffInterval = Duration.ofSeconds(5);
        private @Default int listenerThreads = 1;
        private @Default int ioThreads = 5;
        private @Default boolean allowTlsInsecureConnection = true;
        private @Default boolean enableTlsHostnameVerification = false;
        private @Default boolean enableTcpNoDelay = true;
        private @Default boolean enableTransaction = false;
        private @Default Duration maxBackoffInterval = Duration.ofNanos(30);
        private @Default int maxConcurrentLookupRequests = 5000;
        private @Default int maxLookupRedirects = 50000;
        private @Default int maxLookupRequests = 50000;
        private @Default int maxNumberOfRejectedRequestPerConnection = 50;
        private @Default Duration keepAliveInterval = Duration.ofSeconds(30);
        // Producer
        private @Default boolean enableChunking = true;
        private @Default boolean enableBatching = true;
    }

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    public static class RabbitmqEventBusConfig {
        private @Default Properties properties = new Properties() {
            {
                // see:com.rabbitmq.client.ConnectionFactoryConfigurator
                // see:com.rabbitmq.client.ConnectionFactory
                put("host", "localhost");
                put("port", "5672");
                put("username", "guest");
                put("password", "guest");
                put("virtual.host", "/");
                put("connection.channel.max", "2047");
                put("connection.frame.max", "0");
                put("connection.heartbeat", "60");
                put("use.nio", "true");
            }
        };
    }

}
