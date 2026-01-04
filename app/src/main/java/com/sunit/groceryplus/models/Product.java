package com.sunit.groceryplus.models;

/**
 * Product Model Class
 * 
 * Represents an item available for sale in the store.
 * Contains all display information (name, image, description) and 
 * inventory data (price, stock, vendor).
 */
public class Product {
    
    // Unique ID for the product in the database
    private int productId;
    
    // Display name of the product
    private String productName;
    
    // ID of the category this product belongs to
    private int categoryId;
    
    // Name of the category (for display convenience)
    private String categoryName;
    
    // Sale price per unit
    private double price;
    
    // Detailed product description
    private String description;
    
    // URL or identifier for the product image
    private String image;
    
    // Current available stock count
    private int stockQuantity;
    
    // Average user rating (0.0 to 5.0)
    private double rating;
    
    // ID of the vendor supplying this product
    private int vendorId;
    
    // Name of the vendor
    private String vendorName;

    /**
     * Default Constructor
     */
    public Product() {
        this.rating = 0.0;
    }

    /**
     * Full Constructor
     * Used when retrieving full product details from the database.
     * 
     * @param productId Unique ID
     * @param productName Product Name
     * @param categoryId Category ID
     * @param categoryName Category Name
     * @param price Unit Price
     * @param description Details
     * @param image Image URL
     * @param rating Avg Rating
     * @param stockQuantity Stock Count
     * @param vendorId Vendor ID
     * @param vendorName Vendor Name
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
     * Legacy/DB Helper Constructor
     * Similar to the full constructor but defaults rating to 0.0.
     * Primarily used during database population or simple queries.
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
     * Simplified Constructor
     * Used in contexts where names (Vendor/Category) are not yet fetched or needed.
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

