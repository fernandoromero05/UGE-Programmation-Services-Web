package com.uge.ws.bank;

public class Account {
    private String id;
    private String ownerName;
    private String currency; // e.g. "EUR"
    private double balance;

    public Account() {
    }

    public Account(String id, String ownerName, String currency, double balance) {
        this.id = id;
        this.ownerName = ownerName;
        this.currency = currency;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
