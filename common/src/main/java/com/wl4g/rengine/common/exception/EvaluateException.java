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
package com.wl4g.rengine.common.exception;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;

import lombok.Getter;

/**
 * {@link EvaluateException}
 * 
 * @author James Wong
 * @version 2022-09-22
 * @since v1.0.0
 */
@Getter
public class EvaluateException extends RengineException {
    private static final long serialVersionUID = 5177120828249689148L;

    private String scenesCode;
    private String requestId;

    public EvaluateException(String requestId, String scenesCode, String message) {
        super(message);
        this.requestId = hasTextOf(requestId, "requestId");
        this.scenesCode = hasTextOf(scenesCode, "scenesCode");
    }

    public EvaluateException(String requestId, String scenesCode, String message, Throwable cause) {
        super(message, cause);
        this.requestId = hasTextOf(requestId, "requestId");
        this.scenesCode = hasTextOf(scenesCode, "scenesCode");
    }

    public EvaluateException(String requestId, String scenesCode, Throwable cause) {
        super(cause);
        this.requestId = hasTextOf(requestId, "requestId");
        this.scenesCode = hasTextOf(scenesCode, "scenesCode");
    }

}
