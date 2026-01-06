package com.sunit.groceryplus.models;

/** Payment - Model representing financial transactions for orders, supporting multiple methods and status tracking. */
public class Payment {
    
    // Primary key for payment record
    private int paymentId;          // Unique DB identifier
    private int orderId;            // Associated Order ID
    private double amount;          // Transaction Amount
    private String paymentMethod;   // Method (COD, Credit Card, UPI, etc.)
    private String transactionId;   // External Transaction Ref (null for COD)
    private String paymentDate;     // Timestamp
    private String status;          // Status (Pending, Completed, Failed)

    /**
     * Constructor with automatic status assignment based on method.
     */
    public Payment(int paymentId, int orderId, double amount, String paymentMethod, String transactionId, String paymentDate) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.paymentDate = paymentDate;
        
        // Set status based on payment method
        // COD payments start as "Pending" and are completed on delivery
        // Online payments are immediately "Completed"
        this.status = "cod".equalsIgnoreCase(paymentMethod) ? "Pending" : "Completed";
    }

    /**
     * Constructor with explicit status.
     */
    public Payment(int paymentId, int orderId, double amount, String paymentMethod, String transactionId, String paymentDate, String status) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.paymentDate = paymentDate;
        this.status = status;
    }

    /**
     * Get the unique payment identifier.
     */
    public int getPaymentId() {
        return paymentId;
    }

    /**
     * Get the associated order ID.
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * Get the payment amount.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Get the payment method.
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Get the transaction ID.
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Get the payment date.
     */
    public String getPaymentDate() {
        return paymentDate;
    }

    /**
     * Get the current payment status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the payment status.
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
