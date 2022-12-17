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
package com.wl4g.rengine.executor.execution.sdk;

import static java.util.Objects.isNull;

import java.io.Serializable;
import java.util.HashMap;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.proxy.ProxyObject;

import com.wl4g.rengine.executor.minio.MinioManager;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link ScriptContext}
 * 
 * @author James Wong
 * @version 2022-09-22
 * @since v1.0.0
 */
@Getter
@ToString
@SuperBuilder
public class ScriptContext implements Serializable {
    private static final long serialVersionUID = 1106545214350173531L;
    //
    // Runtime context attributes.
    //
    private final @NotBlank String id;
    private final @NotBlank String type;
    private final @NotNull @Default ScriptParameter parameter = ScriptParameter.builder().build();
    private final @NotNull @Default ProxyObject attributes = ProxyObject.fromMap(new HashMap<>());
    private final @Nullable ScriptResult lastResult;

    //
    // Helper attributes.
    //
    private transient ScriptLogger logger;
    private transient ScriptDataService dataService;
    //
    // Internal attributes.
    //
    private transient MinioManager minioManager;

    public @HostAccess.Export String getId() {
        return id;
    }

    public @HostAccess.Export String getType() {
        return type;
    }

    public @HostAccess.Export ScriptParameter getParameter() {
        return parameter;
    }

    public @HostAccess.Export ProxyObject getAttributes() {
        return attributes;
    }

    public @HostAccess.Export ScriptResult getLastResult() {
        return lastResult;
    }

    //
    // Helper functions.
    //

    public @HostAccess.Export ScriptLogger getLogger() {
        if (isNull(logger)) {
            synchronized (this) {
                if (isNull(logger)) {
                    logger = new ScriptLogger(this);
                }
            }
        }
        return logger;
    }

    public @HostAccess.Export ScriptDataService getDataService() {
        return dataService;
    }

    @ToString
    @SuperBuilder
    public static class ScriptParameter {
        private final @Min(1) long requestTime;
        private final @NotBlank String clientId;
        private final @NotBlank String traceId;
        private final @Default boolean trace = true;
        private final @NotBlank String scenesCode;
        private final @NotBlank String workflowId;
        private final @NotNull @Default ProxyObject args = ProxyObject.fromMap(new HashMap<>());

        public @HostAccess.Export long getRequestTime() {
            return requestTime;
        }

        public @HostAccess.Export String getClientId() {
            return clientId;
        }

        public @HostAccess.Export String getTraceId() {
            return traceId;
        }

        public @HostAccess.Export boolean isTrace() {
            return trace;
        }

        public @HostAccess.Export String getScenesCode() {
            return scenesCode;
        }

        public @HostAccess.Export String getWorkflowId() {
            return workflowId;
        }

        public @HostAccess.Export ProxyObject getArgs() {
            return args;
        }
    }
}
