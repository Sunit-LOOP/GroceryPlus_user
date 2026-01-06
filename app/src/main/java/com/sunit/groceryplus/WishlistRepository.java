package com.sunit.groceryplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sunit.groceryplus.models.Product;
import com.sunit.groceryplus.models.Wishlist;

import java.util.ArrayList;
import java.util.List;

/** Repository for managing user wishlist items in the database. */
public class WishlistRepository {
    // Infrastructure
    private DatabaseHelper dbHelper;

    /** Initializes the repository with a DatabaseHelper. */
    public WishlistRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /** Adds a product to the user's wishlist. */
    public long addToWishlist(int userId, int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.WishlistEntry.COLUMN_NAME_USER_ID, userId);
        values.put(DatabaseContract.WishlistEntry.COLUMN_NAME_PRODUCT_ID, productId);
        long result = db.insert(DatabaseContract.WishlistEntry.TABLE_NAME, null, values);
        return result;
    }

    /** Removes a product from the user's wishlist. */
    public boolean removeFromWishlist(int userId, int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DatabaseContract.WishlistEntry.TABLE_NAME,
                DatabaseContract.WishlistEntry.COLUMN_NAME_USER_ID + " = ? AND " + DatabaseContract.WishlistEntry.COLUMN_NAME_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(productId)});
        return rows > 0;
    }

    /** Checks if a specific product is already in the user's wishlist. */
    public boolean isInWishlist(int userId, int productId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseContract.WishlistEntry.COLUMN_NAME_USER_ID + " = ? AND " + DatabaseContract.WishlistEntry.COLUMN_NAME_PRODUCT_ID + " = ?";
        Cursor cursor = db.query(DatabaseContract.WishlistEntry.TABLE_NAME, null, selection,
                new String[]{String.valueOf(userId), String.valueOf(productId)}, null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        return exists;
    }

    /** Retrieves all products in the user's wishlist with full product details. */
    public List<Product> getWishlistProducts(int userId) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT p.* FROM " + DatabaseContract.ProductEntry.TABLE_NAME + " p " +
                "INNER JOIN " + DatabaseContract.WishlistEntry.TABLE_NAME + " w " +
                "ON p." + DatabaseContract.ProductEntry.COLUMN_NAME_PRODUCT_ID + " = w." + DatabaseContract.WishlistEntry.COLUMN_NAME_PRODUCT_ID + " " +
                "WHERE w." + DatabaseContract.WishlistEntry.COLUMN_NAME_USER_ID + " = ? " +
                "ORDER BY w." + DatabaseContract.WishlistEntry.COLUMN_NAME_ADDED_AT + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_PRODUCT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_CATEGORY_ID));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_PRICE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_DESCRIPTION));
                String image = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_IMAGE));
                int stock = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_STOCK));
                int vendorId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_VENDOR_ID));

                Product product = new Product(id, name, categoryId, price, description, image, stock, vendorId);
                products.add(product);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return products;
    }

    /** Retrieves the formal wishlist entries for a specific user. */
    public List<Wishlist> getWishlistEntries(int userId) {
        List<Wishlist> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.WishlistEntry.TABLE_NAME, null,
                DatabaseContract.WishlistEntry.COLUMN_NAME_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}, null, null,
                DatabaseContract.WishlistEntry.COLUMN_NAME_ADDED_AT + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.WishlistEntry.COLUMN_NAME_WISHLIST_ID));
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.WishlistEntry.COLUMN_NAME_PRODUCT_ID));
                String addedAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.WishlistEntry.COLUMN_NAME_ADDED_AT));
                list.add(new Wishlist(id, userId, productId, addedAt));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }
}
