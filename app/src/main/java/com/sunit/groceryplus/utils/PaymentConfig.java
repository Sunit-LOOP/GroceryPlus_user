package com.sunit.groceryplus.utils;

public class PaymentConfig {
    // Stripe Configuration
    public static final String STRIPE_PUBLISHABLE_KEY = "pk_test_51SgF3yPO7nc2IchGlfjWqbL7xMwkQiUckMME4qxQke5xUNsDjnHRpGOsFB9fKXOJiFkLUc2eKjDmqBvXOgOfqWGq00w6HSkYFq";
    
    // Backend Configuration
    // For development: Android Emulator localhost (PHP backend)
    public static final String BACKEND_URL_DEV = "http://10.0.2.2:80/"; // PHP server on port 80
    // Alternative for PHP with custom port:
    // public static final String BACKEND_URL_DEV = "http://10.0.2.2:8000/";
    
    // For production: Replace with your actual PHP backend URL
    public static final String BACKEND_URL_PROD = "https://your-domain.com/backend-php/";
    
    // Toggle between development and production
    public static final boolean USE_PRODUCTION = false;
    
    public static final String BACKEND_URL = USE_PRODUCTION ? BACKEND_URL_PROD : BACKEND_URL_DEV;
    
    // Payment Configuration
    public static final String CURRENCY = "npr";
    public static final int MIN_AMOUNT = 50; // Minimum amount in paisa (50 paisa = NPR 0.50)
    
    // Delivery Configuration
    public static final double DELIVERY_FEE = 100.0; // Delivery fee in NPR
    
    // Timeout Configuration
    public static final int PAYMENT_TIMEOUT_SECONDS = 300; // 5 minutes
    public static final int NETWORK_TIMEOUT_SECONDS = 30; // 30 seconds
}
