package com.sunit.groceryplus.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.DeliveryPersonRepository;
import com.sunit.groceryplus.OrderRepository;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.adapters.AdminOrderAdapter;
import com.sunit.groceryplus.models.DeliveryPerson;
import com.sunit.groceryplus.models.Order;
import com.sunit.groceryplus.utils.DeliveryOptimizer;
import com.sunit.groceryplus.utils.GroceryNotificationManager;

import java.util.ArrayList;
import java.util.List;

/** OrderManagementActivity - Admin interface for managing customer orders, statuses, and delivery personnel assignment with automated payment handling. */
public class OrderManagementActivity extends AppCompatActivity {

    // UI Components
    private RecyclerView ordersRv;
    private AdminOrderAdapter adapter;
    
    // Logic/Data Components
    private OrderRepository orderRepository;
    private GroceryNotificationManager notificationManager;

    /**
     * Initializes activity, setups toolbar, initializes repositories, and loads orders.
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);

        // Setup toolbar with navigation
        setupToolbar();
        
        // Initialize repositories and managers
        initializeComponents();
        
        // Setup RecyclerView for displaying orders
        setupRecyclerView();
        
        // Load initial order data
        loadOrders();
    }

    /**
     * Configures the toolbar with a back button.
     */
    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /**
     * Initializes repositories and managers.
     */
    private void initializeComponents() {
        ordersRv = findViewById(R.id.ordersRv);
        orderRepository = new OrderRepository(this);
        notificationManager = GroceryNotificationManager.getInstance(this);
    }

    /**
     * Configures the RecyclerView options and click listeners for status updates and delivery assignment.
     */
    private void setupRecyclerView() {
        ordersRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminOrderAdapter(this, new ArrayList<>(), new AdminOrderAdapter.OnOrderActionListener() {
            @Override
            public void onUpdateStatusClick(Order order) {
                showUpdateStatusDialog(order);
            }

            @Override
            public void onAssignDeliveryClick(Order order) {
                showAssignDeliveryDialog(order);
            }
        });
        ordersRv.setAdapter(adapter);
    }

    /**
     * Fetches all orders from the repository and refreshes the adapter.
     */
    private void loadOrders() {
        List<Order> orders = orderRepository.getAllOrders();
        adapter.updateOrders(orders);
    }

    /**
     * Displays a dialog to update order status.
     * Updates database asynchronously, handles payment status syncing for completed/refunded orders,
     * and sends push notifications to the user.
     * @param order The order to update.
     */
    private void showUpdateStatusDialog(Order order) {
        // List of available statuses for the Admin to select from
        // Added "Refunded" to allow full refund processing
        String[] statuses = {"Pending", "Processing", "Shipped", "Delivered", "Cancelled", "Refunded"};

        new AlertDialog.Builder(this)
                .setTitle("Update Order Status")
                .setSingleChoiceItems(statuses, -1, (dialog, which) -> {
                    String newStatus = statuses[which];
                    
                    // Show progress dialog to indicate background work
                    android.app.AlertDialog progressDialog = new android.app.AlertDialog.Builder(this)
                            .setTitle("Updating Order")
                            .setMessage("Please wait...")
                            .setCancelable(false)
                            .create();
                    progressDialog.show();
                    
                    // Run database operations in background thread (AsyncTask)
                    new android.os.AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            // Update order status in Order table
                            // Centralized logic in OrderRepository handles:
                            // 1. Payment status syncing
                            // 2. Stock replenishment for cancellations/refunds
                            // 3. User notifications & chat messages
                            return orderRepository.updateOrderStatus(order.getOrderId(), order.getUserId(), newStatus);
                        }
                        
                        @Override
                        protected void onPostExecute(Boolean success) {
                            // Dismiss progress dialog
                            progressDialog.dismiss();
                            
                            if (success) {
                                Toast.makeText(OrderManagementActivity.this, "Order updated to " + newStatus, Toast.LENGTH_SHORT).show();
                                loadOrders();
                            } else {
                                Toast.makeText(OrderManagementActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    }.execute();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Displays a dialog to assign a delivery person to an order.
     * Uses DeliveryOptimizer to suggest the best candidate.
     * @param order The order to assign.
     */
    private void showAssignDeliveryDialog(Order order) {
        // Load delivery personnel
        DeliveryPersonRepository dpRepo = new DeliveryPersonRepository(this);
        List<DeliveryPerson> personnel = dpRepo.getAllDeliveryPersonnel();

        if (personnel.isEmpty()) {
            // Seed sample data for testing if empty
            dpRepo.addDeliveryPerson("John Doe", "9876543210");
            dpRepo.addDeliveryPerson("Jane Smith", "9800000000");
            personnel = dpRepo.getAllDeliveryPersonnel();
        }

        // Get AI Recommendation using DeliveryOptimizer (using Dijkstra/Nearest Neighbor logic)
        DeliveryPerson suggested = DeliveryOptimizer.getBestDeliveryPerson("Area B", personnel);

        String[] displayNames = new String[personnel.size()];
        int suggestedIndex = -1;

        for (int i = 0; i < personnel.size(); i++) {
            DeliveryPerson p = personnel.get(i);
            displayNames[i] = p.getName();
            if (suggested != null && p.getPersonId() == suggested.getPersonId()) {
                // Removed AI Suggested label as requested
                // displayNames[i] += " [AI Suggested]";
                suggestedIndex = i;
            }
        }

        final List<DeliveryPerson> finalPersonnel = personnel;

        new AlertDialog.Builder(this)
                .setTitle("Assign Delivery Person")
                .setSingleChoiceItems(displayNames, suggestedIndex, (dialog, which) -> {
                    DeliveryPerson selectedPerson = finalPersonnel.get(which);
                    
                    // Show progress dialog
                    android.app.AlertDialog progressDialog = new android.app.AlertDialog.Builder(this)
                            .setTitle("Assigning Delivery Person")
                            .setMessage("Please wait...")
                            .setCancelable(false)
                            .create();
                    progressDialog.show();

                    // Run database operations in background
                    new android.os.AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            return orderRepository.assignDeliveryPerson(order.getOrderId(), selectedPerson.getPersonId());
                        }
                        
                        @Override
                        protected void onPostExecute(Boolean success) {
                            // Dismiss progress dialog
                            progressDialog.dismiss();
                            
                            if (success) {
                                Toast.makeText(OrderManagementActivity.this, "Assigned to " + selectedPerson.getName(), Toast.LENGTH_SHORT).show();

                                // Send notification to user
                                String title = "Delivery Update";
                                String message = "Your order #" + order.getOrderId() + " has been assigned to " + selectedPerson.getName() + ". They will be arriving soon!";
                                notificationManager.sendNotification(order.getUserId(), title, message, GroceryNotificationManager.TYPE_ORDER, String.valueOf(order.getOrderId()));

                                loadOrders();
                            } else {
                                Toast.makeText(OrderManagementActivity.this, "Failed to assign", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    }.execute();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Handles toolbar navigation actions.
     */
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order_management, menu);
        return true;
    }

    /**
     * Handles toolbar navigation and menu actions.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_refund_requests) {
            // - [x] Admin UI Components
            // - [x] Create `RefundAdapter.java`
            // - [x] Create `AdminRefundManagementActivity.java`
            // - [x] Link `AdminRefundManagementActivity` from `OrderManagementActivity`
            android.content.Intent intent = new android.content.Intent(this, AdminRefundManagementActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
