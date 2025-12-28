package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;

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
    public String[] getOrderItemsByOrderId(int orderId) {
        // This is a simplified implementation
        // In a real app, you would query the database and return a list of OrderItem objects
        return new String[]{"Item 1", "Item 2", "Item 3"};
    }

    /**
     * Get order items by product ID
     */
    public String[] getOrderItemsByProductId(int productId) {
        // This is a simplified implementation
        // In a real app, you would query the database and return a list of OrderItem objects
        return new String[]{"Order Item 1", "Order Item 2"};
    }
}