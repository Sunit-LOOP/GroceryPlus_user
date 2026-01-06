package com.sunit.groceryplus.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/** Retrofit service interface defining endpoints for the Stripe Payment Intents API. */
public interface StripeService {
    /** Creates a new PaymentIntent on Stripe to initiate the payment process. */
    @FormUrlEncoded
    @POST("v1/payment_intents")
    Call<JsonObject> createPaymentIntent(
        @Header("Authorization") String authHeader,
        @Field("amount") int amount,
        @Field("currency") String currency,
        @Field("automatic_payment_methods[enabled]") boolean automatedMethods
    );
}
