package com.sunit.groceryplus.utils;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.R;

import java.util.regex.Pattern;

/** Utility class providing centralized validation logic and common UI helper functions for user input. */
public class ValidationUtils {

    // Infrastructure
    private static final String TAG = "ValidationUtils";
    
    // Patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    
    /** Returns true if the provided email string matches the internal email regex pattern. */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /** Returns true if the provided string (after digit extraction) matches a 10-digit Indian phone pattern. */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.replaceAll("[^0-9]", "")).matches();
    }
    
    /** Returns true if the password is non-null and at least 6 characters in length. */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    /** Returns true if the name contains only alphabets and spaces and is at least 2 characters long. */
    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2 && name.matches("[a-zA-Z\\s]+");
    }
    
    /** Returns true if the string can be parsed as a positive double value. */
    public static boolean isValidPrice(String priceStr) {
        try {
            double price = Double.parseDouble(priceStr);
            return price > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /** Returns true if the string can be parsed as a positive integer value. */
    public static boolean isValidQuantity(String quantityStr) {
        try {
            int quantity = Integer.parseInt(quantityStr);
            return quantity > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /** Validates whether requested product quantity is available in the database. */
    public static boolean validateStock(Context context, int productId, int requestedQuantity) {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            return dbHelper.validateStock(productId, requestedQuantity);
        } catch (Exception e) {
            Log.e(TAG, "Error validating stock", e);
            return false;
        }
    }
    
    /** Displays an error message on the specified EditText and focuses the view. */
    public static void showValidationError(EditText editText, String errorMessage) {
        editText.setError(errorMessage);
        editText.requestFocus();
    }
    
    /** Removes any existing error message from the specified EditText. */
    public static void clearValidationError(EditText editText) {
        editText.setError(null);
    }
    
    /** Validates email and password fields for the login form, showing UI errors if necessary. */
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
    
    /** Validates all registration form fields including name, email, phone, and password confirmation. */
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
    
    /** Validates product form inputs including name, price, stock, and description. */
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
    
    /** Validates address form inputs including street address, city, state, and postal code. */
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
    
    /** Displays a short-duration Toast message with the provided text. */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    
    /** Displays a short-duration success Toast message prefixed with a checkmark. */
    public static void showSuccessToast(Context context, String message) {
        Toast.makeText(context, "✓ " + message, Toast.LENGTH_SHORT).show();
    }
    
    /** Displays a short-duration error Toast message prefixed with a cross mark. */
    public static void showErrorToast(Context context, String message) {
        Toast.makeText(context, "✗ " + message, Toast.LENGTH_SHORT).show();
    }
    
    /** Returns true if the string is null or consists only of whitespace. */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /** Returns the provided string or a default value if the original is null. */
    public static String safeString(String str, String defaultValue) {
        return str == null ? defaultValue : str;
    }
    
    /** Returns the provided string or an empty string if the original is null. */
    public static String safeString(String str) {
        return safeString(str, "");
    }
    
    /** Capitalizes the first letter of the provided string and converts the rest to lowercase. */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    /** Formats a numeric amount into a localized currency string (e.g., "Rs. 100.00"). */
    public static String formatCurrency(double amount) {
        return String.format("Rs. %.2f", amount);
    }
    
    /** Formats a numeric amount into a currency string with a custom specified symbol. */
    public static String formatCurrency(double amount, String symbol) {
        return String.format("%s %.2f", symbol, amount);
    }
    
    /** Returns true if the search query is non-null and at least 2 characters in length. */
    public static boolean isValidSearchQuery(String query) {
        return query != null && query.trim().length() >= 2;
    }
    
    /** Removes all non-alphanumeric characters and extra spaces from the provided search query. */
    public static String sanitizeSearchQuery(String query) {
        if (query == null) {
            return "";
        }
        return query.trim().replaceAll("[^a-zA-Z0-9\\s]", "");
    }
    
    /** Returns true if the provided user type string (case-insensitive) equals "admin". */
    public static boolean isAdmin(String userType) {
        return "admin".equalsIgnoreCase(userType);
    }
    
    /** Returns true if the provided user type string (case-insensitive) equals "customer". */
    public static boolean isCustomer(String userType) {
        return "customer".equalsIgnoreCase(userType);
    }
    
    /** Generates a simple ID based on the current system time in milliseconds. */
    public static String generateRandomId() {
        return String.valueOf(System.currentTimeMillis());
    }
    
    /** Returns true if the string contains only numeric digits. */
    public static boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }
    
    /** Returns true if the string contains only alphabetic characters. */
    public static boolean isAlpha(String str) {
        return str != null && str.matches("[a-zA-Z]+");
    }
    
    /** Returns true if the string contains only alphanumeric characters. */
    public static boolean isAlphaNumeric(String str) {
        return str != null && str.matches("[a-zA-Z0-9]+");
    }
}
