package com.uge.ws.shop;

public class BasketItem {
    private long bikeId;
    private int quantity;

    public BasketItem() {
    }

    public BasketItem(long bikeId, int quantity) {
        this.bikeId = bikeId;
        this.quantity = quantity;
    }

    public long getBikeId() {
        return bikeId;
    }

    public void setBikeId(long bikeId) {
        this.bikeId = bikeId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
