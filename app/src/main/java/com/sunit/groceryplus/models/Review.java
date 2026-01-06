package com.sunit.groceryplus.models;

/** Review - Model representing a user's feedback and rating for a product. */
public class Review {
    
    private int reviewId;       // Unique DB identifier
    private int userId;         // Author User ID
    private String userName;    // Author Name (Joined)
    private int productId;      // Product ID
    private String productName; // Product Name (Joined)
    private int rating;         // Score (1-5)
    private String comment;     // Text content
    private String createdAt;   // Timestamp

    /** Full Constructor. */
    public Review(int reviewId, int userId, String userName, int productId, String productName, int rating, String comment, String createdAt) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.userName = userName;
        this.productId = productId;
        this.productName = productName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // ================= GETTERS =================

    public int getReviewId() {
        return reviewId;
    }

    public String getUserName() {
        return userName;
    }

    public int getUserId() {
        return userId;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
