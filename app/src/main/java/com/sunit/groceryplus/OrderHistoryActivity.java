package com.sunit.groceryplus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.adapters.OrderAdapter;
import com.sunit.groceryplus.models.Order;
import com.sunit.groceryplus.models.OrderItem;
import com.sunit.groceryplus.utils.GroceryNotificationManager;

import java.util.ArrayList;
import java.util.List;


/** OrderHistoryActivity - User interface for browsing past orders, reordering items, and tracking status. */
public class OrderHistoryActivity extends AppCompatActivity {

    // Infrastructure & UI
    private static final String TAG = "OrderHistoryActivity";
    private RecyclerView ordersRecyclerView;
    private TextView emptyOrdersTv;

    // Data & Repositories
    private int userId;
    private OrderRepository orderRepository;
    private OrderAdapter orderAdapter;
    private List<Order> orders = new ArrayList<>();

    /** Initializes the activity, verifies user session, and loads order data. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Get user ID from intent
        userId = getIntent().getIntExtra("user_id", -1);
        
        if (userId == -1) {
            // Try to get from SharedPreferences
            android.content.SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            userId = sharedPreferences.getInt("userId", -1);
        }
        
        if (userId == -1) {
            Toast.makeText(this, "Error: Invalid user session", Toast.LENGTH_SHORT).show();
            // Redirect to login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize repositories
        orderRepository = new OrderRepository(this);


        // Initialize views
        initViews();

        // Setup RecyclerView
        setupRecyclerView();

        // Load orders
        loadOrders();
        
        // Setup Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.orderHistoryToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Order History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // Handle explicit navigation click (optional, but good for custom behaviors)
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /** Handles toolbar menu selections. */
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Links UI components to functional fields. */
    private void initViews() {
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        emptyOrdersTv = findViewById(R.id.emptyOrdersTv);
        
        // Setup Bottom Navigation
        com.sunit.groceryplus.utils.NavigationHelper.setupNavigation(this, userId);
    }

    /** Configures RecyclerView with its LayoutManager and adapter actions. */
    private void setupRecyclerView() {
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(this, orders, new OrderAdapter.OnOrderActionExtendedListener() {
            @Override
            public void onOrderClick(Order order) {
                showOrderDetails(order);
            }

            @Override
            public void onReorderClick(Order order) {
                reorderItems(order);
            }

            @Override
            public void onCancelOrderClick(Order order) {
                showCancelConfirmation(order);
            }

            @Override
            public void onRefundClick(Order order) {
                showRefundConfirmation(order);
            }
        });
        ordersRecyclerView.setAdapter(orderAdapter);
    }

    /** Orchestrates order data loading. */
    private void loadOrders() {
        loadOrdersFromDatabase();
    }

    /** Fetches orders and their respective items from the local repository. */
    private void loadOrdersFromDatabase() {
        try {
            orders = orderRepository.getUserOrders(userId);

            if (orders != null && !orders.isEmpty()) {
                // Load order items for each order
                for (Order order : orders) {
                    List<OrderItem> items = orderRepository.getOrderItems(order.getOrderId());
                    order.setItems(items);
                }

                orderAdapter.updateOrders(orders);
                showOrders();
                Log.d(TAG, "Loaded " + orders.size() + " orders from database");
            } else {
                showEmptyOrders();
                Log.d(TAG, "No orders found");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading orders from database", e);
            Toast.makeText(OrderHistoryActivity.this, "Error loading orders", Toast.LENGTH_SHORT).show();
            showEmptyOrders();
        }
    }

    /** Helper to map JSON data to an Order model. */
    private Order parseOrderFromJson(org.json.JSONObject orderJson) throws org.json.JSONException {
        Order order = new Order();
        order.setOrderId(orderJson.optInt("order_id", 0));
        order.setUserId(userId);
        order.setOrderDate(orderJson.optString("order_date", ""));
        order.setStatus(orderJson.optString("status", "pending"));
        order.setTotalAmount(orderJson.optDouble("total_amount", 0.0));
        order.setDeliveryFee(orderJson.optDouble("delivery_fee", 0.0));
        // Note: Items would need to be loaded separately if needed
        return order;
    }

    /** Shows the orders list view. */
    private void showOrders() {
        ordersRecyclerView.setVisibility(View.VISIBLE);
        emptyOrdersTv.setVisibility(View.GONE);
    }

    /** Shows the empty state message. */
    private void showEmptyOrders() {
        ordersRecyclerView.setVisibility(View.GONE);
        emptyOrdersTv.setVisibility(View.VISIBLE);
    }

    /** Navigates to the detailed tracking view for a specific order. */
    private void showOrderDetails(Order order) {
        // Launch specialized OrderDetailsActivity for granular management (cancellation, returns, issues)
        Intent intent = new Intent(this, OrderDetailsActivity.class);
        intent.putExtra("order_id", order.getOrderId());
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }

    /** Adds all items from a past order back into the user's shopping cart. */
    private void reorderItems(Order order) {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            List<OrderItem> items = order.getItems();
            if (items == null || items.isEmpty()) {
                items = orderRepository.getOrderItems(order.getOrderId());
            }

            for (OrderItem item : items) {
                dbHelper.addToCart(userId, item.getProductId(), item.getQuantity());
            }

            Toast.makeText(this, "Items added to cart", Toast.LENGTH_SHORT).show();
            // Redirect to Cart
            Intent intent = new Intent(this, CartActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error reordering items", e);
            Toast.makeText(this, "Failed to reorder", Toast.LENGTH_SHORT).show();
        }
    }

    /** Displays a confirmation dialog before cancelling an order. */
    private void showCancelConfirmation(Order order) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cancel Order")
                .setMessage("Are you sure you want to cancel order #" + order.getOrderId() + "?")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> cancelOrder(order))
                .setNegativeButton("No", null)
                .show();
    }

    /** Processes order cancellation and sends a system notification. */
    private void cancelOrder(Order order) {
        // Use the specialized cancelOrder method to handle 15% deduction and chat notifications
        boolean success = orderRepository.cancelOrder(order.getOrderId(), userId);
        if (success) {
            Toast.makeText(this, "Order #" + order.getOrderId() + " has been cancelled", Toast.LENGTH_SHORT).show();
            loadOrders();
        } else {
            Toast.makeText(this, "Failed to cancel order", Toast.LENGTH_SHORT).show();
        }
    }

    /** Displays a confirmation dialog before requesting a refund for a delivered order. */
    private void showRefundConfirmation(Order order) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Request Refund")
                .setMessage("Are you sure you want to request a refund for order #" + order.getOrderId() + "?")
                .setPositiveButton("Confirm Refund", (dialog, which) -> refundOrder(order))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /** Processes order refund status update. */
    private void refundOrder(Order order) {
        boolean success = orderRepository.updateOrderStatus(order.getOrderId(), userId, "Refunded");
        if (success) {
            Toast.makeText(this, "Refund requested for order #" + order.getOrderId(), Toast.LENGTH_SHORT).show();
            loadOrders();
        } else {
            Toast.makeText(this, "Failed to process refund request", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }
}