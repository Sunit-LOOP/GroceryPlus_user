package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;
import com.sunit.groceryplus.models.User;

/** Utility class for demonstrating and testing various DatabaseHelper operations. */
public class DatabaseHelperTest {
    // Infrastructure
    private static final String TAG = "DatabaseHelperTest";
    
    /** Executes a suite of database operations to verify correctness of DatabaseHelper methods. */
    public static void testDatabaseOperations(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        
        // ... (existing tests) ...
        
        // Test adding a new user
        long userId = dbHelper.addUser("Ram", "ram@gmail.com", "1234567890", "password123", "customer");
        if (userId != -1) {
            Log.d(TAG, "User added successfully with ID: " + userId);
        } else {
            Log.e(TAG, "Failed to add user");
        }
        
        // Test user authentication
        User authenticatedUser = dbHelper.authenticateUser("admin@gmail.com", "admin123");
        if (authenticatedUser != null) {
            Log.d(TAG, "Admin authentication successful: " + authenticatedUser.getName());
        } else {
            Log.e(TAG, "Admin authentication failed");
        }
        
        // Test checking if user exists
        boolean exists = dbHelper.isUserExists("ram@gmail.com");
        Log.d(TAG, "User exists: " + exists);
        
        // Test getting user by email
        User user = dbHelper.getUserByEmail("ram@gmail.com");
        if (user != null) {
            Log.d(TAG, "Retrieved user: " + user.getName());
        } else {
            Log.e(TAG, "Failed to retrieve user");
        }
    }

    /** Executes analytics-related tests. */
    public static void testAnalyticsOperations(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        Log.d(TAG, "Starting Analytics Tests...");

        // 1. Total Revenue
        double revenue = dbHelper.getTotalRevenue();
        Log.d(TAG, "Total Revenue: " + revenue);

        // 2. Total Orders
        int totalOrders = dbHelper.getTotalOrdersCount();
        Log.d(TAG, "Total Orders: " + totalOrders);

        // 3. Order Status Counts
        int deliveredInfo = dbHelper.getOrderCountByStatus("Delivered");
        Log.d(TAG, "Delivered Orders: " + deliveredInfo);
        int pendingInfo = dbHelper.getOrderCountByStatus("Pending");
        Log.d(TAG, "Pending Orders: " + pendingInfo);

        // 4. Time-based Revenue (Smoke Test)
        double todayRevenue = dbHelper.getTodayRevenue();
        Log.d(TAG, "Today's Revenue: " + todayRevenue);
        
        // Use current month/year for testing
        java.util.Calendar cal = java.util.Calendar.getInstance();
        double monthRevenue = dbHelper.getMonthRevenue(cal.get(java.util.Calendar.MONTH) + 1, cal.get(java.util.Calendar.YEAR));
        Log.d(TAG, "This Month's Revenue: " + monthRevenue);

        Log.d(TAG, "Analytics Tests Completed");
    }
}