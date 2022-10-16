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
package com.wl4g.rengine.evaluator.rest;

import javax.inject.Inject;
import javax.transaction.SystemException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.common.model.Evaluation;
import com.wl4g.rengine.common.model.EvaluationResult;
import com.wl4g.rengine.evaluator.rest.interceptor.CustomValid;
import com.wl4g.rengine.evaluator.service.EvaluatorService;

import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link EvaluatorResource}
 * 
 * @author James Wong
 * @version 2022-09-18
 * @since v1.0.0
 * @see https://github.com/quarkusio/quarkus-quickstarts/blob/2.12.Final/jta-quickstart/src/main/java/org/acme/quickstart/TransactionalResource.java
 */
@Slf4j
@Path("/evaluator")
@CustomValid
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EvaluatorResource {

    /**
     * Quarkus 正在使用 GraalVM 构建本机可执行文件。GraalVM
     * 的限制之一是反射的使用。支持反射操作，但必须明确注册所有相关成员以进行反射。这些注册会产生更大的本机可执行文件。 如果 Quarkus DI
     * 需要访问私有成员，它必须使用反射。这就是为什么鼓励 Quarkus 用户不要在他们的
     * bean中使用私有成员的原因。这涉及注入字段、构造函数和初始化程序、观察者方法、生产者方法和字段、处置器和拦截器方法。
     * 
     * @see https://quarkus.io/guides/cdi-reference#native-executables-and-private-members
     */
    @Inject
    EvaluatorService evaluatorService;

    /**
     * Process requests from business applications are evaluated against a rules
     * model.
     * 
     * @param model
     * @return
     * @throws SystemException
     * @see https://quarkus.io/guides/resteasy-reactive#asyncreactive-support
     */
    @POST
    @Path("/evaluate")
    public Uni<RespBase<EvaluationResult>> evaluate(Evaluation model) throws SystemException {
        log.info("called: evaluate ...");
        return evaluatorService.evaluate(model);
    }

}
