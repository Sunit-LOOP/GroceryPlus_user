package com.sunit.groceryplus.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.NotificationActivity;
import com.sunit.groceryplus.OrderTrackingActivity;
import com.sunit.groceryplus.ProductDetailActivity;
import com.sunit.groceryplus.R;

/** Centralized manager for handling local and Firebase notifications across multiple categories (Orders, Payments, Promo, etc.). */
public class GroceryNotificationManager {

    // Notification channel configuration
    private static final String CHANNEL_ID = "GroceryPlus_Notifications";
    private static final String CHANNEL_NAME = "Grocery Plus Alerts";
    private static final String CHANNEL_DESC = "Notifications for orders, payments, and account updates";

    // Notification types
    public static final String TYPE_ORDER = "ORDER";
    public static final String TYPE_PAYMENT = "PAYMENT";
    public static final String TYPE_ACCOUNT = "ACCOUNT";
    public static final String TYPE_PROMO = "PROMO";
    public static final String TYPE_STOCK = "STOCK";
    public static final String TYPE_DELIVERY = "DELIVERY";
    public static final String TYPE_VENDOR = "VENDOR";
    public static final String TYPE_REVIEW = "REVIEW";
    public static final String TYPE_CART = "CART";
    public static final String TYPE_SYSTEM = "SYSTEM";
    public static final String TYPE_REFUND = "REFUND";

    // Infrastructure
    private static GroceryNotificationManager instance;
    private Context context;
    private DatabaseHelper dbHelper;

    /** Private constructor initializing the database helper and notification channels. */
    private GroceryNotificationManager(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = new DatabaseHelper(this.context);
        createNotificationChannel();
    }

    /** Returns the singleton instance of the GroceryNotificationManager. */
    public static synchronized GroceryNotificationManager getInstance(Context context) {
        if (instance == null) {
            instance = new GroceryNotificationManager(context);
        }
        return instance;
    }

