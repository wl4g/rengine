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
 * JobSubmitRequestBody
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
        date = "2023-04-02T23:14:46.617621+08:00[Asia/Shanghai]")
public class JobSubmitRequestBody {
    @SerializedName("jobGraphFileName")
    private String jobGraphFileName = null;

    @SerializedName("jobJarFileNames")
    private List<String> jobJarFileNames = null;

    @SerializedName("jobArtifactFileNames")
    private List<DistributedCacheFile> jobArtifactFileNames = null;

    public JobSubmitRequestBody jobGraphFileName(String jobGraphFileName) {
        this.jobGraphFileName = jobGraphFileName;
        return this;
    }

    /**
     * Get jobGraphFileName
     * 
     * @return jobGraphFileName
     **/
    @Schema(description = "")
    public String getJobGraphFileName() {
        return jobGraphFileName;
    }

    public void setJobGraphFileName(String jobGraphFileName) {
        this.jobGraphFileName = jobGraphFileName;
    }

    public JobSubmitRequestBody jobJarFileNames(List<String> jobJarFileNames) {
        this.jobJarFileNames = jobJarFileNames;
        return this;
    }

    public JobSubmitRequestBody addJobJarFileNamesItem(String jobJarFileNamesItem) {
        if (this.jobJarFileNames == null) {
            this.jobJarFileNames = new ArrayList<String>();
        }
        this.jobJarFileNames.add(jobJarFileNamesItem);
        return this;
    }

    /**
     * Get jobJarFileNames
     * 
     * @return jobJarFileNames
     **/
    @Schema(description = "")
    public List<String> getJobJarFileNames() {
        return jobJarFileNames;
    }

    public void setJobJarFileNames(List<String> jobJarFileNames) {
        this.jobJarFileNames = jobJarFileNames;
    }

    public JobSubmitRequestBody jobArtifactFileNames(List<DistributedCacheFile> jobArtifactFileNames) {
        this.jobArtifactFileNames = jobArtifactFileNames;
        return this;
    }

    public JobSubmitRequestBody addJobArtifactFileNamesItem(DistributedCacheFile jobArtifactFileNamesItem) {
        if (this.jobArtifactFileNames == null) {
            this.jobArtifactFileNames = new ArrayList<DistributedCacheFile>();
        }
        this.jobArtifactFileNames.add(jobArtifactFileNamesItem);
        return this;
    }

    /**
     * Get jobArtifactFileNames
     * 
     * @return jobArtifactFileNames
     **/
    @Schema(description = "")
    public List<DistributedCacheFile> getJobArtifactFileNames() {
        return jobArtifactFileNames;
    }

    public void setJobArtifactFileNames(List<DistributedCacheFile> jobArtifactFileNames) {
        this.jobArtifactFileNames = jobArtifactFileNames;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JobSubmitRequestBody jobSubmitRequestBody = (JobSubmitRequestBody) o;
        return Objects.equals(this.jobGraphFileName, jobSubmitRequestBody.jobGraphFileName)
                && Objects.equals(this.jobJarFileNames, jobSubmitRequestBody.jobJarFileNames)
                && Objects.equals(this.jobArtifactFileNames, jobSubmitRequestBody.jobArtifactFileNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobGraphFileName, jobJarFileNames, jobArtifactFileNames);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class JobSubmitRequestBody {\n");

        sb.append("    jobGraphFileName: ").append(toIndentedString(jobGraphFileName)).append("\n");
        sb.append("    jobJarFileNames: ").append(toIndentedString(jobJarFileNames)).append("\n");
        sb.append("    jobArtifactFileNames: ").append(toIndentedString(jobArtifactFileNames)).append("\n");
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