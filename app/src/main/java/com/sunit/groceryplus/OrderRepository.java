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

/** Repository for managing user and admin order operations in the database. */
public class OrderRepository {
    // Infrastructure
    private static final String TAG = "OrderRepository";
    private DatabaseHelper dbHelper;
    private DeliveryPersonRepository deliveryPersonRepository;
    private Context context;

    /** Initializes the repository with necessary dependencies. */
    public OrderRepository(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
        deliveryPersonRepository = new DeliveryPersonRepository(context);
    }

    /** Creates a new order and sends an initial confirmation notification. */
    public long createOrder(int userId, double totalAmount, double deliveryFee, String status, int addressId, String instructions) {
        try {
            // Create order in database
            long orderId = dbHelper.createOrder(userId, totalAmount, deliveryFee, status, addressId, instructions);
            
            if (orderId != -1) {
                // Send order confirmation notification with delivery time estimate
                String title = "Order Placed";
                
                // Calculate estimated delivery time using Dijkstra's Algorithm
                int deliveryMin = DeliveryOptimizer.calculateShortestDeliveryTime("Area B");
                String message = "Your order #" + orderId + " is placed! Delivery estimated in " + deliveryMin + " mins.";

                // Send notification to user
                GroceryNotificationManager.getInstance(context).sendNotification(userId, title, message, GroceryNotificationManager.TYPE_ORDER, String.valueOf(orderId));
            }
            
            return orderId;
        } catch (Exception e) {
            Log.e(TAG, "Error creating order", e);
            return -1;
        }
    }

    /** Adds a specific product item to an existing order. */
    public boolean addOrderItem(int orderId, int productId, int quantity, double price) {
        try {
            long result = dbHelper.addOrderItem(orderId, productId, quantity, price);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error adding order item", e);
            return false;
        }
    }

    /** Retrieves all orders in the system for admin use. */
    public List<Order> getAllOrders() {
        try {
            return dbHelper.getAllOrders();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all orders", e);
            return new ArrayList<>();
        }
    }

    /** Retrieves all orders placed by a specific user. */
    public List<Order> getUserOrders(int userId) {
        try {
            return dbHelper.getOrdersByUser(userId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting user orders", e);
            return new ArrayList<>();
        }
    }

    /** Retrieves all items associated with a specific order. */
    public List<OrderItem> getOrderItems(int orderId) {
        try {
            return dbHelper.getOrderItems(orderId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting order items", e);
            return new ArrayList<>();
        }
    }

    /** Cancels an order, calculates refund with fee, and notifies the user via chat and system. */
    public boolean cancelOrder(int orderId, int userId) {
        try {
            // 1. Get Order Details to calculate refund
            Order order = dbHelper.getOrderById(orderId);
            if (order == null) {
                Log.e(TAG, "Order not found for cancellation: " + orderId);
                return false;
            }

            // 2. Calculate Refund Amount (Total Amount - 15% Fee)
            double totalAmount = order.getTotalAmount();
            double deductionPercentage = 0.15; // 15% deduction
            double deductionAmount = totalAmount * deductionPercentage;
            double refundAmount = totalAmount - deductionAmount;
            
            // Format amounts for message
            String refundMsg = String.format("Refund of Rs. %.2f (after 15%% fee deduction) has been initiated.", refundAmount);

            // 3. Update Order Status in Database
            boolean success = dbHelper.updateOrderStatus(orderId, "Cancelled");
            
            if (success) {
                // 4. Send Chat Message from Admin to User
                // Create detailed message about cancellation and refund
                String chatMessage = "Your Order #" + orderId + " has been Cancelled by you.\n" +
                                     refundMsg + "\n" +
                                     "Amount will reflect in your original payment method within 5-7 business days.";

                // Get Admin ID (System Admin)
                int adminId = dbHelper.getAdminId();
                if (adminId != -1) {
                     // Insert message into 'messages' table
                     dbHelper.sendMessage(adminId, userId, chatMessage);
                     Log.d(TAG, "Cancellation chat message sent for Order #" + orderId);
                }

                // 5. Send System Notification using Notification Manager
                String notifTitle = "Order Cancelled";
                String notifMessage = "Order #" + orderId + " cancelled. " + refundMsg;
                GroceryNotificationManager.getInstance(context).sendNotification(userId, notifTitle, notifMessage, GroceryNotificationManager.TYPE_ORDER, String.valueOf(orderId));
                
                // 6. Release Delivery Person if assigned
                if (order.getDeliveryPersonId() > 0) {
                    deliveryPersonRepository.releaseDeliveryPerson(order.getDeliveryPersonId());
                }
            }
            return success;
        } catch (Exception e) {
            Log.e(TAG, "Error cancelling order", e);
            return false;
        }
    }

    /** Updates order status and sends appropriate user notifications, including refund info if applicable. */
    public boolean updateOrderStatus(int orderId, int userId, String status) {
        try {
            boolean success = dbHelper.updateOrderStatus(orderId, status);
            if (success) {
                String title = "Order Update";
                String message;
                
                // Get Admin ID for sending chat messages
                int adminId = dbHelper.getAdminId();

                if ("delivered".equalsIgnoreCase(status)) {
                    title = "Order Delivered!";
                    message = "Great news! Your order #" + orderId + " has been delivered. Enjoy your groceries!";
                } else if ("shipped".equalsIgnoreCase(status)) {
                    title = "Order Shipped!";
                    message = "Your order #" + orderId + " is on its way! It should arrive in about 30 minutes.";
                } else if ("Refunded".equalsIgnoreCase(status)) {
                    // Handle Admin-side Full Refund
                    title = "Order Refunded";
                    message = "Your order #" + orderId + " has been fully refunded.";
                    
                    // Send Chat Message for Refund
                    if (adminId != -1) {
                        String chatMessage = "Your Order #" + orderId + " has been marked as Refunded by the store.\n" +
                                             "A FULL refund has been initiated to your original payment method.\n" +
                                             "Please allow 5-7 business days for the amount to reflect.";
                        dbHelper.sendMessage(adminId, userId, chatMessage);
                    }
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

    /** Retrieves the most recent order placed by a user. */
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

    /** Manually assigns a delivery person to an order. */
    public boolean assignDeliveryPerson(int orderId, int deliveryPersonId) {
        boolean dbResult = dbHelper.assignDeliveryPerson(orderId, deliveryPersonId);
        if (dbResult) {
            deliveryPersonRepository.assignOrderToDeliveryPerson(deliveryPersonId, orderId);
        }
        return dbResult;
    }

    /** Automatically assigns the next available delivery person to an order. */
    public boolean autoAssignNextAvailable(int orderId) {
        DeliveryPerson next = deliveryPersonRepository.getNextAvailableDeliveryPerson();
        if (next != null) {
            return assignDeliveryPerson(orderId, next.getPersonId());
        }
        return false;
    }

    /** Updates order status and manages delivery person availability based on the status. */
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

    /** Reassigns pending orders when a delivery person becomes unavailable. */
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
    
    /** Records a payment transaction for an order in the database. */
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