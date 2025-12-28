package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;

import com.sunit.groceryplus.models.User;
import com.sunit.groceryplus.models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartRepository {
    private static final String TAG = "CartRepository";
    private DatabaseHelper dbHelper;

    public CartRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    /**
     * Add item to cart
     */
    public boolean addToCart(int userId, int productId, int quantity) {
        try {
            long result = dbHelper.addToCart(userId, productId, quantity);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error adding to cart", e);
            return false;
        }
    }

    /**
     * Get cart items for user
     */
    public List<CartItem> getCartItems(int userId) {
        try {
            return dbHelper.getCartItemsWithDetails(userId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting cart items", e);
            return new ArrayList<>();
        }
    }

    /**
     * Update cart quantity
     */
    public boolean updateCartQuantity(int cartId, int quantity) {
        try {
            return dbHelper.updateCartQuantity(cartId, quantity);
        } catch (Exception e) {
            Log.e(TAG, "Error updating cart quantity", e);
            return false;
        }
    }

    /**
     * Remove item from cart
     */
    public boolean removeFromCart(int cartId) {
        try {
            int result = dbHelper.removeFromCart(cartId);
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error removing from cart", e);
            return false;
        }
    }

    /**
     * Clear cart for user
     */
    public boolean clearCart(int userId) {
        try {
            int result = dbHelper.clearCart(userId);
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error clearing cart", e);
            return false;
        }
    }

    /**
     * Get cart total
     */
    public double getCartTotal(int userId) {
        try {
            return dbHelper.getCartTotal(userId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting cart total", e);
            return 0.0;
        }
    }
}