    /** Dispatches a standard notification based on user preferences and permission status. */
    public void sendNotification(int userId, String title, String message, String type, String refId) {
        Log.d("GroceryNotificationManager", "Attempting to send notification: " + title + " - " + message);
        
        // Check notification permission for Android 13+ (API 33)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                Log.w("GroceryNotificationManager", "POST_NOTIFICATIONS permission not granted");
                return;
            }
        }
        
        // Step 1: Save notification to database for history
        dbHelper.addNotification(userId, title, message, type, refId);

        // Step 2: Check user notification preferences
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean enabled = prefs.getBoolean("notifications_enabled", true);
        if (!enabled) {
            Log.d("GroceryNotificationManager", "Notifications disabled in preferences");
            return;
        }

        // Step 3: Prepare intent for notification click action
        Intent intent;
        if (TYPE_ORDER.equals(type) && refId != null) {
            // Route to order tracking for order notifications
            intent = new Intent(context, OrderTrackingActivity.class);
            intent.putExtra("order_id", Integer.parseInt(refId));
        } else if (TYPE_DELIVERY.equals(type) && refId != null) {
            // Route to order tracking for delivery updates
            intent = new Intent(context, OrderTrackingActivity.class);
            intent.putExtra("order_id", Integer.parseInt(refId));
        } else if (TYPE_STOCK.equals(type) && refId != null) {
            // Route to product details for stock alerts
            intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", Integer.parseInt(refId));
        } else if (TYPE_VENDOR.equals(type) && refId != null) {
            // Route to notification activity for vendor updates
            intent = new Intent(context, NotificationActivity.class);
            intent.putExtra("user_id", userId);
        } else if (TYPE_REVIEW.equals(type) && refId != null) {
            // Route to product details for review requests
            intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", Integer.parseInt(refId));
        } else if (TYPE_CART.equals(type)) {
            // Route to home for cart updates
            intent = new Intent(context, com.sunit.groceryplus.UserHomeActivity.class);
            intent.putExtra("user_id", userId);
        } else {
            // Default route to notification activity
            intent = new Intent(context, NotificationActivity.class);
            intent.putExtra("user_id", userId);
        }
        
        // Set intent flags for proper activity stack management
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Create pending intent for notification click
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), 
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Step 4: Build notification with basic properties
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications) // Default icon, will be updated based on type
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message)) // Expandable text
                .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority for visibility
                .setContentIntent(pendingIntent) // Click action
                .setAutoCancel(true); // Auto-dismiss on click

        // Step 5: Set icon based on notification type
        int iconRes = getIconByType(type);
        builder.setSmallIcon(iconRes);

        // Step 6: Display notification to user
        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify((int) System.currentTimeMillis(), builder.build());
                Log.d("GroceryNotificationManager", "Notification sent successfully");
            }
        } catch (SecurityException e) {
            Log.e("GroceryNotificationManager", "SecurityException when sending notification", e);
        } catch (Exception e) {
            Log.e("GroceryNotificationManager", "Error sending notification", e);
        }
    }

    /** Immediately sends a notification bypassing user preference checks (used for testing or critical alerts). */
    public void forceSendNotification(int userId, String title, String message, String type, String refId) {
        Log.d("GroceryNotificationManager", "Force sending notification: " + title + " - " + message);
        
        // Check notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                Log.w("GroceryNotificationManager", "POST_NOTIFICATIONS permission not granted");
                return;
            }
        }
        
        // Prepare intent for notification click action
        Intent intent;
        if (TYPE_ORDER.equals(type) && refId != null) {
            intent = new Intent(context, OrderTrackingActivity.class);
            intent.putExtra("order_id", Integer.parseInt(refId));
        } else if (TYPE_STOCK.equals(type) && refId != null) {
            intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", Integer.parseInt(refId));
        } else {
            intent = new Intent(context, NotificationActivity.class);
            intent.putExtra("user_id", userId);
        }
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), 
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Set icon based on type
        int iconRes = getIconByType(type);
        builder.setSmallIcon(iconRes);

        // Show notification
        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify((int) System.currentTimeMillis(), builder.build());
                Log.d("GroceryNotificationManager", "Force notification sent successfully");
            }
        } catch (SecurityException e) {
            Log.e("GroceryNotificationManager", "SecurityException when sending force notification", e);
        } catch (Exception e) {
            Log.e("GroceryNotificationManager", "Error sending force notification", e);
        }
    }

    /** Static test method to verify that the notification system is working correctly. */
    public static void testNotification(Context context, int userId) {
        GroceryNotificationManager manager = getInstance(context);
        manager.forceSendNotification(userId, "Test Notification", "This is a test notification to verify the system works.", TYPE_ORDER, "test_" + System.currentTimeMillis());
    }

    // ==================== CONVENIENCE METHODS ====================
    // These methods provide easy-to-use interfaces for specific notification types

    /** Sends a delivery status update notification for a specific order. */
    public void sendDeliveryNotification(int userId, int orderId, String status) {
        String title = "Delivery Update";
        String message = "Your order #" + orderId + " delivery status: " + status;
        sendNotification(userId, title, message, TYPE_DELIVERY, String.valueOf(orderId));
    }

    /** Sends a notification regarding updates from a vendor. */
    public void sendVendorNotification(int userId, String message) {
        String title = "Vendor Update";
        sendNotification(userId, title, message, TYPE_VENDOR, null);
    }

    /** Sends a request for the user to review a purchased product. */
    public void sendReviewNotification(int userId, int productId, String productName) {
        String title = "Review Request";
        String message = "How was your experience with " + productName + "? Leave a review!";
        sendNotification(userId, title, message, TYPE_REVIEW, String.valueOf(productId));
    }

    /** Sends a notification regarding shopping cart updates. */
    public void sendCartNotification(int userId, String message) {
        String title = "Cart Update";
        sendNotification(userId, title, message, TYPE_CART, null);
    }

    /** Sends a high-level system update notification. */
    public void sendSystemNotification(int userId, String message) {
        String title = "System Update";
        sendNotification(userId, title, message, TYPE_SYSTEM, null);
    }

    /** Sends a critical low stock notification for a product. */
    public void sendLowStockNotification(int userId, String productName) {
        String title = "Low Stock Alert";
        String message = productName + " is running low in stock. Order soon!";
        sendNotification(userId, title, message, TYPE_STOCK, null);
    }

    /** Sends a promotional offer or discount notification to the user. */
    public void sendPromoNotification(int userId, String promoCode, String discount) {
        String title = "Special Offer!";
        String message = "Use code " + promoCode + " to get " + discount + " off on your next order!";
        sendNotification(userId, title, message, TYPE_PROMO, promoCode);
    }

    /** Sends a notification regarding a refund status update. */
    public void sendRefundNotification(int userId, int orderId, double amount, String status, String method) {
        String title = "Refund " + status.substring(0, 1).toUpperCase() + status.substring(1);
        String message;
        if ("approved".equalsIgnoreCase(status) || "completed".equalsIgnoreCase(status)) {
            message = "Your refund of Rs. " + String.format("%.2f", amount) + " has been processed via " + method + ".";
        } else if ("rejected".equalsIgnoreCase(status)) {
            message = "Your refund request for Order #" + orderId + " was declined. Please contact support for details.";
        } else {
            message = "Your refund request for Order #" + orderId + " is now " + status + ".";
        }
        sendNotification(userId, title, message, TYPE_REFUND, String.valueOf(orderId));
    }

    // ==================== HELPER METHODS ====================

    /** Returns the appropriate icon resource based on the specific notification type. */
    private int getIconByType(String type) {
        switch (type) {
            case TYPE_ORDER: return R.drawable.order_icon;
            case TYPE_PAYMENT: return R.drawable.card_icon;
            case TYPE_ACCOUNT: return R.drawable.user_icon;
            case TYPE_PROMO: return R.drawable.promo_icon;
            case TYPE_DELIVERY: return R.drawable.delivery_truck_icon;
            case TYPE_VENDOR: return R.drawable.vendor_icon;
            case TYPE_REVIEW: return R.drawable.review_icon;
            case TYPE_CART: return R.drawable.cart_icon;
            case TYPE_STOCK: return R.drawable.product_icon;
            case TYPE_SYSTEM: return R.drawable.settings_icon;
            case TYPE_REFUND: return R.drawable.card_icon; // Using card icon for refunds
            default: return R.drawable.ic_notifications;
        }
    }

    /** Creates the mandatory notification channel for Android O (API 26) and above. */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.NotificationManager manager = context.getSystemService(android.app.NotificationManager.class);
            if (manager != null) {
                // Create notification channel with high importance
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, android.app.NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription(CHANNEL_DESC);
                manager.createNotificationChannel(channel);
            }
        }
    }
}
