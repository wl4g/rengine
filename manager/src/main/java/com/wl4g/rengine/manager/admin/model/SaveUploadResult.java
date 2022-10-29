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
package com.wl4g.rengine.manager.admin.model;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link SaveUploadResult}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
public class SaveUploadResult {

    private @NotBlank String endpoint;
    private @NotBlank int port;
    private @NotBlank String region;
    // private @NotBlank String useSSL;
    private @NotBlank String bucket;
    private @NotBlank String accessKey;
    private @NotBlank String secretKey;
    private @NotBlank String sessionToken;
    private @NotBlank long partSize;

    private @NotBlank Long id;
    private @NotBlank long fileLimitSize;
    private @NotBlank String objectPrefix;
    private @NotEmpty List<String> extensions;
}
