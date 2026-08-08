package com.wl4g.rengine.common.util;

public class BsonAggregateFilters {
    private static final String ENABLE_MATCH = "{ $match: { \"enable\": { $eq: ";

    public String buildEnableFilter(boolean enable) {
        return ENABLE_MATCH + enable + " } }";
    }

    public String buildEnableFilterWithAdditional(String additional) {
        return ENABLE_MATCH + "true, " + additional + " } }";
    }

    public String buildEnableFilterForCount() {
        return ENABLE_MATCH + "true } }";
    }

    public String buildEnableFilterForUpdate() {
        return ENABLE_MATCH + "false } }";
    }

    public String buildEnableFilterForDelete() {
        return ENABLE_MATCH + "false } }";
    }

    public String buildEnableFilterForQuery() {
        return ENABLE_MATCH + "true } }";
    }

    public String buildEnableFilterForAggregation() {
        return ENABLE_MATCH + "true } }";
    }
}
