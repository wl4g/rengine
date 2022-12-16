/*
 * Copyright 2016-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.common.util;

import static java.util.Objects.nonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import javax.annotation.Nullable;

import org.bson.BSONObject;
import org.bson.BsonBinary;
import org.bson.BsonBoolean;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonParseException;
import org.bson.json.JsonWriterSettings;
import org.bson.json.StrictJsonWriter;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.mongodb.MongoClientSettings;
import com.wl4g.infra.common.collection.CollectionUtils2;
import com.wl4g.infra.common.lang.Assert2;
import com.wl4g.infra.common.lang.DateUtils2;
import com.wl4g.infra.common.lang.ObjectUtils2;
import com.wl4g.infra.common.lang.StringUtils2;

import lombok.CustomLog;

/**
 * Internal API for operations on {@link Bson} elements that can be either
 * {@link Document} or {@link DBObject}.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @since 2.0
 * @since Modification based on
 *        {@link com.wl4g.rengine.common.util.BsonUtils2.data.mongodb.util.BsonUtils}
 */
public abstract class BsonUtils2 {

    /**
     * The empty document (immutable). This document is serializable.
     *
     * @since 3.2.5
     */
    public static final Document EMPTY_DOCUMENT = new EmptyDocument();

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T get(Bson bson, String key) {
        return (T) asMap(bson).get(key);
    }

    /**
     * Return the {@link Bson} object as {@link Map}. Depending on the input
     * type, the return value can be either a casted version of {@code bson} or
     * a converted (detached from the original value).
     *
     * @param bson
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map<String, Object> asMap(Bson bson) {
        if (bson instanceof Document) {
            return (Document) bson;
        }
        if (bson instanceof BasicDBObject) {
            return ((BasicDBObject) bson);
        }
        if (bson instanceof DBObject) {
            return ((DBObject) bson).toMap();
        }
        return (Map) bson.toBsonDocument(Document.class, MongoClientSettings.getDefaultCodecRegistry());
    }

    /**
     * Return the {@link Bson} object as {@link Document}. Depending on the
     * input type, the return value can be either a casted version of
     * {@code bson} or a converted (detached from the original value).
     *
     * @param bson
     * @return
     * @since 3.2.5
     */
    public static Document asDocument(Bson bson) {

        if (bson instanceof Document) {
            return (Document) bson;
        }

        Map<String, Object> map = asMap(bson);

        if (map instanceof Document) {
            return (Document) map;
        }

        return new Document(map);
    }

    /**
     * Return the {@link Bson} object as mutable {@link Document} containing all
     * entries from {@link Bson}.
     *
     * @param bson
     * @return a mutable {@link Document} containing all entries from
     *         {@link Bson}.
     * @since 3.2.5
     */
    public static Document asMutableDocument(Bson bson) {

        if (bson instanceof EmptyDocument) {
            bson = new Document(asDocument(bson));
        }

        if (bson instanceof Document) {
            return (Document) bson;
        }

        Map<String, Object> map = asMap(bson);

        if (map instanceof Document) {
            return (Document) map;
        }

        return new Document(map);
    }

    public static void addToMap(Bson bson, String key, @Nullable Object value) {

        if (bson instanceof Document) {

            ((Document) bson).put(key, value);
            return;
        }
        if (bson instanceof BSONObject) {

            ((BSONObject) bson).put(key, value);
            return;
        }

        throw new IllegalArgumentException(String.format(
                "Cannot add key/value pair to %s. as map. Given Bson must be a Document or BSONObject!", bson.getClass()));
    }

    /**
     * Add all entries from the given {@literal source} {@link Map} to the
     * {@literal target}.
     *
     * @param target
     *            must not be {@literal null}.
     * @param source
     *            must not be {@literal null}.
     * @since 3.2
     */
    public static void addAllToMap(Bson target, Map<String, ?> source) {

        if (target instanceof Document) {

            ((Document) target).putAll(source);
            return;
        }

        if (target instanceof BSONObject) {

            ((BSONObject) target).putAll(source);
            return;
        }

        throw new IllegalArgumentException(
                String.format("Cannot add all to %s. Given Bson must be a Document or BSONObject.", target.getClass()));
    }

