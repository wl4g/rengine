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
 * SavepointTriggerRequestBody
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
        date = "2023-04-02T23:14:46.617621+08:00[Asia/Shanghai]")
public class SavepointTriggerRequestBody {
    @SerializedName("target-directory")
    private String targetDirectory = null;

    @SerializedName("cancel-job")
    private Boolean cancelJob = null;

    @SerializedName("triggerId")
    private String triggerId = null;

    public SavepointTriggerRequestBody targetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
        return this;
    }

    /**
     * Get targetDirectory
     * 
     * @return targetDirectory
     **/
    @Schema(description = "")
    public String getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public SavepointTriggerRequestBody cancelJob(Boolean cancelJob) {
        this.cancelJob = cancelJob;
        return this;
    }

    /**
     * Get cancelJob
     * 
     * @return cancelJob
     **/
    @Schema(description = "")
    public Boolean isCancelJob() {
        return cancelJob;
    }

    public void setCancelJob(Boolean cancelJob) {
        this.cancelJob = cancelJob;
    }

    public SavepointTriggerRequestBody triggerId(String triggerId) {
        this.triggerId = triggerId;
        return this;
    }

    /**
     * Get triggerId
     * 
     * @return triggerId
     **/
    @Schema(description = "")
    public String getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(String triggerId) {
        this.triggerId = triggerId;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SavepointTriggerRequestBody savepointTriggerRequestBody = (SavepointTriggerRequestBody) o;
        return Objects.equals(this.targetDirectory, savepointTriggerRequestBody.targetDirectory)
                && Objects.equals(this.cancelJob, savepointTriggerRequestBody.cancelJob)
                && Objects.equals(this.triggerId, savepointTriggerRequestBody.triggerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetDirectory, cancelJob, triggerId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SavepointTriggerRequestBody {\n");

        sb.append("    targetDirectory: ").append(toIndentedString(targetDirectory)).append("\n");
        sb.append("    cancelJob: ").append(toIndentedString(cancelJob)).append("\n");
        sb.append("    triggerId: ").append(toIndentedString(triggerId)).append("\n");
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
