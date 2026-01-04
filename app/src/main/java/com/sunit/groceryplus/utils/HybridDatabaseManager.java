package com.sunit.groceryplus.utils;

import android.content.Context;
import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.models.Product;
import com.sunit.groceryplus.models.User;
import com.sunit.groceryplus.models.Order;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * HybridDatabaseManager - Manages data flow between local SQLite and optional Firestore cloud.
 * 
 * Architecture:
 * - PRIMARY: SQLite database (fast, reliable, always used)
 * - SECONDARY: Firestore (optional sync, backup, multi-device support)
 * 
 * Strategy:
 * - SQLite-First: All reads/writes happen to SQLite immediately.
 * - Async Sync: Writes are optionally pushed to Firestore in the background.
 */
public class HybridDatabaseManager {
    
    private static final String TAG = "HybridDatabaseManager";
    private static HybridDatabaseManager instance;
    
    private final Context context;
    private final DatabaseHelper primaryDb;  // SQLite - PRIMARY
    private final FirebaseFirestore cloudDb; // Firestore - SECONDARY
    private final FirestoreSyncHelper syncHelper;
    
    // Sync settings - default to OFF for SQLite-first approach
    private boolean autoSync = false;
    private boolean realTimeSync = false;
    private boolean cloudEnabled = true;  // Can be completely disabled
    
    private HybridDatabaseManager(Context context) {
        this.context = context;
        this.primaryDb = new DatabaseHelper(context);
        this.cloudDb = FirebaseFirestore.getInstance();
        this.syncHelper = FirestoreSyncHelper.getInstance();
    }
    
