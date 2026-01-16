package com.flink.state.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChainingInfo {
    private String taskName;
    private String headOperatorUid;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getHeadOperatorUid() {
        return headOperatorUid;
    }

    public void setHeadOperatorUid(String headOperatorUid) {
        this.headOperatorUid = headOperatorUid;
    }
}
