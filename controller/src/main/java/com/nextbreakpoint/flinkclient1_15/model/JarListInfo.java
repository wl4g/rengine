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
 * JarListInfo
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
        date = "2023-04-02T23:14:46.617621+08:00[Asia/Shanghai]")
public class JarListInfo {
    @SerializedName("address")
    private String address = null;

    @SerializedName("files")
    private List<JarFileInfo> files = null;

    public JarListInfo address(String address) {
        this.address = address;
        return this;
    }

    /**
     * Get address
     * 
     * @return address
     **/
    @Schema(description = "")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public JarListInfo files(List<JarFileInfo> files) {
        this.files = files;
        return this;
    }

    public JarListInfo addFilesItem(JarFileInfo filesItem) {
        if (this.files == null) {
            this.files = new ArrayList<JarFileInfo>();
        }
        this.files.add(filesItem);
        return this;
    }

    /**
     * Get files
     * 
     * @return files
     **/
    @Schema(description = "")
    public List<JarFileInfo> getFiles() {
        return files;
    }

    public void setFiles(List<JarFileInfo> files) {
        this.files = files;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JarListInfo jarListInfo = (JarListInfo) o;
        return Objects.equals(this.address, jarListInfo.address) && Objects.equals(this.files, jarListInfo.files);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, files);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class JarListInfo {\n");

        sb.append("    address: ").append(toIndentedString(address)).append("\n");
        sb.append("    files: ").append(toIndentedString(files)).append("\n");
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
