package com.sunit.groceryplus.models;

/** OrderItem - Model representing a specific product line item within a finalized Order. */
public class OrderItem {
    
    // Unique ID for this order line item
    private int orderItemId;    // Unique DB identifier
    private int orderId;        // Parent Order ID
    private int productId;      // Product Reference ID
    private String productName; // Snapshot of Product Name
    private int quantity;       // Quantity purchased
    private double price;       // Snapshot of Unit Price
    private String image;       // Snapshot of Image reference
    private String itemStatus;  // active, cancelled, returned, replaced
    private double refundAmount;
    private String refundStatus;// pending, processed, rejected

    /** Default Constructor. */
    public OrderItem() {
    }

    /**
     * Full Constructor for history retrieval.
     */
    public OrderItem(int orderItemId, int orderId, int productId, String productName, 
                     int quantity, double price, String image) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.image = image;
    }

    /**
     * Creation Constructor.
     */
    public OrderItem(int orderId, int productId, String productName, int quantity, double price) {
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    // ================= GETTERS AND SETTERS =================

    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getItemStatus() { return itemStatus; }
    public void setItemStatus(String itemStatus) { this.itemStatus = itemStatus; }

    public double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(double refundAmount) { this.refundAmount = refundAmount; }

    public String getRefundStatus() { return refundStatus; }
    public void setRefundStatus(String refundStatus) { this.refundStatus = refundStatus; }

    /**
     * Calculates the total cost for this line item.
     * 
     * @return Unit Price * Quantity
     */
    public double getSubtotal() {
        return price * quantity;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemId=" + orderItemId +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", image='" + image + '\'' +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}
