package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;
import com.sunit.groceryplus.models.Product;
import com.sunit.groceryplus.utils.FirestoreSyncHelper;
import java.util.List;

/** Repository for managing products with SQLite local storage and Firestore cloud synchronization. */
public class ProductRepositoryWithSync {
    // Infrastructure
    private static final String TAG = "ProductRepoWithSync";
    private final DatabaseHelper dbHelper;
    private final FirestoreSyncHelper sync;

    /** Initializes the repository with local database helper and cloud sync helper. */
    public ProductRepositoryWithSync(Context context) {
        this.dbHelper = new DatabaseHelper(context);
        this.sync = FirestoreSyncHelper.getInstance();
    }

    /** Adds a product locally to SQLite and mirrors the addition to Firestore. */
    public long addProduct(Product product) {
        long id = dbHelper.addProduct(
                product.getProductName(),
                product.getCategoryId(),
                product.getPrice(),
                product.getDescription(),
                product.getImage(),
                product.getStockQuantity(),
                product.getVendorId()
        );
        if (id != -1) {
            product.setProductId((int) id);
            // Mirror to Firestore (fire-and-forget)
            sync.syncProduct(product, "add");
        } else {
            Log.e(TAG, "Failed to add product to SQLite");
        }
        return id;
    }

    /** Updates a product locally in SQLite and mirrors the update to Firestore. */
    public int updateProduct(Product product) {
        boolean success = dbHelper.updateProduct(
                product.getProductId(),
                product.getProductName(),
                product.getCategoryId(),
                product.getPrice(),
                product.getDescription(),
                product.getImage(),
                product.getStockQuantity(),
                product.getVendorId()
        );
        int rows = success ? 1 : 0;
        if (rows > 0) {
            // Mirror to Firestore
            sync.syncProduct(product, "update");
        } else {
            Log.e(TAG, "Failed to update product in SQLite");
        }
        return rows;
    }

    /** Deletes a product locally from SQLite and mirrors the deletion to Firestore. */
    public int deleteProduct(int productId) {
        // Fetch product to mirror delete
        Product product = dbHelper.getProductById(productId);
        boolean success = dbHelper.deleteProduct(productId);
        int rows = success ? 1 : 0;
        if (rows > 0 && product != null) {
            // Mirror delete to Firestore
            sync.syncProduct(product, "delete");
        } else {
            Log.e(TAG, "Failed to delete product from SQLite");
        }
        return rows;
    }

    /** Retrieves all products from the local SQLite database. */
    public List<Product> getAllProducts() {
        return dbHelper.getAllProducts();
    }

    /** Retrieves a specific product from local SQLite by its ID. */
    public Product getProductById(int productId) {
        return dbHelper.getProductById(productId);
    }

    /** Retrieves products for a specific category from local SQLite. */
    public List<Product> getProductsByCategory(int categoryId) {
        return dbHelper.getProductsByCategory(categoryId);
    }

    /** Refreshes local SQLite product data by pulling the latest from Firestore. */
    public void refreshFromFirestore() {
        sync.refreshProductsFromFirestore();
    }
}
