package com.sunit.groceryplus.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.repositories.ProductRepository;
import com.sunit.groceryplus.repositories.CategoryRepository;
import com.sunit.groceryplus.models.Product;
import com.sunit.groceryplus.models.Category;
import com.sunit.groceryplus.adapters.AdminProductAdapter;

import java.util.ArrayList;
import java.util.List;


/** InventoryAlertsActivity - Dashboard for monitoring stock levels, filtering out-of-stock and low-stock products. */
public class InventoryAlertsActivity extends AppCompatActivity {

    // UI Components
    private RecyclerView alertsRv;
    private View emptyAlertsTv;
    
    // Data & Adapters
    private AdminProductAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Product> lowStockProducts;
    private List<Product> outOfStockProducts;

    /**
     * Initializes activity, sets up toolbar, and triggers inventory check.
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_alerts);

        dbHelper = new DatabaseHelper(this);

        setupToolbar();
        initViews();
        loadInventoryAlerts();
    }

    /**
     * Configures the toolbar with a back button and title.
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Inventory Alerts");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /**
     * Initializes UI views and sets up the RecyclerView with a product adapter.
     */
    private void initViews() {
        alertsRv = findViewById(R.id.alertsRv);
        emptyAlertsTv = findViewById(R.id.emptyAlertsTv);

        alertsRv.setLayoutManager(new LinearLayoutManager(this));
        com.sunit.groceryplus.ProductRepository productRepository = new com.sunit.groceryplus.ProductRepository(this);
        com.sunit.groceryplus.CategoryRepository categoryRepository = new com.sunit.groceryplus.CategoryRepository(this);
        List<Category> categories = categoryRepository.getAllCategories();
        adapter = new AdminProductAdapter(this, new ArrayList<>(), productRepository, categories, null);
        alertsRv.setAdapter(adapter);
    }

    /**
     * Queries the database for low stock and out-of-stock products.
     * Combines results and updates the UI or shows an empty state.
     */
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

    /**
     * Displays a summary dialog with counts of affected products.
     * @param outOfStockCount Count of products with 0 stock
     * @param lowStockCount Count of products below threshold
     */
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

    /**
     * Handles toolbar menu actions (Back button).
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
