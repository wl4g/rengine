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
 * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.executor.util;

import static com.mongodb.assertions.Assertions.notNull;
import static com.mongodb.internal.event.EventListenerHelper.getCommandListener;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.findField;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.setField;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.OptionalInt;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoDriverInformation;
import com.mongodb.client.MongoClient;
import com.mongodb.client.internal.MongoClientImpl;
import com.mongodb.connection.AsynchronousSocketChannelStreamFactory;
import com.mongodb.connection.SocketSettings;
import com.mongodb.connection.SocketStreamFactory;
import com.mongodb.connection.StreamFactory;
import com.mongodb.connection.StreamFactoryFactory;
import com.mongodb.internal.connection.Cluster;
import com.mongodb.internal.connection.DefaultClusterFactory;
import com.mongodb.internal.connection.InternalConnectionPoolSettings;
import com.mongodb.lang.Nullable;
import com.wl4g.rengine.executor.execution.ExecutionConfig;
import com.wl4g.rengine.executor.repository.MongoRepository;

import io.quarkus.mongodb.impl.ReactiveMongoClientImpl;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.vertx.core.runtime.QuarkusExecutorFactory;
import io.quarkus.vertx.core.runtime.config.VertxConfiguration;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxBuilder;
import io.vertx.core.net.impl.transport.Transport;
import io.vertx.core.spi.VertxThreadFactory;

/**
 * {@link TestDefaultBaseSetup}
 * 
 * @author James Wong
 * @version 2022-10-10
 * @since v1.0.0
 */
public abstract class TestDefaultBaseSetup {

    public static io.vertx.mutiny.core.Vertx buildMutinyVertxDefault() {
        return new io.vertx.mutiny.core.Vertx(buildCoreVertxDefault());
    }

    public static Vertx buildCoreVertxDefault() {
        final VertxConfiguration conf = new VertxConfiguration();
        conf.queueSize = OptionalInt.of(2);
        conf.workerPoolSize = 2;
        conf.eventLoopsPoolSize = OptionalInt.of(2);
        conf.internalBlockingPoolSize = 2;
        conf.keepAliveTime = Duration.ofSeconds(3);
        conf.maxWorkerExecuteTime = Duration.ofSeconds(3);
        return new VertxBuilder().transport(Transport.transport(true))
                .executorServiceFactory(new QuarkusExecutorFactory(conf, LaunchMode.TEST))
                .threadFactory(VertxThreadFactory.INSTANCE)
                .vertx();
    }

    public static MongoRepository createMongoRepository() {
        MongoRepository mongoRepository = new MongoRepository();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://localhost:27017/rengine"))
                .build();
        MongoDriverInformation driverInformation = MongoDriverInformation.builder().build();

        Cluster cluster = createCluster(settings, driverInformation);

        MongoClient mongoClient = new MongoClientImpl(settings, driverInformation);
        setField(findField(MongoRepository.class, null, MongoClient.class), mongoRepository, mongoClient, true);

        ReactiveMongoClient reactiveMongoClient = new ReactiveMongoClientImpl(
                new com.mongodb.reactivestreams.client.internal.MongoClientImpl(settings, driverInformation, cluster,
                        new Closeable() {
                            @Override
                            public void close() throws IOException {
                                System.out.println("Closing for test reactive mongo client...");
                            }
                        }));
        setField(findField(MongoRepository.class, null, ReactiveMongoClient.class), mongoRepository, reactiveMongoClient, true);

        return mongoRepository;
    }

    // see:com.mongodb.client.internal.MongoClientImpl#createCluster
    private static Cluster createCluster(
            final MongoClientSettings settings,
            @Nullable final MongoDriverInformation mongoDriverInformation) {
        notNull("settings", settings);
        return new DefaultClusterFactory().createCluster(settings.getClusterSettings(), settings.getServerSettings(),
                settings.getConnectionPoolSettings(), InternalConnectionPoolSettings.builder().build(),
                getStreamFactory(settings, false, true), getStreamFactory(settings, true, true), settings.getCredential(),
                getCommandListener(settings.getCommandListeners()), settings.getApplicationName(), mongoDriverInformation,
                settings.getCompressorList(), settings.getServerApi());
    }

