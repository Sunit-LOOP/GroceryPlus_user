package com.sunit.groceryplus.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/** Utility class providing centralized product image loading using Glide with automatic placeholder management. */
public final class ProductImageLoader {

    /** Private constructor to prevent instantiation of utility class. */
    private ProductImageLoader() {
    }

    /** Loads a product image into an ImageView from various sources including URLs, URIs, and local resources. */
    public static void load(Context context, ImageView imageView, String imageValue, int placeholderResId) {
        if (context == null || imageView == null) return;

        if (imageValue == null || imageValue.trim().isEmpty()) {
            imageView.setImageResource(placeholderResId);
            return;
        }

        String value = imageValue.trim();

        try {
            if (value.startsWith("content://") || value.startsWith("file://") || 
                value.startsWith("android.resource://") || value.startsWith("http://") || value.startsWith("https://") ||
                ImageStorageManager.isPermanentStoragePath(value)) {
                
                // For permanent storage paths, load as file
                if (ImageStorageManager.isPermanentStoragePath(value)) {
                    Glide.with(context)
                            .load(new java.io.File(value))
                            .placeholder(placeholderResId)
                            .error(placeholderResId)
                            .into(imageView);
                } else {
                    Glide.with(context)
                            .load(Uri.parse(value))
                            .placeholder(placeholderResId)
                            .error(placeholderResId)
                            .into(imageView);
                }
                return;
            }

            int resId = context.getResources().getIdentifier(value, "drawable", context.getPackageName());
            if (resId != 0) {
                imageView.setImageResource(resId);
            } else {
                imageView.setImageResource(placeholderResId);
            }
        } catch (Exception e) {
            imageView.setImageResource(placeholderResId);
        }
    }
}
