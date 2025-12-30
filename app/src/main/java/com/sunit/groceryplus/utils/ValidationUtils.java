package com.sunit.groceryplus.utils;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.R;

import java.util.regex.Pattern;

/**
 * Utility class for validation and common helper functions
 */
public class ValidationUtils {
    private static final String TAG = "ValidationUtils";
    
    // Email pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    );
    
    // Phone pattern (10 digits)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[6-9]\\d{9}$"
    );
    
    /**
     * Validate email address
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate phone number (Indian format)
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.replaceAll("[^0-9]", "")).matches();
    }
    
    /**
     * Validate password (minimum 6 characters)
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    /**
     * Validate name (alphabets and spaces only)
     */
    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2 && name.matches("[a-zA-Z\\s]+");
    }
    
    /**
     * Validate price (positive number)
     */
    public static boolean isValidPrice(String priceStr) {
        try {
            double price = Double.parseDouble(priceStr);
            return price > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate quantity (positive integer)
     */
    public static boolean isValidQuantity(String quantityStr) {
        try {
            int quantity = Integer.parseInt(quantityStr);
            return quantity > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate product stock
     */
    public static boolean validateStock(Context context, int productId, int requestedQuantity) {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            return dbHelper.validateStock(productId, requestedQuantity);
        } catch (Exception e) {
            Log.e(TAG, "Error validating stock", e);
            return false;
        }
    }
    
    /**
     * Show validation error on EditText
     */
    public static void showValidationError(EditText editText, String errorMessage) {
        editText.setError(errorMessage);
        editText.requestFocus();
    }
    
    /**
     * Clear validation error
     */
    public static void clearValidationError(EditText editText) {
        editText.setError(null);
    }
    
    /**
     * Validate login form
     */
    public static boolean validateLoginForm(Context context, EditText emailEt, EditText passwordEt) {
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();
        
        if (email.isEmpty()) {
            showValidationError(emailEt, context.getString(R.string.error_email_required));
            return false;
        }
        
        if (!isValidEmail(email)) {
            showValidationError(emailEt, context.getString(R.string.error_invalid_email));
            return false;
        }
        
        if (password.isEmpty()) {
            showValidationError(passwordEt, context.getString(R.string.error_password_required));
            return false;
        }
        
        if (!isValidPassword(password)) {
            showValidationError(passwordEt, context.getString(R.string.error_password_length));
            return false;
        }
        
        return true;
    }
    
    /**
     * Validate signup form
     */
    public static boolean validateSignupForm(Context context, EditText nameEt, EditText emailEt, 
                                            EditText phoneEt, EditText passwordEt, EditText confirmPasswordEt) {
        String name = nameEt.getText().toString().trim();
        String email = emailEt.getText().toString().trim();
        String phone = phoneEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();
        String confirmPassword = confirmPasswordEt.getText().toString().trim();
        
        if (name.isEmpty()) {
            showValidationError(nameEt, context.getString(R.string.error_name_required));
            return false;
        }
        
        if (!isValidName(name)) {
            showValidationError(nameEt, context.getString(R.string.error_invalid_name));
            return false;
        }
        
        if (email.isEmpty()) {
            showValidationError(emailEt, context.getString(R.string.error_email_required));
            return false;
        }
        
        if (!isValidEmail(email)) {
            showValidationError(emailEt, context.getString(R.string.error_invalid_email));
            return false;
        }
        
        if (phone.isEmpty()) {
            showValidationError(phoneEt, context.getString(R.string.error_phone_required));
            return false;
        }
        
        if (!isValidPhone(phone)) {
            showValidationError(phoneEt, context.getString(R.string.error_invalid_phone));
            return false;
        }
        
        if (password.isEmpty()) {
            showValidationError(passwordEt, context.getString(R.string.error_password_required));
            return false;
        }
        
        if (!isValidPassword(password)) {
            showValidationError(passwordEt, context.getString(R.string.error_password_length));
            return false;
        }
        
        if (confirmPassword.isEmpty()) {
            showValidationError(confirmPasswordEt, context.getString(R.string.error_confirm_password_required));
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            showValidationError(confirmPasswordEt, context.getString(R.string.error_password_mismatch));
            return false;
        }
        
        return true;
    }
    
    /**
     * Validate product form
     */
    public static boolean validateProductForm(Context context, EditText nameEt, EditText priceEt, 
                                              EditText stockEt, EditText descriptionEt) {
        String name = nameEt.getText().toString().trim();
        String priceStr = priceEt.getText().toString().trim();
        String stockStr = stockEt.getText().toString().trim();
        String description = descriptionEt.getText().toString().trim();
        
        if (name.isEmpty()) {
            showValidationError(nameEt, context.getString(R.string.error_product_name_required));
            return false;
        }
        
        if (priceStr.isEmpty()) {
            showValidationError(priceEt, context.getString(R.string.error_price_required));
            return false;
        }
        
        if (!isValidPrice(priceStr)) {
            showValidationError(priceEt, context.getString(R.string.error_invalid_price));
            return false;
        }
        
        if (stockStr.isEmpty()) {
            showValidationError(stockEt, context.getString(R.string.error_stock_required));
            return false;
        }
        
        if (!isValidQuantity(stockStr)) {
            showValidationError(stockEt, context.getString(R.string.error_invalid_stock));
            return false;
        }
        
        if (description.isEmpty()) {
            showValidationError(descriptionEt, context.getString(R.string.error_description_required));
            return false;
        }
        
        return true;
    }
    
    /**
     * Validate address form
     */
    public static boolean validateAddressForm(Context context, EditText addressEt, EditText cityEt, 
                                             EditText stateEt, EditText postalCodeEt) {
        String address = addressEt.getText().toString().trim();
        String city = cityEt.getText().toString().trim();
        String state = stateEt.getText().toString().trim();
        String postalCode = postalCodeEt.getText().toString().trim();
        
        if (address.isEmpty()) {
            showValidationError(addressEt, context.getString(R.string.error_address_required));
            return false;
        }
        
        if (city.isEmpty()) {
            showValidationError(cityEt, context.getString(R.string.error_city_required));
            return false;
        }
        
        if (state.isEmpty()) {
            showValidationError(stateEt, context.getString(R.string.error_state_required));
            return false;
        }
        
        if (postalCode.isEmpty()) {
            showValidationError(postalCodeEt, context.getString(R.string.error_postal_code_required));
            return false;
        }
        
        return true;
    }
    
    /**
     * Show toast message
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Show success toast
     */
    public static void showSuccessToast(Context context, String message) {
        Toast.makeText(context, "✓ " + message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Show error toast
     */
    public static void showErrorToast(Context context, String message) {
        Toast.makeText(context, "✗ " + message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Check if string is empty or null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Get safe string (null-safe)
     */
    public static String safeString(String str, String defaultValue) {
        return str == null ? defaultValue : str;
    }
    
    /**
     * Get safe string (null-safe, empty default)
     */
    public static String safeString(String str) {
        return safeString(str, "");
    }
    
    /**
     * Capitalize first letter
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    /**
     * Format currency
     */
    public static String formatCurrency(double amount) {
        return String.format("रु %.2f", amount);
    }
    
    /**
     * Format currency with symbol
     */
    public static String formatCurrency(double amount, String symbol) {
        return String.format("%s %.2f", symbol, amount);
    }
    
    /**
     * Validate search query
     */
    public static boolean isValidSearchQuery(String query) {
        return query != null && query.trim().length() >= 2;
    }
    
    /**
     * Sanitize search query
     */
    public static String sanitizeSearchQuery(String query) {
        if (query == null) {
            return "";
        }
        return query.trim().replaceAll("[^a-zA-Z0-9\\s]", "");
    }
    
    /**
     * Check if user is admin
     */
    public static boolean isAdmin(String userType) {
        return "admin".equalsIgnoreCase(userType);
    }
    
    /**
     * Check if user is customer
     */
    public static boolean isCustomer(String userType) {
        return "customer".equalsIgnoreCase(userType);
    }
    
    /**
     * Generate random ID
     */
    public static String generateRandomId() {
        return String.valueOf(System.currentTimeMillis());
    }
    
    /**
     * Check if string contains only digits
     */
    public static boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }
    
    /**
     * Check if string contains only alphabets
     */
    public static boolean isAlpha(String str) {
        return str != null && str.matches("[a-zA-Z]+");
    }
    
    /**
     * Check if string contains alphabets and numbers
     */
    public static boolean isAlphaNumeric(String str) {
        return str != null && str.matches("[a-zA-Z0-9]+");
    }
}
