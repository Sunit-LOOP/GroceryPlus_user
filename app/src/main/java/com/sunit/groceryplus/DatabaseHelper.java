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

/** Core database management class for handling SQLite operations, schema versioning, and unified data access. */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Infrastructure
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "GroceryPlus.db";
    private static final int DATABASE_VERSION = 10;

    /** Initializes the helper with application context and predefined database settings. */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /** Executes SQL statements to create all database tables and inserts initial seeds. */
    @Override
    public void onCreate(SQLiteDatabase db) {
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
        db.execSQL(DatabaseContract.SQL_CREATE_SEARCH_HISTORY_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_ADMIN_SETTINGS_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_WISHLISTS_TABLE);

        insertDefaultAdmin(db);
        insertSampleCategoriesAndProducts(db);
    }

    /** Manages database upgrades and schema migrations based on version changes. */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        
        if (oldVersion < 10) {
            try {
                db.execSQL("ALTER TABLE " + CategoryEntry.TABLE_NAME + " ADD COLUMN " + CategoryEntry.COLUMN_NAME_IMAGE + " TEXT");
                Log.d(TAG, "Added image column to categories table");
            } catch (Exception e) {
                Log.w(TAG, "Note: Category image column might already exist", e);
            }
        }
        
        insertSampleData();
    }

    /** Inserts a default administrator account into the users table. */
    private void insertDefaultAdmin(SQLiteDatabase db) {
        try {
            String adminPassword = "admin123";
            String salt = generateSalt();
            String hashedPassword = hashPassword(adminPassword, salt);

            ContentValues values = new ContentValues();
            values.put(UserEntry.COLUMN_NAME_USER_NAME, "Admin User");
            values.put(UserEntry.COLUMN_NAME_USER_EMAIL, "admin@gmail.com");
            values.put(UserEntry.COLUMN_NAME_USER_PHONE, "9815689963");
            values.put(UserEntry.COLUMN_NAME_USER_PASSWORD, hashedPassword);
            values.put(UserEntry.COLUMN_NAME_USER_SALT, salt);
            values.put(UserEntry.COLUMN_NAME_USER_TYPE, "admin");

            db.insert(UserEntry.TABLE_NAME, null, values);
            Log.d(TAG, "Default admin created");
        } catch (Exception e) {
            Log.e(TAG, "Error creating default admin", e);
        }
    }

    /** Generates a cryptographically strong random salt. */
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return bytesToHex(salt);
    }

    /** Hashes a plain text password using SHA-256 with a provided salt. */
    private String hashPassword(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt.getBytes());
        byte[] hashed = md.digest(password.getBytes());
        return bytesToHex(hashed);
    }

    /** Converts a byte array into a hexadecimal representation string. */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /** Registers a new user with a salted and hashed password. */
    public long addUser(String name, String email, String phone, String password, String userType) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            String salt = generateSalt();
            String hashedPassword = hashPassword(password, salt);
            
            ContentValues values = new ContentValues();
            values.put(UserEntry.COLUMN_NAME_USER_NAME, name);
            values.put(UserEntry.COLUMN_NAME_USER_EMAIL, email);
            values.put(UserEntry.COLUMN_NAME_USER_PHONE, phone);
            values.put(UserEntry.COLUMN_NAME_USER_PASSWORD, hashedPassword);
            values.put(UserEntry.COLUMN_NAME_USER_SALT, salt);
            values.put(UserEntry.COLUMN_NAME_USER_TYPE, userType);
            
            return db.insert(UserEntry.TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error adding user", e);
            return -1;
        }
    }
    
    /** Authenticates a user by verifying their email and hashed password. */
    public User authenticateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        Log.d(TAG, "Authenticating user with email: " + email);
        String selectQuery = "SELECT * FROM " + UserEntry.TABLE_NAME + " WHERE " + UserEntry.COLUMN_NAME_USER_EMAIL + " = ?";
        
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email});
        
        if (cursor != null && cursor.moveToFirst()) {
            try {
                int saltIndex = cursor.getColumnIndex(UserEntry.COLUMN_NAME_USER_SALT);
                int passwordIndex = cursor.getColumnIndex(UserEntry.COLUMN_NAME_USER_PASSWORD);
                int userIdIndex = cursor.getColumnIndex(UserEntry.COLUMN_NAME_USER_ID);
                int userNameIndex = cursor.getColumnIndex(UserEntry.COLUMN_NAME_USER_NAME);
                int userEmailIndex = cursor.getColumnIndex(UserEntry.COLUMN_NAME_USER_EMAIL);
                int userPhoneIndex = cursor.getColumnIndex(UserEntry.COLUMN_NAME_USER_PHONE);
                int userTypeIndex = cursor.getColumnIndex(UserEntry.COLUMN_NAME_USER_TYPE);
                
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
    
    /** Wipes and re-inserts all sample data for testing and demonstration purposes. */
    public void forceRefreshSampleData() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Log.d(TAG, "Force refreshing ALL sample data...");
            
            // Clear existing sample data
            db.delete(ProductEntry.TABLE_NAME, null, null);
            db.delete(CategoryEntry.TABLE_NAME, null, null);
            db.delete(DatabaseContract.VendorEntry.TABLE_NAME, null, null);
            db.delete(DatabaseContract.PromotionEntry.TABLE_NAME, null, null);
            
            // Insert fresh sample data
            insertSampleCategoriesAndProducts(db);
            insertSampleVendors();
            insertSamplePromotions();
            
            Log.d(TAG, "All sample data refreshed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing sample data", e);
        }
    }

    private void insertSampleCategoriesAndProducts(SQLiteDatabase db) {
        // Insert sample categories
        ContentValues categoryValues = new ContentValues();
        categoryValues.put(CategoryEntry.COLUMN_NAME_CATEGORY_NAME, "Fruits");
        categoryValues.put(CategoryEntry.COLUMN_NAME_CATEGORY_DESCRIPTION, "Fresh and delicious fruits");
        db.insert(CategoryEntry.TABLE_NAME, null, categoryValues);
        
        categoryValues.clear();
        categoryValues.put(CategoryEntry.COLUMN_NAME_CATEGORY_NAME, "Vegetables");
        categoryValues.put(CategoryEntry.COLUMN_NAME_CATEGORY_DESCRIPTION, "Fresh and organic vegetables");
        db.insert(CategoryEntry.TABLE_NAME, null, categoryValues);
        
        categoryValues.clear();
        categoryValues.put(CategoryEntry.COLUMN_NAME_CATEGORY_NAME, "Dairy Products");
        categoryValues.put(CategoryEntry.COLUMN_NAME_CATEGORY_DESCRIPTION, "Milk, cheese, and other dairy items");
        db.insert(CategoryEntry.TABLE_NAME, null, categoryValues);
        
        categoryValues.clear();
        categoryValues.put(CategoryEntry.COLUMN_NAME_CATEGORY_NAME, "Bakery");
        categoryValues.put(CategoryEntry.COLUMN_NAME_CATEGORY_DESCRIPTION, "Fresh bread and bakery items");
        db.insert(CategoryEntry.TABLE_NAME, null, categoryValues);
        
        // Removed redundant single vendor insertion - handled by insertSampleVendors()
        
        // Insert sample products - 5 products per category
        
        // FRUITS CATEGORY (5 products)
        ContentValues productValues = new ContentValues();
        productValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, "Fresh Apples");
        productValues.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, 1); // Fruits
        productValues.put(ProductEntry.COLUMN_NAME_PRICE, 120.0);
        productValues.put(ProductEntry.COLUMN_NAME_DESCRIPTION, "Fresh and crispy red apples");
        productValues.put(ProductEntry.COLUMN_NAME_IMAGE, "product_icon");
        productValues.put(ProductEntry.COLUMN_NAME_STOCK, 50);
        productValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, 1);
        db.insert(ProductEntry.TABLE_NAME, null, productValues);
        
        productValues.clear();
        productValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, "Fresh Bananas");
        productValues.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, 1); // Fruits
        productValues.put(ProductEntry.COLUMN_NAME_PRICE, 60.0);
        productValues.put(ProductEntry.COLUMN_NAME_DESCRIPTION, "Ripe yellow bananas");
        productValues.put(ProductEntry.COLUMN_NAME_IMAGE, "product_icon");
        productValues.put(ProductEntry.COLUMN_NAME_STOCK, 75);
        productValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, 1);
        db.insert(ProductEntry.TABLE_NAME, null, productValues);
        
        productValues.clear();
        productValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, "Sweet Oranges");
        productValues.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, 1); // Fruits
        productValues.put(ProductEntry.COLUMN_NAME_PRICE, 80.0);
        productValues.put(ProductEntry.COLUMN_NAME_DESCRIPTION, "Juicy and sweet oranges");
        productValues.put(ProductEntry.COLUMN_NAME_IMAGE, "product_icon");
        productValues.put(ProductEntry.COLUMN_NAME_STOCK, 60);
        productValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, 1);
        db.insert(ProductEntry.TABLE_NAME, null, productValues);
        
        productValues.clear();
        productValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, "Fresh Grapes");
        productValues.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, 1); // Fruits
        productValues.put(ProductEntry.COLUMN_NAME_PRICE, 150.0);
        productValues.put(ProductEntry.COLUMN_NAME_DESCRIPTION, "Sweet and fresh grapes");
        productValues.put(ProductEntry.COLUMN_NAME_IMAGE, "product_icon");
        productValues.put(ProductEntry.COLUMN_NAME_STOCK, 40);
        productValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, 1);
        db.insert(ProductEntry.TABLE_NAME, null, productValues);
        
        productValues.clear();
        productValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, "Ripe Mangoes");
        productValues.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, 1); // Fruits
        productValues.put(ProductEntry.COLUMN_NAME_PRICE, 180.0);
        productValues.put(ProductEntry.COLUMN_NAME_DESCRIPTION, "Sweet and juicy mangoes");
        productValues.put(ProductEntry.COLUMN_NAME_IMAGE, "product_icon");
        productValues.put(ProductEntry.COLUMN_NAME_STOCK, 35);
        productValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, 1);
        db.insert(ProductEntry.TABLE_NAME, null, productValues);
        
        // VEGETABLES CATEGORY (5 products)
        productValues.clear();
        productValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, "Fresh Tomatoes");
        productValues.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, 2); // Vegetables
        productValues.put(ProductEntry.COLUMN_NAME_PRICE, 40.0);
        productValues.put(ProductEntry.COLUMN_NAME_DESCRIPTION, "Fresh red tomatoes");
        productValues.put(ProductEntry.COLUMN_NAME_IMAGE, "product_icon");
        productValues.put(ProductEntry.COLUMN_NAME_STOCK, 100);
        productValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, 1);
        db.insert(ProductEntry.TABLE_NAME, null, productValues);
        
        productValues.clear();
        productValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, "Fresh Carrots");
        productValues.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, 2); // Vegetables
        productValues.put(ProductEntry.COLUMN_NAME_PRICE, 30.0);
        productValues.put(ProductEntry.COLUMN_NAME_DESCRIPTION, "Fresh orange carrots");
        productValues.put(ProductEntry.COLUMN_NAME_IMAGE, "product_icon");
        productValues.put(ProductEntry.COLUMN_NAME_STOCK, 80);
        productValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, 1);
        db.insert(ProductEntry.TABLE_NAME, null, productValues);
        
        productValues.clear();
        productValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, "Green Spinach");
        productValues.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, 2); // Vegetables
        productValues.put(ProductEntry.COLUMN_NAME_PRICE, 25.0);
        productValues.put(ProductEntry.COLUMN_NAME_DESCRIPTION, "Fresh green spinach leaves");
        productValues.put(ProductEntry.COLUMN_NAME_IMAGE, "product_icon");
        productValues.put(ProductEntry.COLUMN_NAME_STOCK, 60);
        productValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, 1);
        db.insert(ProductEntry.TABLE_NAME, null, productValues);
        
        productValues.clear();
        productValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, "Fresh Potatoes");
        productValues.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, 2); // Vegetables
        productValues.put(ProductEntry.COLUMN_NAME_PRICE, 35.0);
        productValues.put(ProductEntry.COLUMN_NAME_DESCRIPTION, "Fresh and clean potatoes");
        productValues.put(ProductEntry.COLUMN_NAME_IMAGE, "product_icon");
        productValues.put(ProductEntry.COLUMN_NAME_STOCK, 120);
        productValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, 1);
        db.insert(ProductEntry.TABLE_NAME, null, productValues);
        
        productValues.clear();
        productValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, "Fresh Onions");
        productValues.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, 2); // Vegetables
        productValues.put(ProductEntry.COLUMN_NAME_PRICE, 45.0);
        productValues.put(ProductEntry.COLUMN_NAME_DESCRIPTION, "Fresh red onions");
        productValues.put(ProductEntry.COLUMN_NAME_IMAGE, "product_icon");
        productValues.put(ProductEntry.COLUMN_NAME_STOCK, 90);
        productValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, 1);
        db.insert(ProductEntry.TABLE_NAME, null, productValues);
        
        // DAIRY PRODUCTS CATEGORY (5 products)
        productValues.clear();
        productValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, "Fresh Milk");
        productValues.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, 3); // Dairy
        productValues.put(ProductEntry.COLUMN_NAME_PRICE, 55.0);
        productValues.put(ProductEntry.COLUMN_NAME_DESCRIPTION, "Fresh whole milk");
        productValues.put(ProductEntry.COLUMN_NAME_IMAGE, "product_icon");
        productValues.put(ProductEntry.COLUMN_NAME_STOCK, 30);
        productValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, 1);
        db.insert(ProductEntry.TABLE_NAME, null, productValues);
        
        productValues.clear();
        productValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, "Greek Yogurt");
        productValues.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, 3); // Dairy
        productValues.put(ProductEntry.COLUMN_NAME_PRICE, 85.0);
        productValues.put(ProductEntry.COLUMN_NAME_DESCRIPTION, "Creamy Greek yogurt");
        productValues.put(ProductEntry.COLUMN_NAME_IMAGE, "product_icon");
        productValues.put(ProductEntry.COLUMN_NAME_STOCK, 25);
        productValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, 1);
        db.insert(ProductEntry.TABLE_NAME, null, productValues);
        
        productValues.clear();
        productValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, "Cheddar Cheese");
        productValues.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, 3); // Dairy
        productValues.put(ProductEntry.COLUMN_NAME_PRICE, 220.0);
        productValues.put(ProductEntry.COLUMN_NAME_DESCRIPTION, "Aged cheddar cheese");
        productValues.put(ProductEntry.COLUMN_NAME_IMAGE, "product_icon");
        productValues.put(ProductEntry.COLUMN_NAME_STOCK, 20);
        productValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, 1);
        db.insert(ProductEntry.TABLE_NAME, null, productValues);
        
        productValues.clear();
        productValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, "Fresh Butter");
        productValues.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, 3); // Dairy
        productValues.put(ProductEntry.COLUMN_NAME_PRICE, 120.0);
        productValues.put(ProductEntry.COLUMN_NAME_DESCRIPTION, "Pure fresh butter");
        productValues.put(ProductEntry.COLUMN_NAME_IMAGE, "product_icon");
        productValues.put(ProductEntry.COLUMN_NAME_STOCK, 35);
        productValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, 1);
        db.insert(ProductEntry.TABLE_NAME, null, productValues);
        
        productValues.clear();
        productValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, "Farm Eggs");
        productValues.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, 3); // Dairy
        productValues.put(ProductEntry.COLUMN_NAME_PRICE, 75.0);
        productValues.put(ProductEntry.COLUMN_NAME_DESCRIPTION, "Fresh farm eggs");
        productValues.put(ProductEntry.COLUMN_NAME_IMAGE, "product_icon");
        productValues.put(ProductEntry.COLUMN_NAME_STOCK, 50);
        productValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, 1);
        db.insert(ProductEntry.TABLE_NAME, null, productValues);
        
        // BAKERY CATEGORY (5 products)
        productValues.clear();
        productValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, "Whole Wheat Bread");
        productValues.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, 4); // Bakery
        productValues.put(ProductEntry.COLUMN_NAME_PRICE, 35.0);
        productValues.put(ProductEntry.COLUMN_NAME_DESCRIPTION, "Fresh whole wheat bread");
        productValues.put(ProductEntry.COLUMN_NAME_IMAGE, "product_icon");
        productValues.put(ProductEntry.COLUMN_NAME_STOCK, 25);
        productValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, 1);
        db.insert(ProductEntry.TABLE_NAME, null, productValues);
        
        productValues.clear();
        productValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, "Fresh Croissants");
        productValues.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, 4); // Bakery
        productValues.put(ProductEntry.COLUMN_NAME_PRICE, 45.0);
        productValues.put(ProductEntry.COLUMN_NAME_DESCRIPTION, "Buttery fresh croissants");
        productValues.put(ProductEntry.COLUMN_NAME_IMAGE, "product_icon");
        productValues.put(ProductEntry.COLUMN_NAME_STOCK, 30);
        productValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, 1);
        db.insert(ProductEntry.TABLE_NAME, null, productValues);
        
        productValues.clear();
        productValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, "Chocolate Cake");
        productValues.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, 4); // Bakery
        productValues.put(ProductEntry.COLUMN_NAME_PRICE, 180.0);
        productValues.put(ProductEntry.COLUMN_NAME_DESCRIPTION, "Delicious chocolate cake");
        productValues.put(ProductEntry.COLUMN_NAME_IMAGE, "product_icon");
        productValues.put(ProductEntry.COLUMN_NAME_STOCK, 15);
        productValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, 1);
        db.insert(ProductEntry.TABLE_NAME, null, productValues);
        
        productValues.clear();
        productValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, "Fresh Bagels");
        productValues.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, 4); // Bakery
        productValues.put(ProductEntry.COLUMN_NAME_PRICE, 40.0);
        productValues.put(ProductEntry.COLUMN_NAME_DESCRIPTION, "Fresh baked bagels");
        productValues.put(ProductEntry.COLUMN_NAME_IMAGE, "product_icon");
        productValues.put(ProductEntry.COLUMN_NAME_STOCK, 35);
        productValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, 1);
        db.insert(ProductEntry.TABLE_NAME, null, productValues);
        
        productValues.clear();
        productValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, "French Pastries");
        productValues.put(ProductEntry.COLUMN_NAME_CATEGORY_ID, 4); // Bakery
        productValues.put(ProductEntry.COLUMN_NAME_PRICE, 95.0);
        productValues.put(ProductEntry.COLUMN_NAME_DESCRIPTION, "Fresh French pastries");
        productValues.put(ProductEntry.COLUMN_NAME_IMAGE, "product_icon");
        productValues.put(ProductEntry.COLUMN_NAME_STOCK, 20);
        productValues.put(ProductEntry.COLUMN_NAME_VENDOR_ID, 1);
        db.insert(ProductEntry.TABLE_NAME, null, productValues);
        
        Log.d(TAG, "Sample categories and products inserted successfully");
    }

    /** Retrieves a user object by their email address. */
    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + UserEntry.TABLE_NAME + " WHERE " + UserEntry.COLUMN_NAME_USER_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email});
        
        if (cursor != null && cursor.moveToFirst()) {
            try {
                int userIdIndex = cursor.getColumnIndex(UserEntry.COLUMN_NAME_USER_ID);
                int userNameIndex = cursor.getColumnIndex(UserEntry.COLUMN_NAME_USER_NAME);
                int userEmailIndex = cursor.getColumnIndex(UserEntry.COLUMN_NAME_USER_EMAIL);
                int userPhoneIndex = cursor.getColumnIndex(UserEntry.COLUMN_NAME_USER_PHONE);
                int userTypeIndex = cursor.getColumnIndex(UserEntry.COLUMN_NAME_USER_TYPE);
                
                int userId = (userIdIndex >= 0) ? cursor.getInt(userIdIndex) : -1;
                String userName = (userNameIndex >= 0) ? cursor.getString(userNameIndex) : "";
                String userEmail = (userEmailIndex >= 0) ? cursor.getString(userEmailIndex) : "";
                String userPhone = (userPhoneIndex >= 0) ? cursor.getString(userPhoneIndex) : "";
                String userType = (userTypeIndex >= 0) ? cursor.getString(userTypeIndex) : "";
                
                return new User(userId, userName, userEmail, userPhone, userType);
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
    
    /** Retrieves a user object by their numeric ID. */
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + UserEntry.TABLE_NAME + " WHERE " + UserEntry.COLUMN_NAME_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});
        
        if (cursor != null && cursor.moveToFirst()) {
            try {
                int userIdIndex = cursor.getColumnIndex(UserEntry.COLUMN_NAME_USER_ID);
                int userNameIndex = cursor.getColumnIndex(UserEntry.COLUMN_NAME_USER_NAME);
                int userEmailIndex = cursor.getColumnIndex(UserEntry.COLUMN_NAME_USER_EMAIL);
                int userPhoneIndex = cursor.getColumnIndex(UserEntry.COLUMN_NAME_USER_PHONE);
                int userTypeIndex = cursor.getColumnIndex(UserEntry.COLUMN_NAME_USER_TYPE);
                
                int id = (userIdIndex >= 0) ? cursor.getInt(userIdIndex) : -1;
                String userName = (userNameIndex >= 0) ? cursor.getString(userNameIndex) : "";
                String userEmail = (userEmailIndex >= 0) ? cursor.getString(userEmailIndex) : "";
                String userPhone = (userPhoneIndex >= 0) ? cursor.getString(userPhoneIndex) : "";
                String userType = (userTypeIndex >= 0) ? cursor.getString(userTypeIndex) : "";
                
                return new User(id, userName, userEmail, userPhone, userType);
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
    
    /** Updates a user's core profile information in the database. */
    public boolean updateUser(int userId, String name, String email, String phone, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            ContentValues values = new ContentValues();
            values.put(UserEntry.COLUMN_NAME_USER_NAME, name);
            values.put(UserEntry.COLUMN_NAME_USER_EMAIL, email);
            values.put(UserEntry.COLUMN_NAME_USER_PHONE, phone);
            
            int result = db.update(UserEntry.TABLE_NAME, values, 
                                  UserEntry.COLUMN_NAME_USER_ID + " = ?", 
                                  new String[]{String.valueOf(userId)});
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating user", e);
            return false;
        }
    }
    
    /** Checks whether a user with the specified email already exists. */
    public boolean isUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        Log.d(TAG, "Checking if user exists with email: " + email);
        String selectQuery = "SELECT * FROM " + UserEntry.TABLE_NAME + " WHERE " + UserEntry.COLUMN_NAME_USER_EMAIL + " = ?";
        
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email});
        
        boolean exists = (cursor != null && cursor.getCount() > 0);
        Log.d(TAG, "User exists: " + exists);
        
        if (cursor != null) {
            cursor.close();
        }
        
        return exists;
    }

    /** Retrieves a list of all users registered in the system. */
    public java.util.List<User> getAllUsers() {
        java.util.List<User> users = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + UserEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USER_ID));
                    String userName = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USER_NAME));
                    String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USER_EMAIL));
                    String userPhone = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USER_PHONE));
                    String userType = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USER_TYPE));
                    
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
    public long addCategory(String categoryName, String categoryDescription, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.CategoryEntry.COLUMN_NAME_CATEGORY_NAME, categoryName);
        values.put(DatabaseContract.CategoryEntry.COLUMN_NAME_CATEGORY_DESCRIPTION, categoryDescription);
        values.put(DatabaseContract.CategoryEntry.COLUMN_NAME_IMAGE, image);
        
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
    public long createOrder(int userId, double totalAmount, double deliveryFee, String status, int addressId, String instructions) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            ContentValues values = new ContentValues();
            values.put(OrderEntry.COLUMN_NAME_USER_ID, userId);
            values.put(OrderEntry.COLUMN_NAME_TOTAL_AMOUNT, totalAmount);
            values.put(OrderEntry.COLUMN_NAME_DELIVERY_FEE, deliveryFee);
            values.put(OrderEntry.COLUMN_NAME_STATUS, status);
            values.put(OrderEntry.COLUMN_NAME_ADDRESS_ID, addressId);
            values.put(OrderEntry.COLUMN_NAME_DELIVERY_INSTRUCTIONS, instructions);
            
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
    
    /** Retrieves a list of products by searching for a partial name match. */
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
    
    /** Updates an existing product's details in the database. */
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
    
    /** Permanently removes a product from the database. */
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
    
    // ==================== CATEGORY METHODS ====================
    
    /** Retrieves a list of all product categories. */
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
                    String image = cursor.getString(cursor.getColumnIndexOrThrow(CategoryEntry.COLUMN_NAME_IMAGE));
                    
                    com.sunit.groceryplus.models.Category category = new com.sunit.groceryplus.models.Category(
                        categoryId, categoryName, categoryDescription, image
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
    
    /** Retrieves a specific category by its numeric ID. */
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
                String image = cursor.getString(cursor.getColumnIndexOrThrow(CategoryEntry.COLUMN_NAME_IMAGE));
                
                cursor.close();
                return new com.sunit.groceryplus.models.Category(id, categoryName, categoryDescription, image);
            } catch (Exception e) {
                Log.e(TAG, "Error getting category by ID", e);
                cursor.close();
            }
        } else if (cursor != null) {
            cursor.close();
        }
        
        return null;
    }
    
    /** Updates an existing category's name, description, and image. */
    public boolean updateCategory(int categoryId, String categoryName, String categoryDescription, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            ContentValues values = new ContentValues();
            values.put(CategoryEntry.COLUMN_NAME_CATEGORY_NAME, categoryName);
            values.put(CategoryEntry.COLUMN_NAME_CATEGORY_DESCRIPTION, categoryDescription);
            values.put(CategoryEntry.COLUMN_NAME_IMAGE, image);
            
            int result = db.update(CategoryEntry.TABLE_NAME, values, 
                                  CategoryEntry.COLUMN_NAME_CATEGORY_ID + " = ?", 
                                  new String[]{String.valueOf(categoryId)});
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating category", e);
            return false;
        }
    }
    
    /** Permanently deletes a category from the database. */
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
    
    /** Retrieves shopping cart items with joined product details for a user. */
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
    
    /** Updates the quantity of a specific item in the shopping cart. */
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
    
    /** Calculates the total monetary value of all items in a user's cart. */
    public double getCartTotal(int userId) {
        double total = 0.0;
        java.util.List<com.sunit.groceryplus.models.CartItem> items = getCartItemsWithDetails(userId);
        
        for (com.sunit.groceryplus.models.CartItem item : items) {
            total += item.getSubtotal();
        }
        
        return total;
    }
    
    // ==================== ORDER METHODS ====================
    
    /** Retrieves a list of all global orders with joined user and delivery personnel details. */
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
    
    /** Retrieves a list of orders specifically for a given user. */
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
    
    /** Retrieves all individual items belonging to a specific order. */
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
     * Get products with low stock (below threshold)
     */
    public java.util.List<com.sunit.groceryplus.models.Product> getLowStockProducts(int threshold) {
        java.util.List<com.sunit.groceryplus.models.Product> products = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + DatabaseContract.ProductEntry.TABLE_NAME +
                " WHERE " + DatabaseContract.ProductEntry.COLUMN_NAME_STOCK + " > 0 AND " +
                DatabaseContract.ProductEntry.COLUMN_NAME_STOCK + " <= ?" +
                " ORDER BY " + DatabaseContract.ProductEntry.COLUMN_NAME_STOCK + " ASC";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(threshold)});
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_PRODUCT_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME));
                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_CATEGORY_ID));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_PRICE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_DESCRIPTION));
                    String image = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_IMAGE));
                    int stock = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_STOCK));
                    int vendorId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_VENDOR_ID));
                    
                    com.sunit.groceryplus.models.Product product = new com.sunit.groceryplus.models.Product(
                        id, name, categoryId, price, description, image, stock, vendorId
                    );
                    products.add(product);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing low stock product", e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return products;
    }

    /**
     * Get products that are out of stock
     */
    public java.util.List<com.sunit.groceryplus.models.Product> getOutOfStockProducts() {
        java.util.List<com.sunit.groceryplus.models.Product> products = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + DatabaseContract.ProductEntry.TABLE_NAME +
                " WHERE " + DatabaseContract.ProductEntry.COLUMN_NAME_STOCK + " <= 0" +
                " ORDER BY " + DatabaseContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME + " ASC";
        
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_PRODUCT_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME));
                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_CATEGORY_ID));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_PRICE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_DESCRIPTION));
                    String image = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_IMAGE));
                    int stock = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_STOCK));
                    int vendorId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_VENDOR_ID));
                    
                    com.sunit.groceryplus.models.Product product = new com.sunit.groceryplus.models.Product(
                        id, name, categoryId, price, description, image, stock, vendorId
                    );
                    products.add(product);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing out of stock product", e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return products;
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
            Log.d(TAG, "Starting independent sample data check and insertion");
            
            // 1. Categories & Products
            Cursor catCursor = db.rawQuery("SELECT COUNT(*) FROM " + CategoryEntry.TABLE_NAME, null);
            if (catCursor != null && catCursor.moveToFirst()) {
                int count = catCursor.getInt(0);
                catCursor.close();
                Log.d(TAG, "Categories count: " + count);
                if (count == 0) {
                    Log.d(TAG, "Categories table is empty, inserting sample data...");
                    insertSampleCategoriesAndProducts(db);
                } else {
                    Log.d(TAG, "Categories table already has data, skipping sample insertion");
                }

            }
            
            // 2. Admin User
            if (!isUserExists("admin@gmail.com")) {
                long adminId = addUser("Admin User", "admin@gmail.com", "9815689963", "admin123", "admin");
                Log.d(TAG, "Default admin check done");
            }
            
            // 3. Vendors
            insertSampleVendors();
            
            // 4. Promotions
            insertSamplePromotions();
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
        // Only insert if we have fewer than 3 vendors (ensures restoration if only a placeholder was present)
        if (getAllVendors().size() < 3) {
            Log.d(TAG, "Restoring/Expanding sample vendors...");
            // Only add if doesn't exist by name to prevent duplicates
            addVendorIfNotExists("Fresh Market", "123 Main Street", 27.7172, 85.3240, "vendor_icon", 4.5);
            addVendorIfNotExists("Fresh Mart KTM", "Durbar Marg, Kathmandu", 27.7120, 85.3210, "vendor_icon", 4.5);
            addVendorIfNotExists("KTM Food Store", "Lazimpat, Kathmandu", 27.7250, 85.3200, "vendor_icon", 4.3);
            addVendorIfNotExists("Green Valley Grocers", "Baneshwor, Kathmandu", 27.6915, 85.3420, "vendor_icon", 4.7);
            addVendorIfNotExists("Organic Oasis", "Patan, Lalitpur", 27.6710, 85.3120, "vendor_icon", 4.8);
            Log.d(TAG, "Vendors restoration process completed");
        }
    }

    private void addVendorIfNotExists(String name, String address, double lat, double lng, String icon, double rating) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.VendorEntry.TABLE_NAME, 
                new String[]{DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_ID}, 
                DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_NAME + " = ?", 
                new String[]{name}, null, null, null);
        
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        
        if (!exists) {
            addVendor(name, address, lat, lng, icon, rating);
        }
    }

    private void insertSamplePromotions() {
        if (getTotalPromotionsCount() == 0) {
            addPromotion("FRESH20", 20.0, "2025-12-31", "banner_1");
            addPromotion("GROCERY10", 10.0, "2025-12-31", "banner_2");
            addPromotion("SAVE50", 50.0, "2025-12-31", "banner_3");
            addPromotion("FREE_DEL", 0.0, "2025-12-31", "banner_4");
            addPromotion("NEWUSER", 15.0, "2025-12-31", "banner_5");
        }
    }

    public int getTotalPromotionsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseContract.PromotionEntry.TABLE_NAME, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
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
    
    /** Returns the total number of orders stored in the system. */
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

    /** Returns the total number of products currently in the database. */
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

    /** Returns the total number of registered customers (excluding admins). */
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

    /** Adds a new promotional code with associated discount and validity period. */
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

    /** Retreives a specific promotion by its unique code, if active. */
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
    
    /** Retrieves all promotional records from the database. */
    public Cursor getAllPromotions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(DatabaseContract.PromotionEntry.TABLE_NAME, null, null, null, null, null, null);
    }

    /** Permanently deletes a promotional record. */
    public boolean deletePromotion(int promoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DatabaseContract.PromotionEntry.TABLE_NAME, 
                DatabaseContract.PromotionEntry.COLUMN_NAME_PROMO_ID + " = ?", 
                new String[]{String.valueOf(promoId)}) > 0;
    }

    // ==================== REVIEW METHODS ====================
    
    // ==================== REVIEW METHODS ====================
    
    /** Retrieves all product reviews with joined product and user names. */
    public Cursor getAllReviews() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT r.*, p." + DatabaseContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME + ", u." + DatabaseContract.UserEntry.COLUMN_NAME_USER_NAME +
                       " FROM " + DatabaseContract.ReviewEntry.TABLE_NAME + " r " +
                       " LEFT JOIN " + DatabaseContract.ProductEntry.TABLE_NAME + " p ON r." + DatabaseContract.ReviewEntry.COLUMN_NAME_PRODUCT_ID + " = p." + DatabaseContract.ProductEntry.COLUMN_NAME_PRODUCT_ID +
                       " LEFT JOIN " + DatabaseContract.UserEntry.TABLE_NAME + " u ON r." + DatabaseContract.ReviewEntry.COLUMN_NAME_USER_ID + " = u." + DatabaseContract.UserEntry.COLUMN_NAME_USER_ID;
        return db.rawQuery(query, null);
    }
    
    /** Deletes a specific review by its numeric ID. */
    public boolean deleteReview(int reviewId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DatabaseContract.ReviewEntry.TABLE_NAME,
                DatabaseContract.ReviewEntry.COLUMN_NAME_REVIEW_ID + " = ?",
                new String[]{String.valueOf(reviewId)}) > 0;
    }

    // ==================== DELIVERY PERSONNEL METHODS ====================
    
    /** Registers a new delivery person in the system. */
    public long addDeliveryPerson(String name, String phone, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_NAME, name);
        values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_PHONE, phone);
        values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_STATUS, status);
        return db.insert(DatabaseContract.DeliveryPersonEntry.TABLE_NAME, null, values);
    }
    
    /** Retrieves a cursor containing all registered delivery personnel. */
    public Cursor getAllDeliveryPersonnel() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(DatabaseContract.DeliveryPersonEntry.TABLE_NAME, null, null, null, null, null, null);
    }
    
    /** Updates the availability or assignment status of a delivery person. */
    public boolean updateDeliveryPersonStatus(int personId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_STATUS, status);
        return db.update(DatabaseContract.DeliveryPersonEntry.TABLE_NAME, values,
                DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_PERSON_ID + " = ?",
                new String[]{String.valueOf(personId)}) > 0;
    }
    
    // ==================== PAYMENT METHODS ====================
    
    /** Records a new payment transaction associated with an order. */
    public long addPayment(int orderId, double amount, String paymentMethod, String transactionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.PaymentEntry.COLUMN_NAME_ORDER_ID, orderId);
            values.put(DatabaseContract.PaymentEntry.COLUMN_NAME_AMOUNT, amount);
            values.put(DatabaseContract.PaymentEntry.COLUMN_NAME_PAYMENT_METHOD, paymentMethod);
            values.put(DatabaseContract.PaymentEntry.COLUMN_NAME_TRANSACTION_ID, transactionId);
            
            return db.insert(DatabaseContract.PaymentEntry.TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error adding payment", e);
            return -1;
        }
    }
    
    /** Retrieves a list of all payment transactions, ordered by date. */
    public Cursor getAllPayments() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(DatabaseContract.PaymentEntry.TABLE_NAME, null, null, null, null, null, DatabaseContract.PaymentEntry.COLUMN_NAME_PAYMENT_DATE + " DESC");
    }
    
    /** Updates the status (e.g., 'completed', 'pending') of a specific payment. */
    public boolean updatePaymentStatus(int paymentId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PaymentEntry.COLUMN_NAME_STATUS, status);
        
        int rowsAffected = db.update(DatabaseContract.PaymentEntry.TABLE_NAME, values, 
                DatabaseContract.PaymentEntry.COLUMN_NAME_PAYMENT_ID + " = ?", 
                new String[]{String.valueOf(paymentId)});
        
        return rowsAffected > 0;
    }
    
    /** Updates the status of a payment transaction using its linked order ID. */
    public boolean updatePaymentStatusByOrderId(int orderId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PaymentEntry.COLUMN_NAME_STATUS, status);
        
        int rowsAffected = db.update(DatabaseContract.PaymentEntry.TABLE_NAME, values, 
                DatabaseContract.PaymentEntry.COLUMN_NAME_ORDER_ID + " = ?", 
                new String[]{String.valueOf(orderId)});
        
        return rowsAffected > 0;
    }

    // ==================== MESSAGING METHODS ====================
    
    /** Retrieves all system messages with joined sender names. */
    public Cursor getAllMessages() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT m.*, s." + DatabaseContract.UserEntry.COLUMN_NAME_USER_NAME + " as sender_name" +
                       " FROM " + DatabaseContract.MessageEntry.TABLE_NAME + " m " +
                       " LEFT JOIN " + DatabaseContract.UserEntry.TABLE_NAME + " s ON m." + DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID + " = s." + DatabaseContract.UserEntry.COLUMN_NAME_USER_ID +
                       " ORDER BY " + DatabaseContract.MessageEntry.COLUMN_NAME_CREATED_AT + " DESC";
        return db.rawQuery(query, null);
    }

    /** Sends a new message between two users. */
    public long sendMessage(int senderId, int receiverId, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID, senderId);
        values.put(DatabaseContract.MessageEntry.COLUMN_NAME_RECEIVER_ID, receiverId);
        values.put(DatabaseContract.MessageEntry.COLUMN_NAME_MESSAGE_TEXT, message);
        return db.insert(DatabaseContract.MessageEntry.TABLE_NAME, null, values);
    }

    /** Retrieves the message history between two specific users. */
    public Cursor getConversation(int userId1, int userId2) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT m.*, s." + DatabaseContract.UserEntry.COLUMN_NAME_USER_NAME + " as sender_name" +
                       " FROM " + DatabaseContract.MessageEntry.TABLE_NAME + " m " +
                       " LEFT JOIN " + DatabaseContract.UserEntry.TABLE_NAME + " s ON m." + DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID + " = s." + DatabaseContract.UserEntry.COLUMN_NAME_USER_ID +
                       " WHERE (m." + DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID + " = ? AND m." + DatabaseContract.MessageEntry.COLUMN_NAME_RECEIVER_ID + " = ?) OR " +
                       "(m." + DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID + " = ? AND m." + DatabaseContract.MessageEntry.COLUMN_NAME_RECEIVER_ID + " = ?) " +
                       " ORDER BY " + DatabaseContract.MessageEntry.COLUMN_NAME_CREATED_AT + " ASC";
        
        return db.rawQuery(query, new String[]{String.valueOf(userId1), String.valueOf(userId2), String.valueOf(userId2), String.valueOf(userId1)});
    }

    /** Retrieves the unique ID of the primary administrator user. */
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

    /** Retrieves the latest message for each active conversation involving the admin. */
    public Cursor getConversations(int adminId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String sAdminId = String.valueOf(adminId);
        
        // Subquery to find the latest message ID for each conversation
        String subQuery = "SELECT MAX(" + DatabaseContract.MessageEntry.COLUMN_NAME_MESSAGE_ID + ") " +
                         "FROM " + DatabaseContract.MessageEntry.TABLE_NAME + " " +
                         "WHERE " + DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID + " = " + sAdminId + " " +
                         "OR " + DatabaseContract.MessageEntry.COLUMN_NAME_RECEIVER_ID + " = " + sAdminId + " " +
                         "GROUP BY (CASE WHEN " + DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID + " = " + sAdminId + " " +
                         "THEN " + DatabaseContract.MessageEntry.COLUMN_NAME_RECEIVER_ID + " " +
                         "ELSE " + DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID + " END)";

        String mainQuery = "SELECT m.*, u." + DatabaseContract.UserEntry.COLUMN_NAME_USER_NAME + " as remote_name, " +
                          "(CASE WHEN m." + DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID + " = ? " +
                          "THEN m." + DatabaseContract.MessageEntry.COLUMN_NAME_RECEIVER_ID + " " +
                          "ELSE m." + DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID + " END) as conversation_partner_id " +
                          "FROM " + DatabaseContract.MessageEntry.TABLE_NAME + " m " +
                          "JOIN " + DatabaseContract.UserEntry.TABLE_NAME + " u ON u." + DatabaseContract.UserEntry.COLUMN_NAME_USER_ID + " = conversation_partner_id " +
                          "WHERE m." + DatabaseContract.MessageEntry.COLUMN_NAME_MESSAGE_ID + " IN (" + subQuery + ") " +
                          "ORDER BY m." + DatabaseContract.MessageEntry.COLUMN_NAME_CREATED_AT + " DESC";

        return db.rawQuery(mainQuery, new String[]{sAdminId});
    }

    /** Marks all unread messages from a specific sender as read. */
    public boolean markMessagesAsRead(int receiverId, int senderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.MessageEntry.COLUMN_NAME_IS_READ, 1);
        return db.update(DatabaseContract.MessageEntry.TABLE_NAME, values,
                DatabaseContract.MessageEntry.COLUMN_NAME_RECEIVER_ID + " = ? AND " +
                DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID + " = ? AND " +
                DatabaseContract.MessageEntry.COLUMN_NAME_IS_READ + " = 0",
                new String[]{String.valueOf(receiverId), String.valueOf(senderId)}) > 0;
    }

    /**
     * Add a product review
     */
    /** Adds a new product review with rating and text comment. */
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

    /** Retrieves all reviews for a specific product, ordered by date. */
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

    /** Retrieves all system reviews for recommendation engine purposes. */
    public java.util.List<com.sunit.groceryplus.models.Review> getAllReviewsForRecommendations() {
        java.util.List<com.sunit.groceryplus.models.Review> reviews = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.ReviewEntry.TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int reviewId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ReviewEntry.COLUMN_NAME_REVIEW_ID));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ReviewEntry.COLUMN_NAME_USER_ID));
                    int productId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ReviewEntry.COLUMN_NAME_PRODUCT_ID));
                    int rating = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ReviewEntry.COLUMN_NAME_RATING));
                    reviews.add(new com.sunit.groceryplus.models.Review(reviewId, userId, "", productId, "", rating, "", ""));
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing review", e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return reviews;
    }

    /** Retrieves a map of user IDs to products and quantities purchased. */
    public java.util.Map<Integer, java.util.Map<Integer, Integer>> getAllUserPurchaseHistory() {
        java.util.Map<Integer, java.util.Map<Integer, Integer>> history = new java.util.HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT o." + OrderEntry.COLUMN_NAME_USER_ID + ", oi." + OrderItemEntry.COLUMN_NAME_PRODUCT_ID + 
                      ", SUM(oi." + OrderItemEntry.COLUMN_NAME_QUANTITY + ") as total_qty " +
                      "FROM " + OrderEntry.TABLE_NAME + " o " +
                      "JOIN " + OrderItemEntry.TABLE_NAME + " oi ON o." + OrderEntry.COLUMN_NAME_ORDER_ID + " = oi." + OrderItemEntry.COLUMN_NAME_ORDER_ID +
                      " GROUP BY o." + OrderEntry.COLUMN_NAME_USER_ID + ", oi." + OrderItemEntry.COLUMN_NAME_PRODUCT_ID;
        
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int userId = cursor.getInt(0);
                int productId = cursor.getInt(1);
                int qty = cursor.getInt(2);
                
                if (!history.containsKey(userId)) history.put(userId, new java.util.HashMap<>());
                history.get(userId).put(productId, qty);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return history;
    }

    /** Calculates the numerical average rating for a specific product. */
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

    /** Dispatches a new notification to a specific user. */
    public long addNotification(int userId, String title, String message, String type, String refId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.NotificationEntry.COLUMN_NAME_USER_ID, userId);
            values.put(DatabaseContract.NotificationEntry.COLUMN_NAME_TITLE, title);
            values.put(DatabaseContract.NotificationEntry.COLUMN_NAME_MESSAGE, message);
            values.put(DatabaseContract.NotificationEntry.COLUMN_NAME_TYPE, type);
            values.put(DatabaseContract.NotificationEntry.COLUMN_NAME_REF_ID, refId);
            return db.insert(DatabaseContract.NotificationEntry.TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error adding notification", e);
            return -1;
        }
    }

    /** Updates a notification's state to 'read'. */
    public boolean markNotificationRead(int notificationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.NotificationEntry.COLUMN_NAME_IS_READ, 1);
        return db.update(DatabaseContract.NotificationEntry.TABLE_NAME, values,
                DatabaseContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_ID + " = ?",
                new String[]{String.valueOf(notificationId)}) > 0;
    }

    /** Retrieves all notifications for a specific user, ordered by date. */
    public Cursor getUserNotifications(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseContract.NotificationEntry.TABLE_NAME +
                " WHERE " + DatabaseContract.NotificationEntry.COLUMN_NAME_USER_ID + " = ?" +
                " ORDER BY " + DatabaseContract.NotificationEntry.COLUMN_NAME_CREATED_AT + " DESC";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    /** Retrieves a specific order by its numeric ID. */
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

    /** Retrieves a specific saved address by its numeric ID. */
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

    /** Decrements the available stock for a product when an order is successful. */
    public boolean decrementStock(int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + ProductEntry.TABLE_NAME + 
                       " SET " + ProductEntry.COLUMN_NAME_STOCK + " = " + ProductEntry.COLUMN_NAME_STOCK + " - " + quantity +
                       " WHERE " + ProductEntry.COLUMN_NAME_PRODUCT_ID + " = ? AND " + ProductEntry.COLUMN_NAME_STOCK + " >= ?";
        
        db.execSQL(query, new Object[]{productId, quantity});
        return true;
    }

    /** Saves a new location address for a user. */
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

    /** Retrieves all saved addresses belonging to a specific user. */
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

    /** Permanently deletes a saved address. */
    public boolean deleteAddress(int addressId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DatabaseContract.AddressEntry.TABLE_NAME, 
                DatabaseContract.AddressEntry.COLUMN_NAME_ADDRESS_ID + " = ?", 
                new String[]{String.valueOf(addressId)}) > 0;
    }

    /** Designates a specific address as the default for a user, resetting others. */
    public boolean setDefaultAddress(int userId, int addressId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valuesReset = new ContentValues();
        valuesReset.put(DatabaseContract.AddressEntry.COLUMN_NAME_IS_DEFAULT, 0);
        db.update(DatabaseContract.AddressEntry.TABLE_NAME, valuesReset, 
                DatabaseContract.AddressEntry.COLUMN_NAME_USER_ID + " = ?", 
                new String[]{String.valueOf(userId)});
        
        ContentValues valuesSet = new ContentValues();
        valuesSet.put(DatabaseContract.AddressEntry.COLUMN_NAME_IS_DEFAULT, 1);
        return db.update(DatabaseContract.AddressEntry.TABLE_NAME, valuesSet, 
                DatabaseContract.AddressEntry.COLUMN_NAME_ADDRESS_ID + " = ?", 
                new String[]{String.valueOf(addressId)}) > 0;
    }

    /** Updates the details of an existing saved address. */
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

    // ==================== VENDOR METHODS ====================
    
    /** Registers a new vendor or store in the system. */
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

    /** Retrieves a list of all vendors stored in the database. */
    public java.util.List<com.sunit.groceryplus.models.Vendor> getAllVendors() {
        java.util.List<com.sunit.groceryplus.models.Vendor> vendors = new java.util.ArrayList<>();
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

    /** Retrieves a specific vendor's details by their numeric ID. */
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

    /** Updates the profile information for an existing vendor. */
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

    /** Permanently deletes a vendor record from the database. */
    public boolean deleteVendor(int vendorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DatabaseContract.VendorEntry.TABLE_NAME, 
                DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_ID + " = ?", 
                new String[]{String.valueOf(vendorId)}) > 0;
    }

    /** Ensures all products are linked to a valid vendor; creates a default vendor if none exist. */
    public void checkAndAssignDefaultVendor() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int vendorId = -1;
            Cursor cursor = db.query(DatabaseContract.VendorEntry.TABLE_NAME, 
                    new String[]{DatabaseContract.VendorEntry.COLUMN_NAME_VENDOR_ID}, 
                    null, null, null, null, null, "1");
            
            if (cursor != null && cursor.moveToFirst()) {
                vendorId = cursor.getInt(0);
                cursor.close();
            } else {
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

    /** Records a user's search query for future suggestions. */
    public long addSearchQuery(int userId, String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.SearchHistoryEntry.COLUMN_NAME_USER_ID, userId);
        values.put(DatabaseContract.SearchHistoryEntry.COLUMN_NAME_QUERY, query);
        return db.insert(DatabaseContract.SearchHistoryEntry.TABLE_NAME, null, values);
    }

    /** Retrieves the most recent unique search queries for a specific user. */
    public Cursor getRecentSearchQueries(int userId, int limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT " + DatabaseContract.SearchHistoryEntry.COLUMN_NAME_QUERY + 
                      " FROM " + DatabaseContract.SearchHistoryEntry.TABLE_NAME +
                      " WHERE " + DatabaseContract.SearchHistoryEntry.COLUMN_NAME_USER_ID + " = ?" +
                      " ORDER BY " + DatabaseContract.SearchHistoryEntry.COLUMN_NAME_CREATED_AT + " DESC" +
                      " LIMIT " + limit;
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    /** Clears the entire search history for a specific user. */
    public boolean clearSearchHistory(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DatabaseContract.SearchHistoryEntry.TABLE_NAME,
                DatabaseContract.SearchHistoryEntry.COLUMN_NAME_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}) > 0;
    }

    /** Retrieves a collection of every order item recorded in the system. */
    public java.util.List<com.sunit.groceryplus.models.OrderItem> getAllOrderItems() {
        java.util.List<com.sunit.groceryplus.models.OrderItem> orderItems = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + DatabaseContract.OrderItemEntry.TABLE_NAME +
                " ORDER BY " + DatabaseContract.OrderItemEntry.COLUMN_NAME_ORDER_ITEM_ID + " DESC";
        
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int orderItemId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.OrderItemEntry.COLUMN_NAME_ORDER_ITEM_ID));
                    int orderId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.OrderItemEntry.COLUMN_NAME_ORDER_ID));
                    int productId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.OrderItemEntry.COLUMN_NAME_PRODUCT_ID));
                    int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.OrderItemEntry.COLUMN_NAME_QUANTITY));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.OrderItemEntry.COLUMN_NAME_PRICE));
                    
                    com.sunit.groceryplus.models.OrderItem orderItem = new com.sunit.groceryplus.models.OrderItem(
                        orderItemId, orderId, productId, "", quantity, price, ""
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

    /** Updates the quantity of a specific item within an existing order. */
    public boolean updateOrderItemQuantity(int orderItemId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.OrderItemEntry.COLUMN_NAME_QUANTITY, quantity);
            
            int result = db.update(DatabaseContract.OrderItemEntry.TABLE_NAME, values,
                    DatabaseContract.OrderItemEntry.COLUMN_NAME_ORDER_ITEM_ID + " = ?",
                    new String[]{String.valueOf(orderItemId)});
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating order item quantity", e);
            return false;
        }
    }

    /** Permanently removes an item from an existing order. */
    public boolean deleteOrderItem(int orderItemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            int result = db.delete(DatabaseContract.OrderItemEntry.TABLE_NAME,
                    DatabaseContract.OrderItemEntry.COLUMN_NAME_ORDER_ITEM_ID + " = ?",
                    new String[]{String.valueOf(orderItemId)});
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting order item", e);
            return false;
        }
    }

    /**
     * Validate product stock
     */
    /** Verifies if the requested quantity of a product is available in stock. */
    public boolean validateStock(int productId, int requestedQuantity) {
        com.sunit.groceryplus.models.Product product = getProductById(productId);
        return product != null && product.getStock() >= requestedQuantity;
    }

    /** Calculates the cumulative monetary value of all items in a user's cart. */
    public double getTotalCartValue(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT ci." + DatabaseContract.CartItemEntry.COLUMN_NAME_QUANTITY + 
                ", p." + DatabaseContract.ProductEntry.COLUMN_NAME_PRICE +
                " FROM " + DatabaseContract.CartItemEntry.TABLE_NAME + " ci" +
                " JOIN " + DatabaseContract.ProductEntry.TABLE_NAME + " p ON ci." + DatabaseContract.CartItemEntry.COLUMN_NAME_PRODUCT_ID + " = p." + DatabaseContract.ProductEntry.COLUMN_NAME_PRODUCT_ID +
                " WHERE ci." + DatabaseContract.CartItemEntry.COLUMN_NAME_USER_ID + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        double total = 0.0;
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.CartItemEntry.COLUMN_NAME_QUANTITY));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_NAME_PRICE));
                    total += (quantity * price);
                } catch (Exception e) {
                    Log.e(TAG, "Error calculating cart total", e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return total;
    }

    /** Checks if a product has any remaining stock. */
    public boolean isProductAvailable(int productId) {
        com.sunit.groceryplus.models.Product product = getProductById(productId);
        return product != null && product.getStock() > 0;
    }

    /** Formats a numerical amount as a currency string. */
    public static String formatCurrency(double amount) {
        return String.format("Rs. %.2f", amount);
    }

    /** Determines the appropriate delivery fee based on the current cart value. */
    public double calculateDeliveryFee(int userId) {
        double cartValue = getTotalCartValue(userId);
        if (cartValue >= com.sunit.groceryplus.utils.PaymentConfig.FREE_DELIVERY_THRESHOLD) {
            return 0.0;
        }
        return com.sunit.groceryplus.utils.PaymentConfig.DELIVERY_FEE;
    }
    
    /** Retrieves orders for a user, or all orders if the provided ID is -1. */
    public java.util.List<com.sunit.groceryplus.models.Order> getUserOrders(int userId) {
        if (userId == -1) {
            return getAllOrders();
        } else {
            return getOrdersByUser(userId);
        }
    }
    
    /** Retrieves orders filtered by delivery personnel and their current status. */
    public java.util.List<com.sunit.groceryplus.models.Order> getOrdersByDeliveryPersonAndStatus(int deliveryPersonId, String status) {
        java.util.List<com.sunit.groceryplus.models.Order> orders = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + DatabaseContract.OrderEntry.TABLE_NAME + 
                " WHERE " + DatabaseContract.OrderEntry.COLUMN_NAME_DELIVERY_PERSON_ID + " = ? AND " +
                DatabaseContract.OrderEntry.COLUMN_NAME_STATUS + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(deliveryPersonId), status});
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int orderId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.OrderEntry.COLUMN_NAME_ORDER_ID));
                    int orderUserId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.OrderEntry.COLUMN_NAME_USER_ID));
                    double totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.OrderEntry.COLUMN_NAME_TOTAL_AMOUNT));
                    double deliveryFee = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.OrderEntry.COLUMN_NAME_DELIVERY_FEE));
                    String orderStatus = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.OrderEntry.COLUMN_NAME_STATUS));
                    String orderDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.OrderEntry.COLUMN_NAME_ORDER_DATE));
                    
                    com.sunit.groceryplus.models.Order order = new com.sunit.groceryplus.models.Order(
                        orderId, orderUserId, "", "", "", totalAmount, deliveryFee, orderStatus, orderDate
                    );
                    order.setDeliveryPersonId(deliveryPersonId);
                    
                    orders.add(order);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing order", e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return orders;
    }
}
