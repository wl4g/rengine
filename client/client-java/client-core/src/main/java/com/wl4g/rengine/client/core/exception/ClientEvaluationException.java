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
package com.wl4g.rengine.client.core.exception;

import com.wl4g.rengine.common.exception.RengineException;

import lombok.Getter;

/**
 * {@link ClientEvaluationException}
 * 
 * @author James Wong
 * @version 2022-10-17
 * @since v3.0.0
 */
@Getter
public class ClientEvaluationException extends RengineException {
    private static final long serialVersionUID = 4247583228100258388L;

    private String requestId;
    private String scenesCode;
    private Boolean bestEffort;
    private Long timeout;

    public ClientEvaluationException() {
        super();
    }

    public ClientEvaluationException(String requestId, String scenesCode, Long timeout, Boolean bestEffort, String message) {
        super(message);
        this.requestId = requestId;
        this.scenesCode = scenesCode;
        this.timeout = timeout;
        this.bestEffort = bestEffort;
    }

    public ClientEvaluationException(String requestId, String scenesCode, Long timeout, Boolean bestEffort, String message,
            Throwable cause) {
        super(message, cause);
        this.requestId = requestId;
        this.scenesCode = scenesCode;
        this.timeout = timeout;
        this.bestEffort = bestEffort;
    }

    public ClientEvaluationException(String requestId, String scenesCode, Long timeout, Boolean bestEffort, Throwable cause) {
        super(cause);
        this.requestId = requestId;
        this.scenesCode = scenesCode;
        this.timeout = timeout;
        this.bestEffort = bestEffort;
    }

}
