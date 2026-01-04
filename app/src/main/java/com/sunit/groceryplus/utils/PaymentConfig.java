package com.sunit.groceryplus.utils;

/**
 * PaymentConfig - Configuration constants for payment and backend integration.
 * 
 * This class holds all configuration values related to payment processing,
 * backend API endpoints, and fees. It allows for easy switching between
 * development and production environments.
 */
public class PaymentConfig {
    
    // Stripe Configuration
    public static final String STRIPE_PUBLISHABLE_KEY = "pk_test_51SdBfzD0CBYYErpfvgC0r6IPOkh5Ey6g5ju5IOmoGR4nUISi5p8TXgfX75wjLuV8kXW7xoZWq3tBk9Z9svkMqIym0005By6JFh";
    // WARNING: Storing secret key in app is insecure. Use a proper backend in production.
    public static final String STRIPE_SECRET_KEY = "sk_test_51SdBfzD0CBYYErpfg5pIH7DiWP8rBpMVdxMpZm0YexTCbMtA1WQismaPAKlowryeCXFYZIaBkmPV8MAh4ZGWnZ5G00DLnFaJtS";
    
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
    public static final double FREE_DELIVERY_THRESHOLD = 500.0; // Free delivery for orders above this
    
    // Timeout Configuration
    public static final int PAYMENT_TIMEOUT_SECONDS = 300; // 5 minutes
    public static final int NETWORK_TIMEOUT_SECONDS = 30; // 30 seconds
}
