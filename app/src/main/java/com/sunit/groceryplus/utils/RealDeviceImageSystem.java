package com.sunit.groceryplus.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import android.util.Log;
import com.sunit.groceryplus.R;

/**
 * RealDeviceImageSystem - Handles image loading for products and categories
 * Supports both file paths and drawable resources
 */
public class RealDeviceImageSystem {
    
    private static final String TAG = "RealDeviceImageSystem";
    
    public static void loadProductImage(Context context, ImageView imageView, String imagePath, String productName) {
        try {
            // PRIORITY 1: Check for custom image path from database (Admin selected gallery/camera)
            // If the path is a real file or URI (not the default placeholder), we MUST load it.
            if (imagePath != null && !imagePath.isEmpty() && 
                !imagePath.equals("product_icon") && !imagePath.equals("ic_default_product")) {
                
                // Use ProductImageLoader for complex loading (Glide, Files, URIs)
                // Use the smart mapping as the placeholder if loading fails
                int placeholderId = getSmartResourceId(context, productName);
                ProductImageLoader.load(context, imageView, imagePath, placeholderId);
                Log.d(TAG, "✓ LOADED CUSTOM IMAGE for: " + productName + " -> " + imagePath);
                return;
            }
            
            // PRIORITY 2: Smart Mapping Approach (Map product name to drawable)
            int resourceId = getSmartResourceId(context, productName);
            imageView.setImageResource(resourceId);
            Log.d(TAG, "✓ LOADED SMART IMAGE for: " + productName);
            
        } catch (Exception e) {
            Log.e(TAG, "ERROR loading image for: " + productName, e);
            imageView.setImageResource(R.drawable.ic_default_product);
        }
    }
    
    /** Helper to get the resource ID from smart mapping. */
    private static int getSmartResourceId(Context context, String productName) {
        String drawableName = getDrawableNameForProduct(productName);
        int resId = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
        return (resId != 0) ? resId : R.drawable.ic_default_product;
    }
    
    private static String getDrawableNameForProduct(String productName) {
        if (productName == null) return "product_icon";
        
        String lowerName = productName.toLowerCase();
        
        // Mappings for ALL your products
        if (lowerName.contains("milk")) return "bottle_milk";
        if (lowerName.contains("apple")) return "apple";
        if (lowerName.contains("banana")) return "banana";
        if (lowerName.contains("orange")) return "orange";
        if (lowerName.contains("grapes")) return "grapes";
        if (lowerName.contains("mango")) return "mango";
        
        if (lowerName.contains("tomato")) return "tomato_red";
        if (lowerName.contains("carrot")) return "carrot";
        if (lowerName.contains("spinach") || lowerName.contains("green vegetable")) return "green_vegetable";
        if (lowerName.contains("potato")) return "potato";
        if (lowerName.contains("onion")) return "onion";
        if (lowerName.contains("cabbage")) return "cabbage";
        if (lowerName.contains("cauliflower")) return "cauliflower";
        if (lowerName.contains("lettuce")) return "lettuce_leaf";
        if (lowerName.contains("vindi") || lowerName.contains("okra")) return "vindi";
        if (lowerName.contains("bottle gourd")) return "bottle_gourd";
        
        if (lowerName.contains("bread")) return "bread";
        if (lowerName.contains("crossant") || lowerName.contains("croissant")) return "crossant";
        if (lowerName.contains("cake")) return "chocolate_cake";
        if (lowerName.contains("bagel")) return "bagel";
        if (lowerName.contains("pastry") || lowerName.contains("pastery")) return "pastery";
        
        if (lowerName.contains("cheese")) return "cheese_slice";
        if (lowerName.contains("butter")) return "butter";
        if (lowerName.contains("curd") || lowerName.contains("dahi") || lowerName.contains("yogurt")) return "dahi";
        if (lowerName.contains("egg")) return "egg";
        if (lowerName.contains("paneer")) return "paneer_cubes";
        
        if (lowerName.contains("rice")) return "rice_sack";
        if (lowerName.contains("oil")) return "oil_bottle";
        if (lowerName.contains("juice")) return "juice_bottle";
        
        return "product_icon";
    }
    
    public static void loadCategoryImage(Context context, ImageView imageView, String imagePath, String categoryName) {
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                // Try to load as drawable resource first
                int resourceId = context.getResources().getIdentifier(
                    imagePath.replaceAll("[^a-zA-Z0-9_]", "_"), 
                    "drawable", 
                    context.getPackageName()
                );
                
                if (resourceId != 0) {
                    imageView.setImageResource(resourceId);
                    Log.d(TAG, "Loaded category image from drawable: " + imagePath);
                    return;
                }
                
                // Try to load as file path
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    if (myBitmap != null) {
                        imageView.setImageBitmap(myBitmap);
                        Log.d(TAG, "Loaded category image from file: " + imagePath);
                        return;
                    }
                }
                
                // Try to load from assets
                try {
                    java.io.InputStream inputStream = context.getAssets().open(imagePath);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        Log.d(TAG, "Loaded category image from assets: " + imagePath);
                        return;
                    }
                } catch (IOException e) {
                    Log.d(TAG, "Not found in assets: " + imagePath);
                }
            }
            
            // Fallback to default category image
            imageView.setImageResource(R.drawable.ic_default_category);
            Log.d(TAG, "Using default category image for: " + categoryName);
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading category image: " + imagePath, e);
            imageView.setImageResource(R.drawable.ic_default_category);
        }
    }
}
