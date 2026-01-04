// src/main/java/com/uge/ws/shop/BasketItem.java
package com.uge.ws.shop;

public class BasketItem {
    private long bikeId;
    private int quantity;

    public BasketItem() {}

    public BasketItem(long bikeId, int quantity) {
        this.bikeId = bikeId;
        this.quantity = quantity;
    }
    // getters & setters …
}
