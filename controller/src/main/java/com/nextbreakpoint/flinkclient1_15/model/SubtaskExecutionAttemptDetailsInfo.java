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

import java.io.IOException;
import java.util.Objects;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * SubtaskExecutionAttemptDetailsInfo
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
        date = "2023-04-02T23:14:46.617621+08:00[Asia/Shanghai]")
public class SubtaskExecutionAttemptDetailsInfo {
    @SerializedName("subtask")
    private Integer subtask = null;

    /**
     * Gets or Sets status
     */
    @JsonAdapter(StatusEnum.Adapter.class)
    public enum StatusEnum {
        CREATED("CREATED"), SCHEDULED("SCHEDULED"), DEPLOYING("DEPLOYING"), RUNNING("RUNNING"), FINISHED("FINISHED"), CANCELING(
                "CANCELING"), CANCELED("CANCELED"), FAILED("FAILED"), RECONCILING("RECONCILING"), INITIALIZING("INITIALIZING");

        private String value;

        StatusEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static StatusEnum fromValue(String text) {
            for (StatusEnum b : StatusEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public static class Adapter extends TypeAdapter<StatusEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final StatusEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public StatusEnum read(final JsonReader jsonReader) throws IOException {
                Object value = jsonReader.nextString();
                return StatusEnum.fromValue(String.valueOf(value));
            }
        }
    }

    @SerializedName("status")
    private StatusEnum status = null;

    @SerializedName("attempt")
    private Integer attempt = null;

    @SerializedName("host")
    private String host = null;

    @SerializedName("start-time")
    private Long startTime = null;

    @SerializedName("end-time")
    private Long endTime = null;

    @SerializedName("duration")
    private Long duration = null;

    @SerializedName("metrics")
    private IOMetricsInfo metrics = null;

    @SerializedName("taskmanager-id")
    private String taskmanagerId = null;

    public SubtaskExecutionAttemptDetailsInfo subtask(Integer subtask) {
        this.subtask = subtask;
        return this;
    }

    /**
     * Get subtask
     * 
     * @return subtask
     **/
    @Schema(description = "")
    public Integer getSubtask() {
        return subtask;
    }

    public void setSubtask(Integer subtask) {
        this.subtask = subtask;
    }

    public SubtaskExecutionAttemptDetailsInfo status(StatusEnum status) {
        this.status = status;
        return this;
    }

    /**
     * Get status
     * 
     * @return status
     **/
    @Schema(description = "")
    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public SubtaskExecutionAttemptDetailsInfo attempt(Integer attempt) {
        this.attempt = attempt;
        return this;
    }

    /**
     * Get attempt
     * 
     * @return attempt
     **/
    @Schema(description = "")
    public Integer getAttempt() {
        return attempt;
    }

    public void setAttempt(Integer attempt) {
        this.attempt = attempt;
    }

    public SubtaskExecutionAttemptDetailsInfo host(String host) {
        this.host = host;
        return this;
    }

    /**
     * Get host
     * 
     * @return host
     **/
    @Schema(description = "")
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public SubtaskExecutionAttemptDetailsInfo startTime(Long startTime) {
        this.startTime = startTime;
        return this;
    }

    /**
     * Get startTime
     * 
     * @return startTime
     **/
    @Schema(description = "")
    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public SubtaskExecutionAttemptDetailsInfo endTime(Long endTime) {
        this.endTime = endTime;
        return this;
    }

    /**
     * Get endTime
     * 
     * @return endTime
     **/
    @Schema(description = "")
    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public SubtaskExecutionAttemptDetailsInfo duration(Long duration) {
        this.duration = duration;
        return this;
    }

    /**
     * Get duration
     * 
     * @return duration
     **/
    @Schema(description = "")
    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public SubtaskExecutionAttemptDetailsInfo metrics(IOMetricsInfo metrics) {
        this.metrics = metrics;
        return this;
    }

    /**
     * Get metrics
     * 
     * @return metrics
     **/
    @Schema(description = "")
    public IOMetricsInfo getMetrics() {
        return metrics;
    }

    public void setMetrics(IOMetricsInfo metrics) {
        this.metrics = metrics;
    }

    public SubtaskExecutionAttemptDetailsInfo taskmanagerId(String taskmanagerId) {
        this.taskmanagerId = taskmanagerId;
        return this;
    }

    /**
     * Get taskmanagerId
     * 
     * @return taskmanagerId
     **/
    @Schema(description = "")
    public String getTaskmanagerId() {
        return taskmanagerId;
    }

    public void setTaskmanagerId(String taskmanagerId) {
        this.taskmanagerId = taskmanagerId;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubtaskExecutionAttemptDetailsInfo subtaskExecutionAttemptDetailsInfo = (SubtaskExecutionAttemptDetailsInfo) o;
        return Objects.equals(this.subtask, subtaskExecutionAttemptDetailsInfo.subtask)
                && Objects.equals(this.status, subtaskExecutionAttemptDetailsInfo.status)
                && Objects.equals(this.attempt, subtaskExecutionAttemptDetailsInfo.attempt)
                && Objects.equals(this.host, subtaskExecutionAttemptDetailsInfo.host)
                && Objects.equals(this.startTime, subtaskExecutionAttemptDetailsInfo.startTime)
                && Objects.equals(this.endTime, subtaskExecutionAttemptDetailsInfo.endTime)
                && Objects.equals(this.duration, subtaskExecutionAttemptDetailsInfo.duration)
                && Objects.equals(this.metrics, subtaskExecutionAttemptDetailsInfo.metrics)
                && Objects.equals(this.taskmanagerId, subtaskExecutionAttemptDetailsInfo.taskmanagerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subtask, status, attempt, host, startTime, endTime, duration, metrics, taskmanagerId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SubtaskExecutionAttemptDetailsInfo {\n");

        sb.append("    subtask: ").append(toIndentedString(subtask)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        sb.append("    attempt: ").append(toIndentedString(attempt)).append("\n");
        sb.append("    host: ").append(toIndentedString(host)).append("\n");
        sb.append("    startTime: ").append(toIndentedString(startTime)).append("\n");
        sb.append("    endTime: ").append(toIndentedString(endTime)).append("\n");
        sb.append("    duration: ").append(toIndentedString(duration)).append("\n");
        sb.append("    metrics: ").append(toIndentedString(metrics)).append("\n");
        sb.append("    taskmanagerId: ").append(toIndentedString(taskmanagerId)).append("\n");
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
