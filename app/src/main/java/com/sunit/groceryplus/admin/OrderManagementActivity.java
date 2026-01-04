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

/**
 * OrderManagementActivity - Admin interface for managing customer orders
 * 
 * This activity provides comprehensive order management functionality for administrators.
 * It allows admins to view, update, and manage all customer orders in the GroceryPlus system.
 * The activity includes features for order status updates, delivery personnel assignment,
 * and automatic COD payment completion.
 * 
 * Key Features:
 * - Display all customer orders in a RecyclerView
 * - Update order status (Pending → Processing → Shipped → Delivered)
 * - Assign delivery personnel to orders
 * - Automatic COD payment completion when order is marked as Delivered
 * - Send notifications to customers for order updates
 * - Background processing to prevent UI freezing
 * - Delivery route optimization
 * 
 * Order Status Flow:
 * 1. Pending - Order received, awaiting processing
 * 2. Processing - Order accepted, being prepared
 * 3. Shipped - Order out for delivery
 * 4. Delivered - Order delivered to customer
 * 5. Cancelled - Order cancelled (if applicable)
 * 
 * Payment Integration:
 * - When order status changes to "Delivered"
 * - Automatically updates COD payment status to "Completed"
 * - Sends payment confirmation notification
 * - Maintains payment history in database
 * 
 * @author GroceryPlus Development Team
 * @version 1.0
 * @since 1.0
 */
public class OrderManagementActivity extends AppCompatActivity {

    // UI Components
    private RecyclerView ordersRv;
    private AdminOrderAdapter adapter;
    
    // Business Logic Components
    private OrderRepository orderRepository;
    private GroceryNotificationManager notificationManager;

    /**
     * Called when the activity is first created
     * 
     * This method initializes the UI components, sets up the toolbar,
     * configures the RecyclerView, and loads the initial order data.
     * 
     * @param savedInstanceState Previously saved state data
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
     * Setup the MaterialToolbar with navigation and title
     */
    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /**
     * Initialize business logic components
     */
    private void initializeComponents() {
        ordersRv = findViewById(R.id.ordersRv);
        orderRepository = new OrderRepository(this);
        notificationManager = GroceryNotificationManager.getInstance(this);
    }

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

    private void loadOrders() {
        List<Order> orders = orderRepository.getAllOrders();
        adapter.updateOrders(orders);
    }

    /**
     * Display a dialog to update the status of an order.
     * 
     * This method presents a list of valid statuses (Pending, Processing, Shipped, Delivered, Cancelled, Refunded).
     * Upon selection, it updates the order status asynchronously and handles related side effects:
     * - Payment status updates (e.g., Delivery -> Completed, Refunded -> Refunded)
     * - Customer notifications via GroceryNotificationManager
     * 
     * @param order The order object to be updated.
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
                        private boolean paymentUpdateSuccess = false;
                        
                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            // Update order status in Order table
                            // This also triggers the Chat Notification logic in OrderRepository for "Refunded" status
                            boolean success = orderRepository.updateOrderStatus(order.getOrderId(), order.getUserId(), newStatus);
                            
                            if (success) {
                                DatabaseHelper dbHelper = new DatabaseHelper(OrderManagementActivity.this);
                                
                                // Logic for auto-updating Payment Status based on Order Status
                                if ("Delivered".equalsIgnoreCase(newStatus)) {
                                    // If order is Delivered, mark Payment as Completed (assuming COD or confirming pre-paid)
                                    paymentUpdateSuccess = dbHelper.updatePaymentStatusByOrderId(order.getOrderId(), "Completed");
                                    android.util.Log.d("OrderManagement", "Payment status updated to Completed for order #" + order.getOrderId());
                                } else if ("Refunded".equalsIgnoreCase(newStatus)) {
                                    // If order is Refunded, mark Payment as Refunded
                                    // This keeps the financial records consistent
                                    paymentUpdateSuccess = dbHelper.updatePaymentStatusByOrderId(order.getOrderId(), "Refunded");
                                    android.util.Log.d("OrderManagement", "Payment status updated to Refunded for order #" + order.getOrderId());
                                }
                            }
                            
                            return success;
                        }
                        
                        @Override
                        protected void onPostExecute(Boolean success) {
                            // Dismiss progress dialog
                            progressDialog.dismiss();
                            
                            if (success) {
                                Toast.makeText(OrderManagementActivity.this, "Order updated to " + newStatus, Toast.LENGTH_SHORT).show();
                                loadOrders();

                                // Send notification to user
                                String title = "Order Update";
                                String message = "Your order #" + order.getOrderId() + " is now " + newStatus;
                                
                                if ("Processing".equalsIgnoreCase(newStatus)) {
                                    title = "Order Accepted";
                                    message = "The store has accepted your order #" + order.getOrderId();
                                } else if ("Shipped".equalsIgnoreCase(newStatus)) {
                                    title = "Out for Delivery";
                                    message = "Your order #" + order.getOrderId() + " is out for delivery!";
                                } else if ("Delivered".equalsIgnoreCase(newStatus)) {
                                    title = "Order Delivered";
                                    message = "Your order #" + order.getOrderId() + " has been delivered successfully. Enjoy!";
                                } else if ("Cancelled".equalsIgnoreCase(newStatus)) {
                                    title = "Order Cancelled";
                                    message = "Your order #" + order.getOrderId() + " has been cancelled.";
                                } else if ("Refunded".equalsIgnoreCase(newStatus)) {
                                    // Specific notification for Refunds
                                    title = "Order Refunded";
                                    message = "Your order #" + order.getOrderId() + " has been fully refunded by store.";
                                }
                                
                                notificationManager.sendNotification(order.getUserId(), title, message, GroceryNotificationManager.TYPE_ORDER, String.valueOf(order.getOrderId()));

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
                displayNames[i] += " [AI Suggested]";
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
