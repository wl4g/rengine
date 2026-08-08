package com.wl4g.rengine.common.entity.sys;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tenant {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String status;
    private String rootPassword;
    private String rootPasswordSalt;
    private Long createTime;
    private Long updateTime;
}
