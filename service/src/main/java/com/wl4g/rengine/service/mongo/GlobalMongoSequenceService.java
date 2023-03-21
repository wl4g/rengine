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
package com.wl4g.rengine.service.mongo;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_GLOBAL_SEQUENCES;
import static java.util.Objects.isNull;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;

import com.wl4g.rengine.common.entity.RuleScript;
import com.wl4g.rengine.common.entity.graph.WorkflowGraph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link GlobalMongoSequenceService}
 * 
 * @author James Wong
 * @version 2022-12-14
 * @since v1.0.0
 */
@AllArgsConstructor
public class GlobalMongoSequenceService {

    final MongoOperations mongoOperations;

    public long getNextSequence(final @NotBlank String seqName) {
        hasTextOf(seqName, "seqName");
        final FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(true);
        final GlobalSequence seq = mongoOperations.findAndModify(query(where("_id").is(seqName)), new Update().inc("seq", 1),
                options, GlobalSequence.class, SYS_GLOBAL_SEQUENCES.getName());
        return !isNull(seq) ? seq.getSeq() : 1;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class GlobalSequence {
        @Id
        String id;
        long seq;
    }

    public static final String GRAPHS_REVISION_SEQ = WorkflowGraph.class.getSimpleName() + ".revision";
    public static final String SCRIPTS_REVISION_SEQ = RuleScript.class.getSimpleName() + ".revision";

}
