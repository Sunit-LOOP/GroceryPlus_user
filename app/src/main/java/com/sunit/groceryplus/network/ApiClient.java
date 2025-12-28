package com.sunit.groceryplus.network;

import com.sunit.groceryplus.utils.Config;
import com.sunit.groceryplus.utils.PHPBackendConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Context;

public class ApiClient {
    private static Retrofit retrofit = null;
    private static Retrofit phpRetrofit = null;
    private static Retrofit groceryRetrofit = null;
    private static Context appContext = null;

    // Set application context for token access
    public static void setContext(Context context) {
        appContext = context.getApplicationContext();
    }

    // Original Java backend
    public static StripeApi getStripeApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Config.BACKEND_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(createOkHttpClient())
                    .build();
        }
        return retrofit.create(StripeApi.class);
    }

    // Create OkHttpClient with proper timeout settings
    private static okhttp3.OkHttpClient createOkHttpClient() {
        return new okhttp3.OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)  // 30 seconds to connect
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)     // 60 seconds to read response
                .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)    // 60 seconds to send request
                .retryOnConnectionFailure(true)                            // Retry on connection failure
                .build();
    }

    // Create OkHttpClient with authentication interceptor
    private static okhttp3.OkHttpClient createAuthenticatedOkHttpClient() {
        return new okhttp3.OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Content-Type", "application/json");

                    if (appContext != null) {
                        String token = AuthManager.getToken(appContext);
                        if (token != null) {
                            requestBuilder.header("Authorization", token);
                        }
                    }

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                })
                .build();
    }

    // PHP Backend
    public static PHPStripeApi getPHPStripeApi() {
        if (phpRetrofit == null) {
            phpRetrofit = new Retrofit.Builder()
                    .baseUrl(PHPBackendConfig.BACKEND_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(createOkHttpClient())
                    .build();
        }
        return phpRetrofit.create(PHPStripeApi.class);
    }

    // Grocery API with authentication
    public static GroceryApi getGroceryApi() {
        if (groceryRetrofit == null) {
            groceryRetrofit = new Retrofit.Builder()
                    .baseUrl(ApiConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(createAuthenticatedOkHttpClient())
                    .build();
        }
        return groceryRetrofit.create(GroceryApi.class);
    }
    
    // Test backend connection
    public static void testConnection(PHPBackendConnectionCallback callback) {
        getPHPStripeApi().testConnection().enqueue(new retrofit2.Callback<PHPStripeApi.TestResponse>() {
            @Override
            public void onResponse(retrofit2.Call<PHPStripeApi.TestResponse> call, retrofit2.Response<PHPStripeApi.TestResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onConnectionSuccess(response.body());
                } else {
                    callback.onConnectionError("Backend response error: " + response.code());
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<PHPStripeApi.TestResponse> call, Throwable t) {
                callback.onConnectionError("Connection failed: " + t.getMessage());
            }
        });
    }
    
    public interface PHPBackendConnectionCallback {
        void onConnectionSuccess(PHPStripeApi.TestResponse response);
        void onConnectionError(String error);
    }
}
