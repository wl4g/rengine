/*
 * Flink JobManager REST API
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: v1/1.15-SNAPSHOT
 * Contact: user@flink.apache.org
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package com.nextbreakpoint.flinkclient1_15.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * TaskManagerMetricsInfo
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
        date = "2023-04-02T23:14:46.617621+08:00[Asia/Shanghai]")
public class TaskManagerMetricsInfo {
    @SerializedName("heapUsed")
    private Long heapUsed = null;

    @SerializedName("heapCommitted")
    private Long heapCommitted = null;

    @SerializedName("heapMax")
    private Long heapMax = null;

    @SerializedName("nonHeapUsed")
    private Long nonHeapUsed = null;

    @SerializedName("nonHeapCommitted")
    private Long nonHeapCommitted = null;

    @SerializedName("nonHeapMax")
    private Long nonHeapMax = null;

    @SerializedName("directCount")
    private Long directCount = null;

    @SerializedName("directUsed")
    private Long directUsed = null;

    @SerializedName("directMax")
    private Long directMax = null;

    @SerializedName("mappedCount")
    private Long mappedCount = null;

    @SerializedName("mappedUsed")
    private Long mappedUsed = null;

    @SerializedName("mappedMax")
    private Long mappedMax = null;

    @SerializedName("memorySegmentsAvailable")
    private Long memorySegmentsAvailable = null;

    @SerializedName("memorySegmentsTotal")
    private Long memorySegmentsTotal = null;

    @SerializedName("nettyShuffleMemorySegmentsAvailable")
    private Long nettyShuffleMemorySegmentsAvailable = null;

    @SerializedName("nettyShuffleMemorySegmentsUsed")
    private Long nettyShuffleMemorySegmentsUsed = null;

    @SerializedName("nettyShuffleMemorySegmentsTotal")
    private Long nettyShuffleMemorySegmentsTotal = null;

    @SerializedName("nettyShuffleMemoryAvailable")
    private Long nettyShuffleMemoryAvailable = null;

    @SerializedName("nettyShuffleMemoryUsed")
    private Long nettyShuffleMemoryUsed = null;

    @SerializedName("nettyShuffleMemoryTotal")
    private Long nettyShuffleMemoryTotal = null;

    @SerializedName("garbageCollectors")
    private List<GarbageCollectorInfo> garbageCollectors = null;

    public TaskManagerMetricsInfo heapUsed(Long heapUsed) {
        this.heapUsed = heapUsed;
        return this;
    }

    /**
     * Get heapUsed
     * 
     * @return heapUsed
     **/
    @Schema(description = "")
    public Long getHeapUsed() {
        return heapUsed;
    }

    public void setHeapUsed(Long heapUsed) {
        this.heapUsed = heapUsed;
    }

    public TaskManagerMetricsInfo heapCommitted(Long heapCommitted) {
        this.heapCommitted = heapCommitted;
        return this;
    }

    /**
     * Get heapCommitted
     * 
     * @return heapCommitted
     **/
    @Schema(description = "")
    public Long getHeapCommitted() {
        return heapCommitted;
    }

    public void setHeapCommitted(Long heapCommitted) {
        this.heapCommitted = heapCommitted;
    }

    public TaskManagerMetricsInfo heapMax(Long heapMax) {
        this.heapMax = heapMax;
        return this;
    }

    /**
     * Get heapMax
     * 
     * @return heapMax
     **/
    @Schema(description = "")
    public Long getHeapMax() {
        return heapMax;
    }

    public void setHeapMax(Long heapMax) {
        this.heapMax = heapMax;
    }

    public TaskManagerMetricsInfo nonHeapUsed(Long nonHeapUsed) {
        this.nonHeapUsed = nonHeapUsed;
        return this;
    }

    /**
     * Get nonHeapUsed
     * 
     * @return nonHeapUsed
     **/
    @Schema(description = "")
    public Long getNonHeapUsed() {
        return nonHeapUsed;
    }

    public void setNonHeapUsed(Long nonHeapUsed) {
        this.nonHeapUsed = nonHeapUsed;
    }

    public TaskManagerMetricsInfo nonHeapCommitted(Long nonHeapCommitted) {
        this.nonHeapCommitted = nonHeapCommitted;
        return this;
    }

    /**
     * Get nonHeapCommitted
     * 
     * @return nonHeapCommitted
     **/
    @Schema(description = "")
    public Long getNonHeapCommitted() {
        return nonHeapCommitted;
    }

    public void setNonHeapCommitted(Long nonHeapCommitted) {
        this.nonHeapCommitted = nonHeapCommitted;
    }

    public TaskManagerMetricsInfo nonHeapMax(Long nonHeapMax) {
        this.nonHeapMax = nonHeapMax;
        return this;
    }

    /**
     * Get nonHeapMax
     * 
     * @return nonHeapMax
     **/
    @Schema(description = "")
    public Long getNonHeapMax() {
        return nonHeapMax;
    }

    public void setNonHeapMax(Long nonHeapMax) {
        this.nonHeapMax = nonHeapMax;
    }

    public TaskManagerMetricsInfo directCount(Long directCount) {
        this.directCount = directCount;
        return this;
    }

    /**
     * Get directCount
     * 
     * @return directCount
     **/
    @Schema(description = "")
    public Long getDirectCount() {
        return directCount;
    }

    public void setDirectCount(Long directCount) {
        this.directCount = directCount;
    }

    public TaskManagerMetricsInfo directUsed(Long directUsed) {
        this.directUsed = directUsed;
        return this;
    }

    /**
     * Get directUsed
     * 
     * @return directUsed
     **/
    @Schema(description = "")
    public Long getDirectUsed() {
        return directUsed;
    }

    public void setDirectUsed(Long directUsed) {
        this.directUsed = directUsed;
    }

    public TaskManagerMetricsInfo directMax(Long directMax) {
        this.directMax = directMax;
        return this;
    }

    /**
     * Get directMax
     * 
     * @return directMax
     **/
    @Schema(description = "")
    public Long getDirectMax() {
        return directMax;
    }

    public void setDirectMax(Long directMax) {
        this.directMax = directMax;
    }

    public TaskManagerMetricsInfo mappedCount(Long mappedCount) {
        this.mappedCount = mappedCount;
        return this;
    }

    /**
     * Get mappedCount
     * 
     * @return mappedCount
     **/
    @Schema(description = "")
    public Long getMappedCount() {
        return mappedCount;
    }

    public void setMappedCount(Long mappedCount) {
        this.mappedCount = mappedCount;
    }

    public TaskManagerMetricsInfo mappedUsed(Long mappedUsed) {
        this.mappedUsed = mappedUsed;
        return this;
    }

    /**
     * Get mappedUsed
     * 
     * @return mappedUsed
     **/
    @Schema(description = "")
    public Long getMappedUsed() {
        return mappedUsed;
    }

    public void setMappedUsed(Long mappedUsed) {
        this.mappedUsed = mappedUsed;
    }

    public TaskManagerMetricsInfo mappedMax(Long mappedMax) {
        this.mappedMax = mappedMax;
        return this;
    }

    /**
     * Get mappedMax
     * 
     * @return mappedMax
     **/
    @Schema(description = "")
    public Long getMappedMax() {
        return mappedMax;
    }

    public void setMappedMax(Long mappedMax) {
        this.mappedMax = mappedMax;
    }

    public TaskManagerMetricsInfo memorySegmentsAvailable(Long memorySegmentsAvailable) {
        this.memorySegmentsAvailable = memorySegmentsAvailable;
        return this;
    }

    /**
     * Get memorySegmentsAvailable
     * 
     * @return memorySegmentsAvailable
     **/
    @Schema(description = "")
    public Long getMemorySegmentsAvailable() {
        return memorySegmentsAvailable;
    }

    public void setMemorySegmentsAvailable(Long memorySegmentsAvailable) {
        this.memorySegmentsAvailable = memorySegmentsAvailable;
    }

    public TaskManagerMetricsInfo memorySegmentsTotal(Long memorySegmentsTotal) {
        this.memorySegmentsTotal = memorySegmentsTotal;
        return this;
    }

    /**
     * Get memorySegmentsTotal
     * 
     * @return memorySegmentsTotal
     **/
    @Schema(description = "")
    public Long getMemorySegmentsTotal() {
        return memorySegmentsTotal;
    }

    public void setMemorySegmentsTotal(Long memorySegmentsTotal) {
        this.memorySegmentsTotal = memorySegmentsTotal;
    }

    public TaskManagerMetricsInfo nettyShuffleMemorySegmentsAvailable(Long nettyShuffleMemorySegmentsAvailable) {
        this.nettyShuffleMemorySegmentsAvailable = nettyShuffleMemorySegmentsAvailable;
        return this;
    }

    /**
     * Get nettyShuffleMemorySegmentsAvailable
     * 
     * @return nettyShuffleMemorySegmentsAvailable
     **/
    @Schema(description = "")
    public Long getNettyShuffleMemorySegmentsAvailable() {
        return nettyShuffleMemorySegmentsAvailable;
    }

    public void setNettyShuffleMemorySegmentsAvailable(Long nettyShuffleMemorySegmentsAvailable) {
        this.nettyShuffleMemorySegmentsAvailable = nettyShuffleMemorySegmentsAvailable;
    }

    public TaskManagerMetricsInfo nettyShuffleMemorySegmentsUsed(Long nettyShuffleMemorySegmentsUsed) {
        this.nettyShuffleMemorySegmentsUsed = nettyShuffleMemorySegmentsUsed;
        return this;
    }

    /**
     * Get nettyShuffleMemorySegmentsUsed
     * 
     * @return nettyShuffleMemorySegmentsUsed
     **/
    @Schema(description = "")
    public Long getNettyShuffleMemorySegmentsUsed() {
        return nettyShuffleMemorySegmentsUsed;
    }

    public void setNettyShuffleMemorySegmentsUsed(Long nettyShuffleMemorySegmentsUsed) {
        this.nettyShuffleMemorySegmentsUsed = nettyShuffleMemorySegmentsUsed;
    }

    public TaskManagerMetricsInfo nettyShuffleMemorySegmentsTotal(Long nettyShuffleMemorySegmentsTotal) {
        this.nettyShuffleMemorySegmentsTotal = nettyShuffleMemorySegmentsTotal;
        return this;
    }

    /**
     * Get nettyShuffleMemorySegmentsTotal
     * 
     * @return nettyShuffleMemorySegmentsTotal
     **/
    @Schema(description = "")
    public Long getNettyShuffleMemorySegmentsTotal() {
        return nettyShuffleMemorySegmentsTotal;
    }

    public void setNettyShuffleMemorySegmentsTotal(Long nettyShuffleMemorySegmentsTotal) {
        this.nettyShuffleMemorySegmentsTotal = nettyShuffleMemorySegmentsTotal;
    }

    public TaskManagerMetricsInfo nettyShuffleMemoryAvailable(Long nettyShuffleMemoryAvailable) {
        this.nettyShuffleMemoryAvailable = nettyShuffleMemoryAvailable;
        return this;
    }

    /**
     * Get nettyShuffleMemoryAvailable
     * 
     * @return nettyShuffleMemoryAvailable
     **/
    @Schema(description = "")
    public Long getNettyShuffleMemoryAvailable() {
        return nettyShuffleMemoryAvailable;
    }

    public void setNettyShuffleMemoryAvailable(Long nettyShuffleMemoryAvailable) {
        this.nettyShuffleMemoryAvailable = nettyShuffleMemoryAvailable;
    }

    public TaskManagerMetricsInfo nettyShuffleMemoryUsed(Long nettyShuffleMemoryUsed) {
        this.nettyShuffleMemoryUsed = nettyShuffleMemoryUsed;
        return this;
    }

    /**
     * Get nettyShuffleMemoryUsed
     * 
     * @return nettyShuffleMemoryUsed
     **/
    @Schema(description = "")
    public Long getNettyShuffleMemoryUsed() {
        return nettyShuffleMemoryUsed;
    }

    public void setNettyShuffleMemoryUsed(Long nettyShuffleMemoryUsed) {
        this.nettyShuffleMemoryUsed = nettyShuffleMemoryUsed;
    }

    public TaskManagerMetricsInfo nettyShuffleMemoryTotal(Long nettyShuffleMemoryTotal) {
        this.nettyShuffleMemoryTotal = nettyShuffleMemoryTotal;
        return this;
    }

    /**
     * Get nettyShuffleMemoryTotal
     * 
     * @return nettyShuffleMemoryTotal
     **/
    @Schema(description = "")
    public Long getNettyShuffleMemoryTotal() {
        return nettyShuffleMemoryTotal;
    }

    public void setNettyShuffleMemoryTotal(Long nettyShuffleMemoryTotal) {
        this.nettyShuffleMemoryTotal = nettyShuffleMemoryTotal;
    }

    public TaskManagerMetricsInfo garbageCollectors(List<GarbageCollectorInfo> garbageCollectors) {
        this.garbageCollectors = garbageCollectors;
        return this;
    }

    public TaskManagerMetricsInfo addGarbageCollectorsItem(GarbageCollectorInfo garbageCollectorsItem) {
        if (this.garbageCollectors == null) {
            this.garbageCollectors = new ArrayList<GarbageCollectorInfo>();
        }
        this.garbageCollectors.add(garbageCollectorsItem);
        return this;
    }

    /**
     * Get garbageCollectors
     * 
     * @return garbageCollectors
     **/
    @Schema(description = "")
    public List<GarbageCollectorInfo> getGarbageCollectors() {
        return garbageCollectors;
    }

    public void setGarbageCollectors(List<GarbageCollectorInfo> garbageCollectors) {
        this.garbageCollectors = garbageCollectors;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaskManagerMetricsInfo taskManagerMetricsInfo = (TaskManagerMetricsInfo) o;
        return Objects.equals(this.heapUsed, taskManagerMetricsInfo.heapUsed)
                && Objects.equals(this.heapCommitted, taskManagerMetricsInfo.heapCommitted)
                && Objects.equals(this.heapMax, taskManagerMetricsInfo.heapMax)
                && Objects.equals(this.nonHeapUsed, taskManagerMetricsInfo.nonHeapUsed)
                && Objects.equals(this.nonHeapCommitted, taskManagerMetricsInfo.nonHeapCommitted)
                && Objects.equals(this.nonHeapMax, taskManagerMetricsInfo.nonHeapMax)
                && Objects.equals(this.directCount, taskManagerMetricsInfo.directCount)
                && Objects.equals(this.directUsed, taskManagerMetricsInfo.directUsed)
                && Objects.equals(this.directMax, taskManagerMetricsInfo.directMax)
                && Objects.equals(this.mappedCount, taskManagerMetricsInfo.mappedCount)
                && Objects.equals(this.mappedUsed, taskManagerMetricsInfo.mappedUsed)
                && Objects.equals(this.mappedMax, taskManagerMetricsInfo.mappedMax)
                && Objects.equals(this.memorySegmentsAvailable, taskManagerMetricsInfo.memorySegmentsAvailable)
                && Objects.equals(this.memorySegmentsTotal, taskManagerMetricsInfo.memorySegmentsTotal)
                && Objects.equals(this.nettyShuffleMemorySegmentsAvailable,
                        taskManagerMetricsInfo.nettyShuffleMemorySegmentsAvailable)
                && Objects.equals(this.nettyShuffleMemorySegmentsUsed, taskManagerMetricsInfo.nettyShuffleMemorySegmentsUsed)
                && Objects.equals(this.nettyShuffleMemorySegmentsTotal, taskManagerMetricsInfo.nettyShuffleMemorySegmentsTotal)
                && Objects.equals(this.nettyShuffleMemoryAvailable, taskManagerMetricsInfo.nettyShuffleMemoryAvailable)
                && Objects.equals(this.nettyShuffleMemoryUsed, taskManagerMetricsInfo.nettyShuffleMemoryUsed)
                && Objects.equals(this.nettyShuffleMemoryTotal, taskManagerMetricsInfo.nettyShuffleMemoryTotal)
                && Objects.equals(this.garbageCollectors, taskManagerMetricsInfo.garbageCollectors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(heapUsed, heapCommitted, heapMax, nonHeapUsed, nonHeapCommitted, nonHeapMax, directCount, directUsed,
                directMax, mappedCount, mappedUsed, mappedMax, memorySegmentsAvailable, memorySegmentsTotal,
                nettyShuffleMemorySegmentsAvailable, nettyShuffleMemorySegmentsUsed, nettyShuffleMemorySegmentsTotal,
                nettyShuffleMemoryAvailable, nettyShuffleMemoryUsed, nettyShuffleMemoryTotal, garbageCollectors);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TaskManagerMetricsInfo {\n");

        sb.append("    heapUsed: ").append(toIndentedString(heapUsed)).append("\n");
        sb.append("    heapCommitted: ").append(toIndentedString(heapCommitted)).append("\n");
        sb.append("    heapMax: ").append(toIndentedString(heapMax)).append("\n");
        sb.append("    nonHeapUsed: ").append(toIndentedString(nonHeapUsed)).append("\n");
        sb.append("    nonHeapCommitted: ").append(toIndentedString(nonHeapCommitted)).append("\n");
        sb.append("    nonHeapMax: ").append(toIndentedString(nonHeapMax)).append("\n");
        sb.append("    directCount: ").append(toIndentedString(directCount)).append("\n");
        sb.append("    directUsed: ").append(toIndentedString(directUsed)).append("\n");
        sb.append("    directMax: ").append(toIndentedString(directMax)).append("\n");
        sb.append("    mappedCount: ").append(toIndentedString(mappedCount)).append("\n");
        sb.append("    mappedUsed: ").append(toIndentedString(mappedUsed)).append("\n");
        sb.append("    mappedMax: ").append(toIndentedString(mappedMax)).append("\n");
        sb.append("    memorySegmentsAvailable: ").append(toIndentedString(memorySegmentsAvailable)).append("\n");
        sb.append("    memorySegmentsTotal: ").append(toIndentedString(memorySegmentsTotal)).append("\n");
        sb.append("    nettyShuffleMemorySegmentsAvailable: ")
                .append(toIndentedString(nettyShuffleMemorySegmentsAvailable))
                .append("\n");
        sb.append("    nettyShuffleMemorySegmentsUsed: ").append(toIndentedString(nettyShuffleMemorySegmentsUsed)).append("\n");
        sb.append("    nettyShuffleMemorySegmentsTotal: ").append(toIndentedString(nettyShuffleMemorySegmentsTotal)).append("\n");
        sb.append("    nettyShuffleMemoryAvailable: ").append(toIndentedString(nettyShuffleMemoryAvailable)).append("\n");
        sb.append("    nettyShuffleMemoryUsed: ").append(toIndentedString(nettyShuffleMemoryUsed)).append("\n");
        sb.append("    nettyShuffleMemoryTotal: ").append(toIndentedString(nettyShuffleMemoryTotal)).append("\n");
        sb.append("    garbageCollectors: ").append(toIndentedString(garbageCollectors)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}