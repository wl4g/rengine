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
package com.wl4g.rengine.common.entity.sys;

import static java.util.Arrays.asList;

import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wl4g.rengine.common.entity.BaseEntity;
import com.wl4g.rengine.common.entity.quota.ResourceQuota;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link Tenant}
 * 
 * @author James Wong
 * @version 2023-03-08
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
public class Tenant extends BaseEntity {
    private static final long serialVersionUID = 381411777614066880L;

    private @Default ServiceSettings settings = new ServiceSettings();
    private @Default ResourceQuota executorQuota = new ResourceQuota();
    private @Default ResourceQuota controllerQuota = new ResourceQuota();

    // Ignore getter/setter.

    @JsonIgnore
    @Override
    public Long getTenantId() {
        return null;
    }

    @JsonIgnore
    @Override
    public void setTenantId(Long tenantId) {
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class ServiceSettings {
        private MongoServiceSettings mongo;
        private RedisServiceSettings redis;
        private MinIOServiceSettings minio;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class MongoServiceSettings {
        private @Default Boolean enable = true;
        private @Nullable @Default String sharedMongoConnectionString = "mongodb://localhost:27017";
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class RedisServiceSettings {
        private @Default Boolean enable = true;
        private @Nullable @Default List<String> sharedRedisNodes = asList("localhost:6379", "localhost:6380", "localhost:6381",
                "localhost:7379", "localhost:7380", "localhost:7381");
        private @Nullable @Default String username = "";
        private @Nullable @Default String password = "123456";
        private @Nullable @Default Integer timeout = 10_000;
        private @Nullable @Default Integer maxPoolSize = 512;
        private @Nullable @Default Integer reconnectAttempts = 0;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class MinIOServiceSettings {
        private @Default Boolean enable = true;
        private @Nullable @Default String endpoint = "http://localhost:9000";
        private @Nullable @Default String defaultRegion = "us-east-1";
        private @Nullable @Default String defaultBucket = "rengine";
        private @Nullable @Default String accessKey = "rengine";
        private @Nullable @Default String accessSecret = "123456";
        private @Nullable @Default Integer clientConnectTimeout = 10_000;
        private @Nullable @Default Integer clientReadTimeout = 10_000;
        private @Nullable @Default Integer clientWriteTimeout = 10_000;
    }

}