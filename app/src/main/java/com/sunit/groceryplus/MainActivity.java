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

/** MainActivity - Entry point for system verification and API initialization. */
public class MainActivity extends AppCompatActivity {
    
    // Tag for logging and debugging
    private static final String TAG = "MainActivity";

    /** Initializes EdgeToEdge display, network clients, and performs diagnostics. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Enable EdgeToEdge display for modern full-screen experience
        EdgeToEdge.enable(this);
        
        // Set the main layout content
        setContentView(R.layout.activity_main);
        
        // Apply window insets for system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize API client with application context for network operations
        ApiClient.setContext(this);

        // Perform database testing to verify system readiness
        testDatabase();
    }

    /** Verifies database connectivity and default account integrity. */
    private void testDatabase() {
        try {
            // Initialize database helper for testing
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            
            // Test authentication with default admin account
            // This verifies that the default admin user is properly created
            // and can authenticate with the correct credentials
            User adminUser = dbHelper.authenticateUser("admin@gmail.com", "admin123");
            if (adminUser != null) {
                Log.d(TAG, "Default admin authenticated: " + adminUser.getName() + " (" + adminUser.getUserType() + ")");
            } else {
                Log.e(TAG, "Failed to authenticate default admin");
            }
            
            // Test user existence check for sample user account
            // This verifies that user data is properly stored and retrievable
            boolean userExists = dbHelper.isUserExists("ram@gmail.com");
            Log.d(TAG, "User 'ram@gmail.com' exists: " + userExists);
            
        } catch (Exception e) {
            // Log any database errors for debugging purposes
            Log.e(TAG, "Database test failed", e);
        }
    }
}