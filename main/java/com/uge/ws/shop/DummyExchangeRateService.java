// src/main/java/com/uge/ws/shop/DummyExchangeRateService.java
package com.uge.ws.shop;

import java.util.Map;

/**
 * Simple fixed exchange rates for demo.
 * In the report, you can explain how to replace this
 * with a real HTTP call to an external FX API.
 */
public class DummyExchangeRateService implements ExchangeRateService {

    private static final Map<String, Double> EUR_RATES = Map.of(
            "EUR", 1.0,
            "USD", 1.08,
            "GBP", 0.86
    );

    @Override
    public double getRate(String fromCurrency, String toCurrency) {
        if (!"EUR".equals(fromCurrency)) {
            throw new IllegalArgumentException("Demo only supports EUR as base");
        }
        return EUR_RATES.getOrDefault(toCurrency, 1.0);
    }
}
