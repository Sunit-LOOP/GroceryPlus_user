package com.sunit.groceryplus.network;

import com.google.gson.annotations.SerializedName;

public class PaymentIntentResponse {
    @SerializedName("clientSecret")
    private String clientSecret;

    public String getClientSecret() {
        return clientSecret;
    }
}
