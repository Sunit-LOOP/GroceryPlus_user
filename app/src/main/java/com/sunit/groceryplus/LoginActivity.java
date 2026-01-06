package com.sunit.groceryplus;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.sunit.groceryplus.models.User;
import com.sunit.groceryplus.utils.HybridDatabaseManager;

import org.json.JSONObject;

/** LoginActivity - User authentication interface supporting hybrid database sync and role-based navigation. */
public class LoginActivity extends AppCompatActivity {

    // Tag for logging and debugging
    private static final String TAG = "LoginActivity";
    
    // UI Components
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private Button loginButton;
    private TextView signupTextView;
    private ImageView adminIcon;
    
    // Database Management
    private HybridDatabaseManager hybridDb;

    /** Initializes UI components, database managers, and click listeners. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // Initialize all UI components
        initViews();

        // Initialize hybrid database manager for authentication
        hybridDb = HybridDatabaseManager.getInstance(this);

        // Set up click listeners for user interactions
        setClickListeners();
    }

    /**
     * Initialize all UI components by finding views in the layout
     */
    private void initViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupTextView = findViewById(R.id.signupTextView);
        adminIcon = findViewById(R.id.adminIcon);
    }

    /**
     * Set up click listeners for all interactive UI elements
     */
    private void setClickListeners() {
        // Login button click listener - performs authentication
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        // Signup text click listener - navigates to registration
        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        // Admin icon click listener - shortcut to admin login
        adminIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AdminLoginActivity.class);
                startActivity(intent);
            }
        });
    }

    /** Performs input validation and executes the hybrid authentication flow. */
    private void performLogin() {
        // Get user input from text fields
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate email input
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        // Validate password input
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        // Disable login button during authentication to prevent multiple attempts
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        // Perform authentication using hybrid database
        hybridDb.authenticateUser(email, password)
            .thenAccept(user -> {
                // Handle authentication result on UI thread
                runOnUiThread(() -> {
                    if (user != null) {
                        // Authentication successful - log user details
                        Log.d(TAG, "=== HYBRID DATABASE LOGIN SUCCESSFUL ===");
                        Log.d(TAG, "User ID: " + user.getUserId());
                        Log.d(TAG, "User Name: " + user.getName());
                        Log.d(TAG, "User Email: " + user.getEmail());
                        Log.d(TAG, "User Type: " + user.getUserType());
                        Log.d(TAG, "Is Admin: " + user.isAdmin());

                        // Show success message to user
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        // Save user session to SharedPreferences for persistence
                        saveUserSession(user);

                        // Navigate to appropriate interface based on user type
                        navigateBasedOnUserRole(user);
                        
                    } else {
                        // Authentication failed - re-enable login button
                        loginButton.setEnabled(true);
                        loginButton.setText("Login");
                        Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Hybrid database login failed for email: " + email);
                    }
                });
            })
            .exceptionally(throwable -> {
                // Handle authentication errors
                runOnUiThread(() -> {
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                    Toast.makeText(LoginActivity.this, "Login error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Login error", throwable);
                });
                return null;
            });
    }

    /** Persists session data to SharedPreferences. */
    private void saveUserSession(User user) {
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userId", user.getUserId());
        editor.putString("userName", user.getName());
        editor.putString("userEmail", user.getEmail());
        editor.putString("userType", user.getUserType());
        editor.commit();
        Log.d(TAG, "Session saved to SharedPreferences");
    }

    /** Routes user to Admin or Customer interface based on account type. */
    private void navigateBasedOnUserRole(User user) {
        if (user.isAdmin()) {
            // Admin user - navigate to admin dashboard
            Log.d(TAG, "User is ADMIN - Redirecting to AdminDashboardActivity");
            Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            intent.putExtra("user_id", user.getUserId());
            Log.d(TAG, "Starting AdminDashboardActivity...");
            startActivity(intent);
        } else {
            // Customer user - navigate to user home
            Log.d(TAG, "User is CUSTOMER - Redirecting to UserHomeActivity");
            Intent intent = new Intent(LoginActivity.this, UserHomeActivity.class);
            intent.putExtra("user_id", user.getUserId());
            Log.d(TAG, "Intent created with user_id: " + user.getUserId());
            Log.d(TAG, "Starting UserHomeActivity...");
            startActivity(intent);
            Log.d(TAG, "UserHomeActivity started successfully");
        }
        
        // Finish login activity to prevent back navigation
        Log.d(TAG, "Finishing LoginActivity...");
        finish();
        Log.d(TAG, "=== LOGIN FLOW COMPLETE ===");
    }
}