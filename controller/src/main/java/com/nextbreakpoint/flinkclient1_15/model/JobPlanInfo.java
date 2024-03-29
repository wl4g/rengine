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
 * JobPlanInfo
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
        date = "2023-04-02T23:14:46.617621+08:00[Asia/Shanghai]")
public class JobPlanInfo {
    @SerializedName("plan")
    private RawJson plan = null;

    public JobPlanInfo plan(RawJson plan) {
        this.plan = plan;
        return this;
    }

    /**
     * Get plan
     * 
     * @return plan
     **/
    @Schema(description = "")
    public RawJson getPlan() {
        return plan;
    }

    public void setPlan(RawJson plan) {
        this.plan = plan;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JobPlanInfo jobPlanInfo = (JobPlanInfo) o;
        return Objects.equals(this.plan, jobPlanInfo.plan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plan);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class JobPlanInfo {\n");

        sb.append("    plan: ").append(toIndentedString(plan)).append("\n");
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
