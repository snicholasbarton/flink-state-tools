package com.flink.state.testjob;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;

import java.util.Random;

public class TransactionSource implements SourceFunction<Transaction>, CheckpointedFunction {

    private volatile boolean isRunning = true;
    private transient ListState<Long> offsetState;
    private long offset = 0;
    private final Random random = new Random();

    @Override
    public void run(SourceContext<Transaction> ctx) throws Exception {
        final Object lock = ctx.getCheckpointLock();

        while (isRunning) {
            synchronized (lock) {
                String accountId = "account-" + (random.nextInt(10));
                double amount = random.nextDouble() * 100;
                Transaction transaction = new Transaction(accountId, System.currentTimeMillis(), amount);
                ctx.collect(transaction);
                offset++;
            }
            Thread.sleep(100);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
        offsetState.clear();
        offsetState.add(offset);
    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        ListStateDescriptor<Long> descriptor = new ListStateDescriptor<>(
                "source-offset",
                Long.class);
        offsetState = context.getOperatorStateStore().getListState(descriptor);

        if (context.isRestored()) {
            for (Long val : offsetState.get()) {
                offset = val;
            }
        }
    }
}
