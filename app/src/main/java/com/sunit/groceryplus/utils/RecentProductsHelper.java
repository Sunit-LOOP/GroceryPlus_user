package com.sunit.groceryplus.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * RecentProductsHelper - Manages the list of recently viewed products.
 * 
 * This class uses SharedPreferences to store and retrieve a list of product IDs
 * that the user has recently viewed. It maintains a limited list (MAX_RECENT)
 * and ensures that the most recently viewed product is at the top.
 */
public class RecentProductsHelper {

    private static final String PREF_NAME = "RecentProducts";
    private static final String KEY_RECENT_IDS = "recent_product_ids";
    private static final int MAX_RECENT = 10;

    /**
     * Adds a product ID to the recently viewed list.
     * 
     * If the product is already in the list, it is moved to the front.
     * If the list exceeds MAX_RECENT, the oldest items are removed.
     * 
     * @param context Application context
     * @param productId The ID of the product being viewed
     */
    public static void addProduct(Context context, int productId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String savedIds = prefs.getString(KEY_RECENT_IDS, "");
        
        List<String> idList = new ArrayList<>();
        if (!savedIds.isEmpty()) {
            idList.addAll(Arrays.asList(savedIds.split(",")));
        }

        // Remove if already exists to move it to the front
        idList.remove(String.valueOf(productId));
        idList.add(0, String.valueOf(productId));

        // Keep only top MAX_RECENT
        if (idList.size() > MAX_RECENT) {
            idList = idList.subList(0, MAX_RECENT);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < idList.size(); i++) {
            sb.append(idList.get(i));
            if (i < idList.size() - 1) sb.append(",");
        }

        prefs.edit().putString(KEY_RECENT_IDS, sb.toString()).apply();
    }

    /**
     * Retrieves the list of recently viewed product IDs.
     * 
     * @param context Application context
     * @return List of Integer product IDs
     */
    public static List<Integer> getRecentProductIds(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String savedIds = prefs.getString(KEY_RECENT_IDS, "");
        
        List<Integer> ids = new ArrayList<>();
        if (!savedIds.isEmpty()) {
            String[] parts = savedIds.split(",");
            for (String part : parts) {
                try {
                    ids.add(Integer.parseInt(part));
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        }
        return ids;
    }
}
