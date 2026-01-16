package com.flink.state.testjob;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.core.execution.SavepointFormatType;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.runtime.minicluster.MiniCluster;
import org.apache.flink.runtime.minicluster.MiniClusterConfiguration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;

import java.io.File;
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

            env.addSource(new TransactionSource())
                    .uid("source-id")
                    .keyBy(Transaction::getAccountId)
                    .process(new StatefulProcessor())
                    .uid("process-id")
                    .addSink(new DiscardingSink<>())
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
}
