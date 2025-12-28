package com.sunit.groceryplus.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.sunit.groceryplus.R;
import com.sunit.groceryplus.NotificationActivity;

public class NotificationUtils {

    public static final String CHANNEL_ID = "GroceryPlusNotificationChannel";
    private static final String CHANNEL_NAME = "Grocery Plus Notifications";
    private static final String CHANNEL_DESC = "Notifications for orders and updates";

    public static void showNotification(Context context, String title, String message) {
        // Check if notifications are enabled
        android.content.SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean enabled = prefs.getBoolean("notifications_enabled", true);
        if (!enabled) {
            return;
        }

        createNotificationChannel(context);

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 
                PendingIntent.FLAG_IMMUTABLE); // FLAG_IMMUTABLE is required for Android 12+

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_bell_icon) // Use the icon we updated
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            // Use a unique ID based on time to allow stacking, or fixed ID to overwrite
            int notificationId = (int) System.currentTimeMillis(); 
            notificationManager.notify(notificationId, builder.build());
        } catch (SecurityException e) {
            // Android 13+ requires POST_NOTIFICATIONS permission, handling basic case
            e.printStackTrace();
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESC);
            
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
