package com.wl4g.rengine.service.impl;

import com.wl4g.rengine.service.model.BaseDeleteResult;

public class RuleScriptServiceImpl {

    public BaseDeleteResult deleteRuleScript(String id) {
        // Use static access for builder
        return BaseDeleteResult.builder()
                .success(true)
                .build();
    }

    // ... existing code ...
}
