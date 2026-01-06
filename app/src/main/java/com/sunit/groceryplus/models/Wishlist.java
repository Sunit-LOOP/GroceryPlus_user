package com.sunit.groceryplus.models;

/** Wishlist - Model representing an item saved for future purchase. */
public class Wishlist {
    
    private int id;         // Unique DB identifier
    private int userId;     // Owner User ID
    private int productId;  // Saved Product ID
    private String addedAt; // Timestamp

    /** Default Constructor. */
    public Wishlist() {}

    /** Full Constructor. */
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
