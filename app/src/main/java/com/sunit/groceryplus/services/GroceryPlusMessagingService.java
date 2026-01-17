package com.sunit.groceryplus.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.utils.GroceryNotificationManager;

import java.util.Map;

public class GroceryPlusMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "groceryplus_notifications";
    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;
    
    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences("groceryplus_prefs", MODE_PRIVATE);
        createNotificationChannel();
    }
    
    /** Creates notification channel for Android O+ devices. */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "GroceryPlus Notifications",
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for orders, deliveries, and updates");
            channel.enableLights(true);
            channel.enableVibration(true);
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            handleDataMessage(remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Message Notification Body: " + body);
            
            // If data is empty, showing basic notification
            if (remoteMessage.getData().isEmpty()) {
                GroceryNotificationManager.getInstance(this).sendNotification(
                    -1, // Default user or broad notification
                    title != null ? title : "GroceryPlus",
                    body != null ? body : "",
                    GroceryNotificationManager.TYPE_ACCOUNT, // Default category
                    null
                );
            }
        }
    }

    private void handleDataMessage(Map<String, String> data) {
        String title = data.get("title");
        String message = data.get("message");
        String type = data.get("type");
        String refId = data.get("refId");
        String userIdStr = data.get("userId");
        String orderIdStr = data.get("orderId");
        String status = data.get("status");

        int userId = -1;
        try {
            if (userIdStr != null) userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid userId in FCM data", e);
        }

        // Handle different notification types
        switch (type != null ? type : "general") {
            case "order_update":
                handleOrderUpdate(orderIdStr, status, title, message, userId);
                break;
            case "delivery_update":
                handleDeliveryUpdate(orderIdStr, status, title, message, userId);
                break;
            case "payment_update":
                handlePaymentUpdate(refId, title, message, userId);
                break;
            case "promotion":
                handlePromotion(title, message, userId);
                break;
            default:
                handleGeneralNotification(title, message, userId, type, refId);
                break;
        }
    }
    
    /** Handles order status update notifications. */
    private void handleOrderUpdate(String orderIdStr, String status, String title, String message, int userId) {
        try {
            if (orderIdStr != null) {
                int orderId = Integer.parseInt(orderIdStr);
                // Update order status in local database
                dbHelper.updateOrderStatus(orderId, status);
                
                // Send notification with proper priority
                GroceryNotificationManager.getInstance(this).sendNotification(
                    userId,
                    title != null ? title : "Order Update",
                    message != null ? message : "Your order status has been updated",
                    GroceryNotificationManager.TYPE_ORDER,
                    orderIdStr
                );
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid orderId in order update", e);
        }
    }
    
    /** Handles delivery status update notifications. */
    private void handleDeliveryUpdate(String orderIdStr, String status, String title, String message, int userId) {
        try {
            if (orderIdStr != null) {
                int orderId = Integer.parseInt(orderIdStr);
                // Update delivery status in local database
                dbHelper.updateOrderStatus(orderId, status);
                
                // High priority notification for delivery updates
                GroceryNotificationManager.getInstance(this).sendNotification(
                    userId,
                    title != null ? title : "Delivery Update",
                    message != null ? message : "Your delivery status has been updated",
                    GroceryNotificationManager.TYPE_ORDER,
                    orderIdStr
                );
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid orderId in delivery update", e);
        }
    }
    
    /** Handles payment status update notifications. */
    private void handlePaymentUpdate(String paymentId, String title, String message, int userId) {
        GroceryNotificationManager.getInstance(this).sendNotification(
            userId,
            title != null ? title : "Payment Update",
            message != null ? message : "Your payment status has been updated",
            GroceryNotificationManager.TYPE_PAYMENT,
            paymentId
        );
    }
    
    /** Handles promotional notifications. */
    private void handlePromotion(String title, String message, int userId) {
        GroceryNotificationManager.getInstance(this).sendNotification(
            userId,
            title != null ? title : "Special Offer",
            message != null ? message : "Check out our latest promotions",
            GroceryNotificationManager.TYPE_ACCOUNT,
            null
        );
    }
    
    /** Handles general notifications. */
    private void handleGeneralNotification(String title, String message, int userId, String type, String refId) {
        GroceryNotificationManager.getInstance(this).sendNotification(
            userId,
            title != null ? title : "GroceryPlus",
            message != null ? message : "",
            type != null ? type : GroceryNotificationManager.TYPE_ACCOUNT,
            refId
        );
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        
        // Save token locally
        prefs.edit().putString("fcm_token", token).apply();
        
        // Send token to server for current user
        int currentUserId = prefs.getInt("current_user_id", -1);
        if (currentUserId != -1) {
            sendTokenToServer(token, currentUserId);
        }
    }
    
    /** Sends FCM token to server for the current user. */
    private void sendTokenToServer(String token, int userId) {
        // Implementation would depend on your backend API
        // This is a placeholder for server communication
        try {
            // Example: Send to your backend
            // YourApiService.getInstance().updateFcmToken(userId, token);
            Log.d(TAG, "Token sent to server for user: " + userId);
        } catch (Exception e) {
            Log.e(TAG, "Failed to send token to server", e);
        }
    }
    
    /** Registers FCM token for a user after login. */
    public static void registerTokenForUser(android.content.Context context, int userId) {
        SharedPreferences prefs = context.getSharedPreferences("groceryplus_prefs", MODE_PRIVATE);
        String token = prefs.getString("fcm_token", null);
        
        if (token != null) {
            // Send to server
            try {
                // YourApiService.getInstance().updateFcmToken(userId, token);
                Log.d("FCMService", "Token registered for user: " + userId);
            } catch (Exception e) {
                Log.e("FCMService", "Failed to register token", e);
            }
        }
    }
    
    /** Unregisters FCM token when user logs out. */
    public static void unregisterToken(android.content.Context context, int userId) {
        try {
            // YourApiService.getInstance().removeFcmToken(userId);
            Log.d("FCMService", "Token unregistered for user: " + userId);
        } catch (Exception e) {
            Log.e("FCMService", "Failed to unregister token", e);
        }
    }
}
