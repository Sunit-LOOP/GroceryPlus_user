package com.sunit.groceryplus.models;

/**
 * Product model class representing a product in the grocery store
 */
public class Product {
    private int productId;
    private String productName;
    private int categoryId;
    private String categoryName;
    private double price;
    private String description;
    private String image;
    private int stockQuantity;
    private double rating;
    private int vendorId;
    private String vendorName;

    // Default constructor
    public Product() {
        this.rating = 0.0;
    }

    // Constructor with all fields
    public Product(int productId, String productName, int categoryId, String categoryName, 
                   double price, String description, String image, double rating, int stockQuantity, int vendorId, String vendorName) {
        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.price = price;
        this.description = description;
        this.image = image;
        this.rating = rating;
        this.stockQuantity = stockQuantity;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
    }

    // Constructor used by DatabaseHelper
    public Product(int productId, String productName, int categoryId, String categoryName, 
                   double price, String description, String image, int stockQuantity, int vendorId, String vendorName) {
        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.price = price;
        this.description = description;
        this.image = image;
        this.stockQuantity = stockQuantity;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.rating = 0.0;
    }

    // Getters and Setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public boolean isInStock() { return stockQuantity > 0; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getVendorId() { return vendorId; }
    public void setVendorId(int vendorId) { this.vendorId = vendorId; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", vendorName='" + vendorName + '\'' +
                '}';
    }
}
