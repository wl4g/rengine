/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wl4g.rengine.job.ml.v2_0_0.bestpractices;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.ml.classification.logisticregression.LogisticRegression;
import org.apache.flink.ml.classification.logisticregression.LogisticRegressionModel;
import org.apache.flink.ml.linalg.DenseVector;
import org.apache.flink.ml.linalg.Vectors;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Tests {@link LogisticRegression} and {@link LogisticRegressionModel}. */
@SuppressWarnings("unchecked")
public class ElecPowerPredictTest {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private StreamExecutionEnvironment env;
    private StreamTableEnvironment tEnv;

    // @formatter:off
//    private static final List<Row> trainData = Arrays.asList(Row.of(Vectors.dense(1, 2, 3, 4), 0., 1.),
//            Row.of(Vectors.dense(2, 2, 3, 4), 0., 2.), Row.of(Vectors.dense(3, 2, 3, 4), 0., 3.),
//            Row.of(Vectors.dense(4, 2, 3, 4), 0., 4.), Row.of(Vectors.dense(5, 2, 3, 4), 0., 5.),
//            Row.of(Vectors.dense(11, 2, 3, 4), 1., 1.), Row.of(Vectors.dense(12, 2, 3, 4), 1., 2.),
//            Row.of(Vectors.dense(13, 2, 3, 4), 1., 3.), Row.of(Vectors.dense(14, 2, 3, 4), 1., 4.),
//            Row.of(Vectors.dense(15, 2, 3, 4), 1., 5.));
    // @formatter:on

    // TODO
    // @formatter:off
    private static final List<Row> trainData = Arrays.asList(Row.of(Vectors.dense(102, 22, 1), 102., 1.),
            Row.of(Vectors.dense(111, 22, 2), 111., 1.), Row.of(Vectors.dense(123, 22, 3), 123., 1.),
            Row.of(Vectors.dense(132, 24, 4), 132., 1.), Row.of(Vectors.dense(128, 25, 5), 128., 1.),
            Row.of(Vectors.dense(166, 24, 6), 166., 1.), Row.of(Vectors.dense(169, 26, 7), 169., 1.),
            Row.of(Vectors.dense(201, 25, 8), 201., 1.), Row.of(Vectors.dense(235, 27, 9), 235., 1.),
            Row.of(Vectors.dense(234, 26, 10), 234., 1.));
    // @formatter:on

    // TODO
    // @formatter:off
    private static final List<Row> featureData = Arrays.asList(Row.of(Vectors.dense(102, 22, 1), 102., 1.),
            Row.of(Vectors.dense(111, 22, 2), 111., 1.), Row.of(Vectors.dense(123, 22, 3), 123., 1.),
            Row.of(Vectors.dense(132, 24, 4), 132., 1.), Row.of(Vectors.dense(128, 25, 5), 128., 1.),
            Row.of(Vectors.dense(166, 24, 6), 166., 1.), Row.of(Vectors.dense(169, 26, 7), 169., 1.),
            Row.of(Vectors.dense(201, 25, 8), 201., 1.), Row.of(Vectors.dense(235, 27, 9), 235., 1.),
            Row.of(Vectors.dense(234, 26, 10), 234., 1.));
    // @formatter:on

    private Table trainDataTable;

    private Table featureDataTable;

    @Before
    public void before() {
        Configuration config = new Configuration();
        config.set(ExecutionCheckpointingOptions.ENABLE_CHECKPOINTS_AFTER_TASKS_FINISH, true);
        env = StreamExecutionEnvironment.getExecutionEnvironment(config);
        env.setParallelism(4);
        env.enableCheckpointing(100);
        env.setRestartStrategy(RestartStrategies.noRestart());
        tEnv = StreamTableEnvironment.create(env);

        Collections.shuffle(trainData);
        trainDataTable = tEnv.fromDataStream(env.fromCollection(trainData,
                new RowTypeInfo(new TypeInformation[] { TypeInformation.of(DenseVector.class), Types.DOUBLE, Types.DOUBLE },
                        new String[] { "features", "label", "weight" })));

        Collections.shuffle(featureData);
        featureDataTable = tEnv.fromDataStream(env.fromCollection(featureData,
                new RowTypeInfo(new TypeInformation[] { TypeInformation.of(DenseVector.class), Types.DOUBLE, Types.DOUBLE },
                        new String[] { "features", "label", "weight" })));
    }

    @Test
    public void testFitAndPredict() throws Exception {
        LogisticRegression lr = new LogisticRegression().setFeaturesCol("features").setLabelCol("label").setWeightCol("weight");
        Table output = lr.fit(trainDataTable).transform(featureDataTable)[0];

        List<Row> predResult = IteratorUtils.toList(tEnv.toDataStream(output).executeAndCollect());
        for (Row predictionRow : predResult) {
            DenseVector feature = (DenseVector) predictionRow.getField(lr.getFeaturesCol());
            double expectedResult = (double) predictionRow.getField(lr.getLabelCol());
            double predictionResult = (double) predictionRow.getField(lr.getPredictionCol());
            DenseVector rawPrediction = (DenseVector) predictionRow.getField(lr.getRawPredictionCol());
            System.out.println("feature: " + feature + ", expectedResult: " + expectedResult + ", predictionResult: "
                    + predictionResult + ", rawPrediction: " + rawPrediction);
        }
    }

}
