package com.sunit.groceryplus.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Order model class representing a customer order
 */
public class Order {
    private int orderId;
    private int userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private double totalAmount;
    private double deliveryFee;
    private String status;
    private String orderDate;
    private String shippedDate;
    private List<OrderItem> items;
    private boolean paymentReceived;
    private String paymentMethod;
    private int addressId;

    // Order status constants
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_SHIPPED = "shipped";
    public static final String STATUS_DELIVERED = "delivered";
    public static final String STATUS_CANCELLED = "cancelled";

    // Default constructor
    public Order() {
        this.items = new ArrayList<>();
    }

    // Constructor with all fields
    public Order(int orderId, int userId, String userName, String userEmail, String userPhone,
                 double totalAmount, double deliveryFee, String status, String orderDate) {
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.totalAmount = totalAmount;
        this.deliveryFee = deliveryFee;
        this.status = status;
        this.orderDate = orderDate;
        this.items = new ArrayList<>();
    }

    // Constructor without orderId (for new orders)
    public Order(int userId, double totalAmount, double deliveryFee, String status) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.deliveryFee = deliveryFee;
        this.status = status;
        this.items = new ArrayList<>();
    }

    // Constructor with addressId (used by getOrderById)
    public Order(int orderId, int userId, String userName, double totalAmount, double deliveryFee, String status, String orderDate, int addressId) {
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.totalAmount = totalAmount;
        this.deliveryFee = deliveryFee;
        this.status = status;
        this.orderDate = orderDate;
        this.addressId = addressId;
        this.items = new ArrayList<>();
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(String shippedDate) {
        this.shippedDate = shippedDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    public boolean isPaymentReceived() {
        return paymentReceived;
    }

    public void setPaymentReceived(boolean paymentReceived) {
        this.paymentReceived = paymentReceived;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    // Delivery Person
    private int deliveryPersonId;
    private String deliveryPersonName;

    public int getDeliveryPersonId() {
        return deliveryPersonId;
    }

    public void setDeliveryPersonId(int deliveryPersonId) {
        this.deliveryPersonId = deliveryPersonId;
    }

    public String getDeliveryPersonName() {
        return deliveryPersonName;
    }

    public void setDeliveryPersonName(String deliveryPersonName) {
        this.deliveryPersonName = deliveryPersonName;
    }

    // Helper methods
    public int getItemCount() {
        return items.size();
    }

    public int getTotalQuantity() {
        int total = 0;
        for (OrderItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }

    public boolean isPending() {
        return STATUS_PENDING.equals(status);
    }

    public boolean isProcessing() {
        return STATUS_PROCESSING.equals(status);
    }

    public boolean isShipped() {
        return STATUS_SHIPPED.equals(status);
    }

    public boolean isDelivered() {
        return STATUS_DELIVERED.equals(status);
    }

    public boolean isCancelled() {
        return STATUS_CANCELLED.equals(status);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", itemCount=" + getItemCount() +
                '}';
    }
}
