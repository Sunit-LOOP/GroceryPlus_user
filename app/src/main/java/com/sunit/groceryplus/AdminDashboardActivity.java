package com.sunit.groceryplus;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.sunit.groceryplus.models.User;

public class AdminDashboardActivity extends AppCompatActivity {

    private static final String TAG = "AdminDashboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        
        // Setup Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.adminDashboardToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Get user ID from intent
        int userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Log.e(TAG, "Invalid user ID received");
        }

        // Initialize Cards
        findViewById(R.id.manageProductsCard).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, com.sunit.groceryplus.admin.ProductManagementActivity.class));
        });

        findViewById(R.id.manageCategoriesCard).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, com.sunit.groceryplus.admin.CategoryManagementActivity.class));
        });

        findViewById(R.id.customerManagementCard).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, com.sunit.groceryplus.admin.CustomerManagementActivity.class));
        });

        findViewById(R.id.messageCustomersCard).setOnClickListener(v -> {
             startActivity(new android.content.Intent(this, com.sunit.groceryplus.admin.AdminMessagesActivity.class));
        });
        
        findViewById(R.id.orderManagementCard).setOnClickListener(v -> {
             startActivity(new android.content.Intent(this, com.sunit.groceryplus.admin.OrderManagementActivity.class));
        });

        findViewById(R.id.analyticsDashboardCard).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, com.sunit.groceryplus.admin.AnalyticsDashboardActivity.class));
        });

        findViewById(R.id.promotionsManagementCard).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, com.sunit.groceryplus.admin.PromotionManagementActivity.class));
        });

        findViewById(R.id.reviewsManagementCard).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, com.sunit.groceryplus.admin.ReviewsManagementActivity.class));
        });

        findViewById(R.id.deliveryManagementCard).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, com.sunit.groceryplus.admin.DeliveryPersonnelActivity.class));
        });

        findViewById(R.id.paymentReceivedCard).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, com.sunit.groceryplus.admin.PaymentTrackingActivity.class));
        });

        findViewById(R.id.vendorManagementCard).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, com.sunit.groceryplus.admin.VendorManagementActivity.class));
        });
        
         findViewById(R.id.logoutButton).setOnClickListener(v -> {
            // clear prefs if any
            finish();
        });
    }
}