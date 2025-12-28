package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;
import com.sunit.groceryplus.models.User;

public class DatabaseConnectionTest {
    private static final String TAG = "DatabaseConnectionTest";
    
    public static void testDatabaseConnection(Context context) {
        try {
            // Test 1: Initialize DatabaseHelper
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            Log.d(TAG, "DatabaseHelper initialized successfully");
            
            // Test 2: Test UserRepository
            UserRepository userRepository = new UserRepository(context);
            Log.d(TAG, "UserRepository initialized successfully");
            
            // Test 3: Check if default admin exists
            User adminUser = userRepository.loginUser("admin@gmail.com", "admin123");
            if (adminUser != null) {
                Log.d(TAG, "Default admin user found: " + adminUser.getName() + " (" + adminUser.getUserType() + ")");
            } else {
                Log.e(TAG, "Default admin user not found");
            }
            
            // Test 4: Check if user exists method works
            boolean userExists = userRepository.isUserExists("nonexistent@example.com");
            Log.d(TAG, "Non-existent user check: " + userExists);
            
            Log.d(TAG, "All database connection tests completed successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Database connection test failed", e);
        }
    }
}