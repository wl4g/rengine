package com.wl4g.rengine.common.util;

import org.bson.conversions.Bson;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

public class BsonAggregateFilters {
    private static final String ENABLE_MATCH_PREFIX = "{ $match: { \"enable\": { $eq: ";

    public static Bson createEnableMatch(boolean enable) {
        return Aggregates.match(Filters.eq("enable", enable));
    }

    public static Bson createEnableMatch(String enable) {
        return Aggregates.match(Filters.eq("enable", enable));
    }

    public static Bson createEnableMatch(int enable) {
        return Aggregates.match(Filters.eq("enable", enable));
    }

    public static Bson createEnableMatch(long enable) {
        return Aggregates.match(Filters.eq("enable", enable));
    }

    public static Bson createEnableMatch(double enable) {
        return Aggregates.match(Filters.eq("enable", enable));
    }

    public static Bson createEnableMatch(Object enable) {
        return Aggregates.match(Filters.eq("enable", enable));
    }

    public static String getEnableMatchPrefix() {
        return ENABLE_MATCH_PREFIX;
    }
}
