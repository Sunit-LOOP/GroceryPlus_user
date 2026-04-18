package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;

import com.sunit.groceryplus.models.Order;
import com.sunit.groceryplus.models.OrderItem;
import com.sunit.groceryplus.models.DeliveryPerson;
import com.sunit.groceryplus.models.SupportTicket;

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

    /** Decrements the stock quantity of a specific product by the specified amount. */
    public boolean decrementStock(int productId, int quantity) {
        try {
            return dbHelper.decrementStock(productId, quantity);
        } catch (Exception e) {
            Log.e(TAG, "Error decrementing stock for product ID: " + productId, e);
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

    /** Retrieves a specific order by its ID. */
    public Order getOrderById(int orderId) {
        try {
            return dbHelper.getOrderById(orderId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting order by id", e);
            return null;
        }
    }

    public static String lastError = "";

    /** Stripe / card prepayments — refund after cancel is credited to wallet (not COD). */
    private static boolean isOnlineCardPayment(String paymentMethod) {
        if (paymentMethod == null) {
            return false;
        }
        String m = paymentMethod.trim();
        if (m.isEmpty()) {
            return false;
        }
        String lo = m.toLowerCase();
        if ("cod".equals(lo) || lo.contains("cash on delivery")) {
            return false;
        }
        return "stripe".equals(lo)
                || "credit card".equals(lo)
                || "debitcard".equals(lo)
                || "debit card".equals(lo)
                || lo.contains("stripe");
    }

    /** Cancels an order, calculates refund with fee, replenishes stock, and notifies the user. */
    public boolean cancelOrder(int orderId, int userId) {
        try {
            lastError = "";
            // 1. Get Order Details
            Order order = dbHelper.getOrderById(orderId);
            if (order == null) {
                lastError = "Order not found.";
                return false;
            }
            if (userId <= 0) {
                lastError = "Invalid user session.";
                return false;
            }
            if (order.getUserId() != userId) {
                lastError = "You can only cancel your own orders.";
                return false;
            }
            String currentStatus = order.getStatus();
            if (currentStatus != null) {
                currentStatus = currentStatus.trim();
            }
            if (currentStatus == null || currentStatus.isEmpty()
                    || (!"pending".equalsIgnoreCase(currentStatus) && !"processing".equalsIgnoreCase(currentStatus))) {
                lastError = "Only pending or processing orders can be cancelled.";
                return false;
            }

            // 2. Identify Payment Method (trimmed; Stripe may be stored as "stripe", " Stripe ", etc.)
            String paymentMethod = "COD";
            android.database.Cursor paymentCursor = dbHelper.getPaymentByOrderId(orderId);
            if (paymentCursor != null) {
                try {
                    if (paymentCursor.moveToFirst()) {
                        int col = paymentCursor.getColumnIndex(com.sunit.groceryplus.DatabaseContract.PaymentEntry.COLUMN_NAME_PAYMENT_METHOD);
                        if (col != -1 && !paymentCursor.isNull(col)) {
                            String raw = paymentCursor.getString(col);
                            if (raw != null) {
                                paymentMethod = raw.trim();
                            }
                        }
                    }
                } finally {
                    paymentCursor.close();
                }
            }

            boolean isPaidOrder = isOnlineCardPayment(paymentMethod);

            // 3. Update Status
            boolean success = dbHelper.updateOrderStatus(orderId, "Cancelled");
            
            if (success) {
                // 4. Replenish Stock
                List<OrderItem> items = dbHelper.getOrderItems(orderId);
                for (OrderItem item : items) {
                    dbHelper.incrementStock(item.getProductId(), item.getQuantity());
                }

                // 5. Build Messages
                String notifTitle = "Order Cancelled";
                String notifMessage = "Your order #" + orderId + " has been cancelled.";
                String chatMessage = "Your Order #" + orderId + " has been Cancelled.";
                
                // Restore Wallet Balance Used (Immediate) FOR ALL ORDERS
                double walletUsed = dbHelper.getWalletDebitForOrder(userId, orderId);
                if (walletUsed > 0) {
                    dbHelper.logTransaction(userId, walletUsed, "credit", "refund", "Restoration of Balance used for Order #" + orderId);
                    Log.d(TAG, "Restored wallet balance: " + walletUsed);
                }

                // Restore Loyalty Points Used (Immediate) FOR ALL ORDERS
                double pointsUsed = dbHelper.getPointsDebitForOrder(userId, orderId);
                if (pointsUsed > 0) {
                    dbHelper.addLoyaltyPoints(userId, (float)pointsUsed);
                    dbHelper.logTransaction(userId, pointsUsed, "credit", "loyalty_refund", "Restoration of Points used for Order #" + orderId);
                }
                
                if (walletUsed > 0 || pointsUsed > 0) {
                    String partialRefundMsg = String.format("Restored: NPR %.2f.", (walletUsed + pointsUsed));
                    chatMessage += "\n" + partialRefundMsg;
                    notifMessage += " " + partialRefundMsg;
                }

                if (isPaidOrder) {
                    double stripeAmount = order.getTotalAmount();
                    double stipeRefund = stripeAmount * 0.85; // 15% cancellation fee deduction
                    
                    // CREDIT TO WALLET (Immediate)
                    dbHelper.logTransaction(userId, stipeRefund, "credit", "refund", "Cancellation Refund for Order #" + orderId);
                    
                    String refundMsg = String.format("Stripe Refund: NPR %.2f (Instantly Credited to Wallet).", stipeRefund);
                    chatMessage += "\n" + refundMsg;
                    notifMessage += " " + refundMsg;
                    
                    // Update Payment Status to Refunded
                    dbHelper.updatePaymentStatusByOrderId(orderId, "Refunded");
                } else {
                    dbHelper.updatePaymentStatusByOrderId(orderId, "Cancelled");
                }

                // 6. Notify (never fail cancellation if messaging/notifications error after DB update)
                try {
                    int adminId = dbHelper.getAdminId();
                    if (adminId != -1) {
                        dbHelper.sendMessage(adminId, userId, chatMessage);
                    }
                } catch (Exception e) {
                    Log.w(TAG, "sendMessage after cancel failed", e);
                }
                try {
                    GroceryNotificationManager.getInstance(context).sendNotification(userId, notifTitle, notifMessage, GroceryNotificationManager.TYPE_ORDER, String.valueOf(orderId));
                } catch (Exception e) {
                    Log.w(TAG, "sendNotification after cancel failed", e);
                }
                try {
                    if (order.getDeliveryPersonId() > 0) {
                        deliveryPersonRepository.releaseDeliveryPerson(order.getDeliveryPersonId());
                    }
                } catch (Exception e) {
                    Log.w(TAG, "releaseDeliveryPerson after cancel failed", e);
                }
            }
            return success;
        } catch (Exception e) {
            lastError = e.toString();
            Log.e(TAG, "Error cancelling order", e);
            return false;
        }
    }

    /** Updates order status and correctly handles stock, payment, and delivery logic based on state transitions. */
    public boolean updateOrderStatus(int orderId, int userId, String status) {
        try {
            // 1. Get current order state before update to compare transitions
            Order oldOrder = dbHelper.getOrderById(orderId);
            if (oldOrder == null) {
                Log.e(TAG, "Cannot update status: Order #" + orderId + " not found");
                return false;
            }

            String oldStatus = oldOrder.getStatus();
            if (status.equalsIgnoreCase(oldStatus)) {
                Log.d(TAG, "Status is already " + status + " for order #" + orderId);
                return true; // No-op but successful
            }

            // 2. Perform the database update
            boolean success = dbHelper.updateOrderStatus(orderId, status);
            if (!success) return false;

            // 3. Centralized Side-Effects Logic
            String paymentMethod = "COD";
            android.database.Cursor paymentCursor = dbHelper.getPaymentByOrderId(orderId);
            if (paymentCursor != null && paymentCursor.moveToFirst()) {
                paymentMethod = paymentCursor.getString(paymentCursor.getColumnIndexOrThrow(com.sunit.groceryplus.DatabaseContract.PaymentEntry.COLUMN_NAME_PAYMENT_METHOD));
                paymentCursor.close();
            }
            boolean isPaidMethod = "Stripe".equalsIgnoreCase(paymentMethod) || "Credit Card".equalsIgnoreCase(paymentMethod);

            // A. Stock Replenishment/Decrement Logic
            boolean isOldStatusFinal = "Cancelled".equalsIgnoreCase(oldStatus) || "Refunded".equalsIgnoreCase(oldStatus);
            boolean isNewStatusFinal = "Cancelled".equalsIgnoreCase(status) || "Refunded".equalsIgnoreCase(status);

            if (!isOldStatusFinal && isNewStatusFinal) {
                // Moving to a final/cancelled state -> Replenish stock
                List<OrderItem> items = dbHelper.getOrderItems(orderId);
                for (OrderItem item : items) {
                    dbHelper.incrementStock(item.getProductId(), item.getQuantity());
                }
            } else if (isOldStatusFinal && !isNewStatusFinal) {
                // Re-activating a previously cancelled/refunded order -> Decrement stock again
                List<OrderItem> items = dbHelper.getOrderItems(orderId);
                for (OrderItem item : items) {
                    dbHelper.decrementStock(item.getProductId(), item.getQuantity());
                }
            }

            // B. Payment Status Sync and WALLET CREDIT PUSH
            if ("Delivered".equalsIgnoreCase(status)) {
                dbHelper.updatePaymentStatusByOrderId(orderId, "Completed");
            } else if (isNewStatusFinal) {
                boolean isAdminRefundAction = "Refunded".equalsIgnoreCase(status);
                dbHelper.updatePaymentStatusByOrderId(orderId, isAdminRefundAction ? "Refunded" : (isPaidMethod ? "Refunded" : "Cancelled"));
                
                // RESTORE Wallet/Points used (Immediate) FOR ALL
                double walletUsed = dbHelper.getWalletDebitForOrder(userId, orderId);
                if (walletUsed > 0) {
                    dbHelper.logTransaction(userId, walletUsed, "credit", "refund", "Restoration of Balance used for Order #" + orderId);
                }
                double pointsUsed = dbHelper.getPointsDebitForOrder(userId, orderId);
                if (pointsUsed > 0) {
                    dbHelper.addLoyaltyPoints(userId, (float)pointsUsed);
                    dbHelper.logTransaction(userId, pointsUsed, "credit", "loyalty_refund", "Restoration of Points used for Order #" + orderId);
                }

                if (isPaidMethod) {
                    // REFUND Stripe amount (Immediate 85% Refund)
                    double cashAmount = oldOrder.getTotalAmount();
                    double refundAmount = isAdminRefundAction ? cashAmount : (cashAmount * 0.85); // 15% cancellation fee deduction only if cancelled
                    
                    dbHelper.logTransaction(userId, refundAmount, "credit", "refund", 
                            (isAdminRefundAction ? "Full Refund for Order #" : "Cancellation Refund for Order #") + orderId + " (Admin Action)");
                    Log.d(TAG, "Admin pushed Instant Stripe refund for Order #" + orderId);
                } else {
                    // COD Logic
                    if (isAdminRefundAction) {
                        // Immediate for COD/Cash only if the admin explicitly marks as Refunded (implying cash was already paid on delivery)
                        double cashAmount = oldOrder.getTotalAmount();
                        dbHelper.logTransaction(userId, cashAmount, "credit", "refund", 
                                "Full Refund for Order #" + orderId);
                        Log.d(TAG, "Admin pushed Instant Cash refund for Order #" + orderId);
                    } else {
                        // Admin cancelled COD before delivery. User has not paid cash yet, so no cash refund.
                        Log.d(TAG, "Admin cancelled COD Order #" + orderId + ". No cash to refund.");
                    }
                }
            }

            // C. Delivery Person Release Logic
            if (isNewStatusFinal && oldOrder.getDeliveryPersonId() > 0) {
                deliveryPersonRepository.releaseDeliveryPerson(oldOrder.getDeliveryPersonId());
            }

            // D. Notifications
            String title = "Order Update";
            String message;
            int adminId = dbHelper.getAdminId();

            if ("Delivered".equalsIgnoreCase(status)) {
                title = "Order Delivered!";
                message = "Great news! Your order #" + orderId + " has been delivered. Enjoy!";
            } else if ("Shipped".equalsIgnoreCase(status)) {
                title = "Order Shipped!";
                message = "Your order #" + orderId + " is on its way!";
            } else if ("Refunded".equalsIgnoreCase(status)) {
                title = "Order Refunded";
                message = "Your order #" + orderId + " has been marked as Refunded by the store.";
                if (isPaidMethod) {
                    message += " A FULL refund was initiated.";
                    if (adminId != -1) {
                        dbHelper.sendMessage(adminId, userId, "Your Order #" + orderId + " has been Refunded. The amount will reflect in 5-7 business days.");
                    }
                }
            } else if ("Cancelled".equalsIgnoreCase(status)) {
                title = "Order Cancelled";
                message = "Your order #" + orderId + " has been cancelled by the store.";
                if (isPaidMethod) {
                    message += " A partial refund (after fee) was initiated.";
                }
            } else {
                message = "Your order #" + orderId + " is now " + status;
            }
            
            GroceryNotificationManager.getInstance(context).sendNotification(userId, title, message, GroceryNotificationManager.TYPE_ORDER, String.valueOf(orderId));
            
            return true;
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

            boolean success = updateOrderStatus(orderId, userId, status);
            if (!success) return false;

            // Handle delivery person logic
            if ("OUT_FOR_DELIVERY".equalsIgnoreCase(status) && order.getDeliveryPersonId() > 0) {
                deliveryPersonRepository.assignOrderToDeliveryPerson(order.getDeliveryPersonId(), orderId);
            } else if (("DELIVERED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) && order.getDeliveryPersonId() > 0) {
                deliveryPersonRepository.releaseDeliveryPerson(order.getDeliveryPersonId());
            }

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
    public boolean recordPayment(int orderId, double amount, String paymentMethod, String transactionId, String status) {
        try {
            long result = dbHelper.addPayment(orderId, amount, paymentMethod, transactionId, status);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error recording payment", e);
            return false;
        }
    }

    // ==================== NEW CUSTOMER ACTIONS ====================

    /** Retrieves the current wallet balance for a user. */
    public double getWalletBalance(int userId) {
        return dbHelper.getWalletBalance(userId);
    }

    /** Requests cancellation of a single item in an order. */
    public boolean requestPartialCancellation(int orderId, int orderItemId, String reason) {
        try {
            // Find the item to get its price
            List<OrderItem> items = dbHelper.getOrderItems(orderId);
            OrderItem targetItem = null;
            for (OrderItem item : items) {
                if (item.getOrderItemId() == orderItemId) {
                    targetItem = item;
                    break;
                }
            }

            if (targetItem == null) return false;

            // Update item status to 'cancelled'
            boolean success = dbHelper.updateOrderItemStatus(orderItemId, "cancelled", targetItem.getSubtotal(), "pending");
            
            if (success) {
                // Replenish stock
                dbHelper.incrementStock(targetItem.getProductId(), targetItem.getQuantity());
                
                // Notify user
                GroceryNotificationManager.getInstance(context).sendNotification(
                        dbHelper.getOrderById(orderId).getUserId(),
                        "Item Cancelled",
                        "Your request to cancel " + targetItem.getProductName() + " is being processed.",
                        GroceryNotificationManager.TYPE_ORDER, String.valueOf(orderId));
                
                // Add a support ticket for historical tracking
                dbHelper.createSupportTicket(dbHelper.getOrderById(orderId).getUserId(), orderId, 
                        "Item Cancellation: " + targetItem.getProductName(), reason, "cancellation", null);
            }
            return success;
        } catch (Exception e) {
            Log.e(TAG, "Error in partial cancellation", e);
            return false;
        }
    }

    /** Reports an issue with a specific item (damaged, expired, etc.). */
    public boolean reportItemIssue(int userId, int orderId, int orderItemId, String issueType, String description, String imageBase64) {
        try {
            // 1. Create a support ticket
            String itemName = "Item #" + orderItemId;
            List<OrderItem> items = dbHelper.getOrderItems(orderId);
            for(OrderItem item : items) {
                if(item.getOrderItemId() == orderItemId) {
                    itemName = item.getProductName();
                    break;
                }
            }
            
            String subject = "Issue: " + issueType + " - " + itemName;
            long ticketId = dbHelper.createSupportTicket(userId, orderId, subject, description, issueType, imageBase64);
            
            if (ticketId != -1) {
                // 2. Update item status to reflect report
                dbHelper.updateOrderItemStatus(orderItemId, "issue_reported", 0.0, "pending");
                
                // 3. Notify user
                GroceryNotificationManager.getInstance(context).sendNotification(
                        userId,
                        "Issue Reported",
                        "We've received your report regarding " + itemName + ". Ticket #" + ticketId + " has been created.",
                        GroceryNotificationManager.TYPE_ORDER, String.valueOf(orderId));
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error reporting item issue", e);
            return false;
        }
    }

    /** Retrieves all support tickets for a user. */
    public List<SupportTicket> getUserTickets(int userId) {
        List<SupportTicket> tickets = new ArrayList<>();
        android.database.Cursor cursor = dbHelper.getUserTickets(userId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                SupportTicket ticket = new SupportTicket();
                ticket.setTicketId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.SupportTicketEntry._ID)));
                ticket.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.SupportTicketEntry.COLUMN_NAME_USER_ID)));
                ticket.setOrderId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.SupportTicketEntry.COLUMN_NAME_ORDER_ID)));
                ticket.setSubject(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.SupportTicketEntry.COLUMN_NAME_SUBJECT)));
                ticket.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.SupportTicketEntry.COLUMN_NAME_DESCRIPTION)));
                ticket.setIssueType(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.SupportTicketEntry.COLUMN_NAME_ISSUE_TYPE)));
                ticket.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.SupportTicketEntry.COLUMN_NAME_STATUS)));
                ticket.setPriority(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.SupportTicketEntry.COLUMN_NAME_PRIORITY)));
                ticket.setIssueImage(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.SupportTicketEntry.COLUMN_NAME_ISSUE_IMAGE)));
                ticket.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.SupportTicketEntry.COLUMN_NAME_CREATED_AT)));
                ticket.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.SupportTicketEntry.COLUMN_NAME_UPDATED_AT)));
                tickets.add(ticket);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return tickets;
    }

    /** Initiates a formal refund request for a specific order item. */
    public long requestRefund(int userId, int orderId, int productId, String reason, String details, String imageBase64) {
        try {
            // 1. Identify Payment Method
            String paymentMethod = "COD";
            android.database.Cursor paymentCursor = dbHelper.getPaymentByOrderId(orderId);
            if (paymentCursor != null && paymentCursor.moveToFirst()) {
                paymentMethod = paymentCursor.getString(paymentCursor.getColumnIndexOrThrow(com.sunit.groceryplus.DatabaseContract.PaymentEntry.COLUMN_NAME_PAYMENT_METHOD));
                paymentCursor.close();
            }

            // 2. Determine Refund Method
            String refundMethod;
            if ("Stripe".equalsIgnoreCase(paymentMethod) || "Credit Card".equalsIgnoreCase(paymentMethod)) {
                refundMethod = "Wallet (Stripe Source)";
            } else {
                refundMethod = "Wallet";
            }

            // 3. Get Item Amount
            double amount = 0.0;
            List<OrderItem> items = dbHelper.getOrderItems(orderId);
            for (OrderItem item : items) {
                if (item.getProductId() == productId) {
                    amount = item.getSubtotal();
                    break;
                }
            }

            // 4. Submit to DB via Helper
            long refundId = dbHelper.submitRefundRequest(userId, orderId, productId, amount, reason, details, refundMethod);
            
            if (refundId != -1) {
                // 5. Notify User
                String title = "Refund Request Received";
                String message = "Your refund request #" + refundId + " for Order #" + orderId + " is pending review. ";
                if ("Wallet".equals(refundMethod)) {
                    message += "Approved amount will be added to your Wallet in 3-5 days.";
                } else {
                    message += "Approved amount will be credited back to your Wallet.";
                }
                
                GroceryNotificationManager.getInstance(context).sendNotification(userId, title, message, GroceryNotificationManager.TYPE_ORDER, String.valueOf(orderId));
                
                // Add support ticket
                dbHelper.createSupportTicket(userId, orderId, "Refund Request: Order #" + orderId, "Reason: " + reason + "\nDetails: " + details, "refund", imageBase64);
            }
            
            return refundId;
        } catch (Exception e) {
            Log.e(TAG, "Error requesting refund", e);
            return -1;
        }
    }

    /** Retrieves all refund requests that are awaiting admin approval. */
    public List<com.sunit.groceryplus.models.Refund> getAllPendingRefunds() {
        try {
            return dbHelper.getPendingRefunds();
        } catch (Exception e) {
            Log.e(TAG, "Error fetching pending refunds", e);
            return new ArrayList<>();
        }
    }

    /** Processes the Administrator's approval of a refund. */
    public boolean processRefundApproval(int refundId, String adminNotes) {
        try {
            // 1. Get Refund Details from DB
            int userId = -1;
            double amount = 0.0;
            String refundMethod = "";
            int orderId = -1;

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            android.database.Cursor cursor = db.query(com.sunit.groceryplus.DatabaseContract.RefundEntry.TABLE_NAME, null,
                    com.sunit.groceryplus.DatabaseContract.RefundEntry.COLUMN_NAME_REFUND_ID + " = ?",
                    new String[]{String.valueOf(refundId)}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(com.sunit.groceryplus.DatabaseContract.RefundEntry.COLUMN_NAME_CUSTOMER_ID));
                amount = cursor.getDouble(cursor.getColumnIndexOrThrow(com.sunit.groceryplus.DatabaseContract.RefundEntry.COLUMN_NAME_REFUND_AMOUNT));
                refundMethod = cursor.getString(cursor.getColumnIndexOrThrow(com.sunit.groceryplus.DatabaseContract.RefundEntry.COLUMN_NAME_REFUND_METHOD));
                orderId = cursor.getInt(cursor.getColumnIndexOrThrow(com.sunit.groceryplus.DatabaseContract.RefundEntry.COLUMN_NAME_ORDER_ID));
                cursor.close();
            }

            if (userId == -1) {
                Log.e(TAG, "Refund not found: " + refundId);
                return false;
            }

            // 2. Perform Credit Logic
            if (refundMethod.contains("Wallet") && !refundMethod.contains("Stripe")) {
                // For Cash/COD refunds - Apply 3-5 day delay as requested
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.add(java.util.Calendar.DAY_OF_YEAR, 4); // Median of 3-5 days
                String availableAt = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(cal.getTime());
                
                dbHelper.logTransaction(userId, amount, "credit", "refund", 
                        "Refund for Order #" + orderId + " (Available in 3-5 days)", "pending", availableAt);
                
                Log.d(TAG, "Scheduled auto-refund for User " + userId + " at " + availableAt);
            } else {
                // Stripe or immediate wallet refund
                dbHelper.logTransaction(userId, amount, "credit", "refund", "Refund for Order #" + orderId);
            }

            // 3. Update Status to Completed in DB
            dbHelper.updateRefundStatus(refundId, "completed", adminNotes);

            // 4. Update Order Item Status for matching order
            android.content.ContentValues itemValues = new android.content.ContentValues();
            itemValues.put(com.sunit.groceryplus.DatabaseContract.OrderItemEntry.COLUMN_NAME_REFUND_STATUS, "processed");
            itemValues.put(com.sunit.groceryplus.DatabaseContract.OrderItemEntry.COLUMN_NAME_ITEM_STATUS, "refunded");
            dbHelper.getWritableDatabase().update(com.sunit.groceryplus.DatabaseContract.OrderItemEntry.TABLE_NAME, itemValues,
                    com.sunit.groceryplus.DatabaseContract.OrderItemEntry.COLUMN_NAME_ORDER_ID + " = ? AND " + 
                    com.sunit.groceryplus.DatabaseContract.OrderItemEntry.COLUMN_NAME_REFUND_STATUS + " = 'pending'",
                    new String[]{String.valueOf(orderId)});

            // 5. Notify User via specialized refund notification
            GroceryNotificationManager.getInstance(context).sendNotification(
                userId, 
                "Refund Approved", 
                "Your refund of Rs. " + String.format("%.2f", amount) + " has been approved via " + refundMethod, 
                GroceryNotificationManager.TYPE_PAYMENT, 
                String.valueOf(orderId)
            );

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error processing refund approval", e);
            return false;
        }
    }

    /** Processes the Administrator's rejection of a refund. */
    public boolean processRefundRejection(int refundId, String rejectionReason) {
        try {
            // 1. Get Refund Details
            int userId = -1;
            int orderId = -1;
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            android.database.Cursor cursor = db.query(com.sunit.groceryplus.DatabaseContract.RefundEntry.TABLE_NAME, 
                    new String[]{com.sunit.groceryplus.DatabaseContract.RefundEntry.COLUMN_NAME_CUSTOMER_ID, com.sunit.groceryplus.DatabaseContract.RefundEntry.COLUMN_NAME_ORDER_ID},
                    com.sunit.groceryplus.DatabaseContract.RefundEntry.COLUMN_NAME_REFUND_ID + " = ?",
                    new String[]{String.valueOf(refundId)}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                userId = cursor.getInt(0);
                orderId = cursor.getInt(1);
                cursor.close();
            }

            if (userId == -1) return false;

            // 2. Update Status to Rejected
            dbHelper.updateRefundStatus(refundId, "rejected", rejectionReason);

            // 3. Revert Order Item Status
            android.content.ContentValues itemValues = new android.content.ContentValues();
            itemValues.put(com.sunit.groceryplus.DatabaseContract.OrderItemEntry.COLUMN_NAME_REFUND_STATUS, "rejected");
            itemValues.put(com.sunit.groceryplus.DatabaseContract.OrderItemEntry.COLUMN_NAME_ITEM_STATUS, "delivered"); // Assuming it was delivered
            dbHelper.getWritableDatabase().update(com.sunit.groceryplus.DatabaseContract.OrderItemEntry.TABLE_NAME, itemValues,
                    com.sunit.groceryplus.DatabaseContract.OrderItemEntry.COLUMN_NAME_ORDER_ID + " = ? AND " + 
                    com.sunit.groceryplus.DatabaseContract.OrderItemEntry.COLUMN_NAME_REFUND_STATUS + " = 'pending'",
                    new String[]{String.valueOf(orderId)});

            // 4. Notify User
            GroceryNotificationManager.getInstance(context).sendNotification(
                userId, 
                "Refund Request Rejected", 
                "Your refund request for Order #" + orderId + " was declined. Reason: " + rejectionReason, 
                GroceryNotificationManager.TYPE_PAYMENT, 
                String.valueOf(orderId)
            );

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error processing refund rejection", e);
            return false;
        }
    }
}
