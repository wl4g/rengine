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
package com.wl4g.rengine.executor.execution.sdk.notifier;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_notifier_manager_failure;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_notifier_manager_success;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_notifier_manager_time;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_notifier_manager_total;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.IteratorUtils;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.wl4g.infra.common.locks.JedisLockManager;
import com.wl4g.infra.common.notification.MessageNotifier.NotifierKind;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.Notification;
import com.wl4g.rengine.common.exception.ConfigRengineException;
import com.wl4g.rengine.common.exception.RengineException;
import com.wl4g.rengine.common.util.BsonEntitySerializers;
import com.wl4g.rengine.executor.execution.ExecutionConfig;
import com.wl4g.rengine.executor.execution.sdk.ScriptRedisLockClient;
import com.wl4g.rengine.executor.execution.sdk.notifier.ScriptMessageNotifier.RefreshedInfo;
import com.wl4g.rengine.executor.meter.MeterUtil;
import com.wl4g.rengine.executor.meter.RengineExecutorMeterService;
import com.wl4g.rengine.executor.repository.MongoRepository;

import io.quarkus.arc.All;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.string.SetArgs;
import io.quarkus.redis.datasource.string.StringCommands;
import io.vertx.core.Vertx;
import lombok.CustomLog;

/**
 * {@link GlobalMessageNotifierManager}
 * 
 * @author James Wong
 * @version 2023-01-06
 * @since v1.0.0
 */
@SuppressWarnings("deprecation")
@CustomLog
@Singleton
public class GlobalMessageNotifierManager {

    @NotNull
    @Inject
    ExecutionConfig config;

    @NotNull
    @Inject
    RengineExecutorMeterService meterService;

    @NotNull
    @Inject
    Vertx vertx;

    @NotNull
    @Inject
    MongoRepository mongoRepository;

    @Inject
    RedisDataSource redisDS;

    @NotEmpty
    @Inject
    @All
    List<ScriptMessageNotifier> notifiers;

    Map<NotifierKind, ScriptMessageNotifier> notifierRegistry;

    StringCommands<String, String> redisStringCommands;

    JedisLockManager lockManager;

    @PostConstruct
    public void init() {
        this.notifierRegistry = safeList(notifiers).stream().collect(toMap(n -> n.kind(), n -> n));
        this.redisStringCommands = redisDS.string(String.class);
        this.lockManager = ScriptRedisLockClient.buildJedisLockManager(redisDS);
    }

    public ScriptMessageNotifier obtain(final @NotNull NotifierKind notifierType) {
        try {
            MeterUtil.counter(execution_sdk_notifier_manager_total, notifierType, METHOD_OBTAIN);
            return MeterUtil.timer(execution_sdk_notifier_manager_time, notifierType, METHOD_OBTAIN, () -> {
                final ScriptMessageNotifier notifier = notifierRegistry.get(notNullOf(notifierType, "notifierType"));
                notNull(notifier, "Unable to get notifier, please check if notifier of type %s is supported and implemented.",
                        notifierType);

                ensureRefreshed(notifier);
                MeterUtil.counter(execution_sdk_notifier_manager_success, notifierType, METHOD_OBTAIN);
                return notifier;
            });
        } catch (Throwable ex) {
            MeterUtil.counter(execution_sdk_notifier_manager_failure, notifierType, METHOD_OBTAIN);
            log.error(format("Failed to obtain script notifier for %s", notifierType), ex);
            throw ex;
        }
    }

