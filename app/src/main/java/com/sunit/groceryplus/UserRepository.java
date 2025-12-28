package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;
import com.sunit.groceryplus.models.User;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private DatabaseHelper dbHelper;

    public UserRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    /**
     * Register a new user
     */
    public boolean registerUser(String name, String email, String phone, String password, String userType) {
        try {
            long result = dbHelper.addUser(name, email, phone, password, userType);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error registering user", e);
            return false;
        }
    }

    /**
     * Authenticate user login
     */
    public User loginUser(String email, String password) {
        try {
            return dbHelper.authenticateUser(email, password);
        } catch (Exception e) {
            Log.e(TAG, "Error during user login", e);
            return null;
        }
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        try {
            return dbHelper.getUserByEmail(email);
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by email", e);
            return null;
        }
    }

    /**
     * Get user by ID
     */
    public User getUserById(int userId) {
        try {
            return dbHelper.getUserById(userId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by ID", e);
            return null;
        }
    }

    /**
     * Check if user exists
     */
    public boolean isUserExists(String email) {
        try {
            return dbHelper.isUserExists(email);
        } catch (Exception e) {
            Log.e(TAG, "Error checking if user exists", e);
            return false;
        }
    }

    /**
     * Update user profile
     */
    public boolean updateUser(int userId, String name, String email, String phone, String address) {
        try {
            return dbHelper.updateUser(userId, name, email, phone, address);
        } catch (Exception e) {
            Log.e(TAG, "Error updating user", e);
            return false;
        }
    }

    /**
     * Check if user is admin
     */
    public boolean isAdmin(User user) {
        return user != null && user.isAdmin();
    }

    /**
     * Get all users
     */
    public java.util.List<User> getAllUsers() {
        try {
            return dbHelper.getAllUsers();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all users", e);
            return new java.util.ArrayList<>();
        }
    }
}