package com.flink.state.core.source;

import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;

public class LocalFileSource implements SavepointSource {

    @Override
    public SavepointReader load(StreamExecutionEnvironment env, String path) throws Exception {
        return SavepointReader.read(env, path, new HashMapStateBackend());
    }
}
