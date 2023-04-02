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
 * SubtaskExecutionAttemptAccumulatorsInfo
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
        date = "2023-04-02T23:14:46.617621+08:00[Asia/Shanghai]")
public class SubtaskExecutionAttemptAccumulatorsInfo {
    @SerializedName("subtask")
    private Integer subtask = null;

    @SerializedName("attempt")
    private Integer attempt = null;

    @SerializedName("id")
    private String id = null;

    @SerializedName("user-accumulators")
    private List<UserAccumulator> userAccumulators = null;

    public SubtaskExecutionAttemptAccumulatorsInfo subtask(Integer subtask) {
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

    public SubtaskExecutionAttemptAccumulatorsInfo attempt(Integer attempt) {
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

    public SubtaskExecutionAttemptAccumulatorsInfo id(String id) {
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

    public SubtaskExecutionAttemptAccumulatorsInfo userAccumulators(List<UserAccumulator> userAccumulators) {
        this.userAccumulators = userAccumulators;
        return this;
    }

    public SubtaskExecutionAttemptAccumulatorsInfo addUserAccumulatorsItem(UserAccumulator userAccumulatorsItem) {
        if (this.userAccumulators == null) {
            this.userAccumulators = new ArrayList<UserAccumulator>();
        }
        this.userAccumulators.add(userAccumulatorsItem);
        return this;
    }

    /**
     * Get userAccumulators
     * 
     * @return userAccumulators
     **/
    @Schema(description = "")
    public List<UserAccumulator> getUserAccumulators() {
        return userAccumulators;
    }

    public void setUserAccumulators(List<UserAccumulator> userAccumulators) {
        this.userAccumulators = userAccumulators;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubtaskExecutionAttemptAccumulatorsInfo subtaskExecutionAttemptAccumulatorsInfo = (SubtaskExecutionAttemptAccumulatorsInfo) o;
        return Objects.equals(this.subtask, subtaskExecutionAttemptAccumulatorsInfo.subtask)
                && Objects.equals(this.attempt, subtaskExecutionAttemptAccumulatorsInfo.attempt)
                && Objects.equals(this.id, subtaskExecutionAttemptAccumulatorsInfo.id)
                && Objects.equals(this.userAccumulators, subtaskExecutionAttemptAccumulatorsInfo.userAccumulators);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subtask, attempt, id, userAccumulators);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SubtaskExecutionAttemptAccumulatorsInfo {\n");

        sb.append("    subtask: ").append(toIndentedString(subtask)).append("\n");
        sb.append("    attempt: ").append(toIndentedString(attempt)).append("\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    userAccumulators: ").append(toIndentedString(userAccumulators)).append("\n");
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
