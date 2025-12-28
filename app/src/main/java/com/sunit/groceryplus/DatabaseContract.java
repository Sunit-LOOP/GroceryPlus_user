package com.sunit.groceryplus;

import android.provider.BaseColumns;

/**
 * Database contract class that defines the database schema
 */
public final class DatabaseContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DatabaseContract() {}

    /* Inner class that defines the users table contents */
    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_USER_NAME = "user_name";
        public static final String COLUMN_NAME_USER_EMAIL = "user_email";
        public static final String COLUMN_NAME_USER_PHONE = "user_phone";
        public static final String COLUMN_NAME_USER_PASSWORD = "user_password";
        public static final String COLUMN_NAME_USER_SALT = "user_salt";
        public static final String COLUMN_NAME_USER_TYPE = "user_type";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
    }

    /* Inner class that defines the categories table contents */
    public static class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_NAME_CATEGORY_ID = "category_id";
        public static final String COLUMN_NAME_CATEGORY_NAME = "category_name";
        public static final String COLUMN_NAME_CATEGORY_DESCRIPTION = "category_description";
    }

    /* Inner class that defines the products table contents */
    public static class ProductEntry implements BaseColumns {
        public static final String TABLE_NAME = "products";
        public static final String COLUMN_NAME_PRODUCT_ID = "product_id";
        public static final String COLUMN_NAME_PRODUCT_NAME = "product_name";
        public static final String COLUMN_NAME_CATEGORY_ID = "category_id";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_STOCK = "stock_quantity";
        public static final String COLUMN_NAME_VENDOR_ID = "vendor_id";
    }

    /* Inner class that defines the orders table contents */
    public static class OrderEntry implements BaseColumns {
        public static final String TABLE_NAME = "orders";
        public static final String COLUMN_NAME_ORDER_ID = "order_id";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_TOTAL_AMOUNT = "total_amount";
        public static final String COLUMN_NAME_DELIVERY_FEE = "delivery_fee";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_ORDER_DATE = "order_date";
        public static final String COLUMN_NAME_SHIPPED_DATE = "shipped_date";
        public static final String COLUMN_NAME_DELIVERY_PERSON_ID = "delivery_person_id";
        public static final String COLUMN_NAME_ADDRESS_ID = "address_id";
    }

    /* Inner class that defines the order_items table contents */
    public static class OrderItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "order_items";
        public static final String COLUMN_NAME_ORDER_ITEM_ID = "order_item_id";
        public static final String COLUMN_NAME_ORDER_ID = "order_id";
        public static final String COLUMN_NAME_PRODUCT_ID = "product_id";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_PRICE = "price";
    }

    /* Inner class that defines the cart_items table contents */
    public static class CartItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "cart_items";
        public static final String COLUMN_NAME_CART_ID = "cart_id";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_PRODUCT_ID = "product_id";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
    }

    /* Inner class that defines the favorites table contents */
    public static class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_NAME_FAVORITE_ID = "favorite_id";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_PRODUCT_ID = "product_id";
        public static final String COLUMN_NAME_ADDED_AT = "added_at";
    }

    /* Inner class that defines the messages table contents */
    public static class MessageEntry implements BaseColumns {
        public static final String TABLE_NAME = "messages";
        public static final String COLUMN_NAME_MESSAGE_ID = "message_id";
        public static final String COLUMN_NAME_SENDER_ID = "sender_id";
        public static final String COLUMN_NAME_RECEIVER_ID = "receiver_id";
        public static final String COLUMN_NAME_MESSAGE_TEXT = "message_text";
        public static final String COLUMN_NAME_IS_READ = "is_read";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
    }

    public static class PromotionEntry implements BaseColumns {
        public static final String TABLE_NAME = "promotions";
        public static final String COLUMN_NAME_PROMO_ID = "promo_id";
        public static final String COLUMN_NAME_CODE = "code";
        public static final String COLUMN_NAME_DISCOUNT_PERCENTAGE = "discount_percentage";
        public static final String COLUMN_NAME_VALID_UNTIL = "valid_until";
        public static final String COLUMN_NAME_IMAGE_URL = "image_url";
        public static final String COLUMN_NAME_IS_ACTIVE = "is_active";
    }

    public static class ReviewEntry implements BaseColumns {
        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_NAME_REVIEW_ID = "review_id";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_PRODUCT_ID = "product_id";
        public static final String COLUMN_NAME_RATING = "rating";
        public static final String COLUMN_NAME_COMMENT = "comment";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
    }

    public static class DeliveryPersonEntry implements BaseColumns {
        public static final String TABLE_NAME = "delivery_personnel";
        public static final String COLUMN_NAME_PERSON_ID = "person_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_STATUS = "status";
    }

    public static class PaymentEntry implements BaseColumns {
        public static final String TABLE_NAME = "payments";
        public static final String COLUMN_NAME_PAYMENT_ID = "payment_id";
        public static final String COLUMN_NAME_ORDER_ID = "order_id";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_PAYMENT_METHOD = "payment_method";
        public static final String COLUMN_NAME_TRANSACTION_ID = "transaction_id";
        public static final String COLUMN_NAME_PAYMENT_DATE = "payment_date";
    }
    
    public static class AddressEntry implements BaseColumns {
        public static final String TABLE_NAME = "addresses";
        public static final String COLUMN_NAME_ADDRESS_ID = "address_id";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_TYPE = "type"; // Home, Work, etc.
        public static final String COLUMN_NAME_FULL_ADDRESS = "full_address";
        public static final String COLUMN_NAME_LANDMARK = "landmark";
        public static final String COLUMN_NAME_CITY = "city";
        public static final String COLUMN_NAME_AREA = "area"; // Link to DeliveryOptimizer nodes
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_IS_DEFAULT = "is_default";
    }

    public static class VendorEntry implements BaseColumns {
        public static final String TABLE_NAME = "vendors";
        public static final String COLUMN_NAME_VENDOR_ID = "vendor_id";
        public static final String COLUMN_NAME_VENDOR_NAME = "vendor_name";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_ICON = "icon"; // drawable name
        public static final String COLUMN_NAME_RATING = "rating";
    }

    // SQL statements to create tables
    public static final String SQL_CREATE_USERS_TABLE =
            "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                    UserEntry.COLUMN_NAME_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    UserEntry.COLUMN_NAME_USER_NAME + " TEXT," +
                    UserEntry.COLUMN_NAME_USER_EMAIL + " TEXT UNIQUE," +
                    UserEntry.COLUMN_NAME_USER_PHONE + " TEXT," +
                    UserEntry.COLUMN_NAME_USER_PASSWORD + " TEXT," +
                    UserEntry.COLUMN_NAME_USER_SALT + " TEXT," +
                    UserEntry.COLUMN_NAME_USER_TYPE + " TEXT," +
                    UserEntry.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

    public static final String SQL_CREATE_CATEGORIES_TABLE =
            "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                    CategoryEntry.COLUMN_NAME_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CategoryEntry.COLUMN_NAME_CATEGORY_NAME + " TEXT," +
                    CategoryEntry.COLUMN_NAME_CATEGORY_DESCRIPTION + " TEXT)";

    public static final String SQL_CREATE_PRODUCTS_TABLE =
            "CREATE TABLE " + ProductEntry.TABLE_NAME + " (" +
                    ProductEntry.COLUMN_NAME_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ProductEntry.COLUMN_NAME_PRODUCT_NAME + " TEXT," +
                    ProductEntry.COLUMN_NAME_CATEGORY_ID + " INTEGER," +
                    ProductEntry.COLUMN_NAME_PRICE + " REAL," +
                    ProductEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    ProductEntry.COLUMN_NAME_IMAGE + " TEXT," +
                    ProductEntry.COLUMN_NAME_STOCK + " INTEGER DEFAULT 0," +
                    ProductEntry.COLUMN_NAME_VENDOR_ID + " INTEGER," +
                    "FOREIGN KEY(" + ProductEntry.COLUMN_NAME_CATEGORY_ID + ") REFERENCES " + CategoryEntry.TABLE_NAME + "(" + CategoryEntry.COLUMN_NAME_CATEGORY_ID + ")," +
                    "FOREIGN KEY(" + ProductEntry.COLUMN_NAME_VENDOR_ID + ") REFERENCES " + VendorEntry.TABLE_NAME + "(" + VendorEntry.COLUMN_NAME_VENDOR_ID + "))";

    public static final String SQL_CREATE_ORDERS_TABLE =
            "CREATE TABLE " + OrderEntry.TABLE_NAME + " (" +
                    OrderEntry.COLUMN_NAME_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    OrderEntry.COLUMN_NAME_USER_ID + " INTEGER," +
                    OrderEntry.COLUMN_NAME_TOTAL_AMOUNT + " REAL," +
                    OrderEntry.COLUMN_NAME_DELIVERY_FEE + " REAL DEFAULT 0.0," +
                    OrderEntry.COLUMN_NAME_STATUS + " TEXT," +
                    OrderEntry.COLUMN_NAME_ORDER_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    OrderEntry.COLUMN_NAME_SHIPPED_DATE + " DATETIME," +
                    OrderEntry.COLUMN_NAME_DELIVERY_PERSON_ID + " INTEGER," +
                    OrderEntry.COLUMN_NAME_ADDRESS_ID + " INTEGER," +
                    "FOREIGN KEY(" + OrderEntry.COLUMN_NAME_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_USER_ID + ")," +
                    "FOREIGN KEY(" + OrderEntry.COLUMN_NAME_DELIVERY_PERSON_ID + ") REFERENCES " + DeliveryPersonEntry.TABLE_NAME + "(" + DeliveryPersonEntry.COLUMN_NAME_PERSON_ID + ")," +
                    "FOREIGN KEY(" + OrderEntry.COLUMN_NAME_ADDRESS_ID + ") REFERENCES " + AddressEntry.TABLE_NAME + "(" + AddressEntry.COLUMN_NAME_ADDRESS_ID + "))";

    public static final String SQL_CREATE_ORDER_ITEMS_TABLE =
            "CREATE TABLE " + OrderItemEntry.TABLE_NAME + " (" +
                    OrderItemEntry.COLUMN_NAME_ORDER_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    OrderItemEntry.COLUMN_NAME_ORDER_ID + " INTEGER," +
                    OrderItemEntry.COLUMN_NAME_PRODUCT_ID + " INTEGER," +
                    OrderItemEntry.COLUMN_NAME_QUANTITY + " INTEGER," +
                    OrderItemEntry.COLUMN_NAME_PRICE + " REAL," +
                    "FOREIGN KEY(" + OrderItemEntry.COLUMN_NAME_ORDER_ID + ") REFERENCES " + OrderEntry.TABLE_NAME + "(" + OrderEntry.COLUMN_NAME_ORDER_ID + ")," +
                    "FOREIGN KEY(" + OrderItemEntry.COLUMN_NAME_PRODUCT_ID + ") REFERENCES " + ProductEntry.TABLE_NAME + "(" + ProductEntry.COLUMN_NAME_PRODUCT_ID + "))";

    public static final String SQL_CREATE_CART_ITEMS_TABLE =
            "CREATE TABLE " + CartItemEntry.TABLE_NAME + " (" +
                    CartItemEntry.COLUMN_NAME_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CartItemEntry.COLUMN_NAME_USER_ID + " INTEGER," +
                    CartItemEntry.COLUMN_NAME_PRODUCT_ID + " INTEGER," +
                    CartItemEntry.COLUMN_NAME_QUANTITY + " INTEGER," +
                    "FOREIGN KEY(" + CartItemEntry.COLUMN_NAME_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_USER_ID + ")," +
                    "FOREIGN KEY(" + CartItemEntry.COLUMN_NAME_PRODUCT_ID + ") REFERENCES " + ProductEntry.TABLE_NAME + "(" + ProductEntry.COLUMN_NAME_PRODUCT_ID + "))";

    public static final String SQL_CREATE_FAVORITES_TABLE =
            "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +
                    FavoriteEntry.COLUMN_NAME_FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FavoriteEntry.COLUMN_NAME_USER_ID + " INTEGER," +
                    FavoriteEntry.COLUMN_NAME_PRODUCT_ID + " INTEGER," +
                    FavoriteEntry.COLUMN_NAME_ADDED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(" + FavoriteEntry.COLUMN_NAME_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_USER_ID + ")," +
                    "FOREIGN KEY(" + FavoriteEntry.COLUMN_NAME_PRODUCT_ID + ") REFERENCES " + ProductEntry.TABLE_NAME + "(" + ProductEntry.COLUMN_NAME_PRODUCT_ID + "))";

    public static final String SQL_CREATE_MESSAGES_TABLE =
            "CREATE TABLE " + MessageEntry.TABLE_NAME + " (" +
                    MessageEntry.COLUMN_NAME_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MessageEntry.COLUMN_NAME_SENDER_ID + " INTEGER," +
                    MessageEntry.COLUMN_NAME_RECEIVER_ID + " INTEGER," +
                    MessageEntry.COLUMN_NAME_MESSAGE_TEXT + " TEXT," +
                    MessageEntry.COLUMN_NAME_IS_READ + " INTEGER DEFAULT 0," +
                    MessageEntry.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(" + MessageEntry.COLUMN_NAME_SENDER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_USER_ID + ")," +
                    "FOREIGN KEY(" + MessageEntry.COLUMN_NAME_RECEIVER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_USER_ID + "))";

    public static final String SQL_CREATE_PROMOTIONS_TABLE =
            "CREATE TABLE " + PromotionEntry.TABLE_NAME + " (" +
                    PromotionEntry.COLUMN_NAME_PROMO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    PromotionEntry.COLUMN_NAME_CODE + " TEXT UNIQUE," +
                    PromotionEntry.COLUMN_NAME_DISCOUNT_PERCENTAGE + " REAL," +
                    PromotionEntry.COLUMN_NAME_VALID_UNTIL + " TEXT," +
                    PromotionEntry.COLUMN_NAME_IMAGE_URL + " TEXT," +
                    PromotionEntry.COLUMN_NAME_IS_ACTIVE + " INTEGER DEFAULT 1)";

    public static final String SQL_CREATE_REVIEWS_TABLE =
            "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                    ReviewEntry.COLUMN_NAME_REVIEW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ReviewEntry.COLUMN_NAME_USER_ID + " INTEGER," +
                    ReviewEntry.COLUMN_NAME_PRODUCT_ID + " INTEGER," +
                    ReviewEntry.COLUMN_NAME_RATING + " INTEGER," +
                    ReviewEntry.COLUMN_NAME_COMMENT + " TEXT," +
                    ReviewEntry.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(" + ReviewEntry.COLUMN_NAME_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_USER_ID + ")," +
                    "FOREIGN KEY(" + ReviewEntry.COLUMN_NAME_PRODUCT_ID + ") REFERENCES " + ProductEntry.TABLE_NAME + "(" + ProductEntry.COLUMN_NAME_PRODUCT_ID + "))";

    public static final String SQL_CREATE_DELIVERY_PERSONNEL_TABLE =
            "CREATE TABLE " + DeliveryPersonEntry.TABLE_NAME + " (" +
                    DeliveryPersonEntry.COLUMN_NAME_PERSON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DeliveryPersonEntry.COLUMN_NAME_NAME + " TEXT," +
                    DeliveryPersonEntry.COLUMN_NAME_PHONE + " TEXT," +
                    DeliveryPersonEntry.COLUMN_NAME_STATUS + " TEXT)";

    public static final String SQL_CREATE_PAYMENTS_TABLE =
            "CREATE TABLE " + PaymentEntry.TABLE_NAME + " (" +
                    PaymentEntry.COLUMN_NAME_PAYMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    PaymentEntry.COLUMN_NAME_ORDER_ID + " INTEGER," +
                    PaymentEntry.COLUMN_NAME_AMOUNT + " REAL," +
                    PaymentEntry.COLUMN_NAME_PAYMENT_METHOD + " TEXT," +
                    PaymentEntry.COLUMN_NAME_TRANSACTION_ID + " TEXT," +
                    PaymentEntry.COLUMN_NAME_PAYMENT_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(" + PaymentEntry.COLUMN_NAME_ORDER_ID + ") REFERENCES " + OrderEntry.TABLE_NAME + "(" + OrderEntry.COLUMN_NAME_ORDER_ID + "))";
            
    public static final String SQL_CREATE_ADDRESSES_TABLE =
            "CREATE TABLE " + AddressEntry.TABLE_NAME + " (" +
                    AddressEntry.COLUMN_NAME_ADDRESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    AddressEntry.COLUMN_NAME_USER_ID + " INTEGER," +
                    AddressEntry.COLUMN_NAME_TYPE + " TEXT," +
                    AddressEntry.COLUMN_NAME_FULL_ADDRESS + " TEXT," +
                    AddressEntry.COLUMN_NAME_LANDMARK + " TEXT," +
                    AddressEntry.COLUMN_NAME_CITY + " TEXT," +
                    AddressEntry.COLUMN_NAME_AREA + " TEXT," +
                    AddressEntry.COLUMN_NAME_LATITUDE + " REAL," +
                    AddressEntry.COLUMN_NAME_LONGITUDE + " REAL," +
                    AddressEntry.COLUMN_NAME_IS_DEFAULT + " INTEGER DEFAULT 0," +
                    "FOREIGN KEY(" + AddressEntry.COLUMN_NAME_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_USER_ID + "))";

    public static final String SQL_CREATE_VENDORS_TABLE =
            "CREATE TABLE " + VendorEntry.TABLE_NAME + " (" +
                    VendorEntry.COLUMN_NAME_VENDOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    VendorEntry.COLUMN_NAME_VENDOR_NAME + " TEXT," +
                    VendorEntry.COLUMN_NAME_ADDRESS + " TEXT," +
                    VendorEntry.COLUMN_NAME_LATITUDE + " REAL," +
                    VendorEntry.COLUMN_NAME_LONGITUDE + " REAL," +
                    VendorEntry.COLUMN_NAME_ICON + " TEXT," +
                    VendorEntry.COLUMN_NAME_RATING + " REAL DEFAULT 0.0)";
            
            public static class NotificationEntry implements BaseColumns {
        public static final String TABLE_NAME = "notifications";
        public static final String COLUMN_NAME_NOTIFICATION_ID = "notification_id";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_NAME_IS_READ = "is_read";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
    }

    public static final String SQL_CREATE_NOTIFICATIONS_TABLE =
            "CREATE TABLE " + NotificationEntry.TABLE_NAME + " (" +
                    NotificationEntry.COLUMN_NAME_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    NotificationEntry.COLUMN_NAME_USER_ID + " INTEGER," +
                    NotificationEntry.COLUMN_NAME_TITLE + " TEXT," +
                    NotificationEntry.COLUMN_NAME_MESSAGE + " TEXT," +
                    NotificationEntry.COLUMN_NAME_IS_READ + " INTEGER DEFAULT 0," +
                    NotificationEntry.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(" + NotificationEntry.COLUMN_NAME_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_USER_ID + "))";

    public static final String SQL_DELETE_NOTIFICATIONS_TABLE =
            "DROP TABLE IF EXISTS " + NotificationEntry.TABLE_NAME;

    // SQL statements to delete tables
    public static final String SQL_DELETE_USERS_TABLE =
            "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;
            
    public static final String SQL_DELETE_CATEGORIES_TABLE =
            "DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME;
            
    public static final String SQL_DELETE_PRODUCTS_TABLE =
            "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME;
            
    public static final String SQL_DELETE_ORDERS_TABLE =
            "DROP TABLE IF EXISTS " + OrderEntry.TABLE_NAME;
            
    public static final String SQL_DELETE_ORDER_ITEMS_TABLE =
            "DROP TABLE IF EXISTS " + OrderItemEntry.TABLE_NAME;
            
    public static final String SQL_DELETE_CART_ITEMS_TABLE =
            "DROP TABLE IF EXISTS " + CartItemEntry.TABLE_NAME;

    public static final String SQL_DELETE_FAVORITES_TABLE =
            "DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME;

    public static final String SQL_DELETE_MESSAGES_TABLE =
            "DROP TABLE IF EXISTS " + MessageEntry.TABLE_NAME;

    public static final String SQL_DELETE_PROMOTIONS_TABLE =
            "DROP TABLE IF EXISTS " + PromotionEntry.TABLE_NAME;

    public static final String SQL_DELETE_REVIEWS_TABLE =
            "DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME;
            
    public static final String SQL_DELETE_DELIVERY_PERSONNEL_TABLE =
            "DROP TABLE IF EXISTS " + DeliveryPersonEntry.TABLE_NAME;

    public static final String SQL_DELETE_PAYMENTS_TABLE =
            "DROP TABLE IF EXISTS " + PaymentEntry.TABLE_NAME;

    public static final String SQL_DELETE_VENDORS_TABLE =
            "DROP TABLE IF EXISTS " + VendorEntry.TABLE_NAME;

    public static final String SQL_DELETE_ADDRESSES_TABLE =
            "DROP TABLE IF EXISTS " + AddressEntry.TABLE_NAME;
}