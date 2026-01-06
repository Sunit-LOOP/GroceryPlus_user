package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;
import java.util.List;

/** Repository for managing individual items within an order in the database. */
public class OrderItemRepository {
    // Infrastructure
    private static final String TAG = "OrderItemRepository";
    private DatabaseHelper dbHelper;

    /** Initializes the repository with a DatabaseHelper. */
    public OrderItemRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    /** Adds a new item to an existing order. */
    public boolean addOrderItem(int orderId, int productId, int quantity, double price) {
        try {
            long result = dbHelper.addOrderItem(orderId, productId, quantity, price);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error adding order item", e);
            return false;
        }
    }

    /** Retrieves all items associated with a specific order ID. */
    public List<com.sunit.groceryplus.models.OrderItem> getOrderItemsByOrderId(int orderId) {
        try {
            return dbHelper.getOrderItems(orderId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting order items by order ID", e);
            return new java.util.ArrayList<>();
        }
    }

    /** Retrieves all order items that contain a specific product ID. */
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

    /** Updates the quantity for a specific order item. */
    public boolean updateOrderItemQuantity(int orderItemId, int quantity) {
        try {
            return dbHelper.updateOrderItemQuantity(orderItemId, quantity);
        } catch (Exception e) {
            Log.e(TAG, "Error updating order item quantity", e);
            return false;
        }
    }

    /** Deletes a specific order item from the database. */
    public boolean deleteOrderItem(int orderItemId) {
        try {
            return dbHelper.deleteOrderItem(orderItemId);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting order item", e);
            return false;
        }
    }

    /** Calculates the total cost for all items in a specific order. */
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