    /**
     * Check if a given entry (key/value pair) is present in the given
     * {@link Bson}.
     *
     * @param bson
     *            must not be {@literal null}.
     * @param key
     *            must not be {@literal null}.
     * @param value
     *            can be {@literal null}.
     * @return {@literal true} if (key/value pair) is present.
     * @since 3.2
     */
    public static boolean contains(Bson bson, String key, @Nullable Object value) {

        if (bson instanceof Document) {

            Document doc = (Document) bson;
            return doc.containsKey(key) && ObjectUtils2.nullSafeEquals(doc.get(key), value);
        }
        if (bson instanceof BSONObject) {

            BSONObject bsonObject = (BSONObject) bson;
            return bsonObject.containsField(key) && ObjectUtils2.nullSafeEquals(bsonObject.get(key), value);
        }

        Map<String, Object> map = asMap(bson);
        return map.containsKey(key) && ObjectUtils2.nullSafeEquals(map.get(key), value);
    }

    /**
     * Remove {@code _id : null} from the given {@link Bson} if present.
     *
     * @param bson
     *            must not be {@literal null}.
     * @since 3.2
     */
    public static boolean removeNullId(Bson bson) {

        if (!contains(bson, "_id", null)) {
            return false;
        }

        removeFrom(bson, "_id");
        return true;
    }

    /**
     * Remove the given {@literal key} from the {@link Bson} value.
     *
     * @param bson
     *            must not be {@literal null}.
     * @param key
     *            must not be {@literal null}.
     * @since 3.2
     */
    static void removeFrom(Bson bson, String key) {

        if (bson instanceof Document) {

            ((Document) bson).remove(key);
            return;
        }

        if (bson instanceof BSONObject) {

            ((BSONObject) bson).removeField(key);
            return;
        }

        throw new IllegalArgumentException(
                String.format("Cannot remove from %s. Given Bson must be a Document or BSONObject.", bson.getClass()));
    }

    /**
     * Extract the corresponding plain value from {@link BsonValue}. Eg. plain
     * {@link String} from {@link org.bson.BsonString}.
     *
     * @param value
     *            must not be {@literal null}.
     * @return
     * @since 2.1
     */
    public static Object toJavaType(BsonValue value) {

        switch (value.getBsonType()) {
        case INT32:
            return value.asInt32().getValue();
        case INT64:
            return value.asInt64().getValue();
        case STRING:
            return value.asString().getValue();
        case DECIMAL128:
            return value.asDecimal128().doubleValue();
        case DOUBLE:
            return value.asDouble().getValue();
        case BOOLEAN:
            return value.asBoolean().getValue();
        case OBJECT_ID:
            return value.asObjectId().getValue();
        case DB_POINTER:
            return new DBRef(value.asDBPointer().getNamespace(), value.asDBPointer().getId());
        case BINARY:
            return value.asBinary().getData();
        case DATE_TIME:
            return new Date(value.asDateTime().getValue());
        case SYMBOL:
            return value.asSymbol().getSymbol();
        case ARRAY:
            return value.asArray().toArray();
        case DOCUMENT:
            return Document.parse(value.asDocument().toJson());
        default:
            return value;
        }
    }

    /**
     * Convert a given simple value (eg. {@link String}, {@link Long}) to its
     * corresponding {@link BsonValue}.
     *
     * @param source
     *            must not be {@literal null}.
     * @return the corresponding {@link BsonValue} representation.
     * @throws IllegalArgumentException
     *             if {@literal source} does not correspond to a
     *             {@link BsonValue} type.
     * @since 3.0
     */
    public static BsonValue simpleToBsonValue(Object source) {

        if (source instanceof BsonValue) {
            return (BsonValue) source;
        }

        if (source instanceof ObjectId) {
            return new BsonObjectId((ObjectId) source);
        }

        if (source instanceof String) {
            return new BsonString((String) source);
        }

        if (source instanceof Double) {
            return new BsonDouble((Double) source);
        }

        if (source instanceof Integer) {
            return new BsonInt32((Integer) source);
        }

        if (source instanceof Long) {
            return new BsonInt64((Long) source);
        }

        if (source instanceof byte[]) {
            return new BsonBinary((byte[]) source);
        }

        if (source instanceof Boolean) {
            return new BsonBoolean((Boolean) source);
        }

        if (source instanceof Float) {
            return new BsonDouble((Float) source);
        }

        throw new IllegalArgumentException(String.format("Unable to convert %s (%s) to BsonValue.", source,
                source != null ? source.getClass().getName() : "null"));
    }

