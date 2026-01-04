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

/**
 * PaymentActivity - Handles order payment and placement.
 * 
 * This activity is the final step in the checkout process. It allows users to review their order totals,
 * select a delivery address, apply promo codes, and choose a payment method (Stripe or Cash on Delivery).
 * Upon successful payment or order placement, it creates the order record and notifies the user.
 * 
 * Key Features:
 * - Order summary display (Subtotal, Delivery Fee, Discount, Total)
 * - Address selection and management
 * - Promo code application
 * - Payment method selection (Card vs COD)
 * - INTEGRATION with REAL Stripe PaymentSheet
 * - Order creation and inventory management
 * - Notification triggers upon order success
 * 
 * @author GroceryPlus Development Team
 * @version 1.0
 * @since 1.0
 */
public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "PaymentActivity";

    // UI Elements
    private TextView totalAmountTv, summarySubtotal, summaryDeliveryFee;
    private android.widget.EditText instructionsEt;
    private Button payNowBtn;
    private RadioButton creditCardRadio, cashOnDeliveryRadio;
    private MaterialCardView stripeCard, codCard;

    private TextView addressTypeTv;
    private TextView addressDetailTv;
    private View changeAddressBtn;
    private View pickOnMapBtn;

    private TextInputEditText promoCodeEt;
    private Button applyPromoBtn;
    private View discountRow;
    private TextView summaryDiscount;

    // Transaction Data
    private double finalAmount = 0.0;
    private double baseSubtotal = 0.0;
    private double deliveryFee = 0.0;
    private double discountAmount = 0.0;
    private String appliedPromoCode = null;

    // User & Session
    private int userId = -1;
    private GroceryNotificationManager notificationManager;

    private AddressRepository addressRepository;
    private Address selectedAddress;
    private int selectedAddressId = -1;

    // Database helper
    private DatabaseHelper dbHelper;
    
    // Stripe
    private PaymentSheet paymentSheet;
    private String paymentClientSecret;

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

    private void openAddressManagement() {
        Intent intent = new Intent(this, AddressManagementActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }

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

    private void startStripePayment() {
        // Safety check for placeholder key
        String secretKeyRaw = com.sunit.groceryplus.utils.PaymentConfig.STRIPE_SECRET_KEY;
        if (secretKeyRaw == null || secretKeyRaw.contains("...") || secretKeyRaw.isEmpty()) {
            Toast.makeText(this, "Please set your actual Stripe Secret Key in PaymentConfig.java", Toast.LENGTH_LONG).show();
            return;
        }

        payNowBtn.setEnabled(false);
        payNowBtn.setText("Processing...");
        
        // Use centralized Stripe Secret Key from PaymentConfig
        String secretKey = "Bearer " + secretKeyRaw;
        
        // Use Math.round to avoid precision issues with floating point
        int amountInSmallestUnit = (int) Math.round(finalAmount * 100); 
        
        // Use centralized Stripe Secret Key and Currency from PaymentConfig
        String currency = com.sunit.groceryplus.utils.PaymentConfig.CURRENCY;
        Log.d(TAG, "Starting Stripe Payment: Amount=" + amountInSmallestUnit + ", Currency=" + currency);
        
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
                        .allowsDelayedPaymentMethods(true)
                        .build();
                        
                    paymentSheet.presentWithPaymentIntent(paymentClientSecret, configuration);
                    
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
                Toast.makeText(PaymentActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                payNowBtn.setEnabled(true);
                updatePayButtonText();
            }
        });
    }

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

    private void resetPayButton() {
        payNowBtn.setEnabled(true);
        payNowBtn.setText("Pay Rs. " + String.format("%.2f", finalAmount) + " with Stripe");
    }

    private void updatePayButtonText() {
        if (creditCardRadio.isChecked()) {
            payNowBtn.setText("Pay Rs. " + String.format("%.2f", finalAmount) + " with Stripe");
        } else {
            payNowBtn.setText("Place Order (Cash on Delivery)");
        }
    }

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

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}