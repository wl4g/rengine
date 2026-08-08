package com.wl4g.rengine.common.entity.sys;

import com.wl4g.rengine.common.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tenant extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String name;
    private String code;
    private String description;
    private String status;
    private String secretKey;

    // The default secret key is generated at runtime, not hard-coded
    public static final String DEFAULT_SECRET_KEY = generateDefaultSecretKey();

    private static String generateDefaultSecretKey() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}
