package com.sunit.groceryplus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/** RefundRequestActivity - Allows users to formally request a refund for delivered items. 
 * Supports both original payment method refunds (Stripe) and Loyalty Points conversion (Cash). */
public class RefundRequestActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private int orderId, userId, productId;
    private String productName;
    private double amount;
    private String encodedImage = "";

    private TextView productNameTv, refundAmountTv, refundMethodInfoTv, uploadHintTv;
    private AutoCompleteTextView refundReasonSpinner;
    private TextInputEditText detailsEt;
    private ImageView proofIv;
    private OrderRepository orderRepository;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_request);

        // Get Intent Data
        orderId = getIntent().getIntExtra("order_id", -1);
        userId = getIntent().getIntExtra("user_id", -1);
        productId = getIntent().getIntExtra("product_id", -1);
        productName = getIntent().getStringExtra("product_name");
        amount = getIntent().getDoubleExtra("amount", 0.0);

        orderRepository = new OrderRepository(this);
        dbHelper = new DatabaseHelper(this);
        
        initViews();
        setupSpinner();
        determineRefundMethod();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        productNameTv = findViewById(R.id.productNameTv);
        refundAmountTv = findViewById(R.id.refundAmountTv);
        refundMethodInfoTv = findViewById(R.id.refundMethodInfoTv);
        refundReasonSpinner = findViewById(R.id.refundReasonSpinner);
        detailsEt = findViewById(R.id.detailsEt);
        proofIv = findViewById(R.id.proofIv);
        uploadHintTv = findViewById(R.id.uploadHintTv);

        productNameTv.setText(productName);
        refundAmountTv.setText("Refund Amount: Rs. " + String.format("%.2f", amount));

        findViewById(R.id.uploadPhotoCard).setOnClickListener(v -> openGallery());
        findViewById(R.id.submitRefundBtn).setOnClickListener(v -> submitRefundRequest());
    }

    private void setupSpinner() {
        String[] reasons = {
            "Product damaged/broken", 
            "Quality not as expected", 
            "Wrong item received", 
            "Item expired", 
            "Item missing from package", 
            "Other"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, reasons);
        refundReasonSpinner.setAdapter(adapter);
    }

    private void determineRefundMethod() {
        // Check payment method of the order
        String paymentMethod = "COD";
        android.database.Cursor cursor = dbHelper.getPaymentByOrderId(orderId);
        if (cursor != null && cursor.moveToFirst()) {
            paymentMethod = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PaymentEntry.COLUMN_NAME_PAYMENT_METHOD));
            cursor.close();
        }

        if ("Stripe".equalsIgnoreCase(paymentMethod) || "Credit Card".equalsIgnoreCase(paymentMethod)) {
            refundMethodInfoTv.setText("Refund Policy: Approved refunds will be credited back to your Wallet immediately.");
        } else {
            refundMethodInfoTv.setText("Refund Policy: Approved refunds will be added to your Wallet (Available for use in 3-5 days).");
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                proofIv.setImageBitmap(bitmap);
                uploadHintTv.setText("Photo Attached");

                // Encode to Base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] bytes = baos.toByteArray();
                encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT);
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void submitRefundRequest() {
        String reason = refundReasonSpinner.getText().toString();
        String details = detailsEt.getText().toString();

        if (reason.isEmpty()) {
            Toast.makeText(this, "Please select a reason", Toast.LENGTH_SHORT).show();
            return;
        }

        if (details.isEmpty()) {
            Toast.makeText(this, "Please provide more details", Toast.LENGTH_SHORT).show();
            return;
        }

        long refundId = orderRepository.requestRefund(userId, orderId, productId, reason, details, encodedImage);

        if (refundId != -1) {
            Toast.makeText(this, "Refund request #" + refundId + " submitted successfully.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to submit refund request. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
