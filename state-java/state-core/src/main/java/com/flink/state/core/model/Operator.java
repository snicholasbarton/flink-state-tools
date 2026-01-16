package com.flink.state.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Operator {
    private String uid;
    private String name;
    private int parallelism;
    private int maxParallelism;
    private String backendType;
    private ChainingInfo chaining;
    private List<SubtaskState> subtasks = new ArrayList<>();
    private List<StateDetail> stateDetails = new ArrayList<>();

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public int getMaxParallelism() {
        return maxParallelism;
    }

    public void setMaxParallelism(int maxParallelism) {
        this.maxParallelism = maxParallelism;
    }

    public String getBackendType() {
        return backendType;
    }

    public void setBackendType(String backendType) {
        this.backendType = backendType;
    }

    public ChainingInfo getChaining() {
        return chaining;
    }

    public void setChaining(ChainingInfo chaining) {
        this.chaining = chaining;
    }

    public List<SubtaskState> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<SubtaskState> subtasks) {
        this.subtasks = subtasks;
    }

    public List<StateDetail> getStateDetails() {
        return stateDetails;
    }

    public void setStateDetails(List<StateDetail> stateDetails) {
        this.stateDetails = stateDetails;
    }
}
