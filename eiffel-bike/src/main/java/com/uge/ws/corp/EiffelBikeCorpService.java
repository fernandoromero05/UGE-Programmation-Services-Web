package com.uge.ws.corp;

import com.uge.ws.common.Bike;
import com.uge.ws.common.BikeStatus;
import com.uge.ws.common.DataStore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class EiffelBikeCorpService {

    private final DataStore store = DataStore.get();

    // --------- Queries --------- //

    public List<Bike> listAllBikes() {
        return new ArrayList<>(store.getBikes().values());
    }

    public Bike getBike(long bikeId) {
        return store.getBikes().get(bikeId);
    }

    public List<Rental> getRentalsByUser(long userId) {
        List<Rental> result = new ArrayList<>();
        for (Rental r : store.getRentals().values()) {
            if (r.getUserId() == userId) {
                result.add(r);
            }
        }
        return result;
    }

    // NEW: expose notes for a bike
    public List<String> getBikeNotes(long bikeId) {
        return store.getBikeNotes(bikeId);
    }

    // --------- Commands: rent / return --------- //

    /**
     * Try to rent a bike for a user.
     *  - If bike AVAILABLE -> create Rental, mark bike RENTED.
     *  - Else -> user goes on waiting list (FIFO).
     */
    public RentResult rentBike(long bikeId, long userId) {
        Map<Long, Bike> bikes = store.getBikes();
        Bike bike = bikes.get(bikeId);
        if (bike == null) {
            return RentResult.error("Bike not found");
        }

        if (bike.getStatus() == BikeStatus.AVAILABLE) {
            Rental rental = store.newRental(bikeId, userId);
            bike.setStatus(BikeStatus.RENTED);
            bike.setTotalRentals(bike.getTotalRentals() + 1);
            return RentResult.rented(rental);
        }

        // already rented: put on waiting list
        store.addToWaitingList(bikeId, userId);
        int position = store.getWaitingList(bikeId).size();
        return RentResult.waitingList(position);
    }

    /**
     * Return a bike:
     *  - close current rental
     *  - add note on bike
     *  - if waiting list non-empty -> rent automatically to next user
     *  - else -> bike becomes AVAILABLE
     */
    public ReturnResult returnBike(long rentalId, String note) {
        Rental rental = store.getRentals().get(rentalId);
        if (rental == null) {
            return ReturnResult.error("Rental not found");
        }
        if (rental.getStatus() != RentalStatus.ACTIVE) {
            return ReturnResult.error("Rental is not active");
        }

        Bike bike = store.getBikes().get(rental.getBikeId());
        if (bike == null) {
            return ReturnResult.error("Associated bike not found");
        }

        // close rental
        rental.setStatus(RentalStatus.COMPLETED);
        rental.setEnd(LocalDateTime.now());

        // attach note
        if (note != null && !note.isBlank()) {
            store.addBikeNote(rental.getBikeId(), note);
        }

        PriorityQueue<WaitingListEntry> queue = store.getWaitingList(bike.getId());
        WaitingListEntry next = queue.poll();

        if (next != null) {
            Rental newRental = store.newRental(bike.getId(), next.getUserId());
            bike.setStatus(BikeStatus.RENTED);
            bike.setTotalRentals(bike.getTotalRentals() + 1);
            return ReturnResult.rentedToNext(newRental, next.getUserId());
        } else {
            bike.setStatus(BikeStatus.AVAILABLE);
            return ReturnResult.available();
        }
    }

    // --------- DTOs for REST responses --------- //

    public static class RentResult {
        public String status;         // RENTED, WAITING_LIST, ERROR
        public String message;
        public Long rentalId;
        public Integer waitingPosition;

        public static RentResult rented(Rental r) {
            RentResult res = new RentResult();
            res.status = "RENTED";
            res.rentalId = r.getId();
            res.message = "Bike rented successfully";
            return res;
        }

        public static RentResult waitingList(int position) {
            RentResult res = new RentResult();
            res.status = "WAITING_LIST";
            res.waitingPosition = position;
            res.message = "Bike currently rented; added to waiting list";
            return res;
        }

        public static RentResult error(String msg) {
            RentResult res = new RentResult();
            res.status = "ERROR";
            res.message = msg;
            return res;
        }
    }

    public static class ReturnResult {
        public String status;    // AVAILABLE, RENTED_TO_NEXT, ERROR
        public String message;
        public Long newRentalId;
        public Long nextUserId;

        public static ReturnResult available() {
            ReturnResult res = new ReturnResult();
            res.status = "AVAILABLE";
            res.message = "Bike returned and now available";
            return res;
        }

        public static ReturnResult rentedToNext(Rental r, long nextUserId) {
            ReturnResult res = new ReturnResult();
            res.status = "RENTED_TO_NEXT";
            res.newRentalId = r.getId();
            res.nextUserId = nextUserId;
            res.message = "Bike returned and rented to next user in waiting list";
            return res;
        }

        public static ReturnResult error(String msg) {
            ReturnResult res = new ReturnResult();
            res.status = "ERROR";
            res.message = msg;
            return res;
        }
    }
}