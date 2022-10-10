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
import static com.wl4g.infra.common.lang.StringUtils2.getBytes;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.connector.hbase.sink.HBaseMutationConverter;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;

import com.wl4g.rengine.common.event.RengineEvent;
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
        model.getEvent().validate();

        final RengineEvent event = model.getEvent();
        final Put put = new Put(generateRowkey(model));

        // observedTime
        addPutColumn(put, "observedTime", DateFormatUtils.format(event.getObservedTime(), "yyyyMMddHHmmss"));
        // body
        addPutColumn(put, "body", event.getBody());

        return put;
    }

    protected void addPutColumn(@NotNull Put put, @NotBlank String field, @NotNull Object value) {
        if (isNull(value)) {
            value = nullStringBytes;
        }
        put.addColumn(getBytes("f1"), getBytes(field), getBytes(value.toString()));
    }

    protected byte[] generateRowkey(@NotNull RengineEventAnalytical model) {
        EventSource source = (EventSource) model.getEvent().getSource();

        // Use reversed time strings to avoid data hotspots.
        // Options are: SSSssmmHHddMMyy | SSSyyMMddHHmmss?
        String reverseDate = DateFormatUtils.format(source.getTime(), "SSSyyMMddHHmmss");

        // TODO transform to ZIPCODE-standard city/region/country name.
        return new StringBuilder()
                // when
                .append(reverseDate)
                // who
                .append(",")
                .append(join(safeList(source.getPrincipals()).toArray(), ","))
                // what
                .append(",")
                .append(model.getEvent().getType())
                // where
                // TODO 未拿到准确且完整的区域编码字典，无法约束采集源端传来的值，暂时只能放到非 RowKey，
                // 但由于管理端 “数据洞察”->“事件分析” 支持查看事件统计/分析，其中 echarts 按照全球地图展示，因此最好还是
                // 使用一份完整的 Geo-ZipCode 映射字典
                // .append(",")
                // .append(getGeoCityKey(source))
                // .append(",")
                // .append(getGeoRegionKey(source))
                // .append(",")
                // .append(getGeoCountryKey(source))
                .toString()
                .getBytes(UTF_8);
    }

    protected String getGeoCityKey(@NotNull EventSource source) {
        return fixFieldKey(source.getLocation().getCity());
    }

    protected String getGeoRegionKey(@NotNull EventSource source) {
        return fixFieldKey(source.getLocation().getRegion());
    }

    protected String getGeoCountryKey(@NotNull EventSource source) {
        return fixFieldKey(source.getLocation().getCountry());
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
    protected String fixFieldKey(String field) {
        String cleanFieldKey = trimToEmpty(field).toLowerCase().replace(" ", "_");
        // Limit field max length.
        if (cleanFieldKey.length() > DEFAULT_MAX_ROWKEY_FIELD_LENGTH) {
            cleanFieldKey = cleanFieldKey.substring(0, DEFAULT_MAX_ROWKEY_FIELD_LENGTH);
        }
        return cleanFieldKey;
    }

    public static final int DEFAULT_MAX_ROWKEY_FIELD_LENGTH = 24;

}
