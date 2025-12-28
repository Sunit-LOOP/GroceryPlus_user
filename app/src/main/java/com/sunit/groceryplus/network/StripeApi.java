package com.sunit.groceryplus.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface StripeApi {
    @POST("create-payment-intent")
    Call<PaymentIntentResponse> createPaymentIntent(@Body PaymentIntentRequest request);
}
