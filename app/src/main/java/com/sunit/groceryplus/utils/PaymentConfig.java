package com.sunit.groceryplus.utils;

/** Central configuration for payment processing, backend API integration, and fulfillment settings. */
public class PaymentConfig {
    
    // Stripe infrastructure
    public static final String STRIPE_PUBLISHABLE_KEY = "pk_test_51SdBfzD0CBYYErpfvgC0r6IPOkh5Ey6g5ju5IOmoGR4nUISi5p8TXgfX75wjLuV8kXW7xoZWq3tBk9Z9svkMqIym0005By6JFh";
    // WARNING: Storing secret key in app is insecure. Use a proper backend in production.
    public static final String STRIPE_SECRET_KEY = "sk_test_51SdBfzD0CBYYErpfg5pIH7DiWP8rBpMVdxMpZm0YexTCbMtA1WQismaPAKlowryeCXFYZIaBkmPV8MAh4ZGWnZ5G00DLnFaJtS";
    
    // Backend environment
    public static final String BACKEND_URL_DEV = "http://10.0.2.2:80/";
    public static final String BACKEND_URL_PROD = "https://your-domain.com/backend-php/";
    public static final boolean USE_PRODUCTION = false;
    public static final String BACKEND_URL = USE_PRODUCTION ? BACKEND_URL_PROD : BACKEND_URL_DEV;
    
    // Billing settings
    public static final String CURRENCY = "usd";
    public static final int MIN_AMOUNT = 50;
    
    // Fulfillment settings
    public static final double DELIVERY_FEE = 100.0;
    public static final double FREE_DELIVERY_THRESHOLD = 500.0;
    
    // Network timeouts
    public static final int PAYMENT_TIMEOUT_SECONDS = 300;
    public static final int NETWORK_TIMEOUT_SECONDS = 30;
}
