package com.uge.ws.corp;

import java.time.LocalDateTime;

public class WaitingListEntry implements Comparable<WaitingListEntry> {
    private long id;
    private long bikeId;
    private long userId;
    private LocalDateTime requestTime;

    public WaitingListEntry() {
    }

    public WaitingListEntry(long id, long bikeId, long userId) {
        this.id = id;
        this.bikeId = bikeId;
        this.userId = userId;
        this.requestTime = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public long getBikeId() {
        return bikeId;
    }

    public long getUserId() {
        return userId;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    @Override
    public int compareTo(WaitingListEntry o) {
        return this.requestTime.compareTo(o.requestTime);
    }
}
