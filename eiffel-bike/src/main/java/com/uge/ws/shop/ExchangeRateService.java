package com.uge.ws.shop;

public interface ExchangeRateService {
    double convert(String fromCurrency, String toCurrency, double amount);
}