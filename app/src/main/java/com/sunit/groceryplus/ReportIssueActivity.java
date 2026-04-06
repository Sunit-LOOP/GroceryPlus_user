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

/** ReportIssueActivity - Allows users to report problems with individual items in delivered orders. */
public class ReportIssueActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    
    private int orderId, userId, orderItemId;
    private String productName;
    private String encodedImage = "";
    
    private TextView itemNameTv, uploadHintTv;
    private AutoCompleteTextView issueTypeSpinner;
    private TextInputEditText descriptionEt;
    private ImageView issueIv;
    private OrderRepository orderRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);

        orderId = getIntent().getIntExtra("order_id", -1);
        userId = getIntent().getIntExtra("user_id", -1);
        orderItemId = getIntent().getIntExtra("order_item_id", -1);
        productName = getIntent().getStringExtra("product_name");

        orderRepository = new OrderRepository(this);
        initViews();
        setupSpinner();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        itemNameTv = findViewById(R.id.itemNameTv);
        itemNameTv.setText("Product: " + productName);
        
        issueTypeSpinner = findViewById(R.id.issueTypeSpinner);
        descriptionEt = findViewById(R.id.descriptionEt);
        issueIv = findViewById(R.id.issueIv);
        uploadHintTv = findViewById(R.id.uploadHintTv);
        
        findViewById(R.id.uploadPhotoCard).setOnClickListener(v -> openGallery());
        findViewById(R.id.submitBtn).setOnClickListener(v -> submitReport());
    }

    private void setupSpinner() {
        String[] issues = {"Missing Item", "Wrong Item", "Damaged Product", "Expired Product", "Quality Issue", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, issues);
        issueTypeSpinner.setAdapter(adapter);
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
                issueIv.setImageBitmap(bitmap);
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

    private void submitReport() {
        String issueType = issueTypeSpinner.getText().toString();
        String description = descriptionEt.getText().toString();

        if (issueType.isEmpty()) {
            Toast.makeText(this, "Please select issue type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.isEmpty()) {
            Toast.makeText(this, "Please provide a description", Toast.LENGTH_SHORT).show();
            return;
        }

        if (encodedImage.isEmpty()) {
            Toast.makeText(this, "Please upload a photo as proof", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = orderRepository.reportItemIssue(userId, orderId, orderItemId, issueType, description, encodedImage);
        if (success) {
            Toast.makeText(this, "Issue reported successfully. We will get back to you.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to submit report", Toast.LENGTH_SHORT).show();
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
