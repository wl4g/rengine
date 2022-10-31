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
package com.wl4g.rengine.client.eventbus.config;

import static com.wl4g.infra.common.lang.FastTimeClock.currentTimeMillis;
import static org.apache.commons.lang3.SystemUtils.JAVA_IO_TMPDIR;

import java.io.File;
import java.time.Duration;
import java.util.Properties;

import com.wl4g.infra.common.cache.jedis.JedisClientBuilder.JedisConfig;
import com.wl4g.infra.common.io.DataSize;
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

    private @Default String eventTopic = RengineConstants.DEF_EVENTBUS_TOPIC;

    private @Default EventStoreConfig store = new EventStoreConfig();

    private @Default KafkaEventBusConfig kafka = new KafkaEventBusConfig();

    private @Default PulsarEventBusConfig pulsar = new PulsarEventBusConfig();

    private @Default RabbitmqEventBusConfig rabbitmq = new RabbitmqEventBusConfig();

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    public static class EventStoreConfig {

        private @Default EventStoreProvider provider = EventStoreProvider.EHCACHE;

        private @Default CompactionConfig compaction = new CompactionConfig();

        private @Default EhCacheStoreConfig ehcache = new EhCacheStoreConfig();

        private @Default RedisStoreConfig redis = new RedisStoreConfig();

        @Getter
        @Setter
        @ToString
        public static class CompactionConfig {
            private int threadPools = 1;
        }

        @Getter
        @Setter
        @ToString
        public static class EhCacheStoreConfig {

            /**
             * The cached data elimination algorithm.
             */
            private EliminationAlgorithm eliminationAlg = EliminationAlgorithm.LRU;

            /**
             * The cache persistence data directory.
             */
            private File dataDir = new File(JAVA_IO_TMPDIR, "ehcache-data-" + currentTimeMillis());

            /**
             * The cache name prefix.
             */
            private String cacheNamePrefix = "paddingCache";

            /**
             * The number of entries not persisted to keep in memory.
             */
            private long offHeapEntries = 100_000L;

            /**
             * The number of data size not persisted to keep in memory. must be
             * less than {@link #diskSize}
             */
            private DataSize offHeapSize = DataSize.ofMegabytes(128);

            /**
             * The number of total data size not persisted to keep in disk. must
             * be greater than {@link #offHeapSize}
             */
            private DataSize diskSize = DataSize.ofMegabytes(512);

            public static enum EliminationAlgorithm {
                LRU, LFU, FIFO;
            }
        }

        @Getter
        @Setter
        @ToString
        public static class RedisStoreConfig extends JedisConfig {
            private static final long serialVersionUID = 1L;
            private String cachePrefix = "rengine:eventbus";
            private long expireMs = 60_000L;
        }

        public static enum EventStoreProvider {
            EHCACHE,

            REDIS,

            MEMORY;
        }

    }

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    public static class KafkaEventBusConfig {
        private @Default Duration closingTimeout = Duration.ofMinutes(1);
        private @Default Properties properties = new Properties();
    }

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    public static class PulsarEventBusConfig {
        private @Default Duration closingTimeout = Duration.ofMinutes(1);
        private @Default Properties properties = new Properties();
    }

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    public static class RabbitmqEventBusConfig {
        private @Default Duration closingTimeout = Duration.ofMinutes(1);
        private @Default Properties properties = new Properties();
    }

}
