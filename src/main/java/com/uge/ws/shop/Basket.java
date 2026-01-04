// src/main/java/com/uge/ws/shop/Basket.java
package com.uge.ws.shop;

import java.util.ArrayList;
import java.util.List;

public class Basket {
    private long id;
    private long customerId;
    private List<BasketItem> items = new ArrayList<>();
    private boolean checkedOut;

    public Basket() {}

    public Basket(long id, long customerId) {
        this.id = id;
        this.customerId = customerId;
    }
    // getters & setters …
}
