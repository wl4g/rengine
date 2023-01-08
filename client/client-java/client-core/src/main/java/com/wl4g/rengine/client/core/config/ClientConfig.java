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
package com.wl4g.rengine.client.core.config;

import java.net.URI;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.minio.OkHttpClientConfig;
import com.wl4g.rengine.common.model.ExecuteRequest;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link ClientConfig}
 * 
 * @author James Wong
 * @version 2022-10-17
 * @since v3.0.0
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
public class ClientConfig {

    private @NotBlank @Default URI endpoint = URI.create("http://localhost:28002");

    private @NotBlank String clientId;

    private @NotBlank String clientSecret;

    private @NotNull @Min(1) @Default Long defaultTimeout = ExecuteRequest.DEFAULT_TIMEOUT;

    private @NotNull @Min(1) @Default Boolean defaultBestEffort = ExecuteRequest.DEFAULT_BESTEFFORT;

    private @NotNull @Default OkHttpClientConfig okHttpClient = new OkHttpClientConfig();
}
