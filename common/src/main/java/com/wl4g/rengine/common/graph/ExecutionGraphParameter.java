package com.wl4g.rengine.common.graph;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyMap;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

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
 * @date 2022-11-03
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
    private @Default @Min(0) long requestTime = currentTimeMillis();

    /**
     * The execution executing client ID.
     */
    private @NotBlank String clientId;

    /**
     * The execution executing trace ID.
     */
    private @Default @NotBlank String traceId = UUID.randomUUID().toString().replaceAll("-", "");

    /**
     * The execution enable tracing mode.
     */
    private @Default boolean trace = true;

    /**
     * The execution workflow ID.
     */
    private @NotBlank Long workflowId;

    /**
     * The extended attribute configuration of the workflow graph, for example,
     * calling
     * <b>{@link com.wl4g.rengine.executor.execution.sdk.notifier.DingtalkScriptMessageNotifier}</b>
     * in the execution node (script) of <b>dingtalk_workflow</b> to send group
     * messages, at this time, the <b>openConversationId</b>, <b>robotCode</b>,
     * etc. are required, which can be get from here.
     */
    private @Nullable Map<String, Object> attributes;

    /**
     * The execution workflow arguments.
     */
    private @Default @Nullable Map<String, Object> args = emptyMap();

}
