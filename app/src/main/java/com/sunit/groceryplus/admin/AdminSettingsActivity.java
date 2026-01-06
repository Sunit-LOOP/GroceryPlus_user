package com.sunit.groceryplus.admin;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.sunit.groceryplus.AdminSettingsRepository;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.AdminSettings;


/** AdminSettingsActivity - Configuration panel for system-wide settings including store info, pricing, payments, and notifications. */
public class AdminSettingsActivity extends AppCompatActivity {

    // Data Repository & Model
    private AdminSettingsRepository repository;
    private AdminSettings currentSettings;

    // Store Info UI
    private EditText storeNameEt, storeEmailEt, storePhoneEt, storeAddressEt;
    private EditText storeCityEt, storeStateEt, storePostalCodeEt, storeCountryEt;

    // Pricing UI
    private EditText taxRateEt, deliveryFeeEt, freeDeliveryThresholdEt;
    private Switch freeDeliveryAboveSw;

    // Notifications UI
    private Switch enableNotificationsSw, enableEmailNotificationsSw;

    // Email Settings (Placeholder for future implementation)
    // private EditText smtpHostEt, smtpPortEt, smtpUsernameEt, smtpPasswordEt;

    // Payment Settings UI
    private Switch stripeEnabledSw, codEnabledSw;
    private EditText stripePublishableKeyEt, stripeSecretKeyEt;

    // Support UI
    private EditText businessHoursEt, supportEmailEt, supportPhoneEt;

    // Appearance (Placeholder for future implementation)
    // private EditText primaryColorEt, accentColorEt;

    // Maintenance Mode UI
    private Switch maintenanceModeSw;
    private EditText maintenanceMessageEt;

    // Action Buttons
    private Button saveBtn, resetBtn;

    /** Initializes the activity, setting up repositories, toolbars, and UI components. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_settings);

        repository = new AdminSettingsRepository(this);

        setupToolbar();
        initViews();
        loadSettings();
        setupClickListeners();
    }

    /** Configures the toolbar with navigation features and title. */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Admin Settings");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /** Binds UI components to their respective XML layout IDs. */
    private void initViews() {
        // Store Info
        storeNameEt = findViewById(R.id.storeNameEt);
        storeEmailEt = findViewById(R.id.storeEmailEt);
        storePhoneEt = findViewById(R.id.storePhoneEt);
        storeAddressEt = findViewById(R.id.storeAddressEt);
        storeCityEt = findViewById(R.id.storeCityEt);
        storeStateEt = findViewById(R.id.storeStateEt);
        storePostalCodeEt = findViewById(R.id.storePostalCodeEt);
        storeCountryEt = findViewById(R.id.storeCountryEt);

        // Pricing
        taxRateEt = findViewById(R.id.taxRateEt);
        deliveryFeeEt = findViewById(R.id.deliveryFeeEt);
        freeDeliveryThresholdEt = findViewById(R.id.freeDeliveryThresholdEt);
        freeDeliveryAboveSw = findViewById(R.id.freeDeliveryAboveSw);

        // Notifications
        enableNotificationsSw = findViewById(R.id.enableNotificationsSw);
        enableEmailNotificationsSw = findViewById(R.id.enableEmailNotificationsSw);

        // Email Settings - TODO: Add these fields to layout
        // smtpHostEt = findViewById(R.id.smtpHostEt);
        // smtpPortEt = findViewById(R.id.smtpPortEt);
        // smtpUsernameEt = findViewById(R.id.smtpUsernameEt);
        // smtpPasswordEt = findViewById(R.id.smtpPasswordEt);

        // Payment Settings
        stripeEnabledSw = findViewById(R.id.stripeEnabledSw);
        codEnabledSw = findViewById(R.id.codEnabledSw);
        stripePublishableKeyEt = findViewById(R.id.stripePublishableKeyEt);
        stripeSecretKeyEt = findViewById(R.id.stripeSecretKeyEt);

        // Support
        businessHoursEt = findViewById(R.id.businessHoursEt);
        supportEmailEt = findViewById(R.id.supportEmailEt);
        supportPhoneEt = findViewById(R.id.supportPhoneEt);

        // Appearance - TODO: Add these fields to layout
        // primaryColorEt = findViewById(R.id.primaryColorEt);
        // accentColorEt = findViewById(R.id.accentColorEt);

        // Maintenance
        maintenanceModeSw = findViewById(R.id.maintenanceModeSw);
        maintenanceMessageEt = findViewById(R.id.maintenanceMessageEt);

        // Buttons
        saveBtn = findViewById(R.id.saveBtn);
        resetBtn = findViewById(R.id.resetBtn);
    }

