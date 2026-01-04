// src/main/java/com/uge/ws/corp/EiffelBikeCorpService.java
package com.uge.ws.corp;

import com.uge.ws.common.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EiffelBikeCorpService {

    private final DataStore store = DataStore.get();

    public List<Bike> listBikes() {
        return store.getBikes().values().stream().collect(Collectors.toList());
    }

    public Optional<Bike> getBike(long id) {
        return Optional.ofNullable(store.getBikes().get(id));
    }

    /** Try to rent bike; if not available, put user on waiting list. */
    public String rentBike(long bikeId, long userId) {
        Bike bike = store.getBikes().get(bikeId);
        if (bike == null) return "Bike not found";
        InternalUser user = store.getInternalUsers().get(userId);
        if (user == null) return "User not found";

        if (bike.getStatus() == BikeStatus.AVAILABLE) {
            bike.setStatus(BikeStatus.RENTED);
            bike.setTotalRentals(bike.getTotalRentals() + 1);
            store.newRental(bikeId, userId);
            return "RENTED";
        } else {
            store.addToWaitingList(bikeId, userId);
            return "WAITING_LIST";
        }
    }

    public String returnBike(long bikeId, String note) {
        Bike bike = store.getBikes().get(bikeId);
        if (bike == null) return "Bike not found";

        if (note != null && !note.isBlank()) {
            bike.getNotes().add(LocalDateTime.now() + ": " + note);
        }

        // mark one active rental as completed
        store.getRentals().values().stream()
                .filter(r -> r.getBikeId() == bikeId && r.getStatus() == RentalStatus.ACTIVE)
                .findFirst()
                .ifPresent(r -> {
                    r.setStatus(RentalStatus.COMPLETED);
                    r.setEnd(LocalDateTime.now());
                });

        // check waiting list
        var queue = store.getWaitingList(bikeId);
        WaitingListEntry next = queue.poll();
        if (next != null) {
            // automatically rent to next user
            store.newRental(bikeId, next.getUserId());
            bike.setStatus(BikeStatus.RENTED);
            bike.setTotalRentals(bike.getTotalRentals() + 1);
            return "RENTED_TO_NEXT_IN_QUEUE userId=" + next.getUserId();
        } else {
            bike.setStatus(BikeStatus.AVAILABLE);
            return "AVAILABLE";
        }
    }
}
