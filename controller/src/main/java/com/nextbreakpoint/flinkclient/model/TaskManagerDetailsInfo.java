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

package com.nextbreakpoint.flinkclient.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * TaskManagerDetailsInfo
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
        date = "2023-04-02T23:14:46.617621+08:00[Asia/Shanghai]")
public class TaskManagerDetailsInfo {
    @SerializedName("id")
    private String id = null;

    @SerializedName("path")
    private String path = null;

    @SerializedName("dataPort")
    private Integer dataPort = null;

    @SerializedName("jmxPort")
    private Integer jmxPort = null;

    @SerializedName("timeSinceLastHeartbeat")
    private Long timeSinceLastHeartbeat = null;

    @SerializedName("slotsNumber")
    private Integer slotsNumber = null;

    @SerializedName("freeSlots")
    private Integer freeSlots = null;

    @SerializedName("totalResource")
    private ResourceProfileInfo totalResource = null;

    @SerializedName("freeResource")
    private ResourceProfileInfo freeResource = null;

    @SerializedName("hardware")
    private HardwareDescription hardware = null;

    @SerializedName("memoryConfiguration")
    private TaskExecutorMemoryConfiguration memoryConfiguration = null;

    @SerializedName("allocatedSlots")
    private List<SlotInfo> allocatedSlots = null;

    @SerializedName("metrics")
    private TaskManagerMetricsInfo metrics = null;

    public TaskManagerDetailsInfo id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     * 
     * @return id
     **/
    @Schema(description = "")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TaskManagerDetailsInfo path(String path) {
        this.path = path;
        return this;
    }

    /**
     * Get path
     * 
     * @return path
     **/
    @Schema(description = "")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public TaskManagerDetailsInfo dataPort(Integer dataPort) {
        this.dataPort = dataPort;
        return this;
    }

    /**
     * Get dataPort
     * 
     * @return dataPort
     **/
    @Schema(description = "")
    public Integer getDataPort() {
        return dataPort;
    }

    public void setDataPort(Integer dataPort) {
        this.dataPort = dataPort;
    }

    public TaskManagerDetailsInfo jmxPort(Integer jmxPort) {
        this.jmxPort = jmxPort;
        return this;
    }

    /**
     * Get jmxPort
     * 
     * @return jmxPort
     **/
    @Schema(description = "")
    public Integer getJmxPort() {
        return jmxPort;
    }

    public void setJmxPort(Integer jmxPort) {
        this.jmxPort = jmxPort;
    }

    public TaskManagerDetailsInfo timeSinceLastHeartbeat(Long timeSinceLastHeartbeat) {
        this.timeSinceLastHeartbeat = timeSinceLastHeartbeat;
        return this;
    }

    /**
     * Get timeSinceLastHeartbeat
     * 
     * @return timeSinceLastHeartbeat
     **/
    @Schema(description = "")
    public Long getTimeSinceLastHeartbeat() {
        return timeSinceLastHeartbeat;
    }

    public void setTimeSinceLastHeartbeat(Long timeSinceLastHeartbeat) {
        this.timeSinceLastHeartbeat = timeSinceLastHeartbeat;
    }

    public TaskManagerDetailsInfo slotsNumber(Integer slotsNumber) {
        this.slotsNumber = slotsNumber;
        return this;
    }

    /**
     * Get slotsNumber
     * 
     * @return slotsNumber
     **/
    @Schema(description = "")
    public Integer getSlotsNumber() {
        return slotsNumber;
    }

    public void setSlotsNumber(Integer slotsNumber) {
        this.slotsNumber = slotsNumber;
    }

    public TaskManagerDetailsInfo freeSlots(Integer freeSlots) {
        this.freeSlots = freeSlots;
        return this;
    }

    /**
     * Get freeSlots
     * 
     * @return freeSlots
     **/
    @Schema(description = "")
    public Integer getFreeSlots() {
        return freeSlots;
    }

    public void setFreeSlots(Integer freeSlots) {
        this.freeSlots = freeSlots;
    }

    public TaskManagerDetailsInfo totalResource(ResourceProfileInfo totalResource) {
        this.totalResource = totalResource;
        return this;
    }

    /**
     * Get totalResource
     * 
     * @return totalResource
     **/
    @Schema(description = "")
    public ResourceProfileInfo getTotalResource() {
        return totalResource;
    }

    public void setTotalResource(ResourceProfileInfo totalResource) {
        this.totalResource = totalResource;
    }

    public TaskManagerDetailsInfo freeResource(ResourceProfileInfo freeResource) {
        this.freeResource = freeResource;
        return this;
    }

    /**
     * Get freeResource
     * 
     * @return freeResource
     **/
    @Schema(description = "")
    public ResourceProfileInfo getFreeResource() {
        return freeResource;
    }

    public void setFreeResource(ResourceProfileInfo freeResource) {
        this.freeResource = freeResource;
    }

