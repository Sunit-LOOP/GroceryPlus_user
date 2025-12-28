package com.sunit.groceryplus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class UserSettingViewActivity extends AppCompatActivity {

    private SwitchMaterial notificationSwitch;
    private SwitchMaterial darkModeSwitch;
    private MaterialCardView adminLoginCard;
    private MaterialCardView logoutCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting_view);
        
        // Initialize views
        initViews();
        
        // Load preferences
        loadPreferences();

        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        notificationSwitch = findViewById(R.id.notificationSwitch);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        adminLoginCard = findViewById(R.id.adminLoginCard);
        logoutCard = findViewById(R.id.logoutCard);
    }

    private void loadPreferences() {
        android.content.SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean notificationsEnabled = prefs.getBoolean("notifications_enabled", true);
        boolean darkModeEnabled = prefs.getBoolean("dark_mode_enabled", false);
        
        notificationSwitch.setChecked(notificationsEnabled);
        darkModeSwitch.setChecked(darkModeEnabled);
    }

    private void setClickListeners() {

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            android.content.SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
            prefs.edit().putBoolean("notifications_enabled", isChecked).apply();
            
            if (isChecked) {
                Toast.makeText(UserSettingViewActivity.this, "Notifications enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UserSettingViewActivity.this, "Notifications disabled", Toast.LENGTH_SHORT).show();
            }
        });

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            android.content.SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
            prefs.edit().putBoolean("dark_mode_enabled", isChecked).apply();
            
            if (isChecked) {
                Toast.makeText(UserSettingViewActivity.this, "Dark mode enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UserSettingViewActivity.this, "Light mode enabled", Toast.LENGTH_SHORT).show();
            }
        });

        adminLoginCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to admin login activity
                Intent intent = new Intent(UserSettingViewActivity.this, AdminLoginActivity.class);
                startActivity(intent);
            }
        });

        logoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logout and navigate to login screen
                Toast.makeText(UserSettingViewActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserSettingViewActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}