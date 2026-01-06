package com.sunit.groceryplus.network;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Retrofit client specifically configured for direct integration with the Stripe API. */
public class StripeApiClient {
    // Stripe infrastructure
    private static final String BASE_URL = "https://api.stripe.com/";
    private static Retrofit retrofit;

    /** Returns a singleton instance of the Retrofit client configured for Stripe API interactions. */
    public static Retrofit getClient() {
        if (retrofit == null) {
            // Configure OkHttpClient with proper SSL settings and timeouts
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
