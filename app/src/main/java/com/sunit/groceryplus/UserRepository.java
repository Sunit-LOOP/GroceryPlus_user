package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;
import com.sunit.groceryplus.models.User;

/** Repository for managing user account data and authentication in the database. */
public class UserRepository {
    // Infrastructure
    private static final String TAG = "UserRepository";
    private DatabaseHelper dbHelper;

    /** Initializes the repository with a DatabaseHelper. */
    public UserRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    /** Registers a new user account in the system. */
    public boolean registerUser(String name, String email, String phone, String password, String userType) {
        try {
            long result = dbHelper.addUser(name, email, phone, password, userType);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error registering user", e);
            return false;
        }
    }

    /** Authenticates a user based on email and password. */
    public User loginUser(String email, String password) {
        try {
            return dbHelper.authenticateUser(email, password);
        } catch (Exception e) {
            Log.e(TAG, "Error during user login", e);
            return null;
        }
    }

    /** Retrieves a user's details by their email address. */
    public User getUserByEmail(String email) {
        try {
            return dbHelper.getUserByEmail(email);
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by email", e);
            return null;
        }
    }

    /** Retrieves a user's details by their unique ID. */
    public User getUserById(int userId) {
        try {
            return dbHelper.getUserById(userId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by ID", e);
            return null;
        }
    }

    /** Checks if a user already exists with the given email address. */
    public boolean isUserExists(String email) {
        try {
            return dbHelper.isUserExists(email);
        } catch (Exception e) {
            Log.e(TAG, "Error checking if user exists", e);
            return false;
        }
    }

    /** Updates the profile information for a specific user. */
    public boolean updateUser(int userId, String name, String email, String phone, String address) {
        try {
            return dbHelper.updateUser(userId, name, email, phone, address);
        } catch (Exception e) {
            Log.e(TAG, "Error updating user", e);
            return false;
        }
    }

    /** Updates a user object in the database. */
    public boolean updateUser(User user) {
        try {
            return dbHelper.updateUser(user.getUserId(), user.getName(), user.getEmail(), user.getPhone(), "");
        } catch (Exception e) {
            Log.e(TAG, "Error updating user object", e);
            return false;
        }
    }

    /** Checks if a user object has administrative privileges. */
    public boolean isAdmin(User user) {
        return user != null && user.isAdmin();
    }

    /** Retrieves a list of all registered users. */
    public java.util.List<User> getAllUsers() {
        try {
            return dbHelper.getAllUsers();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all users", e);
            return new java.util.ArrayList<>();
        }
    }
}