    public TaskManagerDetailsInfo hardware(HardwareDescription hardware) {
        this.hardware = hardware;
        return this;
    }

    /**
     * Get hardware
     * 
     * @return hardware
     **/
    @Schema(description = "")
    public HardwareDescription getHardware() {
        return hardware;
    }

    public void setHardware(HardwareDescription hardware) {
        this.hardware = hardware;
    }

    public TaskManagerDetailsInfo memoryConfiguration(TaskExecutorMemoryConfiguration memoryConfiguration) {
        this.memoryConfiguration = memoryConfiguration;
        return this;
    }

    /**
     * Get memoryConfiguration
     * 
     * @return memoryConfiguration
     **/
    @Schema(description = "")
    public TaskExecutorMemoryConfiguration getMemoryConfiguration() {
        return memoryConfiguration;
    }

    public void setMemoryConfiguration(TaskExecutorMemoryConfiguration memoryConfiguration) {
        this.memoryConfiguration = memoryConfiguration;
    }

    public TaskManagerDetailsInfo allocatedSlots(List<SlotInfo> allocatedSlots) {
        this.allocatedSlots = allocatedSlots;
        return this;
    }

    public TaskManagerDetailsInfo addAllocatedSlotsItem(SlotInfo allocatedSlotsItem) {
        if (this.allocatedSlots == null) {
            this.allocatedSlots = new ArrayList<SlotInfo>();
        }
        this.allocatedSlots.add(allocatedSlotsItem);
        return this;
    }

    /**
     * Get allocatedSlots
     * 
     * @return allocatedSlots
     **/
    @Schema(description = "")
    public List<SlotInfo> getAllocatedSlots() {
        return allocatedSlots;
    }

    public void setAllocatedSlots(List<SlotInfo> allocatedSlots) {
        this.allocatedSlots = allocatedSlots;
    }

    public TaskManagerDetailsInfo metrics(TaskManagerMetricsInfo metrics) {
        this.metrics = metrics;
        return this;
    }

    /**
     * Get metrics
     * 
     * @return metrics
     **/
    @Schema(description = "")
    public TaskManagerMetricsInfo getMetrics() {
        return metrics;
    }

    public void setMetrics(TaskManagerMetricsInfo metrics) {
        this.metrics = metrics;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaskManagerDetailsInfo taskManagerDetailsInfo = (TaskManagerDetailsInfo) o;
        return Objects.equals(this.id, taskManagerDetailsInfo.id) && Objects.equals(this.path, taskManagerDetailsInfo.path)
                && Objects.equals(this.dataPort, taskManagerDetailsInfo.dataPort)
                && Objects.equals(this.jmxPort, taskManagerDetailsInfo.jmxPort)
                && Objects.equals(this.timeSinceLastHeartbeat, taskManagerDetailsInfo.timeSinceLastHeartbeat)
                && Objects.equals(this.slotsNumber, taskManagerDetailsInfo.slotsNumber)
                && Objects.equals(this.freeSlots, taskManagerDetailsInfo.freeSlots)
                && Objects.equals(this.totalResource, taskManagerDetailsInfo.totalResource)
                && Objects.equals(this.freeResource, taskManagerDetailsInfo.freeResource)
                && Objects.equals(this.hardware, taskManagerDetailsInfo.hardware)
                && Objects.equals(this.memoryConfiguration, taskManagerDetailsInfo.memoryConfiguration)
                && Objects.equals(this.allocatedSlots, taskManagerDetailsInfo.allocatedSlots)
                && Objects.equals(this.metrics, taskManagerDetailsInfo.metrics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, path, dataPort, jmxPort, timeSinceLastHeartbeat, slotsNumber, freeSlots, totalResource,
                freeResource, hardware, memoryConfiguration, allocatedSlots, metrics);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TaskManagerDetailsInfo {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    path: ").append(toIndentedString(path)).append("\n");
        sb.append("    dataPort: ").append(toIndentedString(dataPort)).append("\n");
        sb.append("    jmxPort: ").append(toIndentedString(jmxPort)).append("\n");
        sb.append("    timeSinceLastHeartbeat: ").append(toIndentedString(timeSinceLastHeartbeat)).append("\n");
        sb.append("    slotsNumber: ").append(toIndentedString(slotsNumber)).append("\n");
        sb.append("    freeSlots: ").append(toIndentedString(freeSlots)).append("\n");
        sb.append("    totalResource: ").append(toIndentedString(totalResource)).append("\n");
        sb.append("    freeResource: ").append(toIndentedString(freeResource)).append("\n");
        sb.append("    hardware: ").append(toIndentedString(hardware)).append("\n");
        sb.append("    memoryConfiguration: ").append(toIndentedString(memoryConfiguration)).append("\n");
        sb.append("    allocatedSlots: ").append(toIndentedString(allocatedSlots)).append("\n");
        sb.append("    metrics: ").append(toIndentedString(metrics)).append("\n");
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
