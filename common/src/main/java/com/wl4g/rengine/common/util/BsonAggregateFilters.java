package com.wl4g.rengine.common.util;

public class BsonAggregateFilters {
    private static final String ENABLE_MATCH_PREFIX = "{ $match: { \"enable\": { $eq: ";

    public static String buildEnableFilter(boolean enable) {
        return ENABLE_MATCH_PREFIX + enable + " } }";
    }

    // ... other methods ...
}
