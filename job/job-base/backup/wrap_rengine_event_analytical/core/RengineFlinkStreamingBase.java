/*
 * Copyright 2017 ~ 2025 the original authors James Wong.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.job;

import static com.wl4g.infra.common.lang.Assert2.notNull;
import static java.time.Duration.ofMillis;
import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;

import java.util.Properties;

import org.apache.commons.cli.ParseException;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.api.connector.source.SourceSplit;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;

import com.wl4g.rengine.job.model.RengineEventAnalytical;
import com.wl4g.infra.common.cli.CommandLineTool;
import com.wl4g.infra.common.cli.CommandLineTool.CommandLineFacade;

import lombok.Getter;

/**
 * {@link RengineFlinkStreamingBase}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-06-07 v3.0.0
 * @since v1.0.0
 */
@Getter
public abstract class RengineFlinkStreamingBase implements Runnable {

    // MQ(kafka/pulsar/rabbitmq) server options.
    private String brokers;
    private String topicPattern;
    private String groupId;
    private Long fromOffsetTime;

    // FLINK Checkpoint options.
    private CheckpointingMode checkpointMode;
    private Long checkpointMillis;

    // Performance options.
    private Integer parallelism;
    private Integer maxParallelism;
    private Long bufferTimeoutMillis;
    private Long outOfOrdernessMillis;
    private Long idleTimeoutMillis;

    // FLINK Sink options.
    private Boolean forceUsePrintSink;

    // FLINK Job options.
    private String jobName;

    // Command line.
    protected transient final CommandLineTool.Builder builder;
    protected transient CommandLineFacade line;
    protected transient Properties props;

    protected RengineFlinkStreamingBase() {
        this.builder = CommandLineTool.builder()
                // MQ(Kafka/Pulsar/Rabbitmq/...) options.
                .option("b", "brokers", "localhost:9092", "Connect MQ brokers addresses. default is local kafka brokers")
                .option("t", "topicPattern", "rengine_event", "MQ topic regex pattern.")
                .mustOption("g", "groupId", "Flink source consumer group id.")
                .longOption("fromOffsetTime", "-1",
                        "Start consumption from the first record with a timestamp greater than or equal to a certain timestamp. if <=0, it will not be setup and keep the default behavior.")
                // Checkpoint options.
                .longOption("checkpointMode", null,
                        "Sets the checkpoint mode, the default is null means not enabled. options: "
                                + asList(CheckpointingMode.values()))
                .longOption("checkpointMillis", "500",
                        "Checkpoint execution interval millis, only valid when checkpointMode is sets.")
                // Performance options.
                .longOption("parallelism", "-1",
                        "The parallelism for operator. if <=0, it will not be setup and keep the default behavior.")
                .longOption("maxParallelism", "-1",
                        "The maximum parallelism for operator. if <=0, it will not be setup and keep the default behavior.")
                .longOption("bufferTimeoutMillis", "-1",
                        "Parallelism for this operator, if <=0, it will not be setup and keep the default behavior.")
                .longOption("outOfOrdernessMillis", "120000", "The maximum millis out-of-orderness watermark generator assumes.")
                .longOption("idleTimeoutMillis", "30000", "The timeout millis for the idleness detection.")
                // Sink options.
                .option("F", "forceUsePrintSink", "false", "Force override set to stdout print sink function.")
                // Job options.
                .option("J", "jobName", "RengineAggregateFlinkJob", "Flink connect MQ source streaming job name.");
    }

    /**
     * Parsing command arguments to {@link CommandLineFacade}.
     * 
     * @param args
     * @return
     * @throws ParseException
     */
    protected RengineFlinkStreamingBase parse(String[] args) throws ParseException {
        this.line = builder.helpIfEmpty(args).build(args);
        // KAFKA options.
        this.brokers = line.get("brokers");
        this.topicPattern = line.get("topicPattern");
        this.groupId = line.get("groupId");
        this.fromOffsetTime = line.getLong("fromOffsetTime");
        // Checkpoint options.
        this.checkpointMode = line.getEnum("checkpointMode", CheckpointingMode.class);
        this.checkpointMillis = line.getLong("checkpointMillis");
        // Performance options.
        this.parallelism = line.getInteger("parallelism");
        this.maxParallelism = line.getInteger("maxParallelism");
        this.bufferTimeoutMillis = line.getLong("bufferTimeoutMillis");
        this.outOfOrdernessMillis = line.getLong("outOfOrdernessMillis");
        this.idleTimeoutMillis = line.getLong("idleTimeoutMillis");
        // Sink options.
        this.forceUsePrintSink = line.getBoolean("forceUsePrintSink");
        // Job options.
        this.jobName = line.get("jobName");
        return this;
    }

    /**
     * Configuring custom FLINK environment properties.
     * 
     * @return
     */
    protected void customProps(Properties props) {
    }

    /**
     * Create FLINK source.
     * 
     * @return
     */
    protected abstract <T, S extends SourceSplit, E> Source<T, S, E> createSource();

    /**
     * Configuring custom FLINK data stream.
     * 
     * @return
     */
    protected abstract RengineFlinkStreamingBase customStream(DataStreamSource<RengineEventAnalytical> dataStream);

    /**
     * Handling job execution result.
     * 
     * @param result
     */
    protected void handleJobExecutionResult(JobExecutionResult result) {
    }

    @Override
    public void run() {
        notNull(line, IllegalStateException.class, "Parse arguments are not initialized, must call #parse() before");

        this.props = (Properties) System.getProperties().clone();

        // Custom properties.
        customProps(props);

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // see:https://github.com/apache/flink/blob/release-1.14.4/docs/content/docs/connectors/datastream/kafka.md#consumer-offset-committing
        if (nonNull(checkpointMode)) {
            env.enableCheckpointing(checkpointMillis, checkpointMode);
        }
        DataStreamSource<RengineEventAnalytical> dataStream = env.fromSource(createSource(),
                RengineEventWatermarks.newWatermarkStrategy(ofMillis(outOfOrdernessMillis), ofMillis(idleTimeoutMillis)),
                jobName.concat("Source"));
        if (parallelism > 0) {
            dataStream.setParallelism(parallelism);
        }
        if (maxParallelism > 0) {
            dataStream.setMaxParallelism(maxParallelism);
        }
        if (bufferTimeoutMillis > 0) {
            dataStream.setBufferTimeout(bufferTimeoutMillis);
        }

        if (nonNull(forceUsePrintSink) && forceUsePrintSink) {
            dataStream.addSink(new PrintSinkFunction<>());
        } else {
            customStream(dataStream);
        }

        try {
            handleJobExecutionResult(env.execute(jobName));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
