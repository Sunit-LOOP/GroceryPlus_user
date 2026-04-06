package com.sunit.groceryplus.admin;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.R;

/** AnalyticsDashboardActivity - Visual overview of business performance including revenue, orders, and customer metrics. */
public class AnalyticsDashboardActivity extends AppCompatActivity {

    // Data Helper
    private DatabaseHelper dbHelper;
    
    // UI Components (Metric Cards)
    private TextView totalRevenueTv;
    private TextView totalOrdersTv;
    private TextView totalCustomersTv;
    private TextView totalProductsTv;
    private TextView completedOrdersTv;
    private TextView pendingOrdersTv;
    private TextView avgOrderValueTv;
    private TextView todayRevenueTv;
    private TextView processingOrdersTv;
    private TextView shippedOrdersTv;
    private TextView cancelledOrdersTv;
    private TextView refundedOrdersTv;
    
    // Data refresh
    private Handler refreshHandler;
    private Runnable refreshRunnable;

    /** Initializes the activity, toolbar, and data helper. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_dashboard);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        try {
            dbHelper = new DatabaseHelper(this);
            refreshHandler = new Handler(Looper.getMainLooper());

            initViews();
            loadAnalyticsData();
            startAutoRefresh();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing dashboard: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /** Binds UI components to their respective layout IDs. */
    private void initViews() {
        totalRevenueTv = findViewById(R.id.totalRevenueTv);
        totalOrdersTv = findViewById(R.id.totalOrdersTv);
        totalCustomersTv = findViewById(R.id.totalCustomersTv);
        totalProductsTv = findViewById(R.id.totalProductsTv);
        completedOrdersTv = findViewById(R.id.completedOrdersTv);
        pendingOrdersTv = findViewById(R.id.pendingOrdersTv);
        avgOrderValueTv = findViewById(R.id.avgOrderValueTv);
        todayRevenueTv = findViewById(R.id.todayRevenueTv);
        processingOrdersTv = findViewById(R.id.processingOrdersTv);
        shippedOrdersTv = findViewById(R.id.shippedOrdersTv);
        cancelledOrdersTv = findViewById(R.id.cancelledOrdersTv);
        refundedOrdersTv = findViewById(R.id.refundedOrdersTv);
    }

    /** Fetches aggregated performance metrics and updates UI. */
    private void loadAnalyticsData() {
        try {
            // Total Revenue
            double revenue = dbHelper.getTotalRevenue();
            totalRevenueTv.setText(String.format("Rs. %.2f", revenue));

            // Total Orders
            int orderCount = dbHelper.getTotalOrdersCount();
            totalOrdersTv.setText(String.valueOf(orderCount));

            // Total Customers
            int customerCount = dbHelper.getTotalCustomersCount();
            totalCustomersTv.setText(String.valueOf(customerCount));

            // Total Products
            int productCount = dbHelper.getTotalProductsCount();
            totalProductsTv.setText(String.valueOf(productCount));

            // Completed Orders
            int deliveredCount = dbHelper.getOrderCountByStatus("Delivered");
            completedOrdersTv.setText(String.valueOf(deliveredCount));
            
            // Pending Orders
            int pendingCount = dbHelper.getOrderCountByStatus("Pending");
            pendingOrdersTv.setText(String.valueOf(pendingCount));
            
            // Average Order Value (based on Delivered orders that contribute to revenue)
            double avgOrderValue = deliveredCount > 0 ? revenue / deliveredCount : 0;
            avgOrderValueTv.setText(String.format("Rs. %.2f", avgOrderValue));
            
            // Today's Revenue
            double todayRevenue = dbHelper.getTodayRevenue();
            todayRevenueTv.setText(String.format("Rs. %.2f", todayRevenue));

            // Status Breakdown
            processingOrdersTv.setText(String.valueOf(dbHelper.getOrderCountByStatus("Processing")));
            shippedOrdersTv.setText(String.valueOf(dbHelper.getOrderCountByStatus("Shipped")));
            cancelledOrdersTv.setText(String.valueOf(dbHelper.getOrderCountByStatus("Cancelled")));
            refundedOrdersTv.setText(String.valueOf(dbHelper.getOrderCountByStatus("Refunded")));
            
        } catch (Exception e) {
            Toast.makeText(this, "Error loading analytics data", Toast.LENGTH_SHORT).show();
        }
    }
    
    /** Starts automatic data refresh every 30 seconds. */
    private void startAutoRefresh() {
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadAnalyticsData();
                refreshHandler.postDelayed(this, 30000); // Refresh every 30 seconds
            }
        };
        refreshHandler.post(refreshRunnable);
    }
    
    /** Stops automatic data refresh. */
    private void stopAutoRefresh() {
        if (refreshHandler != null && refreshRunnable != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
        }
    }

    /** Handles toolbar menu selections. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAutoRefresh();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        stopAutoRefresh();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        startAutoRefresh();
    }
}
