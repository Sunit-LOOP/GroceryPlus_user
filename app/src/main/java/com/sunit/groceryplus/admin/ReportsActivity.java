package com.sunit.groceryplus.admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/** ReportsActivity - Data export center for generating CSV reports of orders, sales, inventory, and customers. */
public class ReportsActivity extends AppCompatActivity {

    // UI Components
    private Button exportOrdersBtn, exportSalesBtn, exportInventoryBtn, exportCustomersBtn;
    private TextView lastExportTv;
    
    // Data & Constants
    private DatabaseHelper dbHelper;
    private static final int REQUEST_WRITE_STORAGE = 1;

    /** Initializes the activity, UI components, and event listeners. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        dbHelper = new DatabaseHelper(this);

        setupToolbar();
        initViews();
        setupClickListeners();
    }

    /** Configures the toolbar with navigation features and title. */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Reports & Exports");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /** Binds UI components to their respective layout IDs. */
    private void initViews() {
        exportOrdersBtn = findViewById(R.id.exportOrdersBtn);
        exportSalesBtn = findViewById(R.id.exportSalesBtn);
        exportInventoryBtn = findViewById(R.id.exportInventoryBtn);
        exportCustomersBtn = findViewById(R.id.exportCustomersBtn);
        lastExportTv = findViewById(R.id.lastExportTv);
    }

    /** Sets click listeners for report export buttons. */
    private void setupClickListeners() {
        exportOrdersBtn.setOnClickListener(v -> checkStorageAndExport("orders"));
        exportSalesBtn.setOnClickListener(v -> checkStorageAndExport("sales"));
        exportInventoryBtn.setOnClickListener(v -> checkStorageAndExport("inventory"));
        exportCustomersBtn.setOnClickListener(v -> checkStorageAndExport("customers"));
    }

    /** Verifies storage permissions before initiating the export process. */
    private void checkStorageAndExport(String reportType) {
        // Check for storage permission (for Android < 10)
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        } else {
            exportReport(reportType);
        }
    }

    /** Processes storage permission request results. */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE && grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            // Permission granted, proceed with export
        } else {
            Toast.makeText(this, "Storage permission required for exports", Toast.LENGTH_SHORT).show();
        }
    }

    /** Generates a CSV file for the specified report type in the Downloads directory. */
    private void exportReport(String reportType) {
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }

            String fileName = "groceryplus_" + reportType + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".csv";
            File file = new File(downloadsDir, fileName);

            FileWriter writer = new FileWriter(file);

            switch (reportType) {
                case "orders":
                    exportOrders(writer);
                    break;
                case "sales":
                    exportSales(writer);
                    break;
                case "inventory":
                    exportInventory(writer);
                    break;
                case "customers":
                    exportCustomers(writer);
                    break;
            }

            writer.close();

            lastExportTv.setText("Last export: " + fileName);
            Toast.makeText(this, "Exported to Downloads/" + fileName, Toast.LENGTH_LONG).show();

            // Open the downloads folder
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(android.net.Uri.parse(file.getAbsolutePath()), "resource/folder");
            startActivity(intent);

        } catch (IOException e) {
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /** Orchestrates the CSV writing process for order data. */
    private void exportOrders(FileWriter writer) throws IOException {
        // Header
        writer.append("Order ID,User ID,User Name,User Email,Total Amount,Delivery Fee,Status,Order Date,Shipped Date,Delivery Person\n");

        List<com.sunit.groceryplus.models.Order> orders = dbHelper.getUserOrders(-1); // Get all orders
        for (com.sunit.groceryplus.models.Order order : orders) {
            writer.append(String.format(Locale.getDefault(),
                    "%d,%d,%s,%s,%.2f,%.2f,%s,%s,%s,%s\n",
                    order.getOrderId(),
                    order.getUserId(),
                    escapeCsv(order.getUserName()),
                    escapeCsv(order.getUserEmail()),
                    order.getTotalAmount(),
                    order.getDeliveryFee(),
                    escapeCsv(order.getStatus()),
                    escapeCsv(order.getOrderDate()),
                    escapeCsv(order.getShippedDate()),
                    escapeCsv(order.getDeliveryPersonName())
            ));
        }
    }

    /** Orchestrates the CSV writing process for sales summary data. */
    private void exportSales(FileWriter writer) throws IOException {
        // Header
        writer.append("Order ID,Order Date,Total Amount,Delivery Fee,Net Amount,Status\n");

        List<com.sunit.groceryplus.models.Order> orders = dbHelper.getUserOrders(-1);
        for (com.sunit.groceryplus.models.Order order : orders) {
            double netAmount = order.getTotalAmount() + order.getDeliveryFee();
            writer.append(String.format(Locale.getDefault(),
                    "%d,%s,%.2f,%.2f,%.2f,%s\n",
                    order.getOrderId(),
                    escapeCsv(order.getOrderDate()),
                    order.getTotalAmount(),
                    order.getDeliveryFee(),
                    netAmount,
                    escapeCsv(order.getStatus())
            ));
        }
    }

    /** Orchestrates the CSV writing process for inventory data. */
    private void exportInventory(FileWriter writer) throws IOException {
        // Header
        writer.append("Product ID,Product Name,Category ID,Price,Stock,Vendor ID\n");

        List<com.sunit.groceryplus.models.Product> products = dbHelper.getAllProducts();
        for (com.sunit.groceryplus.models.Product product : products) {
            writer.append(String.format(Locale.getDefault(),
                    "%d,%s,%d,%.2f,%d,%d\n",
                    product.getProductId(),
                    escapeCsv(product.getProductName()),
                    product.getCategoryId(),
                    product.getPrice(),
                    product.getStock(),
                    product.getVendorId()
            ));
        }
    }

    /** Orchestrates the CSV writing process for customer data. */
    private void exportCustomers(FileWriter writer) throws IOException {
        // Header
        writer.append("User ID,Name,Email,Phone,User Type,Created At\n");

        List<com.sunit.groceryplus.models.User> users = dbHelper.getAllUsers();
        for (com.sunit.groceryplus.models.User user : users) {
            writer.append(String.format(Locale.getDefault(),
                    "%d,%s,%s,%s,%s,%s\n",
                    user.getUserId(),
                    escapeCsv(user.getUserName()),
                    escapeCsv(user.getUserEmail()),
                    escapeCsv(user.getUserPhone()),
                    escapeCsv(user.getUserType()),
                    escapeCsv(user.getCreatedAt())
            ));
        }
    }

    /** Escapes special characters to ensure CSV compatibility. */
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
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
}
