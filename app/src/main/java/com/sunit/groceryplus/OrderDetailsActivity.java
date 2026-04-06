package com.sunit.groceryplus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.adapters.OrderDetailAdapter;
import com.sunit.groceryplus.models.Order;
import com.sunit.groceryplus.models.OrderItem;

import java.util.List;

/** OrderDetailsActivity - Detailed view of a single order for modifications and issue reporting. */
public class OrderDetailsActivity extends AppCompatActivity {

    private int orderId;
    private int userId;
    private OrderRepository orderRepository;
    private Order order;
    
    private TextView orderIdTv, orderStatusTv, orderDateTv, totalAmountTv, paymentMethodTv;
    private View modificationCard;
    private RecyclerView itemsRecyclerView;
    private OrderDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        orderId = getIntent().getIntExtra("order_id", -1);
        userId = getIntent().getIntExtra("user_id", -1);

        if (orderId == -1) {
            Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        orderRepository = new OrderRepository(this);
        initViews();
        loadOrderDetails();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        orderIdTv = findViewById(R.id.orderIdTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        orderDateTv = findViewById(R.id.orderDateTv);
        totalAmountTv = findViewById(R.id.totalAmountTv);
        paymentMethodTv = findViewById(R.id.paymentMethodTv);
        modificationCard = findViewById(R.id.modificationCard);
        itemsRecyclerView = findViewById(R.id.itemsRecyclerView);
        
        findViewById(R.id.supportBtn).setOnClickListener(v -> {
            Intent intent = new Intent(this, SupportCenterActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });
    }

    private void loadOrderDetails() {
        order = orderRepository.getOrderById(orderId);
        if (order != null) {
            List<OrderItem> items = orderRepository.getOrderItems(orderId);
            order.setItems(items);

            orderIdTv.setText("Order #" + order.getOrderId());
            orderStatusTv.setText("Status: " + order.getStatus());
            orderDateTv.setText("Placed on: " + order.getOrderDate());
            totalAmountTv.setText("Total Amount: Rs. " + String.format("%.2f", order.getTotalAmount()));
            
            // Show modification warning if packed
            if (order.isPacked()) {
                modificationCard.setVisibility(View.VISIBLE);
            }

            setupRecyclerView(items);
        }
    }

    private void setupRecyclerView(List<OrderItem> items) {
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderDetailAdapter(this, items, order.getStatus(), order.isPacked(), new OrderDetailAdapter.OnItemActionListener() {
            @Override
            public void onCancelItem(OrderItem item) {
                showCancelItemDialog(item);
            }

            @Override
            public void onReportIssue(OrderItem item) {
                Intent intent = new Intent(OrderDetailsActivity.this, ReportIssueActivity.class);
                intent.putExtra("order_id", orderId);
                intent.putExtra("user_id", userId);
                intent.putExtra("order_item_id", item.getOrderItemId());
                intent.putExtra("product_name", item.getProductName());
                startActivity(intent);
            }

            @Override
            public void onRequestRefund(OrderItem item) {
                Intent intent = new Intent(OrderDetailsActivity.this, RefundRequestActivity.class);
                intent.putExtra("order_id", orderId);
                intent.putExtra("user_id", userId);
                intent.putExtra("product_id", item.getProductId());
                intent.putExtra("product_name", item.getProductName());
                intent.putExtra("amount", item.getSubtotal());
                startActivity(intent);
            }
        });
        itemsRecyclerView.setAdapter(adapter);
    }

    private void showCancelItemDialog(OrderItem item) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cancel Item")
                .setMessage("Are you sure you want to cancel " + item.getProductName() + "?")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    boolean success = orderRepository.requestPartialCancellation(orderId, item.getOrderItemId(), "User requested cancellation");
                    if (success) {
                        Toast.makeText(this, "Item cancelled successfully", Toast.LENGTH_SHORT).show();
                        loadOrderDetails();
                    } else {
                        Toast.makeText(this, "Failed to cancel item", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrderDetails();
    }
}
