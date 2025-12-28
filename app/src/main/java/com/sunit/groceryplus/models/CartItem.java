package com.sunit.groceryplus.models;

/**
 * CartItem model class representing an item in the shopping cart
 */
public class CartItem {
    private int cartId;
    private int userId;
    private int productId;
    private String productName;
    private double price;
    private int quantity;
    private String image;

    // Default constructor
    public CartItem() {
    }

    // Constructor with all fields
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

    // Constructor without cartId (for new items)
    public CartItem(int userId, int productId, String productName, 
                    double price, int quantity, String image) {
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
    }

    // Getters and Setters
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

    // Helper method to calculate subtotal
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
