package com.sunit.groceryplus;

import android.os.Bundle;
import android.util.Log;
import com.sunit.groceryplus.models.User;
import com.sunit.groceryplus.network.ApiClient;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize API client with application context
        ApiClient.setContext(this);

        // Test database operations
        testDatabase();
    }

    private void testDatabase() {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            
            // Test authentication with default admin
            User adminUser = dbHelper.authenticateUser("admin@groceryplus.com", "admin123");
            if (adminUser != null) {
                Log.d(TAG, "Default admin authenticated: " + adminUser.getName() + " (" + adminUser.getUserType() + ")");
            } else {
                Log.e(TAG, "Failed to authenticate default admin");
            }
            
            // Check if a regular user exists
            boolean userExists = dbHelper.isUserExists("john@example.com");
            Log.d(TAG, "User 'john@example.com' exists: " + userExists);
            
        } catch (Exception e) {
            Log.e(TAG, "Database test failed", e);
        }
    }
}