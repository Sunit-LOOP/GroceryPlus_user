package com.sunit.groceryplus.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * ProductImageLoader - Utility class for loading product images.
 * 
 * This class provides a centralized utility for loading product images using Glide
 * image loading library. It supports multiple image sources including local resources,
 * content URIs, file URIs, and network URLs with proper error handling
 * and placeholder management.
 * 
 * Key Features:
 * - Multiple image source support (local resources, content URIs, network URLs)
 * - Glide integration for efficient image loading and caching
 * - Automatic placeholder management for missing/invalid images
 * - Error handling with fallback to placeholder
 * - Null safety checks for context and views
 * - Resource ID resolution for drawable names
 */
public final class ProductImageLoader {

    /**
     * Private constructor to prevent instantiation.
     * This class is designed as a utility with only static methods,
     * following the utility class pattern for image loading operations.
     */
    private ProductImageLoader() {
    }

    /**
     * Load product image with automatic placeholder management.
     * 
     * This method loads images from various sources with proper error handling
     * and automatic fallback to placeholder when image loading fails or invalid
     * image data is provided.
     * 
     * Image Loading Priority:
     * 1. Network URLs (http://, https://) - Load via Glide
     * 2. Content URIs (content://) - Load via Glide
     * 3. File URIs (file://) - Load via Glide
     * 4. Android resources (android.resource://) - Load via Glide
     * 5. Local drawable names - Resolve to resource ID and load directly
     * 
     * @param context Application context for resource resolution and Glide operations
     * @param imageView The ImageView to load the image into
     * @param imageValue The image source (URL, URI, or drawable name)
     * @param placeholderResId The placeholder resource to show on error/missing image
     */
    public static void load(Context context, ImageView imageView, String imageValue, int placeholderResId) {
        // Null safety checks to prevent crashes
        if (context == null || imageView == null) return;

        // Handle empty or null image values by showing placeholder
        if (imageValue == null || imageValue.trim().isEmpty()) {
            imageView.setImageResource(placeholderResId);
            return;
        }

        // Clean and validate image value
        String value = imageValue.trim();

        try {
            // Handle network URLs and URIs with Glide
            if (value.startsWith("content://") || value.startsWith("file://") || 
                value.startsWith("android.resource://") || value.startsWith("http://") || value.startsWith("https://")) {
                
                // Load image using Glide with proper configuration
                Glide.with(context)
                        .load(Uri.parse(value))
                        .placeholder(placeholderResId)
                        .error(placeholderResId)
                        .into(imageView);
                return;
            }

            // Handle local drawable resources by name
            int resId = context.getResources().getIdentifier(value, "drawable", context.getPackageName());
            if (resId != 0) {
                // Resource found - load directly
                imageView.setImageResource(resId);
            } else {
                // Resource not found - show placeholder
                imageView.setImageResource(placeholderResId);
            }
        } catch (Exception e) {
            // Any error during image loading - show placeholder
            imageView.setImageResource(placeholderResId);
        }
    }
}