    /**
     * Merge the given {@link Document documents} into on in the given order.
     * Keys contained within multiple documents are overwritten by their follow
     * ups.
     *
     * @param documents
     *            must not be {@literal null}. Can be empty.
     * @return the document containing all key value pairs.
     * @since 2.2
     */
    public static Document merge(Document... documents) {

        if (ObjectUtils2.isEmpty(documents)) {
            return new Document();
        }

        if (documents.length == 1) {
            return documents[0];
        }

        Document target = new Document();
        Arrays.asList(documents).forEach(target::putAll);
        return target;
    }

    /**
     * @param source
     * @param orElse
     * @return
     * @since 2.2
     */
    public static Document toDocumentOrElse(String source, Function<String, Document> orElse) {

        if (StringUtils2.trimLeadingWhitespace(source).startsWith("{")) {
            return Document.parse(source);
        }

        return orElse.apply(source);
    }

    /**
     * Serialize the given {@link Document} as Json applying default codecs if
     * necessary.
     *
     * @param source
     * @return
     * @since 2.2.1
     */
    @Nullable
    public static String toJson(@Nullable Document source) {
        if (source == null) {
            return null;
        }
        try {
            return source.toJson();
        } catch (Exception e) {
            return toJson((Object) source);
        }
    }

    //
    // [BEING] ADD json bson convert FEATURE
    //

    /**
     * Serialize the given {@link Document} as Json applying default codecs if
     * necessary.
     *
     * @param source
     * @return
     * @since 2.2.1
     */
    @Nullable
    public static String toJson(@Nullable final JsonWriterSettings writerSettings, @Nullable Document source) {
        if (source == null) {
            return null;
        }
        try {
            return nonNull(writerSettings) ? source.toJson(writerSettings) : source.toJson();
        } catch (Exception e) {
            return toJson((Object) source);
        }
    }

    //
    // [END] ADD json bson convert FEATURE
    //

    /**
     * Check if a given String looks like {@link Document#parse(String)
     * parsable} json.
     *
     * @param value
     *            can be {@literal null}.
     * @return {@literal true} if the given value looks like a json document.
     * @since 3.0
     */
    public static boolean isJsonDocument(@Nullable String value) {

        if (!!StringUtils2.isBlank(value)) {
            return false;
        }

        String potentialJson = value.trim();
        return potentialJson.startsWith("{") && potentialJson.endsWith("}");
    }

    /**
     * Check if a given String looks like
     * {@link org.bson.BsonArray#parse(String) parsable} json array.
     *
     * @param value
     *            can be {@literal null}.
     * @return {@literal true} if the given value looks like a json array.
     * @since 3.0
     */
    public static boolean isJsonArray(@Nullable String value) {
        return !StringUtils2.isBlank(value) && (value.startsWith("[") && value.endsWith("]"));
    }

    /**
     * Resolve a the value for a given key. If the given {@link Bson} value
     * contains the key the value is immediately returned. If not and the key
     * contains a path using the dot ({@code .}) notation it will try to resolve
     * the path by inspecting the individual parts. If one of the intermediate
     * ones is {@literal null} or cannot be inspected further (wrong) type,
     * {@literal null} is returned.
     *
     * @param bson
     *            the source to inspect. Must not be {@literal null}.
     * @param key
     *            the key to lookup. Must not be {@literal null}.
     * @return can be {@literal null}.
     * @since 3.0.8
     */
    @Nullable
    public static Object resolveValue(Bson bson, String key) {

        Map<String, Object> source = asMap(bson);

        if (source.containsKey(key) || !key.contains(".")) {
            return source.get(key);
        }

        String[] parts = key.split("\\.");

        for (int i = 1; i < parts.length; i++) {

            Object result = source.get(parts[i - 1]);

            if (!(result instanceof Bson)) {
                return null;
            }

            source = asMap((Bson) result);
        }

        return source.get(parts[parts.length - 1]);
    }

