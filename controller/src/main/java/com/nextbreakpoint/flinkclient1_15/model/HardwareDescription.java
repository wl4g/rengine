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
 * HardwareDescription
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
        date = "2023-04-02T23:14:46.617621+08:00[Asia/Shanghai]")
public class HardwareDescription {
    @SerializedName("cpuCores")
    private Integer cpuCores = null;

    @SerializedName("physicalMemory")
    private Long physicalMemory = null;

    @SerializedName("freeMemory")
    private Long freeMemory = null;

    @SerializedName("managedMemory")
    private Long managedMemory = null;

    public HardwareDescription cpuCores(Integer cpuCores) {
        this.cpuCores = cpuCores;
        return this;
    }

    /**
     * Get cpuCores
     * 
     * @return cpuCores
     **/
    @Schema(description = "")
    public Integer getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(Integer cpuCores) {
        this.cpuCores = cpuCores;
    }

    public HardwareDescription physicalMemory(Long physicalMemory) {
        this.physicalMemory = physicalMemory;
        return this;
    }

    /**
     * Get physicalMemory
     * 
     * @return physicalMemory
     **/
    @Schema(description = "")
    public Long getPhysicalMemory() {
        return physicalMemory;
    }

    public void setPhysicalMemory(Long physicalMemory) {
        this.physicalMemory = physicalMemory;
    }

    public HardwareDescription freeMemory(Long freeMemory) {
        this.freeMemory = freeMemory;
        return this;
    }

    /**
     * Get freeMemory
     * 
     * @return freeMemory
     **/
    @Schema(description = "")
    public Long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(Long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public HardwareDescription managedMemory(Long managedMemory) {
        this.managedMemory = managedMemory;
        return this;
    }

    /**
     * Get managedMemory
     * 
     * @return managedMemory
     **/
    @Schema(description = "")
    public Long getManagedMemory() {
        return managedMemory;
    }

    public void setManagedMemory(Long managedMemory) {
        this.managedMemory = managedMemory;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HardwareDescription hardwareDescription = (HardwareDescription) o;
        return Objects.equals(this.cpuCores, hardwareDescription.cpuCores)
                && Objects.equals(this.physicalMemory, hardwareDescription.physicalMemory)
                && Objects.equals(this.freeMemory, hardwareDescription.freeMemory)
                && Objects.equals(this.managedMemory, hardwareDescription.managedMemory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpuCores, physicalMemory, freeMemory, managedMemory);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class HardwareDescription {\n");

        sb.append("    cpuCores: ").append(toIndentedString(cpuCores)).append("\n");
        sb.append("    physicalMemory: ").append(toIndentedString(physicalMemory)).append("\n");
        sb.append("    freeMemory: ").append(toIndentedString(freeMemory)).append("\n");
        sb.append("    managedMemory: ").append(toIndentedString(managedMemory)).append("\n");
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