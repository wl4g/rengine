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
 * AsynchronousOperationInfo
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
        date = "2023-04-02T23:14:46.617621+08:00[Asia/Shanghai]")
public class AsynchronousOperationInfo implements OneOfAsynchronousOperationResultOperation {
    @SerializedName("failure-cause")
    private SerializedThrowable failureCause = null;

    public AsynchronousOperationInfo failureCause(SerializedThrowable failureCause) {
        this.failureCause = failureCause;
        return this;
    }

    /**
     * Get failureCause
     * 
     * @return failureCause
     **/
    @Schema(description = "")
    public SerializedThrowable getFailureCause() {
        return failureCause;
    }

    public void setFailureCause(SerializedThrowable failureCause) {
        this.failureCause = failureCause;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AsynchronousOperationInfo asynchronousOperationInfo = (AsynchronousOperationInfo) o;
        return Objects.equals(this.failureCause, asynchronousOperationInfo.failureCause);
    }

    @Override
    public int hashCode() {
        return Objects.hash(failureCause);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AsynchronousOperationInfo {\n");

        sb.append("    failureCause: ").append(toIndentedString(failureCause)).append("\n");
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
