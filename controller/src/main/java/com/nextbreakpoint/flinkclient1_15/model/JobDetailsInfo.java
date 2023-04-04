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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * JobDetailsInfo
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
        date = "2023-04-02T23:14:46.617621+08:00[Asia/Shanghai]")
public class JobDetailsInfo {
    @SerializedName("jid")
    private String jid = null;

    @SerializedName("name")
    private String name = null;

    @SerializedName("isStoppable")
    private Boolean isStoppable = null;

    /**
     * Gets or Sets state
     */
    @JsonAdapter(StateEnum.Adapter.class)
    public enum StateEnum {
        INITIALIZING("INITIALIZING"), CREATED("CREATED"), RUNNING("RUNNING"), FAILING("FAILING"), FAILED("FAILED"), CANCELLING(
                "CANCELLING"), CANCELED("CANCELED"), FINISHED(
                        "FINISHED"), RESTARTING("RESTARTING"), SUSPENDED("SUSPENDED"), RECONCILING("RECONCILING");

        private String value;

        StateEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static StateEnum fromValue(String text) {
            for (StateEnum b : StateEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public static class Adapter extends TypeAdapter<StateEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final StateEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public StateEnum read(final JsonReader jsonReader) throws IOException {
                Object value = jsonReader.nextString();
                return StateEnum.fromValue(String.valueOf(value));
            }
        }
    }

    @SerializedName("state")
    private StateEnum state = null;

    @SerializedName("start-time")
    private Long startTime = null;

    @SerializedName("end-time")
    private Long endTime = null;

    @SerializedName("duration")
    private Long duration = null;

    @SerializedName("maxParallelism")
    private Long maxParallelism = null;

    @SerializedName("now")
    private Long now = null;

    @SerializedName("timestamps")
    private Map<String, Long> timestamps = null;

    @SerializedName("vertices")
    private List<JobVertexDetailsInfo> vertices = null;

    @SerializedName("status-counts")
    private Map<String, Integer> statusCounts = null;

    @SerializedName("plan")
    private String plan = null;

    public JobDetailsInfo jid(String jid) {
        this.jid = jid;
        return this;
    }

    /**
     * Get jid
     * 
     * @return jid
     **/
    @Schema(description = "")
    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public JobDetailsInfo name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get name
     * 
     * @return name
     **/
    @Schema(description = "")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JobDetailsInfo isStoppable(Boolean isStoppable) {
        this.isStoppable = isStoppable;
        return this;
    }

    /**
     * Get isStoppable
     * 
     * @return isStoppable
     **/
    @Schema(description = "")
    public Boolean isIsStoppable() {
        return isStoppable;
    }

    public void setIsStoppable(Boolean isStoppable) {
        this.isStoppable = isStoppable;
    }

    public JobDetailsInfo state(StateEnum state) {
        this.state = state;
        return this;
    }

    /**
     * Get state
     * 
     * @return state
     **/
    @Schema(description = "")
    public StateEnum getState() {
        return state;
    }

    public void setState(StateEnum state) {
        this.state = state;
    }

