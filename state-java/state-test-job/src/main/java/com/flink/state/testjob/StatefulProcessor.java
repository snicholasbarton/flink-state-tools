package com.flink.state.testjob;

import org.apache.flink.api.common.state.*;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.util.Collector;

public class StatefulProcessor extends KeyedProcessFunction<String, Transaction, AccountSummary> implements CheckpointedFunction {

    // Keyed State
    private transient ValueState<Double> totalAmountState;
    private transient ValueState<Long> countState;
    private transient MapState<String, Double> maxTransactionPerDayState;
    private transient ListState<Double> recentTransactionAmountsState;

    // Operator State
    private transient ListState<Long> totalProcessedCountState;
    private long totalProcessedCount = 0;

    @Override
    public void open(Configuration parameters) throws Exception {
        // Initialize Keyed State
        ValueStateDescriptor<Double> totalAmountDesc = new ValueStateDescriptor<>("total-amount", Double.class);
        totalAmountState = getRuntimeContext().getState(totalAmountDesc);

        ValueStateDescriptor<Long> countDesc = new ValueStateDescriptor<>("transaction-count", Long.class);
        countState = getRuntimeContext().getState(countDesc);

        MapStateDescriptor<String, Double> maxTransactionPerDayDesc = new MapStateDescriptor<>("max-transaction-per-day", String.class, Double.class);
        maxTransactionPerDayState = getRuntimeContext().getMapState(maxTransactionPerDayDesc);

        ListStateDescriptor<Double> recentTransactionsDesc = new ListStateDescriptor<>("recent-transactions", Double.class);
        recentTransactionAmountsState = getRuntimeContext().getListState(recentTransactionsDesc);
    }

    @Override
    public void processElement(Transaction value, Context ctx, Collector<AccountSummary> out) throws Exception {
        // Update total amount
        Double currentTotal = totalAmountState.value();
        if (currentTotal == null) {
            currentTotal = 0.0;
        }
        currentTotal += value.getAmount();
        totalAmountState.update(currentTotal);

        // Update count
        Long currentCount = countState.value();
        if (currentCount == null) {
            currentCount = 0L;
        }
        currentCount += 1;
        countState.update(currentCount);

        // Update max transaction for "today" (simple key for demo)
        String dayKey = String.valueOf(value.getTimestamp() / (24 * 60 * 60 * 1000));
        Double currentMax = maxTransactionPerDayState.get(dayKey);
        if (currentMax == null || value.getAmount() > currentMax) {
            maxTransactionPerDayState.put(dayKey, value.getAmount());
        }

        // Keep last 5 transaction amounts
        recentTransactionAmountsState.add(value.getAmount());
        // (Simple implementation, doesn't actually trim strictly to 5 here to avoid reading full list)

        // Update Operator State local variable
        totalProcessedCount++;

        out.collect(new AccountSummary(value.getAccountId(), currentTotal, currentCount));
    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
        totalProcessedCountState.clear();
        totalProcessedCountState.add(totalProcessedCount);
    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        ListStateDescriptor<Long> descriptor = new ListStateDescriptor<>(
                "total-processed-count",
                Long.class);
        totalProcessedCountState = context.getOperatorStateStore().getListState(descriptor);

        if (context.isRestored()) {
            for (Long val : totalProcessedCountState.get()) {
                totalProcessedCount = val;
            }
        }
    }
}
