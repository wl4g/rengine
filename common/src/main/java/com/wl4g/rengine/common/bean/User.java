package com.wl4g.rengine.common.bean;

import java.util.Date;

public class User extends UserKey {
    private Integer type;

    private String username;

    private String email;

    private String attrJson;

    private Date updateAt;

    private String updateBy;

    private Integer delFlag;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getAttrJson() {
        return attrJson;
    }

    public void setAttrJson(String attrJson) {
        this.attrJson = attrJson == null ? null : attrJson.trim();
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
}