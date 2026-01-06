package com.sunit.groceryplus.utils;

/** Legacy configuration constants for backward compatibility. @deprecated Use {@link PaymentConfig} instead. */
@Deprecated
public class Config {
    // Legacy mapping - redirects to PaymentConfig
    @Deprecated
    public static final String STRIPE_PUBLISHABLE_KEY = PaymentConfig.STRIPE_PUBLISHABLE_KEY;
    
    @Deprecated
    public static final String BACKEND_URL = PaymentConfig.BACKEND_URL;
}
