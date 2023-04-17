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
import static com.wl4g.infra.common.lang.Exceptions.getRootCausesString;
import static com.wl4g.infra.common.runtime.JvmRuntimeTool.isJvmInDebugging;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.time.Duration.ofMillis;
import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.flink.api.common.restartstrategy.RestartStrategies.fixedDelayRestart;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.ParseException;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.api.connector.source.SourceSplit;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import com.wl4g.infra.common.cli.CommandLineTool;
import com.wl4g.infra.common.cli.CommandLineTool.CommandLineFacade;
import com.wl4g.rengine.common.event.RengineEvent;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link AbstractFlinkStreamingBase}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @date 2022-06-07 v3.0.0
 * @since v1.0.0
 */
@Getter
@Slf4j
public abstract class AbstractFlinkStreamingBase implements Runnable {

    // Flink source MQ (kafka/pulsar/rabbitmq/...) options.
    private String brokers;
    private String eventTopic;
    private String groupId;
    private Long fromOffsetTime;
    private String deserializerClass;
    private String keyByExprPath;

    // FLINK basic options.
    private RuntimeExecutionMode runtimeMode;
    private Integer restartAttempts;
    private Integer restartDelaySeconds;
    private String pipelineJars;

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
                .option("T", "eventTopic", "rengine_event", "Topic pattern for consuming events from MQ.")
                .mustOption("G", "groupId", "Flink source consumer group id.")
                .option("O", "fromOffsetTime", "-1",
                        "Start consumption from the first record with a timestamp greater than or equal to a certain timestamp. if <=0, it will not be setup and keep the default behavior.")
                .option("D", "deserializerClass", "com.wl4g.rengine.job.kafka.schema.RengineEventKafkaDeserializationSchema",
                        "Deserializer class for Flink-streaming to consuming from MQ.")
                .option("K", "keyByExprPath", ".source.principals[0]",
                        "The jq expression to extract the grouping key, it extraction from the rengine event object.")
                // FLINK basic options.
                .option("R", "runtimeMode", RuntimeExecutionMode.STREAMING.name(),
                        "Set the job execution mode. default is: STREAMING")
                .longOption("restartAttempts", "3", "Set the maximum number of failed restart attempts. default is: 3")
                .longOption("restartDelaySeconds", "15",
                        "Set the maximum number of failed interval between each restart. default is: 15")
                .longOption("pipelineJars", null,
                        "A semicolon-separated list of the jars to package with the job jars to be sent to the cluster. These have to be valid paths. see:https://nightlies.apache.org/flink/flink-docs-release-1.14/docs/deployment/config/#pipeline-jars")
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
        this.line = builder.width(170).helpIfEmpty(args, true).build(args);
        // KAFKA options.
        this.brokers = line.get("brokers");
        this.eventTopic = line.get("eventTopic");
        this.groupId = line.get("groupId");
        this.fromOffsetTime = line.getLong("fromOffsetTime");
        this.deserializerClass = line.get("deserializerClass");
        this.keyByExprPath = line.get("keyByExprPath");
        // FLINK Basic options.
        this.runtimeMode = line.getEnum("runtimeMode", RuntimeExecutionMode.class);
        this.restartAttempts = line.getInteger("restartAttempts");
        this.restartDelaySeconds = line.getInteger("restartDelaySeconds");
        this.pipelineJars = line.get("pipelineJars");
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
    protected DataStream<?> customStream(DataStream<RengineEvent> dataStreamSource) {
        final String keyByExpr = keyByExprPath;
        return dataStreamSource.keyBy(event -> {
            final String keyBy = event.atAsText(keyByExpr);
            return isBlank(keyBy) ? event.getType() : keyBy;
        });
    }

    /**
     * Create FLINK sink.
     * 
     * @return
     */
    protected abstract Serializable createSink();

    /**
     * Handling job execution result.
     * 
     * @param result
     */
    protected void handleJobExecutionResult(JobExecutionResult result) {
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
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

        final Configuration config = new Configuration();
        // see:https://nightlies.apache.org/flink/flink-docs-master/docs/dev/python/dependency_management/#jar-dependencies
        // see:https://nightlies.apache.org/flink/flink-docs-release-1.14/docs/deployment/config/#pipeline-jars
        // eg:pipeline.jars=file:///my/jar/path/connector.jar;file:///my/jar/path/udf.jar
        if (!isBlank(pipelineJars)) {
            config.setString("pipeline.jars", pipelineJars);
        }

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);
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

        try {
            final DataStream<?> customDataStream = customStream(dataStream);

            if (nonNull(forceUsePrintSink) && forceUsePrintSink) {
                customDataStream.print();
            } else {
                final String sinkName = getClass().getSimpleName().concat("Sink");
                final Serializable sink = createSink();
                if (sink instanceof SinkFunction) {
                    customDataStream.addSink((SinkFunction) sink).name(sinkName).setParallelism(parallelism);
                } else if (sink instanceof Sink) {
                    customDataStream.sinkTo((Sink) sink).name(sinkName).setParallelism(parallelism);
                } else {
                    throw new UnsupportedOperationException(format("No support sink type of %s", sink));
                }
            }

            log.info("Starting for : {} ...", jobName);
            handleJobExecutionResult(env.execute(jobName));
            log.info("Started for : {}", jobName);

        } catch (Throwable ex) {
            final String errmsg = format("Failed to execute streaming job. - %s", getRootCausesString(ex));
            log.error(errmsg);
            System.err.println(format("%s - %s - %s", new Date(), getClass().getSimpleName(), errmsg));
            throw new IllegalStateException(ex);
        }
    }

}
