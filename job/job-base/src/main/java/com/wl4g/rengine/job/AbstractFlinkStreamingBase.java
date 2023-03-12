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
 * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.job;

import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.runtime.JvmRuntimeTool.isJvmInDebugging;
import static java.lang.String.valueOf;
import static java.time.Duration.ofMillis;
import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.flink.api.common.restartstrategy.RestartStrategies.fixedDelayRestart;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.ParseException;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.api.connector.source.SourceSplit;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.wl4g.infra.common.cli.CommandLineTool;
import com.wl4g.infra.common.cli.CommandLineTool.CommandLineFacade;
import com.wl4g.rengine.common.event.RengineEvent;

import lombok.Getter;

/**
 * {@link AbstractFlinkStreamingBase}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-06-07 v3.0.0
 * @since v1.0.0
 */
@Getter
public abstract class AbstractFlinkStreamingBase implements Runnable {

    // Flink MQ(kafka/pulsar/rabbitmq/...) options.
    private String brokers;
    private String topicPattern;
    private String groupId;
    private Long fromOffsetTime;
    private String deserializerClass;

    // FLINK basic options.
    private RuntimeExecutionMode runtimeMode;
    private int restartAttempts;
    private int restartDelaySeconds;

    // FLINK Checkpoint options.
    private String checkpointDir;
    private CheckpointingMode checkpointMode;
    private Long checkpointIntervalMs;
    @Deprecated
    private Integer checkpointSizeThreshold;
    private Long checkpointTimeout;
    private Long checkpointMinPauseBetween;
    private Integer checkpointMaxConcurrent;
    private ExternalizedCheckpointCleanup externalizedCheckpointCleanup;

    // FLINK Performance options.
    private Integer parallelism;
    private Integer maxParallelism;
    private Long bufferTimeoutMillis;
    private Long outOfOrdernessMillis;
    private Long idleTimeoutMillis;

    // FLINK Sink options.
    private Boolean forceUsePrintSink;

    // FLINK ControllerLog options.
    private String jobName;

    // Command line.
    protected transient final CommandLineTool.Builder builder;
    protected transient CommandLineFacade line;
    protected transient Map<String, String> props;

    protected AbstractFlinkStreamingBase() {
        this.builder = CommandLineTool.builder()
                // MQ(Kafka/Pulsar/Rabbitmq/...) options.
                .option("B", "brokers", "localhost:9092", "Connect MQ brokers addresses. default is local kafka brokers")
                .option("T", "topicPattern", "rengine_event", "MQ topic regex pattern.")
                .mustOption("G", "groupId", "Flink source consumer group id.")
                .option("O", "fromOffsetTime", "-1",
                        "Start consumption from the first record with a timestamp greater than or equal to a certain timestamp. if <=0, it will not be setup and keep the default behavior.")
                .option("D", "deserializerClass", "com.wl4g.rengine.job.kafka.GenericKafkaDeserializationSchema",
                        "Deserializer class for Flink-streaming to consuming from MQ.")
                // FLINK basic options.
                .option("R", "runtimeMode", RuntimeExecutionMode.STREAMING.name(),
                        "Set the job execution mode. default is: STREAMING")
                .longOption("restartAttempts", "3", "Set the maximum number of failed restart attempts. default is: 3")
                .longOption("restartDelaySeconds", "15",
                        "Set the maximum number of failed interval between each restart. default is: 15")
                // FLINK Checkpoint options.
                .longOption("checkpointDir", (isJvmInDebugging ? "file" : "hdfs") + ":///tmp/flink-checkpoint",
                        "Checkpoint execution interval millis, only valid when checkpointMode is sets.")
                .longOption("checkpointMode", CheckpointingMode.AT_LEAST_ONCE.name(),
                        "Sets the checkpoint mode, the default is null means not enabled. options: "
                                + asList(CheckpointingMode.values()))
                .longOption("checkpointIntervalMs", null,
                        "Checkpoint execution interval millis, only valid when checkpointMode is sets.")
                .longOption("checkpointSizeThreshold", null, "")
                .longOption("checkpointTimeout", null, "Checkpoint timeout millis.")
                .longOption("checkpointMinPauseBetween", null, "The minimum time interval between two checkpoints.")
                .longOption("checkpointMaxConcurrent", null, "")
                .longOption("externalizedCheckpointCleanup", null, "The program is closed, an extra checkpoint is triggered.")
                // FLINK Performance options.
                .option("p", "parallelism", "-1",
                        "The parallelism for operator. if <=0, it will not be setup and keep the default behavior.")
                .longOption("maxParallelism", "-1",
                        "The maximum parallelism for operator. if <=0, it will not be setup and keep the default behavior.")
                .longOption("bufferTimeoutMillis", "-1",
                        "Parallelism for this operator, if <=0, it will not be setup and keep the default behavior.")
                .longOption("outOfOrdernessMillis", "120000", "The maximum millis out-of-orderness watermark generator assumes.")
                .longOption("idleTimeoutMillis", "30000", "The timeout millis for the idleness detection.")
                // FLINK Sink options.
                .option("F", "forceUsePrintSink", "false", "Force override set to stdout print sink function.")
                // ControllerLog options.
                .option("J", "jobName", getClass().getSimpleName().concat("Job"), "Flink connect MQ source streaming job name.");
    }

