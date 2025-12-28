package com.sunit.groceryplus.network;

import android.content.Context;

public class ApiClient {

    private static Context context;

    private ApiClient() {
        // Private constructor to prevent instantiation
    }

    public static void setContext(Context appContext) {
        context = appContext;
    }

    public static Context getContext() {
        return context;
    }
}