    /** Loads persistened settings from the repository and populates UI fields. */
    private void loadSettings() {
        currentSettings = repository.getSettings();

        // Store Info
        storeNameEt.setText(currentSettings.getStoreName());
        storeEmailEt.setText(currentSettings.getStoreEmail());
        storePhoneEt.setText(currentSettings.getStorePhone());
        storeAddressEt.setText(currentSettings.getStoreAddress());
        storeCityEt.setText(currentSettings.getStoreCity());
        storeStateEt.setText(currentSettings.getStoreState());
        storePostalCodeEt.setText(currentSettings.getStorePostalCode());
        storeCountryEt.setText(currentSettings.getStoreCountry());

        // Pricing
        taxRateEt.setText(String.valueOf(currentSettings.getTaxRate()));
        deliveryFeeEt.setText(String.valueOf(currentSettings.getDeliveryFee()));
        freeDeliveryThresholdEt.setText(String.valueOf(currentSettings.getFreeDeliveryThreshold()));
        freeDeliveryAboveSw.setChecked(currentSettings.isFreeDeliveryAbove());

        // Notifications
        enableNotificationsSw.setChecked(currentSettings.isEnableNotifications());
        enableEmailNotificationsSw.setChecked(currentSettings.isEnableEmailNotifications());

        // Email Settings - TODO: Uncomment when fields are added to layout
        // smtpHostEt.setText(currentSettings.getSmtpHost());
        // smtpPortEt.setText(currentSettings.getSmtpPort());
        // smtpUsernameEt.setText(currentSettings.getSmtpUsername());
        // smtpPasswordEt.setText(currentSettings.getSmtpPassword());

        // Payment Settings
        stripeEnabledSw.setChecked(currentSettings.isStripeEnabled());
        codEnabledSw.setChecked(currentSettings.isCodEnabled());
        stripePublishableKeyEt.setText(currentSettings.getStripePublishableKey());
        stripeSecretKeyEt.setText(currentSettings.getStripeSecretKey());

        // Support
        businessHoursEt.setText(currentSettings.getBusinessHours());
        supportEmailEt.setText(currentSettings.getSupportEmail());
        supportPhoneEt.setText(currentSettings.getSupportPhone());

        // Appearance - TODO: Uncomment when fields are added to layout
        // primaryColorEt.setText(currentSettings.getPrimaryColor());
        // accentColorEt.setText(currentSettings.getAccentColor());

        // Maintenance
        maintenanceModeSw.setChecked(currentSettings.isMaintenanceMode());
        maintenanceMessageEt.setText(currentSettings.getMaintenanceMessage());
    }

    /** Sets up interactive listeners for buttons and toggle switches. */
    private void setupClickListeners() {
        saveBtn.setOnClickListener(v -> saveSettings());
        resetBtn.setOnClickListener(v -> resetToDefaults());

        freeDeliveryAboveSw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            freeDeliveryThresholdEt.setEnabled(isChecked);
        });
    }

    /** Validates user input and persists updated settings to the database. */
    private void saveSettings() {
        // Validate required fields
        if (storeNameEt.getText().toString().trim().isEmpty()) {
            storeNameEt.setError("Required");
            return;
        }

        // Update settings object
        currentSettings.setStoreName(storeNameEt.getText().toString().trim());
        currentSettings.setStoreEmail(storeEmailEt.getText().toString().trim());
        currentSettings.setStorePhone(storePhoneEt.getText().toString().trim());
        currentSettings.setStoreAddress(storeAddressEt.getText().toString().trim());
        currentSettings.setStoreCity(storeCityEt.getText().toString().trim());
        currentSettings.setStoreState(storeStateEt.getText().toString().trim());
        currentSettings.setStorePostalCode(storePostalCodeEt.getText().toString().trim());
        currentSettings.setStoreCountry(storeCountryEt.getText().toString().trim());

        try {
            currentSettings.setTaxRate(Double.parseDouble(taxRateEt.getText().toString()));
            currentSettings.setDeliveryFee(Double.parseDouble(deliveryFeeEt.getText().toString()));
            currentSettings.setFreeDeliveryThreshold(Double.parseDouble(freeDeliveryThresholdEt.getText().toString()));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            return;
        }

        currentSettings.setFreeDeliveryAbove(freeDeliveryAboveSw.isChecked());
        currentSettings.setEnableNotifications(enableNotificationsSw.isChecked());
        currentSettings.setEnableEmailNotifications(enableEmailNotificationsSw.isChecked());

        // Email Settings - TODO: Uncomment when fields are added to layout
        // currentSettings.setSmtpHost(smtpHostEt.getText().toString().trim());
        // currentSettings.setSmtpPort(smtpPortEt.getText().toString().trim());
        // currentSettings.setSmtpUsername(smtpUsernameEt.getText().toString().trim());
        // currentSettings.setSmtpPassword(smtpPasswordEt.getText().toString().trim());

        currentSettings.setStripeEnabled(stripeEnabledSw.isChecked());
        currentSettings.setCodEnabled(codEnabledSw.isChecked());
        currentSettings.setStripePublishableKey(stripePublishableKeyEt.getText().toString().trim());
        currentSettings.setStripeSecretKey(stripeSecretKeyEt.getText().toString().trim());

        currentSettings.setBusinessHours(businessHoursEt.getText().toString().trim());
        currentSettings.setSupportEmail(supportEmailEt.getText().toString().trim());
        currentSettings.setSupportPhone(supportPhoneEt.getText().toString().trim());

        // Appearance - TODO: Uncomment when fields are added to layout
        // currentSettings.setPrimaryColor(primaryColorEt.getText().toString().trim());
        // currentSettings.setAccentColor(accentColorEt.getText().toString().trim());

        currentSettings.setMaintenanceMode(maintenanceModeSw.isChecked());
        currentSettings.setMaintenanceMessage(maintenanceMessageEt.getText().toString().trim());

        // Save to database
        if (repository.saveSettings(currentSettings)) {
            Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save settings", Toast.LENGTH_SHORT).show();
        }
    }

    /** Confirms and executes a reset of all settings to their default values. */
    private void resetToDefaults() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Reset to Defaults")
                .setMessage("Are you sure you want to reset all settings to default values?")
                .setPositiveButton("Reset", (dialog, which) -> {
                    if (repository.resetToDefaults()) {
                        loadSettings();
                        Toast.makeText(this, "Settings reset to defaults", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to reset settings", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /** Handles toolbar back navigation and menu selection. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
