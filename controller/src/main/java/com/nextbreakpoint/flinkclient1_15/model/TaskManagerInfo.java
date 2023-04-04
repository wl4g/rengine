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

import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * TaskManagerInfo
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
        date = "2023-04-02T23:14:46.617621+08:00[Asia/Shanghai]")
public class TaskManagerInfo {
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

    public TaskManagerInfo id(String id) {
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

    public TaskManagerInfo path(String path) {
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

    public TaskManagerInfo dataPort(Integer dataPort) {
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

    public TaskManagerInfo jmxPort(Integer jmxPort) {
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

    public TaskManagerInfo timeSinceLastHeartbeat(Long timeSinceLastHeartbeat) {
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

    public TaskManagerInfo slotsNumber(Integer slotsNumber) {
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

    public TaskManagerInfo freeSlots(Integer freeSlots) {
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

    public TaskManagerInfo totalResource(ResourceProfileInfo totalResource) {
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

    public TaskManagerInfo freeResource(ResourceProfileInfo freeResource) {
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

    public TaskManagerInfo hardware(HardwareDescription hardware) {
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

    public TaskManagerInfo memoryConfiguration(TaskExecutorMemoryConfiguration memoryConfiguration) {
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

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaskManagerInfo taskManagerInfo = (TaskManagerInfo) o;
        return Objects.equals(this.id, taskManagerInfo.id) && Objects.equals(this.path, taskManagerInfo.path)
                && Objects.equals(this.dataPort, taskManagerInfo.dataPort)
                && Objects.equals(this.jmxPort, taskManagerInfo.jmxPort)
                && Objects.equals(this.timeSinceLastHeartbeat, taskManagerInfo.timeSinceLastHeartbeat)
                && Objects.equals(this.slotsNumber, taskManagerInfo.slotsNumber)
                && Objects.equals(this.freeSlots, taskManagerInfo.freeSlots)
                && Objects.equals(this.totalResource, taskManagerInfo.totalResource)
                && Objects.equals(this.freeResource, taskManagerInfo.freeResource)
                && Objects.equals(this.hardware, taskManagerInfo.hardware)
                && Objects.equals(this.memoryConfiguration, taskManagerInfo.memoryConfiguration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, path, dataPort, jmxPort, timeSinceLastHeartbeat, slotsNumber, freeSlots, totalResource,
                freeResource, hardware, memoryConfiguration);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TaskManagerInfo {\n");

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