    /**
     * Returns whether the underlying {@link Bson bson} has a value
     * ({@literal null} or non-{@literal null}) for the given {@code key}.
     *
     * @param bson
     *            the source to inspect. Must not be {@literal null}.
     * @param key
     *            the key to lookup. Must not be {@literal null}.
     * @return {@literal true} if no non {@literal null} value present.
     * @since 3.0.8
     */
    public static boolean hasValue(Bson bson, String key) {

        Map<String, Object> source = asMap(bson);

        if (source.get(key) != null) {
            return true;
        }

        if (!key.contains(".")) {
            return false;
        }

        String[] parts = key.split("\\.");

        Object result;

        for (int i = 1; i < parts.length; i++) {

            result = source.get(parts[i - 1]);
            source = getAsMap(result);

            if (source == null) {
                return false;
            }
        }

        return source.containsKey(parts[parts.length - 1]);
    }

    /**
     * Returns the given source object as map, i.e. {@link Document}s and maps
     * as is or {@literal null} otherwise.
     *
     * @param source
     *            can be {@literal null}.
     * @return can be {@literal null}.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    private static Map<String, Object> getAsMap(Object source) {

        if (source instanceof Document) {
            return (Document) source;
        }

        if (source instanceof BasicDBObject) {
            return (BasicDBObject) source;
        }

        if (source instanceof DBObject) {
            return ((DBObject) source).toMap();
        }

        if (source instanceof Map) {
            return (Map<String, Object>) source;
        }

        return null;
    }

    /**
     * Returns the given source object as {@link Bson}, i.e. {@link Document}s
     * and maps as is or throw {@link IllegalArgumentException}.
     *
     * @param source
     * @return the converted/casted source object.
     * @throws IllegalArgumentException
     *             if {@code source} cannot be converted/cast to {@link Bson}.
     * @since 3.2.3
     * @see #supportsBson(Object)
     */
    @SuppressWarnings("unchecked")
    public static Bson asBson(Object source) {

        if (source instanceof Document) {
            return (Document) source;
        }

        if (source instanceof BasicDBObject) {
            return (BasicDBObject) source;
        }

        if (source instanceof DBObject) {
            return new Document(((DBObject) source).toMap());
        }

        if (source instanceof Map) {
            return new Document((Map<String, Object>) source);
        }

        throw new IllegalArgumentException(String.format("Cannot convert %s to Bson", source));
    }

    /**
     * Returns the given source can be used/converted as {@link Bson}.
     *
     * @param source
     * @return {@literal true} if the given source can be converted to
     *         {@link Bson}.
     * @since 3.2.3
     */
    public static boolean supportsBson(Object source) {
        return source instanceof DBObject || source instanceof Map;
    }

    /**
     * Returns given object as {@link Collection}. Will return the
     * {@link Collection} as is if the source is a {@link Collection} already,
     * will convert an array into a {@link Collection} or simply create a single
     * element collection for everything else.
     *
     * @param source
     *            must not be {@literal null}.
     * @return never {@literal null}.
     * @since 3.2
     */
    public static Collection<?> asCollection(Object source) {

        if (source instanceof Collection) {
            return (Collection<?>) source;
        }

        return source.getClass().isArray() ? CollectionUtils2.arrayToList(source) : Collections.singleton(source);
    }

    @Nullable
    public static String toJson(@Nullable Object value) {
        if (value == null) {
            return null;
        }
        try {
            return value instanceof Document
                    ? ((Document) value).toJson(MongoClientSettings.getDefaultCodecRegistry().get(Document.class))
                    : serializeValue(value);

        } catch (Exception e) {

            if (value instanceof Collection) {
                return toString((Collection<?>) value);
            } else if (value instanceof Map) {
                return toString((Map<?, ?>) value);
            } else if (ObjectUtils2.isArray(value)) {
                return toString(Arrays.asList(ObjectUtils2.toObjectArray(value)));
            }

            throw e instanceof JsonParseException ? (JsonParseException) e : new JsonParseException(e);
        }
    }

