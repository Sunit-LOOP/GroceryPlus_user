package com.sunit.groceryplus.models;

public class User {
    private int userId;
    private String name;
    private String email;
    private String phone;
    private String userType; // 'admin' or 'customer'

    public User() {
    }

    public User(int userId, String name, String email, String phone, String userType) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userType = userType;
    }

    // Getters
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

    // Setters
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
     */
    public boolean isAdmin() {
        return "admin".equals(this.userType);
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