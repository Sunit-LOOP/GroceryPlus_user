package com.sunit.groceryplus.models;

public class Promotion {
    private int promoId;
    private String code;
    private double discountPercentage;
    private String validUntil;
    private String imageUrl;
    private boolean isActive;

    public Promotion(int promoId, String code, double discountPercentage, String validUntil, String imageUrl, boolean isActive) {
        this.promoId = promoId;
        this.code = code;
        this.discountPercentage = discountPercentage;
        this.validUntil = validUntil;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
    }

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
