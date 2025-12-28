package com.sunit.groceryplus.network;

import com.sunit.groceryplus.utils.PHPBackendConfig;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Body;

/**
 * Retrofit API interface for PHP Backend
 */
public interface PHPStripeApi {
    
    /**
     * Create Payment Intent
     * POST stripe-backend-simple.php/create-payment-intent
     */
    @POST("stripe-backend-simple.php")
    Call<PaymentIntentResponse> createPaymentIntent(@retrofit2.http.Body PaymentIntentRequest request);
    
    /**
     * Get Payment Status
     * GET stripe-backend-simple.php/payment-status?payment_intent_id=xxx
     */
    @GET("stripe-backend-simple.php")
    Call<PaymentStatusResponse> getPaymentStatus(@Query("payment_intent_id") String paymentIntentId);
    
    /**
     * Test Backend Connection
     * GET stripe-backend-simple.php/test
     */
    @GET("stripe-backend-simple.php")
    Call<TestResponse> testConnection();
    
    /**
     * Response models for PHP Backend
     */
    class PaymentStatusResponse {
        private String status;
        private int amount;
        private String currency;
        private String payment_method;
        
        // Getters and setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public int getAmount() { return amount; }
        public void setAmount(int amount) { this.amount = amount; }
        
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        
        public String getPayment_method() { return payment_method; }
        public void setPayment_method(String payment_method) { this.payment_method = payment_method; }
    }
    
    class TestResponse {
        private String status;
        private String timestamp;
        private boolean stripe_configured;
        
        // Getters and setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        
        public boolean isStripe_configured() { return stripe_configured; }
        public void setStripe_configured(boolean stripe_configured) { this.stripe_configured = stripe_configured; }
    }
}