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

    public Bike() {
    }

    public Bike(long id, String model, String description, double basePriceEUR) {
        this.id = id;
        this.model = model;
        this.description = description;
        this.basePriceEUR = basePriceEUR;
        this.status = BikeStatus.AVAILABLE;
        this.totalRentals = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BikeStatus getStatus() {
        return status;
    }

    public void setStatus(BikeStatus status) {
        this.status = status;
    }

    public int getTotalRentals() {
        return totalRentals;
    }

    public void setTotalRentals(int totalRentals) {
        this.totalRentals = totalRentals;
    }

    public double getBasePriceEUR() {
        return basePriceEUR;
    }

    public void setBasePriceEUR(double basePriceEUR) {
        this.basePriceEUR = basePriceEUR;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public void addNote(String note) {
        notes.add(note);
    }

    @Override
    public String toString() {
        return "Bike{" +
                "id=" + id +
                ", model='" + model + '\'' +
                ", status=" + status +
                ", rentals=" + totalRentals +
                ", price=" + basePriceEUR +
                '}';
    }
}
