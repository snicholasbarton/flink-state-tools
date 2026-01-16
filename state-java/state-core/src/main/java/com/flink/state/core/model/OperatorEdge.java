package com.flink.state.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperatorEdge {
    private String source;
    private String target;
    private String shipStrategy;

    public OperatorEdge() {}

    public OperatorEdge(String source, String target) {
        this.source = source;
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getShipStrategy() {
        return shipStrategy;
    }

    public void setShipStrategy(String shipStrategy) {
        this.shipStrategy = shipStrategy;
    }
}
