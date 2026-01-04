package com.sunit.groceryplus.models;

/**
 * Payment - Model class representing payment information for orders
 * 
 * This class encapsulates payment data for orders in the GroceryPlus application.
 * It supports multiple payment methods including Cash on Delivery (COD) and online payments,
 * with automatic status management based on payment type.
 * 
 * Key Features:
 * - Multiple payment method support (COD, online payments)
 * - Automatic status assignment based on payment method
 * - Payment tracking and status management
 * - Transaction ID support for online payments
 * - Order payment association
 * 
 * Payment Methods:
 * - COD (Cash on Delivery): Status initially "Pending", updated to "Completed" on delivery
 * - Online payments: Status automatically "Completed" upon successful transaction
 * - Other methods: Configurable status based on business logic
 * 
 * Payment Status Flow:
 * - COD: Pending → Completed (when order is delivered)
 * - Online: Completed (immediate upon successful payment)
 * - Failed: Used for unsuccessful payment attempts
 * 
 * Integration Points:
 * - DatabaseHelper for payment persistence
 * - OrderRepository for payment-order association
 * - AdminPaymentAdapter for payment management UI
 * - OrderManagementActivity for automatic COD completion
 * 
 * @author GroceryPlus Development Team
 * @version 1.0
 * @since 1.0
 */
public class Payment {
    
    // Primary key for payment record
    private int paymentId;
    
    // Associated order ID
    private int orderId;
    
    // Payment amount
    private double amount;
    
    // Payment method (e.g., "cod", "credit_card", "debit_card", "upi", etc.)
    private String paymentMethod;
    
    // Transaction ID for online payments (null for COD)
    private String transactionId;
    
    // Payment date/timestamp
    private String paymentDate;
    
    // Payment status: "Pending", "Completed", "Failed"
    private String status;

    /**
     * Constructor for Payment with automatic status assignment
     * 
     * This constructor automatically sets the payment status based on the payment method:
     * - COD payments: "Pending" (awaiting delivery completion)
     * - Online payments: "Completed" (immediate completion)
     * 
     * @param paymentId Unique payment identifier
     * @param orderId Associated order ID
     * @param amount Payment amount
     * @param paymentMethod Payment method used
     * @param transactionId Transaction ID (null for COD)
     * @param paymentDate Payment date/timestamp
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
     * Constructor for Payment with explicit status
     * 
     * This constructor allows setting a specific payment status, useful for
     * loading existing payment data from the database or handling special cases.
     * 
     * @param paymentId Unique payment identifier
     * @param orderId Associated order ID
     * @param amount Payment amount
     * @param paymentMethod Payment method used
     * @param transactionId Transaction ID (null for COD)
     * @param paymentDate Payment date/timestamp
     * @param status Explicit payment status
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
     * Get the unique payment identifier
     * 
     * @return Payment ID
     */
    public int getPaymentId() {
        return paymentId;
    }

    /**
     * Get the associated order ID
     * 
     * @return Order ID that this payment belongs to
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * Get the payment amount
     * 
     * @return Payment amount in local currency
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Get the payment method
     * 
     * @return Payment method (e.g., "cod", "credit_card", "upi")
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Get the transaction ID
     * 
     * @return Transaction ID for online payments, null for COD
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Get the payment date
     * 
     * @return Payment date/timestamp
     */
    public String getPaymentDate() {
        return paymentDate;
    }

    /**
     * Get the current payment status
     * 
     * @return Payment status ("Pending", "Completed", "Failed")
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the payment status
     * 
     * This method is used to update the payment status, typically when
     * a COD payment is completed upon order delivery.
     * 
     * @param status New payment status
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
