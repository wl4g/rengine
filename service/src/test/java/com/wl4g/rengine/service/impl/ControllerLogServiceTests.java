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

import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.RE_CONTROLLER_LOG;

import java.text.ParseException;
import java.time.Duration;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.wl4g.rengine.common.entity.ControllerLog;
import com.wl4g.rengine.service.model.ControllerLogDelete;
import com.wl4g.rengine.service.util.TestDefaultMongoSetup;
import com.wl4g.rengine.service.util.TestDefaultRedisTemplateSetup;

/**
 * {@link ControllerLogServiceTests}
 * 
 * @author James Wong
 * @date 2023-02-14
 * @since v1.0.0
 */
public class ControllerLogServiceTests {

    static ControllerLogServiceImpl controllerLogService;
    static MongoTemplate mongoTemplate;

    @BeforeClass
    public static void init() {
        controllerLogService = new ControllerLogServiceImpl();
        controllerLogService.mongoTemplate = (mongoTemplate = TestDefaultMongoSetup.createMongoTemplate());
        controllerLogService.redisTemplate = TestDefaultRedisTemplateSetup.createRedisTemplate();
        controllerLogService.init();
    }

    @Test
    public void testDeleteWithRetentionCountAndUpdateDateUpper() {
        // Intiail testdata.
        mongoTemplate.dropCollection(RE_CONTROLLER_LOG.getName());

        mongoTemplate.insert(ControllerLog.builder().id(1001010111L).updateDate(buildDate("2023-02-11 00:00:00")).build(),
                RE_CONTROLLER_LOG.getName());
        mongoTemplate.insert(ControllerLog.builder().id(1001010112L).updateDate(buildDate("2023-02-12 00:00:00")).build(),
                RE_CONTROLLER_LOG.getName());
        mongoTemplate.insert(ControllerLog.builder().id(1001010113L).updateDate(buildDate("2023-02-13 00:00:00")).build(),
                RE_CONTROLLER_LOG.getName());
        mongoTemplate.insert(ControllerLog.builder().id(1001010114L).updateDate(buildDate("2023-02-14 00:00:00")).build(),
                RE_CONTROLLER_LOG.getName());
        mongoTemplate.insert(ControllerLog.builder().id(1001010115L).updateDate(buildDate("2023-02-15 00:00:00")).build(),
                RE_CONTROLLER_LOG.getName());
        mongoTemplate.insert(ControllerLog.builder().id(1001010116L).updateDate(buildDate("2023-02-16 00:00:00")).build(),
                RE_CONTROLLER_LOG.getName());
        mongoTemplate.insert(ControllerLog.builder().id(1001010117L).updateDate(buildDate("2023-02-17 00:00:00")).build(),
                RE_CONTROLLER_LOG.getName());
        mongoTemplate.insert(ControllerLog.builder().id(1001010118L).updateDate(buildDate("2023-02-18 00:00:00")).build(),
                RE_CONTROLLER_LOG.getName());
        mongoTemplate.insert(ControllerLog.builder().id(1001010119L).updateDate(buildDate("2023-02-19 00:00:00")).build(),
                RE_CONTROLLER_LOG.getName());

        // Testing deleting.
        final long currentTime = buildDate("2023-02-19 01:00:00").getTime();
        final long retentionHours = 168; // 7 days
        final long purgeUpperTime = currentTime - Duration.ofHours(retentionHours).toMillis();
        final var result = controllerLogService
                .delete(ControllerLogDelete.builder().updateDateUpper(new Date(purgeUpperTime)).retentionCount(3L).build());
        System.out.println("Deleted count: " + result);
        assert result.getDeletedCount() == 6L;
    }

    static Date buildDate(String dateStr) {
        try {
            return DateUtils.parseDate(dateStr, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

}
