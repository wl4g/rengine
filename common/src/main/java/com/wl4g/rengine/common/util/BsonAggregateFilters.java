package com.wl4g.rengine.common.util;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

/**
 * Utility for building BSON aggregate filter documents.
 */
public final class BsonAggregateFilters {

    private static final String ENABLE_MATCH_PREFIX = "{ $match: { \"enable\": { $eq: ";

    private BsonAggregateFilters() {
        // Prevent instantiation
    }

    /**
     * Builds a filter that matches documents where the enable field equals the given value.
     *
     * @param enable the boolean value to match
     * @return a Document representing the match filter
     */
    public static Document enableMatch(boolean enable) {
        return Document.parse(ENABLE_MATCH_PREFIX + enable + " } }");
    }

    /**
     * Builds a filter that matches documents where the enable field equals true.
     *
     * @return a Document representing the match filter
     */
    public static Document enabledOnly() {
        return enableMatch(true);
    }

    /**
     * Builds a filter that matches documents where the enable field equals false.
     *
     * @return a Document representing the match filter
     */
    public static Document disabledOnly() {
        return enableMatch(false);
    }

    /**
     * Builds a filter that matches documents where the enable field equals the given value and also matches additional criteria.
     *
     * @param enable the boolean value to match
     * @param additionalCriteria additional criteria to include in the match
     * @return a Document representing the combined match filter
     */
    public static Document enableMatchWithCriteria(boolean enable, Document additionalCriteria) {
        Document match = enableMatch(enable);
        if (additionalCriteria != null) {
            match.putAll(additionalCriteria);
        }
        return match;
    }

    /**
     * Builds a filter that matches documents where the enable field equals true and also matches additional criteria.
     *
     * @param additionalCriteria additional criteria to include in the match
     * @return a Document representing the combined match filter
     */
    public static Document enabledOnlyWithCriteria(Document additionalCriteria) {
        return enableMatchWithCriteria(true, additionalCriteria);
    }

    /**
     * Builds a filter that matches documents where the enable field equals false and also matches additional criteria.
     *
     * @param additionalCriteria additional criteria to include in the match
     * @return a Document representing the combined match filter
     */
    public static Document disabledOnlyWithCriteria(Document additionalCriteria) {
        return enableMatchWithCriteria(false, additionalCriteria);
    }

    /**
     * Builds a filter that matches documents where the enable field equals the given value and also matches additional criteria.
     *
     * @param enable the boolean value to match
     * @param additionalCriteria additional criteria to include in the match
     * @return a Document representing the combined match filter
     */
    public static Document enableMatchWithCriteria(boolean enable, String additionalCriteriaJson) {
        Document additionalCriteria = Document.parse(additionalCriteriaJson);
        return enableMatchWithCriteria(enable, additionalCriteria);
    }

    /**
     * Builds a filter that matches documents where the enable field equals true and also matches additional criteria.
     *
     * @param additionalCriteriaJson additional criteria in JSON format
     * @return a Document representing the combined match filter
     */
    public static Document enabledOnlyWithCriteria(String additionalCriteriaJson) {
        return enableMatchWithCriteria(true, additionalCriteriaJson);
    }

    /**
     * Builds a filter that matches documents where the enable field equals false and also matches additional criteria.
     *
     * @param additionalCriteriaJson additional criteria in JSON format
     * @return a Document representing the combined match filter
     */
    public static Document disabledOnlyWithCriteria(String additionalCriteriaJson) {
        return enableMatchWithCriteria(false, additionalCriteriaJson);
    }
}
