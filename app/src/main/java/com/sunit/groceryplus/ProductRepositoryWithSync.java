package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;
import com.sunit.groceryplus.models.Product;
import com.sunit.groceryplus.utils.FirestoreSyncHelper;
import java.util.List;

/**
 * ProductRepositoryWithSync: Local-first SQLite + Firestore mirror.
 * - All reads go to SQLite (fast, offline).
 * - All writes go to SQLite first, then mirror to Firestore (non-blocking).
 */
public class ProductRepositoryWithSync {

    private static final String TAG = "ProductRepoWithSync";
    private final DatabaseHelper dbHelper;
    private final FirestoreSyncHelper sync;

    public ProductRepositoryWithSync(Context context) {
        this.dbHelper = new DatabaseHelper(context);
        this.sync = FirestoreSyncHelper.getInstance();
    }

    // ==== CREATE ====
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

    // ==== UPDATE ====
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

    // ==== DELETE ====
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

    // ==== READS (local only) ====
    public List<Product> getAllProducts() {
        return dbHelper.getAllProducts();
    }

    public Product getProductById(int productId) {
        return dbHelper.getProductById(productId);
    }

    public List<Product> getProductsByCategory(int categoryId) {
        return dbHelper.getProductsByCategory(categoryId);
    }

    // ==== OPTIONAL READ-THROUGH REFRESH ====
    // Call on app start if you want to pull latest from Firestore into SQLite
    public void refreshFromFirestore() {
        sync.refreshProductsFromFirestore();
    }
}
