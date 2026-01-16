package com.flink.state.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Topology {
    private String jobId;
    private List<Operator> operators = new ArrayList<>();
    private List<OperatorEdge> edges = new ArrayList<>();

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public List<Operator> getOperators() {
        return operators;
    }

    public void setOperators(List<Operator> operators) {
        this.operators = operators;
    }

    public List<OperatorEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<OperatorEdge> edges) {
        this.edges = edges;
    }
}
