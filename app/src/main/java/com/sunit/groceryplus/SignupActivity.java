package com.sunit.groceryplus;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.sunit.groceryplus.models.User;
import com.sunit.groceryplus.network.ApiService;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private TextInputEditText nameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText phoneEditText;
    private TextInputEditText addressEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;
    private Button signupButton;
    private TextView loginTextView;
    private UserRepository userRepository;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        initViews();

        // Initialize repositories
        userRepository = new UserRepository(this);
        apiService = new ApiService(this);

        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signupButton = findViewById(R.id.signupButton);
        loginTextView = findViewById(R.id.loginTextView);
    }

    private void setClickListeners() {
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSignup();
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void performSignup() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneEditText.setError("Phone number is required");
            phoneEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(address)) {
            addressEditText.setError("Address is required");
            addressEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            confirmPasswordEditText.requestFocus();
            return;
        }

        // Disable button during registration
        signupButton.setEnabled(false);
        signupButton.setText("Registering...");

        // Try API registration first
        Log.d(TAG, "Attempting API registration for email: " + email);
        apiService.register(name, email, password, phone, new ApiService.ApiCallback<org.json.JSONObject>() {
            @Override
            public void onSuccess(org.json.JSONObject response) {
                Log.d(TAG, "=== API REGISTRATION SUCCESSFUL ===");
                Toast.makeText(SignupActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                // Navigate to login screen
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "API registration failed: " + error + " - Trying database registration");

                // Fallback to database registration (for admin users)
                if (userRepository.isUserExists(email)) {
                    signupButton.setEnabled(true);
                    signupButton.setText("Sign Up");
                    Toast.makeText(SignupActivity.this, "User with this email already exists", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean success = userRepository.registerUser(name, email, phone, password, "customer");
                if (success) {
                    Log.d(TAG, "=== DATABASE REGISTRATION SUCCESSFUL ===");
                    Toast.makeText(SignupActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "User registered: " + name + " (" + email + ")");

                    // Navigate to login screen
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    signupButton.setEnabled(true);
                    signupButton.setText("Sign Up");
                    Toast.makeText(SignupActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Both API and database registration failed for email: " + email);
                }
            }
        });
    }
}