package com.sunit.groceryplus.models;

/**
 * CartItem Model Class
 * 
 * Represents a single item within the user's shopping cart.
 * This class links a specific Product to a User and tracks the quantity
 * selected for purchase. It also stores a snapshot of the product's price
 * and name at the time it was added to the cart.
 */
public class CartItem {
    
    // Unique ID for this cart entry in the database
    private int cartId;
    
    // The ID of the user who owns this cart items
    private int userId;
    
    // The ID of the product being purchased
    private int productId;
    
    // Name of the product (stored to avoid repeated DB lookups)
    private String productName;
    
    // Price per unit of the product
    private double price;
    
    // Quantity of the product selected by the user
    private int quantity;
    
    // Image URL or resource name for the product thumbnail
    private String image;

    /**
     * Default constructor required for some serialization frameworks.
     */
    public CartItem() {
    }

    /**
     * Full Constructor
     * Used when retrieving an existing cart item from the database.
     * 
     * @param cartId Unique ID of the cart entry
     * @param userId User's ID
     * @param productId Product's ID
     * @param productName Name of the product
     * @param price Unit price
     * @param quantity Quantity selected
     * @param image Product image identifier
     */
    public CartItem(int cartId, int userId, int productId, String productName, 
                    double price, int quantity, String image) {
        this.cartId = cartId;
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
    }

    /**
     * Creation Constructor
     * Used when creating a NEW cart item before it has an ID from the database.
     * 
     * @param userId User's ID
     * @param productId Product's ID
     * @param productName Name of the product
     * @param price Unit price
     * @param quantity Quantity selected
     * @param image Product image identifier
     */
    public CartItem(int userId, int productId, String productName, 
                    double price, int quantity, String image) {
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
    }

    // ================= GETTERS AND SETTERS =================

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Calculates the total cost for this line item.
     * Formula: Unit Price * Quantity
     * 
     * @return The subtotal amount
     */
    public double getSubtotal() {
        return price * quantity;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "cartId=" + cartId +
                ", userId=" + userId +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", image='" + image + '\'' +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}
