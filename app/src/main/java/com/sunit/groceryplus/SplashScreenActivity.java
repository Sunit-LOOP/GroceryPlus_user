package com.sunit.groceryplus;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


/**
 * SplashScreenActivity - Application entry point with branding and initialization
 * 
 * This activity serves as the launch screen for the GroceryPlus application.
 * It displays the brand logo with animations, performs initial system checks,
 * initializes the database with sample data if needed, and handles navigation
 * to the login screen.
 * 
 * Key Features:
 * - Animated delivery branding
 * - Database connection testing
 * - Sample data insertion on first run
 * - Permission handling for notifications (Android 13+)
 * - Timed transition to LoginActivity
 * 
 * Initialization Flow:
 * 1. Request necessary permissions (Post Notifications)
 * 2. Test database connectivity
 * 3. Seed database with sample products/users if empty
 * 4. Animate branding elements
 * 5. Navigate to LoginActivity after delay
 * 
 * @author GroceryPlus Development Team
 * @version 1.0
 * @since 1.0
 */
public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";
    private static final int SPLASH_DURATION = 3000; // 3 seconds

    private ActivityResultLauncher<String> postNotificationsPermissionLauncher;

    /**
     * Called when the activity is first created
     * 
     * Initializes the splash screen UI, registers permission launchers,
     * triggers database startup tasks, starts animations, and schedules
     * the navigation to the next screen.
     *
     * @param savedInstanceState Previously saved state data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Register permission launcher for notification permission
        postNotificationsPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> Log.d(TAG, "POST_NOTIFICATIONS granted: " + isGranted)
        );

        // Request notification permission for Android 13+
        requestPostNotificationsIfNeeded();

        // Test database connection to ensure system readiness
        DatabaseConnectionTest.testDatabaseConnection(this);

        // Insert sample data on first run
        // This ensures the app has content for demonstration purposes
        Log.d(TAG, "Inserting sample data");
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.insertSampleData();
        dbHelper.ensureAllProductsHaveStock(); // Ensure all products have stock
        Log.d(TAG, "Sample data insertion completed");
        
        // Start the delivery guy animation
        animateDeliveryGuy();

        // usage of Handler to delay transition to LoginActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Redirecting to LoginActivity");
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Prevent user from returning to splash screen
            }
        }, SPLASH_DURATION);
    }

    private void animateDeliveryGuy() {
        ImageView deliveryGuy = findViewById(R.id.deliveryGuy);
        ObjectAnimator animator = ObjectAnimator.ofFloat(deliveryGuy, "translationX", 0f, 1000f);
        animator.setDuration(SPLASH_DURATION);
        animator.start();
    }

    private void requestPostNotificationsIfNeeded() {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                return;
            }

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                return;
            }

            if (postNotificationsPermissionLauncher != null) {
                postNotificationsPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to request POST_NOTIFICATIONS", e);
        }
    }
}