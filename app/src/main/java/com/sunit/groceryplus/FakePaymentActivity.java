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


/**
 * FakePaymentActivity - Simulates the Stripe payment gateway environment.
 * 
 * This activity provides a realistic-looking credit card entry form to simulate
 * a Stripe checkout experience. It handles input validation (Luhn algorithm not fully implemented, but basic checks are),
 * displays a card preview that updates in real-time, and simulates a payment processing delay
 * before successfully placing the order.
 * 
 * Key Features:
 * - Realistic Card Input Form (Number, Expiry, CVV, Name)
 * - Live Card Preview
 * - Simulated Processing Delay (Authorizing -> Confirming -> Finalizing)
 * - Integration with Order Creation logic upon success
 * 
 * @author GroceryPlus Development Team
 * @version 1.0
 * @since 1.0
 */
public class FakePaymentActivity extends AppCompatActivity {

    private TextInputEditText cardNumberEt, cardExpiryEt, cardCvvEt, cardNameEt;
    private TextView cardPreviewNumber, cardPreviewName, cardPreviewExpiry;
    private Button processPaymentBtn;

    private View paymentProcessingOverlay;
    private TextView processingSubtitleTv;

    private int userId;
    private double amount;
    private double subtotal;
    private double deliveryFee;
    private int addressId;
    private String instructions;
    private com.sunit.groceryplus.DatabaseHelper dbHelper;
    private GroceryNotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_payment);

        userId = getIntent().getIntExtra("user_id", -1);
        amount = getIntent().getDoubleExtra("amount", 0.0);
        subtotal = getIntent().getDoubleExtra("subtotal_amount", 0.0);
        deliveryFee = getIntent().getDoubleExtra("delivery_fee", -1.0);
        addressId = getIntent().getIntExtra("address_id", -1);
        instructions = getIntent().getStringExtra("delivery_instructions");
        dbHelper = new com.sunit.groceryplus.DatabaseHelper(this);
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

        paymentProcessingOverlay = findViewById(R.id.paymentProcessingOverlay);
        processingSubtitleTv = findViewById(R.id.processingSubtitleTv);
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
            showProcessingOverlay(true);
            notificationManager.sendNotification(userId, "Payment Processing", "Your payment for order is being processed safely.", GroceryNotificationManager.TYPE_PAYMENT, null);

            Handler handler = new Handler(Looper.getMainLooper());
            // Stage 1
            handler.postDelayed(() -> updateProcessingStatus("Authorizing…"), 350);
            // Stage 2
            handler.postDelayed(() -> updateProcessingStatus("Confirming…"), 1150);
            // Stage 3
            handler.postDelayed(() -> updateProcessingStatus("Finalizing…"), 2100);

            // Finish
            handler.postDelayed(() -> {
                createOrder();
            }, 3000);
        }
    }

    private void showProcessingOverlay(boolean show) {
        if (paymentProcessingOverlay != null) {
            paymentProcessingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void updateProcessingStatus(String status) {
        if (processingSubtitleTv != null && status != null) {
            processingSubtitleTv.setText(status);
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
        double resolvedDeliveryFee = deliveryFee;
        if (resolvedDeliveryFee < 0) {
            resolvedDeliveryFee = amount - subtotal;
        }

        long orderId = dbHelper.createOrder(userId, amount, resolvedDeliveryFee, "PENDING", addressId, instructions);
        if (orderId != -1) {
            // Add payment record for tracking
            long paymentId = dbHelper.addPayment((int)orderId, amount, "stripe", "TXN_" + System.currentTimeMillis());
            if (paymentId == -1) {
                Log.e("FakePaymentActivity", "Failed to add payment record for order: " + orderId);
            }

            try {
                CartRepository cartRepo = new CartRepository(this);
                java.util.List<com.sunit.groceryplus.models.CartItem> cartItems = cartRepo.getCartItems(userId);
                if (cartItems != null) {
                    for (com.sunit.groceryplus.models.CartItem item : cartItems) {
                        dbHelper.addOrderItem((int) orderId, item.getProductId(), item.getQuantity(), item.getPrice());
                        dbHelper.decrementStock(item.getProductId(), item.getQuantity());
                    }
                }
            } catch (Exception e) {
                Log.e("FakePaymentActivity", "Failed to transfer cart items to order", e);
            }

            // Clear cart after successful payment
            dbHelper.clearCart(userId);

            notificationManager.sendNotification(userId, "Payment Successful", "Your payment of Rs. " + String.format("%.2f", amount) + " was successful. Your order #" + orderId + " is now pending.", GroceryNotificationManager.TYPE_PAYMENT, String.valueOf(orderId));
            notificationManager.sendNotification(userId, "Order Placed", "Your order #" + orderId + " has been placed successfully!", GroceryNotificationManager.TYPE_ORDER, String.valueOf(orderId));

            Toast.makeText(this, "Payment successful!", Toast.LENGTH_LONG).show();
            showProcessingOverlay(false);
            Intent intent = new Intent(this, com.sunit.groceryplus.OrderSuccessActivity.class);
            intent.putExtra("user_id", userId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            showProcessingOverlay(false);
            processPaymentBtn.setEnabled(true);
            Toast.makeText(this, "Error creating order", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (paymentProcessingOverlay != null && paymentProcessingOverlay.getVisibility() == View.VISIBLE) {
            // Prevent back during processing
            return;
        }
        super.onBackPressed();
    }
}
