package com.sunit.groceryplus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
    private double finalAmount = 0.0, baseSubtotal = 0.0, deliveryFee = 0.0, discountAmount = 0.0, pointsDiscount = 0.0;
    private double walletBalance = 0.0, walletDiscount = 0.0;
    private int userPoints = 0;
    private String appliedPromoCode = null;
    private boolean isUsingPoints = false, isUsingWallet = false;

    // UI Elements - Loyalty Points & Wallet
    private TextView availablePointsTv, pointsValueTv, summaryPointsDiscount;
    private TextView walletBalancePaymentTv, summaryWalletDiscount;
    private View pointsDiscountRow, walletDiscountRow;
    private com.google.android.material.switchmaterial.SwitchMaterial usePointsSwitch, useWalletSwitch;

    // Domain Data
    private AddressRepository addressRepository;
    private OrderRepository orderRepository;
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

        // Remove complex window flags that might interfere with PaymentSheet
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Initialize Stripe with centralized key
        PaymentConfiguration.init(getApplicationContext(), com.sunit.groceryplus.utils.PaymentConfig.STRIPE_PUBLISHABLE_KEY);
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);
        notificationManager = GroceryNotificationManager.getInstance(this);

        addressRepository = new AddressRepository(this);
        orderRepository = new OrderRepository(this);

        userId = getIntent().getIntExtra("user_id", -1);

        initViews();
        setupToolbar();
        loadCartData();
        loadSelectedAddress();
        setupLoyaltyPoints();
        setupWallet();
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
        stripeCard.setOnClickListener(v -> {
            Log.d(TAG, "Stripe card clicked");
            selectStripePayment();
        });
        codCard.setOnClickListener(v -> {
            Log.d(TAG, "COD card clicked");
            selectCodPayment();
        });

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

        // Initialize Loyalty Points Views
        availablePointsTv = findViewById(R.id.availablePointsTv);
        pointsValueTv = findViewById(R.id.pointsValueTv);
        usePointsSwitch = findViewById(R.id.usePointsSwitch);
        pointsDiscountRow = findViewById(R.id.pointsDiscountRow);
        summaryPointsDiscount = findViewById(R.id.summaryPointsDiscount);

        if (usePointsSwitch != null) {
            usePointsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                isUsingPoints = isChecked;
                calculateTotals();
            });
        }

        // Initialize Wallet Views
        walletBalancePaymentTv = findViewById(R.id.walletBalancePaymentTv);
        useWalletSwitch = findViewById(R.id.useWalletSwitch);
        walletDiscountRow = findViewById(R.id.walletDiscountRow);
        summaryWalletDiscount = findViewById(R.id.summaryWalletDiscount);

        if (useWalletSwitch != null) {
            useWalletSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                isUsingWallet = isChecked;
                calculateTotals();
            });
        }
    }

    /** Configures the toolbar with back navigation. */
    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.paymentToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /** Populates the order summary from intent extras. */
    private void loadCartData() {
        // Get amounts from intent
        baseSubtotal = getIntent().getDoubleExtra("subtotal_amount", 0.0);
        finalAmount = getIntent().getDoubleExtra("total_amount", 0.0);
        
        // Calculate delivery fee with free delivery logic
        if (baseSubtotal >= com.sunit.groceryplus.utils.PaymentConfig.FREE_DELIVERY_THRESHOLD) {
            deliveryFee = 0.0; // Free delivery for orders 500 or more
        } else {
            deliveryFee = finalAmount - baseSubtotal; // Normal delivery fee
        }
        
        // Recalculate final amount with correct delivery fee
        finalAmount = baseSubtotal + deliveryFee;
        
        discountAmount = 0.0;
        appliedPromoCode = null;

        // Update UI
        summarySubtotal.setText("NPR " + String.format("%.2f", baseSubtotal));
        if (summaryDeliveryFee != null) {
            if (deliveryFee == 0.0) {
                summaryDeliveryFee.setText("FREE");
                summaryDeliveryFee.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                summaryDeliveryFee.setText("NPR " + String.format("%.2f", deliveryFee));
                summaryDeliveryFee.setTextColor(getResources().getColor(R.color.text_primary));
            }
        }
        totalAmountTv.setText("NPR " + String.format("%.2f", finalAmount));

        if (discountRow != null) {
            discountRow.setVisibility(View.GONE);
        }

        // Enable pay button and set initial text based on selected payment method
        payNowBtn.setEnabled(true);
        calculateTotals();
    }

    /** Fetches the user's loyalty points and updates the discount UI section. */
    private void setupLoyaltyPoints() {
        if (userId == -1) return;
        
        userPoints = (int) dbHelper.getLoyaltyPoints(userId);
        if (availablePointsTv != null) {
            availablePointsTv.setText("Available: " + userPoints + " Points");
        }
        
        if (pointsValueTv != null) {
            pointsValueTv.setText("(Equivalent to NPR " + String.format("%.2f", (double) userPoints) + ")");
        }
        
        if (usePointsSwitch != null) {
            usePointsSwitch.setEnabled(userPoints > 0);
        }
    }

    /** Fetches the user's wallet balance and updates the discount UI section. */
    private void setupWallet() {
        if (userId == -1) return;
        
        walletBalance = dbHelper.getWalletBalance(userId);
        if (walletBalancePaymentTv != null) {
            walletBalancePaymentTv.setText("Balance: NPR " + String.format("%.2f", walletBalance));
        }
        
        if (useWalletSwitch != null) {
            useWalletSwitch.setEnabled(walletBalance > 0);
        }
    }

    /** Calculates the final amount considering subtotal, delivery fee, promo codes, and loyalty points. */
    private void calculateTotals() {
        // 1. Reset from base
        finalAmount = baseSubtotal + deliveryFee;
        
        // 2. Apply Promo Code
        if (appliedPromoCode != null) {
            // Already calculated in applyPromoCode() logic
            finalAmount -= discountAmount;
        }
        
        // 3. Apply Wallet Balance
        if (isUsingWallet && walletBalance > 0) {
            walletDiscount = Math.min(finalAmount, walletBalance);
            finalAmount -= walletDiscount;
        } else {
            walletDiscount = 0.0;
        }

        // 4. Apply Loyalty Points
        if (isUsingPoints && userPoints > 0) {
            pointsDiscount = Math.min(finalAmount, (double) userPoints);
            finalAmount -= pointsDiscount;
        } else {
            pointsDiscount = 0.0;
        }
        
        // 5. Ensure total is not negative
        if (finalAmount < 0) finalAmount = 0;
        
        updateTotalsUi();
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

            calculateTotals();
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
            summarySubtotal.setText("NPR " + String.format("%.2f", baseSubtotal));
        }

        if (summaryDeliveryFee != null) {
            if (deliveryFee == 0.0) {
                summaryDeliveryFee.setText("FREE");
                summaryDeliveryFee.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                summaryDeliveryFee.setText("NPR " + String.format("%.2f", deliveryFee));
                summaryDeliveryFee.setTextColor(getResources().getColor(R.color.text_primary));
            }
        }

        if (discountRow != null && summaryDiscount != null) {
            if (discountAmount > 0.0) {
                discountRow.setVisibility(View.VISIBLE);
                summaryDiscount.setText("-NPR " + String.format("%.2f", discountAmount));
            } else {
                discountRow.setVisibility(View.GONE);
            }
        }

        if (totalAmountTv != null) {
            totalAmountTv.setText("NPR " + String.format("%.2f", finalAmount));
        }

        if (pointsDiscountRow != null && summaryPointsDiscount != null) {
            if (pointsDiscount > 0.0) {
                pointsDiscountRow.setVisibility(View.VISIBLE);
                summaryPointsDiscount.setText("-NPR " + String.format("%.2f", pointsDiscount));
            } else {
                pointsDiscountRow.setVisibility(View.GONE);
            }
        }

        if (walletDiscountRow != null && summaryWalletDiscount != null) {
            if (walletDiscount > 0.0) {
                walletDiscountRow.setVisibility(View.VISIBLE);
                summaryWalletDiscount.setText("-NPR " + String.format("%.2f", walletDiscount));
            } else {
                walletDiscountRow.setVisibility(View.GONE);
            }
        }

        updatePayButtonText();
        updateCardStyles();
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

    /** Test method to check if other input fields work in the app. */
    private void testOtherInputFields() {
        Log.d(TAG, "=== Testing Other Input Fields ===");
        
        // Test promo code input field (without filling data)
        if (promoCodeEt != null) {
            promoCodeEt.requestFocus();
            Log.d(TAG, "Promo code field focus: " + promoCodeEt.hasFocus());
            Log.d(TAG, "Promo code field enabled: " + promoCodeEt.isEnabled());
            promoCodeEt.clearFocus();
        }
        
        // Test instructions input field (without filling data)
        if (instructionsEt != null) {
            instructionsEt.requestFocus();
            Log.d(TAG, "Instructions field focus: " + instructionsEt.hasFocus());
            Log.d(TAG, "Instructions field enabled: " + instructionsEt.isEnabled());
            instructionsEt.clearFocus();
        }
        
        // Test pay button
        if (payNowBtn != null) {
            Log.d(TAG, "Pay button enabled: " + payNowBtn.isEnabled());
            Log.d(TAG, "Pay button clickable: " + payNowBtn.isClickable());
        }
        
        Log.d(TAG, "Input field testing completed");
    }

    /** Check for potential overlays that might interfere with PaymentSheet. */
    private void checkForOverlays() {
        Log.d(TAG, "=== Checking for Overlays ===");
        
        // Check if any dialogs are showing
        if (isFinishing()) {
            Log.w(TAG, "Activity is finishing");
        }
        
        // Check window flags
        int flags = getWindow().getAttributes().flags;
        Log.d(TAG, "Window flags: " + flags);
        Log.d(TAG, "FLAG_NOT_TOUCH_MODAL: " + ((flags & WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL) != 0));
        Log.d(TAG, "FLAG_NOT_FOCUSABLE: " + ((flags & WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE) != 0));
        Log.d(TAG, "FLAG_ALT_FOCUSABLE_IM: " + ((flags & WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM) != 0));
        
        // Check decor view
        View decorView = getWindow().getDecorView();
        Log.d(TAG, "Decor view: " + (decorView != null ? "exists" : "null"));
        Log.d(TAG, "Decor view focusable: " + (decorView != null ? decorView.isFocusable() : "null"));
    }

    /** Debug method to log system information. */
    private void logSystemInfo() {
        Log.d(TAG, "=== System Information ===");
        Log.d(TAG, "SDK Version: " + android.os.Build.VERSION.SDK_INT);
        Log.d(TAG, "Device: " + android.os.Build.MODEL);
        Log.d(TAG, "Manufacturer: " + android.os.Build.MANUFACTURER);
        Log.d(TAG, "Android Version: " + android.os.Build.VERSION.RELEASE);
        
        // Check for potential issues
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            Log.d(TAG, "Android 11+ - Check for scoped storage issues");
        }
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            Log.d(TAG, "Android 12+ - Check for new privacy features");
        }
    }

    /** Presents the Stripe PaymentSheet with proper configuration and focus handling. */
    private void presentPaymentSheet() {
        try {
            if (paymentClientSecret == null || !paymentClientSecret.startsWith("pi_")) {
                showFinalErrorMessage("Invalid client secret received from Stripe.");
                return;
            }

            // Use simple configuration for maximum compatibility
            PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("GroceryPlus")
                .allowsDelayedPaymentMethods(true)
                .build();
            
            Log.d(TAG, "Presenting payment sheet...");
            
            // Clear any current focus that might interfere
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                currentFocus.clearFocus();
            }

            // Small delay to ensure any keyboard or processing UI is settled
            new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                    paymentSheet.presentWithPaymentIntent(paymentClientSecret, configuration);
                    Log.d(TAG, "Payment sheet present() called with secret: " + paymentClientSecret.substring(0, 10));
                } catch (Exception e) {
                    Log.e(TAG, "Exception during paymentSheet.present", e);
                    handlePaymentSheetError(e);
                }
            }, 500); // 500ms delay for UI stability
            
        } catch (Exception e) {
            Log.e(TAG, "Error preparing payment sheet", e);
            handlePaymentSheetError(e);
        }
    }

    /** Handles PaymentSheet presentation errors. */
    private void handlePaymentSheetError(Exception e) {
        Log.e(TAG, "PaymentSheet error: " + e.getMessage(), e);
        runOnUiThread(() -> {
            Toast.makeText(this, "UI Error: Unable to show payment sheet. " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            payNowBtn.setEnabled(true);
            updatePayButtonText();
        });
    }

    /** Initiates the Stripe payment flow by fetching a Client Secret. */
    private void startStripePayment() {
        try {
            // Safety check for placeholder key
            String secretKeyRaw = com.sunit.groceryplus.utils.PaymentConfig.STRIPE_SECRET_KEY;
            if (secretKeyRaw == null || secretKeyRaw.contains("...") || secretKeyRaw.isEmpty()) {
                Toast.makeText(this, "Please set your actual Stripe Secret Key in PaymentConfig.java", Toast.LENGTH_LONG).show();
                return;
            }

            if (finalAmount <= 0) {
                Toast.makeText(this, "Invalid order amount", Toast.LENGTH_SHORT).show();
                return;
            }

            payNowBtn.setEnabled(false);
            payNowBtn.setText("Step 1/2: Preparing…");
            
            Log.d(TAG, "Starting Stripe payment request with NPR...");
            
            // Use centralized Stripe Secret Key from PaymentConfig
            String authHeader = "Bearer " + secretKeyRaw;
            
            // Use Math.round to avoid precision issues with floating point
            // For NPR, 100 paisa = 1 Rupee, so * 100 is correct for Stripe smallest unit
            int amountInSmallestUnit = (int) Math.round(finalAmount * 100); 
            
            // Stripe minimum requirement check
            if (amountInSmallestUnit < 50) { // Essential minimum
                amountInSmallestUnit = 50; 
            }
            
            String currency = com.sunit.groceryplus.utils.PaymentConfig.CURRENCY.toLowerCase(Locale.US);
            
            // Use Map for flexible parameters
            java.util.Map<String, String> params = new java.util.HashMap<>();
            params.put("amount", String.valueOf(amountInSmallestUnit));
            params.put("currency", currency);
            
            // Standard modern approach for mobile PaymentSheet
            params.put("automatic_payment_methods[enabled]", "true");
            params.put("description", "GroceryPlus Order for User ID: " + userId);
            
            Log.d(TAG, "Requesting PaymentIntent: Amount=" + amountInSmallestUnit + " " + currency);

            com.sunit.groceryplus.network.StripeApiClient.getClient().create(StripeService.class).createPaymentIntent(
                authHeader,
                params
            ).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().has("client_secret")) {
                        paymentClientSecret = response.body().get("client_secret").getAsString();
                        Log.d(TAG, "Successfully received client secret: " + paymentClientSecret.substring(0, Math.min(10, paymentClientSecret.length())) + "...");
                        
                        runOnUiThread(() -> {
                            Toast.makeText(PaymentActivity.this, "Connecting to Stripe...", Toast.LENGTH_SHORT).show();
                            payNowBtn.setText("Step 2/2: Opening Form…");
                        });

                        // Present Payment Sheet
                        presentPaymentSheet();
                        
                    } else {
                        String errorMsg = "Stripe Error: " + response.code();
                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                Log.e(TAG, "Stripe API Error Body: " + errorBody);
                                
                                try {
                                    JsonObject errorObj = com.google.gson.JsonParser.parseString(errorBody).getAsJsonObject();
                                    if (errorObj.has("error")) {
                                        JsonObject innerError = errorObj.getAsJsonObject("error");
                                        if (innerError.has("message")) {
                                            errorMsg = "Stripe: " + innerError.get("message").getAsString();
                                        }
                                    }
                                } catch (Exception ignored) {}
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                        
                        showFinalErrorMessage(errorMsg);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    Log.e(TAG, "Retrofit request failed", t);
                    showFinalErrorMessage("Network Error: " + t.getLocalizedMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error in startStripePayment", e);
            showFinalErrorMessage("App Error: " + e.getLocalizedMessage());
        }
    }

    /** Helper to show error, re-enable button, and reset text in one place. */
    private void showFinalErrorMessage(String msg) {
        runOnUiThread(() -> {
            Toast.makeText(PaymentActivity.this, msg, Toast.LENGTH_LONG).show();
            payNowBtn.setEnabled(true);
            updatePayButtonText();
            Log.e(TAG, "Payment initiation failed: " + msg);
        });
    }

    /** Callback for Stripe PaymentSheet results. */
    private void onPaymentSheetResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, "Payment Successful!", Toast.LENGTH_LONG).show();
            createOrder("stripe");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(this, "Payment Canceled", Toast.LENGTH_SHORT).show();
            // Re-enable pay button and update text after cancellation
            payNowBtn.setEnabled(true);
            updatePayButtonText();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            PaymentSheetResult.Failed error = (PaymentSheetResult.Failed) paymentSheetResult;
            Toast.makeText(this, "Payment Failed: " + error.getError().getLocalizedMessage(), Toast.LENGTH_LONG).show();
            // Re-enable pay button and update text after failure
            payNowBtn.setEnabled(true);
            updatePayButtonText();
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
        // Create order using repository
        double subtotal = baseSubtotal;
        double deliveryFee = this.deliveryFee;
        String instructions = instructionsEt.getText().toString().trim();
        long orderId = orderRepository.createOrder(userId, finalAmount, deliveryFee, "PENDING", selectedAddressId, instructions);

        if (orderId != -1) {
            // Add payment record for tracking
            // For Stripe, we can use the paymentClientSecret (or its ID) as transaction ID if we want, but unique timestamp is fine for local.
            String txnId = "TXN_" + System.currentTimeMillis();
            String pStatus = "Pending"; // Default for COD
            
            if (paymentMethod.equals("stripe") && paymentClientSecret != null) {
                txnId = "STRIPE_" + System.currentTimeMillis(); 
                pStatus = "Completed"; // Stripe is already paid
            }
            
            long paymentId = dbHelper.addPayment((int) orderId, finalAmount, paymentMethod, txnId, pStatus);
            if (paymentId == -1) {
                Log.e(TAG, "Failed to add payment record for order: " + orderId);
            }

            try {
                CartRepository cartRepo = new CartRepository(this);
                List<CartItem> cartItems = cartRepo.getCartItems(userId);
                if (cartItems != null) {
                    for (CartItem item : cartItems) {
                        orderRepository.addOrderItem((int) orderId, item.getProductId(), item.getQuantity(), item.getPrice());
                        orderRepository.decrementStock(item.getProductId(), item.getQuantity());
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to transfer cart items to order", e);
            }

            // 1. Deduct Wallet Balance if used
            if (isUsingWallet && walletDiscount > 0) {
                dbHelper.logTransaction(userId, walletDiscount, "debit", "purchase_redemption", "Paid from Wallet for Order #" + orderId);
            }

            // 2. Deduct Loyalty Points if used
            if (isUsingPoints && pointsDiscount > 0) {
                dbHelper.addLoyaltyPoints(userId, -pointsDiscount);
                dbHelper.logTransaction(userId, pointsDiscount, "debit", "purchase_redemption", "Redeemed for Order #" + orderId);
            }

            // 2. Clear cart after successful order
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
            payNowBtn.setText("Pay NPR " + String.format("%.2f", finalAmount) + " with Stripe");
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
