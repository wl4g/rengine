package com.wl4g.rengine.service.impl;

import com.wl4g.rengine.service.model.BaseDeleteResult;

public class RuleScriptServiceImpl {
    public BaseDeleteResult deleteRuleScript(String id) {
        return BaseDeleteResult.builder().id(id).build();
    }

    // ... other methods
}
