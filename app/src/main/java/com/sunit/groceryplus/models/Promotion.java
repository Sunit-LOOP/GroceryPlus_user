package com.sunit.groceryplus.models;

/** Promotion - Model representing a discount coupon or special offer. */
public class Promotion {
    
    private int promoId;            // Unique DB identifier
    private String code;            // Coupon Code (e.g., "SAVE10")
    private double discountPercentage; // Discount Value (e.g., 10.0)
    private String validUntil;      // Expiry Date (YYYY-MM-DD)
    private String imageUrl;        // Banner Image Resource/URL
    private boolean isActive;       // Activation Flag

    /** Full Constructor. */
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
