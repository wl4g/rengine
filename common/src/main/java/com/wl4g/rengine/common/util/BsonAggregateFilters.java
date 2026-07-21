/*
 * Copyright 2023 the original author or authors.
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

package com.wl4g.rengine.common.util;

import static com.wl4g.rengine.common.constants.RengineConstants.*;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

/**
 * Utility class for building BSON aggregate filter pipelines.
 *
 * @author James Wong
 */
public class BsonAggregateFilters {

    private static final String MATCH_ENABLE_EQ = "{ $match: { \"enable\": { $eq: ";

    private BsonAggregateFilters() {
        // utility class
    }

    public static List<Bson> buildEnableFilter() {
        List<Bson> pipeline = new ArrayList<>();
        pipeline.add(Aggregates.match(Filters.eq("enable", true)));
        return pipeline;
    }

    // other methods...
}
