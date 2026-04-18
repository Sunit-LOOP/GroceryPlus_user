package com.sunit.groceryplus;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sunit.groceryplus.admin.AdminRefundManagementActivity;
import com.sunit.groceryplus.admin.AdminSettingsActivity;
import com.sunit.groceryplus.admin.AnalyticsDashboardActivity;
import com.sunit.groceryplus.admin.CategoryManagementActivity;
import com.sunit.groceryplus.admin.CustomerManagementActivity;
import com.sunit.groceryplus.admin.DeliveryPersonnelActivity;
import com.sunit.groceryplus.admin.InventoryAlertsActivity;
import com.sunit.groceryplus.admin.OrderManagementActivity;
import com.sunit.groceryplus.admin.PaymentTrackingActivity;
import com.sunit.groceryplus.admin.ProductManagementActivity;
import com.sunit.groceryplus.admin.PromotionManagementActivity;
import com.sunit.groceryplus.admin.ReportsActivity;
import com.sunit.groceryplus.admin.ReviewsManagementActivity;
import com.sunit.groceryplus.admin.VendorManagementActivity;


/** AdminDashboardActivity - Central hub for all administrative actions and system management. */
public class AdminDashboardActivity extends AppCompatActivity {

    /** Initializes the activity and sets up navigation grid. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Toolbar toolbar = findViewById(R.id.adminDashboardToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        setupClickListeners();
    }

    /** Sets up click listeners for all dashboard management modules. */
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

        // Messages
        findViewById(R.id.messageCustomersCard).setOnClickListener(v -> {
            startActivity(new Intent(this, com.sunit.groceryplus.admin.AdminMessagesActivity.class));
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

        // Payment Management
        findViewById(R.id.paymentReceivedCard).setOnClickListener(v -> {
            startActivity(new Intent(this, PaymentTrackingActivity.class));
        });

        // Delivery Management
        findViewById(R.id.deliveryManagementCard).setOnClickListener(v -> {
            startActivity(new Intent(this, DeliveryPersonnelActivity.class));
        });

        // Support & Refunds
        findViewById(R.id.supportRefundsCard).setOnClickListener(v -> {
            startActivity(new Intent(this, com.sunit.groceryplus.admin.AdminSupportActivity.class));
        });

        findViewById(R.id.adminRefundQueueCard).setOnClickListener(v ->
                startActivity(new Intent(this, AdminRefundManagementActivity.class)));

        findViewById(R.id.inventoryAlertsCard).setOnClickListener(v ->
                startActivity(new Intent(this, InventoryAlertsActivity.class)));

        findViewById(R.id.reportsExportCard).setOnClickListener(v ->
                startActivity(new Intent(this, ReportsActivity.class)));

        findViewById(R.id.storeSettingsCard).setOnClickListener(v ->
                startActivity(new Intent(this, AdminSettingsActivity.class)));

        // Logout Button
        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

}
