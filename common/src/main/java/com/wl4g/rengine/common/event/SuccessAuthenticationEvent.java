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
package com.wl4g.rengine.common.event;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * {@link SuccessAuthenticationEvent}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-05-30 v3.0.0
 * @since v3.0.0
 */
public class SuccessAuthenticationEvent extends RengineEventBase {
    private static final long serialVersionUID = -8912834545311079238L;

    public SuccessAuthenticationEvent(@NotNull Object source, @NotBlank String remoteIp, @NotBlank String coordinates,
            @Nullable String message) {
        super(EventType.AUTHC_SUCCESS, source, remoteIp, coordinates, message);
    }

}
