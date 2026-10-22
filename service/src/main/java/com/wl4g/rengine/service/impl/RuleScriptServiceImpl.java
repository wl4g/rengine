package com.wl4g.rengine.service.impl;

import com.wl4g.rengine.service.model.BaseDeleteResult;

public class RuleScriptServiceImpl {
    public BaseDeleteResult deleteRuleScript(String id) {
        // Simplified delete logic
        return BaseDeleteResult.builder()
                .deleted(true)
                .id(id)
                .build();
    }
}
