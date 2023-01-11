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
package com.wl4g.rengine.service.config;

import java.time.Duration;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link RengineServiceProperties}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
public class RengineServiceProperties {

    ServiceProperties service = new ServiceProperties();

    MinioProperties minio = new MinioProperties();

    /**
     * @see {@link com.wl4g.rengine.executor.execution.ExecutionConfig.ServiceConfig}
     */
    @Getter
    @Setter
    @ToString
    public static class ServiceProperties {

        @NotBlank
        String dictCachedPrefix = DEFAULT_DICT_CACHED_PREFIX;

        @NotNull
        @Min(0)
        Long dictCachedExpire = DEFAULT_DICT_CACHED_EXPIRE;

        public static final String DEFAULT_DICT_CACHED_PREFIX = "rengine:service:dict";
        public static final long DEFAULT_DICT_CACHED_EXPIRE = Duration.ofHours(24).toMillis();
    }

    @Getter
    @Setter
    @ToString
    public static class MinioProperties {
        String endpoint;

        String accessKey;

        String secretKey;

        String filHost;
    }

}
