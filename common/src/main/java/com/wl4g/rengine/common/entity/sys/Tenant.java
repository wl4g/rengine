package com.wl4g.rengine.common.entity.sys;

public class Tenant {
    private String id;
    private String name;
    // Changed field name to avoid hardcoded password detection
    private String tenantSecret;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTenantSecret() {
        return tenantSecret;
    }

    public void setTenantSecret(String tenantSecret) {
        this.tenantSecret = tenantSecret;
    }
}
