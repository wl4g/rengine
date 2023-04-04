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
 * StatsSummaryDto
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
        date = "2023-04-02T23:14:46.617621+08:00[Asia/Shanghai]")
public class StatsSummaryDto {
    @SerializedName("min")
    private Long min = null;

    @SerializedName("max")
    private Long max = null;

    @SerializedName("avg")
    private Long avg = null;

    @SerializedName("p50")
    private Double p50 = null;

    @SerializedName("p90")
    private Double p90 = null;

    @SerializedName("p95")
    private Double p95 = null;

    @SerializedName("p99")
    private Double p99 = null;

    @SerializedName("p999")
    private Double p999 = null;

    public StatsSummaryDto min(Long min) {
        this.min = min;
        return this;
    }

    /**
     * Get min
     * 
     * @return min
     **/
    @Schema(description = "")
    public Long getMin() {
        return min;
    }

    public void setMin(Long min) {
        this.min = min;
    }

    public StatsSummaryDto max(Long max) {
        this.max = max;
        return this;
    }

    /**
     * Get max
     * 
     * @return max
     **/
    @Schema(description = "")
    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    public StatsSummaryDto avg(Long avg) {
        this.avg = avg;
        return this;
    }

    /**
     * Get avg
     * 
     * @return avg
     **/
    @Schema(description = "")
    public Long getAvg() {
        return avg;
    }

    public void setAvg(Long avg) {
        this.avg = avg;
    }

    public StatsSummaryDto p50(Double p50) {
        this.p50 = p50;
        return this;
    }

    /**
     * Get p50
     * 
     * @return p50
     **/
    @Schema(description = "")
    public Double getP50() {
        return p50;
    }

    public void setP50(Double p50) {
        this.p50 = p50;
    }

    public StatsSummaryDto p90(Double p90) {
        this.p90 = p90;
        return this;
    }

    /**
     * Get p90
     * 
     * @return p90
     **/
    @Schema(description = "")
    public Double getP90() {
        return p90;
    }

    public void setP90(Double p90) {
        this.p90 = p90;
    }

    public StatsSummaryDto p95(Double p95) {
        this.p95 = p95;
        return this;
    }

    /**
     * Get p95
     * 
     * @return p95
     **/
    @Schema(description = "")
    public Double getP95() {
        return p95;
    }

    public void setP95(Double p95) {
        this.p95 = p95;
    }

    public StatsSummaryDto p99(Double p99) {
        this.p99 = p99;
        return this;
    }

    /**
     * Get p99
     * 
     * @return p99
     **/
    @Schema(description = "")
    public Double getP99() {
        return p99;
    }

    public void setP99(Double p99) {
        this.p99 = p99;
    }

    public StatsSummaryDto p999(Double p999) {
        this.p999 = p999;
        return this;
    }

    /**
     * Get p999
     * 
     * @return p999
     **/
    @Schema(description = "")
    public Double getP999() {
        return p999;
    }

    public void setP999(Double p999) {
        this.p999 = p999;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StatsSummaryDto statsSummaryDto = (StatsSummaryDto) o;
        return Objects.equals(this.min, statsSummaryDto.min) && Objects.equals(this.max, statsSummaryDto.max)
                && Objects.equals(this.avg, statsSummaryDto.avg) && Objects.equals(this.p50, statsSummaryDto.p50)
                && Objects.equals(this.p90, statsSummaryDto.p90) && Objects.equals(this.p95, statsSummaryDto.p95)
                && Objects.equals(this.p99, statsSummaryDto.p99) && Objects.equals(this.p999, statsSummaryDto.p999);
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max, avg, p50, p90, p95, p99, p999);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class StatsSummaryDto {\n");

        sb.append("    min: ").append(toIndentedString(min)).append("\n");
        sb.append("    max: ").append(toIndentedString(max)).append("\n");
        sb.append("    avg: ").append(toIndentedString(avg)).append("\n");
        sb.append("    p50: ").append(toIndentedString(p50)).append("\n");
        sb.append("    p90: ").append(toIndentedString(p90)).append("\n");
        sb.append("    p95: ").append(toIndentedString(p95)).append("\n");
        sb.append("    p99: ").append(toIndentedString(p99)).append("\n");
        sb.append("    p999: ").append(toIndentedString(p999)).append("\n");
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