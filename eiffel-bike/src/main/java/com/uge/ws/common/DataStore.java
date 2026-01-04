// src/main/java/com/uge/ws/common/DataStore.java
package com.uge.ws.common;

import com.uge.ws.bank.Account;
import com.uge.ws.shop.Basket;
import com.uge.ws.shop.Customer;
import com.uge.ws.corp.Rental;
import com.uge.ws.corp.WaitingListEntry;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class DataStore {

    private static final DataStore INSTANCE = new DataStore();

    public static DataStore get() {
        return INSTANCE;
    }

    // Bikes and internal users
    private final Map<Long, Bike> bikes = new HashMap<>();
    private final Map<Long, InternalUser> internalUsers = new HashMap<>();

    // Rentals & waiting lists
    private final Map<Long, Rental> rentals = new HashMap<>();
    private final Map<Long, PriorityQueue<WaitingListEntry>> waitingLists = new HashMap<>();

    // Customers, baskets, bank accounts
    private final Map<Long, Customer> customers = new HashMap<>();
    private final Map<Long, Basket> baskets = new HashMap<>();
    private final Map<String, Account> accounts = new HashMap<>();

    // ID generators
    private final AtomicLong bikeSeq = new AtomicLong(1);
    private final AtomicLong userSeq = new AtomicLong(1);
    private final AtomicLong rentalSeq = new AtomicLong(1);
    private final AtomicLong waitingSeq = new AtomicLong(1);
    private final AtomicLong customerSeq = new AtomicLong(1);
    private final AtomicLong basketSeq = new AtomicLong(1);

    private DataStore() {
        seedData();
    }

    private void seedData() {
        // Seed bikes
        addBike(new Bike(bikeSeq.getAndIncrement(), "CityBike 100", "Urban city bike", 200));
        addBike(new Bike(bikeSeq.getAndIncrement(), "Mountain Pro", "MTB for trails", 400));
        addBike(new Bike(bikeSeq.getAndIncrement(), "E-Bike Campus", "Electric bike", 800));

        // Seed internal users
        addInternalUser(new InternalUser(userSeq.getAndIncrement(), "Alice Student",
                "alice@student.uge.fr", UserType.STUDENT));
        addInternalUser(new InternalUser(userSeq.getAndIncrement(), "Bob Employee",
                "bob@uge.fr", UserType.EMPLOYEE));

        // Seed customers + bank accounts
        Customer c1 = new Customer(customerSeq.getAndIncrement(), "Charlie Customer",
                "charlie@example.com", "ACC-001");
        customers.put(c1.getId(), c1);
        accounts.put("ACC-001", new Account("ACC-001", "Charlie Customer", "EUR", 2000.0));

        Customer c2 = new Customer(customerSeq.getAndIncrement(), "Dana Customer",
                "dana@example.com", "ACC-002");
        customers.put(c2.getId(), c2);
        accounts.put("ACC-002", new Account("ACC-002", "Dana Customer", "USD", 1500.0));
    }

    // Bikes
    public synchronized Bike addBike(Bike bike) {
        bikes.put(bike.getId(), bike);
        return bike;
    }
    public Map<Long, Bike> getBikes() { return bikes; }


    public void addBikeNote(long bikeId, String note) {
        Bike bike = bikes.get(bikeId);
        if (bike != null && note != null && !note.isBlank()) {
            String withTimestamp = "[" + java.time.LocalDateTime.now() + "] " + note;
            bike.addNote(withTimestamp);
        }
    }

    public List<String> getBikeNotes(long bikeId) {
        Bike bike = bikes.get(bikeId);
        return (bike != null) ? bike.getNotes() : List.of();
    }

    // Internal users
    public synchronized InternalUser addInternalUser(InternalUser u) {
        internalUsers.put(u.getId(), u);
        return u;
    }
    public Map<Long, InternalUser> getInternalUsers() { return internalUsers; }

    // Rentals
    public synchronized Rental newRental(long bikeId, long userId) {
        Rental r = new Rental(rentalSeq.getAndIncrement(), bikeId, userId);
        rentals.put(r.getId(), r);
        return r;
    }
    public Map<Long, Rental> getRentals() { return rentals; }

    // Waiting lists
    public PriorityQueue<WaitingListEntry> getWaitingList(long bikeId) {
        return waitingLists.computeIfAbsent(bikeId, id -> new PriorityQueue<>());
    }
    public synchronized WaitingListEntry addToWaitingList(long bikeId, long userId) {
        WaitingListEntry w = new WaitingListEntry(waitingSeq.getAndIncrement(), bikeId, userId);
        getWaitingList(bikeId).add(w);
        return w;
    }

    // Customers & baskets
    public Map<Long, Customer> getCustomers() { return customers; }
    public Map<Long, Basket> getBaskets() { return baskets; }
    public long nextBasketId() { return basketSeq.getAndIncrement(); }

    // Bank accounts
    public Map<String, Account> getAccounts() { return accounts; }

    // Expose sequences if needed
    public long nextBikeId() { return bikeSeq.getAndIncrement(); }
    public long nextUserId() { return userSeq.getAndIncrement(); }
    public long nextCustomerId() { return customerSeq.getAndIncrement(); }
}
