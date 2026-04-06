package com.sunit.groceryplus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.sunit.groceryplus.models.User;


/** UserProfileActivity - User account dashboard providing navigation to settings, addresses, and history. */
public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";

    /** Initializes the profile dashboard and navigation components. */
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

        // Wallet Management
        findViewById(R.id.myWalletButton).setOnClickListener(v -> {
            Intent walletIntent = new Intent(this, UserWalletActivity.class);
            walletIntent.putExtra("user_id", userId);
            startActivity(walletIntent);
        });
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