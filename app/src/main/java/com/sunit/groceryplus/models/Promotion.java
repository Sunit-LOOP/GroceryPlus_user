package com.sunit.groceryplus.models;

/**
 * Promotion Model Class
 * 
 * Represents a discount coupon or promotional offer.
 * Users can apply these codes during checkout to receive discounts.
 */
public class Promotion {
    
    // Unique ID for the promotion
    private int promoId;
    
    // The code users enter to redeem (e.g., "SALE50")
    private String code;
    
    // Discount amount in percentage (e.g., 10.0 for 10%)
    private double discountPercentage;
    
    // Expiration date (YYYY-MM-DD)
    private String validUntil;
    
    // Banner image for the promotion
    private String imageUrl;
    
    // Is the promotion currently active?
    private boolean isActive;

    /**
     * Full Constructor
     * 
     * @param promoId Unique ID
     * @param code Coupon Code
     * @param discountPercentage Discount %
     * @param validUntil Expiry Date
     * @param imageUrl Banner Image
     * @param isActive Active Status
     */
    public Promotion(int promoId, String code, double discountPercentage, String validUntil, String imageUrl, boolean isActive) {
        this.promoId = promoId;
        this.code = code;
        this.discountPercentage = discountPercentage;
        this.validUntil = validUntil;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
    }

    // ================= GETTERS =================

    public int getPromoId() {
        return promoId;
    }

    public String getCode() {
        return code;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isActive() {
        return isActive;
    }
}
