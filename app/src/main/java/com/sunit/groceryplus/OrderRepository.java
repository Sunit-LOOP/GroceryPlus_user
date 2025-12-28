package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;

import com.sunit.groceryplus.models.Order;
import com.sunit.groceryplus.models.OrderItem;
import com.sunit.groceryplus.network.ApiClient;
import com.sunit.groceryplus.network.GroceryApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.sunit.groceryplus.utils.DeliveryOptimizer;
public class OrderRepository {
    private static final String TAG = "OrderRepository";
    private DatabaseHelper dbHelper;
    private Context context;
    private GroceryApi groceryApi;

    public OrderRepository(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
        this.groceryApi = ApiClient.getGroceryApi();
    }

    /**
     * Create a new order
     */
    public long createOrder(int userId, double totalAmount, double deliveryFee, String status, int addressId) {
        try {
            long orderId = dbHelper.createOrder(userId, totalAmount, deliveryFee, status, addressId);
            if (orderId != -1) {
                // Sync with web API
                syncOrderToAPI(orderId, userId, totalAmount);

                String title = "Order Placed";
                // Estimate delivery time using Dijkstra's Algorithm
                int deliveryMin = DeliveryOptimizer.calculateShortestDeliveryTime("Area B");
                String message = "Your order #" + orderId + " is placed! Delivery estimated in " + deliveryMin + " mins.";

                dbHelper.addNotification(userId, title, message);
                com.sunit.groceryplus.utils.NotificationUtils.showNotification(context, title, message);
            }
            return orderId;
        } catch (Exception e) {
            Log.e(TAG, "Error creating order", e);
            return -1;
        }
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
     * Get all orders (for admin)
     */
    public List<Order> getAllOrders() {
        try {
            return dbHelper.getAllOrders();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all orders", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get orders for user
     */
    public List<Order> getUserOrders(int userId) {
        try {
            return dbHelper.getOrdersByUser(userId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting user orders", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get order items
     */
    public List<OrderItem> getOrderItems(int orderId) {
        try {
            return dbHelper.getOrderItems(orderId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting order items", e);
            return new ArrayList<>();
        }
    }

    /**
     * Update order status
     */
    public boolean updateOrderStatus(int orderId, int userId, String status) {
        try {
            boolean success = dbHelper.updateOrderStatus(orderId, status);
            if (success) {
                String title = "Order Update";
                String message;
                if ("delivered".equalsIgnoreCase(status)) {
                    title = "Order Delivered!";
                    message = "Great news! Your order #" + orderId + " has been delivered. Enjoy your groceries!";
                } else if ("shipped".equalsIgnoreCase(status)) {
                    title = "Order Shipped!";
                    message = "Your order #" + orderId + " is on its way! It should arrive in about 30 minutes.";
                } else {
                    message = "Your order #" + orderId + " is now " + status;
                }
                
                dbHelper.addNotification(userId, title, message);
                com.sunit.groceryplus.utils.NotificationUtils.showNotification(context, title, message);
            }
            return success;
        } catch (Exception e) {
            Log.e(TAG, "Error updating order status", e);
            return false;
        }
    }

    /**
     * Get last order for user
     */
    public Order getLastOrder(int userId) {
        try {
            // Get all user orders
            List<Order> orders = dbHelper.getOrdersByUser(userId);
            if (orders != null && !orders.isEmpty()) {
                return orders.get(0); 
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting last order", e);
        }
        return null;
    }

    public boolean assignDeliveryPerson(int orderId, int deliveryPersonId) {
        return dbHelper.assignDeliveryPerson(orderId, deliveryPersonId);
    }
    
    /**
     * Record payment for an order
     */
    public boolean recordPayment(int orderId, double amount, String paymentMethod, String transactionId) {
        try {
            long result = dbHelper.addPayment(orderId, amount, paymentMethod, transactionId);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error recording payment", e);
            return false;
        }
    }

    /**
     * Sync order to web API
     */
    private void syncOrderToAPI(long orderId, int userId, double total) {
        try {
            // Get order items
            List<OrderItem> items = dbHelper.getOrderItems((int) orderId);
            List<Map<String, Object>> itemData = new ArrayList<>();
            for (OrderItem item : items) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("product_id", item.getProductId());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("price", item.getPrice());
                itemData.add(itemMap);
            }

            Map<String, Object> orderData = new HashMap<>();
            orderData.put("user_id", userId);
            orderData.put("total_amount", total);
            orderData.put("status", "pending");
            orderData.put("items", itemData);

            groceryApi.placeOrder(orderData).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Order synced to web API");
                    } else {
                        Log.e(TAG, "Failed to sync order: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Log.e(TAG, "Error syncing order to API", t);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error preparing order sync", e);
        }
    }
}