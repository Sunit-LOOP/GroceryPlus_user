package com.sunit.groceryplus.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.sunit.groceryplus.DeliveryPersonRepository;
import com.sunit.groceryplus.OrderRepository;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.adapters.AdminOrderAdapter;
import com.sunit.groceryplus.models.DeliveryPerson;
import com.sunit.groceryplus.models.Order;
import com.sunit.groceryplus.utils.DeliveryOptimizer;
import com.sunit.groceryplus.utils.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

public class OrderManagementActivity extends AppCompatActivity {

    private RecyclerView ordersRv;
    private AdminOrderAdapter adapter;
    private OrderRepository orderRepository;
    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ordersRv = findViewById(R.id.ordersRv);
        orderRepository = new OrderRepository(this);
        notificationHelper = new NotificationHelper(this);

        setupRecyclerView();
        loadOrders();
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

    private void showUpdateStatusDialog(Order order) {
        String[] statuses = {"Pending", "Processing", "Shipped", "Delivered", "Cancelled"};

        new AlertDialog.Builder(this)
                .setTitle("Update Order Status")
                .setSingleChoiceItems(statuses, -1, (dialog, which) -> {
                    String newStatus = statuses[which];
                    // Update in DB
                    boolean success = orderRepository.updateOrderStatus(order.getOrderId(), order.getUserId(), newStatus);
                    if (success) {
                        Toast.makeText(this, "Order updated to " + newStatus, Toast.LENGTH_SHORT).show();
                        loadOrders();

                        // Send notification to user
                        if ("Shipped".equals(newStatus)) {
                            notificationHelper.sendNotification(order.getUserId(), "Order Shipped!", "Your order #" + order.getOrderId() + " has been shipped.");
                        } else if ("Delivered".equals(newStatus)) {
                            notificationHelper.sendNotification(order.getUserId(), "Order Delivered!", "Your order #" + order.getOrderId() + " has been delivered.");
                        }

                    } else {
                        Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
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

                    boolean success = orderRepository.assignDeliveryPerson(order.getOrderId(), selectedPerson.getPersonId());
                    if (success) {
                        Toast.makeText(this, "Assigned to " + selectedPerson.getName(), Toast.LENGTH_SHORT).show();

                        // Send notification to user
                        String title = "Delivery Update";
                        String message = "Your order #" + order.getOrderId() + " has been assigned to " + selectedPerson.getName();
                        notificationHelper.sendNotification(order.getUserId(), title, message);

                        loadOrders();
                    } else {
                        Toast.makeText(this, "Failed to assign", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
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
