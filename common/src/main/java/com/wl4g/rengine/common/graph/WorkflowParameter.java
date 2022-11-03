package com.wl4g.rengine.common.graph;

import static java.lang.System.currentTimeMillis;

import java.io.Serializable;

import com.wl4g.rengine.common.util.IdGenUtil;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link WorkflowParameter}
 * 
 * @author James Wong
 * @version 2022-11-03
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString
public final class WorkflowParameter implements Serializable {
    private static final long serialVersionUID = -982477387755376877L;

    private @Default long requestTime = currentTimeMillis();
    private @Default String traceId = IdGenUtil.next();
    private @Default long priority = 0L;
    private @Default boolean debug = false;
    private Object parameter;// TODO using graal.js ScriptContext ??
}
