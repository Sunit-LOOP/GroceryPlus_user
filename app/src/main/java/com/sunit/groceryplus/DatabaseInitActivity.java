package com.sunit.groceryplus;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.sunit.groceryplus.models.User;

/**
 * Activity to demonstrate database initialization and usage
 */
public class DatabaseInitActivity extends AppCompatActivity {
    private static final String TAG = "DatabaseInitActivity";
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Using existing layout for demo
        
        // Initialize the database helper
        initializeDatabase();
        
        // Test database operations
        testDatabaseOperations();
    }

    /**
     * Initialize the database helper
     */
    private void initializeDatabase() {
        try {
            dbHelper = new DatabaseHelper(this);
            Log.d(TAG, "Database initialized successfully");
            Toast.makeText(this, "Database initialized", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database", e);
            Toast.makeText(this, "Database initialization failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Test various database operations
     */
    private void testDatabaseOperations() {
        if (dbHelper == null) {
            Log.e(TAG, "Database helper not initialized");
            return;
        }

        try {
            // Test 1: Check if default admin exists
            User adminUser = dbHelper.authenticateUser("admin@groceryplus.com", "admin123");
            if (adminUser != null) {
                Log.d(TAG, "Default admin authenticated: " + adminUser.getName() + " (" + adminUser.getUserType() + ")");
                Toast.makeText(this, "Default admin found: " + adminUser.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Log.w(TAG, "Default admin authentication failed");
                Toast.makeText(this, "Default admin authentication failed", Toast.LENGTH_SHORT).show();
            }

            // Test 2: Add a new user
            long userId = dbHelper.addUser("John Doe", "john.doe@example.com", "1234567890", "password123", "customer");
            if (userId != -1) {
                Log.d(TAG, "New user added with ID: " + userId);
                Toast.makeText(this, "User added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Failed to add new user");
                Toast.makeText(this, "Failed to add user", Toast.LENGTH_SHORT).show();
            }

            // Test 3: Authenticate the new user
            User newUser = dbHelper.authenticateUser("john.doe@example.com", "password123");
            if (newUser != null) {
                Log.d(TAG, "New user authenticated: " + newUser.getName() + " (" + newUser.getUserType() + ")");
                Toast.makeText(this, "User authentication successful", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "New user authentication failed");
                Toast.makeText(this, "User authentication failed", Toast.LENGTH_SHORT).show();
            }

            // Test 4: Check if user exists
            boolean userExists = dbHelper.isUserExists("john.doe@example.com");
            Log.d(TAG, "User 'john.doe@example.com' exists: " + userExists);
            
        } catch (Exception e) {
            Log.e(TAG, "Error during database operations", e);
            Toast.makeText(this, "Database operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close database helper if needed (though our implementation handles this)
        if (dbHelper != null) {
            // Note: SQLiteOpenHelper manages database connections automatically
            // We don't need to explicitly close it in most cases
        }
    }
}