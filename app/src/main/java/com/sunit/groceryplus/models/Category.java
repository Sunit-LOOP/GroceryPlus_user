package com.sunit.groceryplus.models;

/**
 * Category model class representing a product category
 */
public class Category {
    private int categoryId;
    private String categoryName;
    private String categoryDescription;
    private String imageUrl;

    // Default constructor
    public Category() {
    }

    // Constructor with all fields
    public Category(int categoryId, String categoryName, String categoryDescription, String imageUrl) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
        this.imageUrl = imageUrl;
    }

    // Constructor without ID (for new categories)
    public Category(String categoryName, String categoryDescription, String imageUrl) {
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
        this.imageUrl = imageUrl;
    }

    // Constructor without image
    public Category(int categoryId, String categoryName, String categoryDescription) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
    }

    // Getters and Setters
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", categoryDescription='" + categoryDescription + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
