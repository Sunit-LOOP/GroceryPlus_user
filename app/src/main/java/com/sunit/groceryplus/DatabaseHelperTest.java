package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;
import com.sunit.groceryplus.models.User;

/**
 * Test class to demonstrate DatabaseHelper usage
 */
public class DatabaseHelperTest {
    private static final String TAG = "DatabaseHelperTest";
    
    public static void testDatabaseOperations(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        
        // Test adding a new user
        long userId = dbHelper.addUser("John Doe", "john@example.com", "1234567890", "password123", "customer");
        if (userId != -1) {
            Log.d(TAG, "User added successfully with ID: " + userId);
        } else {
            Log.e(TAG, "Failed to add user");
        }
        
        // Test user authentication
        User authenticatedUser = dbHelper.authenticateUser("admin@groceryplus.com", "admin123");
        if (authenticatedUser != null) {
            Log.d(TAG, "Admin authentication successful: " + authenticatedUser.getName());
        } else {
            Log.e(TAG, "Admin authentication failed");
        }
        
        // Test checking if user exists
        boolean exists = dbHelper.isUserExists("john@example.com");
        Log.d(TAG, "User exists: " + exists);
        
        // Test getting user by email
        User user = dbHelper.getUserByEmail("john@example.com");
        if (user != null) {
            Log.d(TAG, "Retrieved user: " + user.getName());
        } else {
            Log.e(TAG, "Failed to retrieve user");
        }
    }
}