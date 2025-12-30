package com.sunit.groceryplus.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.ProductRepository;
import com.sunit.groceryplus.adapters.AdminProductAdapter;
import com.sunit.groceryplus.models.Product;
import com.sunit.groceryplus.models.Category;

import java.util.ArrayList;
import java.util.List;

public class InventoryAlertsActivity extends AppCompatActivity {

    private RecyclerView alertsRv;
    private TextView emptyAlertsTv;
    private AdminProductAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Product> lowStockProducts;
    private List<Product> outOfStockProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_alerts);

        dbHelper = new DatabaseHelper(this);

        setupToolbar();
        initViews();
        loadInventoryAlerts();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Inventory Alerts");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        alertsRv = findViewById(R.id.alertsRv);
        emptyAlertsTv = findViewById(R.id.emptyAlertsTv);

        alertsRv.setLayoutManager(new LinearLayoutManager(this));
        // TODO: Get proper repository and category list
        ProductRepository productRepository = new ProductRepository(this);
        List<Category> categories = new ArrayList<>(); // Empty for now
        adapter = new AdminProductAdapter(this, new ArrayList<>(), productRepository, categories, null);
        alertsRv.setAdapter(adapter);
    }

    private void loadInventoryAlerts() {
        try {
            lowStockProducts = dbHelper.getLowStockProducts(10); // Low stock threshold = 10
            outOfStockProducts = dbHelper.getOutOfStockProducts();

            List<Product> allAlertProducts = new ArrayList<>();
            allAlertProducts.addAll(outOfStockProducts);
            allAlertProducts.addAll(lowStockProducts);

            if (allAlertProducts.isEmpty()) {
                alertsRv.setVisibility(android.view.View.GONE);
                emptyAlertsTv.setVisibility(android.view.View.VISIBLE);
                emptyAlertsTv.setText("No inventory alerts. All products are well stocked.");
            } else {
                alertsRv.setVisibility(android.view.View.VISIBLE);
                emptyAlertsTv.setVisibility(android.view.View.GONE);
                adapter.updateProducts(allAlertProducts);
                showAlertSummary(outOfStockProducts.size(), lowStockProducts.size());
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading inventory alerts", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAlertSummary(int outOfStockCount, int lowStockCount) {
        String message = "Found " + outOfStockCount + " out of stock products and " + lowStockCount + " low stock products.";
        
        new AlertDialog.Builder(this)
                .setTitle("Inventory Alert Summary")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setNegativeButton("View Products", (dialog, which) -> {
                    // Already showing products
                })
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
