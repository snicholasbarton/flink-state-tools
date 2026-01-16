package com.flink.state.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flink.state.core.model.Operator;
import com.flink.state.core.model.OperatorEdge;
import com.flink.state.core.model.Topology;
import com.flink.state.core.source.LocalFileSource;
import com.flink.state.core.source.SavepointSource;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class StateAnalyzer {

    private final StreamExecutionEnvironment env;
    private SavepointReader savepoint;
    private final ObjectMapper mapper;

    public StateAnalyzer() {
        this.env = StreamExecutionEnvironment.getExecutionEnvironment();
        this.mapper = new ObjectMapper();
    }

    public void loadSavepoint(String path) throws Exception {
        SavepointSource source = new LocalFileSource();
        this.savepoint = source.load(env, path);
    }

    public String getTopologyJson() {
        Topology topology = new Topology();

        // Placeholder data logic - to be replaced with actual extraction logic
        Operator op1 = new Operator();
        op1.setUid("hash_123");
        op1.setName("Source: Kafka");
        op1.setParallelism(4);

        Operator op2 = new Operator();
        op2.setUid("hash_456");
        op2.setName("Window Aggregation");
        op2.setParallelism(4);

        topology.getOperators().add(op1);
        topology.getOperators().add(op2);

        topology.getEdges().add(new OperatorEdge("hash_123", "hash_456"));

        try {
            return mapper.writeValueAsString(topology);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize topology", e);
        }
    }

    public String getSkewMetrics(String operatorUid) {
        // In the future, this should probably retrieve the Operator object and return its metrics or subtask stats
        try {
            // Returning a JSON string as before, but potentially this could be a serialized Metrics object
            return mapper.createObjectNode()
                .put("operatorUid", operatorUid)
                .put("skew", 0.05)
                .toString();
        } catch (Exception e) {
             throw new RuntimeException(e);
        }
    }
}
