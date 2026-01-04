package com.sunit.groceryplus.models;

/**
 * OrderItem Model Class
 * 
 * Represents a single line item within a finalized Order.
 * Unlike CartItem, this record is permanent and tied to a specific Order ID.
 * It stores snapshot data (price, product name) to preserve order history
 * even if the original Product is modified or deleted.
 */
public class OrderItem {
    
    // Unique ID for this order line item
    private int orderItemId;
    
    // The ID of the parent Order
    private int orderId;
    
    // The ID of the Product
    private int productId;
    
    // Name of the product at time of purchase
    private String productName;
    
    // Quantity purchased
    private int quantity;
    
    // Price per unit at time of purchase
    private double price;
    
    // Product image identifier
    private String image;

    /**
     * Default Constructor
     */
    public OrderItem() {
    }

    /**
     * Full Constructor
     * Used when retrieving items from the database.
     * 
     * @param orderItemId Unique Line Item ID
     * @param orderId Parent Order ID
     * @param productId Product ID
     * @param productName Product Name
     * @param quantity Quantity
     * @param price Unit Price
     * @param image Image URL
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
     * Creation Constructor
     * Used when creating a new OrderItem before it is saved to DB.
     * 
     * @param orderId Parent Order ID
     * @param productId Product ID
     * @param productName Product Name
     * @param quantity Quantity
     * @param price Unit Price
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
