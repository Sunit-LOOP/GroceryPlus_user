package com.sunit.groceryplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.sunit.groceryplus.models.User;
import com.sunit.groceryplus.models.Product;

import java.util.ArrayList;
import java.util.List;

import static com.sunit.groceryplus.DatabaseContract.FavoriteEntry;
import static com.sunit.groceryplus.DatabaseContract.ProductEntry;

public class FavoriteRepository {
    private static final String TAG = "FavoriteRepository";
    private DatabaseHelper dbHelper;

    public FavoriteRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    /**
     * Add product to favorites
     */
    public boolean addToFavorites(int userId, int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        try {
            // Check if already in favorites
            if (isInFavorites(userId, productId)) {
                return true; // Already in favorites
            }
            
            ContentValues values = new ContentValues();
            values.put(FavoriteEntry.COLUMN_NAME_USER_ID, userId);
            values.put(FavoriteEntry.COLUMN_NAME_PRODUCT_ID, productId);
            
            long result = db.insert(FavoriteEntry.TABLE_NAME, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error adding to favorites", e);
            return false;
        } finally {
            db.close();
        }
    }

    /**
     * Remove product from favorites
     */
    public boolean removeFromFavorites(int userId, int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        try {
            int result = db.delete(FavoriteEntry.TABLE_NAME,
                    FavoriteEntry.COLUMN_NAME_USER_ID + " = ? AND " +
                    FavoriteEntry.COLUMN_NAME_PRODUCT_ID + " = ?",
                    new String[]{String.valueOf(userId), String.valueOf(productId)});
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error removing from favorites", e);
            return false;
        } finally {
            db.close();
        }
    }

    /**
     * Check if product is in favorites
     */
    public boolean isInFavorites(int userId, int productId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        try {
            String query = "SELECT COUNT(*) FROM " + FavoriteEntry.TABLE_NAME +
                          " WHERE " + FavoriteEntry.COLUMN_NAME_USER_ID + " = ? AND " +
                          FavoriteEntry.COLUMN_NAME_PRODUCT_ID + " = ?";
            
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(productId)});
            
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                cursor.close();
                return count > 0;
            }
            
            if (cursor != null) {
                cursor.close();
            }
            
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error checking favorites", e);
            return false;
        }
    }

    /**
     * Get all favorite products for a user
     */
    public List<Product> getFavoriteProducts(int userId) {
        List<Product> favorites = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        try {
            String query = "SELECT p.*, v." + DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_NAME + " FROM " + ProductEntry.TABLE_NAME + " p " +
                          "INNER JOIN " + FavoriteEntry.TABLE_NAME + " f " +
                          "ON p." + ProductEntry.COLUMN_NAME_PRODUCT_ID + " = f." + FavoriteEntry.COLUMN_NAME_PRODUCT_ID +
                          " LEFT JOIN " + DatabaseContract.VendorEntry.TABLE_NAME + " v " +
                          "ON p." + ProductEntry.COLUMN_NAME_VENDOR_ID + " = v." + DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_ID +
                          " WHERE f." + FavoriteEntry.COLUMN_NAME_USER_ID + " = ? " +
                          "ORDER BY f." + FavoriteEntry.COLUMN_NAME_ADDED_AT + " DESC";
            
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    try {
                        int productId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRODUCT_ID));
                        String productName = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRODUCT_NAME));
                        int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_CATEGORY_ID));
                        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRICE));
                        String description = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_DESCRIPTION));
                        String image = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_IMAGE));
                        int stockQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_STOCK));
                        int vendorId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_VENDOR_ID));
                        String vendorName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_NAME));
                        
                        Product product = new Product(productId, productName, categoryId, "", price, description, image, stockQuantity, vendorId, vendorName);
                        favorites.add(product);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing favorite product", e);
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting favorite products", e);
        }
        
        return favorites;
    }

    /**
     * Get count of favorite products for a user
     */
    public int getFavoritesCount(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        try {
            String query = "SELECT COUNT(*) FROM " + FavoriteEntry.TABLE_NAME +
                          " WHERE " + FavoriteEntry.COLUMN_NAME_USER_ID + " = ?";
            
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
            
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                cursor.close();
                return count;
            }
            
            if (cursor != null) {
                cursor.close();
            }
            
            return 0;
        } catch (Exception e) {
            Log.e(TAG, "Error getting favorites count", e);
            return 0;
        }
    }

    /**
     * Clear all favorites for a user
     */
    public boolean clearFavorites(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        try {
            int result = db.delete(FavoriteEntry.TABLE_NAME,
                    FavoriteEntry.COLUMN_NAME_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)});
            return result >= 0;
        } catch (Exception e) {
            Log.e(TAG, "Error clearing favorites", e);
            return false;
        } finally {
            db.close();
        }
    }
}
