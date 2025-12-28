package com.sunit.groceryplus.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.R;

public class NotificationHelper {

    private static final String CHANNEL_ID = "grocery_plus_channel";
    private static final String CHANNEL_NAME = "GroceryPlus Notifications";
    private static final String CHANNEL_DESC = "Notifications for order status and other updates";

    private Context context;
    private NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification(int userId, String title, String message) {
        // Save notification to database
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.addNotification(userId, title, message);

        // Check if notifications are enabled
        android.content.SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean notificationsEnabled = prefs.getBoolean("notifications_enabled", true);

        if (!notificationsEnabled) {
            return; // Don't show system notification if disabled
        }

        // Build and display system notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
