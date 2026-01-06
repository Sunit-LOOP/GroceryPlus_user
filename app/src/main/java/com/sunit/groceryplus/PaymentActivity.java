package com.sunit.groceryplus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.sunit.groceryplus.models.Address;
import com.sunit.groceryplus.models.CartItem;
import com.sunit.groceryplus.models.PaymentIntentParams;
import com.sunit.groceryplus.network.RetrofitClient;
import com.sunit.groceryplus.network.StripeService;
import com.sunit.groceryplus.utils.GroceryNotificationManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** PaymentActivity - Final checkout gateway supporting Stripe integration, promo codes, and address management. */
public class PaymentActivity extends AppCompatActivity {

    // Infrastructure & User
    private static final String TAG = "PaymentActivity";
    private int userId = -1;
    private GroceryNotificationManager notificationManager;
    private DatabaseHelper dbHelper;

    // UI Elements - Order Summary
    private TextView totalAmountTv, summarySubtotal, summaryDeliveryFee, summaryDiscount;
    private View discountRow;
    private android.widget.EditText instructionsEt;
    private TextInputEditText promoCodeEt;
    private Button applyPromoBtn, payNowBtn;

    // UI Elements - Address & Options
    private TextView addressTypeTv, addressDetailTv;
    private View changeAddressBtn, pickOnMapBtn;
    private RadioButton creditCardRadio, cashOnDeliveryRadio;
    private MaterialCardView stripeCard, codCard;

    // Transaction Data
    private double finalAmount = 0.0, baseSubtotal = 0.0, deliveryFee = 0.0, discountAmount = 0.0;
    private String appliedPromoCode = null;

    // Domain Data
    private AddressRepository addressRepository;
    private Address selectedAddress;
    private int selectedAddressId = -1;
    
    // Stripe Components
    private PaymentSheet paymentSheet;
    private String paymentClientSecret;

    /** Initializes the activity, Stripe SDK, and loads order summary. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize Stripe with centralized key
        PaymentConfiguration.init(getApplicationContext(), com.sunit.groceryplus.utils.PaymentConfig.STRIPE_PUBLISHABLE_KEY);
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);
        notificationManager = GroceryNotificationManager.getInstance(this);

        addressRepository = new AddressRepository(this);

        userId = getIntent().getIntExtra("user_id", -1);

        initViews();
        setupToolbar();
        loadCartData();
        loadSelectedAddress();
    }

    /** Links UI components to functional fields and sets interaction listeners. */
    private void initViews() {
        totalAmountTv = findViewById(R.id.paymentTotalAmount);
        summarySubtotal = findViewById(R.id.summarySubtotal);
        summaryDeliveryFee = findViewById(R.id.summaryDeliveryFee);
        instructionsEt = findViewById(R.id.instructionsEt);
        payNowBtn = findViewById(R.id.paymentPayNowBtn);

        addressTypeTv = findViewById(R.id.addressTypeTv);
        addressDetailTv = findViewById(R.id.addressDetailTv);
        changeAddressBtn = findViewById(R.id.changeAddressBtn);
        pickOnMapBtn = findViewById(R.id.pickOnMapBtn);

        promoCodeEt = findViewById(R.id.promoCodeEt);
        applyPromoBtn = findViewById(R.id.applyPromoBtn);
        discountRow = findViewById(R.id.discountRow);
        summaryDiscount = findViewById(R.id.summaryDiscount);

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

        if (changeAddressBtn != null) {
            changeAddressBtn.setOnClickListener(v -> openAddressManagement());
        }

        if (pickOnMapBtn != null) {
            pickOnMapBtn.setOnClickListener(v -> Toast.makeText(this, "Pick on map coming soon", Toast.LENGTH_SHORT).show());
        }

        if (applyPromoBtn != null) {
            applyPromoBtn.setOnClickListener(v -> applyPromoCode());
        }
    }

    /** Configures the toolbar with back navigation. */
    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.paymentToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Payment");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /** Populates the order summary from intent extras. */
    private void loadCartData() {
        // Get amounts from intent
        baseSubtotal = getIntent().getDoubleExtra("subtotal_amount", 0.0);
        finalAmount = getIntent().getDoubleExtra("total_amount", 0.0);
        deliveryFee = finalAmount - baseSubtotal;
        discountAmount = 0.0;
        appliedPromoCode = null;

        // Update UI
        summarySubtotal.setText("Rs. " + String.format("%.2f", baseSubtotal));
        if (summaryDeliveryFee != null) {
            summaryDeliveryFee.setText("Rs. " + String.format("%.2f", deliveryFee));
        }
        totalAmountTv.setText("Rs. " + String.format("%.2f", finalAmount));

        if (discountRow != null) {
            discountRow.setVisibility(View.GONE);
        }

        // Enable pay button and set initial text based on selected payment method
        payNowBtn.setEnabled(true);
        updatePayButtonText();
        updateCardStyles();
    }

