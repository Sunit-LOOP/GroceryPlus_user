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
import com.sunit.groceryplus.utils.ValidationUtils;


/**
 * AdminLoginActivity - Secure login interface for Administrators.
 * 
 * This activity handles the authentication of admin users. It differs from the standard
 * login by explicitly checking the user role (isAdmin flag) after successful credential validation.
 * It prevents non-admin users from accessing the dashboard.
 * 
 * Key Features:
 * - Email/Password Authentication
 * - Role Verification (Admin Check)
 * - Session Management (SharedPreferences)
 * - Navigation to User Login (for non-admins)
 * 
 * @author GroceryPlus Development Team
 * @version 1.0
 * @since 1.0
 */
public class AdminLoginActivity extends AppCompatActivity {

    private static final String TAG = "AdminLoginActivity";
    private TextInputEditText adminEmailEditText;
    private TextInputEditText adminPasswordEditText;
    private Button adminLoginButton;
    private TextView userLoginTextView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        // Initialize views
        initViews();

        // Initialize repository
        databaseHelper = new DatabaseHelper(this);

        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        adminEmailEditText = findViewById(R.id.adminEmailEditText);
        adminPasswordEditText = findViewById(R.id.adminPasswordEditText);
        adminLoginButton = findViewById(R.id.adminLoginButton);
        userLoginTextView = findViewById(R.id.userLoginTextView);
    }

    private void setClickListeners() {
        adminLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAdminLogin();
            }
        });

        userLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminLoginActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void performAdminLogin() {
        String email = adminEmailEditText.getText().toString().trim();
        String password = adminPasswordEditText.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            adminEmailEditText.setError("Email is required");
            adminEmailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            adminPasswordEditText.setError("Password is required");
            adminPasswordEditText.requestFocus();
            return;
        }

        // Perform login
        User user = databaseHelper.authenticateUser(email, password);
        if (user != null && user.isAdmin()) {
            Toast.makeText(this, "Admin login successful!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Admin logged in: " + user.getName());

            // Save session
            android.content.SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("userId", user.getUserId());
            editor.putString("userName", user.getName());
            editor.putString("userEmail", user.getEmail());
            editor.putString("userType", user.getUserType());
            editor.commit();
            Log.d(TAG, "Admin session saved to SharedPreferences");

            // Navigate to admin dashboard
            Intent intent = new Intent(AdminLoginActivity.this, AdminDashboardActivity.class);
            intent.putExtra("user_id", user.getUserId());
            startActivity(intent);
            finish();
        } else if (user != null && !user.isAdmin()) {
            Toast.makeText(this, "This account is not an admin account", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Non-admin user tried to access admin panel: " + user.getEmail());
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Admin login failed for email: " + email);
        }
    }
}