    private static String serializeValue(@Nullable Object value) {

        if (value == null) {
            return "null";
        }

        String documentJson = new Document("toBeEncoded", value).toJson();
        return documentJson.substring(documentJson.indexOf(':') + 1, documentJson.length() - 1).trim();
    }

    private static String toString(Map<?, ?> source) {
        return iterableToDelimitedString(source.entrySet(), "{ ", " }",
                entry -> String.format("\"%s\" : %s", entry.getKey(), toJson(entry.getValue())));
    }

    private static String toString(Collection<?> source) {
        return iterableToDelimitedString(source, "[ ", " ]", BsonUtils2::toJson);
    }

    private static <T> String iterableToDelimitedString(
            Iterable<T> source,
            String prefix,
            String suffix,
            Converter<? super T, String> transformer) {

        StringJoiner joiner = new StringJoiner(", ", prefix, suffix);

        StreamSupport.stream(source.spliterator(), false).map(transformer::convert).forEach(joiner::add);

        return joiner.toString();
    }

    /**
     * Empty variant of {@link Document}.
     *
     * @author Mark Paluch
     */
    static class EmptyDocument extends Document {
        private static final long serialVersionUID = 1L;

        @Override
        public Document append(String key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object put(String key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends String, ?> map) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(String key, Object oldValue, Object newValue) {
            throw new UnsupportedOperationException();
        }

        @Nullable
        @Override
        public Object replace(String key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<Entry<String, Object>> entrySet() {
            return Collections.emptySet();
        }

        @Override
        public Collection<Object> values() {
            return Collections.emptyList();
        }

        @Override
        public Set<String> keySet() {
            return Collections.emptySet();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

    }

    @FunctionalInterface
    static interface Converter<S, T> {

        /**
         * Convert the source object of type {@code S} to target type {@code T}.
         * 
         * @param source
         *            the source object to convert, which must be an instance of
         *            {@code S} (never {@code null})
         * @return the converted object, which must be an instance of {@code T}
         *         (potentially {@code null})
         * @throws IllegalArgumentException
         *             if the source cannot be converted to the desired target
         *             type
         */
        @Nullable
        T convert(S source);

        /**
         * Construct a composed {@link Converter} that first applies this
         * {@link Converter} to its input, and then applies the {@code after}
         * {@link Converter} to the result.
         * 
         * @param after
         *            the {@link Converter} to apply after this
         *            {@link Converter} is applied
         * @param <U>
         *            the type of output of both the {@code after}
         *            {@link Converter} and the composed {@link Converter}
         * @return a composed {@link Converter} that first applies this
         *         {@link Converter} and then applies the {@code after}
         *         {@link Converter}
         * @since 5.3
         */
        default <U> Converter<S, U> andThen(Converter<? super T, ? extends U> after) {
            Assert2.notNull(after, "After Converter must not be null");
            return (S s) -> {
                T initialResult = convert(s);
                return (initialResult != null ? after.convert(initialResult) : null);
            };
        }

    }

    /**
     * @see https://www.baeldung.com/java-convert-bson-to-json
     */
    @CustomLog
    public static class JsonDateTimeConverter implements org.bson.json.Converter<Long> {
        // static final DateTimeFormatter DATE_TIME_FORMATTER =
        // DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC+8"));

        @Override
        public void convert(Long value, StrictJsonWriter writer) {
            try {
                // Instant instant = new Date(value).toInstant();
                // String s = DATE_TIME_FORMATTER.format(instant);
                // writer.writeString(s);
                writer.writeString(DateUtils2.formatDate(new Date(value), "yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                log.error(String.format("Failed to convert offset %d to JSON date", value), e);
            }
        }
    }

    public static final JsonWriterSettings DEFAULT_JSON_WRITER_SETTINGS = JsonWriterSettings.builder()
            .dateTimeConverter(new JsonDateTimeConverter())
            .build();
}