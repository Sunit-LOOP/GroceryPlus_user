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
import com.sunit.groceryplus.network.ApiClient;
import com.sunit.groceryplus.network.ApiService;
import com.sunit.groceryplus.network.PaymentIntentRequest;
import com.sunit.groceryplus.network.PaymentIntentResponse;
import com.sunit.groceryplus.utils.Config;

import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "PaymentActivity";

    // UI Elements
    private TextView totalAmountTv, summarySubtotal, summaryDeliveryFee;
    private Button payNowBtn;
    private RadioButton creditCardRadio, cashOnDeliveryRadio;
    private MaterialCardView stripeCard, codCard;
    
    private PaymentSheet paymentSheet;
    private String paymentIntentClientSecret;
    private double finalAmount = 0.0;
    private int userId = -1;

    // Database helper
    private com.sunit.groceryplus.DatabaseHelper dbHelper;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        
        // Initialize Stripe
        PaymentConfiguration.init(this, Config.STRIPE_PUBLISHABLE_KEY);
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
        
        // Get user ID from intent
        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Invalid user", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Initialize database helper
        dbHelper = new com.sunit.groceryplus.DatabaseHelper(this);
        apiService = new ApiService(this);
        
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
    
    private void createPaymentIntent() {
        Log.d(TAG, "Creating PaymentIntent for amount: " + finalAmount);
        
        // Convert amount to paisa (Stripe expects amount in smallest currency unit)
        int amountInPaisa = (int) (finalAmount * 100);

        PaymentIntentRequest request = new PaymentIntentRequest(amountInPaisa, "npr");
        
        ApiClient.getStripeApi().createPaymentIntent(request)
            .enqueue(new Callback<PaymentIntentResponse>() {
                @Override
                public void onResponse(Call<PaymentIntentResponse> call, Response<PaymentIntentResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        paymentIntentClientSecret = response.body().getClientSecret();
                        Log.d(TAG, "PaymentIntent created successfully");
                        presentPaymentSheet();
                    } else {
                        String errorMsg = "Failed to create payment intent";
                        Log.e(TAG, errorMsg + " (Code: " + response.code() + ")");
                        Toast.makeText(PaymentActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        resetPayButton();
                    }
                }
                
                @Override
                public void onFailure(Call<PaymentIntentResponse> call, Throwable t) {
                    Log.e(TAG, "Network error creating PaymentIntent", t);
                    String errorMsg = "Network error: " + t.getMessage();
                    
                    // More specific timeout and network error handling
                    if (t instanceof java.net.SocketTimeoutException) {
                        errorMsg = "Payment request timed out. Please check your connection and try again.";
                    } else if (t instanceof java.net.ConnectException) {
                        errorMsg = "Cannot connect to payment server. Please check internet connection.";
                    } else if (t instanceof java.net.UnknownHostException) {
                        errorMsg = "Payment server not found. Please check server URL.";
                    } else if (t instanceof java.io.IOException) {
                        errorMsg = "Network error. Please check your internet connection.";
                    }
                    
                    Toast.makeText(PaymentActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    resetPayButton();
                }
            });
    }
    
    private void presentPaymentSheet() {
        if (paymentIntentClientSecret != null) {
            PaymentSheet.Configuration configuration = new PaymentSheet.Configuration("GroceryPlus");
            paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration);
        } else {
            Log.e(TAG, "Cannot present PaymentSheet - client secret is null");
            Toast.makeText(this, "Payment configuration error", Toast.LENGTH_SHORT).show();
            resetPayButton();
        }
    }
    
    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        resetPayButton();

        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, "Payment successfully completed!", Toast.LENGTH_SHORT).show();
            createOrder("stripe");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Log.d(TAG, "Payment canceled");
            Toast.makeText(this, "Payment canceled", Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Throwable error = ((PaymentSheetResult.Failed) paymentSheetResult).getError();
            Log.e(TAG, "Payment failed", error);
            Toast.makeText(this, "Payment failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void createOrder(String paymentMethod) {
        Log.d(TAG, "Creating order with payment method: " + paymentMethod);

        // Get cart items from database for the API call
        CartRepository cartRepo = new CartRepository(this);
        java.util.List<CartItem> cartItems = cartRepo.getCartItems(userId);
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare cart items for API
        org.json.JSONArray cartItemsArray = new org.json.JSONArray();
        try {
            for (CartItem item : cartItems) {
                org.json.JSONObject itemObj = new org.json.JSONObject();
                itemObj.put("product_id", item.getProductId());
                itemObj.put("quantity", item.getQuantity());
                cartItemsArray.put(itemObj);
            }
        } catch (org.json.JSONException e) {
            Log.e(TAG, "Error preparing cart items", e);
            Toast.makeText(this, "Error preparing order", Toast.LENGTH_SHORT).show();
            return;
        }

        // Try API first, fallback to database
        apiService.placeOrder(cartItemsArray, "Kathmandu, Nepal", new ApiService.ApiCallback<org.json.JSONObject>() {
            @Override
            public void onSuccess(org.json.JSONObject response) {
                Log.d(TAG, "Order placed successfully via API");

                // Clear cart after successful order
                dbHelper.clearCart(userId);

                // Show success message
                String message = "Order placed successfully!";
                Toast.makeText(PaymentActivity.this, message, Toast.LENGTH_LONG).show();

                // Navigate to order success
                Intent intent = new Intent(PaymentActivity.this, com.sunit.groceryplus.OrderSuccessActivity.class);
                intent.putExtra("user_id", userId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "API place order failed: " + error + " - Using database");
                createOrderDatabase(paymentMethod);
            }
        });
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