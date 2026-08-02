package com.wl4g.rengine.common.util;

public class BsonAggregateFilters {
    private static final String ENABLE_MATCH = "{ $match: { \"enable\": { $eq: ";

    // Use ENABLE_MATCH constant
    public void buildFilter() {
        String filter = ENABLE_MATCH + "true" + " } }";
        // Use filter
    }
}
