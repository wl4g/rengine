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

import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * RestoredCheckpointStatistics
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
        date = "2023-04-02T23:14:46.617621+08:00[Asia/Shanghai]")
public class RestoredCheckpointStatistics {
    @SerializedName("id")
    private Long id = null;

    @SerializedName("restore_timestamp")
    private Long restoreTimestamp = null;

    @SerializedName("is_savepoint")
    private Boolean isSavepoint = null;

    @SerializedName("external_path")
    private String externalPath = null;

    public RestoredCheckpointStatistics id(Long id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     * 
     * @return id
     **/
    @Schema(description = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RestoredCheckpointStatistics restoreTimestamp(Long restoreTimestamp) {
        this.restoreTimestamp = restoreTimestamp;
        return this;
    }

    /**
     * Get restoreTimestamp
     * 
     * @return restoreTimestamp
     **/
    @Schema(description = "")
    public Long getRestoreTimestamp() {
        return restoreTimestamp;
    }

    public void setRestoreTimestamp(Long restoreTimestamp) {
        this.restoreTimestamp = restoreTimestamp;
    }

    public RestoredCheckpointStatistics isSavepoint(Boolean isSavepoint) {
        this.isSavepoint = isSavepoint;
        return this;
    }

    /**
     * Get isSavepoint
     * 
     * @return isSavepoint
     **/
    @Schema(description = "")
    public Boolean isIsSavepoint() {
        return isSavepoint;
    }

    public void setIsSavepoint(Boolean isSavepoint) {
        this.isSavepoint = isSavepoint;
    }

    public RestoredCheckpointStatistics externalPath(String externalPath) {
        this.externalPath = externalPath;
        return this;
    }

    /**
     * Get externalPath
     * 
     * @return externalPath
     **/
    @Schema(description = "")
    public String getExternalPath() {
        return externalPath;
    }

    public void setExternalPath(String externalPath) {
        this.externalPath = externalPath;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RestoredCheckpointStatistics restoredCheckpointStatistics = (RestoredCheckpointStatistics) o;
        return Objects.equals(this.id, restoredCheckpointStatistics.id)
                && Objects.equals(this.restoreTimestamp, restoredCheckpointStatistics.restoreTimestamp)
                && Objects.equals(this.isSavepoint, restoredCheckpointStatistics.isSavepoint)
                && Objects.equals(this.externalPath, restoredCheckpointStatistics.externalPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, restoreTimestamp, isSavepoint, externalPath);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RestoredCheckpointStatistics {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    restoreTimestamp: ").append(toIndentedString(restoreTimestamp)).append("\n");
        sb.append("    isSavepoint: ").append(toIndentedString(isSavepoint)).append("\n");
        sb.append("    externalPath: ").append(toIndentedString(externalPath)).append("\n");
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
