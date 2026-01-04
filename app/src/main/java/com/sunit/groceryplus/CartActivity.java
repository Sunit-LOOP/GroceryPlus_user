package com.sunit.groceryplus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.adapters.CartAdapter;
import com.sunit.groceryplus.models.User;
import com.sunit.groceryplus.models.CartItem;
// Fixed import - CartRepository is in the same package
import com.sunit.groceryplus.CartRepository;


import java.util.ArrayList;
import java.util.List;


/**
 * CartActivity - Manages the user's shopping cart.
 * 
 * This activity displays the items currently in the user's cart, calculates totals (including delivery fees),
 * and handles quantity adjustments or item removals. It also enforces minimum order values
 * and guides the user towards the checkout process.
 * 
 * Key Features:
 * - List view of cart items
 * - Real-time total calculation
 * - Quantity adjustment (increment/decrement)
 * - Item removal
 * - Clear cart functionality
 * - Free delivery progress tracking
 * - Minimum order value check
 * - Navigation to Payment
 * 
 * @author GroceryPlus Development Team
 * @version 1.0
 * @since 1.0
 */
public class CartActivity extends AppCompatActivity {

    private static final String TAG = "CartActivity";
    
    // UI Components
    private RecyclerView cartRecyclerView;
    private TextView emptyCartTv;
    private TextView totalPriceTv;
    private TextView freeDeliveryTv;
    private com.google.android.material.progressindicator.LinearProgressIndicator freeDeliveryProgress;
    private View freeDeliveryGoalCard;
    private Button checkoutBtn;
    
    // Repositories & Adapters
    private CartRepository cartRepository;
    private CartAdapter cartAdapter;

    // Data State
    private List<CartItem> cartItems;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        
        // Get user ID from intent
        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Invalid user", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Initialize repositories
        cartRepository = new CartRepository(this);

        
        // Initialize views
        initViews();
        
        // Setup RecyclerView
        setupRecyclerView();

        // Setup Bottom Navigation
        com.sunit.groceryplus.utils.NavigationHelper.setupNavigation(this, userId);
        
        // Set click listeners
        setClickListeners();
        
        // Load cart items
        loadCartItems();
        
        // Setup Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.cartToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Cart");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_clear_cart) {
            showClearCartConfirmation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void initViews() {
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        emptyCartTv = findViewById(R.id.emptyCartTv);
        totalPriceTv = findViewById(R.id.cartTotalPriceTv);
        freeDeliveryTv = findViewById(R.id.freeDeliveryTv);
        freeDeliveryProgress = findViewById(R.id.freeDeliveryProgress);
        freeDeliveryGoalCard = findViewById(R.id.freeDeliveryGoalCard);
        checkoutBtn = findViewById(R.id.checkoutBtn);
    }
    
