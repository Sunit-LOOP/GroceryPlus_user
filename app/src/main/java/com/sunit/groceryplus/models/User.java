package com.sunit.groceryplus.models;

/**
 * User Model Class
 * 
 * Represents a registered user in the application.
 * Can be either a regular 'customer' or an 'admin'.
 */
public class User {
    
    // Unique ID for the user
    private int userId;
    
    // Full Name
    private String name;
    
    // Email Address (Login credential)
    private String email;
    
    // Phone Number (Login credential)
    private String phone;
    
    // User Role: 'admin' or 'customer'
    private String userType; 

    /**
     * Default Constructor
     */
    public User() {
    }

    /**
     * Full Constructor
     * 
     * @param userId Unique ID
     * @param name Full Name
     * @param email Email Address
     * @param phone Phone Number
     * @param userType Role (admin/customer)
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