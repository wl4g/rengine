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
package com.wl4g.rengine.service.impl;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.T_SCHEDULE_JOB_LOGS;
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.defaultSort;
import static com.wl4g.rengine.service.mongo.QueryHolder.gteUpdateDateCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.isCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.lteUpdateDateCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.orCriteria;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import javax.annotation.PostConstruct;

import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.DeleteResult;
import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.jedis.BasicJedisClient;
import com.wl4g.infra.common.locks.JedisLockManager;
import com.wl4g.rengine.common.entity.ScheduleJobLog;
import com.wl4g.rengine.common.util.IdGenUtils;
import com.wl4g.rengine.service.ScheduleJobLogService;
import com.wl4g.rengine.service.model.DeleteScheduleJobLog;
import com.wl4g.rengine.service.model.DeleteScheduleJobResult;
import com.wl4g.rengine.service.model.QueryScheduleJobLog;
import com.wl4g.rengine.service.model.SaveScheduleJobLogResult;

import lombok.CustomLog;

/**
 * {@link ScheduleJobLogServiceImpl}
 * 
 * @author James Wong
 * @version 2023-01-08
 * @since v1.0.0
 */
@CustomLog
@Service
public class ScheduleJobLogServiceImpl implements ScheduleJobLogService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    MongoTemplate mongoTemplate;

    JedisLockManager jedisLockManager;

    Lock deletingLock;

    @PostConstruct
    public void init() {
        this.jedisLockManager = new JedisLockManager(new BasicJedisClient() {
            @Override
            public Object eval(String script, List<String> keys, List<String> args) {
                return redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), safeList(keys),
                        safeList(args).toArray());
            }

            @Override
            public String get(String key) {
                return redisTemplate.opsForValue().get(key);
            }

            @Override
            public String setIfAbsent(String key, String value, long expireMs) {
                return redisTemplate.opsForValue().setIfAbsent(key, value, expireMs, TimeUnit.MILLISECONDS) ? "1" : "0";
            }
        });
        this.deletingLock = this.jedisLockManager.getLock(DELETING_LOCK_NAME, DEFAULT_DELETING_LOCK_TIMEOUT,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public PageHolder<ScheduleJobLog> query(QueryScheduleJobLog model) {
        final Query query = new Query(andCriteria(baseCriteria(model), isIdCriteria(model.getJobLogId()),
                isCriteria("triggerId", model.getTriggerId())))
                        .with(PageRequest.of(model.getPageNum(), model.getPageSize(), defaultSort()));

        final List<ScheduleJobLog> jobes = mongoTemplate.find(query, ScheduleJobLog.class, T_SCHEDULE_JOB_LOGS.getName());

        return new PageHolder<ScheduleJobLog>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, T_SCHEDULE_JOB_LOGS.getName()))
                .withRecords(jobes);
    }

    @Override
    public SaveScheduleJobLogResult save(ScheduleJobLog model) {
        ScheduleJobLog job = model;
        notNullOf(job, "job");

        if (isNull(job.getId())) {
            job.setId(IdGenUtils.nextLong());
            job.preInsert();
        } else {
            job.preUpdate();
        }

        final ScheduleJobLog saved = mongoTemplate.save(job, T_SCHEDULE_JOB_LOGS.getName());
        return SaveScheduleJobLogResult.builder().id(saved.getId()).build();
    }

    /**
     * <pre>
     *   //
     *   // Delete all data older than a certain time by updateDate.
     *   //
     *   db.getCollection("t_schedule_job_logs").remove({
     *       updateDate: {
     *           $lte: new Date(2012, 7, 14)
     *       }
     *   });
     *   
     *   //
     *   // Delete all data except the reserved quantity according to the descending order of updateDate.
     *   // see:https://stackoverflow.com/questions/19065615/how-to-delete-n-numbers-of-documents-in-mongodb
     *   // Case 1:
     *   var ITERATIONS = 1000;
     *   var LIMIT = 1000;
     *   var collObj = db.getCollection("t_schedule_job_logs");
     *   for (i = 0; i < ITERATIONS; i++) {
     *       var ids = collObj.find({
     *           updateDate: {
     *               $lte: new Date(2012, 7, 14)
     *           }
     *       }).limit(LIMIT).toArray().map((doc) => doc._id);
     *       if (ids != null && ids.length > 0) {
     *           collObj.remove({
     *               _id: {
     *                   $in: ids
     *               }
     *           });
     *           sleep(1); // Optional for not loading mongo in case of huge amount of deletions
     *       } else {
     *           break;
     *       }
     *   }
     *   
     *   // Case 2:
     *   db.t_schedule_job_logs.aggregate([
     *       //{
     *       //    $match: {
     *       //        "updateDate": {
     *       //            $gte: new Date(2023, 7, 14)
     *       //        }
     *       //    }
     *       //},
     *       {
     *           $sort: {
     *               updateDate: - 1
     *           }
     *       },
     *       {
     *           $limit: 10000000
     *       },
     *       {
     *           $out: 'tmp_job_log_deleting' // 将拷贝到临时collection
     *       }
     *   ]);
     *   db.t_schedule_job_logs.remove({});
     *   db.tmp_job_log_deleting.aggregate([ { $out: 't_schedule_job_logs' } ])
     *   db.tmp_job_log_deleting.drop();
     *   
     *   // Case 3:
     *   // 注意: 这不支持 limit，事实上只会删除 1 个元素(因为这是默认逻辑)
     *   // https://docs.mongodb.com/manual/reference/method/db.collection.findAndModify/
     *   db.getCollection("t_schedule_job_logs").findAndModify({
     *       query: {
     *           updateDate: {
     *               $lte: new Date(2012, 7, 14)
     *           }
     *       },
     *       sort: {
     *           "updateDate": - 1
     *       },
     *       //limit: 1000000,
     *       remove: true
     *   });
     * </pre>
     */
    @Override
    public DeleteScheduleJobResult delete(DeleteScheduleJobLog model) {
        // (Defaults) Delete all data older than a certain time by updateDate.
        final Criteria filter = orCriteria(isIdCriteria(model.getId()), andCriteria(
                gteUpdateDateCriteria(model.getUpdateDateLower()), lteUpdateDateCriteria(model.getUpdateDateUpper())));
        final DeleteResult result = mongoTemplate.remove(new Query(filter), T_SCHEDULE_JOB_LOGS.getName());

        // Delete all data except the reserved quantity according to the
        // descending order of updateDate.
        long deletedCount = 0;
        if (nonNull(model.getRetentionCount())) {
            try {
                if (deletingLock.tryLock()) {
                    log.info("Deleting job logs by retention count ...");
                    final long totalCount = mongoTemplate.getCollection(T_SCHEDULE_JOB_LOGS.getName()).countDocuments();

                    final List<Bson> outToTempBsons = new ArrayList<>(3);
                    outToTempBsons.add(BsonDocument.parse("{ $sort: { updateDate: -1 } }"));
                    outToTempBsons.add(BsonDocument.parse(format("{ $limit: %s }", model.getRetentionCount())));
                    outToTempBsons.add(BsonDocument.parse(format("{ $out: '%s'}", TMP_JOB_LOG_DELETING_COLLECTION)));
                    mongoTemplate.getCollection(T_SCHEDULE_JOB_LOGS.getName()).aggregate(outToTempBsons).toCollection();

                    // TODO It is best to lock collection large batches deleting
                    // of data?
                    mongoTemplate.remove(new Query(), T_SCHEDULE_JOB_LOGS.getName());

                    final String outToSrcBson = format("{ $out: '%s' }", T_SCHEDULE_JOB_LOGS.getName());
                    mongoTemplate.getCollection(TMP_JOB_LOG_DELETING_COLLECTION)
                            .aggregate(singletonList(BsonDocument.parse(outToSrcBson)))
                            .toCollection();

                    final long _retentionCount = mongoTemplate.getCollection(TMP_JOB_LOG_DELETING_COLLECTION).countDocuments();
                    deletedCount = totalCount - _retentionCount;

                    mongoTemplate.dropCollection(TMP_JOB_LOG_DELETING_COLLECTION);
                } else {
                    log.warn("Unable deleting job logs by retention count, because get lock failure");
                }
            } finally {
                deletingLock.unlock();
            }
        }
        return DeleteScheduleJobResult.builder().deletedCount(result.getDeletedCount() + deletedCount).build();
    }

    public static final String DELETING_LOCK_NAME = ScheduleJobLog.class.getSimpleName() + ".deleting";
    public static final Long DEFAULT_DELETING_LOCK_TIMEOUT = 60_000L;
    public static final String TMP_JOB_LOG_DELETING_COLLECTION = "tmp_job_log_deleting";

}
