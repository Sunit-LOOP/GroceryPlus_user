package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;
import com.sunit.groceryplus.utils.FirestoreSyncHelper;

/** Handles background data synchronization from Firestore to SQLite during application startup. */
public class SyncOnAppStart {
    // Infrastructure
    private static final String TAG = "SyncOnAppStart";

    public static void syncIfNeeded(Context context) {
        // Example: only sync if network is available
        if (!isNetworkAvailable(context)) {
            Log.i(TAG, "No network; skipping Firestore sync");
            return;
        }

        FirestoreSyncHelper sync = FirestoreSyncHelper.getInstance();

        // Refresh key collections from Firestore into SQLite (if you implement upsert logic)
        sync.refreshProductsFromFirestore();
        sync.refreshUsersFromFirestore();
        sync.refreshOrdersFromFirestore();

        // You can also trigger a one-way upload of pending local changes if you track them
        // For now, write-through sync already mirrors changes on each local write.
    }

    private static boolean isNetworkAvailable(Context context) {
        // Simple check; replace with proper ConnectivityManager check if needed
        return true; // Placeholder
    }
}
