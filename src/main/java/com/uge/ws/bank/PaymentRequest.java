// src/main/java/com/uge/ws/bank/PaymentRequest.java
package com.uge.ws.bank;

public class PaymentRequest {
    private String accountId;
    private double amount;
    private String currency;

    public PaymentRequest() {}

    public PaymentRequest(String accountId, double amount, String currency) {
        this.accountId = accountId;
        this.amount = amount;
        this.currency = currency;
    }
    // getters & setters …
}
