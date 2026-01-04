// src/main/java/com/uge/ws/common/Bike.java
package com.uge.ws.common;

import java.util.ArrayList;
import java.util.List;

public class Bike {
    private long id;
    private String model;
    private String description;
    private BikeStatus status;
    private int totalRentals;
    private double basePriceEUR;

    private List<String> notes = new ArrayList<>();

    public Bike() {}

    public Bike(long id, String model, String description, double basePriceEUR) {
        this.id = id;
        this.model = model;
        this.description = description;
        this.basePriceEUR = basePriceEUR;
        this.status = BikeStatus.AVAILABLE;
        this.totalRentals = 0;
    }

    // getters & setters …
    // (Generate with IDE to save time)
}
