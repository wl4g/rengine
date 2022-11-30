package com.wl4g.rengine.common.graph;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyMap;

import java.io.Serializable;
import java.util.Map;
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
@ToString
@SuperBuilder
@NoArgsConstructor
public final class ExecutionGraphParameter implements Serializable {
    private static final long serialVersionUID = -982477387755376877L;

    /**
     * The execution request time-stamp.
     */
    private @Default long requestTime = currentTimeMillis();

    /**
     * The execution executing trace ID.
     */
    private @Default String traceId = UUID.randomUUID().toString().replaceAll("-", "");

    /**
     * The execution enable tracing mode.
     */
    private @Default boolean trace = true;

    /**
     * The execution enable debuging mode.
     */
    private @Default boolean debug = true;

    /**
     * The execution workflow ID.
     */
    private String workflowId;

    /**
     * The execution workflow arguments.
     */
    private @Default Map<String, Object> args = emptyMap();

}
