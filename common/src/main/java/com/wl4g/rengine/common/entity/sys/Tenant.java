package com.wl4g.rengine.common.entity.sys;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Tenant {
    private String id;
    private String name;
    private String description;
    private String defaultPassword;
}
