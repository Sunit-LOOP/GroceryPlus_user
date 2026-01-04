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

/**
 * OrderRepository - Repository class for order management operations
 * 
 * This repository class handles all order-related database operations and business logic
 * for the GroceryPlus application. It provides a clean abstraction layer between
 * the UI components and the database, implementing the Repository pattern.
 * 
 * Key Responsibilities:
 * - Order creation and management
 * - Order item handling
 * - Order status updates
 * - Delivery personnel assignment
 * - Order tracking and history
 * - Notification management for order updates
 * - Delivery optimization integration
 * 
 * Order Lifecycle:
 * 1. Order created with initial status
 * 2. Order items added to the order
 * 3. Delivery personnel assigned (optional)
 * 4. Order status updated through various stages
 * 5. Notifications sent for status changes
 * 6. Order completed or cancelled
 * 
 * Order Status Flow:
 * - Pending → Processing → Shipped → Delivered
 * - Can be cancelled at any stage
 * - Each status change triggers notifications
 * 
 * Integration Points:
 * - DatabaseHelper for data persistence
 * - DeliveryOptimizer for delivery time estimation
 * - GroceryNotificationManager for user notifications
 * - DeliveryPersonRepository for delivery assignments
 * 
 * @author GroceryPlus Development Team
 * @version 1.0
 * @since 1.0
 */
public class OrderRepository {
    
    // Tag for logging and debugging
    private static final String TAG = "OrderRepository";
    
    // Database and repository dependencies
    private DatabaseHelper dbHelper;
    private DeliveryPersonRepository deliveryPersonRepository;
    private Context context;

    /**
     * Constructor for OrderRepository
     * 
     * Initializes the repository with database helper and delivery person repository
     * for comprehensive order management operations.
     * 
     * @param context Application context for database operations and notifications
     */
    public OrderRepository(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
        deliveryPersonRepository = new DeliveryPersonRepository(context);
    }

    /**
     * Create a new order in the system
     * 
     * This method creates a new order with the specified details and automatically
     * sends a notification to the user with delivery time estimation using the
     * DeliveryOptimizer algorithm.
     * 
     * @param userId ID of the user placing the order
     * @param totalAmount Total amount for the order
     * @param deliveryFee Delivery fee for the order
     * @param status Initial status of the order (usually "Pending")
     * @param addressId Delivery address ID for the order
     * @param instructions Special delivery instructions
     * @return Order ID if successful, -1 if failed
     */
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

    /**
     * Add an item to an existing order
     * 
     * This method adds a product to an order with the specified quantity and price.
     * It's typically used during the order creation process when multiple items
     * are added to a single order.
     * 
     * @param orderId ID of the order to add the item to
     * @param productId ID of the product being added
     * @param quantity Quantity of the product
     * @param price Price per unit of the product
     * @return true if successful, false if failed
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
     * Get all orders in the system (Admin functionality)
     * 
     * This method retrieves all orders from the database, typically used by
     * administrators to view and manage all customer orders.
     * 
     * @return List of all orders, empty list if error occurs
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
     * Get orders for a specific user
     * 
     * This method retrieves all orders placed by a specific user, typically
     * used in order history and tracking features.
     * 
     * @param userId ID of the user to get orders for
     * @return List of user's orders, empty list if error occurs
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
     * Cancel an order initiated by the user.
     * 
     * This method performs the following operations:
     * 1. Updates the order status to "Cancelled".
     * 2. Calculates the refund amount by deducting a 15% cancellation fee.
     * 3. Sends a chat message from the Admin to the User notifying them of the cancellation and the calculated refund amount.
     * 4. Sends a system notification for the status change.
     * 
     * @param orderId The unique identifier of the order to be cancelled.
     * @param userId The ID of the user who owns the order.
     * @return true if the cancellation and message sending were successful, false otherwise.
     */
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

    /**
     * Update order status with extensive notification and refund handling.
     * 
     * Now includes logic for handling "Refunded" status initiated by Admin.
     * - If status is "Refunded", it triggers a full refund notification via chat.
     */
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