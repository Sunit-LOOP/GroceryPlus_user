package com.sunit.groceryplus.network;

import android.content.Context;

/** Static client for managing application context required by network components. */
public class ApiClient {

    private static Context context;

    private ApiClient() {
        // Private constructor to prevent instantiation
    }

    /** Sets the application context for global accessibility. */
    public static void setContext(Context appContext) {
        context = appContext;
    }

    /** Returns the globally set application context. */
    public static Context getContext() {
        return context;
    }
}