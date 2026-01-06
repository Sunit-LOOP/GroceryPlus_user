package com.sunit.groceryplus.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Centralized Retrofit client provider for general backend API interactions. */
public class RetrofitClient {
    // Backend infrastructure
    private static final String BASE_URL = "http://groceryplus.infinityfreeapp.com/";
    private static Retrofit retrofit;

    /** Returns a singleton instance of the Retrofit client configured with the base URL and GSON converter. */
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
