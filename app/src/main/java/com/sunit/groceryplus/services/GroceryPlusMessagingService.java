package com.sunit.groceryplus.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sunit.groceryplus.utils.GroceryNotificationManager;

import java.util.Map;

public class GroceryPlusMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";

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

        int userId = -1;
        try {
            if (userIdStr != null) userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid userId in FCM data", e);
        }

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
        // If we had a server-side user mapping, we would send this token to the backend
    }
}
