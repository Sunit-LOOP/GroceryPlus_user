package com.sunit.groceryplus.models;

/**
 * Review Model Class
 * 
 * Represents a user review for a specific product.
 * Contains rating (1-5 stars) and a text comment.
 */
public class Review {
    
    // Unique ID for the review
    private int reviewId;
    
    // ID of the user who wrote the review
    private int userId;
    
    // Name of the user (Pre-fetched for display)
    private String userName;
    
    // ID of the product being reviewed
    private int productId;
    
    // Name of the product
    private String productName;
    
    // Rating given (1 to 5)
    private int rating;
    
    // Text feedback
    private String comment;
    
    // Timestamp of review creation
    private String createdAt;

    /**
     * Full Constructor
     * 
     * @param reviewId Unique ID
     * @param userId User ID
     * @param userName User Name
     * @param productId Product ID
     * @param productName Product Name
     * @param rating Star Rating
     * @param comment Feedback Text
     * @param createdAt Date/Time
     */
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
