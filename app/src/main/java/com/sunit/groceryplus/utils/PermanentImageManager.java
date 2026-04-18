package com.sunit.groceryplus.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import android.util.Log;
import com.sunit.groceryplus.ProductRepository;

/**
 * Stub implementation for PermanentImageManager
 * This is a placeholder to resolve compilation errors
 */
/**
 * Manager to handle permanent image storage for products.
 * Wraps ImageStorageManager and updates the database with permanent file paths.
 */
public class PermanentImageManager {
    
    private static final String TAG = "PermanentImageManager";
    
    public static void saveProductImagePermanently(Context context, int productId, String imagePath, String productName) {
        if (imagePath == null || imagePath.isEmpty() || productId <= 0) return;
        
        // Skip if it's already a drawable resource name (from sample data)
        if (!imagePath.contains("/") && !imagePath.startsWith("content://")) {
            return;
        }

        new Thread(() -> {
            try {
                // 1. Copy to internal storage
                String permanentPath = ImageStorageManager.saveImagePermanently(
                    context, 
                    imagePath, 
                    ImageStorageManager.ImageType.PRODUCT, 
                    productName
                );

                if (permanentPath != null) {
                    // 2. Update database
                    ProductRepository repo = new ProductRepository(context);
                    // Use a direct update since we have the ID
                    boolean updated = repo.updateProductImagePath(productId, permanentPath);
                    Log.d(TAG, "✓ Permanent storage sync for " + productName + ": " + (updated ? "SUCCESS" : "FAILED"));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in permanent storage background task", e);
            }
        }).start();
    }
    
    public static void updatePermanentImage(Context context, int productId, String imagePath, String productName) {
        saveProductImagePermanently(context, productId, imagePath, productName);
    }
    
    public static String loadPermanentProductImage(Context context, int productId, String productName) {
        // This could be used for specific lookup, but ProductRepository already gets the path from DB
        return null;
    }
    
    public static void ensureProductImagePermanently(Context context, int productId, String productName, String imagePath) {
        saveProductImagePermanently(context, productId, imagePath, productName);
    }
}
