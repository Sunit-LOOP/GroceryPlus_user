package com.sunit.groceryplus.utils;

public class Config {
    // Legacy constants - use PaymentConfig for new code
    @Deprecated
    public static final String STRIPE_PUBLISHABLE_KEY = PaymentConfig.STRIPE_PUBLISHABLE_KEY;
    
    @Deprecated
    public static final String BACKEND_URL = PaymentConfig.BACKEND_URL;
}
