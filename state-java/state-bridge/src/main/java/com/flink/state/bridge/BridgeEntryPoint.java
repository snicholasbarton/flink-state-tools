package com.flink.state.bridge;

import com.flink.state.core.StateAnalyzer;
import py4j.GatewayServer;

public class BridgeEntryPoint {

    public static void main(String[] args) {
        BridgeEntryPoint entryPoint = new BridgeEntryPoint();

        GatewayServer server = new GatewayServer(entryPoint);
        server.start();

        System.out.println("Gateway Server Started");
    }

    public StateAnalyzer createStateAnalyzer() {
        return new StateAnalyzer();
    }
}
