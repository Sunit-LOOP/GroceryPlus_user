package com.sunit.groceryplus;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.sunit.groceryplus.models.User;

/** DatabaseInitActivity - Utility activity to verify database initialization and run core operation tests. */
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

    /** Initializes the DatabaseHelper instance. */
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

    /** Executes a sequence of tests including user authentication and registration. */
    private void testDatabaseOperations() {
        if (dbHelper == null) {
            Log.e(TAG, "Database helper not initialized");
            return;
        }

        try {
            // Test 1: Check if default admin exists
            User adminUser = dbHelper.authenticateUser("admin@gmail.com", "admin123");
            if (adminUser != null) {
                Log.d(TAG, "Default admin authenticated: " + adminUser.getName() + " (" + adminUser.getUserType() + ")");
                Toast.makeText(this, "Default admin found: " + adminUser.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Log.w(TAG, "Default admin authentication failed");
                Toast.makeText(this, "Default admin authentication failed", Toast.LENGTH_SHORT).show();
            }

            // Test 2: Add a new user
            long userId = dbHelper.addUser("Ram", "ram@gmail.com", "9876543210", "123456", "customer");
            if (userId != -1) {
                Log.d(TAG, "Test user added with ID: " + userId);
                Toast.makeText(this, "Test user added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Failed to add test user");
                Toast.makeText(this, "Failed to add test user", Toast.LENGTH_SHORT).show();
            }

            // Test 3: Authenticate the new user
            User newUser = dbHelper.authenticateUser("ram@gmail.com", "123456");
            if (newUser != null) {
                Log.d(TAG, "New user authenticated: " + newUser.getName() + " (" + newUser.getUserType() + ")");
                Toast.makeText(this, "User authentication successful", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "New user authentication failed");
                Toast.makeText(this, "User authentication failed", Toast.LENGTH_SHORT).show();
            }

            // Test 4: Check if user exists
            boolean userExists = dbHelper.isUserExists("ram@gmail.com");
            Log.d(TAG, "User 'ram@gmail.com' exists: " + userExists);

        } catch (Exception e) {
            Log.e(TAG, "Database test failed", e);
            Toast.makeText(this, "Database test failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
