package com.wl4g.rengine.common.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class BsonAggregateFilters {
    private static final String ENABLE_MATCH_PREFIX = "{ $match: { \"enable\": { $eq: ";

    public static DBObject createEnableFilter(boolean enable) {
        return BasicDBObject.parse(ENABLE_MATCH_PREFIX + enable + " } }");
    }

    public static DBObject createEnableFilterWithAdditionalCriteria(boolean enable, String additionalCriteria) {
        return BasicDBObject.parse(ENABLE_MATCH_PREFIX + enable + ", " + additionalCriteria + " } }");
    }

    // Other methods that used the duplicated string now use the constant
}
