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
import com.sunit.groceryplus.utils.GroceryNotificationManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    private com.google.android.material.textfield.TextInputEditText promoCodeEt;
    private Button applyPromoBtn;
    private View discountRow;
    private TextView summaryDiscount;

    private double finalAmount = 0.0;
    private double baseSubtotal = 0.0;
    private double deliveryFee = 0.0;
    private double discountAmount = 0.0;
    private String appliedPromoCode = null;

    private int userId = -1;
    private GroceryNotificationManager notificationManager;

    private AddressRepository addressRepository;
    private com.sunit.groceryplus.models.Address selectedAddress;
    private int selectedAddressId = -1;

    // Database helper
    private com.sunit.groceryplus.DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize Stripe
        // Initialize database helper
        dbHelper = new com.sunit.groceryplus.DatabaseHelper(this);
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
        summarySubtotal.setText("₹" + String.format("%.2f", baseSubtotal));
        if (summaryDeliveryFee != null) {
            summaryDeliveryFee.setText("₹" + String.format("%.2f", deliveryFee));
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
            java.util.List<com.sunit.groceryplus.models.Address> addresses = addressRepository.getUserAddresses(userId);
            selectedAddress = null;
            selectedAddressId = -1;

            if (addresses != null) {
                for (com.sunit.groceryplus.models.Address a : addresses) {
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
            summarySubtotal.setText("₹" + String.format("%.2f", baseSubtotal));
        }

        if (summaryDeliveryFee != null) {
            summaryDeliveryFee.setText("₹" + String.format("%.2f", deliveryFee));
        }

        if (discountRow != null && summaryDiscount != null) {
            if (discountAmount > 0.0) {
                discountRow.setVisibility(View.VISIBLE);
                summaryDiscount.setText("-₹" + String.format("%.2f", discountAmount));
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
        Log.d(TAG, "Starting Fake Stripe payment redirection");

        Intent intent = new Intent(this, FakePaymentActivity.class);
        intent.putExtra("user_id", userId);
        intent.putExtra("amount", finalAmount);
        intent.putExtra("subtotal_amount", baseSubtotal);
        intent.putExtra("delivery_fee", deliveryFee);
        intent.putExtra("address_id", selectedAddressId);
        intent.putExtra("promo_code", appliedPromoCode);
        intent.putExtra("discount_amount", discountAmount);
        intent.putExtra("delivery_instructions", instructionsEt.getText().toString().trim());
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
        double subtotal = baseSubtotal;
        double deliveryFee = this.deliveryFee;
        String instructions = instructionsEt.getText().toString().trim();
        long orderId = dbHelper.createOrder(userId, finalAmount, deliveryFee, "PENDING", selectedAddressId, instructions);

        if (orderId != -1) {
            // Add payment record for tracking
            long paymentId = dbHelper.addPayment((int) orderId, finalAmount, paymentMethod, "TXN_" + System.currentTimeMillis());
            if (paymentId == -1) {
                Log.e(TAG, "Failed to add payment record for order: " + orderId);
            }

            try {
                CartRepository cartRepo = new CartRepository(this);
                java.util.List<CartItem> cartItems = cartRepo.getCartItems(userId);
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