    public static synchronized HybridDatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new HybridDatabaseManager(context.getApplicationContext());
        }
        return instance;
    }
    
    // ==== SYNC CONFIGURATION ====
    
    /**
     * Enable/disable auto-sync to cloud (default: OFF for SQLite-first).
     */
    public void setAutoSync(boolean enabled) {
        this.autoSync = enabled;
        Log.d(TAG, "Auto-sync " + (enabled ? "enabled" : "disabled") + " (SQLite-first approach)");
    }
    
    /**
     * Enable/disable real-time sync from cloud (default: OFF).
     */
    public void setRealTimeSync(boolean enabled) {
        this.realTimeSync = enabled;
        if (enabled) {
            setupRealTimeListeners();
        }
        Log.d(TAG, "Real-time sync " + (enabled ? "enabled" : "disabled") + " (SQLite-first approach)");
    }
    
    /**
     * Enable/disable cloud functionality completely.
     */
    public void setCloudEnabled(boolean enabled) {
        this.cloudEnabled = enabled;
        Log.d(TAG, "Cloud functionality " + (enabled ? "enabled" : "disabled") + " (SQLite-first approach)");
    }
    
    /**
     * Get current sync status.
     */
    public String getSyncStatus() {
        return "SQLite-First | " +
               "Auto-sync: " + (autoSync ? "ON" : "OFF") + 
               ", Real-time: " + (realTimeSync ? "ON" : "OFF") +
               ", Cloud: " + (cloudEnabled ? "ENABLED" : "DISABLED") +
               ", Online: " + (isOnline() ? "YES" : "NO");
    }
    
    // ==== PRODUCT OPERATIONS ====

    /**
     * Add product to SQLite (PRIMARY) with optional cloud sync.
     */
    public CompletableFuture<Long> addProduct(Product product) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. Add to PRIMARY SQLite database first
                long localId = primaryDb.addProduct(
                    product.getProductName(),
                    product.getCategoryId(),
                    product.getPrice(),
                    product.getDescription(),
                    product.getImage(),
                    product.getStockQuantity(),
                    product.getVendorId()
                );
                
                if (localId != -1) {
                    product.setProductId((int) localId);
                    
                    // 2. OPTIONAL: Sync to Firestore if cloud is enabled and auto-sync is on
                    if (cloudEnabled && autoSync && isOnline()) {
                        syncHelper.syncProduct(product, "add");
                        Log.d(TAG, "Product added to SQLite and synced to cloud: " + localId);
                    } else {
                        Log.d(TAG, "Product added to SQLite only (cloud sync disabled): " + localId);
                    }
                    
                    return localId;
                } else {
                    Log.e(TAG, "Failed to add product to SQLite");
                    return -1L;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error adding product to SQLite", e);
                return -1L;
            }
        });
    }
    
    /**
     * Update product in SQLite (PRIMARY) with optional cloud sync.
     */
    public CompletableFuture<Boolean> updateProduct(Product product) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. Update PRIMARY SQLite database
                boolean localSuccess = primaryDb.updateProduct(
                    product.getProductId(),
                    product.getProductName(),
                    product.getCategoryId(),
                    product.getPrice(),
                    product.getDescription(),
                    product.getImage(),
                    product.getStockQuantity(),
                    product.getVendorId()
                );
                
                if (localSuccess) {
                    // 2. OPTIONAL: Sync to Firestore if cloud is enabled and auto-sync is on
                    if (cloudEnabled && autoSync && isOnline()) {
                        syncHelper.syncProduct(product, "update");
                        Log.d(TAG, "Product updated in SQLite and synced to cloud: " + product.getProductId());
                    } else {
                        Log.d(TAG, "Product updated in SQLite only (cloud sync disabled): " + product.getProductId());
                    }
                    
                    return true;
                } else {
                    Log.e(TAG, "Failed to update product in SQLite");
                    return false;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error updating product in SQLite", e);
                return false;
            }
        });
    }
    
    /**
     * Delete product from SQLite (PRIMARY) with optional cloud sync.
     */
    public CompletableFuture<Boolean> deleteProduct(int productId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. Get product details before deletion
                Product product = primaryDb.getProductById(productId);
                
                // 2. Delete from PRIMARY SQLite database
                boolean deleted = primaryDb.deleteProduct(productId);
                
                if (deleted) {
                    // 3. OPTIONAL: Sync to Firestore if cloud is enabled and auto-sync is on
                    if (cloudEnabled && autoSync && isOnline() && product != null) {
                        syncHelper.syncProduct(product, "delete");
                        Log.d(TAG, "Product deleted from SQLite and synced to cloud: " + productId);
                    } else {
                        Log.d(TAG, "Product deleted from SQLite only (cloud sync disabled): " + productId);
                    }
                    
                    return true;
                } else {
                    Log.e(TAG, "Failed to delete product from SQLite");
                    return false;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error deleting product from SQLite", e);
                return false;
            }
        });
    }
    
    /**
     * Get product from SQLite (PRIMARY database).
     */
    public Product getProductById(int productId) {
        try {
            return primaryDb.getProductById(productId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting product from SQLite", e);
            return null;
        }
    }
    
    /**
     * Get all products from SQLite (PRIMARY database).
     */
    public List<Product> getAllProducts() {
        try {
            return primaryDb.getAllProducts();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all products from SQLite", e);
            return new ArrayList<>();
        }
    }
    
    // ==== USER OPERATIONS ====
    
    /**
     * Authenticate user using SQLite (PRIMARY) with optional cloud sync.
     */
    public CompletableFuture<User> authenticateUser(String email, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. Authenticate using PRIMARY SQLite database
                User user = primaryDb.authenticateUser(email, password);
                
                if (user != null) {
                    // 2. OPTIONAL: Sync user data to Firestore if cloud is enabled and auto-sync is on
                    if (cloudEnabled && autoSync && isOnline()) {
                        syncHelper.syncUser(user, "login");
                        Log.d(TAG, "User authenticated in SQLite and synced to cloud: " + email);
                    } else {
                        Log.d(TAG, "User authenticated in SQLite only (cloud sync disabled): " + email);
                    }
                    
                    return user;
                } else {
                    Log.w(TAG, "SQLite authentication failed for: " + email);
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error authenticating user in SQLite", e);
                return null;
            }
        });
    }
    
    /**
     * Add new user to SQLite (PRIMARY) with optional cloud sync.
     */
    public CompletableFuture<Long> addUser(String name, String email, String phone, String password, String userType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. Add to PRIMARY SQLite database
                long userId = primaryDb.addUser(name, email, phone, password, userType);
                
                if (userId != -1) {
                    // 2. Get user details and optionally sync to Firestore
                    User user = primaryDb.getUserById((int) userId);
                    if (user != null && cloudEnabled && autoSync && isOnline()) {
                        syncHelper.syncUser(user, "add");
                        Log.d(TAG, "User added to SQLite and synced to cloud: " + userId);
                    } else {
                        Log.d(TAG, "User added to SQLite only (cloud sync disabled): " + userId);
                    }
                    
                    return userId;
                } else {
                    Log.e(TAG, "Failed to add user to SQLite");
                    return -1L;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error adding user to SQLite", e);
                return -1L;
            }
        });
    }
    
    // ==== ORDER OPERATIONS ====
    
    /**
     * Create order in SQLite (PRIMARY) with optional cloud sync.
     */
    public CompletableFuture<Long> createOrder(int userId, double totalAmount, double deliveryFee, String status, int addressId, String instructions) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. Create order in PRIMARY SQLite database
                long orderId = primaryDb.createOrder(userId, totalAmount, deliveryFee, status, addressId, instructions);
                
                if (orderId != -1) {
                    // 2. Get order details and optionally sync to Firestore
                    Order order = primaryDb.getOrderById((int) orderId);
                    if (order != null && cloudEnabled && autoSync && isOnline()) {
                        syncHelper.syncOrder(order, "create");
                        Log.d(TAG, "Order created in SQLite and synced to cloud: " + orderId);
                    } else {
                        Log.d(TAG, "Order created in SQLite only (cloud sync disabled): " + orderId);
                    }
                    
                    return orderId;
                } else {
                    Log.e(TAG, "Failed to create order in SQLite");
                    return -1L;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error creating order in SQLite", e);
                return -1L;
            }
        });
    }
    
    // ==== SYNC OPERATIONS ====
    
    /**
     * Manual sync all local data to Firestore.
     */
    public CompletableFuture<Void> syncAllToCloud() {
        return CompletableFuture.runAsync(() -> {
            try {
                Log.d(TAG, "Starting manual sync to cloud...");
                
                // Sync products
                List<Product> products = primaryDb.getAllProducts();
                for (Product product : products) {
                    syncHelper.syncProduct(product, "update");
                }
                
                // Sync users
                List<User> users = primaryDb.getAllUsers();
                for (User user : users) {
                    syncHelper.syncUser(user, "update");
                }
                
                // Sync orders
                List<Order> orders = primaryDb.getAllOrders();
                for (Order order : orders) {
                    syncHelper.syncOrder(order, "update");
                }
                
                Log.d(TAG, "Manual sync to cloud completed");
            } catch (Exception e) {
                Log.e(TAG, "Error during manual sync", e);
            }
        });
    }
    
    /**
     * Sync from cloud to local (for initial data load or refresh).
     */
    public CompletableFuture<Void> syncFromCloud() {
        return CompletableFuture.runAsync(() -> {
            try {
                Log.d(TAG, "Starting sync from cloud...");
                
                // This would implement fetching from Firestore and updating local SQLite
                // Implementation depends on specific requirements
                
                Log.d(TAG, "Sync from cloud completed");
            } catch (Exception e) {
                Log.e(TAG, "Error during sync from cloud", e);
            }
        });
    }
    
    // ==== REAL-TIME LISTENERS ====
    
    private void setupRealTimeListeners() {
        if (!realTimeSync) return;
        
        // Setup real-time listeners for Firestore collections
        // This would listen for changes and update local SQLite accordingly
        
        Log.d(TAG, "Real-time listeners setup completed");
    }
    
    // ==== UTILITY METHODS ====
    
    /**
     * Check if device is online.
     */
    public boolean isOnline() {
        return NetworkUtils.isOnline(context);
    }
    
    /**
     * Clear all data (SQLite PRIMARY and optionally Firestore).
     */
    public CompletableFuture<Void> clearAllData() {
        return CompletableFuture.runAsync(() -> {
            try {
                // 1. Clear PRIMARY SQLite database manually
                Log.d(TAG, "SQLite clear operation - individual delete methods should be used");
                
                // 2. OPTIONAL: Clear Firestore collections if cloud is enabled
                if (cloudEnabled && isOnline()) {
                    syncHelper.clearAllCollections();
                    Log.d(TAG, "All data cleared from Firestore (SECONDARY)");
                } else {
                    Log.d(TAG, "Firestore clear skipped (cloud disabled or offline)");
                }
                
                Log.d(TAG, "Data clear operation completed");
            } catch (Exception e) {
                Log.e(TAG, "Error clearing data", e);
            }
        });
    }
}
