package com.wl4g.rengine.job.ml.v2_1_0;

import org.apache.flink.ml.linalg.DenseVector;
import org.apache.flink.ml.linalg.Vectors;
import org.apache.flink.ml.regression.linearregression.LinearRegression;
import org.apache.flink.ml.regression.linearregression.LinearRegressionModel;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;

/**
 * Simple program that trains a LinearRegression model and uses it for
 * regression.
 * 
 * @see https://github.com/apache/flink-ml/blob/release-2.1.0/flink-ml-examples/src/main/java/org/apache/flink/ml/examples/regression/LinearRegressionExample.java
 */
public class LinearRegressionExample {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        // Generates input data.
        DataStream<Row> inputStream = env.fromElements(Row.of(Vectors.dense(2, 1), 4.0, 1.0),
                Row.of(Vectors.dense(3, 2), 7.0, 1.0), Row.of(Vectors.dense(4, 3), 10.0, 1.0),
                Row.of(Vectors.dense(2, 4), 10.0, 1.0), Row.of(Vectors.dense(2, 2), 6.0, 1.0),
                Row.of(Vectors.dense(4, 3), 10.0, 1.0), Row.of(Vectors.dense(1, 2), 5.0, 1.0),
                Row.of(Vectors.dense(5, 3), 11.0, 1.0));
        Table inputTable = tEnv.fromDataStream(inputStream).as("features", "label", "weight");

        // Creates a LinearRegression object and initializes its parameters.
        LinearRegression lr = new LinearRegression().setWeightCol("weight");

        // Trains the LinearRegression Model.
        LinearRegressionModel lrModel = lr.fit(inputTable);
        lrModel.save("/tmp/flink-ml-test-model.linearregression");

        // Uses the LinearRegression Model for predictions.
        Table outputTable = lrModel.transform(inputTable)[0];

        // Extracts and displays the results.
        for (CloseableIterator<Row> it = outputTable.execute().collect(); it.hasNext();) {
            Row row = it.next();
            DenseVector features = (DenseVector) row.getField(lr.getFeaturesCol());
            double expectedResult = (Double) row.getField(lr.getLabelCol());
            double predictionResult = (Double) row.getField(lr.getPredictionCol());
            System.out.printf("Features: %s \tExpected Result: %s \tPrediction Result: %s\n", features, expectedResult,
                    predictionResult);
        }
    }

}