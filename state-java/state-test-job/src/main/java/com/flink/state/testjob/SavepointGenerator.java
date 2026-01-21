package com.flink.state.testjob;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.core.execution.SavepointFormatType;
import org.apache.flink.runtime.minicluster.MiniCluster;
import org.apache.flink.runtime.minicluster.MiniClusterConfiguration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.connector.datagen.source.GeneratorFunction;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.functions.sink.v2.DiscardingSink;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.api.common.typeinfo.Types;

import java.io.File;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class SavepointGenerator {

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();
        config.set(RestOptions.BIND_PORT, "8081");

        MiniClusterConfiguration clusterConfig = new MiniClusterConfiguration.Builder()
                .setConfiguration(config)
                .setNumSlotsPerTaskManager(2)
                .setNumTaskManagers(1)
                .build();

        try (MiniCluster miniCluster = new MiniCluster(clusterConfig)) {
            miniCluster.start();

            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(2);
            env.enableCheckpointing(1000); // Enable checkpointing to ensure state backends are active

            DataGeneratorSource<Transaction> source = new DataGeneratorSource<>(
                    new TransactionGenerator(),
                    Long.MAX_VALUE,
                    RateLimiterStrategy.noOp(),
                    Types.POJO(Transaction.class)
            );

            env.fromSource(source, WatermarkStrategy.<Transaction>noWatermarks(), "Transaction Source")
                    .uid("source-id")
                    .keyBy(Transaction::getAccountId)
                    .process(new StatefulProcessor())
                    .uid("process-id")
                    .sinkTo(new DiscardingSink<>())
                    .uid("sink-id");

            JobGraph jobGraph = env.getStreamGraph().getJobGraph();

            CompletableFuture<?> jobResult = miniCluster.submitJob(jobGraph);

            System.out.println("Job submitted. ID: " + jobGraph.getJobID());

            // Let it run for a bit to generate some state
            Thread.sleep(5000);

            // Trigger Savepoint
            File savepointDir = new File(System.getProperty("java.io.tmpdir"), "flink-savepoints");
            if (!savepointDir.exists()) {
                savepointDir.mkdirs();
            }

            System.out.println("Triggering savepoint to: " + savepointDir.getAbsolutePath());

            String savepointPath = miniCluster.triggerSavepoint(jobGraph.getJobID(), savepointDir.getAbsolutePath(), true, SavepointFormatType.CANONICAL).get();

            System.out.println("Savepoint created at: " + savepointPath);

            // Cancel the job (optional if we used triggerSavepoint with cancel=true, but here we used explicit trigger)
            // Actually, triggerSavepoint's 3rd arg is 'cancelJob', if true it cancels.

            // Wait for job to finish if it wasn't cancelled by savepoint (it was)
            try {
                jobResult.get();
            } catch (Exception e) {
                // Job cancellation expected
            }
        }
    }

    private static class TransactionGenerator implements GeneratorFunction<Long, Transaction> {
        private final Random javaRandom = new Random();

        @Override
        public Transaction map(Long value) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            String accountId = "account-" + (javaRandom.nextInt(10));
            double amount = javaRandom.nextDouble() * 100.0;
            return new Transaction(accountId, System.currentTimeMillis(), amount);
        }
    }
}
