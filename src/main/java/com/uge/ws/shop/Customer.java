// src/main/java/com/uge/ws/shop/Customer.java
package com.uge.ws.shop;

public class Customer {
    private long id;
    private String name;
    private String email;
    private String bankAccountId; // link to BankService account

    public Customer() {}

    public Customer(long id, String name, String email, String bankAccountId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.bankAccountId = bankAccountId;
    }
    // getters & setters …
}
