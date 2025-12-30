package com.sunit.groceryplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sunit.groceryplus.models.AdminSettings;

public class AdminSettingsRepository {
    private DatabaseHelper dbHelper;

    public AdminSettingsRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Get admin settings (singleton pattern - only one row)
    public AdminSettings getSettings() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.AdminSettingsEntry.TABLE_NAME, null,
                null, null, null, null, null);

        AdminSettings settings = new AdminSettings();
        if (cursor != null && cursor.moveToFirst()) {
            settings = cursorToSettings(cursor);
            cursor.close();
        } else {
            // Set default values if no settings exist
            setDefaults(settings);
        }
        return settings;
    }

    // Update or insert settings
    public boolean saveSettings(AdminSettings settings) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = settingsToContentValues(settings);

        // Check if settings already exist
        Cursor cursor = db.query(DatabaseContract.AdminSettingsEntry.TABLE_NAME, null,
                null, null, null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();

        long result;
        if (exists) {
            // Update existing record
            result = db.update(DatabaseContract.AdminSettingsEntry.TABLE_NAME, values,
                    null, null);
        } else {
            // Insert new record
            result = db.insert(DatabaseContract.AdminSettingsEntry.TABLE_NAME, null, values);
        }
        return result != -1;
    }

    // Reset to defaults
    public boolean resetToDefaults() {
        AdminSettings defaults = new AdminSettings();
        setDefaults(defaults);
        return saveSettings(defaults);
    }

    // Helper methods
    private AdminSettings cursorToSettings(Cursor cursor) {
        AdminSettings settings = new AdminSettings();
        settings.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_SETTINGS_ID)));
        settings.setStoreName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STORE_NAME)));
        settings.setStoreEmail(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STORE_EMAIL)));
        settings.setStorePhone(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STORE_PHONE)));
        settings.setStoreAddress(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STORE_ADDRESS)));
        settings.setStoreCity(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STORE_CITY)));
        settings.setStoreState(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STORE_STATE)));
        settings.setStorePostalCode(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STORE_POSTAL_CODE)));
        settings.setStoreCountry(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STORE_COUNTRY)));
        settings.setTaxRate(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_TAX_RATE)));
        settings.setDeliveryFee(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_DELIVERY_FEE)));
        settings.setFreeDeliveryAbove(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_FREE_DELIVERY_ABOVE)) == 1);
        settings.setFreeDeliveryThreshold(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_FREE_DELIVERY_THRESHOLD)));
        settings.setCurrencySymbol(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_CURRENCY_SYMBOL)));
        settings.setTimezone(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_TIMEZONE)));
        settings.setEnableNotifications(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_ENABLE_NOTIFICATIONS)) == 1);
        settings.setEnableEmailNotifications(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_ENABLE_EMAIL_NOTIFICATIONS)) == 1);
        settings.setSmtpHost(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_SMTP_HOST)));
        settings.setSmtpPort(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_SMTP_PORT)));
        settings.setSmtpUsername(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_SMTP_USERNAME)));
        settings.setSmtpPassword(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_SMTP_PASSWORD)));
        settings.setStripeEnabled(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STRIPE_ENABLED)) == 1);
        settings.setStripePublishableKey(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STRIPE_PUBLISHABLE_KEY)));
        settings.setStripeSecretKey(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STRIPE_SECRET_KEY)));
        settings.setCodEnabled(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_COD_ENABLED)) == 1);
        settings.setBusinessHours(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_BUSINESS_HOURS)));
        settings.setSupportEmail(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_SUPPORT_EMAIL)));
        settings.setSupportPhone(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_SUPPORT_PHONE)));
        settings.setLogoUrl(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_LOGO_URL)));
        settings.setFaviconUrl(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_FAVICON_URL)));
        settings.setPrimaryColor(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_PRIMARY_COLOR)));
        settings.setAccentColor(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_ACCENT_COLOR)));
        settings.setMaintenanceMode(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_MAINTENANCE_MODE)) == 1);
        settings.setMaintenanceMessage(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_MAINTENANCE_MESSAGE)));
        settings.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_CREATED_AT)));
        settings.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_UPDATED_AT)));
        return settings;
    }

    private ContentValues settingsToContentValues(AdminSettings settings) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STORE_NAME, settings.getStoreName());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STORE_EMAIL, settings.getStoreEmail());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STORE_PHONE, settings.getStorePhone());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STORE_ADDRESS, settings.getStoreAddress());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STORE_CITY, settings.getStoreCity());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STORE_STATE, settings.getStoreState());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STORE_POSTAL_CODE, settings.getStorePostalCode());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STORE_COUNTRY, settings.getStoreCountry());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_TAX_RATE, settings.getTaxRate());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_DELIVERY_FEE, settings.getDeliveryFee());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_FREE_DELIVERY_ABOVE, settings.isFreeDeliveryAbove() ? 1 : 0);
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_FREE_DELIVERY_THRESHOLD, settings.getFreeDeliveryThreshold());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_CURRENCY_SYMBOL, settings.getCurrencySymbol());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_TIMEZONE, settings.getTimezone());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_ENABLE_NOTIFICATIONS, settings.isEnableNotifications() ? 1 : 0);
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_ENABLE_EMAIL_NOTIFICATIONS, settings.isEnableEmailNotifications() ? 1 : 0);
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_SMTP_HOST, settings.getSmtpHost());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_SMTP_PORT, settings.getSmtpPort());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_SMTP_USERNAME, settings.getSmtpUsername());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_SMTP_PASSWORD, settings.getSmtpPassword());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STRIPE_ENABLED, settings.isStripeEnabled() ? 1 : 0);
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STRIPE_PUBLISHABLE_KEY, settings.getStripePublishableKey());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_STRIPE_SECRET_KEY, settings.getStripeSecretKey());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_COD_ENABLED, settings.isCodEnabled() ? 1 : 0);
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_BUSINESS_HOURS, settings.getBusinessHours());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_SUPPORT_EMAIL, settings.getSupportEmail());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_SUPPORT_PHONE, settings.getSupportPhone());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_LOGO_URL, settings.getLogoUrl());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_FAVICON_URL, settings.getFaviconUrl());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_PRIMARY_COLOR, settings.getPrimaryColor());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_ACCENT_COLOR, settings.getAccentColor());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_MAINTENANCE_MODE, settings.isMaintenanceMode() ? 1 : 0);
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_MAINTENANCE_MESSAGE, settings.getMaintenanceMessage());
        values.put(DatabaseContract.AdminSettingsEntry.COLUMN_NAME_UPDATED_AT, "CURRENT_TIMESTAMP");
        return values;
    }

    private void setDefaults(AdminSettings settings) {
        settings.setStoreName("GroceryPlus");
        settings.setStoreEmail("admin@groceryplus.com");
        settings.setStorePhone("+1234567890");
        settings.setStoreAddress("123 Main Street");
        settings.setStoreCity("New York");
        settings.setStoreState("NY");
        settings.setStorePostalCode("10001");
        settings.setStoreCountry("USA");
        settings.setTaxRate(0.08);
        settings.setDeliveryFee(2.99);
        settings.setFreeDeliveryAbove(true);
        settings.setFreeDeliveryThreshold(50.0);
        settings.setCurrencySymbol("₹");
        settings.setTimezone("UTC");
        settings.setEnableNotifications(true);
        settings.setEnableEmailNotifications(true);
        settings.setStripeEnabled(false);
        settings.setCodEnabled(true);
        settings.setBusinessHours("Mon-Sat: 9AM-8PM, Sun: 10AM-6PM");
        settings.setSupportEmail("support@groceryplus.com");
        settings.setSupportPhone("+1234567890");
        settings.setPrimaryColor("#4CAF50");
        settings.setAccentColor("#FF9800");
        settings.setMaintenanceMode(false);
        settings.setMaintenanceMessage("We are currently under maintenance. Please check back later.");
    }
}
