package com.sunit.groceryplus.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for managing persistent image storage in app-specific directories.
 * Handles saving images from URIs to internal storage and managing file paths.
 */
public class ImageStorageManager {

    private static final String TAG = "ImageStorageManager";
    private static final String PRODUCT_IMAGES_DIR = "product_images";
    private static final String CATEGORY_IMAGES_DIR = "category_images";

    public enum ImageType {
        PRODUCT, CATEGORY
    }

    /**
     * Saves an image from a temporary URI to the app's internal storage permanently.
     *
     * @param context Application context
     * @param imageUri Source URI of the image
     * @param type Type of image (Product/Category) to determine storage sub-folder
     * @param nameBase Base name for the file (e.g., product name)
     * @return Absolute path to the saved file, or null if failed
     */
    public static String saveImagePermanently(Context context, Uri imageUri, ImageType type, String nameBase) {
        if (context == null || imageUri == null) return null;

        try {
            // Create directory if it doesn't exist
            File storageDir = getStorageDirectory(context, type);
            if (!storageDir.exists() && !storageDir.mkdirs()) {
                Log.e(TAG, "Failed to create storage directory");
                return null;
            }

            // Generate unique filename
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = formatFileName(nameBase) + "_" + timeStamp + ".jpg";
            File destFile = new File(storageDir, fileName);

            // Copy file content
            ContentResolver resolver = context.getContentResolver();
            try (InputStream inputStream = resolver.openInputStream(imageUri);
                 OutputStream outputStream = new FileOutputStream(destFile)) {

                if (inputStream == null) {
                    Log.e(TAG, "Cannot open input stream for URI: " + imageUri);
                    return null;
                }

                byte[] buffer = new byte[4096];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                
                Log.d(TAG, "Image saved permanently to: " + destFile.getAbsolutePath());
                return destFile.getAbsolutePath();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving image permanently", e);
            return null;
        }
    }
    
    /**
     * Overload for compatibility if string path is passed (wrapper around URI version if it's a URI string).
     */
    public static String saveImagePermanently(Context context, String imagePathOrUri, ImageType type, String name) {
        if (imagePathOrUri == null) return null;
        
        // If it's already a permanent path ensuring existence, just return it
        if (isPermanentStoragePath(imagePathOrUri)) {
            return imagePathOrUri;
        }
        
        try {
            Uri uri = Uri.parse(imagePathOrUri);
            return saveImagePermanently(context, uri, type, name);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing URI string: " + imagePathOrUri, e);
            return null;
        }
    }

    /**
     * Gets the appropriate storage directory based on image type.
     */
    public static File getStorageDirectory(Context context, ImageType type) {
        String subDir = (type == ImageType.PRODUCT) ? PRODUCT_IMAGES_DIR : CATEGORY_IMAGES_DIR;
         // Use app's private files directory so it's not accessible to other apps strictly, 
         // but easily managed. switching to getFilesDir() instead of getCacheDir() for permanence.
        return new File(context.getFilesDir(), subDir);
    }

    /**
     * Checks if a path string represents a local file path (assumed to be permanent storage).
     */
    public static boolean isPermanentStoragePath(String path) {
        if (path == null || path.isEmpty()) return false;
        
        // Check if it starts with / (Linux/Android file path root) and is not a content/file URI scheme prefix
        return path.startsWith("/") && !path.startsWith("file://") && !path.startsWith("content://");
    }

    /**
     * Deletes an image file from storage.
     */
    public static void deleteImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) return;
        
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                Log.d(TAG, "Deleted image: " + imagePath + ", success: " + deleted);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting image: " + imagePath, e);
        }
    }

    /**
     * Helper to sanitize filenames.
     */
    private static String formatFileName(String name) {
        if (name == null) return "image";
        return name.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
    }
}
