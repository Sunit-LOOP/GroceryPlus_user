package com.sunit.groceryplus;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.sunit.groceryplus.models.User;

import com.google.android.material.textfield.TextInputEditText;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    
    private TextInputEditText nameEt;
    private TextInputEditText emailEt;
    private TextInputEditText phoneEt;
    private TextInputEditText addressEt;
    private Button saveBtn;
    private Button cancelBtn;
    
    private int userId;
    private UserRepository userRepository;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Get user ID from intent
        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Error: Invalid user session", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize repository
        userRepository = new UserRepository(this);

        // Initialize views
        initViews();

        // Load current user data
        loadUserData();

        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        nameEt = findViewById(R.id.editProfileNameEt);
        emailEt = findViewById(R.id.editProfileEmailEt);
        phoneEt = findViewById(R.id.editProfilePhoneEt);
        addressEt = findViewById(R.id.editProfileAddressEt);
        saveBtn = findViewById(R.id.saveProfileBtn);
        cancelBtn = findViewById(R.id.cancelProfileBtn);
    }

    private void loadUserData() {
        try {
            currentUser = userRepository.getUserById(userId);
            
            if (currentUser != null) {
                nameEt.setText(currentUser.getName());
                emailEt.setText(currentUser.getEmail());
                phoneEt.setText(currentUser.getPhone());
                // Address field removed - User model doesn't have address
            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading user data", e);
            Toast.makeText(this, "Error loading user data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setClickListeners() {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveProfile() {
        // Get input values
        String name = nameEt.getText().toString().trim();
        String email = emailEt.getText().toString().trim();
        String phone = phoneEt.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty()) {
            nameEt.setError("Name is required");
            nameEt.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            emailEt.setError("Email is required");
            emailEt.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("Invalid email address");
            emailEt.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            phoneEt.setError("Phone is required");
            phoneEt.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            phoneEt.setError("Phone number must be at least 10 digits");
            phoneEt.requestFocus();
            return;
        }

        try {
            // Update user data (address removed)
            boolean success = userRepository.updateUser(
                userId,
                name,
                email,
                phone,
                "" // Empty address for now
            );

            if (success) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating profile", e);
            Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
        }
    }
}
