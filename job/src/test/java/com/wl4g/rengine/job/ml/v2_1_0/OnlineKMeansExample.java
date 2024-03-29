package com.wl4g.rengine.job.ml.v2_1_0;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.ml.clustering.kmeans.KMeansModelData;
import org.apache.flink.ml.clustering.kmeans.OnlineKMeans;
import org.apache.flink.ml.clustering.kmeans.OnlineKMeansModel;
import org.apache.flink.ml.linalg.DenseVector;
import org.apache.flink.ml.linalg.Vectors;
import org.apache.flink.ml.linalg.typeinfo.DenseVectorTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;

/**
 * Simple program that trains an OnlineKMeans model and uses it for clustering.
 * 
 * @see https://github.com/apache/flink-ml/blob/release-2.1.0/flink-ml-examples/src/main/java/org/apache/flink/ml/examples/clustering/OnlineKMeansExample.java
 */
public class OnlineKMeansExample {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        // Generates input training and prediction data. Both are infinite
        // streams that periodically
        // sends out provided data to trigger model update and prediction.
        List<Row> trainData1 = Arrays.asList(Row.of(Vectors.dense(0.0, 0.0)), Row.of(Vectors.dense(0.0, 0.3)),
                Row.of(Vectors.dense(0.3, 0.0)), Row.of(Vectors.dense(9.0, 0.0)), Row.of(Vectors.dense(9.0, 0.6)),
                Row.of(Vectors.dense(9.6, 0.0)));

        List<Row> trainData2 = Arrays.asList(Row.of(Vectors.dense(10.0, 100.0)), Row.of(Vectors.dense(10.0, 100.3)),
                Row.of(Vectors.dense(10.3, 100.0)), Row.of(Vectors.dense(-10.0, -100.0)), Row.of(Vectors.dense(-10.0, -100.6)),
                Row.of(Vectors.dense(-10.6, -100.0)));

        List<Row> predictData = Arrays.asList(Row.of(Vectors.dense(10.0, 10.0)), Row.of(Vectors.dense(-10.0, 10.0)));

        SourceFunction<Row> trainSource = new PeriodicSourceFunction(1000, Arrays.asList(trainData1, trainData2));
        DataStream<Row> trainStream = env.addSource(trainSource, new RowTypeInfo(DenseVectorTypeInfo.INSTANCE));
        Table trainTable = tEnv.fromDataStream(trainStream).as("features");

        SourceFunction<Row> predictSource = new PeriodicSourceFunction(1000, Collections.singletonList(predictData));
        DataStream<Row> predictStream = env.addSource(predictSource, new RowTypeInfo(DenseVectorTypeInfo.INSTANCE));
        Table predictTable = tEnv.fromDataStream(predictStream).as("features");

        // Creates an online K-means object and initializes its parameters and
        // initial model data.
        OnlineKMeans onlineKMeans = new OnlineKMeans().setFeaturesCol("features")
                .setPredictionCol("prediction")
                .setGlobalBatchSize(6)
                .setInitialModelData(KMeansModelData.generateRandomModelData(tEnv, 2, 2, 0.0, 0));

        // Trains the online K-means Model.
        OnlineKMeansModel onlineModel = onlineKMeans.fit(trainTable);
        onlineModel.save("/tmp/flink-ml-test-model.onlinekmeans");

        // Uses the online K-means Model for predictions.
        Table outputTable = onlineModel.transform(predictTable)[0];

        // Extracts and displays the results. As training data stream
        // continuously triggers the
        // update of the internal k-means model data, clustering results of the
        // same predict dataset
        // would change over time.
        for (CloseableIterator<Row> it = outputTable.execute().collect(); it.hasNext();) {
            Row row1 = it.next();
            DenseVector features1 = (DenseVector) row1.getField(onlineKMeans.getFeaturesCol());
            Integer clusterId1 = (Integer) row1.getField(onlineKMeans.getPredictionCol());
            Row row2 = it.next();
            DenseVector features2 = (DenseVector) row2.getField(onlineKMeans.getFeaturesCol());
            Integer clusterId2 = (Integer) row2.getField(onlineKMeans.getPredictionCol());
            if (Objects.equals(clusterId1, clusterId2)) {
                System.out.printf("%s and %s are now in the same cluster.\n", features1, features2);
            } else {
                System.out.printf("%s and %s are now in different clusters.\n", features1, features2);
            }
        }
    }

}
