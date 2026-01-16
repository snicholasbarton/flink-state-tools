package com.flink.state.core;

import com.flink.state.core.source.LocalFileSource;
import com.flink.state.core.source.SavepointSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.state.api.SavepointReader;

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
        ObjectNode root = mapper.createObjectNode();
        ArrayNode operators = root.putArray("operators");

        ObjectNode op1 = operators.addObject();
        op1.put("uid", "hash_123");
        op1.put("name", "Source: Kafka");
        op1.put("parallelism", 4);

        ObjectNode op2 = operators.addObject();
        op2.put("uid", "hash_456");
        op2.put("name", "Window Aggregation");
        op2.put("parallelism", 4);

        ArrayNode edges = root.putArray("edges");
        ObjectNode edge = edges.addObject();
        edge.put("source", "hash_123");
        edge.put("target", "hash_456");

        return root.toString();
    }

    public String getSkewMetrics(String operatorUid) {
        ObjectNode metrics = mapper.createObjectNode();
        metrics.put("operatorUid", operatorUid);
        metrics.put("skew", 0.05);
        return metrics.toString();
    }
}
