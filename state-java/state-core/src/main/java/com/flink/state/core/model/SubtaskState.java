package com.flink.state.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubtaskState {
    private int index;
    private long stateSize;
    private String host;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getStateSize() {
        return stateSize;
    }

    public void setStateSize(long stateSize) {
        this.stateSize = stateSize;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
