package com.sunit.groceryplus.models;

public class Payment {
    private int paymentId;
    private int orderId;
    private double amount;
    private String paymentMethod;
    private String transactionId;
    private String paymentDate;

    public Payment(int paymentId, int orderId, double amount, String paymentMethod, String transactionId, String paymentDate) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.paymentDate = paymentDate;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public int getOrderId() {
        return orderId;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getPaymentDate() {
        return paymentDate;
    }
}
