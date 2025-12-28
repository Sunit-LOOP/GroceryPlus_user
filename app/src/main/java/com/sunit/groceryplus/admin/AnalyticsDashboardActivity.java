package com.sunit.groceryplus.admin;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.R;

public class AnalyticsDashboardActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView totalRevenueTv;
    private TextView totalOrdersTv;
    private TextView totalCustomersTv;
    private TextView totalProductsTv;
    private TextView completedOrdersTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_dashboard);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        dbHelper = new DatabaseHelper(this);

        initViews();
        loadAnalyticsData();
    }

    private void initViews() {
        totalRevenueTv = findViewById(R.id.totalRevenueTv);
        totalOrdersTv = findViewById(R.id.totalOrdersTv);
        totalCustomersTv = findViewById(R.id.totalCustomersTv);
        totalProductsTv = findViewById(R.id.totalProductsTv);
        completedOrdersTv = findViewById(R.id.completedOrdersTv);
    }

    private void loadAnalyticsData() {
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
