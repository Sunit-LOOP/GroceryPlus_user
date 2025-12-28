package com.sunit.groceryplus.network;

public class PaymentIntentRequest {
    private int amount;
    private String currency;

    public PaymentIntentRequest(int amount, String currency) {
        this.amount = amount;
        // Ensure we're using a valid currency code
        this.currency = currency != null ? currency.toLowerCase() : "npr"; // NPR for Nepal
    }

    public int getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}