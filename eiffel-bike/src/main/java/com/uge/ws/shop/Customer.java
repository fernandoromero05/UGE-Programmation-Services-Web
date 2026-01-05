package com.uge.ws.shop;

public class Customer {
    private long id;
    private String name;
    private String email;
    private String bankAccountId;

    public Customer() {
    }

    public Customer(long id, String name, String email, String bankAccountId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.bankAccountId = bankAccountId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(String bankAccountId) {
        this.bankAccountId = bankAccountId;
    }
}
