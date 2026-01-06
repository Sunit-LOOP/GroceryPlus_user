package com.sunit.groceryplus.models;

/** CartItem - Model representing a specific product and quantity in a user's shopping cart. */
public class CartItem {
    
    // Unique ID for this cart entry in the database
    private int cartId;         // Unique DB identifier
    private int userId;         // Owner user ID
    private int productId;      // Product reference ID
    private String productName; // Snapshot of product name
    private double price;       // Snapshot of product price
    private int quantity;       // Quantity selected
    private String image;       // Thumbnail image reference

    /** Default constructor. */
    public CartItem() {
    }

    /**
     * Full Constructor for retrieving existing items.
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
     * Creation Constructor for adding new items.
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
