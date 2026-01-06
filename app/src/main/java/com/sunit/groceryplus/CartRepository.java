package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;

import com.sunit.groceryplus.models.User;
import com.sunit.groceryplus.models.CartItem;

import java.util.ArrayList;
import java.util.List;

/** Repository for managing user shopping cart data in the database. */
public class CartRepository {
    // Infrastructure
    private static final String TAG = "CartRepository";
    private DatabaseHelper dbHelper;

    /** Initializes the repository with a DatabaseHelper. */
    public CartRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    /** Adds an item to the user's shopping cart. */
    public boolean addToCart(int userId, int productId, int quantity) {
        try {
            long result = dbHelper.addToCart(userId, productId, quantity);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error adding to cart", e);
            return false;
        }
    }

    /** Retrieves all cart items with product details for a specific user. */
    public List<CartItem> getCartItems(int userId) {
        try {
            return dbHelper.getCartItemsWithDetails(userId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting cart items", e);
            return new ArrayList<>();
        }
    }

    /** Updates the quantity of a specific cart item. */
    public boolean updateCartQuantity(int cartId, int quantity) {
        try {
            return dbHelper.updateCartQuantity(cartId, quantity);
        } catch (Exception e) {
            Log.e(TAG, "Error updating cart quantity", e);
            return false;
        }
    }

    /** Removes a specific item from the cart. */
    public boolean removeFromCart(int cartId) {
        try {
            int result = dbHelper.removeFromCart(cartId);
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error removing from cart", e);
            return false;
        }
    }

    /** Removes all items from the user's shopping cart. */
    public boolean clearCart(int userId) {
        try {
            int result = dbHelper.clearCart(userId);
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error clearing cart", e);
            return false;
        }
    }

    /** Calculates the total cost of all items in the user's cart. */
    public double getCartTotal(int userId) {
        try {
            return dbHelper.getCartTotal(userId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting cart total", e);
            return 0.0;
        }
    }
}