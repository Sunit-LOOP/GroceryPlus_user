package com.sunit.groceryplus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import android.widget.RadioButton;
import com.sunit.groceryplus.models.CartItem;
import com.sunit.groceryplus.models.CartItem;
import com.sunit.groceryplus.utils.Config;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "PaymentActivity";

    // UI Elements
    private TextView totalAmountTv, summarySubtotal, summaryDeliveryFee;
    private Button payNowBtn;
    private RadioButton creditCardRadio, cashOnDeliveryRadio;
    private MaterialCardView stripeCard, codCard;
    
    private double finalAmount = 0.0;
    private int userId = -1;

    // Database helper
    private com.sunit.groceryplus.DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        
        // Initialize Stripe
        // Initialize database helper
        dbHelper = new com.sunit.groceryplus.DatabaseHelper(this);
        
        initViews();
        setupToolbar();
        loadCartData();
    }
    
    private void initViews() {
        totalAmountTv = findViewById(R.id.paymentTotalAmount);
        summarySubtotal = findViewById(R.id.summarySubtotal);
        summaryDeliveryFee = findViewById(R.id.summaryDeliveryFee);
        payNowBtn = findViewById(R.id.paymentPayNowBtn);

        // Initialize radio buttons
        creditCardRadio = findViewById(R.id.creditCardRadio);
        cashOnDeliveryRadio = findViewById(R.id.cashOnDeliveryRadio);

        // Initialize cards
        stripeCard = findViewById(R.id.stripeCard);
        codCard = findViewById(R.id.codCard);

        // Card click listeners for payment method selection
        stripeCard.setOnClickListener(v -> selectStripePayment());
        codCard.setOnClickListener(v -> selectCodPayment());

        // Pay button click listener
        payNowBtn.setOnClickListener(v -> processPayment());
    }
    
    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.paymentToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Payment");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void loadCartData() {
        // Get amounts from intent
        double subtotal = getIntent().getDoubleExtra("subtotal_amount", 0.0);
        finalAmount = getIntent().getDoubleExtra("total_amount", 0.0);
        double deliveryFee = finalAmount - subtotal;

        // Update UI
        summarySubtotal.setText("₹" + String.format("%.2f", subtotal));
        if (summaryDeliveryFee != null) {
            summaryDeliveryFee.setText("₹" + String.format("%.2f", deliveryFee));
        }
        totalAmountTv.setText("Rs. " + String.format("%.2f", finalAmount));

        // Enable pay button and set initial text based on selected payment method
        payNowBtn.setEnabled(true);
        updatePayButtonText();
        updateCardStyles();
    }

    private void selectStripePayment() {
        creditCardRadio.setChecked(true);
        cashOnDeliveryRadio.setChecked(false);
        updateCardStyles();
        updatePayButtonText();
    }

    private void selectCodPayment() {
        creditCardRadio.setChecked(false);
        cashOnDeliveryRadio.setChecked(true);
        updateCardStyles();
        updatePayButtonText();
    }

    private void updateCardStyles() {
        // Update card appearances based on selection
        if (creditCardRadio.isChecked()) {
            stripeCard.setStrokeColor(getResources().getColor(R.color.primary));
            codCard.setStrokeColor(getResources().getColor(R.color.chip_background_color));
        } else {
            stripeCard.setStrokeColor(getResources().getColor(R.color.chip_background_color));
            codCard.setStrokeColor(getResources().getColor(R.color.primary));
        }
    }

    private void updatePayButtonText() {
        if (creditCardRadio.isChecked()) {
            payNowBtn.setText("Pay Rs. " + String.format("%.2f", finalAmount) + " with Stripe");
        } else {
            payNowBtn.setText("Place Order (Cash on Delivery)");
        }
    }
    
    private void processPayment() {
        if (creditCardRadio.isChecked()) {
            // Stripe payment
            startStripePayment();
        } else if (cashOnDeliveryRadio.isChecked()) {
            // Cash on Delivery
            createOrder("cod");
        }
    }

    private void startStripePayment() {
        Log.d(TAG, "Starting Fake Stripe payment redirection");

        Intent intent = new Intent(this, FakePaymentActivity.class);
        intent.putExtra("user_id", userId);
        intent.putExtra("amount", finalAmount);
        intent.putExtra("subtotal_amount", getIntent().getDoubleExtra("subtotal_amount", 0.0));
        startActivity(intent);
    }
    
    private void createOrder(String paymentMethod) {
        Log.d(TAG, "Creating order with payment method: " + paymentMethod);

        // Get cart items from database
        CartRepository cartRepo = new CartRepository(this);
        java.util.List<CartItem> cartItems = cartRepo.getCartItems(userId);
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        createOrderDatabase(paymentMethod);
    }
    


    private void createOrderDatabase(String paymentMethod) {
        // Create order using direct database method
        double subtotal = getIntent().getDoubleExtra("subtotal_amount", 0.0);
        double deliveryFee = finalAmount - subtotal;
        long orderId = dbHelper.createOrder(userId, finalAmount, deliveryFee, "PENDING", -1);

        if (orderId != -1) {
            // Add payment record for tracking
            long paymentId = dbHelper.addPayment((int)orderId, finalAmount, paymentMethod, "TXN_" + System.currentTimeMillis());
            if (paymentId == -1) {
                Log.e(TAG, "Failed to add payment record for order: " + orderId);
            }

            // Clear cart after successful order
            dbHelper.clearCart(userId);

            // Show success message
            String message = "Order placed successfully!";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            // Navigate to order success
            Intent intent = new Intent(PaymentActivity.this, com.sunit.groceryplus.OrderSuccessActivity.class);
            intent.putExtra("user_id", userId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error creating order", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void resetPayButton() {
        payNowBtn.setEnabled(true);
        payNowBtn.setText("Pay Rs. " + String.format("%.2f", finalAmount) + " with Stripe");
    }
    
    private boolean isNetworkAvailable() {
        android.net.ConnectivityManager cm = (android.net.ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}