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

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private Button loginButton;
    private TextView signupTextView;
    private UserRepository userRepository;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // Initialize views
        initViews();

        // Initialize repositories
        userRepository = new UserRepository(this);
        apiService = new ApiService(this);

        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupTextView = findViewById(R.id.signupTextView);
    }

    private void setClickListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void performLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        // Disable button during login
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        // Try API login first
        Log.d(TAG, "Attempting API login for email: " + email);
        apiService.login(email, password, new ApiService.ApiCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d(TAG, "=== API LOGIN SUCCESSFUL ===");
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                // Navigate to user home (API users are customers)
                Intent intent = new Intent(LoginActivity.this, UserHomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "API login failed: " + error + " - Trying database login");

                // Fallback to database login (for admin users)
                User user = userRepository.loginUser(email, password);

                if (user != null) {
                    Log.d(TAG, "=== DATABASE LOGIN SUCCESSFUL ===");
                    Log.d(TAG, "User ID: " + user.getUserId());
                    Log.d(TAG, "User Name: " + user.getName());
                    Log.d(TAG, "User Email: " + user.getEmail());
                    Log.d(TAG, "User Type: " + user.getUserType());
                    Log.d(TAG, "Is Admin: " + user.isAdmin());

                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                    // Save session
                    android.content.SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("userId", user.getUserId());
                    editor.putString("userName", user.getName());
                    editor.putString("userEmail", user.getEmail());
                    editor.putString("userType", user.getUserType());
                    editor.commit();
                    Log.d(TAG, "Session saved to SharedPreferences");

                    // Navigate to appropriate screen based on user type
                    if (user.isAdmin()) {
                        Log.d(TAG, "User is ADMIN - Redirecting to AdminDashboardActivity");
                        Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                        intent.putExtra("user_id", user.getUserId());
                        Log.d(TAG, "Starting AdminDashboardActivity...");
                        startActivity(intent);
                    } else {
                        Log.d(TAG, "User is CUSTOMER - Redirecting to UserHomeActivity");
                        Intent intent = new Intent(LoginActivity.this, UserHomeActivity.class);
                        intent.putExtra("user_id", user.getUserId());
                        Log.d(TAG, "Intent created with user_id: " + user.getUserId());
                        Log.d(TAG, "Starting UserHomeActivity...");
                        startActivity(intent);
                        Log.d(TAG, "UserHomeActivity started successfully");
                    }
                    Log.d(TAG, "Finishing LoginActivity...");
                    finish();
                    Log.d(TAG, "=== LOGIN FLOW COMPLETE ===");
                } else {
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Both API and database login failed for email: " + email);
                }
            }
        });
    }
}