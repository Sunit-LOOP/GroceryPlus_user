package com.sunit.groceryplus;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";
    private static final int SPLASH_DURATION = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Test database connection
        DatabaseConnectionTest.testDatabaseConnection(this);

        // Insert sample data on first run
        Log.d(TAG, "Inserting sample data");
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.insertSampleData();
        dbHelper.ensureAllProductsHaveStock(); // Ensure all products have stock
        Log.d(TAG, "Sample data insertion completed");
        
        // Animate the delivery guy
        animateDeliveryGuy();

        // Move to login screen after delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Redirecting to LoginActivity");
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DURATION);
    }

    private void animateDeliveryGuy() {
        ImageView deliveryGuy = findViewById(R.id.deliveryGuy);
        ObjectAnimator animator = ObjectAnimator.ofFloat(deliveryGuy, "translationX", 0f, 1000f);
        animator.setDuration(SPLASH_DURATION);
        animator.start();
    }
}