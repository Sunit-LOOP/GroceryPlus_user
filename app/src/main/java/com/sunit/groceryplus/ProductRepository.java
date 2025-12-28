package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;

import com.sunit.groceryplus.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private static final String TAG = "ProductRepository";
    private DatabaseHelper dbHelper;

    public ProductRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public boolean addProduct(String productName, int categoryId, double price, String description, String image, int stockQuantity, int vendorId) {
        try {
            long result = dbHelper.addProduct(productName, categoryId, price, description, image, stockQuantity, vendorId);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error adding product", e);
            return false;
        }
    }

    /**
     * Get all products
     */
    public List<Product> getAllProducts() {
        try {
            return dbHelper.getAllProducts();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all products", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get product by ID
     */
    public Product getProductById(int productId) {
        try {
            return dbHelper.getProductById(productId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting product by ID", e);
            return null;
        }
    }

    /**
     * Get products by category
     */
    public List<Product> getProductsByCategory(int categoryId) {
        try {
            return dbHelper.getProductsByCategory(categoryId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting products by category", e);
            return new ArrayList<>();
        }
    }

    /**
     * Search products
     */
    public List<Product> searchProducts(String query) {
        try {
            return dbHelper.searchProducts(query);
        } catch (Exception e) {
            Log.e(TAG, "Error searching products", e);
            return new ArrayList<>();
        }
    }

    public boolean updateProduct(int productId, String productName, int categoryId, double price, String description, String image, int stockQuantity, int vendorId) {
        try {
            return dbHelper.updateProduct(productId, productName, categoryId, price, description, image, stockQuantity, vendorId);
        } catch (Exception e) {
            Log.e(TAG, "Error updating product", e);
            return false;
        }
    }

    /**
     * Delete product
     */
    public boolean deleteProduct(int productId) {
        try {
            return dbHelper.deleteProduct(productId);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting product", e);
            return false;
        }
    }
}