package com.sunit.groceryplus.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.NotificationActivity;
import com.sunit.groceryplus.OrderTrackingActivity;
import com.sunit.groceryplus.ProductDetailActivity;
import com.sunit.groceryplus.R;

public class GroceryNotificationManager {

    private static final String CHANNEL_ID = "GroceryPlus_Notifications";
    private static final String CHANNEL_NAME = "Grocery Plus Alerts";
    private static final String CHANNEL_DESC = "Notifications for orders, payments, and account updates";

    public static final String TYPE_ORDER = "ORDER";
    public static final String TYPE_PAYMENT = "PAYMENT";
    public static final String TYPE_ACCOUNT = "ACCOUNT";
    public static final String TYPE_PROMO = "PROMO";
    public static final String TYPE_STOCK = "STOCK";

    private static GroceryNotificationManager instance;
    private Context context;
    private DatabaseHelper dbHelper;

    private GroceryNotificationManager(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = new DatabaseHelper(this.context);
        createNotificationChannel();
    }

    public static synchronized GroceryNotificationManager getInstance(Context context) {
        if (instance == null) {
            instance = new GroceryNotificationManager(context);
        }
        return instance;
    }

    public void sendNotification(int userId, String title, String message, String type, String refId) {
        // 1. Save to Database
        dbHelper.addNotification(userId, title, message, type, refId);

        // 2. Check Preferences
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean enabled = prefs.getBoolean("notifications_enabled", true);
        if (!enabled) return;

        // 3. Prepare Intent
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

        // 4. Build Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications) // Ensure this exists
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // 5. Set Icon based on type
        int iconRes = getIconByType(type);
        builder.setSmallIcon(iconRes);

        // 6. Show Notification
        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private int getIconByType(String type) {
        switch (type) {
            case TYPE_ORDER: return R.drawable.order_icon;
            case TYPE_PAYMENT: return R.drawable.card_icon;
            case TYPE_ACCOUNT: return R.drawable.user_icon;
            case TYPE_PROMO: return R.drawable.promo_icon;
            default: return R.drawable.ic_notifications;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.NotificationManager manager = context.getSystemService(android.app.NotificationManager.class);
            if (manager != null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, android.app.NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription(CHANNEL_DESC);
                manager.createNotificationChannel(channel);
            }
        }
    }
}
