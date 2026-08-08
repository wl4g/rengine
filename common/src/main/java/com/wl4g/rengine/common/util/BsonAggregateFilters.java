package com.wl4g.rengine.common.util;

public class BsonAggregateFilters {
    private static final String ENABLE_MATCH = "{ $match: { \"enable\": { $eq: ";

    public static String filter1() {
        return ENABLE_MATCH + "true } }";
    }

    public static String filter2() {
        return ENABLE_MATCH + "false } }";
    }

    public static String filter3() {
        return ENABLE_MATCH + "1 } }";
    }

    public static String filter4() {
        return ENABLE_MATCH + "0 } }";
    }

    public static String filter5() {
        return ENABLE_MATCH + "\"true\" } }";
    }

    public static String filter6() {
        return ENABLE_MATCH + "\"false\" } }";
    }

    public static String filter7() {
        return ENABLE_MATCH + "null } }";
    }
}
