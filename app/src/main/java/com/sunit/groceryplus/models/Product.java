package com.sunit.groceryplus.models;

/** Product - Core model representing an item available for sale, including inventory and vendor details. */
public class Product {
    
    // Identity & Details
    private int productId;      // Unique Identifier
    private String productName; // Display Name
    private String description; // Product Details
    private String image;       // Image URL or Resource Name
    
    // Categorization
    private int categoryId;     // Category Reference
    private String categoryName;// Category Name (Joined)
    
    // Sales & Inventory
    private double price;       // Unit Price
    private int stockQuantity;  // Available Stock
    
    // Vendor Info
    private int vendorId;       // Supplier ID
    private String vendorName;  // Supplier Name (Joined)
    
    // Metrics
    private double rating;      // Average Rating (0.0 - 5.0)

    /** Default Constructor. */
    public Product() {
        this.rating = 0.0;
    }

    /**
     * Full Constructor for retrieving complete product details.
     */
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

    /**
     * Legacy/Helper Constructor (defaults rating to 0.0).
     */
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
    
    /**
     * Simplified Constructor (minimal fields).
     */
    public Product(int productId, String productName, int categoryId, double price, String description, String image, int stockQuantity, int vendorId) {
        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
        this.categoryName = "";
        this.price = price;
        this.description = description;
        this.image = image;
        this.stockQuantity = stockQuantity;
        this.vendorId = vendorId;
        this.vendorName = "";
        this.rating = 0.0;
    }

    // ================= GETTERS AND SETTERS =================

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
    
    // Alias methods for compatibility or semantic clarity
    public int getStock() { return stockQuantity; }
    public void setStock(int stock) { this.stockQuantity = stock; }
    
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