    public JobDetailsInfo startTime(Long startTime) {
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

    public JobDetailsInfo endTime(Long endTime) {
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

    public JobDetailsInfo duration(Long duration) {
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

    public JobDetailsInfo maxParallelism(Long maxParallelism) {
        this.maxParallelism = maxParallelism;
        return this;
    }

    /**
     * Get maxParallelism
     * 
     * @return maxParallelism
     **/
    @Schema(description = "")
    public Long getMaxParallelism() {
        return maxParallelism;
    }

    public void setMaxParallelism(Long maxParallelism) {
        this.maxParallelism = maxParallelism;
    }

    public JobDetailsInfo now(Long now) {
        this.now = now;
        return this;
    }

    /**
     * Get now
     * 
     * @return now
     **/
    @Schema(description = "")
    public Long getNow() {
        return now;
    }

    public void setNow(Long now) {
        this.now = now;
    }

    public JobDetailsInfo timestamps(Map<String, Long> timestamps) {
        this.timestamps = timestamps;
        return this;
    }

    public JobDetailsInfo putTimestampsItem(String key, Long timestampsItem) {
        if (this.timestamps == null) {
            this.timestamps = new HashMap<String, Long>();
        }
        this.timestamps.put(key, timestampsItem);
        return this;
    }

    /**
     * Get timestamps
     * 
     * @return timestamps
     **/
    @Schema(description = "")
    public Map<String, Long> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(Map<String, Long> timestamps) {
        this.timestamps = timestamps;
    }

    public JobDetailsInfo vertices(List<JobVertexDetailsInfo> vertices) {
        this.vertices = vertices;
        return this;
    }

    public JobDetailsInfo addVerticesItem(JobVertexDetailsInfo verticesItem) {
        if (this.vertices == null) {
            this.vertices = new ArrayList<JobVertexDetailsInfo>();
        }
        this.vertices.add(verticesItem);
        return this;
    }

    /**
     * Get vertices
     * 
     * @return vertices
     **/
    @Schema(description = "")
    public List<JobVertexDetailsInfo> getVertices() {
        return vertices;
    }

    public void setVertices(List<JobVertexDetailsInfo> vertices) {
        this.vertices = vertices;
    }

    public JobDetailsInfo statusCounts(Map<String, Integer> statusCounts) {
        this.statusCounts = statusCounts;
        return this;
    }

    public JobDetailsInfo putStatusCountsItem(String key, Integer statusCountsItem) {
        if (this.statusCounts == null) {
            this.statusCounts = new HashMap<String, Integer>();
        }
        this.statusCounts.put(key, statusCountsItem);
        return this;
    }

    /**
     * Get statusCounts
     * 
     * @return statusCounts
     **/
    @Schema(description = "")
    public Map<String, Integer> getStatusCounts() {
        return statusCounts;
    }

    public void setStatusCounts(Map<String, Integer> statusCounts) {
        this.statusCounts = statusCounts;
    }

    public JobDetailsInfo plan(String plan) {
        this.plan = plan;
        return this;
    }

    /**
     * Get plan
     * 
     * @return plan
     **/
    @Schema(description = "")
    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
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
        JobDetailsInfo jobDetailsInfo = (JobDetailsInfo) o;
        return Objects.equals(this.jid, jobDetailsInfo.jid) && Objects.equals(this.name, jobDetailsInfo.name)
                && Objects.equals(this.isStoppable, jobDetailsInfo.isStoppable)
                && Objects.equals(this.state, jobDetailsInfo.state) && Objects.equals(this.startTime, jobDetailsInfo.startTime)
                && Objects.equals(this.endTime, jobDetailsInfo.endTime) && Objects.equals(this.duration, jobDetailsInfo.duration)
                && Objects.equals(this.maxParallelism, jobDetailsInfo.maxParallelism)
                && Objects.equals(this.now, jobDetailsInfo.now) && Objects.equals(this.timestamps, jobDetailsInfo.timestamps)
                && Objects.equals(this.vertices, jobDetailsInfo.vertices)
                && Objects.equals(this.statusCounts, jobDetailsInfo.statusCounts)
                && Objects.equals(this.plan, jobDetailsInfo.plan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jid, name, isStoppable, state, startTime, endTime, duration, maxParallelism, now, timestamps,
                vertices, statusCounts, plan);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class JobDetailsInfo {\n");

        sb.append("    jid: ").append(toIndentedString(jid)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    isStoppable: ").append(toIndentedString(isStoppable)).append("\n");
        sb.append("    state: ").append(toIndentedString(state)).append("\n");
        sb.append("    startTime: ").append(toIndentedString(startTime)).append("\n");
        sb.append("    endTime: ").append(toIndentedString(endTime)).append("\n");
        sb.append("    duration: ").append(toIndentedString(duration)).append("\n");
        sb.append("    maxParallelism: ").append(toIndentedString(maxParallelism)).append("\n");
        sb.append("    now: ").append(toIndentedString(now)).append("\n");
        sb.append("    timestamps: ").append(toIndentedString(timestamps)).append("\n");
        sb.append("    vertices: ").append(toIndentedString(vertices)).append("\n");
        sb.append("    statusCounts: ").append(toIndentedString(statusCounts)).append("\n");
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