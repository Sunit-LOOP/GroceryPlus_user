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

/** Utility class for managing centralized navigation and bottom bar interactions between activities. */
public class NavigationHelper {

    /** Sets up click listeners for the bottom navigation bar items in the provided activity. */
    public static void setupNavigation(Activity activity, int userId) {
        LinearLayout navHome = activity.findViewById(R.id.navHome);
        LinearLayout navMessage = activity.findViewById(R.id.navMessage);
        LinearLayout navHistory = activity.findViewById(R.id.navHistory);
        LinearLayout navCart = activity.findViewById(R.id.navCart);
        LinearLayout navProfile = activity.findViewById(R.id.navProfile);

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

    /** Handles authenticated navigation between activities with duplicate prevention and session validation. */
    private static void navigateTo(Activity currentActivity, Class<?> targetActivityClass, int userId) {
        String TAG = "NavigationHelper";
        String currentActivityName = currentActivity.getClass().getSimpleName();
        String targetActivityName = targetActivityClass.getSimpleName();
        
        Log.d(TAG, "Navigating: " + currentActivityName + " -> " + targetActivityName + " (User: " + userId + ")");
        
        if (currentActivity.getClass().equals(targetActivityClass)) {
            Log.d(TAG, "Already on target: " + targetActivityName);
            return;
        }

        if (userId == -1) {
            Log.e(TAG, "Invalid userId, blocking navigation");
            android.widget.Toast.makeText(currentActivity, "Session expired. Please login again.", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent intent = new Intent(currentActivity, targetActivityClass);
            intent.putExtra("user_id", userId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            
            currentActivity.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Navigation error to " + targetActivityName, e);
            android.widget.Toast.makeText(currentActivity, "Navigation error", android.widget.Toast.LENGTH_SHORT).show();
        }
    }
}