    /**
     * Make sure that the refreshed info of the currently obtained notifier is
     * valid.
     * 
     * @param notifier
     * @return
     */
    ScriptMessageNotifier ensureRefreshed(final @NotNull ScriptMessageNotifier notifier) {
        final NotifierKind notifierType = notifier.kind();

        RefreshedInfo refreshed = loadRefreshed(notifierType);
        if (isNull(refreshed)) {
            synchronized (notifierType) {
                refreshed = loadRefreshed(notifierType);
                if (isNull(refreshed)) {
                    // Here we give priority to using local locks to prevent
                    // local multi-threaded execution. After obtaining local
                    // locks, we also need to obtain distributed locks to
                    // prevent other nodes in the cluster from preempting
                    // execution. In this way, multi-level locks are used to
                    // ensure performance as much as possible.
                    final Lock lock = lockManager.getLock(DEFAULT_LOCK_PREFIX.concat(notifierType.name()),
                            config.engine().notifier().refreshLockTimeout(), TimeUnit.MILLISECONDS);
                    try {
                        if (lock.tryLock()) {
                            refreshed = loadRefreshed(notifierType);
                            if (isNull(refreshed)) { // expired?
                                refreshed = notifier.refresh(findNotification(notifierType));
                                log.info("Refreshed to {} for notifier %s, {}", refreshed, notifierType);
                                saveRefreshed(refreshed);
                            }
                        }
                    } catch (Throwable e) {
                        log.error(format("Failed to refresh notifier for '%s'.", notifierType), e);
                        throw e;
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }

        // Sets to current effective refreshed.
        notifier.update(refreshed, vertx);

        return notifier;
    }

    RefreshedInfo loadRefreshed(NotifierKind notifierType) {
        try {
            MeterUtil.counter(execution_sdk_notifier_manager_total, notifierType, METHOD_LOADREFRESHED);
            return MeterUtil.timer(execution_sdk_notifier_manager_time, notifierType, METHOD_LOADREFRESHED, () -> {
                return parseJSON(redisStringCommands.get(buildRefreshedCachedKey(notifierType)), RefreshedInfo.class);
            });
        } catch (Throwable e) {
            MeterUtil.counter(execution_sdk_notifier_manager_failure, notifierType, METHOD_LOADREFRESHED);
            throw e;
        }
    }

    void saveRefreshed(RefreshedInfo refreshed) {
        try {
            MeterUtil.counter(execution_sdk_notifier_manager_total, refreshed.getNotifierType(), METHOD_SAVEREFRESHED);
            MeterUtil.timer(execution_sdk_notifier_manager_time, refreshed.getNotifierType(), METHOD_SAVEREFRESHED, () -> {
                final int effectiveExpireSec = (int) (refreshed.getExpireSeconds()
                        * (1 - config.engine().notifier().refreshedCachedExpireOffsetRate()));
                // Sets effective expire.
                refreshed.setEffectiveExpireSeconds(effectiveExpireSec);

                redisStringCommands.set(buildRefreshedCachedKey(refreshed.getNotifierType()), toJSONString(refreshed),
                        new SetArgs().px(Duration.ofSeconds(effectiveExpireSec)));
                return null;
            });
        } catch (Throwable e) {
            MeterUtil.counter(execution_sdk_notifier_manager_failure, refreshed.getNotifierType(), METHOD_SAVEREFRESHED);
            throw e;
        }
    }

    String buildRefreshedCachedKey(final @NotNull NotifierKind notifierType) {
        notNullOf(notifierType, "notifierType");
        return config.engine().notifier().refreshedCachedPrefix().concat(notifierType.name());
    }

    @NotNull
    Notification findNotification(final @NotNull NotifierKind notifierType) {
        try {
            MeterUtil.counter(execution_sdk_notifier_manager_total, notifierType, METHOD_FINDNOTIFICATION);
            return MeterUtil.timer(execution_sdk_notifier_manager_time, notifierType, METHOD_FINDNOTIFICATION, () -> {
                final List<Notification> notifications = findNotifications(notifierType);
                if (notifications.size() > 1) {
                    throw new ConfigRengineException(
                            format("The multiple notification of the same type and name were found of %s", notifierType));
                }
                return notifications.get(0);
            });
        } catch (Throwable e) {
            MeterUtil.counter(execution_sdk_notifier_manager_failure, notifierType, METHOD_FINDNOTIFICATION);
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    @NotNull
    List<Notification> findNotifications(final @NotNull NotifierKind... notifierTypes) {
        notNullOf(notifierTypes, "notifierTypes");

        final MongoCollection<Document> collection = mongoRepository.getCollection(MongoCollectionDefinition.SYS_NOTIFICATIONS);

        try (final MongoCursor<Notification> cursor = collection.find(Filters.and(Filters.in("properties.type", notifierTypes)))
                .batchSize(2)
                .limit(2)
                .map(doc -> BsonEntitySerializers.fromDocument(doc, Notification.class))
                .iterator();) {

            // Check should have only one.
            final List<Notification> ncs = safeList(IteratorUtils.toList(cursor));
            if (ncs.isEmpty()) {
                throw new ConfigRengineException(format("Unable found notification identifier of '%s'", asList(notifierTypes)));
            }
            return ncs;
        } catch (Throwable e) {
            throw new RengineException(e);
        }
    }

    static final String DEFAULT_LOCK_PREFIX = "notifier:";
    static final String METHOD_OBTAIN = "obtain";
    static final String METHOD_LOADREFRESHED = "loadRefreshed";
    static final String METHOD_SAVEREFRESHED = "saveRefreshed";
    static final String METHOD_FINDNOTIFICATION = "findNotification";
}
