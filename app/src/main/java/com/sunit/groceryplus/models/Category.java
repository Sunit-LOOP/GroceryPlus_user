package com.sunit.groceryplus.models;

/** Category - Model representing a product grouping or department. */
public class Category {
    // Unique ID for the category
    private int categoryId;             // Unique DB identifier
    private String categoryName;        // Category Display Name
    private String categoryDescription; // Short description
    private String imageUrl;            // Icon resource or URL

    /** Default Constructor. */
    public Category() {
    }

    /**
     * Full Constructor.
     */
    public Category(int categoryId, String categoryName, String categoryDescription, String imageUrl) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
        this.imageUrl = imageUrl;
    }

    /**
     * Constructor without ID for creating new categories.
     */
    public Category(String categoryName, String categoryDescription, String imageUrl) {
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
        this.imageUrl = imageUrl;
    }

    /**
     * Simple Constructor without image.
     */
    public Category(int categoryId, String categoryName, String categoryDescription) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
    }

    // ================= GETTERS AND SETTERS =================

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
