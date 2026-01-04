package com.sunit.groceryplus.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.sunit.groceryplus.CartActivity;
import com.sunit.groceryplus.MessageActivity;
import com.sunit.groceryplus.OrderHistoryActivity;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.UserDetailViewActivity;
import com.sunit.groceryplus.UserHomeActivity;
import com.sunit.groceryplus.UserProfileActivity;

/**
 * NavigationHelper - Utility class for handling navigation between activities.
 * 
 * This class provides centralized navigation management for the GroceryPlus app.
 * It handles bottom navigation bar interactions and provides consistent navigation
 * behavior across all user-facing activities.
 * 
 * Key Features:
 * - Centralized navigation setup
 * - User session validation
 * - Navigation logging for debugging
 * - Duplicate navigation prevention
 * - Error handling with user feedback
 */
public class NavigationHelper {

    /**
     * Sets up navigation click listeners for the bottom navigation bar.
     * 
     * This method initializes click listeners for all navigation items in the bottom bar.
     * It validates the presence of navigation views and sets up appropriate navigation
     * actions for each item.
     * 
     * @param activity The current activity where navigation is being set up
     * @param userId The current user's ID for session management and navigation
     */
    public static void setupNavigation(Activity activity, int userId) {
        // Find all navigation view elements in the bottom navigation bar
        LinearLayout navHome = activity.findViewById(R.id.navHome);
        LinearLayout navMessage = activity.findViewById(R.id.navMessage);
        LinearLayout navHistory = activity.findViewById(R.id.navHistory);
        LinearLayout navCart = activity.findViewById(R.id.navCart);
        LinearLayout navProfile = activity.findViewById(R.id.navProfile); // Note: Layout ID might be navProfile or navUser based on inconsistent naming, checking layouts first is safer

        // Set up click listener for Home navigation
        if (navHome != null) {
            navHome.setOnClickListener(v -> navigateTo(activity, UserHomeActivity.class, userId));
        }

        // Set up click listener for Messages navigation
        if (navMessage != null) {
            navMessage.setOnClickListener(v -> navigateTo(activity, MessageActivity.class, userId));
        }

        // Set up click listener for Order History navigation
        if (navHistory != null) {
            navHistory.setOnClickListener(v -> navigateTo(activity, OrderHistoryActivity.class, userId));
        }

        // Set up click listener for Shopping Cart navigation
        if (navCart != null) {
            navCart.setOnClickListener(v -> navigateTo(activity, CartActivity.class, userId));
        }

        // Set up click listener for User Profile navigation
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> navigateTo(activity, UserDetailViewActivity.class, userId));
        }
    }

    /**
     * Handles navigation between activities with proper validation and logging.
     * 
     * This method provides centralized navigation logic with comprehensive error handling,
     * session validation, and navigation logging. It prevents duplicate navigation
     * and ensures user session integrity.
     * 
     * @param currentActivity The activity from which navigation is initiated
     * @param targetActivityClass The target activity class to navigate to
     * @param userId The current user's ID for session validation
     */
    private static void navigateTo(Activity currentActivity, Class<?> targetActivityClass, int userId) {
        // Tag for logging purposes
        String TAG = "NavigationHelper";
        
        // Get activity names for logging
        String currentActivityName = currentActivity.getClass().getSimpleName();
        String targetActivityName = targetActivityClass.getSimpleName();
        
        // Log navigation request details for debugging
        Log.d(TAG, "=== NAVIGATION REQUEST ===");
        Log.d(TAG, "From: " + currentActivityName);
        Log.d(TAG, "To: " + targetActivityName);
        Log.d(TAG, "User ID: " + userId);
        
        // Prevent duplicate navigation - if already on target screen, skip navigation
        if (currentActivity.getClass().equals(targetActivityClass)) {
            Log.d(TAG, "Already on target screen (" + targetActivityName + "), skipping navigation");
            return;
        }

        // Validate user session - prevent navigation with invalid user ID
        if (userId == -1) {
            Log.e(TAG, "Invalid userId (-1), cannot navigate");
            android.widget.Toast.makeText(currentActivity, "Session expired. Please login again.", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Create intent for navigation with user ID
            Intent intent = new Intent(currentActivity, targetActivityClass);
            intent.putExtra("user_id", userId);
            
            // Set intent flags for proper activity stack management
            // CLEAR_TOP: Bring target activity to front if it exists, clearing activities above it
            // SINGLE_TOP: Prevent creating new instance if it's already on top
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            
            // Log intent creation and navigation initiation
            Log.d(TAG, "Intent created with user_id: " + userId);
            Log.d(TAG, "Starting " + targetActivityName + "...");
            
            // Execute navigation
            currentActivity.startActivity(intent);
            Log.d(TAG, "Navigation initiated successfully");
            
        } catch (Exception e) {
            // Handle navigation errors gracefully
            Log.e(TAG, "Error navigating to " + targetActivityName, e);
            android.widget.Toast.makeText(currentActivity, "Navigation error", android.widget.Toast.LENGTH_SHORT).show();
        }
    }
}
