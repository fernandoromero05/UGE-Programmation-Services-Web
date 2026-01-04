package com.uge.ws.corp;

import java.time.LocalDateTime;

public class Rental {
    private long id;
    private long bikeId;
    private long userId;
    private LocalDateTime start;
    private LocalDateTime end;
    private RentalStatus status;

    public Rental() {
    }

    public Rental(long id, long bikeId, long userId) {
        this.id = id;
        this.bikeId = bikeId;
        this.userId = userId;
        this.start = LocalDateTime.now();
        this.status = RentalStatus.ACTIVE;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBikeId() {
        return bikeId;
    }

    public void setBikeId(long bikeId) {
        this.bikeId = bikeId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }
}
