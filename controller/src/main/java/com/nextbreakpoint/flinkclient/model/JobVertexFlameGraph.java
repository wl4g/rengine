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
 * JobVertexFlameGraph
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
        date = "2023-04-02T23:14:46.617621+08:00[Asia/Shanghai]")
public class JobVertexFlameGraph {
    @SerializedName("endTimestamp")
    private Long endTimestamp = null;

    @SerializedName("data")
    private Node data = null;

    public JobVertexFlameGraph endTimestamp(Long endTimestamp) {
        this.endTimestamp = endTimestamp;
        return this;
    }

    /**
     * Get endTimestamp
     * 
     * @return endTimestamp
     **/
    @Schema(description = "")
    public Long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public JobVertexFlameGraph data(Node data) {
        this.data = data;
        return this;
    }

    /**
     * Get data
     * 
     * @return data
     **/
    @Schema(description = "")
    public Node getData() {
        return data;
    }

    public void setData(Node data) {
        this.data = data;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JobVertexFlameGraph jobVertexFlameGraph = (JobVertexFlameGraph) o;
        return Objects.equals(this.endTimestamp, jobVertexFlameGraph.endTimestamp)
                && Objects.equals(this.data, jobVertexFlameGraph.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endTimestamp, data);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class JobVertexFlameGraph {\n");

        sb.append("    endTimestamp: ").append(toIndentedString(endTimestamp)).append("\n");
        sb.append("    data: ").append(toIndentedString(data)).append("\n");
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
