package com.sunit.groceryplus.models;

/** User - Model representing a registered customer or administrator account. */
public class User {
    
    private int userId;         // Unique DB identifier
    private String name;        // Full Name
    private String email;       // Email (Login Credential)
    private String phone;       // Phone Number (Login Credential)
    private String userType;    // Account Type ('admin', 'customer') 
    private String password;    // Password (Login Credential) 
    private double walletBalance;
    private double loyaltyPoints;

    /** Default Constructor. */
    public User() {}

    /**
     * Full Constructor.
     */
    public User(int userId, String name, String email, String phone, String userType) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userType = userType;
    }

    // ================= GETTERS =================

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getUserType() {
        return userType;
    }

    public String getPassword() {
        return password;
    }

    // ================= ALIAS METHODS =================
    // For consistency with other parts of the app

    public int getId() { return userId; }
    public String getUserName() { return name; }
    public String getUserEmail() { return email; }
    public String getUserPhone() { return phone; }

    // ================= SETTERS =================

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getWalletBalance() { return walletBalance; }
    public void setWalletBalance(double walletBalance) { this.walletBalance = walletBalance; }

    public double getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(double loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    /**
     * Check if user is an admin
     * @return true if userType is "admin", false otherwise
     */
    public boolean isAdmin() {
        return "admin".equals(this.userType);
    }

    /**
     * Get created at timestamp (placeholder for compatibility)
     * Currently returns empty string as User table created_at is not always fetched.
     */
    public String getCreatedAt() {
        return ""; 
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}