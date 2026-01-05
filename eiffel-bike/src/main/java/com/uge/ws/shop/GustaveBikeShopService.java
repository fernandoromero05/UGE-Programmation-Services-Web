package com.uge.ws.shop;

import com.uge.ws.bank.BankService;
import com.uge.ws.bank.PaymentRequest;
import com.uge.ws.bank.PaymentResponse;
import com.uge.ws.common.Bike;
import com.uge.ws.common.BikeStatus;
import com.uge.ws.common.DataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GustaveBikeShopService {

    private final DataStore store = DataStore.get();
    private final ExchangeRateService fx = new DummyExchangeRateService();
    private final BankService bankService = new BankService();

    // ---------- bikes for sale ---------- //

    /**
     * Bikes that have been rented at least once internally.
     */
    public List<Bike> listBikesForSale() {
        List<Bike> result = new ArrayList<>();
        for (Bike b : store.getBikes().values()) {
            if (b.getTotalRentals() > 0 && b.getStatus() != BikeStatus.RENTED) {
                result.add(b);
            }
        }
        return result;
    }

    public Bike getBike(long bikeId) {
        return store.getBikes().get(bikeId);
    }

    public List<Bike> listBikesForSale(String currency) {
        // default currency = EUR
        String target = (currency == null || currency.isBlank())
                ? "EUR"
                : currency.toUpperCase();

        // simple format check: 3 letters (USD, EUR, GBP, ...)
        if (!target.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException(
                    "Invalid currency code format: '" + target
                            + "' – expected 3-letter ISO code (e.g. EUR, USD, GBP)"
            );
        }

        List<Bike> original = listBikesForSale();   // prices in EUR
        List<Bike> convertedList = new ArrayList<>();

        for (Bike b : original) {
            double priceEUR = b.getBasePriceEUR();
            double convertedPrice;
            try {
                convertedPrice = fx.convert("EUR", target, priceEUR);
            } catch (IllegalArgumentException ex) {
                // e.g. DummyExchangeRateService doesn't know this currency
                throw new IllegalArgumentException("Unknown currency code: " + target, ex);
            }

            // Create a COPY so we don't overwrite the stored EUR price
            Bike copy = new Bike();            // uses no-arg constructor
            copy.setId(b.getId());
            copy.setModel(b.getModel());
            copy.setDescription(b.getDescription());
            copy.setStatus(b.getStatus());
            copy.setTotalRentals(b.getTotalRentals());
            copy.setBasePriceEUR(convertedPrice);   // now in target currency

            // copy notes (no need for setNotes; just reuse the list)
            copy.getNotes().addAll(b.getNotes());

            convertedList.add(copy);
        }

        return convertedList;
    }

    // ---------- baskets ---------- //

    public Basket createBasket(long customerId) {
        Map<Long, Customer> customers = store.getCustomers();
        if (!customers.containsKey(customerId)) {
            throw new IllegalArgumentException("Customer not found");
        }
        long id = store.nextBasketId();
        Basket basket = new Basket(id, customerId);
        store.getBaskets().put(id, basket);
        return basket;
    }

    public Basket addItem(long basketId, long bikeId, int quantity) {
        Basket basket = store.getBaskets().get(basketId);
        if (basket == null) {
            throw new IllegalArgumentException("Basket not found");
        }
        if (basket.isCheckedOut()) {
            throw new IllegalStateException("Basket already checked out");
        }

        Bike bike = store.getBikes().get(bikeId);
        if (bike == null) {
            throw new IllegalArgumentException("Bike not found");
        }

        // merge quantities if bike already present
        BasketItem existing = null;
        for (BasketItem item : basket.getItems()) {
            if (item.getBikeId() == bikeId) {
                existing = item;
                break;
            }
        }
        if (existing == null) {
            basket.getItems().add(new BasketItem(bikeId, quantity));
        } else {
            existing.setQuantity(existing.getQuantity() + quantity);
        }
        return basket;
    }

    public Basket getBasket(long basketId) {
        return store.getBaskets().get(basketId);
    }

    // ---------- checkout ---------- //

    public CheckoutResult checkout(long basketId, String currency) {
        Basket basket = store.getBaskets().get(basketId);
        if (basket == null) {
            return CheckoutResult.error("Basket not found");
        }
        if (basket.isCheckedOut()) {
            return CheckoutResult.error("Basket already checked out");
        }

        if (currency == null || currency.isBlank()) {
            currency = "EUR";
        }
        String targetCurrency = currency.toUpperCase();

        // 1) Validate _format_ of the currency code (3 uppercase letters)
        if (!targetCurrency.matches("^[A-Z]{3}$")) {
            return CheckoutResult.error(
                    "Invalid currency code format: '" + targetCurrency
                            + "' – expected a 3-letter ISO code (e.g. EUR, USD, GBP)"
            );
        }

        // 2) Compute total in EUR
        double totalEUR = 0.0;
        for (BasketItem item : basket.getItems()) {
            Bike bike = store.getBikes().get(item.getBikeId());
            if (bike == null) continue;
            totalEUR += bike.getBasePriceEUR() * item.getQuantity();
        }

        // 3) Convert using FX service and handle "unknown currency" from FX
        double totalInCurrency;
        try {
            totalInCurrency = fx.convert("EUR", targetCurrency, totalEUR);
        } catch (IllegalArgumentException ex) {
            // convention: DummyExchangeRateService throws IllegalArgumentException
            // for unsupported currency codes
            return CheckoutResult.error("Unknown currency code: " + targetCurrency);
        }

        Customer customer = store.getCustomers().get(basket.getCustomerId());
        if (customer == null) {
            return CheckoutResult.error("Customer not found for basket");
        }

        PaymentRequest req = new PaymentRequest(
                customer.getBankAccountId(),
                totalInCurrency,
                targetCurrency
        );

        PaymentResponse resp = bankService.processPayment(req);
        if (!resp.isApproved()) {
            return CheckoutResult.declined(
                    totalEUR,
                    targetCurrency,
                    totalInCurrency,
                    resp.getMessage()
            );
        }

        basket.setCheckedOut(true);
        return CheckoutResult.approved(
                totalEUR,
                targetCurrency,
                totalInCurrency,
                resp.getMessage()
        );
    }

    // ---------- DTO for checkout result ---------- //

    public static class CheckoutResult {
        private boolean success;
        private String status;      // APPROVED / DECLINED / ERROR
        private String message;
        private double totalEUR;
        private String currency;
        private double totalInCurrency;

        public static CheckoutResult approved(double totalEUR, String currency,
                                              double totalInCurrency, String msg) {
            CheckoutResult r = new CheckoutResult();
            r.success = true;
            r.status = "APPROVED";
            r.totalEUR = totalEUR;
            r.currency = currency;
            r.totalInCurrency = totalInCurrency;
            r.message = msg;
            return r;
        }

        public static CheckoutResult declined(double totalEUR, String currency,
                                              double totalInCurrency, String msg) {
            CheckoutResult r = new CheckoutResult();
            r.success = false;
            r.status = "DECLINED";
            r.totalEUR = totalEUR;
            r.currency = currency;
            r.totalInCurrency = totalInCurrency;
            r.message = msg;
            return r;
        }

        public static CheckoutResult error(String msg) {
            CheckoutResult r = new CheckoutResult();
            r.success = false;
            r.status = "ERROR";
            r.message = msg;
            return r;
        }

        // getters (needed for JSON)
        public boolean isSuccess() { return success; }
        public String getStatus() { return status; }
        public String getMessage() { return message; }
        public double getTotalEUR() { return totalEUR; }
        public String getCurrency() { return currency; }
        public double getTotalInCurrency() { return totalInCurrency; }
    }
}