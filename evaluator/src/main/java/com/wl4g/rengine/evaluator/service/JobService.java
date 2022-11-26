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
package com.wl4g.rengine.evaluator.service;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.wl4g.rengine.common.entity.Job;
import com.wl4g.rengine.common.entity.Scenes;
import com.wl4g.rengine.evaluator.service.impl.JobServiceImpl;

import io.smallrye.mutiny.Uni;

/**
 * {@link JobServiceImpl}
 * 
 * @author James Wong
 * @version 2022-09-17
 * @since v1.0.0
 */
public interface JobService {

    Scenes loadScenesWithCascade(@NotBlank String scenesCode);

    Uni<List<Job>> listAll();

    Uni<Void> save(Job job);

}
