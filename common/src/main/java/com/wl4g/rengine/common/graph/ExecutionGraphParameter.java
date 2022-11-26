package com.wl4g.rengine.common.graph;

import static java.lang.System.currentTimeMillis;

import java.io.Serializable;
import java.util.UUID;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link ExecutionGraphParameter}
 * 
 * @author James Wong
 * @version 2022-11-03
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
public final class ExecutionGraphParameter implements Serializable {
    private static final long serialVersionUID = -982477387755376877L;

    private @Default long requestTime = currentTimeMillis();

    /**
     * The execution executing trace ID.
     */
    private @Default String traceId = UUID.randomUUID().toString().replaceAll("-", "");

    /**
     * The execution workflow ID.
     */
    private String workflowId;

    /**
     * The execution enable debugger mode.
     */
    private @Default boolean debug = true;

    private Object parameter;// TODO using graal.js ScriptContext ??
}
