package com.sunit.groceryplus;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.WriteBatch;
import java.util.*;

/**
 * FirestoreSeeder: One‑time script to populate Firestore with sample data for GroceryPlus.
 * Call FirestoreSeeder.seedOnce(); from e.g. SplashScreenActivity or a debug menu.
 */
public class FirestoreSeeder {

    private static final String TAG = "FirestoreSeeder";
    private static final String PREF_KEY_SEEDED = "firestore_seeded";

    public static void seedOnce(FirebaseFirestore db, java.util.concurrent.Executor executor, Runnable onComplete) {
        // Simple guard: run only once per install
        if (isAlreadySeeded()) {
            Log.i(TAG, "Firestore already seeded. Skipping.");
            if (onComplete != null) onComplete.run();
            return;
        }

        WriteBatch batch = db.batch();

        // 1. Categories
        CollectionReference categories = db.collection("categories");
        Map<String, Object> cat1 = Map.of(
                "name", "Dairy",
                "createdAt", new Date()
        );
        Map<String, Object> cat2 = Map.of(
                "name", "Vegetables",
                "createdAt", new Date()
        );
        Map<String, Object> cat3 = Map.of(
                "name", "Fruits",
                "createdAt", new Date()
        );
        batch.set(categories.document(), cat1);
        batch.set(categories.document(), cat2);
        batch.set(categories.document(), cat3);

        // 2. Vendors
        CollectionReference vendors = db.collection("vendors");
        Map<String, Object> ven1 = Map.of(
                "name", "Fresh Dairy Co",
                "phone", "9876543210",
                "createdAt", new Date()
        );
        Map<String, Object> ven2 = Map.of(
                "name", "Green Veggies",
                "phone", "9876543211",
                "createdAt", new Date()
        );
        batch.set(vendors.document(), ven1);
        batch.set(vendors.document(), ven2);

        // 3. Users (admin + customer)
        CollectionReference users = db.collection("users");
        Map<String, Object> admin = Map.of(
                "email", "admin@gmail.com",
                "name", "Admin",
                "phone", "9999999999",
                "type", "admin",
                "createdAt", new Date()
        );
        Map<String, Object> customer = Map.of(
                "email", "ram@gmail.com",
                "name", "Ram",
                "phone", "9123456789",
                "type", "customer",
                "createdAt", new Date()
        );
        batch.set(users.document("admin@gmail.com"), admin);
        batch.set(users.document("ram@gmail.com"), customer);

        // 4. Products
        CollectionReference products = db.collection("products");
        Map<String, Object> milk = Map.of(
                "name", "Organic Milk",
                "price", 45.5,
                "stock", 20,
                "categoryId", "cat_001",
                "vendorId", "ven_001",
                "imageUrl", "milk.png",
                "createdAt", new Date()
        );
        Map<String, Object> tomato = Map.of(
                "name", "Fresh Tomato",
                "price", 12.0,
                "stock", 50,
                "categoryId", "cat_002",
                "vendorId", "ven_002",
                "imageUrl", "tomato.png",
                "createdAt", new Date()
        );
        Map<String, Object> apple = Map.of(
                "name", "Red Apple",
                "price", 80.0,
                "stock", 30,
                "categoryId", "cat_003",
                "vendorId", "ven_002",
                "imageUrl", "apple.png",
                "createdAt", new Date()
        );
        batch.set(products.document(), milk);
        batch.set(products.document(), tomato);
        batch.set(products.document(), apple);

        // 5. Addresses (for customer)
        CollectionReference addresses = db.collection("addresses");
        Map<String, Object> addr1 = Map.of(
                "userId", "ram@gmail.com",
                "addressLine", "123 Main St",
                "city", "Kathmandu",
                "pincode", "44600",
                "isDefault", true,
                "createdAt", new Date()
        );
        batch.set(addresses.document(), addr1);

        // 6. Orders (sample order)
        CollectionReference orders = db.collection("orders");
        Map<String, Object> order1 = Map.of(
                "userId", "ram@gmail.com",
                "totalAmount", 137.5,
                "deliveryFee", 20.0,
                "status", "PENDING",
                "addressId", "addr_001",
                "deliveryInstructions", "Leave at door",
                "createdAt", new Date()
        );
        batch.set(orders.document(), order1);

        // 7. OrderItems
        CollectionReference orderItems = db.collection("orderItems");
        Map<String, Object> oi1 = Map.of(
                "orderId", "order_001",
                "productId", "prod_001",
                "quantity", 2,
                "price", 45.5,
                "createdAt", new Date()
        );
        Map<String, Object> oi2 = Map.of(
                "orderId", "order_001",
                "productId", "prod_002",
                "quantity", 3,
                "price", 12.0,
                "createdAt", new Date()
        );
        batch.set(orderItems.document(), oi1);
        batch.set(orderItems.document(), oi2);

        // 8. Cart (sample cart items)
        CollectionReference cart = db.collection("cart");
        Map<String, Object> cartItem1 = Map.of(
                "userId", "ram@gmail.com",
                "productId", "prod_001",
                "quantity", 1,
                "createdAt", new Date()
        );
        batch.set(cart.document(), cartItem1);

        // 9. Notifications
        CollectionReference notifications = db.collection("notifications");
        Map<String, Object> notif1 = Map.of(
                "userId", "ram@gmail.com",
                "title", "Order Placed",
                "message", "Your order #order_001 has been placed successfully.",
                "type", "ORDER",
                "refId", "order_001",
                "createdAt", new Date()
        );
        batch.set(notifications.document(), notif1);

        // Commit batch
        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Log.i(TAG, "Firestore seeded successfully.");
                    markSeeded();
                    if (onComplete != null) onComplete.run();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore seeding failed", e);
                    if (onComplete != null) onComplete.run();
                });
    }

    private static boolean isAlreadySeeded() {
        // Use SharedPreferences to remember seeding status
        // For simplicity, using a static flag; replace with proper SharedPreferences in your app
        return false; // Replace with real check
    }

    private static void markSeeded() {
        // Store in SharedPreferences that seeding is done
        // For simplicity, omitted here
    }
}

/*
Usage example (from SplashScreenActivity or a debug menu):

FirebaseFirestore db = FirebaseFirestore.getInstance();
FirestoreSeeder.seedOnce(db, Runnable::run, () -> {
    // Optional: show a toast or log
});
*/
