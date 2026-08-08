package com.wl4g.rengine.common.entity.sys;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tenant {
    private Long id;
    private String name;
    private String password;
}
