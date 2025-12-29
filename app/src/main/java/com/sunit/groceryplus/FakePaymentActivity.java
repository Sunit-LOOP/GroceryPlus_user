package com.sunit.groceryplus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.sunit.groceryplus.utils.GroceryNotificationManager;
import com.sunit.groceryplus.utils.LoadingDialog;

public class FakePaymentActivity extends AppCompatActivity {

    private TextInputEditText cardNumberEt, cardExpiryEt, cardCvvEt, cardNameEt;
    private TextView cardPreviewNumber, cardPreviewName, cardPreviewExpiry;
    private Button processPaymentBtn;

    private int userId;
    private double amount;
    private double subtotal;
    private String instructions;
    private com.sunit.groceryplus.DatabaseHelper dbHelper;
    private LoadingDialog loadingDialog;
    private GroceryNotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_payment);

        userId = getIntent().getIntExtra("user_id", -1);
        amount = getIntent().getDoubleExtra("amount", 0.0);
        subtotal = getIntent().getDoubleExtra("subtotal_amount", 0.0);
        instructions = getIntent().getStringExtra("delivery_instructions");
        dbHelper = new com.sunit.groceryplus.DatabaseHelper(this);
        loadingDialog = new LoadingDialog(this);
        notificationManager = GroceryNotificationManager.getInstance(this);

        initViews();
        setupToolbar();
        setupTextWatchers();

        processPaymentBtn.setOnClickListener(v -> handlePayment());
    }

    private void initViews() {
        cardNumberEt = findViewById(R.id.cardNumberEt);
        cardExpiryEt = findViewById(R.id.cardExpiryEt);
        cardCvvEt = findViewById(R.id.cardCvvEt);
        cardNameEt = findViewById(R.id.cardNameEt);

        cardPreviewNumber = findViewById(R.id.cardPreviewNumber);
        cardPreviewName = findViewById(R.id.cardPreviewName);
        cardPreviewExpiry = findViewById(R.id.cardPreviewExpiry);

        processPaymentBtn = findViewById(R.id.processPaymentBtn);
        processPaymentBtn.setText("Pay Rs. " + String.format("%.2f", amount));
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Stripe Payment");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupTextWatchers() {
        cardNumberEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cardPreviewNumber.setText(s.length() == 0 ? "**** **** **** 4242" : s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        cardNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cardPreviewName.setText(s.length() == 0 ? "JOHN DOE" : s.toString().toUpperCase());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        cardExpiryEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cardPreviewExpiry.setText(s.length() == 0 ? "12/25" : s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void handlePayment() {
        if (validateFields()) {
            processPaymentBtn.setEnabled(false);
            loadingDialog.startLoadingDialog();
            notificationManager.sendNotification(userId, "Payment Processing", "Your payment for order is being processed safely.", GroceryNotificationManager.TYPE_PAYMENT, null);
            // Simulate payment processing delay
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                loadingDialog.dismissDialog();
                createOrder();
            }, 3000); // 3 seconds delay
        }
    }

    private boolean validateFields() {
        if (cardNumberEt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter card number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (cardExpiryEt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter expiry date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (cardCvvEt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter CVV", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (cardNameEt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter cardholder name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void createOrder() {
        double deliveryFee = amount - subtotal;
        long orderId = dbHelper.createOrder(userId, amount, deliveryFee, "PENDING", -1, instructions);
        if (orderId != -1) {
            // Add payment record for tracking
            long paymentId = dbHelper.addPayment((int)orderId, amount, "stripe", "TXN_" + System.currentTimeMillis());
            if (paymentId == -1) {
                Log.e("FakePaymentActivity", "Failed to add payment record for order: " + orderId);
            }

            // Clear cart after successful payment
            dbHelper.clearCart(userId);

            notificationManager.sendNotification(userId, "Payment Successful", "Your payment of Rs. " + String.format("%.2f", amount) + " was successful. Your order #" + orderId + " is now pending.", GroceryNotificationManager.TYPE_PAYMENT, String.valueOf(orderId));
            notificationManager.sendNotification(userId, "Order Placed", "Your order #" + orderId + " has been placed successfully!", GroceryNotificationManager.TYPE_ORDER, String.valueOf(orderId));

            Toast.makeText(this, "Payment successful!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, com.sunit.groceryplus.OrderSuccessActivity.class);
            intent.putExtra("user_id", userId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error creating order", Toast.LENGTH_SHORT).show();
        }
    }
}
