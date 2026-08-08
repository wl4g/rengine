package com.wl4g.rengine.common.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class BsonAggregateFilters {

    private static final String ENABLE_MATCH_PREFIX = "{ $match: { \"enable\": { $eq: ";

    public static DBObject createEnableMatch(boolean enable) {
        return BasicDBObject.parse(ENABLE_MATCH_PREFIX + enable + " } }");
    }

    public static DBObject createEnableMatch(String enable) {
        return BasicDBObject.parse(ENABLE_MATCH_PREFIX + "\"" + enable + "\" } }");
    }

    public static DBObject createEnableMatch(int enable) {
        return BasicDBObject.parse(ENABLE_MATCH_PREFIX + enable + " } }");
    }

    public static DBObject createEnableMatch(long enable) {
        return BasicDBObject.parse(ENABLE_MATCH_PREFIX + enable + " } }");
    }

    public static DBObject createEnableMatch(double enable) {
        return BasicDBObject.parse(ENABLE_MATCH_PREFIX + enable + " } }");
    }

    public static DBObject createEnableMatch(float enable) {
        return BasicDBObject.parse(ENABLE_MATCH_PREFIX + enable + " } }");
    }

    public static DBObject createEnableMatch(Object enable) {
        return BasicDBObject.parse(ENABLE_MATCH_PREFIX + enable + " } }");
    }
}
