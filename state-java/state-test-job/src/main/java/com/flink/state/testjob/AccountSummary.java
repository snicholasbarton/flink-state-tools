package com.flink.state.testjob;

public class AccountSummary {
    private String accountId;
    private double totalAmount;
    private long transactionCount;

    public AccountSummary() {}

    public AccountSummary(String accountId, double totalAmount, long transactionCount) {
        this.accountId = accountId;
        this.totalAmount = totalAmount;
        this.transactionCount = transactionCount;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(long transactionCount) {
        this.transactionCount = transactionCount;
    }
}
