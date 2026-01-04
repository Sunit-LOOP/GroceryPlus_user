package com.sunit.groceryplus.models;

/**
 * Wishlist Model Class
 * 
 * Represents an item saved by the user for later purchase.
 * Links a User to a specific Product.
 */
public class Wishlist {
    
    // Unique ID for the wishlist entry
    private int id;
    
    // User ID owner
    private int userId;
    
    // Product ID saved
    private int productId;
    
    // Timestamp when it was added
    private String addedAt;

    /**
     * Default Constructor
     */
    public Wishlist() {}

    /**
     * Full Constructor
     * 
     * @param id Unique ID
     * @param userId User ID
     * @param productId Product ID
     * @param addedAt Timestamp
     */
    public Wishlist(int id, int userId, int productId, String addedAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.addedAt = addedAt;
    }

    // ================= GETTERS AND SETTERS =================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(String addedAt) {
        this.addedAt = addedAt;
    }
}
