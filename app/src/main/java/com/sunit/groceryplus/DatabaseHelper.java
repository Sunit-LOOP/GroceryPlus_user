package com.sunit.groceryplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import com.sunit.groceryplus.models.User;
import static com.sunit.groceryplus.DatabaseContract.CartItemEntry;
import static com.sunit.groceryplus.DatabaseContract.CategoryEntry;
import static com.sunit.groceryplus.DatabaseContract.OrderEntry;
import static com.sunit.groceryplus.DatabaseContract.OrderItemEntry;
import static com.sunit.groceryplus.DatabaseContract.ProductEntry;
import static com.sunit.groceryplus.DatabaseContract.UserEntry;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // Database Info
    private static final String DATABASE_NAME = "GroceryPlus.db";
    private static final int DATABASE_VERSION = 6;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create all tables using DatabaseContract constants
        db.execSQL(DatabaseContract.SQL_CREATE_USERS_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_CATEGORIES_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_PRODUCTS_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_ORDERS_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_ORDER_ITEMS_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_CART_ITEMS_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_FAVORITES_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_MESSAGES_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_PROMOTIONS_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_REVIEWS_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_DELIVERY_PERSONNEL_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_PAYMENTS_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_NOTIFICATIONS_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_ADDRESSES_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_VENDORS_TABLE);

        // Insert default admin user
        insertDefaultAdmin(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables
        db.execSQL(DatabaseContract.SQL_DELETE_USERS_TABLE);
        db.execSQL(DatabaseContract.SQL_DELETE_CATEGORIES_TABLE);
        db.execSQL(DatabaseContract.SQL_DELETE_PRODUCTS_TABLE);
        db.execSQL(DatabaseContract.SQL_DELETE_ORDERS_TABLE);
        db.execSQL(DatabaseContract.SQL_DELETE_ORDER_ITEMS_TABLE);
        db.execSQL(DatabaseContract.SQL_DELETE_CART_ITEMS_TABLE);
        db.execSQL(DatabaseContract.SQL_DELETE_FAVORITES_TABLE);
        db.execSQL(DatabaseContract.SQL_DELETE_MESSAGES_TABLE);
        db.execSQL(DatabaseContract.SQL_DELETE_PROMOTIONS_TABLE);
        db.execSQL(DatabaseContract.SQL_DELETE_REVIEWS_TABLE);
        db.execSQL(DatabaseContract.SQL_DELETE_DELIVERY_PERSONNEL_TABLE);
        db.execSQL(DatabaseContract.SQL_DELETE_PAYMENTS_TABLE);
        db.execSQL(DatabaseContract.SQL_DELETE_NOTIFICATIONS_TABLE);
        db.execSQL(DatabaseContract.SQL_DELETE_ADDRESSES_TABLE);
        db.execSQL(DatabaseContract.SQL_DELETE_VENDORS_TABLE);

        onCreate(db);
    }

    /** Insert default admin user */
    private void insertDefaultAdmin(SQLiteDatabase db) {
        try {
            String adminPassword = "admin123";
            String salt = generateSalt();
            String hashedPassword = hashPassword(adminPassword, salt);

            ContentValues values = new ContentValues();
            values.put(DatabaseContract.UserEntry.COLUMN_NAME_USER_NAME, "Admin User");
            values.put(DatabaseContract.UserEntry.COLUMN_NAME_USER_EMAIL, "admin@gmail.com");
            values.put(DatabaseContract.UserEntry.COLUMN_NAME_USER_PHONE, "9815689963");
            values.put(DatabaseContract.UserEntry.COLUMN_NAME_USER_PASSWORD, hashedPassword);
            values.put(DatabaseContract.UserEntry.COLUMN_NAME_USER_SALT, salt);
            values.put(DatabaseContract.UserEntry.COLUMN_NAME_USER_TYPE, "admin");

            db.insert(DatabaseContract.UserEntry.TABLE_NAME, null, values);
            Log.d(TAG, "Default admin created");
        } catch (Exception e) {
            Log.e(TAG, "Error creating default admin", e);
        }
    }

    /** Generate random salt */
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return bytesToHex(salt);
    }

    /** Hash password with SHA-256 + salt */
    private String hashPassword(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt.getBytes());
        byte[] hashed = md.digest(password.getBytes());
        return bytesToHex(hashed);
    }

    /** Convert byte array to hex string */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Add a new user to the database
     */
    public long addUser(String name, String email, String phone, String password, String userType) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            String salt = generateSalt();
            String hashedPassword = hashPassword(password, salt);
            
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.UserEntry.COLUMN_NAME_USER_NAME, name);
            values.put(DatabaseContract.UserEntry.COLUMN_NAME_USER_EMAIL, email);
            values.put(DatabaseContract.UserEntry.COLUMN_NAME_USER_PHONE, phone);
            values.put(DatabaseContract.UserEntry.COLUMN_NAME_USER_PASSWORD, hashedPassword);
            values.put(DatabaseContract.UserEntry.COLUMN_NAME_USER_SALT, salt);
            values.put(DatabaseContract.UserEntry.COLUMN_NAME_USER_TYPE, userType);
            
            // Inserting Row
            long userId = db.insert(DatabaseContract.UserEntry.TABLE_NAME, null, values);
            return userId;
        } catch (Exception e) {
            Log.e(TAG, "Error adding user", e);
            return -1;
        }
    }
    
    /**
     * Check user credentials for login
     */
    public User authenticateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        Log.d(TAG, "Authenticating user with email: " + email);
        String selectQuery = "SELECT * FROM " + DatabaseContract.UserEntry.TABLE_NAME + " WHERE " + DatabaseContract.UserEntry.COLUMN_NAME_USER_EMAIL + " = ?";
        
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email});
        
        if (cursor != null && cursor.moveToFirst()) {
            try {
                int saltIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NAME_USER_SALT);
                int passwordIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NAME_USER_PASSWORD);
                int userIdIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NAME_USER_ID);
                int userNameIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NAME_USER_NAME);
                int userEmailIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NAME_USER_EMAIL);
                int userPhoneIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NAME_USER_PHONE);
                int userTypeIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NAME_USER_TYPE);
                
                if (saltIndex >= 0 && passwordIndex >= 0) {
                    String storedSalt = cursor.getString(saltIndex);
                    String storedPassword = cursor.getString(passwordIndex);
                    String hashedInputPassword = hashPassword(password, storedSalt);
                    Log.d(TAG, "Comparing passwords - input: " + hashedInputPassword + ", stored: " + storedPassword);
                    
                    if (hashedInputPassword.equals(storedPassword)) {
                        // Password matches
                        int userId = (userIdIndex >= 0) ? cursor.getInt(userIdIndex) : -1;
                        String userName = (userNameIndex >= 0) ? cursor.getString(userNameIndex) : "";
                        String userEmail = (userEmailIndex >= 0) ? cursor.getString(userEmailIndex) : "";
                        String userPhone = (userPhoneIndex >= 0) ? cursor.getString(userPhoneIndex) : "";
                        String userType = (userTypeIndex >= 0) ? cursor.getString(userTypeIndex) : "";
                        
                        User user = new User(userId, userName, userEmail, userPhone, userType);
                        Log.d(TAG, "Authentication successful for user: " + userName + " (" + userType + ")");
                        return user;
                    } else {
                        Log.d(TAG, "Password mismatch");
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error authenticating user", e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if (cursor != null) {
            Log.d(TAG, "No user found with email: " + email);
            cursor.close();
        } else {
            Log.d(TAG, "Cursor is null for email: " + email);
        }
        
        Log.d(TAG, "Authentication failed for email: " + email);
        return null; // Authentication failed
    }
    
    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selectQuery = "SELECT * FROM " + DatabaseContract.UserEntry.TABLE_NAME + " WHERE " + DatabaseContract.UserEntry.COLUMN_NAME_USER_EMAIL + " = ?";
        
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email});
        
        if (cursor != null && cursor.moveToFirst()) {
            try {
                int userIdIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NAME_USER_ID);
                int userNameIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NAME_USER_NAME);
                int userEmailIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NAME_USER_EMAIL);
                int userPhoneIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NAME_USER_PHONE);
                int userTypeIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NAME_USER_TYPE);
                
                int userId = (userIdIndex >= 0) ? cursor.getInt(userIdIndex) : -1;
                String userName = (userNameIndex >= 0) ? cursor.getString(userNameIndex) : "";
                String userEmail = (userEmailIndex >= 0) ? cursor.getString(userEmailIndex) : "";
                String userPhone = (userPhoneIndex >= 0) ? cursor.getString(userPhoneIndex) : "";
                String userType = (userTypeIndex >= 0) ? cursor.getString(userTypeIndex) : "";
                
                User user = new User(userId, userName, userEmail, userPhone, userType);
                return user;
            } catch (Exception e) {
                Log.e(TAG, "Error getting user by email", e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if (cursor != null) {
            cursor.close();
        }
        
        return null;
    }
    
    /**
     * Get user by ID
     */
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selectQuery = "SELECT * FROM " + DatabaseContract.UserEntry.TABLE_NAME + " WHERE " + DatabaseContract.UserEntry.COLUMN_NAME_USER_ID + " = ?";
        
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});
        
        if (cursor != null && cursor.moveToFirst()) {
            try {
                int userIdIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NAME_USER_ID);
                int userNameIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NAME_USER_NAME);
                int userEmailIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NAME_USER_EMAIL);
                int userPhoneIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NAME_USER_PHONE);
                int userTypeIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NAME_USER_TYPE);
                
                int id = (userIdIndex >= 0) ? cursor.getInt(userIdIndex) : -1;
                String userName = (userNameIndex >= 0) ? cursor.getString(userNameIndex) : "";
                String userEmail = (userEmailIndex >= 0) ? cursor.getString(userEmailIndex) : "";
                String userPhone = (userPhoneIndex >= 0) ? cursor.getString(userPhoneIndex) : "";
                String userType = (userTypeIndex >= 0) ? cursor.getString(userTypeIndex) : "";
                
                User user = new User(id, userName, userEmail, userPhone, userType);
                return user;
            } catch (Exception e) {
                Log.e(TAG, "Error getting user by ID", e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if (cursor != null) {
            cursor.close();
        }
        
        return null;
    }
    
    /**
     * Update user profile
     */
    public boolean updateUser(int userId, String name, String email, String phone, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            ContentValues values = new ContentValues();
            values.put(UserEntry.COLUMN_NAME_USER_NAME, name);
            values.put(UserEntry.COLUMN_NAME_USER_EMAIL, email);
            values.put(UserEntry.COLUMN_NAME_USER_PHONE, phone);
            // Address field removed - User table doesn't have address column
            
            int result = db.update(UserEntry.TABLE_NAME, values, 
                                  UserEntry.COLUMN_NAME_USER_ID + " = ?", 
                                  new String[]{String.valueOf(userId)});
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating user", e);
            return false;
        }
    }
    
    /**
     * Check if user exists
     */
    public boolean isUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        Log.d(TAG, "Checking if user exists with email: " + email);
        String selectQuery = "SELECT * FROM " + DatabaseContract.UserEntry.TABLE_NAME + " WHERE " + DatabaseContract.UserEntry.COLUMN_NAME_USER_EMAIL + " = ?";
        
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email});
        
        boolean exists = (cursor != null && cursor.getCount() > 0);
        Log.d(TAG, "User exists: " + exists);
        
        if (cursor != null) {
            cursor.close();
        }
        
        return exists;
    }

    /**
     * Get all users
     */
    public java.util.List<User> getAllUsers() {
        java.util.List<User> users = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + DatabaseContract.UserEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_NAME_USER_ID));
                    String userName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_NAME_USER_NAME));
                    String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_NAME_USER_EMAIL));
                    String userPhone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_NAME_USER_PHONE));
                    String userType = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_NAME_USER_TYPE));
                    
                    User user = new User(userId, userName, userEmail, userPhone, userType);
                    users.add(user);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing user", e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return users;
    }
    
    /**
     * Add a new category
     */
    public long addCategory(String categoryName, String categoryDescription) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.CategoryEntry.COLUMN_NAME_CATEGORY_NAME, categoryName);
        values.put(DatabaseContract.CategoryEntry.COLUMN_NAME_CATEGORY_DESCRIPTION, categoryDescription);
        
        long categoryId = db.insert(DatabaseContract.CategoryEntry.TABLE_NAME, null, values);
        return categoryId;
    }
    
    /**
     * Add a new product
     */
    public long addProduct(String productName, int categoryId, double price, String description, String image, int stockQuantity, int vendorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME, productName);
        values.put(DatabaseContract.ProductEntry.COLUMN_NAME_CATEGORY_ID, categoryId);
        values.put(DatabaseContract.ProductEntry.COLUMN_NAME_PRICE, price);
        values.put(DatabaseContract.ProductEntry.COLUMN_NAME_DESCRIPTION, description);
        values.put(DatabaseContract.ProductEntry.COLUMN_NAME_IMAGE, image);
        values.put(DatabaseContract.ProductEntry.COLUMN_NAME_STOCK, stockQuantity);
        values.put(DatabaseContract.ProductEntry.COLUMN_NAME_VENDOR_ID, vendorId);
        
        long productId = db.insert(DatabaseContract.ProductEntry.TABLE_NAME, null, values);
        return productId;
    }
    
    /**
     * Add item to cart
     */
    public long addToCart(int userId, int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            // First check if item already exists
            int existingQty = getProductQuantityInCart(userId, productId);
            if (existingQty > 0) {
                return updateCartQuantity(userId, productId, existingQty + quantity) ? 1 : -1;
            }

            ContentValues values = new ContentValues();
            values.put(CartItemEntry.COLUMN_NAME_USER_ID, userId);
            values.put(CartItemEntry.COLUMN_NAME_PRODUCT_ID, productId);
            values.put(CartItemEntry.COLUMN_NAME_QUANTITY, quantity);
            
            // Inserting Row
            long cartId = db.insert(CartItemEntry.TABLE_NAME, null, values);
            return cartId;
        } catch (Exception e) {
            Log.e(TAG, "Error adding to cart", e);
            return -1;
        }
    }

    /**
     * Get specific product quantity in cart
     */
    public int getProductQuantityInCart(int userId, int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + CartItemEntry.COLUMN_NAME_QUANTITY + " FROM " + CartItemEntry.TABLE_NAME +
                       " WHERE " + CartItemEntry.COLUMN_NAME_USER_ID + " = ? AND " + CartItemEntry.COLUMN_NAME_PRODUCT_ID + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(productId)});
        int quantity = 0;
        if (cursor != null && cursor.moveToFirst()) {
            quantity = cursor.getInt(0);
            cursor.close();
        }
        return quantity;
    }

    /**
     * Update cart quantity for a specific product
     */
    public boolean updateCartQuantity(int userId, int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CartItemEntry.COLUMN_NAME_QUANTITY, quantity);
        
        int rows = db.update(CartItemEntry.TABLE_NAME, values, 
                CartItemEntry.COLUMN_NAME_USER_ID + " = ? AND " + CartItemEntry.COLUMN_NAME_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(productId)});
        return rows > 0;
    }
    
    /**
     * Get cart items for user
     */
    public Cursor getCartItems(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selectQuery = "SELECT * FROM " + CartItemEntry.TABLE_NAME + " WHERE " + CartItemEntry.COLUMN_NAME_USER_ID + " = ?";
        
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});
        return cursor;
    }
    
    /**
     * Remove item from cart
     */
    public int removeFromCart(int cartId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        int result = db.delete(CartItemEntry.TABLE_NAME, CartItemEntry.COLUMN_NAME_CART_ID + " = ?", 
                              new String[]{String.valueOf(cartId)});
        return result;
    }

    /**
     * Remove specific product from cart for user
     */
    public boolean removeFromCart(int userId, int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(CartItemEntry.TABLE_NAME, 
                CartItemEntry.COLUMN_NAME_USER_ID + " = ? AND " + CartItemEntry.COLUMN_NAME_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(productId)});
        return result > 0;
    }
    
    /**
     * Clear cart for user
     */
    public int clearCart(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        int result = db.delete(CartItemEntry.TABLE_NAME, CartItemEntry.COLUMN_NAME_USER_ID + " = ?", 
                              new String[]{String.valueOf(userId)});
        return result;
    }
    
    /**
     * Create a new order
     */
    public long createOrder(int userId, double totalAmount, double deliveryFee, String status, int addressId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            ContentValues values = new ContentValues();
            values.put(OrderEntry.COLUMN_NAME_USER_ID, userId);
            values.put(OrderEntry.COLUMN_NAME_TOTAL_AMOUNT, totalAmount);
            values.put(OrderEntry.COLUMN_NAME_DELIVERY_FEE, deliveryFee);
            values.put(OrderEntry.COLUMN_NAME_STATUS, status);
            values.put(OrderEntry.COLUMN_NAME_ADDRESS_ID, addressId);
            
            // Inserting Row
            long orderId = db.insert(OrderEntry.TABLE_NAME, null, values);
            return orderId;
        } catch (Exception e) {
            Log.e(TAG, "Error creating order", e);
            return -1;
        }
    }
    
    /**
     * Add order item
     */
    public long addOrderItem(int orderId, int productId, int quantity, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            ContentValues values = new ContentValues();
            values.put(OrderItemEntry.COLUMN_NAME_ORDER_ID, orderId);
            values.put(OrderItemEntry.COLUMN_NAME_PRODUCT_ID, productId);
            values.put(OrderItemEntry.COLUMN_NAME_QUANTITY, quantity);
            values.put(OrderItemEntry.COLUMN_NAME_PRICE, price);
            
            // Inserting Row
            long orderItemId = db.insert(OrderItemEntry.TABLE_NAME, null, values);
            return orderItemId;
        } catch (Exception e) {
            Log.e(TAG, "Error adding order item", e);
            return -1;
        }
    }

    // ==================== PRODUCT METHODS ====================
    
    /**
     * Get all products with category information
     */
    public java.util.List<com.sunit.groceryplus.models.Product> getAllProducts() {
        java.util.List<com.sunit.groceryplus.models.Product> products = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT p.*, c." + CategoryEntry.COLUMN_NAME_CATEGORY_NAME + 
                      ", v." + DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_NAME +
                      ", (SELECT AVG(" + DatabaseContract.ReviewEntry.COLUMN_NAME_RATING + ") FROM " + DatabaseContract.ReviewEntry.TABLE_NAME + 
                      " WHERE " + DatabaseContract.ReviewEntry.COLUMN_NAME_PRODUCT_ID + " = p." + ProductEntry.COLUMN_NAME_PRODUCT_ID + ") as avg_rating" +
                      " FROM " + ProductEntry.TABLE_NAME + " p " +
                      " LEFT JOIN " + CategoryEntry.TABLE_NAME + " c " +
                      " ON p." + ProductEntry.COLUMN_NAME_CATEGORY_ID + " = c." + CategoryEntry.COLUMN_NAME_CATEGORY_ID +
                      " LEFT JOIN " + DatabaseContract.VendorEntry.TABLE_NAME + " v " +
                      " ON p." + ProductEntry.COLUMN_NAME_VENDOR_ID + " = v." + DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_ID;
        
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int productId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRODUCT_ID));
                    String productName = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRODUCT_NAME));
                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_CATEGORY_ID));
                    String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(CategoryEntry.COLUMN_NAME_CATEGORY_NAME));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRICE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_DESCRIPTION));
                    String image = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_IMAGE));
                    int stock = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_STOCK));
                    double avgRating = cursor.getDouble(cursor.getColumnIndexOrThrow("avg_rating"));
                    int vendorId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_VENDOR_ID));
                    String vendorName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_NAME));
                    
                    com.sunit.groceryplus.models.Product product = new com.sunit.groceryplus.models.Product(
                        productId, productName, categoryId, categoryName, price, description, image, avgRating, stock, vendorId, vendorName != null ? vendorName : "General Store"
                    );
                    products.add(product);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing product", e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return products;
    }
    
    /**
     * Get product by ID
     */
    public com.sunit.groceryplus.models.Product getProductById(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT p.*, c." + CategoryEntry.COLUMN_NAME_CATEGORY_NAME + 
                      ", v." + DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_NAME +
                      ", (SELECT AVG(" + DatabaseContract.ReviewEntry.COLUMN_NAME_RATING + ") FROM " + DatabaseContract.ReviewEntry.TABLE_NAME + 
                      " WHERE " + DatabaseContract.ReviewEntry.COLUMN_NAME_PRODUCT_ID + " = p." + ProductEntry.COLUMN_NAME_PRODUCT_ID + ") as avg_rating" +
                      " FROM " + ProductEntry.TABLE_NAME + " p " +
                      " LEFT JOIN " + CategoryEntry.TABLE_NAME + " c " +
                      " ON p." + ProductEntry.COLUMN_NAME_CATEGORY_ID + " = c." + CategoryEntry.COLUMN_NAME_CATEGORY_ID +
                      " LEFT JOIN " + DatabaseContract.VendorEntry.TABLE_NAME + " v " +
                      " ON p." + ProductEntry.COLUMN_NAME_VENDOR_ID + " = v." + DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_ID +
                      " WHERE p." + ProductEntry.COLUMN_NAME_PRODUCT_ID + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(productId)});
        
        if (cursor != null && cursor.moveToFirst()) {
            try {
                String productName = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRODUCT_NAME));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_CATEGORY_ID));
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(CategoryEntry.COLUMN_NAME_CATEGORY_NAME));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRICE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_DESCRIPTION));
                String image = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_IMAGE));
                int stock = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_STOCK));
                double avgRating = cursor.getDouble(cursor.getColumnIndexOrThrow("avg_rating"));
                int vendorId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_VENDOR_ID));
                String vendorName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_NAME));
                
                return new com.sunit.groceryplus.models.Product(
                    productId, productName, categoryId, categoryName, price, description, image, avgRating, stock, vendorId, vendorName != null ? vendorName : "General Store"
                );
            } catch (Exception e) {
                Log.e(TAG, "Error getting product by ID", e);
            } finally {
                cursor.close();
            }
        } else if (cursor != null) {
            cursor.close();
        }
        
        return null;
    }
    
    /**
     * Get products by category
     */
    public java.util.List<com.sunit.groceryplus.models.Product> getProductsByCategory(int categoryId) {
        java.util.List<com.sunit.groceryplus.models.Product> products = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT p.*, c." + CategoryEntry.COLUMN_NAME_CATEGORY_NAME + 
                      ", v." + DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_NAME +
                      ", (SELECT AVG(" + DatabaseContract.ReviewEntry.COLUMN_NAME_RATING + ") FROM " + DatabaseContract.ReviewEntry.TABLE_NAME + 
                      " WHERE " + DatabaseContract.ReviewEntry.COLUMN_NAME_PRODUCT_ID + " = p." + ProductEntry.COLUMN_NAME_PRODUCT_ID + ") as avg_rating" +
                      " FROM " + ProductEntry.TABLE_NAME + " p " +
                      " LEFT JOIN " + CategoryEntry.TABLE_NAME + " c " +
                      " ON p." + ProductEntry.COLUMN_NAME_CATEGORY_ID + " = c." + CategoryEntry.COLUMN_NAME_CATEGORY_ID +
                      " LEFT JOIN " + DatabaseContract.VendorEntry.TABLE_NAME + " v " +
                      " ON p." + ProductEntry.COLUMN_NAME_VENDOR_ID + " = v." + DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_ID +
                      " WHERE p." + ProductEntry.COLUMN_NAME_CATEGORY_ID + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int productId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRODUCT_ID));
                    String productName = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRODUCT_NAME));
                    int catId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_CATEGORY_ID));
                    String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(CategoryEntry.COLUMN_NAME_CATEGORY_NAME));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRICE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_DESCRIPTION));
                    String image = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_IMAGE));
                    int stock = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_STOCK));
                    double avgRating = cursor.getDouble(cursor.getColumnIndexOrThrow("avg_rating"));
                    int vendorId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_VENDOR_ID));
                    String vendorName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_NAME));
                    
                    com.sunit.groceryplus.models.Product product = new com.sunit.groceryplus.models.Product(
                        productId, productName, catId, categoryName, price, description, image, avgRating, stock, vendorId, vendorName != null ? vendorName : "General Store"
                    );
                    products.add(product);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing product", e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return products;
    }
    
    /**
     * Search products by name
     */
    public java.util.List<com.sunit.groceryplus.models.Product> searchProducts(String query) {
        java.util.List<com.sunit.groceryplus.models.Product> products = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String searchQuery = "SELECT p.*, c." + CategoryEntry.COLUMN_NAME_CATEGORY_NAME + 
                            ", v." + DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_NAME +
                            " FROM " + ProductEntry.TABLE_NAME + " p " +
                            " LEFT JOIN " + CategoryEntry.TABLE_NAME + " c " +
                            " ON p." + ProductEntry.COLUMN_NAME_CATEGORY_ID + " = c." + CategoryEntry.COLUMN_NAME_CATEGORY_ID +
                            " LEFT JOIN " + DatabaseContract.VendorEntry.TABLE_NAME + " v " +
                            " ON p." + ProductEntry.COLUMN_NAME_VENDOR_ID + " = v." + DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_ID +
                            " WHERE p." + ProductEntry.COLUMN_NAME_PRODUCT_NAME + " LIKE ?";
        
        Cursor cursor = db.rawQuery(searchQuery, new String[]{"%" + query + "%"});
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int productId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRODUCT_ID));
                    String productName = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRODUCT_NAME));
                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_CATEGORY_ID));
                    String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(CategoryEntry.COLUMN_NAME_CATEGORY_NAME));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRICE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_DESCRIPTION));
                    String image = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_IMAGE));
                    int stock = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_STOCK));
                    int vendorId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_VENDOR_ID));
                    String vendorName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_NAME));
                    
                    com.sunit.groceryplus.models.Product product = new com.sunit.groceryplus.models.Product(
                        productId, productName, categoryId, categoryName, price, description, image, stock, vendorId, vendorName != null ? vendorName : "General Store"
                    );
                    products.add(product);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing product", e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return products;
    }
    
    /**
     * Update product
     */
    public boolean updateProduct(int productId, String productName, int categoryId, double price, String description, String image, int stockQuantity, int vendorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, productName);
            values.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, categoryId);
            values.put(ProductEntry.COLUMN_NAME_PRICE, price);
            values.put(ProductEntry.COLUMN_NAME_DESCRIPTION, description);
            values.put(ProductEntry.COLUMN_NAME_IMAGE, image);
            values.put(ProductEntry.COLUMN_NAME_STOCK, stockQuantity);
            values.put(ProductEntry.COLUMN_NAME_VENDOR_ID, vendorId);
            
            int result = db.update(ProductEntry.TABLE_NAME, values, 
                                  ProductEntry.COLUMN_NAME_PRODUCT_ID + " = ?", 
                                  new String[]{String.valueOf(productId)});
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating product", e);
            return false;
        }
    }
    
    /**
     * Delete product
     */
    public boolean deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            int result = db.delete(ProductEntry.TABLE_NAME, 
                                  ProductEntry.COLUMN_NAME_PRODUCT_ID + " = ?", 
                                  new String[]{String.valueOf(productId)});
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting product", e);
            return false;
        }
    }
    
    // ==================== CATEGORY METHODS ====================
    
    /**
     * Get all categories
     */
    public java.util.List<com.sunit.groceryplus.models.Category> getAllCategories() {
        java.util.List<com.sunit.groceryplus.models.Category> categories = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + CategoryEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(CategoryEntry.COLUMN_NAME_CATEGORY_ID));
                    String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(CategoryEntry.COLUMN_NAME_CATEGORY_NAME));
                    String categoryDescription = cursor.getString(cursor.getColumnIndexOrThrow(CategoryEntry.COLUMN_NAME_CATEGORY_DESCRIPTION));
                    
                    com.sunit.groceryplus.models.Category category = new com.sunit.groceryplus.models.Category(
                        categoryId, categoryName, categoryDescription
                    );
                    categories.add(category);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing category", e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return categories;
    }
    
    /**
     * Get category by ID
     */
    public com.sunit.groceryplus.models.Category getCategoryById(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + CategoryEntry.TABLE_NAME + 
                      " WHERE " + CategoryEntry.COLUMN_NAME_CATEGORY_ID + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});
        
        if (cursor != null && cursor.moveToFirst()) {
            try {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(CategoryEntry.COLUMN_NAME_CATEGORY_ID));
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(CategoryEntry.COLUMN_NAME_CATEGORY_NAME));
                String categoryDescription = cursor.getString(cursor.getColumnIndexOrThrow(CategoryEntry.COLUMN_NAME_CATEGORY_DESCRIPTION));
                
                cursor.close();
                return new com.sunit.groceryplus.models.Category(id, categoryName, categoryDescription);
            } catch (Exception e) {
                Log.e(TAG, "Error getting category by ID", e);
                cursor.close();
            }
        } else if (cursor != null) {
            cursor.close();
        }
        
        return null;
    }
    
    /**
     * Update category
     */
    public boolean updateCategory(int categoryId, String categoryName, String categoryDescription) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            ContentValues values = new ContentValues();
            values.put(CategoryEntry.COLUMN_NAME_CATEGORY_NAME, categoryName);
            values.put(CategoryEntry.COLUMN_NAME_CATEGORY_DESCRIPTION, categoryDescription);
            
            int result = db.update(CategoryEntry.TABLE_NAME, values, 
                                  CategoryEntry.COLUMN_NAME_CATEGORY_ID + " = ?", 
                                  new String[]{String.valueOf(categoryId)});
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating category", e);
            return false;
        }
    }
    
    /**
     * Delete category
     */
    public boolean deleteCategory(int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            int result = db.delete(CategoryEntry.TABLE_NAME, 
                                  CategoryEntry.COLUMN_NAME_CATEGORY_ID + " = ?", 
                                  new String[]{String.valueOf(categoryId)});
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting category", e);
            return false;
        }
    }
    
    // ==================== CART METHODS ====================
    
    /**
     * Get cart items with product details
     */
    public java.util.List<com.sunit.groceryplus.models.CartItem> getCartItemsWithDetails(int userId) {
        java.util.List<com.sunit.groceryplus.models.CartItem> cartItems = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT c.*, p." + ProductEntry.COLUMN_NAME_PRODUCT_NAME + ", p." + ProductEntry.COLUMN_NAME_PRICE + ", p." + ProductEntry.COLUMN_NAME_IMAGE +
                      " FROM " + CartItemEntry.TABLE_NAME + " c " +
                      " JOIN " + ProductEntry.TABLE_NAME + " p " +
                      " ON c." + CartItemEntry.COLUMN_NAME_PRODUCT_ID + " = p." + ProductEntry.COLUMN_NAME_PRODUCT_ID +
                      " WHERE c." + CartItemEntry.COLUMN_NAME_USER_ID + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int cartId = cursor.getInt(cursor.getColumnIndexOrThrow(CartItemEntry.COLUMN_NAME_CART_ID));
                    int productId = cursor.getInt(cursor.getColumnIndexOrThrow(CartItemEntry.COLUMN_NAME_PRODUCT_ID));
                    String productName = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRODUCT_NAME));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRICE));
                    int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(CartItemEntry.COLUMN_NAME_QUANTITY));
                    String image = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_IMAGE));
                    
                    com.sunit.groceryplus.models.CartItem cartItem = new com.sunit.groceryplus.models.CartItem(
                        cartId, userId, productId, productName, price, quantity, image
                    );
                    cartItems.add(cartItem);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing cart item", e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return cartItems;
    }
    
    /**
     * Update cart item quantity
     */
    public boolean updateCartQuantity(int cartId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            ContentValues values = new ContentValues();
            values.put(CartItemEntry.COLUMN_NAME_QUANTITY, quantity);
            
            int result = db.update(CartItemEntry.TABLE_NAME, values, 
                                  CartItemEntry.COLUMN_NAME_CART_ID + " = ?", 
                                  new String[]{String.valueOf(cartId)});
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating cart quantity", e);
            return false;
        }
    }
    
    /**
     * Get cart total
     */
    public double getCartTotal(int userId) {
        double total = 0.0;
        java.util.List<com.sunit.groceryplus.models.CartItem> items = getCartItemsWithDetails(userId);
        
        for (com.sunit.groceryplus.models.CartItem item : items) {
            total += item.getSubtotal();
        }
        
        return total;
    }
    
    // ==================== ORDER METHODS ====================
    
    /**
     * Get all orders (for admin)
     */
    public java.util.List<com.sunit.groceryplus.models.Order> getAllOrders() {
        java.util.List<com.sunit.groceryplus.models.Order> orders = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT o.*, u." + UserEntry.COLUMN_NAME_USER_NAME + ", u." + UserEntry.COLUMN_NAME_USER_EMAIL + ", u." + UserEntry.COLUMN_NAME_USER_PHONE +
                      ", p." + DatabaseContract.PaymentEntry.COLUMN_NAME_PAYMENT_ID + ", p." + DatabaseContract.PaymentEntry.COLUMN_NAME_PAYMENT_METHOD +
                      ", dp." + DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_NAME + " as delivery_person_name" +
                      " FROM " + OrderEntry.TABLE_NAME + " o " +
                      " JOIN " + UserEntry.TABLE_NAME + " u " +
                      " ON o." + OrderEntry.COLUMN_NAME_USER_ID + " = u." + UserEntry.COLUMN_NAME_USER_ID +
                      " LEFT JOIN " + DatabaseContract.PaymentEntry.TABLE_NAME + " p " +
                      " ON o." + OrderEntry.COLUMN_NAME_ORDER_ID + " = p." + DatabaseContract.PaymentEntry.COLUMN_NAME_ORDER_ID +
                      " LEFT JOIN " + DatabaseContract.DeliveryPersonEntry.TABLE_NAME + " dp " +
                      " ON o." + OrderEntry.COLUMN_NAME_DELIVERY_PERSON_ID + " = dp." + DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_PERSON_ID +
                      " ORDER BY o." + OrderEntry.COLUMN_NAME_ORDER_DATE + " DESC";
        
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int orderId = cursor.getInt(cursor.getColumnIndexOrThrow(OrderEntry.COLUMN_NAME_ORDER_ID));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(OrderEntry.COLUMN_NAME_USER_ID));
                    String userName = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USER_NAME));
                    String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USER_EMAIL));
                    String userPhone = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USER_PHONE));
                    double totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(OrderEntry.COLUMN_NAME_TOTAL_AMOUNT));
                    double deliveryFee = cursor.getDouble(cursor.getColumnIndexOrThrow(OrderEntry.COLUMN_NAME_DELIVERY_FEE));
                    String status = cursor.getString(cursor.getColumnIndexOrThrow(OrderEntry.COLUMN_NAME_STATUS));
                    String orderDate = cursor.getString(cursor.getColumnIndexOrThrow(OrderEntry.COLUMN_NAME_ORDER_DATE));
                    String shippedDate = cursor.getString(cursor.getColumnIndexOrThrow(OrderEntry.COLUMN_NAME_SHIPPED_DATE));
                    
                    int deliveryPersonIdIndex = cursor.getColumnIndex(OrderEntry.COLUMN_NAME_DELIVERY_PERSON_ID);
                    int deliveryPersonId = (deliveryPersonIdIndex != -1) ? cursor.getInt(deliveryPersonIdIndex) : 0;
                    
                    String deliveryPersonName = null;
                    int dpNameIndex = cursor.getColumnIndex("delivery_person_name");
                    if (dpNameIndex != -1) {
                         deliveryPersonName = cursor.getString(dpNameIndex);
                    }

                    // Check if payment exists (LEFT JOIN will return null if no payment)
                    int paymentIdIndex = cursor.getColumnIndex(DatabaseContract.PaymentEntry.COLUMN_NAME_PAYMENT_ID);
                    boolean paymentReceived = false;
                    String paymentMethod = null;
                    if (paymentIdIndex >= 0 && !cursor.isNull(paymentIdIndex)) {
                        paymentReceived = true;
                        int paymentMethodIndex = cursor.getColumnIndex(DatabaseContract.PaymentEntry.COLUMN_NAME_PAYMENT_METHOD);
                        if (paymentMethodIndex >= 0) {
                            paymentMethod = cursor.getString(paymentMethodIndex);
                        }
                    }
                    
                    com.sunit.groceryplus.models.Order order = new com.sunit.groceryplus.models.Order(
                        orderId, userId, userName, userEmail, userPhone, totalAmount, deliveryFee, status, orderDate
                    );
                    order.setShippedDate(shippedDate);
                    order.setPaymentReceived(paymentReceived);
                    order.setPaymentMethod(paymentMethod);
                    order.setDeliveryPersonId(deliveryPersonId);
                    order.setDeliveryPersonName(deliveryPersonName);
                    
                    orders.add(order);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing order", e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return orders;
    }
    
    /**
     * Get orders by user
     */
    public java.util.List<com.sunit.groceryplus.models.Order> getOrdersByUser(int userId) {
        java.util.List<com.sunit.groceryplus.models.Order> orders = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT o.*, u." + UserEntry.COLUMN_NAME_USER_NAME + ", u." + UserEntry.COLUMN_NAME_USER_EMAIL + ", u." + UserEntry.COLUMN_NAME_USER_PHONE +
                      ", dp." + DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_NAME + " as delivery_person_name" +
                      " FROM " + OrderEntry.TABLE_NAME + " o " +
                      " JOIN " + UserEntry.TABLE_NAME + " u " +
                      " ON o." + OrderEntry.COLUMN_NAME_USER_ID + " = u." + UserEntry.COLUMN_NAME_USER_ID +
                      " LEFT JOIN " + DatabaseContract.DeliveryPersonEntry.TABLE_NAME + " dp " +
                      " ON o." + OrderEntry.COLUMN_NAME_DELIVERY_PERSON_ID + " = dp." + DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_PERSON_ID +
                      " WHERE o." + OrderEntry.COLUMN_NAME_USER_ID + " = ?" +
                      " ORDER BY o." + OrderEntry.COLUMN_NAME_ORDER_DATE + " DESC";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int orderId = cursor.getInt(cursor.getColumnIndexOrThrow(OrderEntry.COLUMN_NAME_ORDER_ID));
                    String userName = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USER_NAME));
                    String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USER_EMAIL));
                    String userPhone = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USER_PHONE));
                    double totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(OrderEntry.COLUMN_NAME_TOTAL_AMOUNT));
                    double deliveryFee = cursor.getDouble(cursor.getColumnIndexOrThrow(OrderEntry.COLUMN_NAME_DELIVERY_FEE));
                    String status = cursor.getString(cursor.getColumnIndexOrThrow(OrderEntry.COLUMN_NAME_STATUS));
                    String orderDate = cursor.getString(cursor.getColumnIndexOrThrow(OrderEntry.COLUMN_NAME_ORDER_DATE));
                    String shippedDate = cursor.getString(cursor.getColumnIndexOrThrow(OrderEntry.COLUMN_NAME_SHIPPED_DATE));

                    int deliveryPersonIdIndex = cursor.getColumnIndex(OrderEntry.COLUMN_NAME_DELIVERY_PERSON_ID);
                    int deliveryPersonId = (deliveryPersonIdIndex != -1) ? cursor.getInt(deliveryPersonIdIndex) : 0;
                    
                    String deliveryPersonName = null;
                    int dpNameIndex = cursor.getColumnIndex("delivery_person_name");
                    if (dpNameIndex != -1) {
                         deliveryPersonName = cursor.getString(dpNameIndex);
                    }
                    
                    com.sunit.groceryplus.models.Order order = new com.sunit.groceryplus.models.Order(
                        orderId, userId, userName, userEmail, userPhone, totalAmount, deliveryFee, status, orderDate
                    );
                    order.setShippedDate(shippedDate);
                    order.setDeliveryPersonId(deliveryPersonId);
                    order.setDeliveryPersonName(deliveryPersonName);
                    
                    orders.add(order);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing order", e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return orders;
    }
    
    /**
     * Get order items for an order
     */
    public java.util.List<com.sunit.groceryplus.models.OrderItem> getOrderItems(int orderId) {
        java.util.List<com.sunit.groceryplus.models.OrderItem> orderItems = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT oi.*, p." + ProductEntry.COLUMN_NAME_PRODUCT_NAME + ", p." + ProductEntry.COLUMN_NAME_IMAGE +
                      " FROM " + OrderItemEntry.TABLE_NAME + " oi " +
                      " JOIN " + ProductEntry.TABLE_NAME + " p " +
                      " ON oi." + OrderItemEntry.COLUMN_NAME_PRODUCT_ID + " = p." + ProductEntry.COLUMN_NAME_PRODUCT_ID +
                      " WHERE oi." + OrderItemEntry.COLUMN_NAME_ORDER_ID + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int orderItemId = cursor.getInt(cursor.getColumnIndexOrThrow(OrderItemEntry.COLUMN_NAME_ORDER_ITEM_ID));
                    int productId = cursor.getInt(cursor.getColumnIndexOrThrow(OrderItemEntry.COLUMN_NAME_PRODUCT_ID));
                    String productName = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRODUCT_NAME));
                    int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(OrderItemEntry.COLUMN_NAME_QUANTITY));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(OrderItemEntry.COLUMN_NAME_PRICE));
                    String image = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_IMAGE));
                    
                    com.sunit.groceryplus.models.OrderItem orderItem = new com.sunit.groceryplus.models.OrderItem(
                        orderItemId, orderId, productId, productName, quantity, price, image
                    );
                    orderItems.add(orderItem);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing order item", e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return orderItems;
    }
    
    /**
     * Update order status
     */
    public boolean updateOrderStatus(int orderId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            ContentValues values = new ContentValues();
            values.put(OrderEntry.COLUMN_NAME_STATUS, status);
            
            if ("shipped".equalsIgnoreCase(status)) {
                values.put(OrderEntry.COLUMN_NAME_SHIPPED_DATE, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date()));
            }
            
            int result = db.update(OrderEntry.TABLE_NAME, values, 
                                  OrderEntry.COLUMN_NAME_ORDER_ID + " = ?", 
                                  new String[]{String.valueOf(orderId)});
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating order status", e);
            return false;
        }
    }

    public boolean assignDeliveryPerson(int orderId, int deliveryPersonId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(OrderEntry.COLUMN_NAME_DELIVERY_PERSON_ID, deliveryPersonId);
        
        int rows = db.update(OrderEntry.TABLE_NAME, values, OrderEntry.COLUMN_NAME_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)});
        return rows > 0;
    }

    
    // ==================== SAMPLE DATA METHODS ====================
    
    /**
     * Insert sample categories and products
     */
    public void insertSampleData() {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            // Check if data already exists
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + CategoryEntry.TABLE_NAME, null);
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                cursor.close();
                if (count > 0) {
                    Log.d(TAG, "Sample data already exists");
                    return;
                }
            }
            Log.d(TAG, "Starting sample data insertion");
            
            // Insert default admin user if not exists
            Log.d(TAG, "Checking if admin user exists");
            if (!isUserExists("admin@gmail.com")) {
                Log.d(TAG, "Creating default admin user");
                long adminId = addUser("Admin User", "admin@gmail.com", "9876543210", "admin123", "admin");
                if (adminId != -1) {
                    Log.d(TAG, "Default admin user created with ID: " + adminId);
                } else {
                    Log.e(TAG, "Failed to create default admin user");
                }
            } else {
                Log.d(TAG, "Admin user already exists");
            }
            
            // Insert categories
            long fruitsId = addCategory("Fruits", "Fresh fruits");
            long vegetablesId = addCategory("Vegetables", "Fresh vegetables");
            long dairyId = addCategory("Dairy", "Milk and dairy products");
            long bakeryId = addCategory("Bakery", "Bread and baked goods");
            long meatId = addCategory("Meat", "Fresh meat and poultry");
            
            // Insert products linked to vendors
