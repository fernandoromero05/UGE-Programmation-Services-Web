package com.uge.ws.shop;

import java.util.HashMap;
import java.util.Map;

public class DummyExchangeRateService implements ExchangeRateService {

    private final Map<String, Double> rates = new HashMap<>();

    public DummyExchangeRateService() {
        // Very simple fake rates relative to EUR
        rates.put("EUR", 1.0);
        rates.put("USD", 1.1);
        rates.put("GBP", 0.85);
        rates.put("JPY", 130.0);
    }

    @Override
    public double convert(String fromCurrency, String toCurrency, double amount) {
        String from = fromCurrency.toUpperCase();
        String to = toCurrency.toUpperCase();
        Double fromRate = rates.get(from);
        Double toRate = rates.get(to);

        if (fromRate == null || toRate == null) {
            throw new IllegalArgumentException("Unsupported currency: " + from + " or " + to);
        }

        double inEur = amount / fromRate;
        return inEur * toRate;
    }
}
