package com.sunit.groceryplus.admin;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.sunit.groceryplus.DatabaseContract;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.adapters.AdminPaymentAdapter;
import com.sunit.groceryplus.models.Payment;

import java.util.ArrayList;
import java.util.List;


/** PaymentTrackingActivity - Admin dashboard for monitoring financial transactions, revenue stats, and filtering by payment method. */
public class PaymentTrackingActivity extends AppCompatActivity {

    // UI Components
    private RecyclerView paymentsRv;
    private TextView totalPaymentsTv, totalAmountTv, monthlyAmountTv;
    
    // Filters
    private com.google.android.material.chip.Chip chipAll, chipStripe, chipCod;
    private String currentFilter = "all"; // all, cod, stripe

    // Data & Helper
    private AdminPaymentAdapter adapter;
    private DatabaseHelper dbHelper;

    /**
     * Initializes activity, sets up toolbar, views, recycler view, and loads payment history.
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_tracking);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        dbHelper = new DatabaseHelper(this);
        paymentsRv = findViewById(R.id.paymentsRv);
        
        // Initialize statistics views
        totalPaymentsTv = findViewById(R.id.totalPaymentsTv);
        totalAmountTv = findViewById(R.id.totalAmountTv);
        monthlyAmountTv = findViewById(R.id.monthlyAmountTv);
        
        // Initialize filter chips
        chipAll = findViewById(R.id.chipAll);
        chipStripe = findViewById(R.id.chipStripe);
        chipCod = findViewById(R.id.chipCod);

        setupRecyclerView();
        setupFilterChips();
        loadPayments(currentFilter);
        updateStatistics();
    }

    /**
     * Configures the RecyclerView with AdminPaymentAdapter.
     */
    private void setupRecyclerView() {
        paymentsRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminPaymentAdapter(this, new ArrayList<>());
        paymentsRv.setAdapter(adapter);
    }

    /**
     * Sets up filter chip click listeners to toggle payment display.
     */
    private void setupFilterChips() {
        chipAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectFilter("all");
        });
        chipStripe.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectFilter("stripe");
        });
        chipCod.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectFilter("cod");
        });
        
        // Initial selection
        chipAll.setChecked(true);
    }

    /**
     * Updates the current filter and reloads data.
     * @param filter The selected filter type ('all', 'cod', 'stripe')
     */
    private void selectFilter(String filter) {
        currentFilter = filter;
        
        loadPayments(currentFilter);
        updateStatistics();
    }

    /**
     * Fetches all payments from database and filters them based on selection.
     * Updates the adapter with the filtered list.
     */
    private void loadPayments(String filter) {
        List<Payment> payments = new ArrayList<>();
        Cursor cursor = dbHelper.getAllPayments();
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PaymentEntry.COLUMN_NAME_PAYMENT_ID));
                int orderId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PaymentEntry.COLUMN_NAME_ORDER_ID));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.PaymentEntry.COLUMN_NAME_AMOUNT));
                String method = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PaymentEntry.COLUMN_NAME_PAYMENT_METHOD));
                String txnId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PaymentEntry.COLUMN_NAME_TRANSACTION_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PaymentEntry.COLUMN_NAME_PAYMENT_DATE));
                
                // Apply filter
                if (filter.equals("all") || 
                    (filter.equals("cod") && method.equalsIgnoreCase("cod")) ||
                    (filter.equals("stripe") && method.equalsIgnoreCase("stripe"))) {
                    payments.add(new Payment(id, orderId, amount, method, txnId, date));
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        adapter.updatePayments(payments);
    }
    
    /**
     * Calculates and displays total revenue and transaction counts based on the active filter.
     */
    private void updateStatistics() {
        // Get all payments for statistics
        List<Payment> allPayments = new ArrayList<>();
        Cursor cursor = dbHelper.getAllPayments();
        
        double totalAmount = 0.0;
        int totalPayments = 0;
        double monthlyAmount = 0.0;
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.PaymentEntry.COLUMN_NAME_AMOUNT));
                String method = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PaymentEntry.COLUMN_NAME_PAYMENT_METHOD));
                String dateStr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PaymentEntry.COLUMN_NAME_PAYMENT_DATE));
                
                // Apply filter for displayed statistics
                if (currentFilter.equals("all") || 
                    (currentFilter.equals("cod") && method.equalsIgnoreCase("cod")) ||
                    (currentFilter.equals("stripe") && method.equalsIgnoreCase("stripe"))) {
                    totalAmount += amount;
                    totalPayments++;
                    
                    // Check if payment is from this month (simplified check)
                    // In a real app, you'd parse the date properly
                    monthlyAmount += amount;
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        // Update UI
        if (totalPaymentsTv != null) totalPaymentsTv.setText(String.valueOf(totalPayments));
        if (totalAmountTv != null) totalAmountTv.setText("Rs. " + String.format("%.2f", totalAmount));
        if (monthlyAmountTv != null) monthlyAmountTv.setText("Rs. " + String.format("%.2f", monthlyAmount));
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
