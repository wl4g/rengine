/*
 * Copyright 2017 ~ 2025 the original authors James Wong.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.job.analytic.core.hbase;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.lang.EnvironmentUtil.getIntProperty;
import static com.wl4g.infra.common.lang.EnvironmentUtil.getStringProperty;
import static com.wl4g.infra.common.lang.StringUtils2.getBytes;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.connector.hbase.sink.HBaseMutationConverter;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;

import com.wl4g.rengine.common.event.RengineEvent.EventLocation;
import com.wl4g.rengine.common.event.RengineEvent.EventSource;
import com.wl4g.rengine.job.analytic.core.model.RengineEventAnalytical;

/**
 * {@link EventToMutationConverter}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-06-06 v3.0.0
 * @since v3.0.0
 */
public class EventToMutationConverter implements HBaseMutationConverter<RengineEventAnalytical> {
    private static final long serialVersionUID = 1L;

    private final byte[] nullStringBytes;

    public EventToMutationConverter() {
        this(new byte[0]);
    }

    public EventToMutationConverter(byte[] nullStringBytes) {
        this.nullStringBytes = notNullOf(nullStringBytes, "nullStringBytes");
    }

    @Override
    public void open() {
    }

    @Override
    public Mutation convertToMutation(@NotNull RengineEventAnalytical model) {
        notNullOf(model, "model");
        model.validate();

        final Put put = new Put(generateRowkey(model));

        // Automatic mapped.
        // for (Field f : RengineEventAnalytical.ORDERED_FIELDS) {
        // byte[] value = nullStringBytes;
        // Object v = getField(f, model, true);
        // if (nonNull(v)) {
        // value = getBytes(v.toString());
        // }
        // addPutColumn(put, f.getName(), value);
        // }

        // observedTime
        addPutColumn(put, "observedTime", DateFormatUtils.format(model.getObservedTime(), "yyMMddHHmmss"));
        // body
        addPutColumn(put, "body", model.getBody());
        // attributes
        addPutColumn(put, "attributes", toJSONString(model.getAttributes()));
        // source
        final EventSource source = (EventSource) model.getSource();
        if (nonNull(source)) {
            addPutColumn(put, "sourceTime", DateFormatUtils.format(source.getTime(), "yyMMddHHmmss"));
            addPutColumn(put, "sourcePrincipals", getSourcePrincipalsString(source));
        }
        // source location
        final EventLocation location = source.getLocation();
        if (nonNull(location)) {
            addPutColumn(put, "locationIpAddress", location.getIpAddress());
            addPutColumn(put, "locationIpv6", (nonNull(location.getIpv6()) && location.getIpv6()) ? 1 : 0);
            addPutColumn(put, "locationIsp", location.getIsp());
            addPutColumn(put, "locationDomain", location.getDomain());
            addPutColumn(put, "locationElevation", location.getElevation());
            addPutColumn(put, "locationLatitude", location.getLatitude());
            addPutColumn(put, "locationLongitude", location.getLongitude());
            addPutColumn(put, "locationZipcode", location.getZipcode());
            addPutColumn(put, "locationTimezone", location.getTimezone());
            addPutColumn(put, "locationCity", getGeoCityKey(source));
            addPutColumn(put, "locationRegion", getGeoRegionKey(source));
            addPutColumn(put, "locationCountry", getGeoCountryKey(source));
        }

        return put;
    }

    protected void addPutColumn(@NotNull Put put, @NotBlank String field, @NotNull Serializable value) {
        value = isNull(value) ? nullStringBytes : value;
        put.addColumn(getBytes("info"), getBytes(field), (value instanceof byte[]) ? (byte[]) value : getBytes(value.toString()));
    }

    protected byte[] generateRowkey(@NotNull RengineEventAnalytical model) {
        EventSource source = (EventSource) model.getSource();

        // Use reversed time strings to avoid data hotspots.
        // Options are: SSSssmmHHddMMyy | SSSyyMMddHHmmss?
        String reverseDate = DateFormatUtils.format(source.getTime(), "SSSyyMMddHHmmss");

        // TODO transform to ZIPCODE-standard city/region/country name.
        return new StringBuilder()
                // when
                .append(reverseDate)
                // who
                .append(ROWKEY_SPEARATOR)
                .append(getSourcePrincipalsString(source))
                // what
                .append(ROWKEY_SPEARATOR)
                .append(model.getType())
                // where
                // TODO 未拿到准确且完整的区域编码字典，无法约束采集源端传来的值，暂时只能放到非 RowKey，
                // 但由于管理端 “数据洞察”->“事件分析” 支持查看事件统计/分析，其中 echarts 按照全球地图展示，因此最好还是
                // 使用一份完整的 Geo-ZipCode 映射字典
                .append(ROWKEY_SPEARATOR)
                .append(getGeoCityKey(source))
                .append(ROWKEY_SPEARATOR)
                .append(getGeoRegionKey(source))
                .append(ROWKEY_SPEARATOR)
                .append(getGeoCountryKey(source))
                .toString()
                .getBytes(UTF_8);
    }

    protected String getSourcePrincipalsString(@NotNull EventSource source) {
        return join(safeList(source.getPrincipals()).toArray(), "|");
    }

    protected String getGeoCityKey(@NotNull EventSource source) {
        return fixFieldRowKey(source.getLocation().getCity());
    }

    protected String getGeoRegionKey(@NotNull EventSource source) {
        return fixFieldRowKey(source.getLocation().getRegion());
    }

    protected String getGeoCountryKey(@NotNull EventSource source) {
        return fixFieldRowKey(source.getLocation().getCountry());
    }

    /**
     * Fix for example:
     * 
     * <pre>
     *  IP2LocationRecord:
     *      IP Address = 1.1.1.1
     *      Country Short = US
     *      Country Long = United States of America
     *      Region = California
     *      City = Los Angeles
     *      ISP = Not_Supported
     *      Latitude = 34.05223
     *      Longitude = -118.24368
     *      Domain = Not_Supported
     *      ZipCode = 90001
     *      TimeZone = -07:00
     *      NetSpeed = Not_Supported
     *      IDDCode = Not_Supported
     *      AreaCode = Not_Supported
     *      WeatherStationCode = Not_Supported
     *      WeatherStationName = Not_Supported
     *      MCC = Not_Supported
     *      MNC = Not_Supported
     *      MobileBrand = Not_Supported
     *      Elevation = 0.0
     *      UsageType = Not_Supported
     *      AddressType = Not_Supported
     *      Category = Not_Supported
     * </pre>
     **/
    protected String fixFieldRowKey(@Nullable String fieldValue) {
        if (isBlank(fieldValue)) {
            return EMPTY;
        }
        String cleanFieldKey = trimToEmpty(fieldValue).toLowerCase().replace(" ", "_");
        // Limit field max length.
        if (cleanFieldKey.length() > ROWKEY_PART_MAX_LEN) {
            cleanFieldKey = cleanFieldKey.substring(0, ROWKEY_PART_MAX_LEN);
        }
        return cleanFieldKey;
    }

    public static final int ROWKEY_PART_MAX_LEN = getIntProperty("HBASE_ROWKEY_PART_MAX_LEN", 16);
    public static final String ROWKEY_SPEARATOR = getStringProperty("HBASE_ROWKEY_SPEARATOR", ":");

}