    // see:com.mongodb.client.internal.MongoClientImpl#getStreamFactory
    private static StreamFactory getStreamFactory(
            final MongoClientSettings settings,
            final boolean isHeartbeat,
            final boolean isAsync) {
        StreamFactoryFactory streamFactoryFactory = settings.getStreamFactoryFactory();
        SocketSettings socketSettings = isHeartbeat ? settings.getHeartbeatSocketSettings() : settings.getSocketSettings();
        if (streamFactoryFactory == null) {
            return isAsync ? new AsynchronousSocketChannelStreamFactory(socketSettings, settings.getSslSettings())
                    : new SocketStreamFactory(socketSettings, settings.getSslSettings());
        } else {
            return streamFactoryFactory.create(socketSettings, settings.getSslSettings());
        }
    }

    public static ExecutionConfig createExecutionConfig() {
        return new ExecutionConfig() {
            @Override
            public @NotNull ServiceConfig service() {
                return new ServiceConfig() {
                    @Override
                    public @NotBlank String dictCachedPrefix() {
                        return ServiceConfig.DEFAULT_DICT_CACHED_PREFIX;
                    }

                    @Override
                    public @NotNull @Min(0) Long dictCachedExpire() {
                        return ServiceConfig.DEFAULT_DICT_CACHED_EXPIRE;
                    }
                };
            }

            @Override
            public @NotNull EngineConfig engine() {
                return new EngineConfig() {
                    @Override
                    public @NotBlank String scenesRulesCachedPrefix() {
                        return EngineConfig.DEFAULT_SCENES_RULES_CACHED_PREFIX;
                    }

                    @Override
                    public @NotNull @Min(0) Long scenesRulesCachedExpire() {
                        return EngineConfig.DEFAULT_SCENES_RULES_CACHED_EXPIRE;
                    }

                    @Override
                    public @NotNull @Min(0) @Max(65535) Integer executorThreadPools() {
                        return EngineConfig.DEFAULT_EXECUTOR_THREAD_POOLS;
                    }

                    @Override
                    public @NotNull @Min(1) @Max(10240) Integer executorAcceptQueue() {
                        return EngineConfig.DEFAULT_EXECUTOR_ACCEPT_QUEUE;
                    }

                    @Override
                    public @NotNull @Min(0) @Max(1024) Integer perExecutorThreadPools() {
                        return EngineConfig.DEFAULT_PER_EXECUTOR_THREAD_POOLS;
                    }

                    @Override
                    public @NotNull @Min(0) @Max(100000) Integer maxQueryBatch() {
                        return EngineConfig.DEFAULT_MAX_QUERY_BATCH;
                    }

                    @Override
                    public @NotNull @Min(0) @Max(1) Float executeTimeoutOffsetRate() {
                        return EngineConfig.DEFAULT_TIMEOUT_OFFSET_RATE;
                    }
                };
            }

            @Override
            public @NotNull ScriptLogConfig log() {
                return new ScriptLogConfig() {
                    @Override
                    public @NotBlank String baseDir() {
                        return ScriptLogConfig.DEFAULT_SCRIPT_LOG_BASE_DIR;
                    }

                    @Override
                    public @NotNull Boolean enableConsole() {
                        return ScriptLogConfig.DEFAULT_SCRIPT_LOG_ENABLE_CONSOLE;
                    }

                    @Override
                    public @NotNull @Min(1024) Integer fileMaxSize() {
                        return ScriptLogConfig.DEFAULT_SCRIPT_LOG_FILE_MAX_SIZE;
                    }

                    @Override
                    public @NotNull @Min(1) Integer fileMaxCount() {
                        return ScriptLogConfig.DEFAULT_SCRIPT_LOG_FILE_MAX_COUNT;
                    }

                    @Override
                    public @NotBlank String uploaderCron() {
                        return ScriptLogConfig.DEFAULT_SCRIPT_LOG_UPLOADER_CRON;
                    }
                };
            }

            @Override
            public @NotNull NotifierConfig notifier() {
                return new NotifierConfig() {

                    @Override
                    public @NotNull @Min(0) Long refreshLockTimeout() {
                        return NotifierConfig.DEFAULT_NOTIFIER_REFRESH_LOCK_TIMEOUT;
                    }

                    @Override
                    public @NotBlank String refreshedCachedPrefix() {
                        return NotifierConfig.DEFAULT_NOTIFIER_REFRESHED_CACHED_PREFIX;
                    }

                    @Override
                    public @NotNull @Min(0) @Max(1) Float refreshedCachedExpireOffsetRate() {
                        return NotifierConfig.DEFAULT_NOTIFIER_EXPIRE_OFFSET_RATE;
                    }
                };
            }
        };
    }

}
