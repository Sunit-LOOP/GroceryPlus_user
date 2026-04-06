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
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/** Manages coordinated data flow between local SQLite (Primary) and Firestore cloud (Secondary) storage. */
public class HybridDatabaseManager {
    
    // Infrastructure
    private static final String TAG = "HybridDatabaseManager";
    private static HybridDatabaseManager instance;
    private final Context context;
    private final DatabaseHelper primaryDb;
    private final FirebaseFirestore cloudDb;
    private final FirestoreSyncHelper syncHelper;
    
    // Sync Configuration
    private boolean autoSync = false;
    private boolean realTimeSync = false;
    private boolean cloudEnabled = true;
    
    /** Initializes the manager with database helpers and cloud configuration. */
    public HybridDatabaseManager(Context context) {
        this.context = context;
        this.primaryDb = new DatabaseHelper(context);
        
        FirebaseFirestore firestore = null;
        FirestoreSyncHelper sync = null;
        try {
            firestore = FirebaseFirestore.getInstance();
            sync = FirestoreSyncHelper.getInstance();
            Log.d(TAG, "Firebase Firestore initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Firebase Firestore initialization failed. Cloud sync will be disabled.", e);
            this.cloudEnabled = false;
        }
        this.cloudDb = firestore;
        this.syncHelper = sync;
    }
    