    /** Retrieves and displays the user's default delivery address. */
    private void loadSelectedAddress() {
        try {
            List<Address> addresses = addressRepository.getUserAddresses(userId);
            selectedAddress = null;
            selectedAddressId = -1;

            if (addresses != null) {
                for (Address a : addresses) {
                    if (a != null && a.isDefault()) {
                        selectedAddress = a;
                        break;
                    }
                }
                if (selectedAddress == null && !addresses.isEmpty()) {
                    selectedAddress = addresses.get(0);
                }
            }

            if (selectedAddress != null) {
                selectedAddressId = selectedAddress.getAddressId();
                if (addressTypeTv != null) {
                    addressTypeTv.setText(selectedAddress.getType());
                }
                if (addressDetailTv != null) {
                    String detail = selectedAddress.getFullAddress();
                    if (selectedAddress.getLandmark() != null && !selectedAddress.getLandmark().trim().isEmpty()) {
                        detail = detail + ", " + selectedAddress.getLandmark();
                    }
                    if (selectedAddress.getCity() != null && !selectedAddress.getCity().trim().isEmpty()) {
                        detail = detail + ", " + selectedAddress.getCity();
                    }
                    addressDetailTv.setText(detail);
                }
            } else {
                if (addressTypeTv != null) {
                    addressTypeTv.setText("Address");
                }
                if (addressDetailTv != null) {
                    addressDetailTv.setText("Add your address to continue");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading address", e);
        }
    }

    /** Navigates to the address management screen. */
    private void openAddressManagement() {
        Intent intent = new Intent(this, AddressManagementActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }

    /** Validates and applies a discount code to the total amount. */
    private void applyPromoCode() {
        if (promoCodeEt == null) return;

        String code = promoCodeEt.getText() != null ? promoCodeEt.getText().toString().trim().toUpperCase(Locale.US) : "";
        if (code.isEmpty()) {
            Toast.makeText(this, "Enter a promo code", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            com.sunit.groceryplus.models.Promotion promo = dbHelper.getPromotionByCode(code);
            if (promo == null) {
                Toast.makeText(this, "Invalid promo code", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isPromotionValid(promo)) {
                Toast.makeText(this, "Promo code expired", Toast.LENGTH_SHORT).show();
                return;
            }

            appliedPromoCode = code;
            discountAmount = Math.max(0.0, baseSubtotal * (promo.getDiscountPercentage() / 100.0));
            if (discountAmount > baseSubtotal) {
                discountAmount = baseSubtotal;
            }

            finalAmount = (baseSubtotal - discountAmount) + deliveryFee;
            updateTotalsUi();
            Toast.makeText(this, "Promo applied", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error applying promo", e);
            Toast.makeText(this, "Error applying promo", Toast.LENGTH_SHORT).show();
        }
    }

    /** Verifies if a promotion is still within its validity period. */
    private boolean isPromotionValid(com.sunit.groceryplus.models.Promotion promo) {
        try {
            if (promo == null || promo.getValidUntil() == null) return false;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date expiry = sdf.parse(promo.getValidUntil());
            if (expiry == null) return false;

            Date today = sdf.parse(sdf.format(new Date()));
            if (today == null) return false;

            return !expiry.before(today);
        } catch (Exception e) {
            return false;
        }
    }

    /** Updates the order total and discount displays. */
    private void updateTotalsUi() {
        if (summarySubtotal != null) {
            summarySubtotal.setText("Rs. " + String.format("%.2f", baseSubtotal));
        }

        if (summaryDeliveryFee != null) {
            summaryDeliveryFee.setText("Rs. " + String.format("%.2f", deliveryFee));
        }

        if (discountRow != null && summaryDiscount != null) {
            if (discountAmount > 0.0) {
                discountRow.setVisibility(View.VISIBLE);
                summaryDiscount.setText("-Rs. " + String.format("%.2f", discountAmount));
            } else {
                discountRow.setVisibility(View.GONE);
            }
        }

        if (totalAmountTv != null) {
            totalAmountTv.setText("Rs. " + String.format("%.2f", finalAmount));
        }

        updatePayButtonText();
    }

    /** Configures UI for Stripe Card payment selection. */
    private void selectStripePayment() {
        creditCardRadio.setChecked(true);
        cashOnDeliveryRadio.setChecked(false);
        updateCardStyles();
        updatePayButtonText();
    }

    /** Configures UI for Cash on Delivery selection. */
    private void selectCodPayment() {
        creditCardRadio.setChecked(false);
        cashOnDeliveryRadio.setChecked(true);
        updateCardStyles();
        updatePayButtonText();
    }

    /** Orchestrates the payment flow based on the selected method. */
    private void processPayment() {
        if (selectedAddressId == -1) {
            Toast.makeText(this, "Please add/select a delivery address", Toast.LENGTH_SHORT).show();
            openAddressManagement();
            return;
        }

        if (creditCardRadio.isChecked()) {
            // Stripe payment
            startStripePayment();
        } else if (cashOnDeliveryRadio.isChecked()) {
            // Cash on Delivery
            createOrder("cod");
        }
    }

    /** Initiates the Stripe payment flow by fetching a Client Secret. */
    private void startStripePayment() {
        // Safety check for placeholder key
        String secretKeyRaw = com.sunit.groceryplus.utils.PaymentConfig.STRIPE_SECRET_KEY;
        if (secretKeyRaw == null || secretKeyRaw.contains("...") || secretKeyRaw.isEmpty()) {
            Toast.makeText(this, "Please set your actual Stripe Secret Key in PaymentConfig.java", Toast.LENGTH_LONG).show();
            return;
        }

        payNowBtn.setEnabled(false);
        payNowBtn.setText("Processing...");
        
        // Test: Show a simple toast to confirm button click works
        Toast.makeText(this, "Starting Stripe payment...", Toast.LENGTH_SHORT).show();
        
        // Use centralized Stripe Secret Key from PaymentConfig
        String secretKey = "Bearer " + secretKeyRaw;
        
        // Use Math.round to avoid precision issues with floating point
        int amountInSmallestUnit = (int) Math.round(finalAmount * 100); 
        
        // Use centralized Stripe Secret Key and Currency from PaymentConfig
        String currency = com.sunit.groceryplus.utils.PaymentConfig.CURRENCY;
        Log.d(TAG, "Starting Stripe Payment: Amount=" + amountInSmallestUnit + ", Currency=" + currency);
        Log.d(TAG, "Secret Key (first 10 chars): " + secretKey.substring(0, Math.min(10, secretKey.length())) + "...");
        
        com.sunit.groceryplus.network.StripeApiClient.getClient().create(StripeService.class).createPaymentIntent(
            secretKey,
            amountInSmallestUnit,
            currency,
            true
        ).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null && response.body().has("client_secret")) {
                    paymentClientSecret = response.body().get("client_secret").getAsString();
                    Log.d(TAG, "Successfully received client secret");
                    
                    // 2. Present Payment Sheet
                    PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("GroceryPlus")
                        .allowsDelayedPaymentMethods(false)
                        .build();
                    
                    try {
                        Log.d(TAG, "Presenting payment sheet...");
                        Log.d(TAG, "Client secret: " + paymentClientSecret.substring(0, Math.min(10, paymentClientSecret.length())) + "...");
                        paymentSheet.presentWithPaymentIntent(paymentClientSecret, configuration);
                        Log.d(TAG, "Payment sheet presented successfully");
                        
                        // Add debug delay to see if sheet appears
                        new android.os.Handler().postDelayed(() -> {
                            Log.d(TAG, "Payment sheet should be visible now");
                        }, 1000);
                        
                    } catch (Exception e) {
                        Log.e(TAG, "Error presenting payment sheet", e);
                        Toast.makeText(PaymentActivity.this, "Error showing payment form. Please try again.", Toast.LENGTH_LONG).show();
                        payNowBtn.setEnabled(true);
                        updatePayButtonText();
                        return;
                    }
                    
                    payNowBtn.setEnabled(true);
                    updatePayButtonText();
                } else {
                    String errorMsg = "Failed to initialize payment.";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Stripe Error Body: " + errorBody);
                            if (errorBody.contains("invalid_api_key")) {
                                errorMsg += " (Invalid API Key)";
                            } else if (errorBody.contains("invalid_request_error")) {
                                errorMsg += " (Invalid Request)";
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    
                    Log.e(TAG, "Stripe API Error: " + response.code() + " " + response.message());
                    Toast.makeText(PaymentActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    payNowBtn.setEnabled(true);
                    updatePayButtonText();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage());
                String errorMessage = "Network error: " + t.getMessage();
                
                // Check for SSL/Certificate specific errors
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("SSL") || t.getMessage().contains("certificate") || 
                        t.getMessage().contains("trust") || t.getMessage().contains("handshake")) {
                        errorMessage = "SSL Certificate Error. Please check your network connection and try again.";
                    } else if (t.getMessage().contains("timeout") || t.getMessage().contains("Timeout")) {
                        errorMessage = "Connection timeout. Please check your internet connection and try again.";
                    } else if (t.getMessage().contains("UnknownHost") || t.getMessage().contains("Network")) {
                        errorMessage = "Network connection error. Please check your internet connection.";
                    }
                }
                
                Toast.makeText(PaymentActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                payNowBtn.setEnabled(true);
                updatePayButtonText();
            }
        });
    }

    /** Callback for Stripe PaymentSheet results. */
    private void onPaymentSheetResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, "Payment Successful!", Toast.LENGTH_LONG).show();
            createOrder("stripe");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(this, "Payment Canceled", Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            PaymentSheetResult.Failed error = (PaymentSheetResult.Failed) paymentSheetResult;
            Toast.makeText(this, "Payment Failed: " + error.getError().getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /** Orchestrates order creation in the local database. */
    private void createOrder(String paymentMethod) {
        Log.d(TAG, "Creating order with payment method: " + paymentMethod);

        // Get cart items from database
        CartRepository cartRepo = new CartRepository(this);
        List<CartItem> cartItems = cartRepo.getCartItems(userId);
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        createOrderDatabase(paymentMethod);
    }

    /** Persists order, items, and payment records to SQLite; triggers notifications. */
    private void createOrderDatabase(String paymentMethod) {
        // Create order using direct database method
        double subtotal = baseSubtotal;
        double deliveryFee = this.deliveryFee;
        String instructions = instructionsEt.getText().toString().trim();
        long orderId = dbHelper.createOrder(userId, finalAmount, deliveryFee, "PENDING", selectedAddressId, instructions);

        if (orderId != -1) {
            // Add payment record for tracking
            // For Stripe, we can use the paymentClientSecret (or its ID) as transaction ID if we want, but unique timestamp is fine for local.
            String txnId = "TXN_" + System.currentTimeMillis();
            if (paymentMethod.equals("stripe") && paymentClientSecret != null) {
                // Shorten client secret to just ID if possible, or leave as is.
                txnId = "STRIPE_" + System.currentTimeMillis(); 
            }
            
            long paymentId = dbHelper.addPayment((int) orderId, finalAmount, paymentMethod, txnId);
            if (paymentId == -1) {
                Log.e(TAG, "Failed to add payment record for order: " + orderId);
            }

            try {
                CartRepository cartRepo = new CartRepository(this);
                List<CartItem> cartItems = cartRepo.getCartItems(userId);
                if (cartItems != null) {
                    for (CartItem item : cartItems) {
                        dbHelper.addOrderItem((int) orderId, item.getProductId(), item.getQuantity(), item.getPrice());
                        dbHelper.decrementStock(item.getProductId(), item.getQuantity());
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to transfer cart items to order", e);
            }

            // Clear cart after successful order
            dbHelper.clearCart(userId);

            // Send Notifications
            String title = "Order Placed Successfully";
            String msg = "Your order #" + orderId + " has been placed. We're processing it now!";
            if ("cod".equalsIgnoreCase(paymentMethod)) {
                title = "Order Confirmed (COD)";
                msg = "Order #" + orderId + " confirmed! Please keep Rs. " + String.format("%.2f", finalAmount) + " ready for Cash on Delivery.";
            }
            notificationManager.sendNotification(userId, title, msg, GroceryNotificationManager.TYPE_ORDER, String.valueOf(orderId));

            if (!"cod".equalsIgnoreCase(paymentMethod)) {
                notificationManager.sendNotification(userId, "Payment Successful", "Payment for order #" + orderId + " has been received successfully.", GroceryNotificationManager.TYPE_PAYMENT, String.valueOf(orderId));
            }

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

    /** Resets the pay button to its idle state. */
    private void resetPayButton() {
        payNowBtn.setEnabled(true);
        payNowBtn.setText("Pay Rs. " + String.format("%.2f", finalAmount) + " with Stripe");
    }

    /** Synchronizes action button text with the selected payment method. */
    private void updatePayButtonText() {
        if (creditCardRadio.isChecked()) {
            payNowBtn.setText("Pay Rs. " + String.format("%.2f", finalAmount) + " with Stripe");
        } else {
            payNowBtn.setText("Place Order (Cash on Delivery)");
        }
    }

    /** Updates visual styling for the payment method cards. */
    private void updateCardStyles() {
        try {
            if (stripeCard == null || codCard == null) return;

            if (creditCardRadio != null && creditCardRadio.isChecked()) {
                stripeCard.setStrokeColor(getResources().getColor(R.color.primary));
                stripeCard.setStrokeWidth(2);
                codCard.setStrokeColor(getResources().getColor(R.color.chip_background_color));
                codCard.setStrokeWidth(1);
            } else {
                stripeCard.setStrokeColor(getResources().getColor(R.color.chip_background_color));
                stripeCard.setStrokeWidth(1);
                codCard.setStrokeColor(getResources().getColor(R.color.primary));
                codCard.setStrokeWidth(2);
            }
        } catch (Exception ignored) {
        }
    }

    /** Checks for active internet connectivity. */
    private boolean isNetworkAvailable() {
        android.net.ConnectivityManager cm = (android.net.ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSelectedAddress();
    }

    /** Handles toolbar menu selections. */
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
