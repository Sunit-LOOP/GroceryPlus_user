package com.sunit.groceryplus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.sunit.groceryplus.utils.NotificationHelper;

public class OrderSuccessActivity extends AppCompatActivity {

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable navigateRunnable = () -> {
        navigateToHome();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        // Get user ID from intent
        int userId = getIntent().getIntExtra("user_id", -1);

        // Send notification
        if (userId != -1) {
            NotificationHelper notificationHelper = new NotificationHelper(this);
            notificationHelper.sendNotification(userId, "Order Placed!", "Your order has been successfully placed.");
        }

        // Handle back press with modern dispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Go home immediately on back press instead of waiting or exiting
                handler.removeCallbacks(navigateRunnable);
                navigateToHome();
            }
        });

        // Start timer
        handler.postDelayed(navigateRunnable, 3000);
    }

    private void navigateToHome() {
        int userId = getIntent().getIntExtra("user_id", -1);
        Intent intent = new Intent(OrderSuccessActivity.this, UserHomeActivity.class);
        intent.putExtra("user_id", userId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Prevent navigation if activity is destroyed
        handler.removeCallbacks(navigateRunnable);
    }

}
