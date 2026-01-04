package com.uge.ws.bank;

public class PaymentResponse {
    private boolean approved;
    private String message;

    public PaymentResponse() {
    }

    public PaymentResponse(boolean approved, String message) {
        this.approved = approved;
        this.message = message;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
