package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;
import java.util.List;

public class OrderItemRepository {
    private static final String TAG = "OrderItemRepository";
    private DatabaseHelper dbHelper;

    public OrderItemRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    /**
     * Add order item
     */
    public boolean addOrderItem(int orderId, int productId, int quantity, double price) {
        try {
            long result = dbHelper.addOrderItem(orderId, productId, quantity, price);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error adding order item", e);
            return false;
        }
    }

    /**
     * Get order items by order ID
     */
    public List<com.sunit.groceryplus.models.OrderItem> getOrderItemsByOrderId(int orderId) {
        try {
            return dbHelper.getOrderItems(orderId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting order items by order ID", e);
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Get order items by product ID
     */
    public List<com.sunit.groceryplus.models.OrderItem> getOrderItemsByProductId(int productId) {
        try {
            // Get all order items and filter by product ID
            List<com.sunit.groceryplus.models.OrderItem> allItems = dbHelper.getAllOrderItems();
            List<com.sunit.groceryplus.models.OrderItem> filteredItems = new java.util.ArrayList<>();
            
            for (com.sunit.groceryplus.models.OrderItem item : allItems) {
                if (item.getProductId() == productId) {
                    filteredItems.add(item);
                }
            }
            
            return filteredItems;
        } catch (Exception e) {
            Log.e(TAG, "Error getting order items by product ID", e);
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Update order item quantity
     */
    public boolean updateOrderItemQuantity(int orderItemId, int quantity) {
        try {
            return dbHelper.updateOrderItemQuantity(orderItemId, quantity);
        } catch (Exception e) {
            Log.e(TAG, "Error updating order item quantity", e);
            return false;
        }
    }

    /**
     * Delete order item
     */
    public boolean deleteOrderItem(int orderItemId) {
        try {
            return dbHelper.deleteOrderItem(orderItemId);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting order item", e);
            return false;
        }
    }

    /**
     * Get total amount for order
     */
    public double getOrderTotal(int orderId) {
        try {
            List<com.sunit.groceryplus.models.OrderItem> items = dbHelper.getOrderItems(orderId);
            double total = 0.0;
            
            for (com.sunit.groceryplus.models.OrderItem item : items) {
                total += (item.getPrice() * item.getQuantity());
            }
            
            return total;
        } catch (Exception e) {
            Log.e(TAG, "Error calculating order total", e);
            return 0.0;
        }
    }
}