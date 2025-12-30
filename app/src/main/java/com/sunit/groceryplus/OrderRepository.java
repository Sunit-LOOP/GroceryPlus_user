package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;

import com.sunit.groceryplus.models.Order;
import com.sunit.groceryplus.models.OrderItem;
import com.sunit.groceryplus.models.DeliveryPerson;

import java.util.ArrayList;
import java.util.List;

import com.sunit.groceryplus.utils.DeliveryOptimizer;
import com.sunit.groceryplus.utils.GroceryNotificationManager;

public class OrderRepository {
    private static final String TAG = "OrderRepository";
    private DatabaseHelper dbHelper;
    private DeliveryPersonRepository deliveryPersonRepository;
    private Context context;

    public OrderRepository(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
        deliveryPersonRepository = new DeliveryPersonRepository(context);
    }

    /**
     * Create a new order
     */
    public long createOrder(int userId, double totalAmount, double deliveryFee, String status, int addressId, String instructions) {
        try {
            long orderId = dbHelper.createOrder(userId, totalAmount, deliveryFee, status, addressId, instructions);
            if (orderId != -1) {
                String title = "Order Placed";
                // Estimate delivery time using Dijkstra's Algorithm
                int deliveryMin = DeliveryOptimizer.calculateShortestDeliveryTime("Area B");
                String message = "Your order #" + orderId + " is placed! Delivery estimated in " + deliveryMin + " mins.";

                GroceryNotificationManager.getInstance(context).sendNotification(userId, title, message, GroceryNotificationManager.TYPE_ORDER, String.valueOf(orderId));
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
                
                GroceryNotificationManager.getInstance(context).sendNotification(userId, title, message, GroceryNotificationManager.TYPE_ORDER, String.valueOf(orderId));
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
        boolean dbResult = dbHelper.assignDeliveryPerson(orderId, deliveryPersonId);
        if (dbResult) {
            deliveryPersonRepository.assignOrderToDeliveryPerson(deliveryPersonId, orderId);
        }
        return dbResult;
    }

    /**
     * Auto-assign next available delivery boy to an order
     */
    public boolean autoAssignNextAvailable(int orderId) {
        DeliveryPerson next = deliveryPersonRepository.getNextAvailableDeliveryPerson();
        if (next != null) {
            return assignDeliveryPerson(orderId, next.getPersonId());
        }
        return false;
    }

    /**
     * Update order status and handle delivery person availability
     */
    public boolean updateOrderStatusWithDeliveryLogic(int orderId, int userId, String status) {
        try {
            Order order = dbHelper.getOrderById(orderId);
            if (order == null) return false;

            boolean success = dbHelper.updateOrderStatus(orderId, status);
            if (!success) return false;

            // Handle delivery person logic
            if ("OUT_FOR_DELIVERY".equalsIgnoreCase(status) && order.getDeliveryPersonId() > 0) {
                deliveryPersonRepository.assignOrderToDeliveryPerson(order.getDeliveryPersonId(), orderId);
            } else if (("DELIVERED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) && order.getDeliveryPersonId() > 0) {
                deliveryPersonRepository.releaseDeliveryPerson(order.getDeliveryPersonId());
            }

            // Notification
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
            GroceryNotificationManager.getInstance(context).sendNotification(userId, title, message, GroceryNotificationManager.TYPE_ORDER, String.valueOf(orderId));

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error updating order status with delivery logic", e);
            return false;
        }
    }

    /**
     * Reassign pending orders for a delivery boy who becomes unavailable
     */
    public void reassignOrdersForUnavailableDeliveryBoy(int deliveryPersonId) {
        try {
            List<Order> pendingOrders = dbHelper.getOrdersByDeliveryPersonAndStatus(deliveryPersonId, "PENDING");
            for (Order order : pendingOrders) {
                boolean reassigned = autoAssignNextAvailable(order.getOrderId());
                if (reassigned) {
                    Log.d(TAG, "Reassigned order " + order.getOrderId() + " from unavailable delivery boy " + deliveryPersonId);
                } else {
                    Log.w(TAG, "No available delivery boy to reassign order " + order.getOrderId());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reassigning orders for unavailable delivery boy", e);
        }
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
}