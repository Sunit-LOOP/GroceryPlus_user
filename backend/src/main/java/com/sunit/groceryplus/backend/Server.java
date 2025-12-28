package com.sunit.groceryplus.backend;

import static spark.Spark.post;
import static spark.Spark.port;
import static spark.Spark.options;
import static spark.Spark.before;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Server {
    private static final Gson gson = new Gson();

// Use environment variable for Stripe secret key
    private static final String STRIPE_SECRET_KEY = System.getenv("STRIPE_SECRET_KEY") != null ? 
        System.getenv("STRIPE_SECRET_KEY") : "sk_test_your_secret_key_here";

    public static void main(String[] args) {
        port(4567);
        Stripe.apiKey = STRIPE_SECRET_KEY;
        
        System.out.println("Using Stripe Secret Key: " + (STRIPE_SECRET_KEY.equals("STRIPE_KEY_PLACEHOLDER") ? 
            "DEFAULT TEST KEY - PLEASE REPLACE WITH YOUR ACTUAL KEY" : "CUSTOM KEY SET"));
        System.out.println("Server running on http://localhost:4567");

        // CORS headers
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

        post("/create-payment-intent", (request, response) -> {
            response.type("application/json");
            
            System.out.println("Received payment intent request: " + request.body());

            try {
                PaymentIntentRequest req = gson.fromJson(request.body(), PaymentIntentRequest.class);
                
                System.out.println("Parsed request - Amount: " + req.amount + ", Currency: " + req.currency);

                PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                        .setAmount((long) req.amount)
                        .setCurrency(req.currency != null ? req.currency.toLowerCase() : "npr")
                        .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                        )
                        .build();

                System.out.println("Creating PaymentIntent with params: Amount=" + req.amount + ", Currency=" + req.currency);
                
                PaymentIntent paymentIntent = PaymentIntent.create(params);
                System.out.println("PaymentIntent created successfully with client secret: " + paymentIntent.getClientSecret());
                return gson.toJson(new PaymentIntentResponse(paymentIntent.getClientSecret()));
            } catch (StripeException e) {
                System.err.println("Stripe Error: " + e.getCode() + " - " + e.getMessage());
                e.printStackTrace();
                response.status(400);
                return gson.toJson(new ErrorResponse("Stripe Error: " + e.getMessage()));
            } catch (Exception e) {
                System.err.println("General Error creating payment intent: " + e.getMessage());
                e.printStackTrace();
                response.status(400);
                return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
            }
        });
    }

    static class PaymentIntentRequest {
        @SerializedName("amount")
        long amount;
        @SerializedName("currency")
        String currency;
    }

    static class PaymentIntentResponse {
        @SerializedName("clientSecret")
        String clientSecret;

        public PaymentIntentResponse(String clientSecret) {
            this.clientSecret = clientSecret;
        }
    }

    static class ErrorResponse {
        @SerializedName("error")
        String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}