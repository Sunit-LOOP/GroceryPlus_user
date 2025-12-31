package com.sunit.groceryplus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sunit.groceryplus.admin.AnalyticsDashboardActivity;
import com.sunit.groceryplus.admin.CategoryManagementActivity;
import com.sunit.groceryplus.admin.CustomerManagementActivity;
import com.sunit.groceryplus.admin.OrderManagementActivity;
import com.sunit.groceryplus.admin.ProductManagementActivity;
import com.sunit.groceryplus.admin.PromotionManagementActivity;
import com.sunit.groceryplus.admin.ReviewsManagementActivity;
import com.sunit.groceryplus.admin.VendorManagementActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Set up click listeners for dashboard cards
        setupClickListeners();
    }

    private void setupClickListeners() {
        // Product Management
        findViewById(R.id.manageProductsCard).setOnClickListener(v -> {
            startActivity(new Intent(this, ProductManagementActivity.class));
        });

        // Order Management
        findViewById(R.id.orderManagementCard).setOnClickListener(v -> {
            startActivity(new Intent(this, OrderManagementActivity.class));
        });

        // Category Management
        findViewById(R.id.manageCategoriesCard).setOnClickListener(v -> {
            startActivity(new Intent(this, CategoryManagementActivity.class));
        });

        // Customer Management
        findViewById(R.id.customerManagementCard).setOnClickListener(v -> {
            startActivity(new Intent(this, CustomerManagementActivity.class));
        });

        // Vendor Management
        findViewById(R.id.vendorManagementCard).setOnClickListener(v -> {
            startActivity(new Intent(this, VendorManagementActivity.class));
        });

        // Analytics Dashboard
        findViewById(R.id.analyticsDashboardCard).setOnClickListener(v -> {
            startActivity(new Intent(this, AnalyticsDashboardActivity.class));
        });

        // Promotion Management
        findViewById(R.id.promotionsManagementCard).setOnClickListener(v -> {
            startActivity(new Intent(this, PromotionManagementActivity.class));
        });

        // Reviews Management
        findViewById(R.id.reviewsManagementCard).setOnClickListener(v -> {
            startActivity(new Intent(this, ReviewsManagementActivity.class));
        });

        // Logout Button
        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Handle back press - go to login or exit
        finish();
    }
}
