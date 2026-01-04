package com.uge.ws.bank;

public class PaymentRequest {
    private String accountId;
    private double amount;
    private String currency;

    public PaymentRequest() {
    }

    public PaymentRequest(String accountId, double amount, String currency) {
        this.accountId = accountId;
        this.amount = amount;
        this.currency = currency;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