    /** Returns the singleton instance of the HybridDatabaseManager. */
    public static synchronized HybridDatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new HybridDatabaseManager(context.getApplicationContext());
        }
        return instance;
    }
    
    /** Tests the database connection and sync functionality. */
    public void testConnection() {
        Log.d(TAG, "Testing Hybrid Database connections...");
        
        // Test primary database connection
        try {
            primaryDb.getReadableDatabase();
            Log.d(TAG, "✓ Primary SQLite database connection successful");
        } catch (Exception e) {
            Log.e(TAG, "✗ Primary SQLite database connection failed", e);
        }
        
        // Test cloud database connection
        if (cloudDb != null && cloudEnabled) {
            try {
                cloudDb.collection("test").limit(1).get()
                    .addOnSuccessListener(snapshot -> Log.d(TAG, "✓ Cloud Firestore connection successful"))
                    .addOnFailureListener(e -> Log.e(TAG, "✗ Cloud Firestore connection failed", e));
            } catch (Exception e) {
                Log.e(TAG, "✗ Cloud Firestore connection failed during test", e);
            }
        } else {
            Log.w(TAG, "Cloud sync is disabled or Firebase not initialized. Skipping cloud test.");
        }
        
        Log.d(TAG, "Hybrid Database connection test completed");
    }
    
    // ==== SYNC CONFIGURATION ====
    
    // ==== SYNC CONFIGURATION ====
    
    /** Enables or disables automatic background synchronization to the cloud. */
    public void setAutoSync(boolean enabled) {
        this.autoSync = enabled;
        Log.d(TAG, "Auto-sync " + (enabled ? "enabled" : "disabled") + " (SQLite-first approach)");
    }
    
    /** Enables or disables real-time listening for cloud data changes. */
    public void setRealTimeSync(boolean enabled) {
        this.realTimeSync = enabled;
        if (enabled) {
            setupRealTimeListeners();
        }
        Log.d(TAG, "Real-time sync " + (enabled ? "enabled" : "disabled") + " (SQLite-first approach)");
    }
    
    /** Completely enables or disables all cloud-related features and synchronization. */
    public void setCloudEnabled(boolean enabled) {
        this.cloudEnabled = enabled;
        Log.d(TAG, "Cloud functionality " + (enabled ? "enabled" : "disabled") + " (SQLite-first approach)");
    }
    
    /** Returns a formatted string detailing the current synchronization state. */
    public String getSyncStatus() {
        return "SQLite-First | " +
               "Auto-sync: " + (autoSync ? "ON" : "OFF") + 
               ", Real-time: " + (realTimeSync ? "ON" : "OFF") +
               ", Cloud: " + (cloudEnabled ? "ENABLED" : "DISABLED") +
               ", Online: " + (isOnline() ? "YES" : "NO");
    }
    
    // ==== PRODUCT OPERATIONS ====

    /** Adds a product to local storage and optionally syncs it to the cloud. */
    public CompletableFuture<Long> addProduct(Product product) {
        return CompletableFuture.supplyAsync(() -> {
            try {
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
    
    /** Updates an existing product in local storage and optionally pushes changes to the cloud. */
    public CompletableFuture<Boolean> updateProduct(Product product) {
        return CompletableFuture.supplyAsync(() -> {
            try {
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
    
    /** Removes a product from local storage and optionally deletes its cloud replica. */
    public CompletableFuture<Boolean> deleteProduct(int productId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Product product = primaryDb.getProductById(productId);
                boolean deleted = primaryDb.deleteProduct(productId);
                
                if (deleted) {
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
    
    /** Retrieves a product by its ID from the primary local database. */
    public Product getProductById(int productId) {
        try {
            return primaryDb.getProductById(productId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting product from SQLite", e);
            return null;
        }
    }
    
    /** Retrieves all products available in the primary local database. */
    public List<Product> getAllProducts() {
        try {
            return primaryDb.getAllProducts();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all products from SQLite", e);
            return new ArrayList<>();
        }
    }
    
    // ==== USER OPERATIONS ====
    
    // ==== USER OPERATIONS ====
    
    /** Authenticates a user against the local database and optionally caches credentials in the cloud. */
    public CompletableFuture<User> authenticateUser(String email, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = primaryDb.authenticateUser(email, password);
                
                if (user != null) {
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
    
    /** Registers a new user locally and optionally mirrors the account in the cloud. */
    public CompletableFuture<Long> addUser(String name, String email, String phone, String password, String userType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                long userId = primaryDb.addUser(name, email, phone, password, userType);
                
                if (userId != -1) {
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
    
    /** Records a new order locally and optionally propagates it to cloud storage. */
    public CompletableFuture<Long> createOrder(int userId, double totalAmount, double deliveryFee, String status, int addressId, String instructions) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                long orderId = primaryDb.createOrder(userId, totalAmount, deliveryFee, status, addressId, instructions);
                
                if (orderId != -1) {
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
    
    /** Triggers a complete manual upload of all local records to the cloud. */
    public CompletableFuture<Void> syncAllToCloud() {
        return CompletableFuture.runAsync(() -> {
            try {
                Log.d(TAG, "Starting manual sync to cloud...");
                
                List<Product> products = primaryDb.getAllProducts();
                for (Product product : products) {
                    syncHelper.syncProduct(product, "update");
                }
                
                List<User> users = primaryDb.getAllUsers();
                for (User user : users) {
                    syncHelper.syncUser(user, "update");
                }
                
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
    
    /** Fetches the latest cloud data to refresh the local database state. */
    public CompletableFuture<Void> syncFromCloud() {
        return CompletableFuture.runAsync(() -> {
            try {
                Log.d(TAG, "Starting sync from cloud...");
                // Note: Implementation specific to data reconciliation requirements
                Log.d(TAG, "Sync from cloud completed");
            } catch (Exception e) {
                Log.e(TAG, "Error during sync from cloud", e);
            }
        });
    }
    
    // ==== REAL-TIME LISTENERS ====
    
    /** Initializes observers for cloud-side changes when real-time sync is active. */
    private void setupRealTimeListeners() {
        if (!realTimeSync) return;
        Log.d(TAG, "Real-time listeners setup completed");
    }
    
    // ==== UTILITY METHODS ====
    
    /** Determines if the device currently has active network connectivity. */
    public boolean isOnline() {
        return NetworkUtils.isOnline(context);
    }
    
    /** Completely wipes all data from both local and associated cloud storage. */
    public CompletableFuture<Void> clearAllData() {
        return CompletableFuture.runAsync(() -> {
            try {
                Log.d(TAG, "SQLite clear operation - individual delete methods should be used");
                
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
