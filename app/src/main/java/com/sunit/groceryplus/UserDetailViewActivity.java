package com.sunit.groceryplus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.sunit.groceryplus.models.User;

import com.google.android.material.button.MaterialButton;


/** Activity for displaying comprehensive user account details and providing quick access to profile actions. */
public class UserDetailViewActivity extends AppCompatActivity {
    // Infrastructure
    private static final String TAG = "UserDetailViewActivity";
    private UserRepository userRepository;
    private int userId;

    // UI Components
    private ImageButton backButton;
    private ImageView settingsIcon;
    private TextView fullNameValue;
    private TextView emailValue;
    private TextView phoneValue;
    private TextView userTypeValue;
    private MaterialButton editProfileButton;
    private MaterialButton changePasswordButton;
    private MaterialButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail_view);

        // Get user ID from intent
        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Log.e(TAG, "Invalid user ID received");
            Toast.makeText(this, "Error loading user data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize repository
        userRepository = new UserRepository(this);

        // Initialize views
        initViews();

        // Load user data
        loadUserData();

        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        settingsIcon = findViewById(R.id.settingsIcon);
        fullNameValue = findViewById(R.id.fullNameValue);
        emailValue = findViewById(R.id.emailValue);
        phoneValue = findViewById(R.id.phoneValue);
        userTypeValue = findViewById(R.id.userTypeValue);
        editProfileButton = findViewById(R.id.editProfileButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        logoutButton = findViewById(R.id.logoutButton);
        
        // Setup Notification Click
        findViewById(R.id.actionNotification).setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserData() {
        try {
            User user = userRepository.getUserById(userId);
            if (user != null) {
                // Update UI with user data
                fullNameValue.setText(user.getName());
                emailValue.setText(user.getEmail());
                phoneValue.setText(user.getPhone());
                userTypeValue.setText(user.getUserType().substring(0, 1).toUpperCase() + user.getUserType().substring(1));
            } else {
                Log.e(TAG, "User not found with ID: " + userId);
                Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading user data", e);
            Toast.makeText(this, "Error loading user data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setClickListeners() {
        // New Action Icons
        findViewById(R.id.actionMap).setOnClickListener(v -> {
            OrderRepository orderRepo = new OrderRepository(this);
            com.sunit.groceryplus.models.Order lastOrder = orderRepo.getLastOrder(userId);
            if (lastOrder != null) {
                Intent intent = new Intent(this, OrderTrackingActivity.class);
                intent.putExtra("order_id", lastOrder.getOrderId());
                intent.putExtra("order_status", lastOrder.getStatus());
                startActivity(intent);
            } else {
                Toast.makeText(this, "No recent orders to track", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.actionCart).setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        findViewById(R.id.actionHistory).setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderHistoryActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the activity and go back
                finish();
            }
        });

        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to settings activity
                Intent intent = new Intent(UserDetailViewActivity.this, UserSettingViewActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
            }
        });



        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to edit profile activity
                Intent intent = new Intent(UserDetailViewActivity.this, EditProfileActivity.class);
                intent.putExtra("user_id", userId);
                startActivityForResult(intent, 100);
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logout and navigate to login screen
                Toast.makeText(UserDetailViewActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserDetailViewActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
    
    /**
     * Shows a dialog for changing user password
     */
    private void showChangePasswordDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Change Password");
        
        // Create dialog layout
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);
        
        com.google.android.material.textfield.TextInputEditText currentPasswordEt = dialogView.findViewById(R.id.currentPasswordEt);
        com.google.android.material.textfield.TextInputEditText newPasswordEt = dialogView.findViewById(R.id.newPasswordEt);
        com.google.android.material.textfield.TextInputEditText confirmPasswordEt = dialogView.findViewById(R.id.confirmPasswordEt);
        MaterialButton changeBtn = dialogView.findViewById(R.id.changeBtn);
        MaterialButton cancelBtn = dialogView.findViewById(R.id.cancelBtn);
        
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        
        changeBtn.setOnClickListener(v -> {
            String currentPassword = currentPasswordEt.getText().toString().trim();
            String newPassword = newPasswordEt.getText().toString().trim();
            String confirmPassword = confirmPasswordEt.getText().toString().trim();
            
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (newPassword.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Update password in database
            try {
                User user = userRepository.getUserById(userId);
                if (user != null && user.getPassword().equals(currentPassword)) {
                    user.setPassword(newPassword);
                    boolean success = userRepository.updateUser(user);
                    if (success) {
                        Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, "Failed to change password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error changing password", e);
                Toast.makeText(this, "Error changing password", Toast.LENGTH_SHORT).show();
            }
        });
        
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
}