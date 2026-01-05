// src/main/java/com/uge/ws/bank/BankService.java
package com.uge.ws.bank;

import com.uge.ws.common.DataStore;

import java.util.HashMap;
import java.util.Map;

/**
 * Very small in-memory "bank".
 *
 * For phase 2 we want to support payments in *any* currency:
 *   - The shop sends a PaymentRequest in whatever currency the customer chose.
 *   - The bank account itself has its own currency (EUR, USD, ...).
 *   - Here we convert from the request currency into the account currency
 *     using a tiny FX table and EUR as pivot.
 *
 * Any unknown currency code falls back to a neutral rate 1.0, so the
 * application continues to work even if the user enters exotic codes.
 */
public class BankService {

    private final DataStore store = DataStore.get();

    /**
     * Approx. rates: 1 EUR = X units of that currency.
     * (values are deliberately rough; this is a teaching project)
     */
    private static final Map<String, Double> EUR_RATES = new HashMap<>();
    static {
        EUR_RATES.put("EUR", 1.0);
        EUR_RATES.put("USD", 1.08);
        EUR_RATES.put("GBP", 0.86);
        EUR_RATES.put("JPY", 160.0);
        EUR_RATES.put("CHF", 0.95);
        EUR_RATES.put("CAD", 1.45);
        EUR_RATES.put("AUD", 1.65);
        // Anything else will default to 1.0
    }

    /**
     * Converts an amount from one currency to another using EUR as pivot.
     * rate(from -> to) = (amount / (1 EUR in FROM)) * (1 EUR in TO)
     */
    private double convert(String fromCurrency, String toCurrency, double amount) {
        if (fromCurrency == null || toCurrency == null) {
            return amount; // degenerate, but keeps things simple
        }

        String from = fromCurrency.toUpperCase();
        String to   = toCurrency.toUpperCase();

        if (from.equals(to)) {
            return amount;
        }

        double eurToFrom = EUR_RATES.getOrDefault(from, 1.0); // 1 EUR = eurToFrom FROM
        double eurToTo   = EUR_RATES.getOrDefault(to,   1.0); // 1 EUR = eurToTo TO

        // amount (FROM) -> EUR -> TO
        double inEur = amount / eurToFrom;
        return inEur * eurToTo;
    }

    public PaymentResponse processPayment(PaymentRequest req) {
        var account = store.getAccounts().get(req.getAccountId());
        if (account == null) {
            return new PaymentResponse(false, "Account not found");
        }

        String requestCurrency = req.getCurrency();
        String accountCurrency = account.getCurrency();

        // Convert from the currency in the payment request into the
        // currency of the bank account.
        double debitAmount = convert(requestCurrency, accountCurrency, req.getAmount());

        if (account.getBalance() < debitAmount) {
            return new PaymentResponse(false, "Insufficient funds");
        }

        account.setBalance(account.getBalance() - debitAmount);

        return new PaymentResponse(
                true,
                "Payment processed. Debited " + debitAmount + " " + accountCurrency
                        + ". New balance: " + account.getBalance()
        );
    }
}