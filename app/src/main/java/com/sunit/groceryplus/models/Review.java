package com.sunit.groceryplus.models;

public class Review {
    private int reviewId;
    private int userId;
    private String userName;
    private int productId;
    private String productName;
    private int rating;
    private String comment;
    private String createdAt;

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

    public int getReviewId() {
        return reviewId;
    }

    public String getUserName() {
        return userName;
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
