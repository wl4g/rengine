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
 * ClusterOverviewWithVersion
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
        date = "2023-04-02T23:14:46.617621+08:00[Asia/Shanghai]")
public class ClusterOverviewWithVersion {
    @SerializedName("taskmanagers")
    private Integer taskmanagers = null;

    @SerializedName("slots-total")
    private Integer slotsTotal = null;

    @SerializedName("slots-available")
    private Integer slotsAvailable = null;

    @SerializedName("jobs-running")
    private Integer jobsRunning = null;

    @SerializedName("jobs-finished")
    private Integer jobsFinished = null;

    @SerializedName("jobs-cancelled")
    private Integer jobsCancelled = null;

    @SerializedName("jobs-failed")
    private Integer jobsFailed = null;

    @SerializedName("flink-version")
    private String flinkVersion = null;

    @SerializedName("flink-commit")
    private String flinkCommit = null;

    public ClusterOverviewWithVersion taskmanagers(Integer taskmanagers) {
        this.taskmanagers = taskmanagers;
        return this;
    }

    /**
     * Get taskmanagers
     * 
     * @return taskmanagers
     **/
    @Schema(description = "")
    public Integer getTaskmanagers() {
        return taskmanagers;
    }

    public void setTaskmanagers(Integer taskmanagers) {
        this.taskmanagers = taskmanagers;
    }

    public ClusterOverviewWithVersion slotsTotal(Integer slotsTotal) {
        this.slotsTotal = slotsTotal;
        return this;
    }

    /**
     * Get slotsTotal
     * 
     * @return slotsTotal
     **/
    @Schema(description = "")
    public Integer getSlotsTotal() {
        return slotsTotal;
    }

    public void setSlotsTotal(Integer slotsTotal) {
        this.slotsTotal = slotsTotal;
    }

    public ClusterOverviewWithVersion slotsAvailable(Integer slotsAvailable) {
        this.slotsAvailable = slotsAvailable;
        return this;
    }

    /**
     * Get slotsAvailable
     * 
     * @return slotsAvailable
     **/
    @Schema(description = "")
    public Integer getSlotsAvailable() {
        return slotsAvailable;
    }

    public void setSlotsAvailable(Integer slotsAvailable) {
        this.slotsAvailable = slotsAvailable;
    }

    public ClusterOverviewWithVersion jobsRunning(Integer jobsRunning) {
        this.jobsRunning = jobsRunning;
        return this;
    }

    /**
     * Get jobsRunning
     * 
     * @return jobsRunning
     **/
    @Schema(description = "")
    public Integer getJobsRunning() {
        return jobsRunning;
    }

    public void setJobsRunning(Integer jobsRunning) {
        this.jobsRunning = jobsRunning;
    }

    public ClusterOverviewWithVersion jobsFinished(Integer jobsFinished) {
        this.jobsFinished = jobsFinished;
        return this;
    }

    /**
     * Get jobsFinished
     * 
     * @return jobsFinished
     **/
    @Schema(description = "")
    public Integer getJobsFinished() {
        return jobsFinished;
    }

    public void setJobsFinished(Integer jobsFinished) {
        this.jobsFinished = jobsFinished;
    }

    public ClusterOverviewWithVersion jobsCancelled(Integer jobsCancelled) {
        this.jobsCancelled = jobsCancelled;
        return this;
    }

    /**
     * Get jobsCancelled
     * 
     * @return jobsCancelled
     **/
    @Schema(description = "")
    public Integer getJobsCancelled() {
        return jobsCancelled;
    }

    public void setJobsCancelled(Integer jobsCancelled) {
        this.jobsCancelled = jobsCancelled;
    }

    public ClusterOverviewWithVersion jobsFailed(Integer jobsFailed) {
        this.jobsFailed = jobsFailed;
        return this;
    }

    /**
     * Get jobsFailed
     * 
     * @return jobsFailed
     **/
    @Schema(description = "")
    public Integer getJobsFailed() {
        return jobsFailed;
    }

    public void setJobsFailed(Integer jobsFailed) {
        this.jobsFailed = jobsFailed;
    }

    public ClusterOverviewWithVersion flinkVersion(String flinkVersion) {
        this.flinkVersion = flinkVersion;
        return this;
    }

    /**
     * Get flinkVersion
     * 
     * @return flinkVersion
     **/
    @Schema(description = "")
    public String getFlinkVersion() {
        return flinkVersion;
    }

    public void setFlinkVersion(String flinkVersion) {
        this.flinkVersion = flinkVersion;
    }

    public ClusterOverviewWithVersion flinkCommit(String flinkCommit) {
        this.flinkCommit = flinkCommit;
        return this;
    }

    /**
     * Get flinkCommit
     * 
     * @return flinkCommit
     **/
    @Schema(description = "")
    public String getFlinkCommit() {
        return flinkCommit;
    }

    public void setFlinkCommit(String flinkCommit) {
        this.flinkCommit = flinkCommit;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClusterOverviewWithVersion clusterOverviewWithVersion = (ClusterOverviewWithVersion) o;
        return Objects.equals(this.taskmanagers, clusterOverviewWithVersion.taskmanagers)
                && Objects.equals(this.slotsTotal, clusterOverviewWithVersion.slotsTotal)
                && Objects.equals(this.slotsAvailable, clusterOverviewWithVersion.slotsAvailable)
                && Objects.equals(this.jobsRunning, clusterOverviewWithVersion.jobsRunning)
                && Objects.equals(this.jobsFinished, clusterOverviewWithVersion.jobsFinished)
                && Objects.equals(this.jobsCancelled, clusterOverviewWithVersion.jobsCancelled)
                && Objects.equals(this.jobsFailed, clusterOverviewWithVersion.jobsFailed)
                && Objects.equals(this.flinkVersion, clusterOverviewWithVersion.flinkVersion)
                && Objects.equals(this.flinkCommit, clusterOverviewWithVersion.flinkCommit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskmanagers, slotsTotal, slotsAvailable, jobsRunning, jobsFinished, jobsCancelled, jobsFailed,
                flinkVersion, flinkCommit);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ClusterOverviewWithVersion {\n");

        sb.append("    taskmanagers: ").append(toIndentedString(taskmanagers)).append("\n");
        sb.append("    slotsTotal: ").append(toIndentedString(slotsTotal)).append("\n");
        sb.append("    slotsAvailable: ").append(toIndentedString(slotsAvailable)).append("\n");
        sb.append("    jobsRunning: ").append(toIndentedString(jobsRunning)).append("\n");
        sb.append("    jobsFinished: ").append(toIndentedString(jobsFinished)).append("\n");
        sb.append("    jobsCancelled: ").append(toIndentedString(jobsCancelled)).append("\n");
        sb.append("    jobsFailed: ").append(toIndentedString(jobsFailed)).append("\n");
        sb.append("    flinkVersion: ").append(toIndentedString(flinkVersion)).append("\n");
        sb.append("    flinkCommit: ").append(toIndentedString(flinkCommit)).append("\n");
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