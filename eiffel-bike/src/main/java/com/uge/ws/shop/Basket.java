package com.uge.ws.shop;

import java.util.ArrayList;
import java.util.List;

public class Basket {
    private long id;
    private long customerId;
    private List<BasketItem> items = new ArrayList<>();
    private boolean checkedOut;

    public Basket() {
    }

    public Basket(long id, long customerId) {
        this.id = id;
        this.customerId = customerId;
        this.checkedOut = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public List<BasketItem> getItems() {
        return items;
    }

    public void setItems(List<BasketItem> items) {
        this.items = items;
    }

    public boolean isCheckedOut() {
        return checkedOut;
    }

    public void setCheckedOut(boolean checkedOut) {
        this.checkedOut = checkedOut;
    }
}
