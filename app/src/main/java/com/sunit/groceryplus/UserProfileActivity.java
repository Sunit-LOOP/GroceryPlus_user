package com.sunit.groceryplus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.sunit.groceryplus.models.User;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Get user ID from intent
        int userId = getIntent().getIntExtra("user_id", -1);
        
        // Setup Bottom Navigation
        com.sunit.groceryplus.utils.NavigationHelper.setupNavigation(this, userId);

        // Setup Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.userProfileToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("User Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Address Management
        findViewById(R.id.manageAddressesButton).setOnClickListener(v -> {
            Intent addressIntent = new Intent(this, AddressManagementActivity.class);
            addressIntent.putExtra("user_id", userId);
            startActivity(addressIntent);
        });
    }
}