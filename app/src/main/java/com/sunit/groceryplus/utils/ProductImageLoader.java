package com.sunit.groceryplus.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/** Utility class providing centralized product image loading using Glide with automatic placeholder management. */
public final class ProductImageLoader {

    private static final String TAG = "ProductImageLoader";

    /** Private constructor to prevent instantiation of utility class. */
    private ProductImageLoader() {
    }

    /** Loads a product image into an ImageView from various sources including URLs, URIs, and local resources. */
    public static void load(Context context, ImageView imageView, String imageValue, int placeholderResId) {
        if (context == null || imageView == null) return;

        if (imageValue == null || imageValue.trim().isEmpty()) {
            Log.d(TAG, "Image value is null or empty, using placeholder");
            imageView.setImageResource(placeholderResId);
            return;
        }

        String value = imageValue.trim();
        Log.d(TAG, "Loading image: " + value);

        try {
            if (value.startsWith("content://") || value.startsWith("file://") || 
                value.startsWith("android.resource://") || value.startsWith("http://") || value.startsWith("https://") ||
                ImageStorageManager.isPermanentStoragePath(value)) {
                
                // For permanent storage paths, load as file
                if (ImageStorageManager.isPermanentStoragePath(value)) {
                    Log.d(TAG, "Loading permanent storage path as file: " + value);
                    java.io.File imageFile = new java.io.File(value);
                    
                    // Verify file exists before loading
                    if (imageFile.exists() && imageFile.canRead()) {
                        Log.d(TAG, "File exists and is readable, size: " + imageFile.length() + " bytes");
                        Glide.with(context)
                                .load(imageFile)
                                .placeholder(placeholderResId)
                                .error(placeholderResId)
                                .into(imageView);
                    } else {
                        Log.e(TAG, "File does not exist or cannot be read: " + value);
                        Log.e(TAG, "File exists: " + imageFile.exists());
                        Log.e(TAG, "File can read: " + imageFile.canRead());
                        Log.e(TAG, "File absolute path: " + imageFile.getAbsolutePath());
                        imageView.setImageResource(placeholderResId);
                    }
                } else {
                    Log.d(TAG, "Loading as URI: " + value);
                    Glide.with(context)
                            .load(Uri.parse(value))
                            .placeholder(placeholderResId)
                            .error(placeholderResId)
                            .into(imageView);
                }
                return;
            }

            // Try to load as drawable resource
            Log.d(TAG, "Attempting to load as drawable resource: " + value);
            int resId = context.getResources().getIdentifier(value, "drawable", context.getPackageName());
            if (resId != 0) {
                Log.d(TAG, "Drawable resource found: " + resId);
                imageView.setImageResource(resId);
            } else {
                Log.e(TAG, "Drawable resource not found: " + value);
                imageView.setImageResource(placeholderResId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + value, e);
            imageView.setImageResource(placeholderResId);
        }
    }
}
