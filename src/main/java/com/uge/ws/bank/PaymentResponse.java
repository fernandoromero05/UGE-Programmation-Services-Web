// src/main/java/com/uge/ws/bank/PaymentResponse.java
package com.uge.ws.bank;

public class PaymentResponse {
    private boolean approved;
    private String message;

    public PaymentResponse() {}

    public PaymentResponse(boolean approved, String message) {
        this.approved = approved;
        this.message = message;
    }
    // getters & setters …
}
