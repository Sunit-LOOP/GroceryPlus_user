package com.sunit.groceryplus;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.sunit.groceryplus.models.User;
import androidx.appcompat.app.AppCompatActivity;

public class DatabaseTestActivity extends AppCompatActivity {
    private static final String TAG = "DatabaseTestActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Using existing layout for demo
        
        // Initialize Database and run migration
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.checkAndAssignDefaultVendor();

        // Run database tests
        testDatabaseOperations();
    }
    
    private void testDatabaseOperations() {
        try {
            // Test 1: Verify default admin insertion
            UserRepository userRepository = new UserRepository(this);
            User adminUser = userRepository.loginUser("admin@gmail.com", "admin123");
            if (adminUser != null) {
                Log.d(TAG, "Default admin user verified: " + adminUser.getName() + " (" + adminUser.getUserType() + ")");
                Toast.makeText(this, "Default admin user verified", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Failed to verify default admin user");
                Toast.makeText(this, "Failed to verify default admin user", Toast.LENGTH_SHORT).show();
            }
            
            // Test 2: Register a new user
            boolean registrationSuccess = userRepository.registerUser(
                "John Doe", 
                "john.doe@example.com", 
                "1234567890", 
                "password123", 
                "customer"
            );
            if (registrationSuccess) {
                Log.d(TAG, "User registration successful");
                Toast.makeText(this, "User registration successful", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "User registration failed");
                Toast.makeText(this, "User registration failed", Toast.LENGTH_SHORT).show();
            }
            
            // Test 3: Login with new user
            User newUser = userRepository.loginUser("john.doe@example.com", "password123");
            if (newUser != null) {
                Log.d(TAG, "User login successful: " + newUser.getName());
                Toast.makeText(this, "User login successful", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "User login failed");
                Toast.makeText(this, "User login failed", Toast.LENGTH_SHORT).show();
            }
            
            // Test 4: Add category
            CategoryRepository categoryRepository = new CategoryRepository(this);
            boolean categoryAdded = categoryRepository.addCategory("Fruits", "Fresh fruits");
            if (categoryAdded) {
                Log.d(TAG, "Category added successfully");
                Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Failed to add category");
                Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show();
            }
            
            // Test 5: Add product
            ProductRepository productRepository = new ProductRepository(this);
            boolean productAdded = productRepository.addProduct("Apple", 1, 1.99, "Fresh red apple", "apple.jpg", 100, 1);
            if (productAdded) {
                Log.d(TAG, "Product added successfully");
                Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Failed to add product");
                Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show();
            }
            
            // Test 6: Add to cart
            CartRepository cartRepository = new CartRepository(this);
            boolean itemAddedToCart = cartRepository.addToCart(newUser.getUserId(), 1, 5);
            if (itemAddedToCart) {
                Log.d(TAG, "Item added to cart successfully");
                Toast.makeText(this, "Item added to cart successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Failed to add item to cart");
                Toast.makeText(this, "Failed to add item to cart", Toast.LENGTH_SHORT).show();
            }
            
            // Test 7: Create order
            OrderRepository orderRepository = new OrderRepository(this);
            long orderId = orderRepository.createOrder(newUser.getUserId(), 9.95, 5.0, "pending", 1);
            if (orderId != -1) {
                Log.d(TAG, "Order created successfully with ID: " + orderId);
                Toast.makeText(this, "Order created successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Failed to create order");
                Toast.makeText(this, "Failed to create order", Toast.LENGTH_SHORT).show();
            }
            
            // Test 8: Add order item
            OrderItemRepository orderItemRepository = new OrderItemRepository(this);
            boolean orderItemAdded = orderItemRepository.addOrderItem((int) orderId, 1, 5, 1.99);
            if (orderItemAdded) {
                Log.d(TAG, "Order item added successfully");
                Toast.makeText(this, "Order item added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Failed to add order item");
                Toast.makeText(this, "Failed to add order item", Toast.LENGTH_SHORT).show();
            }
            
            Log.d(TAG, "All database tests completed");
            Toast.makeText(this, "All database tests completed", Toast.LENGTH_LONG).show();
            
        } catch (Exception e) {
            Log.e(TAG, "Error during database tests", e);
            Toast.makeText(this, "Error during database tests: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}