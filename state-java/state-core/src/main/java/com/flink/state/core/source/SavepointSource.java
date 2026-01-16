package com.flink.state.core.source;

import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public interface SavepointSource {
    SavepointReader load(StreamExecutionEnvironment env, String path) throws Exception;
}
