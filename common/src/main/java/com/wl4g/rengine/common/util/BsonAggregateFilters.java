package com.wl4g.rengine.common.util;

public class BsonAggregateFilters {
    private static final String MATCH_ENABLE = "{ $match: { \"enable\": { $eq: ";

    public String buildFilter(String value) {
        return MATCH_ENABLE + value + " } }";
    }

    // ... other methods using MATCH_ENABLE constant
}
