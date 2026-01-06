package com.sunit.groceryplus.models;

/** PaymentIntentParams - Model for Stripe Payment Intent parameters. */
public class PaymentIntentParams {
    private int amount;         // Amount in smallest currency unit (e.g., cents)
    private String currency;    // Currency code (e.g., "usd", "inr")

    /** Constructor. */
    public PaymentIntentParams(int amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public int getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}
