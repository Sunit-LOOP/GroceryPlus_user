package com.sunit.groceryplus.models;

public class PaymentIntentParams {
    private int amount;
    private String currency;

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
