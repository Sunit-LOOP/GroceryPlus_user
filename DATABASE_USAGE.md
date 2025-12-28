# Database Helper Usage Guide

This document explains how to use the DatabaseHelper class in the GroceryPlus application.

## Overview

The DatabaseHelper class provides a SQLite database implementation for the GroceryPlus application. It handles user management with secure password storage using SHA-256 hashing and salt generation.

## Features

1. **User Management**
   - Add new users (customers and admins)
   - Authenticate users with secure password verification
   - Check if users exist
   - Retrieve user information

2. **Security**
   - Passwords are hashed using SHA-256 with random salt
   - Default admin user created automatically
   - Secure authentication mechanism

## Database Schema

### Users Table
```sql
CREATE TABLE users(
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_name TEXT,
    user_email TEXT UNIQUE,
    user_phone TEXT,
    user_password TEXT,
    user_salt TEXT,
    user_type TEXT DEFAULT 'customer',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

## Usage Examples

### Initializing the DatabaseHelper
```java
DatabaseHelper dbHelper = new DatabaseHelper(context);
```

### Adding a New User
```java
long userId = dbHelper.addUser("John Doe", "john@example.com", "1234567890", "password123", "customer");
if (userId != -1) {
    // User added successfully
} else {
    // Failed to add user
}
```

### Authenticating a User
```java
User authenticatedUser = dbHelper.authenticateUser("admin@groceryplus.com", "admin123");
if (authenticatedUser != null) {
    // Authentication successful
    if (authenticatedUser.isAdmin()) {
        // Redirect to admin dashboard
    } else {
        // Redirect to customer interface
    }
} else {
    // Authentication failed
}
```

### Checking if User Exists
```java
boolean exists = dbHelper.isUserExists("john@example.com");
```

### Getting User by Email
```java
User user = dbHelper.getUserByEmail("john@example.com");
if (user != null) {
    // User found
}
```

## Default Admin User

A default admin user is automatically created:
- Email: admin@groceryplus.com
- Password: admin123
- Type: admin

## Security Notes

1. All passwords are stored as SHA-256 hashes with random salt
2. Never store plain text passwords
3. The salt is generated using SecureRandom for cryptographic strength
4. Always validate user input before storing in the database

## Error Handling

All database operations include proper error handling with logging. Methods return appropriate values (-1 for failures) to indicate success or failure.