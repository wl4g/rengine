package com.wl4g.rengine.job.ml.v2_1_0;

import org.apache.flink.ml.classification.logisticregression.LogisticRegression;
import org.apache.flink.ml.classification.logisticregression.LogisticRegressionModel;
import org.apache.flink.ml.linalg.DenseVector;
import org.apache.flink.ml.linalg.Vectors;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;

/**
 * Simple program that trains a LogisticRegression model and uses it for
 * classification.
 */
public class LogisticRegressionExample {

    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        // Generates input data.
        DataStream<Row> inputStream = env.fromElements(Row.of(Vectors.dense(1, 2, 3, 4), 0., 1.),
                Row.of(Vectors.dense(2, 2, 3, 4), 0., 2.), Row.of(Vectors.dense(3, 2, 3, 4), 0., 3.),
                Row.of(Vectors.dense(4, 2, 3, 4), 0., 4.), Row.of(Vectors.dense(5, 2, 3, 4), 0., 5.),
                Row.of(Vectors.dense(11, 2, 3, 4), 1., 1.), Row.of(Vectors.dense(12, 2, 3, 4), 1., 2.),
                Row.of(Vectors.dense(13, 2, 3, 4), 1., 3.), Row.of(Vectors.dense(14, 2, 3, 4), 1., 4.),
                Row.of(Vectors.dense(15, 2, 3, 4), 1., 5.));
        Table inputTable = tEnv.fromDataStream(inputStream).as("features", "label", "weight");

        // Creates a LogisticRegression object and initializes its parameters.
        LogisticRegression lr = new LogisticRegression().setWeightCol("weight");

        // Trains the LogisticRegression Model.
        LogisticRegressionModel lrModel = lr.fit(inputTable);

        // Uses the LogisticRegression Model for predictions.
        Table outputTable = lrModel.transform(inputTable)[0];

        // Extracts and displays the results.
        for (CloseableIterator<Row> it = outputTable.execute().collect(); it.hasNext();) {
            Row row = it.next();
            DenseVector features = (DenseVector) row.getField(lr.getFeaturesCol());
            double expectedResult = (Double) row.getField(lr.getLabelCol());
            double predictionResult = (Double) row.getField(lr.getPredictionCol());
            DenseVector rawPredictionResult = (DenseVector) row.getField(lr.getRawPredictionCol());
            System.out.printf("Features: %-25s \tExpected Result: %s \tPrediction Result: %s \tRaw Prediction Result: %s\n",
                    features, expectedResult, predictionResult, rawPredictionResult);
        }
    }

}
