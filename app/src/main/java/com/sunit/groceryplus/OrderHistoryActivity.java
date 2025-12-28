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
import com.sunit.groceryplus.network.ApiService;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private static final String TAG = "OrderHistoryActivity";
    
    private RecyclerView ordersRecyclerView;
    private TextView emptyOrdersTv;
    
    private int userId;
    private OrderRepository orderRepository;
    private OrderAdapter orderAdapter;
    private ApiService apiService;
    private List<Order> orders = new ArrayList<>();

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
        apiService = new ApiService(this);

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

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        emptyOrdersTv = findViewById(R.id.emptyOrdersTv);
        
        // Setup Bottom Navigation
        com.sunit.groceryplus.utils.NavigationHelper.setupNavigation(this, userId);
    }

    private void setupRecyclerView() {
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(this, orders, new OrderAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(Order order) {
                showOrderDetails(order);
            }

            @Override
            public void onReorderClick(Order order) {
                reorderItems(order);
            }
        });
        ordersRecyclerView.setAdapter(orderAdapter);
    }

    private void loadOrders() {
        // Try API first, fallback to database
        apiService.getOrders(new ApiService.ApiCallback<org.json.JSONArray>() {
            @Override
            public void onSuccess(org.json.JSONArray response) {
                try {
                    orders.clear();
                    for (int i = 0; i < response.length(); i++) {
                        org.json.JSONObject orderJson = response.getJSONObject(i);
                        Order order = parseOrderFromJson(orderJson);
                        orders.add(order);
                    }

                    if (!orders.isEmpty()) {
                        orderAdapter.updateOrders(orders);
                        showOrders();
                        Log.d(TAG, "Loaded " + orders.size() + " orders from API");
                    } else {
                        showEmptyOrders();
                    }
                } catch (org.json.JSONException e) {
                    Log.e(TAG, "Error parsing orders from API", e);
                    loadOrdersFromDatabase();
                }
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "API orders failed: " + error + " - Loading from database");
                loadOrdersFromDatabase();
            }
        });
    }

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

    private void showOrders() {
        ordersRecyclerView.setVisibility(View.VISIBLE);
        emptyOrdersTv.setVisibility(View.GONE);
    }

    private void showEmptyOrders() {
        ordersRecyclerView.setVisibility(View.GONE);
        emptyOrdersTv.setVisibility(View.VISIBLE);
    }

    private void showOrderDetails(Order order) {
        // Launch Tracking Activity
        Intent intent = new Intent(this, OrderTrackingActivity.class);
        intent.putExtra("order_id", order.getOrderId());
        intent.putExtra("order_status", order.getStatus());
        startActivity(intent);
    }

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

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }
}