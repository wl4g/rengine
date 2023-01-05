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
import com.wl4g.infra.common.serialize.BsonUtils2;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.Notification;
import com.wl4g.rengine.common.exception.ConfigRengineException;
import com.wl4g.rengine.common.exception.RengineException;
import com.wl4g.rengine.executor.execution.ExecutionConfig;
import com.wl4g.rengine.executor.execution.sdk.ScriptRedisLockClient;
import com.wl4g.rengine.executor.execution.sdk.notifier.ScriptMessageNotifier.RefreshedInfo;
import com.wl4g.rengine.executor.metrics.ExecutorMeterService;
import com.wl4g.rengine.executor.repository.MongoRepository;

import io.quarkus.arc.All;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.string.SetArgs;
import io.quarkus.redis.datasource.string.StringCommands;
import lombok.CustomLog;

/**
 * {@link GlobalMessageNotifierManager}
 * 
 * @author James Wong
 * @version 2023-01-06
 * @since v1.0.0
 */
@CustomLog
@Singleton
public class GlobalMessageNotifierManager {

    @NotNull
    @Inject
    ExecutionConfig config;

    @NotNull
    @Inject
    ExecutorMeterService meterService;

    @NotNull
    @Inject
    MongoRepository mongoRepository;

    @Inject
    RedisDataSource redisDS;

    @NotEmpty
    @Inject
    @All
    List<ScriptMessageNotifier> notifiers;

    Map<NotifierKind, ScriptMessageNotifier> notifierMap;

    StringCommands<String, String> redisStringCommands;

    JedisLockManager lockManager;

    @PostConstruct
    public void init() {
        this.notifierMap = safeList(notifiers).stream().collect(toMap(n -> n.kind(), n -> n));
        this.redisStringCommands = redisDS.string(String.class);
        this.lockManager = ScriptRedisLockClient.buildJedisLockManager(redisDS);
    }

    public ScriptMessageNotifier getMessageNotifier(final @NotNull NotifierKind notifierType) {
        final ScriptMessageNotifier notifier = notifierMap.get(notifierType);
        notNull(notifier, "Unable to get notifier, please check if notifier of type %s is supported and implemented.",
                notifierType);

        RefreshedInfo refreshed = loadRefreshedInfo(notifierType);
        if (isNull(refreshed)) {
            // Use distributed exclusive locks to prevent other nodes in the
            // cluster from overwriting repeated refreshes.
            final Lock lock = lockManager.getLock(DEFAULT_LOCK_PREFIX.concat(notifierType.name()));
            try {
                if (lock.tryLock(config.notifier().refreshLockTimeout(), TimeUnit.MILLISECONDS)) {
                    refreshed = loadRefreshedInfo(notifierType);
                    if (isNull(refreshed)) { // expired?
                        refreshed = notifier.refresh(findNotification(notifierType));
                        saveRefreshedInfo(refreshed);
                    }
                }
            } catch (InterruptedException e) {
                log.error(format("Failed to refresh notifier for '%s'.", notifierType), e);
            } finally {
                lock.unlock();
            }
        }

        return notifier;
    }

    RefreshedInfo loadRefreshedInfo(NotifierKind notifierType) {
        return parseJSON(redisStringCommands.get(config.notifier().refreshedCachedPrefix().concat(notifierType.name())),
                RefreshedInfo.class);
    }

    void saveRefreshedInfo(RefreshedInfo refreshed) {
        final int expireSeconds = (int) (refreshed.getExpireSeconds()
                * (1 - config.notifier().refreshedCachedExpireOffsetRate()));
        redisStringCommands.set(config.notifier().refreshedCachedPrefix().concat(refreshed.getNotifierType().name()),
                toJSONString(refreshed), new SetArgs().px(Duration.ofSeconds(expireSeconds)));
    }

    @NotNull
    Notification findNotification(final @NotNull NotifierKind notifierType) {
        final List<Notification> notifications = findNotifications(notifierType);
        if (notifications.size() > 1) {
            throw new ConfigRengineException(
                    format("The multiple notification of the same type and name were found of %s", notifierType));
        }
        return notifications.get(0);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    List<Notification> findNotifications(final @NotNull NotifierKind... notifierTypes) {
        notNullOf(notifierTypes, "notifierTypes");

        final MongoCollection<Document> collection = mongoRepository
                .getCollection(MongoCollectionDefinition.SYS_NOTIFICATION_CONFIG);

        try (final MongoCursor<Notification> cursor = collection.find(Filters.and(Filters.in("properties.type", notifierTypes)))
                .batchSize(2)
                .limit(2)
                .map(doc -> parseJSON(doc.toJson(BsonUtils2.DEFAULT_JSON_WRITER_SETTINGS), Notification.class))
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

}
