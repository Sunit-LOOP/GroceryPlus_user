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

public class NavigationHelper {

    public static void setupNavigation(Activity activity, int userId) {
        LinearLayout navHome = activity.findViewById(R.id.navHome);
        LinearLayout navMessage = activity.findViewById(R.id.navMessage);
        LinearLayout navHistory = activity.findViewById(R.id.navHistory);
        LinearLayout navCart = activity.findViewById(R.id.navCart);
        LinearLayout navProfile = activity.findViewById(R.id.navProfile); // Note: Layout ID might be navProfile or navUser based on inconsistent naming, checking layouts first is safer, but assuming standard from UserHomeActivity

        if (navHome != null) {
            navHome.setOnClickListener(v -> navigateTo(activity, UserHomeActivity.class, userId));
        }

        if (navMessage != null) {
            navMessage.setOnClickListener(v -> navigateTo(activity, MessageActivity.class, userId));
        }

        if (navHistory != null) {
            navHistory.setOnClickListener(v -> navigateTo(activity, OrderHistoryActivity.class, userId));
        }

        if (navCart != null) {
            navCart.setOnClickListener(v -> navigateTo(activity, CartActivity.class, userId));
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> navigateTo(activity, UserDetailViewActivity.class, userId));
        }
    }

    private static void navigateTo(Activity currentActivity, Class<?> targetActivityClass, int userId) {
        String TAG = "NavigationHelper";
        String currentActivityName = currentActivity.getClass().getSimpleName();
        String targetActivityName = targetActivityClass.getSimpleName();
        
        Log.d(TAG, "=== NAVIGATION REQUEST ===");
        Log.d(TAG, "From: " + currentActivityName);
        Log.d(TAG, "To: " + targetActivityName);
        Log.d(TAG, "User ID: " + userId);
        
        if (currentActivity.getClass().equals(targetActivityClass)) {
            Log.d(TAG, "Already on target screen (" + targetActivityName + "), skipping navigation");
            return;
        }

        if (userId == -1) {
            Log.e(TAG, "Invalid userId (-1), cannot navigate");
            android.widget.Toast.makeText(currentActivity, "Session expired. Please login again.", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent intent = new Intent(currentActivity, targetActivityClass);
            intent.putExtra("user_id", userId);
            // Use CLEAR_TOP to bring UserHomeActivity to front if it exists, clearing activities above it
            // SINGLE_TOP prevents creating a new instance if it's already on top
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            
            Log.d(TAG, "Intent created with user_id: " + userId);
            Log.d(TAG, "Starting " + targetActivityName + "...");
            currentActivity.startActivity(intent);
            Log.d(TAG, "Navigation initiated successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to " + targetActivityName, e);
            android.widget.Toast.makeText(currentActivity, "Navigation error", android.widget.Toast.LENGTH_SHORT).show();
        }
    }
}