    private void setupRecyclerView() {
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartItems, new CartAdapter.OnQuantityChangeListener() {
            @Override
            public void onQuantityChanged(int cartItemId, int newQuantity) {
                updateQuantity(cartItemId, newQuantity);
            }

            @Override
            public void onItemRemoved(int cartItemId) {
                removeItem(cartItemId);
            }
        });
        cartRecyclerView.setAdapter(cartAdapter);
    }
    
    private void loadCartItems() {
        loadCartItemsFromDatabase();
    }

    private void loadCartItemsFromDatabase() {
        try {
            cartItems = cartRepository.getCartItems(userId);

            if (cartItems != null && !cartItems.isEmpty()) {
                cartAdapter.updateCartItems(cartItems);
                updateTotalPrice();
                showCart();
            } else {
                showEmptyCart();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading cart items from database", e);
            Toast.makeText(CartActivity.this, "Error loading cart", Toast.LENGTH_SHORT).show();
            showEmptyCart();
        }
    }

    private CartItem parseCartItemFromJson(org.json.JSONObject itemJson) throws org.json.JSONException {
        CartItem cartItem = new CartItem();
        cartItem.setCartId(itemJson.optInt("cart_id", 0));
        cartItem.setUserId(userId);
        cartItem.setProductId(itemJson.optInt("product_id", 0));
        cartItem.setProductName(itemJson.optString("product_name", ""));
        cartItem.setPrice(itemJson.optDouble("price", 0.0));
        cartItem.setQuantity(itemJson.optInt("quantity", 1));
        cartItem.setImage(itemJson.optString("image", null));
        return cartItem;
    }
    
    private void updateQuantity(int cartItemId, int newQuantity) {
        updateQuantityDatabase(cartItemId, newQuantity);
    }

    private void updateQuantityDatabase(int cartItemId, int newQuantity) {
        try {
            boolean success = cartRepository.updateCartQuantity(cartItemId, newQuantity);

            if (success) {
                // Update the item in our list
                for (CartItem item : cartItems) {
                    if (item.getCartId() == cartItemId) {
                        item.setQuantity(newQuantity);
                        break;
                    }
                }
                cartAdapter.notifyDataSetChanged();
                updateTotalPrice();
                Toast.makeText(this, "Quantity updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update quantity", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating quantity", e);
            Toast.makeText(this, "Error updating quantity", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showClearCartConfirmation() {
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is already empty", Toast.LENGTH_SHORT).show();
            return;
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Clear Cart")
                .setMessage("Are you sure you want to remove all items from your cart?")
                .setPositiveButton("Clear All", (dialog, which) -> clearCart())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearCart() {
        try {
            boolean success = cartRepository.clearCart(userId);
            if (success) {
                cartItems.clear();
                cartAdapter.notifyDataSetChanged();
                updateTotalPrice();
                showEmptyCart();
                Toast.makeText(this, "Cart cleared", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to clear cart", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error clearing cart", e);
            Toast.makeText(this, "Error clearing cart", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void removeItem(int cartItemId) {
        removeItemDatabase(cartItemId);
    }

    private void removeItemDatabase(int cartItemId) {
        try {
            boolean success = cartRepository.removeFromCart(cartItemId);

            if (success) {
                // Remove the item from our list
                CartItem itemToRemove = null;
                for (CartItem item : cartItems) {
                    if (item.getCartId() == cartItemId) {
                        itemToRemove = item;
                        break;
                    }
                }

                if (itemToRemove != null) {
                    cartItems.remove(itemToRemove);
                }

                cartAdapter.notifyDataSetChanged();
                updateTotalPrice();
                Toast.makeText(this, "Item removed from cart", Toast.LENGTH_SHORT).show();

                if (cartItems.isEmpty()) {
                    showEmptyCart();
                }
            } else {
                Toast.makeText(this, "Failed to remove item", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error removing item", e);
            Toast.makeText(this, "Error removing item", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateTotalPrice() {
        double total = cartAdapter.getTotalPrice();
        double totalWithDelivery = cartAdapter.getTotalPriceWithDelivery();
        totalPriceTv.setText("Total: Rs. " + String.format("%.2f", total) + " (Including Delivery: Rs. " + String.format("%.2f", totalWithDelivery) + ")");
        updateFreeDeliveryGoal(total);
    }

    private void updateFreeDeliveryGoal(double total) {
        double threshold = com.sunit.groceryplus.utils.PaymentConfig.FREE_DELIVERY_THRESHOLD;
        if (total <= 0) {
            freeDeliveryGoalCard.setVisibility(View.GONE);
        } else if (total >= threshold) {
            freeDeliveryGoalCard.setVisibility(View.VISIBLE);
            freeDeliveryTv.setText("Yay! You get FREE delivery!");
            freeDeliveryProgress.setProgress(100);
        } else {
            freeDeliveryGoalCard.setVisibility(View.VISIBLE);
            double gap = threshold - total;
            freeDeliveryTv.setText("Add Rs. " + String.format("%.0f", gap) + " more for FREE delivery");
            int progress = (int) ((total / threshold) * 100);
            freeDeliveryProgress.setProgress(progress);
        }
    }
    
    private void showCart() {
        cartRecyclerView.setVisibility(View.VISIBLE);
        totalPriceTv.setVisibility(View.VISIBLE);
        checkoutBtn.setVisibility(View.VISIBLE);
        emptyCartTv.setVisibility(View.GONE);
    }
    
    private void showEmptyCart() {
        cartRecyclerView.setVisibility(View.GONE);
        totalPriceTv.setVisibility(View.GONE);
        checkoutBtn.setVisibility(View.GONE);
        emptyCartTv.setVisibility(View.VISIBLE);
    }
    
    private void setClickListeners() {
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedToCheckout();
            }
        });
    }
    
    private void proceedToCheckout() {
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        
        double total = cartAdapter.getTotalPrice();
        double minOrder = 200.0; // Minimum order value
        
        if (total < minOrder) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Minimum Order Value")
                    .setMessage("A minimum order of Rs. " + String.format("%.2f", minOrder) + " is required to checkout. Your current total is Rs. " + String.format("%.2f", total))
                    .setPositiveButton("Add More Items", null)
                    .show();
            return;
        }

        try {
            // Calculate total with delivery fee
            double subtotal = cartAdapter.getTotalPrice();
            double totalWithDelivery = cartAdapter.getTotalPriceWithDelivery();
            int itemCount = cartItems.size();
            
            // Navigate to payment activity
            Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("total_amount", totalWithDelivery);
            intent.putExtra("subtotal_amount", subtotal);
            intent.putExtra("total_items", itemCount);
            startActivity(intent);
            
        } catch (Exception e) {
            Log.e(TAG, "Error during checkout", e);
            Toast.makeText(this, "Error processing checkout", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }
}