addProduct("Apple", (int)fruitsId, 120.0, "Fresh red apples", "apple", 200, 1);
            addProduct("Banana", (int)fruitsId, 60.0, "Fresh bananas", "banana", 150, 1);
            addProduct("Orange", (int)fruitsId, 100.0, "Fresh oranges", "orange", 180, 2);
            addProduct("Mango", (int)fruitsId, 150.0, "Sweet mangoes", "mango", 120, 2);
            
            addProduct("Tomato", (int)vegetablesId, 40.0, "Fresh tomatoes", "tomato", 200, 3);
            addProduct("Potato", (int)vegetablesId, 30.0, "Fresh potatoes", "potato", 250, 3);
            addProduct("Onion", (int)vegetablesId, 35.0, "Fresh onions", "onion", 220, 4);
            addProduct("Carrot", (int)vegetablesId, 50.0, "Fresh carrots", "carrot", 180, 4);
            
            addProduct("Milk", (int)dairyId, 70.0, "Fresh milk", "milk", 100, 5);
            addProduct("Cheese", (int)dairyId, 200.0, "Cheddar cheese", "cheese", 80, 5);
            addProduct("Yogurt", (int)dairyId, 80.0, "Plain yogurt", "yogurt", 90, 1);
            
            addProduct("Bread", (int)bakeryId, 45.0, "White bread", "bread", 60, 2);
            addProduct("Croissant", (int)bakeryId, 100.0, "Butter croissant", "croissant", 50, 2);
            
            addProduct("Chicken", (int)meatId, 350.0, "Fresh chicken", "chicken", 75, 3);
            addProduct("Mutton", (int)meatId, 800.0, "Fresh mutton", "mutton", 40, 4);
            
            // No default customer user creation - users must register manually
            
            Log.d(TAG, "Sample data inserted successfully");
            insertSampleVendors();
        } catch (Exception e) {
            Log.e(TAG, "Error inserting sample data", e);
        }
    }

    /**
     * Ensure all products have stock quantities
     * Updates products with 0 or null stock to have default stock of 100
     */
    public void ensureAllProductsHaveStock() {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            // Update products with 0 or null stock to have default stock of 100
            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_NAME_STOCK, 100);
            
            int updated = db.update(ProductEntry.TABLE_NAME, values, 
                    ProductEntry.COLUMN_NAME_STOCK + " <= 0 OR " + ProductEntry.COLUMN_NAME_STOCK + " IS NULL", 
                    null);
            
            if (updated > 0) {
                Log.d(TAG, "Updated " + updated + " products with default stock quantity of 100");
            } else {
                Log.d(TAG, "All products already have stock quantities");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error ensuring products have stock", e);
        }
    }

    private void insertSampleVendors() {
        if (getAllVendors().isEmpty()) {
            addVendor("Fresh Mart KTM", "Durbar Marg, Kathmandu", 27.7120, 85.3210, "vendor_icon", 4.5);
            addVendor("KTM Food Store", "Lazimpat, Kathmandu", 27.7250, 85.3200, "vendor_icon", 4.3);
        }
    }
    

    // Analytics Methods

    public double getTotalRevenue() {
        SQLiteDatabase db = this.getReadableDatabase();
        double totalRevenue = 0;
        String query = "SELECT SUM(" + OrderEntry.COLUMN_NAME_TOTAL_AMOUNT + ") FROM " + OrderEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            totalRevenue = cursor.getDouble(0);
        }
        cursor.close();
        return totalRevenue;
    }

    public int getOrderCountByStatus(String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        String query = "SELECT COUNT(*) FROM " + OrderEntry.TABLE_NAME + " WHERE " + OrderEntry.COLUMN_NAME_STATUS + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{status});
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
    
    public int getTotalOrdersCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        String query = "SELECT COUNT(*) FROM " + OrderEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getTotalProductsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        String query = "SELECT COUNT(*) FROM " + ProductEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getTotalCustomersCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        String query = "SELECT COUNT(*) FROM " + UserEntry.TABLE_NAME + " WHERE " + UserEntry.COLUMN_NAME_USER_TYPE + " != 'admin'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // ==================== PROMOTION METHODS ====================

    public long addPromotion(String code, double discount, String validUntil, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PromotionEntry.COLUMN_NAME_CODE, code);
        values.put(DatabaseContract.PromotionEntry.COLUMN_NAME_DISCOUNT_PERCENTAGE, discount);
        values.put(DatabaseContract.PromotionEntry.COLUMN_NAME_VALID_UNTIL, validUntil);
        values.put(DatabaseContract.PromotionEntry.COLUMN_NAME_IMAGE_URL, imageUrl);
        values.put(DatabaseContract.PromotionEntry.COLUMN_NAME_IS_ACTIVE, 1);
        return db.insert(DatabaseContract.PromotionEntry.TABLE_NAME, null, values);
    }

    public com.sunit.groceryplus.models.Promotion getPromotionByCode(String code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.PromotionEntry.TABLE_NAME, null,
                DatabaseContract.PromotionEntry.COLUMN_NAME_CODE + " = ? AND " +
                DatabaseContract.PromotionEntry.COLUMN_NAME_IS_ACTIVE + " = 1",
                new String[]{code}, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PromotionEntry.COLUMN_NAME_PROMO_ID));
            double discount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.PromotionEntry.COLUMN_NAME_DISCOUNT_PERCENTAGE));
            String validUntil = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PromotionEntry.COLUMN_NAME_VALID_UNTIL));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PromotionEntry.COLUMN_NAME_IMAGE_URL));
            boolean isActive = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PromotionEntry.COLUMN_NAME_IS_ACTIVE)) == 1;
            cursor.close();
            return new com.sunit.groceryplus.models.Promotion(id, code, discount, validUntil, imageUrl, isActive);
        }
        if (cursor != null) cursor.close();
        return null;
    }
    
    public Cursor getAllPromotions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(DatabaseContract.PromotionEntry.TABLE_NAME, null, null, null, null, null, null);
    }

    public boolean deletePromotion(int promoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DatabaseContract.PromotionEntry.TABLE_NAME, 
                DatabaseContract.PromotionEntry.COLUMN_NAME_PROMO_ID + " = ?", 
                new String[]{String.valueOf(promoId)}) > 0;
    }

    // ==================== REVIEW METHODS ====================
    
    public Cursor getAllReviews() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT r.*, p." + DatabaseContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME + ", u." + DatabaseContract.UserEntry.COLUMN_NAME_USER_NAME +
                       " FROM " + DatabaseContract.ReviewEntry.TABLE_NAME + " r " +
                       " LEFT JOIN " + DatabaseContract.ProductEntry.TABLE_NAME + " p ON r." + DatabaseContract.ReviewEntry.COLUMN_NAME_PRODUCT_ID + " = p." + DatabaseContract.ProductEntry.COLUMN_NAME_PRODUCT_ID +
                       " LEFT JOIN " + DatabaseContract.UserEntry.TABLE_NAME + " u ON r." + DatabaseContract.ReviewEntry.COLUMN_NAME_USER_ID + " = u." + DatabaseContract.UserEntry.COLUMN_NAME_USER_ID;
        return db.rawQuery(query, null);
    }
    
    public boolean deleteReview(int reviewId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DatabaseContract.ReviewEntry.TABLE_NAME,
                DatabaseContract.ReviewEntry.COLUMN_NAME_REVIEW_ID + " = ?",
                new String[]{String.valueOf(reviewId)}) > 0;
    }

    // ==================== DELIVERY PERSONNEL METHODS ====================
    
    public long addDeliveryPerson(String name, String phone, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_NAME, name);
        values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_PHONE, phone);
        values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_STATUS, status);
        return db.insert(DatabaseContract.DeliveryPersonEntry.TABLE_NAME, null, values);
    }
    
    public Cursor getAllDeliveryPersonnel() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(DatabaseContract.DeliveryPersonEntry.TABLE_NAME, null, null, null, null, null, null);
    }
    
    public boolean updateDeliveryPersonStatus(int personId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_STATUS, status);
        return db.update(DatabaseContract.DeliveryPersonEntry.TABLE_NAME, values,
                DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_PERSON_ID + " = ?",
                new String[]{String.valueOf(personId)}) > 0;
    }
    
    // ==================== PAYMENT METHODS ====================
    
    public long addPayment(int orderId, double amount, String paymentMethod, String transactionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.PaymentEntry.COLUMN_NAME_ORDER_ID, orderId);
            values.put(DatabaseContract.PaymentEntry.COLUMN_NAME_AMOUNT, amount);
            values.put(DatabaseContract.PaymentEntry.COLUMN_NAME_PAYMENT_METHOD, paymentMethod);
            values.put(DatabaseContract.PaymentEntry.COLUMN_NAME_TRANSACTION_ID, transactionId);
            
            // Inserting Row
            long paymentId = db.insert(DatabaseContract.PaymentEntry.TABLE_NAME, null, values);
            return paymentId;
        } catch (Exception e) {
            Log.e(TAG, "Error adding payment", e);
            return -1;
        }
    }
    
    public Cursor getAllPayments() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(DatabaseContract.PaymentEntry.TABLE_NAME, null, null, null, null, null, DatabaseContract.PaymentEntry.COLUMN_NAME_PAYMENT_DATE + " DESC");
    }

    // ==================== MESSAGING METHODS ====================
    
    // ==================== MESSAGING METHODS ====================
    
    public Cursor getAllMessages() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT m.*, s." + DatabaseContract.UserEntry.COLUMN_NAME_USER_NAME + " as sender_name" +
                       " FROM " + DatabaseContract.MessageEntry.TABLE_NAME + " m " +
                       " LEFT JOIN " + DatabaseContract.UserEntry.TABLE_NAME + " s ON m." + DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID + " = s." + DatabaseContract.UserEntry.COLUMN_NAME_USER_ID +
                       " ORDER BY " + DatabaseContract.MessageEntry.COLUMN_NAME_CREATED_AT + " DESC";
        return db.rawQuery(query, null);
    }

    public long sendMessage(int senderId, int receiverId, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID, senderId);
        values.put(DatabaseContract.MessageEntry.COLUMN_NAME_RECEIVER_ID, receiverId);
        values.put(DatabaseContract.MessageEntry.COLUMN_NAME_MESSAGE_TEXT, message);
        return db.insert(DatabaseContract.MessageEntry.TABLE_NAME, null, values);
    }

    public Cursor getConversation(int userId1, int userId2) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Get messages between two users (sent by either)
        String query = "SELECT m.*, s." + DatabaseContract.UserEntry.COLUMN_NAME_USER_NAME + " as sender_name" +
                       " FROM " + DatabaseContract.MessageEntry.TABLE_NAME + " m " +
                       " LEFT JOIN " + DatabaseContract.UserEntry.TABLE_NAME + " s ON m." + DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID + " = s." + DatabaseContract.UserEntry.COLUMN_NAME_USER_ID +
                       " WHERE (m." + DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID + " = ? AND m." + DatabaseContract.MessageEntry.COLUMN_NAME_RECEIVER_ID + " = ?) OR " +
                       "(m." + DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID + " = ? AND m." + DatabaseContract.MessageEntry.COLUMN_NAME_RECEIVER_ID + " = ?) " +
                       " ORDER BY " + DatabaseContract.MessageEntry.COLUMN_NAME_CREATED_AT + " ASC"; // Oldest first for chat
        
        return db.rawQuery(query, new String[]{String.valueOf(userId1), String.valueOf(userId2), String.valueOf(userId2), String.valueOf(userId1)});
    }

    public int getAdminId() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + DatabaseContract.UserEntry.COLUMN_NAME_USER_ID + 
                      " FROM " + DatabaseContract.UserEntry.TABLE_NAME + 
                      " WHERE " + DatabaseContract.UserEntry.COLUMN_NAME_USER_TYPE + " = 'admin' LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        int adminId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            adminId = cursor.getInt(0);
        }
        if (cursor != null) cursor.close();
        return adminId;
    }

    /**
     * Add a product review
     */
    public long addReview(int userId, int productId, float rating, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.ReviewEntry.COLUMN_NAME_USER_ID, userId);
            values.put(DatabaseContract.ReviewEntry.COLUMN_NAME_PRODUCT_ID, productId);
            values.put(DatabaseContract.ReviewEntry.COLUMN_NAME_RATING, (int) rating);
            values.put(DatabaseContract.ReviewEntry.COLUMN_NAME_COMMENT, comment);
            return db.insert(DatabaseContract.ReviewEntry.TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error adding review", e);
            return -1;
        }
    }

    /**
     * Get reviews for a specific product
     */
    public java.util.List<com.sunit.groceryplus.models.Review> getReviewsForProduct(int productId) {
        java.util.List<com.sunit.groceryplus.models.Review> reviews = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT r.*, u." + DatabaseContract.UserEntry.COLUMN_NAME_USER_NAME + 
                      ", p." + DatabaseContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME +
                      " FROM " + DatabaseContract.ReviewEntry.TABLE_NAME + " r " +
                      " JOIN " + DatabaseContract.UserEntry.TABLE_NAME + " u ON r." + DatabaseContract.ReviewEntry.COLUMN_NAME_USER_ID + " = u." + DatabaseContract.UserEntry.COLUMN_NAME_USER_ID +
                      " JOIN " + DatabaseContract.ProductEntry.TABLE_NAME + " p ON r." + DatabaseContract.ReviewEntry.COLUMN_NAME_PRODUCT_ID + " = p." + DatabaseContract.ProductEntry.COLUMN_NAME_PRODUCT_ID +
                      " WHERE r." + DatabaseContract.ReviewEntry.COLUMN_NAME_PRODUCT_ID + " = ?" +
                      " ORDER BY r." + DatabaseContract.ReviewEntry.COLUMN_NAME_CREATED_AT + " DESC";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(productId)});
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int reviewId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ReviewEntry.COLUMN_NAME_REVIEW_ID));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ReviewEntry.COLUMN_NAME_USER_ID));
                    String userName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_NAME_USER_NAME));
                    String productName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME));
                    int rating = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ReviewEntry.COLUMN_NAME_RATING));
                    String comment = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ReviewEntry.COLUMN_NAME_COMMENT));
                    String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ReviewEntry.COLUMN_NAME_CREATED_AT));
                    
                    reviews.add(new com.sunit.groceryplus.models.Review(reviewId, userId, userName, productId, productName, rating, comment, createdAt));
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing review", e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return reviews;
    }

    /**
     * Get average rating for a product
     */
    public float getAverageRatingForProduct(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT AVG(" + DatabaseContract.ReviewEntry.COLUMN_NAME_RATING + ") FROM " + DatabaseContract.ReviewEntry.TABLE_NAME +
                      " WHERE " + DatabaseContract.ReviewEntry.COLUMN_NAME_PRODUCT_ID + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(productId)});
        float avgRating = 0;
        if (cursor != null && cursor.moveToFirst()) {
            avgRating = cursor.getFloat(0);
            cursor.close();
        }
        return avgRating;
    }

    // ==================== NOTIFICATION METHODS ====================

    public long addNotification(int userId, String title, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.NotificationEntry.COLUMN_NAME_USER_ID, userId);
            values.put(DatabaseContract.NotificationEntry.COLUMN_NAME_TITLE, title);
            values.put(DatabaseContract.NotificationEntry.COLUMN_NAME_MESSAGE, message);
            return db.insert(DatabaseContract.NotificationEntry.TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error adding notification", e);
            return -1;
        }
    }

    public Cursor getUserNotifications(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseContract.NotificationEntry.TABLE_NAME +
                " WHERE " + DatabaseContract.NotificationEntry.COLUMN_NAME_USER_ID + " = ?" +
                " ORDER BY " + DatabaseContract.NotificationEntry.COLUMN_NAME_CREATED_AT + " DESC";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    public com.sunit.groceryplus.models.Order getOrderById(int orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT o.*, u." + UserEntry.COLUMN_NAME_USER_NAME + 
                      " FROM " + OrderEntry.TABLE_NAME + " o " +
                      " JOIN " + UserEntry.TABLE_NAME + " u ON o." + OrderEntry.COLUMN_NAME_USER_ID + " = u." + UserEntry.COLUMN_NAME_USER_ID +
                      " WHERE o." + OrderEntry.COLUMN_NAME_ORDER_ID + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});
        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(OrderEntry.COLUMN_NAME_USER_ID));
            String userName = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USER_NAME));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(OrderEntry.COLUMN_NAME_TOTAL_AMOUNT));
            double deliveryFee = cursor.getDouble(cursor.getColumnIndexOrThrow(OrderEntry.COLUMN_NAME_DELIVERY_FEE));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(OrderEntry.COLUMN_NAME_STATUS));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(OrderEntry.COLUMN_NAME_ORDER_DATE));
            String shippedDate = cursor.getString(cursor.getColumnIndexOrThrow(OrderEntry.COLUMN_NAME_SHIPPED_DATE));
            int addressId = cursor.getInt(cursor.getColumnIndexOrThrow(OrderEntry.COLUMN_NAME_ADDRESS_ID));
            cursor.close();
            com.sunit.groceryplus.models.Order order = new com.sunit.groceryplus.models.Order(orderId, userId, userName, amount, deliveryFee, status, date, addressId);
            order.setShippedDate(shippedDate);
            return order;
        }
        return null;
    }

    public com.sunit.groceryplus.models.Address getAddressById(int addressId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.AddressEntry.TABLE_NAME, null, 
                DatabaseContract.AddressEntry.COLUMN_NAME_ADDRESS_ID + " = ?", 
                new String[]{String.valueOf(addressId)}, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AddressEntry.COLUMN_NAME_USER_ID));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AddressEntry.COLUMN_NAME_TYPE));
            String full = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AddressEntry.COLUMN_NAME_FULL_ADDRESS));
            String landmark = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AddressEntry.COLUMN_NAME_LANDMARK));
            String city = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AddressEntry.COLUMN_NAME_CITY));
            String area = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AddressEntry.COLUMN_NAME_AREA));
            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.AddressEntry.COLUMN_NAME_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.AddressEntry.COLUMN_NAME_LONGITUDE));
            boolean isDefault = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AddressEntry.COLUMN_NAME_IS_DEFAULT)) == 1;
            cursor.close();
            return new com.sunit.groceryplus.models.Address(addressId, userId, type, full, landmark, city, area, latitude, longitude, isDefault);
        }
        return null;
    }

    // ==================== QUICK COMMERCE METHODS ====================

    /**
     * Decrement product stock when order is placed
     */
    public boolean decrementStock(int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + ProductEntry.TABLE_NAME + 
                       " SET " + ProductEntry.COLUMN_NAME_STOCK + " = " + ProductEntry.COLUMN_NAME_STOCK + " - " + quantity +
                       " WHERE " + ProductEntry.COLUMN_NAME_PRODUCT_ID + " = ? AND " + ProductEntry.COLUMN_NAME_STOCK + " >= ?";
        
        db.execSQL(query, new Object[]{productId, quantity});
        return true; // Simplified for now
    }

    /**
     * Add a new saved address
     */
    public long addAddress(int userId, String type, String fullAddress, String landmark, String city, String area, double latitude, double longitude, boolean isDefault) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.AddressEntry.COLUMN_NAME_USER_ID, userId);
        values.put(DatabaseContract.AddressEntry.COLUMN_NAME_TYPE, type);
        values.put(DatabaseContract.AddressEntry.COLUMN_NAME_FULL_ADDRESS, fullAddress);
        values.put(DatabaseContract.AddressEntry.COLUMN_NAME_LANDMARK, landmark);
        values.put(DatabaseContract.AddressEntry.COLUMN_NAME_CITY, city);
        values.put(DatabaseContract.AddressEntry.COLUMN_NAME_AREA, area);
        values.put(DatabaseContract.AddressEntry.COLUMN_NAME_LATITUDE, latitude);
        values.put(DatabaseContract.AddressEntry.COLUMN_NAME_LONGITUDE, longitude);
        values.put(DatabaseContract.AddressEntry.COLUMN_NAME_IS_DEFAULT, isDefault ? 1 : 0);
        
        return db.insert(DatabaseContract.AddressEntry.TABLE_NAME, null, values);
    }

    /**
     * Get all saved addresses for a user
     */
    public java.util.List<com.sunit.groceryplus.models.Address> getUserAddresses(int userId) {
        java.util.List<com.sunit.groceryplus.models.Address> addresses = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(DatabaseContract.AddressEntry.TABLE_NAME, null, 
                DatabaseContract.AddressEntry.COLUMN_NAME_USER_ID + " = ?", 
                new String[]{String.valueOf(userId)}, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AddressEntry.COLUMN_NAME_ADDRESS_ID));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AddressEntry.COLUMN_NAME_TYPE));
                    String full = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AddressEntry.COLUMN_NAME_FULL_ADDRESS));
                    String landmark = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AddressEntry.COLUMN_NAME_LANDMARK));
                    String city = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AddressEntry.COLUMN_NAME_CITY));
                    String area = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AddressEntry.COLUMN_NAME_AREA));
                    double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.AddressEntry.COLUMN_NAME_LATITUDE));
                    double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.AddressEntry.COLUMN_NAME_LONGITUDE));
                    boolean isDefault = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AddressEntry.COLUMN_NAME_IS_DEFAULT)) == 1;
                    
                    addresses.add(new com.sunit.groceryplus.models.Address(id, userId, type, full, landmark, city, area, latitude, longitude, isDefault));
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing address", e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return addresses;
    }

    public boolean deleteAddress(int addressId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DatabaseContract.AddressEntry.TABLE_NAME, 
                DatabaseContract.AddressEntry.COLUMN_NAME_ADDRESS_ID + " = ?", 
                new String[]{String.valueOf(addressId)}) > 0;
    }

    public boolean setDefaultAddress(int userId, int addressId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Reset all to 0
        ContentValues valuesReset = new ContentValues();
        valuesReset.put(DatabaseContract.AddressEntry.COLUMN_NAME_IS_DEFAULT, 0);
        db.update(DatabaseContract.AddressEntry.TABLE_NAME, valuesReset, 
                DatabaseContract.AddressEntry.COLUMN_NAME_USER_ID + " = ?", 
                new String[]{String.valueOf(userId)});
        
        // Set selected to 1
        ContentValues valuesSet = new ContentValues();
        valuesSet.put(DatabaseContract.AddressEntry.COLUMN_NAME_IS_DEFAULT, 1);
        return db.update(DatabaseContract.AddressEntry.TABLE_NAME, valuesSet, 
                DatabaseContract.AddressEntry.COLUMN_NAME_ADDRESS_ID + " = ?", 
                new String[]{String.valueOf(addressId)}) > 0;
    }

    public boolean updateAddress(int addressId, String type, String fullAddress, String landmark, String city, String area, double latitude, double longitude, boolean isDefault) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.AddressEntry.COLUMN_NAME_TYPE, type);
        values.put(DatabaseContract.AddressEntry.COLUMN_NAME_FULL_ADDRESS, fullAddress);
        values.put(DatabaseContract.AddressEntry.COLUMN_NAME_LANDMARK, landmark);
        values.put(DatabaseContract.AddressEntry.COLUMN_NAME_CITY, city);
        values.put(DatabaseContract.AddressEntry.COLUMN_NAME_AREA, area);
        values.put(DatabaseContract.AddressEntry.COLUMN_NAME_LATITUDE, latitude);
        values.put(DatabaseContract.AddressEntry.COLUMN_NAME_LONGITUDE, longitude);
        values.put(DatabaseContract.AddressEntry.COLUMN_NAME_IS_DEFAULT, isDefault ? 1 : 0);
        
        return db.update(DatabaseContract.AddressEntry.TABLE_NAME, values, 
                DatabaseContract.AddressEntry.COLUMN_NAME_ADDRESS_ID + " = ?", 
                new String[]{String.valueOf(addressId)}) > 0;
    }
    // ==================== VENDOR METHODS ====================

    public long addVendor(String name, String address, double lat, double lng, String icon, double rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_NAME, name);
        values.put(DatabaseContract.VendorEntry.COLUMN_NAME_ADDRESS, address);
        values.put(DatabaseContract.VendorEntry.COLUMN_NAME_LATITUDE, lat);
        values.put(DatabaseContract.VendorEntry.COLUMN_NAME_LONGITUDE, lng);
        values.put(DatabaseContract.VendorEntry.COLUMN_NAME_ICON, icon);
        values.put(DatabaseContract.VendorEntry.COLUMN_NAME_RATING, rating);
        return db.insert(DatabaseContract.VendorEntry.TABLE_NAME, null, values);
    }

    public List<com.sunit.groceryplus.models.Vendor> getAllVendors() {
        List<com.sunit.groceryplus.models.Vendor> vendors = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.VendorEntry.TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_NAME));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.VendorEntry.COLUMN_NAME_ADDRESS));
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.VendorEntry.COLUMN_NAME_LATITUDE));
                double lng = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.VendorEntry.COLUMN_NAME_LONGITUDE));
                String icon = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.VendorEntry.COLUMN_NAME_ICON));
                double rating = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.VendorEntry.COLUMN_NAME_RATING));

                vendors.add(new com.sunit.groceryplus.models.Vendor(id, name, address, lat, lng, icon, rating));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return vendors;
    }

    public com.sunit.groceryplus.models.Vendor getVendorById(int vendorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.VendorEntry.TABLE_NAME, null,
                DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_ID + " = ?",
                new String[]{String.valueOf(vendorId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_NAME));
            String address = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.VendorEntry.COLUMN_NAME_ADDRESS));
            double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.VendorEntry.COLUMN_NAME_LATITUDE));
            double lng = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.VendorEntry.COLUMN_NAME_LONGITUDE));
            String icon = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.VendorEntry.COLUMN_NAME_ICON));
            double rating = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.VendorEntry.COLUMN_NAME_RATING));
            cursor.close();
            return new com.sunit.groceryplus.models.Vendor(id, name, address, lat, lng, icon, rating);
        }
        if (cursor != null) cursor.close();
        return null;
    }
    public boolean updateVendor(int vendorId, String name, String address, double lat, double lng, String icon, double rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_NAME, name);
        values.put(DatabaseContract.VendorEntry.COLUMN_NAME_ADDRESS, address);
        values.put(DatabaseContract.VendorEntry.COLUMN_NAME_LATITUDE, lat);
        values.put(DatabaseContract.VendorEntry.COLUMN_NAME_LONGITUDE, lng);
        values.put(DatabaseContract.VendorEntry.COLUMN_NAME_ICON, icon);
        values.put(DatabaseContract.VendorEntry.COLUMN_NAME_RATING, rating);
        return db.update(DatabaseContract.VendorEntry.TABLE_NAME, values, 
                DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_ID + " = ?", 
                new String[]{String.valueOf(vendorId)}) > 0;
    }

    public boolean deleteVendor(int vendorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DatabaseContract.VendorEntry.TABLE_NAME, 
                DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_ID + " = ?", 
                new String[]{String.valueOf(vendorId)}) > 0;
    }

    /**
     * Data Migration: Ensure all products have a valid vendor.
     * If no vendor exists, creates a default one.
     */
    public void checkAndAssignDefaultVendor() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // 1. Check if any vendor exists
            int vendorId = -1;
            Cursor cursor = db.query(DatabaseContract.VendorEntry.TABLE_NAME, 
                    new String[]{DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_ID}, 
                    null, null, null, null, null, "1");
            
            if (cursor != null && cursor.moveToFirst()) {
                vendorId = cursor.getInt(0);
                cursor.close();
            } else {
                // 2. Create default vendor if none exists
                ContentValues vValues = new ContentValues();
                vValues.put(DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_NAME, "General Store");
                vValues.put(DatabaseContract.VendorEntry.COLUMN_NAME_ADDRESS, "City Center");
                vValues.put(DatabaseContract.VendorEntry.COLUMN_NAME_LATITUDE, 27.7172);
                vValues.put(DatabaseContract.VendorEntry.COLUMN_NAME_LONGITUDE, 85.3240);
                vValues.put(DatabaseContract.VendorEntry.COLUMN_NAME_ICON, "ic_vendor");
                vValues.put(DatabaseContract.VendorEntry.COLUMN_NAME_RATING, 4.5);
                vendorId = (int) db.insert(DatabaseContract.VendorEntry.TABLE_NAME, null, vValues);
                if (cursor != null) cursor.close();
            }

            // 3. Update products with missing vendor (0 or null)
            ContentValues pValues = new ContentValues();
            pValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, vendorId);
            db.update(ProductEntry.TABLE_NAME, pValues, 
                    ProductEntry.COLUMN_NAME_VENDOR_ID + " <= 0 OR " + ProductEntry.COLUMN_NAME_VENDOR_ID + " IS NULL", 
                    null);
            
            Log.d(TAG, "Data Migration: All products assigned to vendor ID " + vendorId);
        } catch (Exception e) {
            Log.e(TAG, "Error during vendor migration", e);
        }
    }
}
