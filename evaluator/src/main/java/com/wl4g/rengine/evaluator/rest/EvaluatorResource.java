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

import javax.transaction.SystemException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link EvaluatorResource}
 * 
 * @author James Wong
 * @version 2022-09-18
 * @since v3.0.0
 * @see https://github.com/quarkusio/quarkus-quickstarts/blob/2.12.Final/jta-quickstart/src/main/java/org/acme/quickstart/TransactionalResource.java
 */
@Slf4j
@Path("/evaluator")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EvaluatorResource {

    @POST
    @Path("evaluate")
    public String evaluate() throws SystemException {
        log.info("called: evaluate ...");

        // TODO

        return "ok";
    }

}
