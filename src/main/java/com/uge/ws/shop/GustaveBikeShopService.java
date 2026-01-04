// src/main/java/com/uge/ws/shop/GustaveBikeShopService.java
package com.uge.ws.shop;

import com.uge.ws.bank.PaymentRequest;
import com.uge.ws.bank.PaymentResponse;
import com.uge.ws.bank.BankService;
import com.uge.ws.common.Bike;
import com.uge.ws.common.BikeStatus;
import com.uge.ws.common.DataStore;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GustaveBikeShopService {

    private final DataStore store = DataStore.get();
    private final ExchangeRateService fx = new DummyExchangeRateService();
    private final BankService bankService = new BankService();

    /** Bikes that have been rented at least once and are not yet sold. */
    public List<Bike> listSellableBikes() {
        return store.getBikes().values().stream()
                .filter(b -> b.getTotalRentals() > 0 &&
                             (b.getStatus() == BikeStatus.AVAILABLE
                              || b.getStatus() == BikeStatus.FOR_SALE))
                .peek(b -> b.setStatus(BikeStatus.FOR_SALE))
                .collect(Collectors.toList());
    }

    public Optional<Bike> getSellableBike(long id) {
        return listSellableBikes().stream()
                .filter(b -> b.getId() == id)
                .findFirst();
    }

    public Basket createBasket(long customerId) {
        long id = store.nextBasketId();
        Basket b = new Basket(id, customerId);
        store.getBaskets().put(id, b);
        return b;
    }

    public Optional<Basket> getBasket(long basketId) {
        return Optional.ofNullable(store.getBaskets().get(basketId));
    }

    public Optional<Basket> addItem(long basketId, long bikeId, int qty) {
        Basket basket = store.getBaskets().get(basketId);
        if (basket == null || basket.isCheckedOut()) return Optional.empty();
        basket.getItems().add(new BasketItem(bikeId, qty));
        return Optional.of(basket);
    }

    public CheckoutResult checkout(long basketId, String currency) {
        Basket basket = store.getBaskets().get(basketId);
        if (basket == null) return new CheckoutResult(false, "Basket not found");
        if (basket.isCheckedOut()) return new CheckoutResult(false, "Basket already checked out");

        var customer = store.getCustomers().get(basket.getCustomerId());
        if (customer == null) return new CheckoutResult(false, "Customer not found");

        // calculate total in EUR
        double totalEUR = basket.getItems().stream()
                .mapToDouble(item -> {
                    Bike bike = store.getBikes().get(item.getBikeId());
                    return bike.getBasePriceEUR() * item.getQuantity();
                }).sum();

        double rate = fx.getRate("EUR", currency);
        double totalInCurrency = totalEUR * rate;

        PaymentRequest req = new PaymentRequest(customer.getBankAccountId(), totalInCurrency, currency);
        PaymentResponse resp = bankService.processPayment(req);
        if (!resp.isApproved()) {
            return new CheckoutResult(false, "Payment refused: " + resp.getMessage());
        }

        // Mark bikes as SOLD
        basket.getItems().forEach(item -> {
            Bike bike = store.getBikes().get(item.getBikeId());
            bike.setStatus(BikeStatus.SOLD);
        });
        basket.setCheckedOut(true);
        return new CheckoutResult(true, "Payment OK. Total " + totalInCurrency + " " + currency);
    }

    public static class CheckoutResult {
        private boolean success;
        private String message;

        public CheckoutResult() {}
        public CheckoutResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        // getters & setters …
    }
}
