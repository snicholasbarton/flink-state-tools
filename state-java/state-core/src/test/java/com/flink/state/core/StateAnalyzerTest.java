package com.flink.state.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StateAnalyzerTest {

    @Test
    public void testGetTopologyJsonSchema() throws Exception {
        StateAnalyzer analyzer = new StateAnalyzer();
        String json = analyzer.getTopologyJson();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        assertTrue(root.has("operators"));
        assertTrue(root.has("edges"));

        JsonNode operators = root.get("operators");
        assertTrue(operators.isArray());
        assertEquals(2, operators.size());

        JsonNode op1 = operators.get(0);
        assertTrue(op1.has("uid"));
        assertTrue(op1.has("name"));
        assertTrue(op1.has("parallelism"));
        assertEquals("hash_123", op1.get("uid").asText());

        JsonNode edges = root.get("edges");
        assertTrue(edges.isArray());
        assertEquals(1, edges.size());
        assertEquals("hash_123", edges.get(0).get("source").asText());
        assertEquals("hash_456", edges.get(0).get("target").asText());
    }
}
