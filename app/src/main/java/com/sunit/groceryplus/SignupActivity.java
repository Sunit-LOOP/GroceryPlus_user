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

/**
 * SignupActivity - User registration interface for GroceryPlus
 * 
 * This activity provides the registration interface for new customers to create
 * accounts in the GroceryPlus application. It handles user input validation,
 * account creation, and navigation to the login screen upon successful registration.
 * 
 * Key Features:
 * - Complete user registration form
 * - Input validation with error messages
 * - Password confirmation matching
 * - Duplicate email checking
 * - Secure password hashing
 * - User role assignment (Customer)
 * - Navigation to login after successful signup
 * 
 * Registration Flow:
 * 1. User fills registration form with personal details
 * 2. Input validation performed for all fields
 * 3. Password confirmation verified
 * 4. Email uniqueness checked
 * 5. Account created with secure password hashing
 * 6. User redirected to login for authentication
 * 
 * Security Features:
 * - Password strength validation
 * - Secure password hashing with salt
 * - Input sanitization
 * - Email uniqueness enforcement
 * - SQL injection prevention
 * 
 * @author GroceryPlus Development Team
 * @version 1.0
 * @since 1.0
 */
public class SignupActivity extends AppCompatActivity {

    // Tag for logging and debugging
    private static final String TAG = "SignupActivity";
    
    // UI Components for registration form
    private TextInputEditText nameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText phoneEditText;
    private TextInputEditText addressEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;
    private Button signupButton;
    private TextView loginTextView;
    
    // User repository for database operations
    private UserRepository userRepository;

    /**
     * Called when the activity is first created
     * 
     * This method initializes the UI components, sets up the user repository,
     * and configures click listeners for user interactions.
     * 
     * @param savedInstanceState Previously saved state data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize all UI components
        initViews();

        // Initialize user repository for database operations
        userRepository = new UserRepository(this);

        // Set up click listeners for user interactions
        setClickListeners();
    }

    /**
     * Initialize all UI components by finding views in the layout
     */
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

    /**
     * Set up click listeners for all interactive UI elements
     */
    private void setClickListeners() {
        // Signup button click listener - performs user registration
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSignup();
            }
        });

        // Login text click listener - navigates to login screen
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Perform user registration with comprehensive validation
     * 
     * This method handles the complete signup flow including input validation,
     * password confirmation, email uniqueness checking, and account creation.
     * It provides detailed error messages and logging for debugging purposes.
     */
    private void performSignup() {
        // Get user input from all form fields
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

        // Use database registration
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
            Log.e(TAG, "Database registration failed for email: " + email);
        }
    }
}