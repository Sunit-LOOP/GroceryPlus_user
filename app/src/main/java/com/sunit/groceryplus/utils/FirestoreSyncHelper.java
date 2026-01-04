package com.sunit.groceryplus.utils;

import android.util.Log;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.sunit.groceryplus.models.Product;
import com.sunit.groceryplus.models.User;
import com.sunit.groceryplus.models.Order;
import java.util.HashMap;
import java.util.Map;

/**
 * FirestoreSyncHelper: Mirror key SQLite entities to Firestore.
 * - Write-through: on SQLite insert/update/delete, also push to Firestore.
 * - Read-through: optionally refresh from Firestore on app start.
 *
 * This helper does NOT replace SQLite; it just mirrors selected collections to allow for 
 * cloud backup or multi-device synchronization in future.
 */
public class FirestoreSyncHelper {

    private static final String TAG = "FirestoreSyncHelper";
    private static FirestoreSyncHelper instance;
    private final FirebaseFirestore db;

    private FirestoreSyncHelper() {
        this.db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirestoreSyncHelper getInstance() {
        if (instance == null) {
            instance = new FirestoreSyncHelper();
        }
        return instance;
    }

    // ==== PRODUCTS ====
    
    /**
     * Syncs product changes to Firestore.
     * @param product The product object.
     * @param action "add", "update", or "delete".
     */
    public void syncProduct(Product product, String action) {
        CollectionReference ref = db.collection("products");
        Map<String, Object> doc = productToMap(product);
        switch (action.toLowerCase()) {
            case "add":
            case "update":
                ref.document(String.valueOf(product.getProductId())).set(doc)
                        .addOnSuccessListener(u -> Log.d(TAG, "Product synced: " + product.getProductId()))
                        .addOnFailureListener(e -> Log.e(TAG, "Product sync failed", e));
                break;
            case "delete":
                ref.document(String.valueOf(product.getProductId())).delete()
                        .addOnSuccessListener(u -> Log.d(TAG, "Product deleted from Firestore: " + product.getProductId()))
                        .addOnFailureListener(e -> Log.e(TAG, "Product delete failed", e));
                break;
        }
    }

    // ==== USERS ====
    
    /**
     * Syncs user changes to Firestore.
     */
    public void syncUser(User user, String action) {
        CollectionReference ref = db.collection("users");
        Map<String, Object> doc = userToMap(user);
        switch (action.toLowerCase()) {
            case "add":
            case "update":
                ref.document(user.getEmail()).set(doc)
                        .addOnSuccessListener(u -> Log.d(TAG, "User synced: " + user.getEmail()))
                        .addOnFailureListener(e -> Log.e(TAG, "User sync failed", e));
                break;
            case "delete":
                ref.document(user.getEmail()).delete()
                        .addOnSuccessListener(u -> Log.d(TAG, "User deleted from Firestore: " + user.getEmail()))
                        .addOnFailureListener(e -> Log.e(TAG, "User delete failed", e));
                break;
        }
    }

    // ==== ORDERS ====
    
    /**
     * Syncs order changes to Firestore.
     */
    public void syncOrder(Order order, String action) {
        CollectionReference ref = db.collection("orders");
        Map<String, Object> doc = orderToMap(order);
        switch (action.toLowerCase()) {
            case "add":
            case "update":
                ref.document(String.valueOf(order.getOrderId())).set(doc)
                        .addOnSuccessListener(u -> Log.d(TAG, "Order synced: " + order.getOrderId()))
                        .addOnFailureListener(e -> Log.e(TAG, "Order sync failed", e));
                break;
            case "delete":
                ref.document(String.valueOf(order.getOrderId())).delete()
                        .addOnSuccessListener(u -> Log.d(TAG, "Order deleted from Firestore: " + order.getOrderId()))
                        .addOnFailureListener(e -> Log.e(TAG, "Order delete failed", e));
                break;
        }
    }

    // ==== READ-THROUGH CACHE (optional) ====
    // You can call these on app start to refresh SQLite from Firestore.
    // For now, they just log; you can extend to upsert into SQLite.

    public void refreshProductsFromFirestore() {
        db.collection("products").get()
                .addOnSuccessListener(snapshot -> {
                    Log.d(TAG, "Fetched " + snapshot.size() + " products from Firestore");
                    // TODO: upsert into SQLite if you want read-through cache
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch products", e));
    }

    public void refreshUsersFromFirestore() {
        db.collection("users").get()
                .addOnSuccessListener(snapshot -> {
                    Log.d(TAG, "Fetched " + snapshot.size() + " users from Firestore");
                    // TODO: upsert into SQLite if you want read-through cache
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch users", e));
    }

    public void refreshOrdersFromFirestore() {
        db.collection("orders").get()
                .addOnSuccessListener(snapshot -> {
                    Log.d(TAG, "Fetched " + snapshot.size() + " orders from Firestore");
                    // TODO: upsert into SQLite if you want read-through cache
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch orders", e));
    }

    // ==== MAPPERS ====
    private Map<String, Object> productToMap(Product p) {
        Map<String, Object> map = new HashMap<>();
        map.put("productId", p.getProductId());
        map.put("productName", p.getProductName());
        map.put("price", p.getPrice());
        map.put("stock", p.getStockQuantity());
        map.put("categoryId", p.getCategoryId());
        map.put("vendorId", p.getVendorId());
        map.put("imageUrl", p.getImage());
        map.put("description", p.getDescription());
        map.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());
        return map;
    }

    private Map<String, Object> userToMap(User u) {
        Map<String, Object> map = new HashMap<>();
        map.put("email", u.getEmail());
        map.put("name", u.getName());
        map.put("phone", u.getPhone());
        map.put("type", u.getUserType());
        map.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());
        return map;
    }

    private Map<String, Object> orderToMap(Order o) {
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", o.getOrderId());
        map.put("userId", o.getUserId());
        map.put("totalAmount", o.getTotalAmount());
        map.put("deliveryFee", o.getDeliveryFee());
        map.put("status", o.getStatus());
        map.put("addressId", o.getAddressId());
        map.put("deliveryInstructions", o.getDeliveryInstructions());
        map.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());
        return map;
    }

    /**
     * Clear all Firestore collections.
     * Use with caution!
     */
    public void clearAllCollections() {
        WriteBatch batch = db.batch();
        
        // Clear products
        db.collection("products").get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                batch.delete(doc.getReference());
            }
        });
        
        // Clear users
        db.collection("users").get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                batch.delete(doc.getReference());
            }
        });
        
        // Clear orders
        db.collection("orders").get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                batch.delete(doc.getReference());
            }
        });
        
        batch.commit()
            .addOnSuccessListener(aVoid -> Log.d(TAG, "All collections cleared"))
            .addOnFailureListener(e -> Log.e(TAG, "Failed to clear collections", e));
    }
}
