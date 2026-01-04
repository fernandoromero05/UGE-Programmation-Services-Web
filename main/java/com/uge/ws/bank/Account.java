// src/main/java/com/uge/ws/bank/Account.java
package com.uge.ws.bank;

public class Account {
    private String id;
    private String ownerName;
    private String currency; // "EUR", "USD", ...
    private double balance;

    public Account() {}

    public Account(String id, String ownerName, String currency, double balance) {
        this.id = id;
        this.ownerName = ownerName;
        this.currency = currency;
        this.balance = balance;
    }
    // getters & setters …
}
