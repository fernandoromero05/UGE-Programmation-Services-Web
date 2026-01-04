// src/main/java/com/uge/ws/shop/ExchangeRateService.java
package com.uge.ws.shop;

public interface ExchangeRateService {
    double getRate(String fromCurrency, String toCurrency);
}
