package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;
import com.sunit.groceryplus.utils.FirestoreSyncHelper;

/**
 * SyncOnAppStart: Optional background sync on app launch.
 * - Mirrors selected collections from Firestore to SQLite (read-through cache).
 * - Non-blocking; runs in background.
 *
 * Call from SplashScreenActivity or MainActivity once.
 */
public class SyncOnAppStart {

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
