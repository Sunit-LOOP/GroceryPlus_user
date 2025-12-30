package com.sunit.groceryplus.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public final class ProductImageLoader {

    private ProductImageLoader() {
    }

    public static void load(Context context, ImageView imageView, String imageValue, int placeholderResId) {
        if (context == null || imageView == null) return;

        if (imageValue == null || imageValue.trim().isEmpty()) {
            imageView.setImageResource(placeholderResId);
            return;
        }

        String value = imageValue.trim();

        try {
            if (value.startsWith("content://") || value.startsWith("file://") || value.startsWith("android.resource://") || value.startsWith("http://") || value.startsWith("https://")) {
                Glide.with(context)
                        .load(Uri.parse(value))
                        .placeholder(placeholderResId)
                        .error(placeholderResId)
                        .into(imageView);
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
