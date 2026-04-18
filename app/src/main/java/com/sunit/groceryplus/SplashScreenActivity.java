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
import android.database.sqlite.SQLiteDatabase;
import com.sunit.groceryplus.models.User;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Application entry point for GroceryPlus, handling initialization and navigation. */
public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";
    private static final int SPLASH_DURATION = 3000; // 3 seconds

    private ActivityResultLauncher<String[]> runtimePermissionsLauncher;

    /** Initializes UI, performs system checks, and sets up navigation timer. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        runtimePermissionsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                (Map<String, Boolean> result) -> {
                    for (Map.Entry<String, Boolean> e : result.entrySet()) {
                        Log.d(TAG, "Permission " + e.getKey() + " -> " + e.getValue());
                    }
                });

        requestEssentialRuntimePermissions();

        // Optimized Initialization Flow
        new Thread(() -> {
            try {
                Log.d(TAG, "Starting Database Initialization...");
                DatabaseHelper dbHelper = new DatabaseHelper(SplashScreenActivity.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                DatabaseConnectionTest.testDatabaseConnection(SplashScreenActivity.this);

                dbHelper.insertSampleData(db);
                dbHelper.ensureAllProductsHaveStock(db);

                Log.d(TAG, "Database Initialization Completed Successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error during database initialization", e);
            }
        }).start();

        animateDeliveryGuy();

        new Handler().postDelayed(() -> {
            Log.d(TAG, "Redirecting to LoginActivity");
            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }

    /** Requests notifications, location (maps), camera (product photos), and media read where applicable. */
    private void requestEssentialRuntimePermissions() {
        try {
            List<String> need = new ArrayList<>();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    need.add(android.Manifest.permission.POST_NOTIFICATIONS);
                }
            }

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                need.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                need.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            }

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                need.add(android.Manifest.permission.CAMERA);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {
                    need.add(android.Manifest.permission.READ_MEDIA_IMAGES);
                }
            } else {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    need.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }

            if (!need.isEmpty()) {
                runtimePermissionsLauncher.launch(need.toArray(new String[0]));
            }
        } catch (Exception e) {
            Log.e(TAG, "Permission request setup failed", e);
        }
    }

    private void animateDeliveryGuy() {
        ImageView deliveryGuy = findViewById(R.id.deliveryGuy);
        ObjectAnimator animator = ObjectAnimator.ofFloat(deliveryGuy, "translationX", 0f, 1000f);
        animator.setDuration(SPLASH_DURATION);
        animator.start();
    }
}
