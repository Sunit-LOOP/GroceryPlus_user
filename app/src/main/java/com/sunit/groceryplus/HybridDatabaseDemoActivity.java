package com.sunit.groceryplus;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.sunit.groceryplus.models.Product;
import com.sunit.groceryplus.models.User;
import com.sunit.groceryplus.utils.HybridDatabaseManager;
import com.sunit.groceryplus.utils.NetworkUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Demo activity to showcase SQLite-First Hybrid Database functionality
 * Demonstrates SQLite (PRIMARY) + Firestore (SECONDARY) integration
 */
public class HybridDatabaseDemoActivity extends AppCompatActivity {
    
    private static final String TAG = "HybridDatabaseDemo";
    
    private HybridDatabaseManager hybridDb;
    private TextView statusText;
    private TextView syncStatusText;
    private Button addProductButton;
    private Button syncButton;
    private Button toggleAutoSyncButton;
    private Button toggleCloudButton;
    private Button getProductsButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hybrid_database_demo);
        
        initViews();
        setupHybridDatabase();
        setupClickListeners();
        updateStatus();
    }
    
    private void initViews() {
        statusText = findViewById(R.id.statusText);
        syncStatusText = findViewById(R.id.syncStatusText);
        addProductButton = findViewById(R.id.addProductButton);
        syncButton = findViewById(R.id.syncButton);
        toggleAutoSyncButton = findViewById(R.id.toggleAutoSyncButton);
        toggleCloudButton = findViewById(R.id.toggleCloudButton);
        getProductsButton = findViewById(R.id.getProductsButton);
    }
    
    private void setupHybridDatabase() {
        hybridDb = HybridDatabaseManager.getInstance(this);
        
        // SQLite-first approach: auto-sync OFF by default
        hybridDb.setAutoSync(false);
        hybridDb.setCloudEnabled(true);
        
        Log.d(TAG, "SQLite-First Hybrid Database initialized");
        Toast.makeText(this, "SQLite-First approach: Local DB is PRIMARY", Toast.LENGTH_SHORT).show();
    }
    
    private void setupClickListeners() {
        addProductButton.setOnClickListener(v -> addSampleProduct());
        syncButton.setOnClickListener(v -> syncAllData());
        toggleAutoSyncButton.setOnClickListener(v -> toggleAutoSync());
        toggleCloudButton.setOnClickListener(v -> toggleCloud());
        getProductsButton.setOnClickListener(v -> getAllProducts());
    }
    
    private void addSampleProduct() {
        Product product = new Product();
        product.setProductName("Demo Product " + System.currentTimeMillis());
        product.setCategoryId(1);
        product.setPrice(99.99);
        product.setDescription("Sample product for hybrid database demo");
        product.setImage("demo_product.jpg");
        product.setStockQuantity(50);
        product.setVendorId(1);
        
        hybridDb.addProduct(product)
            .thenAccept(productId -> {
                runOnUiThread(() -> {
                    if (productId > 0) {
                        String message = "Product added to SQLite: " + productId;
                        if (hybridDb.isOnline() && hybridDb.getSyncStatus().contains("Auto-sync: ON")) {
                            message += " (synced to cloud)";
                        } else {
                            message += " (local only)";
                        }
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Product added: " + productId);
                    } else {
                        Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to add product");
                    }
                    updateStatus();
                });
            })
            .exceptionally(throwable -> {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error adding product", throwable);
                });
                return null;
            });
    }
    
    private void syncAllData() {
        Toast.makeText(this, "Syncing SQLite data to cloud...", Toast.LENGTH_SHORT).show();
        
        hybridDb.syncAllToCloud()
            .thenRun(() -> {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Sync completed", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Manual sync completed");
                    updateStatus();
                });
            })
            .exceptionally(throwable -> {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Sync failed: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Sync failed", throwable);
                });
                return null;
            });
    }
    
    private void toggleAutoSync() {
        boolean currentStatus = hybridDb.getSyncStatus().contains("Auto-sync: ON");
        hybridDb.setAutoSync(!currentStatus);
        
        runOnUiThread(() -> {
            String message = "Auto-sync " + (!currentStatus ? "enabled" : "disabled");
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            updateStatus();
        });
    }
    
    private void toggleCloud() {
        boolean currentStatus = hybridDb.getSyncStatus().contains("Cloud: ENABLED");
        hybridDb.setCloudEnabled(!currentStatus);
        
        runOnUiThread(() -> {
            String message = "Cloud functionality " + (!currentStatus ? "enabled" : "disabled");
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            updateStatus();
        });
    }
    
    private void getAllProducts() {
        List<Product> products = hybridDb.getAllProducts();
        
        runOnUiThread(() -> {
            String message = "Found " + products.size() + " products locally";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            Log.d(TAG, message);
            
            // Log first few products
            for (int i = 0; i < Math.min(3, products.size()); i++) {
                Product p = products.get(i);
                Log.d(TAG, "Product " + (i+1) + ": " + p.getProductName());
            }
        });
    }
    
    private void updateStatus() {
        String networkStatus = "Network: " + NetworkUtils.getNetworkType(this);
        String syncStatus = hybridDb.getSyncStatus();
        
        statusText.setText(networkStatus);
        syncStatusText.setText(syncStatus);
        
        Log.d(TAG, "Status updated - " + networkStatus + ", " + syncStatus);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateStatus();
    }
}
