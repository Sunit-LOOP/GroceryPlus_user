package com.sunit.groceryplus.utils;

public class PHPBackendConfig {
    // PHP Backend Configuration
    public static final String BACKEND_URL_DEV = "http://10.0.2.2/backend-php/";
    public static final String BACKEND_URL_PROD = "https://your-domain.com/backend-php/";
    
    // Toggle between development and production
    public static final boolean USE_PRODUCTION = false;
    
    public static final String BACKEND_URL = USE_PRODUCTION ? BACKEND_URL_PROD : BACKEND_URL_DEV;
    
    // PHP Backend Endpoints
    public static final String CREATE_PAYMENT_INTENT = "stripe-backend-simple.php";
    public static final String PAYMENT_STATUS = "stripe-backend-simple.php";
    public static final String WEBHOOK = "stripe-backend-simple.php";
    public static final String TEST_ENDPOINT = "stripe-backend-simple.php";
    
    // Build full URLs
    public static String getCreatePaymentIntentUrl() {
        return BACKEND_URL + CREATE_PAYMENT_INTENT;
    }
    
    public static String getPaymentStatusUrl() {
        return BACKEND_URL + PAYMENT_STATUS;
    }
    
    public static String getTestUrl() {
        return BACKEND_URL + TEST_ENDPOINT;
    }
}