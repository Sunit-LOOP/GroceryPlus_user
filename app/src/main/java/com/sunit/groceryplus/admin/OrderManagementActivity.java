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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);

        setupToolbar();
        initializeComponents();
        setupRecyclerView();
        loadOrders();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

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

            @Override
            public void onRefundClick(Order order) {
                showPushRefundConfirmDialog(order);
            }
        });
        ordersRv.setAdapter(adapter);
    }

    private void loadOrders() {
        List<Order> orders = orderRepository.getAllOrders();
        adapter.updateOrders(orders);
    }

    private void showUpdateStatusDialog(Order order) {
        String[] statuses = {"Pending", "Processing", "Shipped", "Delivered", "Cancelled", "Refunded"};

        new AlertDialog.Builder(this)
                .setTitle("Update Order Status")
                .setSingleChoiceItems(statuses, -1, (dialog, which) -> {
                    String newStatus = statuses[which];
                    
                    AlertDialog progressDialog = new AlertDialog.Builder(this)
                            .setTitle("Updating Order")
                            .setMessage("Please wait...")
                            .setCancelable(false)
                            .create();
                    progressDialog.show();
                    
                    new android.os.AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            return orderRepository.updateOrderStatus(order.getOrderId(), order.getUserId(), newStatus);
                        }
                        
                        @Override
                        protected void onPostExecute(Boolean success) {
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

    private void showAssignDeliveryDialog(Order order) {
        DeliveryPersonRepository dpRepo = new DeliveryPersonRepository(this);
        List<DeliveryPerson> personnel = dpRepo.getAllDeliveryPersonnel();

        if (personnel.isEmpty()) {
            dpRepo.addDeliveryPerson("John Doe", "9876543210");
            dpRepo.addDeliveryPerson("Jane Smith", "9800000000");
            personnel = dpRepo.getAllDeliveryPersonnel();
        }

        DeliveryPerson suggested = DeliveryOptimizer.getBestDeliveryPerson("Area B", personnel);
        String[] displayNames = new String[personnel.size()];
        int suggestedIndex = -1;

        for (int i = 0; i < personnel.size(); i++) {
            DeliveryPerson p = personnel.get(i);
            displayNames[i] = p.getName();
            if (suggested != null && p.getPersonId() == suggested.getPersonId()) {
                suggestedIndex = i;
            }
        }

        final List<DeliveryPerson> finalPersonnel = personnel;

        new AlertDialog.Builder(this)
                .setTitle("Assign Delivery Person")
                .setSingleChoiceItems(displayNames, suggestedIndex, (dialog, which) -> {
                    DeliveryPerson selectedPerson = finalPersonnel.get(which);
                    
                    AlertDialog progressDialog = new AlertDialog.Builder(this)
                            .setTitle("Assigning Delivery Person")
                            .setMessage("Please wait...")
                            .setCancelable(false)
                            .create();
                    progressDialog.show();

                    new android.os.AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            return orderRepository.assignDeliveryPerson(order.getOrderId(), selectedPerson.getPersonId());
                        }
                        
                        @Override
                        protected void onPostExecute(Boolean success) {
                            progressDialog.dismiss();
                            if (success) {
                                Toast.makeText(OrderManagementActivity.this, "Assigned to " + selectedPerson.getName(), Toast.LENGTH_SHORT).show();
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

    private void showPushRefundConfirmDialog(Order order) {
        double amount = order.getTotalAmount();
        String method = order.getPaymentMethod() != null ? order.getPaymentMethod() : "COD";
        boolean isStripe = "Stripe".equalsIgnoreCase(method);

        String msg = "Are you sure you want to push a refund for Order #" + order.getOrderId() + "?\n\n" +
                     "Total Amount: Rs. " + String.format("%.2f", amount) + "\n" +
                     "Payment Method: " + method + "\n\n" +
                     (isStripe ? "A 15% fee applies. NPR " + String.format("%.2f", amount * 0.85) + " will be credited instantly." : 
                                 "Full amount will be credited instantly.");

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Confirm Push Refund")
                .setMessage(msg)
                .setPositiveButton("Push Refund", (dialog, which) -> {
                    new android.os.AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            return orderRepository.updateOrderStatus(order.getOrderId(), order.getUserId(), "Refunded");
                        }
                        @Override
                        protected void onPostExecute(Boolean success) {
                            if (success) {
                                Toast.makeText(OrderManagementActivity.this, "Refund Pushed Successfully", Toast.LENGTH_SHORT).show();
                                loadOrders();
                            } else {
                                Toast.makeText(OrderManagementActivity.this, "Failed to push refund", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }.execute();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order_management, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_refund_requests) {
            android.content.Intent intent = new android.content.Intent(this, AdminRefundManagementActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
