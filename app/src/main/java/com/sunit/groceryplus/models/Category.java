package com.sunit.groceryplus.models;

/**
 * Category Model Class
 * 
 * Represents a product category (e.g., "Vegetables", "Fruits", "Dairy").
 * Categories are used to organize products and help users browse the catalog.
 */
public class Category {
    // Unique ID for the category
    private int categoryId;
    
    // Display name of the category
    private String categoryName;
    
    // Brief description of what this category contains
    private String categoryDescription;
    
    // URL or resource name for the category icon/image
    private String imageUrl;

    /**
     * Default Constructor
     */
    public Category() {
    }

    /**
     * Full Constructor
     * 
     * @param categoryId Unique ID
     * @param categoryName Name of the category
     * @param categoryDescription Description text
     * @param imageUrl Image identifier
     */
    public Category(int categoryId, String categoryName, String categoryDescription, String imageUrl) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
        this.imageUrl = imageUrl;
    }

    /**
     * Constructor without ID
     * Used when creating a new category that hasn't been saved to DB yet.
     */
    public Category(String categoryName, String categoryDescription, String imageUrl) {
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
        this.imageUrl = imageUrl;
    }

    /**
     * Simple Constructor
     * Used when image is not available or relevant.
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
