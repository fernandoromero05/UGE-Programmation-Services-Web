// src/main/java/com/uge/ws/bank/BankService.java
package com.uge.ws.bank;

import com.uge.ws.common.DataStore;

public class BankService {

    private final DataStore store = DataStore.get();

    public PaymentResponse processPayment(PaymentRequest req) {
        var account = store.getAccounts().get(req.getAccountId());
        if (account == null) {
            return new PaymentResponse(false, "Account not found");
        }
        if (!account.getCurrency().equals(req.getCurrency())) {
            // for simplicity we assume same currency, or you could apply FX again
            return new PaymentResponse(false, "Currency mismatch");
        }
        if (account.getBalance() < req.getAmount()) {
            return new PaymentResponse(false, "Insufficient funds");
        }
        account.setBalance(account.getBalance() - req.getAmount());
        return new PaymentResponse(true, "Payment processed. New balance: " + account.getBalance());
    }
}
