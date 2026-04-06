package com.sunit.groceryplus.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

import java.util.Map;

/** Retrofit service interface defining endpoints for the Stripe Payment Intents API. */
public interface StripeService {
    /** Creates a new PaymentIntent on Stripe to initiate the payment process. */
    @FormUrlEncoded
    @POST("v1/payment_intents")
    Call<JsonObject> createPaymentIntent(
        @Header("Authorization") String authHeader,
        @FieldMap Map<String, String> params
    );
}
