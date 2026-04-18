package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;

import com.sunit.groceryplus.models.Product;

import java.util.ArrayList;
import java.util.List;

/** Repository for managing products in the database. */
public class ProductRepository {
    // Infrastructure
    private static final String TAG = "ProductRepository";
    private DatabaseHelper dbHelper;

    /** Initializes the repository with a DatabaseHelper. */
    public ProductRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    /** Adds a new product to the database. */
    public boolean addProduct(String productName, int categoryId, double price, String description, String image, int stockQuantity, int vendorId) {
        try {
            long result = dbHelper.addProduct(productName, categoryId, price, description, image, stockQuantity, vendorId);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error adding product", e);
            return false;
        }
    }

    /** Retrieves all products available in the database. */
    public List<Product> getAllProducts() {
        try {
            return dbHelper.getAllProducts();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all products", e);
            return new ArrayList<>();
        }
    }

    /** Retrieves a specific product by its ID. */
    public Product getProductById(int productId) {
        try {
            return dbHelper.getProductById(productId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting product by ID", e);
            return null;
        }
    }

    /** Retrieves all products belonging to a specific category. */
    public List<Product> getProductsByCategory(int categoryId) {
        try {
            return dbHelper.getProductsByCategory(categoryId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting products by category", e);
            return new ArrayList<>();
        }
    }

    /** Searches for products matching a text query. */
    public List<Product> searchProducts(String query) {
        try {
            return dbHelper.searchProducts(query);
        } catch (Exception e) {
            Log.e(TAG, "Error searching products", e);
            return new ArrayList<>();
        }
    }

    /** Updates an existing product's information in the database. */
    public boolean updateProduct(int productId, String productName, int categoryId, double price, String description, String image, int stockQuantity, int vendorId) {
        try {
            return dbHelper.updateProduct(productId, productName, categoryId, price, description, image, stockQuantity, vendorId);
        } catch (Exception e) {
            Log.e(TAG, "Error updating product", e);
            return false;
        }
    }

    /** Deletes a specific product from the database by its ID. */
    public boolean deleteProduct(int productId) {
        try {
            return dbHelper.deleteProduct(productId);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting product", e);
            return false;
        }
    }

    /** Specifically updates only the image path for a product. */
    public boolean updateProductImagePath(int productId, String imagePath) {
        try {
            return dbHelper.updateProductImagePath(productId, imagePath);
        } catch (Exception e) {
            Log.e(TAG, "Error updating product image path", e);
            return false;
        }
    }
}