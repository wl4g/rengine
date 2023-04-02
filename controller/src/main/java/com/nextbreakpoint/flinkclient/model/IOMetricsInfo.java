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
 * IOMetricsInfo
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
        date = "2023-04-02T23:14:46.617621+08:00[Asia/Shanghai]")
public class IOMetricsInfo {
    @SerializedName("read-bytes")
    private Long readBytes = null;

    @SerializedName("read-bytes-complete")
    private Boolean readBytesComplete = null;

    @SerializedName("write-bytes")
    private Long writeBytes = null;

    @SerializedName("write-bytes-complete")
    private Boolean writeBytesComplete = null;

    @SerializedName("read-records")
    private Long readRecords = null;

    @SerializedName("read-records-complete")
    private Boolean readRecordsComplete = null;

    @SerializedName("write-records")
    private Long writeRecords = null;

    @SerializedName("write-records-complete")
    private Boolean writeRecordsComplete = null;

    public IOMetricsInfo readBytes(Long readBytes) {
        this.readBytes = readBytes;
        return this;
    }

    /**
     * Get readBytes
     * 
     * @return readBytes
     **/
    @Schema(description = "")
    public Long getReadBytes() {
        return readBytes;
    }

    public void setReadBytes(Long readBytes) {
        this.readBytes = readBytes;
    }

    public IOMetricsInfo readBytesComplete(Boolean readBytesComplete) {
        this.readBytesComplete = readBytesComplete;
        return this;
    }

    /**
     * Get readBytesComplete
     * 
     * @return readBytesComplete
     **/
    @Schema(description = "")
    public Boolean isReadBytesComplete() {
        return readBytesComplete;
    }

    public void setReadBytesComplete(Boolean readBytesComplete) {
        this.readBytesComplete = readBytesComplete;
    }

    public IOMetricsInfo writeBytes(Long writeBytes) {
        this.writeBytes = writeBytes;
        return this;
    }

    /**
     * Get writeBytes
     * 
     * @return writeBytes
     **/
    @Schema(description = "")
    public Long getWriteBytes() {
        return writeBytes;
    }

    public void setWriteBytes(Long writeBytes) {
        this.writeBytes = writeBytes;
    }

    public IOMetricsInfo writeBytesComplete(Boolean writeBytesComplete) {
        this.writeBytesComplete = writeBytesComplete;
        return this;
    }

    /**
     * Get writeBytesComplete
     * 
     * @return writeBytesComplete
     **/
    @Schema(description = "")
    public Boolean isWriteBytesComplete() {
        return writeBytesComplete;
    }

    public void setWriteBytesComplete(Boolean writeBytesComplete) {
        this.writeBytesComplete = writeBytesComplete;
    }

    public IOMetricsInfo readRecords(Long readRecords) {
        this.readRecords = readRecords;
        return this;
    }

    /**
     * Get readRecords
     * 
     * @return readRecords
     **/
    @Schema(description = "")
    public Long getReadRecords() {
        return readRecords;
    }

    public void setReadRecords(Long readRecords) {
        this.readRecords = readRecords;
    }

    public IOMetricsInfo readRecordsComplete(Boolean readRecordsComplete) {
        this.readRecordsComplete = readRecordsComplete;
        return this;
    }

    /**
     * Get readRecordsComplete
     * 
     * @return readRecordsComplete
     **/
    @Schema(description = "")
    public Boolean isReadRecordsComplete() {
        return readRecordsComplete;
    }

    public void setReadRecordsComplete(Boolean readRecordsComplete) {
        this.readRecordsComplete = readRecordsComplete;
    }

    public IOMetricsInfo writeRecords(Long writeRecords) {
        this.writeRecords = writeRecords;
        return this;
    }

    /**
     * Get writeRecords
     * 
     * @return writeRecords
     **/
    @Schema(description = "")
    public Long getWriteRecords() {
        return writeRecords;
    }

    public void setWriteRecords(Long writeRecords) {
        this.writeRecords = writeRecords;
    }

    public IOMetricsInfo writeRecordsComplete(Boolean writeRecordsComplete) {
        this.writeRecordsComplete = writeRecordsComplete;
        return this;
    }

    /**
     * Get writeRecordsComplete
     * 
     * @return writeRecordsComplete
     **/
    @Schema(description = "")
    public Boolean isWriteRecordsComplete() {
        return writeRecordsComplete;
    }

    public void setWriteRecordsComplete(Boolean writeRecordsComplete) {
        this.writeRecordsComplete = writeRecordsComplete;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IOMetricsInfo ioMetricsInfo = (IOMetricsInfo) o;
        return Objects.equals(this.readBytes, ioMetricsInfo.readBytes)
                && Objects.equals(this.readBytesComplete, ioMetricsInfo.readBytesComplete)
                && Objects.equals(this.writeBytes, ioMetricsInfo.writeBytes)
                && Objects.equals(this.writeBytesComplete, ioMetricsInfo.writeBytesComplete)
                && Objects.equals(this.readRecords, ioMetricsInfo.readRecords)
                && Objects.equals(this.readRecordsComplete, ioMetricsInfo.readRecordsComplete)
                && Objects.equals(this.writeRecords, ioMetricsInfo.writeRecords)
                && Objects.equals(this.writeRecordsComplete, ioMetricsInfo.writeRecordsComplete);
    }

    @Override
    public int hashCode() {
        return Objects.hash(readBytes, readBytesComplete, writeBytes, writeBytesComplete, readRecords, readRecordsComplete,
                writeRecords, writeRecordsComplete);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class IOMetricsInfo {\n");

        sb.append("    readBytes: ").append(toIndentedString(readBytes)).append("\n");
        sb.append("    readBytesComplete: ").append(toIndentedString(readBytesComplete)).append("\n");
        sb.append("    writeBytes: ").append(toIndentedString(writeBytes)).append("\n");
        sb.append("    writeBytesComplete: ").append(toIndentedString(writeBytesComplete)).append("\n");
        sb.append("    readRecords: ").append(toIndentedString(readRecords)).append("\n");
        sb.append("    readRecordsComplete: ").append(toIndentedString(readRecordsComplete)).append("\n");
        sb.append("    writeRecords: ").append(toIndentedString(writeRecords)).append("\n");
        sb.append("    writeRecordsComplete: ").append(toIndentedString(writeRecordsComplete)).append("\n");
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
