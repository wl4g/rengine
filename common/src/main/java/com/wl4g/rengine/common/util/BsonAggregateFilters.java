package com.wl4g.rengine.common.util;

public class BsonAggregateFilters {
    private static final String MATCH_ENABLE = "{ $match: { \"enable\": { $eq: ";

    // Replace all occurrences with MATCH_ENABLE constant
    public void filter() {
        String filter = MATCH_ENABLE + "true } }";
        // Rest of the logic
    }
}
