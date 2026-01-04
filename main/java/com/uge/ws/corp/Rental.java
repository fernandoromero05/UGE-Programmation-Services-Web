// src/main/java/com/uge/ws/corp/Rental.java
package com.uge.ws.corp;

import java.time.LocalDateTime;

public class Rental {
    private long id;
    private long bikeId;
    private long userId;
    private LocalDateTime start;
    private LocalDateTime end;
    private RentalStatus status;

    public Rental() {}

    public Rental(long id, long bikeId, long userId) {
        this.id = id;
        this.bikeId = bikeId;
        this.userId = userId;
        this.start = LocalDateTime.now();
        this.status = RentalStatus.ACTIVE;
    }
    // getters & setters …
}