    /**
     * Parsing command arguments to {@link CommandLineFacade}.
     * 
     * @param args
     * @return
     * @throws ParseException
     */
    protected AbstractFlinkStreamingBase parse(String[] args) throws ParseException {
        this.line = builder.helpIfEmpty(args).build(args);
        // KAFKA options.
        this.brokers = line.get("brokers");
        this.topicPattern = line.get("topicPattern");
        this.groupId = line.get("groupId");
        this.fromOffsetTime = line.getLong("fromOffsetTime");
        this.deserializerClass = line.get("deserializerClass");
        // FLINK Basic options.
        this.runtimeMode = line.getEnum("runtimeMode", RuntimeExecutionMode.class);
        this.restartAttempts = line.getInteger("restartAttempts");
        this.restartDelaySeconds = line.getInteger("restartDelaySeconds");
        // FLINK Checkpoint options.
        this.checkpointDir = line.get("checkpointDir");
        this.checkpointMode = line.getEnum("checkpointMode", CheckpointingMode.class);
        this.checkpointIntervalMs = line.getLong("checkpointIntervalMs");
        this.checkpointSizeThreshold = line.getInteger("checkpointSizeThreshold");
        this.checkpointTimeout = line.getLong("checkpointTimeout");
        this.checkpointMinPauseBetween = line.getLong("checkpointMinPauseBetween");
        this.checkpointMaxConcurrent = line.getInteger("checkpointMaxConcurrent");
        this.externalizedCheckpointCleanup = line.getEnum("externalizedCheckpointCleanup", ExternalizedCheckpointCleanup.class);
        // FLINK Performance options.
        this.parallelism = line.getInteger("parallelism");
        this.maxParallelism = line.getInteger("maxParallelism");
        this.bufferTimeoutMillis = line.getLong("bufferTimeoutMillis");
        this.outOfOrdernessMillis = line.getLong("outOfOrdernessMillis");
        this.idleTimeoutMillis = line.getLong("idleTimeoutMillis");
        // FLINK Sink options.
        this.forceUsePrintSink = line.getBoolean("forceUsePrintSink");
        // FLINK ControllerLog options.
        this.jobName = line.get("jobName");
        return this;
    }

    /**
     * Configuring custom FLINK environment details.
     * 
     * @return
     */
    protected void customProps(Map<String, String> props) {
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
    protected abstract DataStream<?> customStream(DataStreamSource<RengineEvent> dataStreamSource);

    /**
     * Handling job execution result.
     * 
     * @param result
     */
    protected void handleJobExecutionResult(JobExecutionResult result) {
    }

    @Override
    public void run() {
        notNull(line, errmsg -> new IllegalStateException(errmsg),
                "Parse arguments are not initialized, must call #parse() before");

        // Custom details.
        this.props = System.getProperties()
                .entrySet()
                .stream()
                .filter(e -> startsWith(valueOf(e.getKey()), "--"))
                .collect(toMap(e -> valueOf(e.getKey()), e -> valueOf(e.getValue())));
        customProps(props);

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(runtimeMode);
        env.setRestartStrategy(fixedDelayRestart(restartAttempts, Time.of(restartDelaySeconds, TimeUnit.SECONDS)));
        final CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        if (!isBlank(checkpointDir)) {
            //@formatter:off
            //env.setStateBackend(new FsStateBackend(new Path(checkpointDir).toUri(), flinkCheckpointSizeThreshold));
            //@formatter:on
            env.setStateBackend(new HashMapStateBackend());
            checkpointConfig.setCheckpointStorage(checkpointDir);
        }
        // see:https://github.com/apache/flink/blob/release-1.14.4/docs/content/docs/connectors/datastream/kafka.md#consumer-offset-committing
        if (nonNull(checkpointMode)) {
            checkpointConfig.setCheckpointingMode(checkpointMode);
        }
        if (nonNull(checkpointIntervalMs)) {
            env.enableCheckpointing(checkpointIntervalMs);
        }
        if (nonNull(checkpointMinPauseBetween)) {
            // Sets the minimum time interval between two checkpoints.
            checkpointConfig.setMinPauseBetweenCheckpoints(checkpointMinPauseBetween);
        }
        if (nonNull(checkpointTimeout)) {
            checkpointConfig.setCheckpointTimeout(checkpointTimeout);
        }
        if (nonNull(checkpointMaxConcurrent)) {
            checkpointConfig.setMaxConcurrentCheckpoints(checkpointMaxConcurrent);
        }
        // When the program is closed, an extra checkpoint is triggered.
        if (nonNull(externalizedCheckpointCleanup)) {
            checkpointConfig.setExternalizedCheckpointCleanup(externalizedCheckpointCleanup);
        }

        final DataStreamSource<RengineEvent> dataStream = env.fromSource(createSource(),
                RengineEventWatermarks.newWatermarkStrategy(ofMillis(outOfOrdernessMillis), ofMillis(idleTimeoutMillis)),
                jobName.concat("Source"));

        //// @formatter:off
        //dataStream.assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<RengineEvent>(Time.seconds(5)) {
        //    @Override
        //    public long extractTimestamp(RengineEvent event) {
        //        return event.getObservedTime(); // event-time
        //    }
        //})
        //// @formatter:on

        if (parallelism > 0) {
            dataStream.setParallelism(parallelism);
        }
        if (maxParallelism > 0) {
            dataStream.setMaxParallelism(maxParallelism);
        }
        if (bufferTimeoutMillis > 0) {
            dataStream.setBufferTimeout(bufferTimeoutMillis);
        }

        final DataStream<?> customDataStream = customStream(dataStream);

        if (nonNull(forceUsePrintSink) && forceUsePrintSink) {
            customDataStream.print();
        }

        try {
            handleJobExecutionResult(env.execute(jobName));
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
    }

}
