package com.uge.ws.shop;

import java.util.Map;

public class DummyExchangeRateService implements ExchangeRateService {

    // All rates expressed as: 1 EUR = rate * currency
    // (you can tweak the numbers if you want)
private static final Map<String, Double> EUR_RATES = Map.ofEntries(

    //Europe
        Map.entry("EUR", 1.0),
        Map.entry("BGN", 1.96),
        Map.entry("CZK", 25.2),
        Map.entry("DKK", 7.45),
        Map.entry("HUF", 390.0),
        Map.entry("PLN", 4.35),
        Map.entry("RON", 4.97),
        Map.entry("SEK", 11.2),
        Map.entry("CHF", 0.95),
        Map.entry("NOK", 11.5),
        Map.entry("ISK", 150.0),
        Map.entry("GBP", 0.86),
        Map.entry("TRY", 33.0),
        Map.entry("MKD", 61.5),
        Map.entry("RSD", 117.0),
        Map.entry("ALL", 102.0),
        Map.entry("USD", 1.08),

        //North America
        Map.entry("USD", 1.08),   // United States
        Map.entry("CAD", 1.45),   // Canada
        Map.entry("MXN", 18.5),   // Mexico

        // South America
        Map.entry("COP", 4300.0), // Colombia
        Map.entry("VES", 39.0),   // Venezuela (bolívar digital)
        Map.entry("ARS", 950.0),  // Argentina
        Map.entry("CLP", 980.0),  // Chile
        Map.entry("PEN", 4.1),    // Peru
        Map.entry("USD", 1.08),   // Ecuador uses USD officially
        Map.entry("BRL", 5.4),    // Brazil

        // Asia-Pacific
        Map.entry("JPY", 158.0),  // Japan
        Map.entry("KRW", 1450.0), // South Korea
        Map.entry("CNY", 7.8),    // China (yuan)
        Map.entry("AUD", 1.65),   // Australia
        Map.entry("NZD", 1.78),   // New Zealand
        Map.entry("INR", 89.0),   // India
        Map.entry("RUB", 98.0)    // Russia

        //French North Africa 
        Map.entry("MAD", 10.9),    // Morocco
        Map.entry("DZD", 145.0),   // Algeria
        Map.entry("TND", 3.3),     // Tunisia
        Map.entry("MRU", 42.0),    // Mauritania

        // Franc CFA zones
        Map.entry("XOF", 655.96),  // West African CFA franc
        Map.entry("XAF", 655.96),  // Central African CFA franc

        // Other francophone African currencies
        Map.entry("KMF", 492.0),   // Comoros
        Map.entry("DJF", 192.0),   // Djibouti
        Map.entry("GNF", 9150.0)   // Guinea


);

    @Override
    public double convert(String fromCurrency, String toCurrency, double amount) {
        // Normalise to uppercase
        String from = fromCurrency.toUpperCase();
        String to   = toCurrency.toUpperCase();

        double fromRate = EUR_RATES.getOrDefault(from, 1.0);
        double toRate   = EUR_RATES.getOrDefault(to,   1.0);

        // amount (from)  ->  EUR  ->  to
        double amountInEur = amount / fromRate;
        return amountInEur * toRate;
    }
}