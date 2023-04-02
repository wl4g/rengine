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
 * ThreadInfo
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
        date = "2023-04-02T23:14:46.617621+08:00[Asia/Shanghai]")
public class ThreadInfo {
    @SerializedName("threadName")
    private String threadName = null;

    @SerializedName("stringifiedThreadInfo")
    private String stringifiedThreadInfo = null;

    public ThreadInfo threadName(String threadName) {
        this.threadName = threadName;
        return this;
    }

    /**
     * Get threadName
     * 
     * @return threadName
     **/
    @Schema(description = "")
    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public ThreadInfo stringifiedThreadInfo(String stringifiedThreadInfo) {
        this.stringifiedThreadInfo = stringifiedThreadInfo;
        return this;
    }

    /**
     * Get stringifiedThreadInfo
     * 
     * @return stringifiedThreadInfo
     **/
    @Schema(description = "")
    public String getStringifiedThreadInfo() {
        return stringifiedThreadInfo;
    }

    public void setStringifiedThreadInfo(String stringifiedThreadInfo) {
        this.stringifiedThreadInfo = stringifiedThreadInfo;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ThreadInfo threadInfo = (ThreadInfo) o;
        return Objects.equals(this.threadName, threadInfo.threadName)
                && Objects.equals(this.stringifiedThreadInfo, threadInfo.stringifiedThreadInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(threadName, stringifiedThreadInfo);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ThreadInfo {\n");

        sb.append("    threadName: ").append(toIndentedString(threadName)).append("\n");
        sb.append("    stringifiedThreadInfo: ").append(toIndentedString(stringifiedThreadInfo)).append("\n");
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
