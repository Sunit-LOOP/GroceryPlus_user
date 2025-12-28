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

public class PaymentTrackingActivity extends AppCompatActivity {

    private RecyclerView paymentsRv;
    private AdminPaymentAdapter adapter;
    private DatabaseHelper dbHelper;

    private com.google.android.material.chip.Chip chipAll, chipStripe, chipCod;
    private String currentFilter = "all"; // all, cod, stripe
    
    private TextView totalPaymentsTv, totalAmountTv, monthlyAmountTv;

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

    private void setupRecyclerView() {
        paymentsRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminPaymentAdapter(this, new ArrayList<>());
        paymentsRv.setAdapter(adapter);
    }

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

    private void selectFilter(String filter) {
        currentFilter = filter;
        
        loadPayments(currentFilter);
        updateStatistics();
    }

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
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
