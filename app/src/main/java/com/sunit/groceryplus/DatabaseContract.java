package com.sunit.groceryplus;

import android.provider.BaseColumns;

/** Contract class defining the database schema, table names, and column definitions for the application. */
public final class DatabaseContract {
    /** Private constructor to prevent instantiation. */
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
        public static final String COLUMN_NAME_WALLET_BALANCE = "wallet_balance";
        public static final String COLUMN_NAME_LOYALTY_POINTS = "loyalty_points";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
    }

    /* Inner class that defines the admin_settings table contents */
    public static class AdminSettingsEntry implements BaseColumns {
        public static final String TABLE_NAME = "admin_settings";
        public static final String COLUMN_NAME_SETTINGS_ID = "settings_id";
        public static final String COLUMN_NAME_STORE_NAME = "store_name";
        public static final String COLUMN_NAME_STORE_EMAIL = "store_email";
        public static final String COLUMN_NAME_STORE_PHONE = "store_phone";
        public static final String COLUMN_NAME_STORE_ADDRESS = "store_address";
        public static final String COLUMN_NAME_STORE_CITY = "store_city";
        public static final String COLUMN_NAME_STORE_STATE = "store_state";
        public static final String COLUMN_NAME_STORE_POSTAL_CODE = "store_postal_code";
        public static final String COLUMN_NAME_STORE_COUNTRY = "store_country";
        public static final String COLUMN_NAME_TAX_RATE = "tax_rate";
        public static final String COLUMN_NAME_DELIVERY_FEE = "delivery_fee";
        public static final String COLUMN_NAME_FREE_DELIVERY_ABOVE = "free_delivery_above";
        public static final String COLUMN_NAME_FREE_DELIVERY_THRESHOLD = "free_delivery_threshold";
        public static final String COLUMN_NAME_CURRENCY_SYMBOL = "currency_symbol";
        public static final String COLUMN_NAME_TIMEZONE = "timezone";
        public static final String COLUMN_NAME_ENABLE_NOTIFICATIONS = "enable_notifications";
        public static final String COLUMN_NAME_ENABLE_EMAIL_NOTIFICATIONS = "enable_email_notifications";
        public static final String COLUMN_NAME_SMTP_HOST = "smtp_host";
        public static final String COLUMN_NAME_SMTP_PORT = "smtp_port";
        public static final String COLUMN_NAME_SMTP_USERNAME = "smtp_username";
        public static final String COLUMN_NAME_SMTP_PASSWORD = "smtp_password";
        public static final String COLUMN_NAME_STRIPE_ENABLED = "stripe_enabled";
        public static final String COLUMN_NAME_STRIPE_PUBLISHABLE_KEY = "stripe_publishable_key";
        public static final String COLUMN_NAME_STRIPE_SECRET_KEY = "stripe_secret_key";
        public static final String COLUMN_NAME_COD_ENABLED = "cod_enabled";
        public static final String COLUMN_NAME_BUSINESS_HOURS = "business_hours";
        public static final String COLUMN_NAME_SUPPORT_EMAIL = "support_email";
        public static final String COLUMN_NAME_SUPPORT_PHONE = "support_phone";
        public static final String COLUMN_NAME_LOGO_URL = "logo_url";
        public static final String COLUMN_NAME_FAVICON_URL = "favicon_url";
        public static final String COLUMN_NAME_PRIMARY_COLOR = "primary_color";
        public static final String COLUMN_NAME_ACCENT_COLOR = "accent_color";
        public static final String COLUMN_NAME_MAINTENANCE_MODE = "maintenance_mode";
        public static final String COLUMN_NAME_MAINTENANCE_MESSAGE = "maintenance_message";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
        public static final String COLUMN_NAME_UPDATED_AT = "updated_at";
    }

    /* Inner class that defines the wishlists table contents */
    public static class WishlistEntry implements BaseColumns {
        public static final String TABLE_NAME = "wishlists";
        public static final String COLUMN_NAME_WISHLIST_ID = "wishlist_id";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_PRODUCT_ID = "product_id";
        public static final String COLUMN_NAME_ADDED_AT = "added_at";
    }

    /* Inner class that defines the delivery_boys table contents */
    public static class DeliveryBoyEntry implements BaseColumns {
        public static final String TABLE_NAME = "delivery_boys";
        public static final String COLUMN_NAME_DELIVERY_BOY_ID = "delivery_boy_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_AVAILABLE = "available";
        public static final String COLUMN_NAME_CURRENT_ORDER_ID = "current_order_id";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
    }

    /* Inner class that defines the categories table contents */
    public static class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_NAME_CATEGORY_ID = "category_id";
        public static final String COLUMN_NAME_CATEGORY_NAME = "category_name";
        public static final String COLUMN_NAME_CATEGORY_DESCRIPTION = "category_description";
        public static final String COLUMN_NAME_IMAGE = "image";
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
        public static final String COLUMN_NAME_DELIVERY_INSTRUCTIONS = "delivery_instructions";
        public static final String COLUMN_NAME_IS_PACKED = "is_packed";
        public static final String COLUMN_NAME_MODIFIED_AT = "modified_at";
    }

    /* Inner class that defines the order_items table contents */
    public static class OrderItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "order_items";
        public static final String COLUMN_NAME_ORDER_ITEM_ID = "order_item_id";
        public static final String COLUMN_NAME_ORDER_ID = "order_id";
        public static final String COLUMN_NAME_PRODUCT_ID = "product_id";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_ITEM_STATUS = "item_status"; // active, cancelled, returned, replaced
        public static final String COLUMN_NAME_REFUND_AMOUNT = "refund_amount";
        public static final String COLUMN_NAME_REFUND_STATUS = "refund_status"; // pending, processed, rejected
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
        public static final String COLUMN_NAME_AVAILABLE = "available";
        public static final String COLUMN_NAME_CURRENT_ORDER_ID = "current_order_id";
    }

    public static class PaymentEntry implements BaseColumns {
        public static final String TABLE_NAME = "payments";
        public static final String COLUMN_NAME_PAYMENT_ID = "payment_id";
        public static final String COLUMN_NAME_ORDER_ID = "order_id";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_PAYMENT_METHOD = "payment_method";
        public static final String COLUMN_NAME_TRANSACTION_ID = "transaction_id";
        public static final String COLUMN_NAME_PAYMENT_DATE = "payment_date";
        public static final String COLUMN_NAME_STATUS = "status";
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

    public static class SearchHistoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "search_history";
        public static final String COLUMN_NAME_HISTORY_ID = "history_id";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_QUERY = "query";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
    }

    /* Inner class that defines the wallet_transactions table contents */
    public static class WalletTransactionEntry implements BaseColumns {
        public static final String TABLE_NAME = "wallet_transactions";
        public static final String COLUMN_NAME_TRANSACTION_ID = "transaction_id";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_SOURCE = "source";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_STATUS = "status"; // completed, pending
        public static final String COLUMN_NAME_AVAILABLE_AT = "available_at"; // for auto-refund
    }

    public static final String SQL_CREATE_WALLET_TRANSACTIONS_TABLE =
            "CREATE TABLE " + WalletTransactionEntry.TABLE_NAME + " (" +
                    WalletTransactionEntry.COLUMN_NAME_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    WalletTransactionEntry.COLUMN_NAME_USER_ID + " INTEGER," +
                    WalletTransactionEntry.COLUMN_NAME_AMOUNT + " REAL," +
                    WalletTransactionEntry.COLUMN_NAME_TYPE + " TEXT," +
                    WalletTransactionEntry.COLUMN_NAME_SOURCE + " TEXT," +
                    WalletTransactionEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    WalletTransactionEntry.COLUMN_NAME_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    WalletTransactionEntry.COLUMN_NAME_STATUS + " TEXT DEFAULT 'completed'," +
                    WalletTransactionEntry.COLUMN_NAME_AVAILABLE_AT + " DATETIME," +
                    "FOREIGN KEY(" + WalletTransactionEntry.COLUMN_NAME_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_USER_ID + "))";

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
                    UserEntry.COLUMN_NAME_WALLET_BALANCE + " REAL DEFAULT 0.0," +
                    UserEntry.COLUMN_NAME_LOYALTY_POINTS + " REAL DEFAULT 0.0," +
                    UserEntry.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

    public static final String SQL_CREATE_CATEGORIES_TABLE =
            "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                    CategoryEntry.COLUMN_NAME_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CategoryEntry.COLUMN_NAME_CATEGORY_NAME + " TEXT," +
                    CategoryEntry.COLUMN_NAME_CATEGORY_DESCRIPTION + " TEXT," +
                    CategoryEntry.COLUMN_NAME_IMAGE + " TEXT)";

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
                    OrderEntry.COLUMN_NAME_DELIVERY_INSTRUCTIONS + " TEXT," +
                    OrderEntry.COLUMN_NAME_IS_PACKED + " INTEGER DEFAULT 0," +
                    OrderEntry.COLUMN_NAME_MODIFIED_AT + " TEXT," +
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
                    OrderItemEntry.COLUMN_NAME_ITEM_STATUS + " TEXT DEFAULT 'active'," +
                    OrderItemEntry.COLUMN_NAME_REFUND_AMOUNT + " REAL DEFAULT 0.0," +
                    OrderItemEntry.COLUMN_NAME_REFUND_STATUS + " TEXT," +
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

    public static final String SQL_CREATE_ADMIN_SETTINGS_TABLE =
            "CREATE TABLE " + AdminSettingsEntry.TABLE_NAME + " (" +
                    AdminSettingsEntry.COLUMN_NAME_SETTINGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    AdminSettingsEntry.COLUMN_NAME_STORE_NAME + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_STORE_EMAIL + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_STORE_PHONE + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_STORE_ADDRESS + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_STORE_CITY + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_STORE_STATE + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_STORE_POSTAL_CODE + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_STORE_COUNTRY + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_TAX_RATE + " REAL DEFAULT 0.0," +
                    AdminSettingsEntry.COLUMN_NAME_DELIVERY_FEE + " REAL DEFAULT 0.0," +
                    AdminSettingsEntry.COLUMN_NAME_FREE_DELIVERY_ABOVE + " INTEGER DEFAULT 0," +
                    AdminSettingsEntry.COLUMN_NAME_FREE_DELIVERY_THRESHOLD + " REAL DEFAULT 0.0," +
                    AdminSettingsEntry.COLUMN_NAME_CURRENCY_SYMBOL + " TEXT DEFAULT 'NPR'," +
                    AdminSettingsEntry.COLUMN_NAME_TIMEZONE + " TEXT DEFAULT 'UTC'," +
                    AdminSettingsEntry.COLUMN_NAME_ENABLE_NOTIFICATIONS + " INTEGER DEFAULT 1," +
                    AdminSettingsEntry.COLUMN_NAME_ENABLE_EMAIL_NOTIFICATIONS + " INTEGER DEFAULT 1," +
                    AdminSettingsEntry.COLUMN_NAME_SMTP_HOST + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_SMTP_PORT + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_SMTP_USERNAME + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_SMTP_PASSWORD + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_STRIPE_ENABLED + " INTEGER DEFAULT 0," +
                    AdminSettingsEntry.COLUMN_NAME_STRIPE_PUBLISHABLE_KEY + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_STRIPE_SECRET_KEY + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_COD_ENABLED + " INTEGER DEFAULT 1," +
                    AdminSettingsEntry.COLUMN_NAME_BUSINESS_HOURS + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_SUPPORT_EMAIL + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_SUPPORT_PHONE + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_LOGO_URL + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_FAVICON_URL + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_PRIMARY_COLOR + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_ACCENT_COLOR + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_MAINTENANCE_MODE + " INTEGER DEFAULT 0," +
                    AdminSettingsEntry.COLUMN_NAME_MAINTENANCE_MESSAGE + " TEXT," +
                    AdminSettingsEntry.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    AdminSettingsEntry.COLUMN_NAME_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

    public static final String SQL_CREATE_WISHLISTS_TABLE =
            "CREATE TABLE " + WishlistEntry.TABLE_NAME + " (" +
                    WishlistEntry.COLUMN_NAME_WISHLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    WishlistEntry.COLUMN_NAME_USER_ID + " INTEGER," +
                    WishlistEntry.COLUMN_NAME_PRODUCT_ID + " INTEGER," +
                    WishlistEntry.COLUMN_NAME_ADDED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "UNIQUE(" + WishlistEntry.COLUMN_NAME_USER_ID + ", " + WishlistEntry.COLUMN_NAME_PRODUCT_ID + ")," +
                    "FOREIGN KEY(" + WishlistEntry.COLUMN_NAME_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_USER_ID + ")," +
                    "FOREIGN KEY(" + WishlistEntry.COLUMN_NAME_PRODUCT_ID + ") REFERENCES " + ProductEntry.TABLE_NAME + "(" + ProductEntry.COLUMN_NAME_PRODUCT_ID + "))";

    public static final String SQL_CREATE_VENDORS_TABLE =
            "CREATE TABLE " + VendorEntry.TABLE_NAME + " (" +
                    VendorEntry.COLUMN_NAME_VENDOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    VendorEntry.COLUMN_NAME_VENDOR_NAME + " TEXT," +
                    VendorEntry.COLUMN_NAME_ADDRESS + " TEXT," +
                    VendorEntry.COLUMN_NAME_LATITUDE + " REAL," +
                    VendorEntry.COLUMN_NAME_LONGITUDE + " REAL," +
                    VendorEntry.COLUMN_NAME_ICON + " TEXT," +
                    VendorEntry.COLUMN_NAME_RATING + " REAL DEFAULT 0.0)";

    public static final String SQL_CREATE_SEARCH_HISTORY_TABLE =
            "CREATE TABLE " + SearchHistoryEntry.TABLE_NAME + " (" +
                    SearchHistoryEntry.COLUMN_NAME_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SearchHistoryEntry.COLUMN_NAME_USER_ID + " INTEGER," +
                    SearchHistoryEntry.COLUMN_NAME_QUERY + " TEXT," +
                    SearchHistoryEntry.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(" + SearchHistoryEntry.COLUMN_NAME_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_USER_ID + "))";

    public static final String SQL_CREATE_DELIVERY_PERSONNEL_TABLE =
            "CREATE TABLE " + DeliveryPersonEntry.TABLE_NAME + " (" +
                    DeliveryPersonEntry.COLUMN_NAME_PERSON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DeliveryPersonEntry.COLUMN_NAME_NAME + " TEXT," +
                    DeliveryPersonEntry.COLUMN_NAME_PHONE + " TEXT," +
                    DeliveryPersonEntry.COLUMN_NAME_STATUS + " TEXT," +
                    DeliveryPersonEntry.COLUMN_NAME_AVAILABLE + " INTEGER DEFAULT 0," +
                    DeliveryPersonEntry.COLUMN_NAME_CURRENT_ORDER_ID + " INTEGER)";

    public static final String SQL_CREATE_PAYMENTS_TABLE =
            "CREATE TABLE " + PaymentEntry.TABLE_NAME + " (" +
                    PaymentEntry.COLUMN_NAME_PAYMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    PaymentEntry.COLUMN_NAME_ORDER_ID + " INTEGER," +
                    PaymentEntry.COLUMN_NAME_AMOUNT + " REAL," +
                    PaymentEntry.COLUMN_NAME_PAYMENT_METHOD + " TEXT," +
                    PaymentEntry.COLUMN_NAME_TRANSACTION_ID + " TEXT," +
                    PaymentEntry.COLUMN_NAME_PAYMENT_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    PaymentEntry.COLUMN_NAME_STATUS + " TEXT DEFAULT 'Pending'," +
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

    public static final String SQL_CREATE_NOTIFICATIONS_TABLE =
            "CREATE TABLE " + NotificationEntry.TABLE_NAME + " (" +
                    NotificationEntry.COLUMN_NAME_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    NotificationEntry.COLUMN_NAME_USER_ID + " INTEGER," +
                    NotificationEntry.COLUMN_NAME_TITLE + " TEXT," +
                    NotificationEntry.COLUMN_NAME_MESSAGE + " TEXT," +
                    NotificationEntry.COLUMN_NAME_TYPE + " TEXT," +
                    NotificationEntry.COLUMN_NAME_REF_ID + " TEXT," +
                    NotificationEntry.COLUMN_NAME_IS_READ + " INTEGER DEFAULT 0," +
                    NotificationEntry.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(" + NotificationEntry.COLUMN_NAME_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_USER_ID + "))";

public static final String SQL_DELETE_USERS_TABLE = "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;
public static final String SQL_DELETE_ADMIN_SETTINGS_TABLE = "DROP TABLE IF EXISTS " + AdminSettingsEntry.TABLE_NAME;
public static final String SQL_DELETE_WISHLISTS_TABLE = "DROP TABLE IF EXISTS " + WishlistEntry.TABLE_NAME;
public static final String SQL_DELETE_CATEGORIES_TABLE = "DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME;
public static final String SQL_DELETE_PRODUCTS_TABLE = "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME;
public static final String SQL_DELETE_ORDERS_TABLE = "DROP TABLE IF EXISTS " + OrderEntry.TABLE_NAME;
public static final String SQL_DELETE_ORDER_ITEMS_TABLE = "DROP TABLE IF EXISTS " + OrderItemEntry.TABLE_NAME;
public static final String SQL_DELETE_CART_ITEMS_TABLE = "DROP TABLE IF EXISTS " + CartItemEntry.TABLE_NAME;
public static final String SQL_DELETE_FAVORITES_TABLE = "DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME;
public static final String SQL_DELETE_MESSAGES_TABLE = "DROP TABLE IF EXISTS " + MessageEntry.TABLE_NAME;
public static final String SQL_DELETE_PROMOTIONS_TABLE = "DROP TABLE IF EXISTS " + PromotionEntry.TABLE_NAME;
public static final String SQL_DELETE_REVIEWS_TABLE = "DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME;
public static final String SQL_DELETE_DELIVERY_PERSONNEL_TABLE = "DROP TABLE IF EXISTS " + DeliveryPersonEntry.TABLE_NAME;

    // ==================== GUEST USER TABLE ====================
    
    public static class GuestUserEntry implements BaseColumns {
        public static final String TABLE_NAME = "guest_users";
        public static final String COLUMN_NAME_GUEST_ID = "guest_id";
        public static final String COLUMN_NAME_SESSION_ID = "session_id";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_FIRST_NAME = "first_name";
        public static final String COLUMN_NAME_LAST_NAME = "last_name";
        public static final String COLUMN_NAME_DELIVERY_ADDRESS = "delivery_address";
        public static final String COLUMN_NAME_CITY = "city";
        public static final String COLUMN_NAME_POSTAL_CODE = "postal_code";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
        public static final String COLUMN_NAME_LAST_ACTIVE = "last_active";
        public static final String COLUMN_NAME_IS_ACTIVE = "is_active";
        public static final String COLUMN_NAME_CART_ITEMS_COUNT = "cart_items_count";
        public static final String COLUMN_NAME_CART_TOTAL = "cart_total";
    }
    
    public static final String SQL_CREATE_GUEST_USERS_TABLE =
            "CREATE TABLE " + GuestUserEntry.TABLE_NAME + " (" +
                    GuestUserEntry.COLUMN_NAME_GUEST_ID + " TEXT PRIMARY KEY," +
                    GuestUserEntry.COLUMN_NAME_SESSION_ID + " TEXT," +
                    GuestUserEntry.COLUMN_NAME_EMAIL + " TEXT," +
                    GuestUserEntry.COLUMN_NAME_PHONE + " TEXT," +
                    GuestUserEntry.COLUMN_NAME_FIRST_NAME + " TEXT," +
                    GuestUserEntry.COLUMN_NAME_LAST_NAME + " TEXT," +
                    GuestUserEntry.COLUMN_NAME_DELIVERY_ADDRESS + " TEXT," +
                    GuestUserEntry.COLUMN_NAME_CITY + " TEXT," +
                    GuestUserEntry.COLUMN_NAME_POSTAL_CODE + " TEXT," +
                    GuestUserEntry.COLUMN_NAME_CREATED_AT + " INTEGER," +
                    GuestUserEntry.COLUMN_NAME_LAST_ACTIVE + " INTEGER," +
                    GuestUserEntry.COLUMN_NAME_IS_ACTIVE + " INTEGER DEFAULT 1," +
                    GuestUserEntry.COLUMN_NAME_CART_ITEMS_COUNT + " INTEGER DEFAULT 0," +
                    GuestUserEntry.COLUMN_NAME_CART_TOTAL + " REAL DEFAULT 0.0)";

    public static final String SQL_DELETE_GUEST_USERS_TABLE = "DROP TABLE IF EXISTS " + GuestUserEntry.TABLE_NAME;

    // ==================== REFUNDS TABLE ====================
    
    public static class RefundEntry implements BaseColumns {
        public static final String TABLE_NAME = "refunds";
        public static final String COLUMN_NAME_REFUND_ID = "refund_id";
        public static final String COLUMN_NAME_ORDER_ID = "order_id";
        public static final String COLUMN_NAME_REFUND_NUMBER = "refund_number";
        public static final String COLUMN_NAME_REFUND_AMOUNT = "refund_amount";
        public static final String COLUMN_NAME_ORIGINAL_AMOUNT = "original_amount";
        public static final String COLUMN_NAME_REFUND_REASON = "refund_reason";
        public static final String COLUMN_NAME_REFUND_TYPE = "refund_type";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_REQUESTED_DATE = "requested_date";
        public static final String COLUMN_NAME_PROCESSED_DATE = "processed_date";
        public static final String COLUMN_NAME_COMPLETED_DATE = "completed_date";
        public static final String COLUMN_NAME_PAYMENT_METHOD = "payment_method";
        public static final String COLUMN_NAME_REFUND_METHOD = "refund_method";
        public static final String COLUMN_NAME_TRANSACTION_ID = "transaction_id";
        public static final String COLUMN_NAME_PROCESSOR_NOTES = "processor_notes";
        public static final String COLUMN_NAME_ITEM_QUANTITY = "item_quantity";
        public static final String COLUMN_NAME_ITEM_CONDITION = "item_condition";
        public static final String COLUMN_NAME_RETURN_REASON = "return_reason";
        public static final String COLUMN_NAME_CUSTOMER_ID = "customer_id";
        public static final String COLUMN_NAME_CUSTOMER_NAME = "customer_name";
        public static final String COLUMN_NAME_CUSTOMER_EMAIL = "customer_email";
        public static final String COLUMN_NAME_PROCESSED_BY = "processed_by";
        public static final String COLUMN_NAME_ADMIN_NOTES = "admin_notes";
    }
    
    public static final String SQL_CREATE_REFUNDS_TABLE =
            "CREATE TABLE " + RefundEntry.TABLE_NAME + " (" +
                    RefundEntry.COLUMN_NAME_REFUND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    RefundEntry.COLUMN_NAME_ORDER_ID + " INTEGER," +
                    RefundEntry.COLUMN_NAME_REFUND_NUMBER + " TEXT," +
                    RefundEntry.COLUMN_NAME_REFUND_AMOUNT + " REAL," +
                    RefundEntry.COLUMN_NAME_ORIGINAL_AMOUNT + " REAL," +
                    RefundEntry.COLUMN_NAME_REFUND_REASON + " TEXT," +
                    RefundEntry.COLUMN_NAME_REFUND_TYPE + " TEXT," +
                    RefundEntry.COLUMN_NAME_STATUS + " TEXT," +
                    RefundEntry.COLUMN_NAME_REQUESTED_DATE + " TEXT," +
                    RefundEntry.COLUMN_NAME_PROCESSED_DATE + " TEXT," +
                    RefundEntry.COLUMN_NAME_COMPLETED_DATE + " TEXT," +
                    RefundEntry.COLUMN_NAME_PAYMENT_METHOD + " TEXT," +
                    RefundEntry.COLUMN_NAME_REFUND_METHOD + " TEXT," +
                    RefundEntry.COLUMN_NAME_TRANSACTION_ID + " TEXT," +
                    RefundEntry.COLUMN_NAME_PROCESSOR_NOTES + " TEXT," +
                    RefundEntry.COLUMN_NAME_ITEM_QUANTITY + " INTEGER," +
                    RefundEntry.COLUMN_NAME_ITEM_CONDITION + " TEXT," +
                    RefundEntry.COLUMN_NAME_RETURN_REASON + " TEXT," +
                    RefundEntry.COLUMN_NAME_CUSTOMER_ID + " INTEGER," +
                    RefundEntry.COLUMN_NAME_CUSTOMER_NAME + " TEXT," +
                    RefundEntry.COLUMN_NAME_CUSTOMER_EMAIL + " TEXT," +
                    RefundEntry.COLUMN_NAME_PROCESSED_BY + " INTEGER," +
                    RefundEntry.COLUMN_NAME_ADMIN_NOTES + " TEXT)";

    public static final String SQL_DELETE_REFUNDS_TABLE = "DROP TABLE IF EXISTS " + RefundEntry.TABLE_NAME;

    // ==================== SHIPPING OPTIONS TABLE ====================
    
    public static class ShippingOptionEntry implements BaseColumns {
        public static final String TABLE_NAME = "shipping_options";
        public static final String COLUMN_NAME_OPTION_ID = "option_id";
        public static final String COLUMN_NAME_OPTION_NAME = "option_name";
        public static final String COLUMN_NAME_OPTION_CODE = "option_code";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_BASE_COST = "base_cost";
        public static final String COLUMN_NAME_COST_PER_KG = "cost_per_kg";
        public static final String COLUMN_NAME_COST_PER_KM = "cost_per_km";
        public static final String COLUMN_NAME_DELIVERY_DAYS = "delivery_days";
        public static final String COLUMN_NAME_DELIVERY_TIME = "delivery_time";
        public static final String COLUMN_NAME_CARRIER_NAME = "carrier_name";
        public static final String COLUMN_NAME_SERVICE_TYPE = "service_type";
        public static final String COLUMN_NAME_TRACKING_AVAILABLE = "tracking_available";
        public static final String COLUMN_NAME_INSURANCE_AVAILABLE = "insurance_available";
        public static final String COLUMN_NAME_MAX_WEIGHT = "max_weight";
        public static final String COLUMN_NAME_MAX_DIMENSIONS = "max_dimensions";
        public static final String COLUMN_NAME_AVAILABLE = "available";
        public static final String COLUMN_NAME_MIN_ORDER_AMOUNT = "min_order_amount";
        public static final String COLUMN_NAME_IS_DEFAULT = "is_default";
        public static final String COLUMN_NAME_PRIORITY = "priority";
        public static final String COLUMN_NAME_ICON = "icon";
    }
    
    public static final String SQL_CREATE_SHIPPING_OPTIONS_TABLE =
            "CREATE TABLE " + ShippingOptionEntry.TABLE_NAME + " (" +
                    ShippingOptionEntry.COLUMN_NAME_OPTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ShippingOptionEntry.COLUMN_NAME_OPTION_NAME + " TEXT," +
                    ShippingOptionEntry.COLUMN_NAME_OPTION_CODE + " TEXT," +
                    ShippingOptionEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    ShippingOptionEntry.COLUMN_NAME_BASE_COST + " REAL," +
                    ShippingOptionEntry.COLUMN_NAME_COST_PER_KG + " REAL," +
                    ShippingOptionEntry.COLUMN_NAME_COST_PER_KM + " REAL," +
                    ShippingOptionEntry.COLUMN_NAME_DELIVERY_DAYS + " INTEGER," +
                    ShippingOptionEntry.COLUMN_NAME_DELIVERY_TIME + " TEXT," +
                    ShippingOptionEntry.COLUMN_NAME_CARRIER_NAME + " TEXT," +
                    ShippingOptionEntry.COLUMN_NAME_SERVICE_TYPE + " TEXT," +
                    ShippingOptionEntry.COLUMN_NAME_TRACKING_AVAILABLE + " INTEGER DEFAULT 1," +
                    ShippingOptionEntry.COLUMN_NAME_INSURANCE_AVAILABLE + " INTEGER DEFAULT 0," +
                    ShippingOptionEntry.COLUMN_NAME_MAX_WEIGHT + " REAL," +
                    ShippingOptionEntry.COLUMN_NAME_MAX_DIMENSIONS + " REAL," +
                    ShippingOptionEntry.COLUMN_NAME_AVAILABLE + " INTEGER DEFAULT 1," +
                    ShippingOptionEntry.COLUMN_NAME_MIN_ORDER_AMOUNT + " REAL DEFAULT 0.0," +
                    ShippingOptionEntry.COLUMN_NAME_IS_DEFAULT + " INTEGER DEFAULT 0," +
                    ShippingOptionEntry.COLUMN_NAME_PRIORITY + " INTEGER DEFAULT 999," +
                    ShippingOptionEntry.COLUMN_NAME_ICON + " TEXT)";

    public static final String SQL_DELETE_SHIPPING_OPTIONS_TABLE = "DROP TABLE IF EXISTS " + ShippingOptionEntry.TABLE_NAME;

    // ==================== INVOICES TABLE ====================
    
    public static class InvoiceEntry implements BaseColumns {
        public static final String TABLE_NAME = "invoices";
        public static final String COLUMN_NAME_INVOICE_ID = "invoice_id";
        public static final String COLUMN_NAME_INVOICE_NUMBER = "invoice_number";
        public static final String COLUMN_NAME_ORDER_ID = "order_id";
        public static final String COLUMN_NAME_ORDER_NUMBER = "order_number";
        public static final String COLUMN_NAME_CUSTOMER_ID = "customer_id";
        public static final String COLUMN_NAME_CUSTOMER_NAME = "customer_name";
        public static final String COLUMN_NAME_CUSTOMER_EMAIL = "customer_email";
        public static final String COLUMN_NAME_CUSTOMER_PHONE = "customer_phone";
        public static final String COLUMN_NAME_BILLING_ADDRESS = "billing_address";
        public static final String COLUMN_NAME_SHIPPING_ADDRESS = "shipping_address";
        public static final String COLUMN_NAME_INVOICE_DATE = "invoice_date";
        public static final String COLUMN_NAME_DUE_DATE = "due_date";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_CURRENCY = "currency";
        public static final String COLUMN_NAME_SUBTOTAL = "subtotal";
        public static final String COLUMN_NAME_TAX_AMOUNT = "tax_amount";
        public static final String COLUMN_NAME_SHIPPING_COST = "shipping_cost";
        public static final String COLUMN_NAME_DISCOUNT_AMOUNT = "discount_amount";
        public static final String COLUMN_NAME_TOTAL_AMOUNT = "total_amount";
        public static final String COLUMN_NAME_PAYMENT_METHOD = "payment_method";
        public static final String COLUMN_NAME_PAYMENT_STATUS = "payment_status";
        public static final String COLUMN_NAME_TRANSACTION_ID = "transaction_id";
        public static final String COLUMN_NAME_PAID_DATE = "paid_date";
        public static final String COLUMN_NAME_BUSINESS_NAME = "business_name";
        public static final String COLUMN_NAME_BUSINESS_ADDRESS = "business_address";
        public static final String COLUMN_NAME_BUSINESS_PHONE = "business_phone";
        public static final String COLUMN_NAME_BUSINESS_EMAIL = "business_email";
        public static final String COLUMN_NAME_TAX_ID = "tax_id";
        public static final String COLUMN_NAME_NOTES = "notes";
        public static final String COLUMN_NAME_TERMS = "terms";
        public static final String COLUMN_NAME_CREATED_BY = "created_by";
        public static final String COLUMN_NAME_PDF_PATH = "pdf_path";
        public static final String COLUMN_NAME_EMAIL_SENT = "email_sent";
    }
    
    public static final String SQL_CREATE_INVOICES_TABLE =
            "CREATE TABLE " + InvoiceEntry.TABLE_NAME + " (" +
                    InvoiceEntry.COLUMN_NAME_INVOICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    InvoiceEntry.COLUMN_NAME_INVOICE_NUMBER + " TEXT UNIQUE," +
                    InvoiceEntry.COLUMN_NAME_ORDER_ID + " INTEGER," +
                    InvoiceEntry.COLUMN_NAME_ORDER_NUMBER + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_CUSTOMER_ID + " INTEGER," +
                    InvoiceEntry.COLUMN_NAME_CUSTOMER_NAME + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_CUSTOMER_EMAIL + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_CUSTOMER_PHONE + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_BILLING_ADDRESS + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_SHIPPING_ADDRESS + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_INVOICE_DATE + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_DUE_DATE + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_STATUS + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_CURRENCY + " TEXT DEFAULT 'NPR'," +
                    InvoiceEntry.COLUMN_NAME_SUBTOTAL + " REAL," +
                    InvoiceEntry.COLUMN_NAME_TAX_AMOUNT + " REAL," +
                    InvoiceEntry.COLUMN_NAME_SHIPPING_COST + " REAL DEFAULT 0.0," +
                    InvoiceEntry.COLUMN_NAME_DISCOUNT_AMOUNT + " REAL DEFAULT 0.0," +
                    InvoiceEntry.COLUMN_NAME_TOTAL_AMOUNT + " REAL," +
                    InvoiceEntry.COLUMN_NAME_PAYMENT_METHOD + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_PAYMENT_STATUS + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_TRANSACTION_ID + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_PAID_DATE + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_BUSINESS_NAME + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_BUSINESS_ADDRESS + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_BUSINESS_PHONE + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_BUSINESS_EMAIL + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_TAX_ID + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_NOTES + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_TERMS + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_CREATED_BY + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_PDF_PATH + " TEXT," +
                    InvoiceEntry.COLUMN_NAME_EMAIL_SENT + " INTEGER DEFAULT 0)";

    public static final String SQL_DELETE_INVOICES_TABLE = "DROP TABLE IF EXISTS " + InvoiceEntry.TABLE_NAME;
public static final String SQL_DELETE_PAYMENTS_TABLE = "DROP TABLE IF EXISTS " + PaymentEntry.TABLE_NAME;
public static final String SQL_DELETE_ADDRESSES_TABLE = "DROP TABLE IF EXISTS " + AddressEntry.TABLE_NAME;
public static final String SQL_DELETE_VENDORS_TABLE = "DROP TABLE IF EXISTS " + VendorEntry.TABLE_NAME;
public static final String SQL_DELETE_SEARCH_HISTORY_TABLE = "DROP TABLE IF EXISTS " + SearchHistoryEntry.TABLE_NAME;

public static class NotificationEntry implements BaseColumns {
    public static final String TABLE_NAME = "notifications";
    public static final String COLUMN_NAME_NOTIFICATION_ID = "notification_id";
    public static final String COLUMN_NAME_USER_ID = "user_id";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_MESSAGE = "message";
    public static final String COLUMN_NAME_TYPE = "type"; // ORDER, PAYMENT, ACCOUNT, PROMO
    public static final String COLUMN_NAME_REF_ID = "ref_id"; // Order ID, Product ID, etc.
    public static final String COLUMN_NAME_IS_READ = "is_read";
    public static final String COLUMN_NAME_CREATED_AT = "created_at";
}

public static final String SQL_DELETE_NOTIFICATIONS_TABLE =
        "DROP TABLE IF EXISTS " + NotificationEntry.TABLE_NAME;

    /* Inner class that defines the support_tickets table contents */
    public static class SupportTicketEntry implements BaseColumns {
        public static final String TABLE_NAME = "support_tickets";
        public static final String COLUMN_NAME_TICKET_ID = "ticket_id";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_ORDER_ID = "order_id";
        public static final String COLUMN_NAME_SUBJECT = "subject";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_ISSUE_TYPE = "issue_type"; // missing, wrong, damaged, expired, other
        public static final String COLUMN_NAME_STATUS = "status"; // open, in-progress, resolved, closed
        public static final String COLUMN_NAME_PRIORITY = "priority"; // low, medium, high
        public static final String COLUMN_NAME_ISSUE_IMAGE = "issue_image"; // Base64 or path
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
        public static final String COLUMN_NAME_UPDATED_AT = "updated_at";
    }

    public static final String SQL_CREATE_SUPPORT_TICKETS_TABLE =
            "CREATE TABLE " + SupportTicketEntry.TABLE_NAME + " (" +
                    SupportTicketEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SupportTicketEntry.COLUMN_NAME_USER_ID + " INTEGER," +
                    SupportTicketEntry.COLUMN_NAME_ORDER_ID + " INTEGER," +
                    SupportTicketEntry.COLUMN_NAME_SUBJECT + " TEXT," +
                    SupportTicketEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    SupportTicketEntry.COLUMN_NAME_ISSUE_TYPE + " TEXT," +
                    SupportTicketEntry.COLUMN_NAME_STATUS + " TEXT DEFAULT 'open'," +
                    SupportTicketEntry.COLUMN_NAME_PRIORITY + " TEXT DEFAULT 'medium'," +
                    SupportTicketEntry.COLUMN_NAME_ISSUE_IMAGE + " TEXT," +
                    SupportTicketEntry.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    SupportTicketEntry.COLUMN_NAME_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(" + SupportTicketEntry.COLUMN_NAME_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_USER_ID + ")," +
                    "FOREIGN KEY(" + SupportTicketEntry.COLUMN_NAME_ORDER_ID + ") REFERENCES " + OrderEntry.TABLE_NAME + "(" + OrderEntry.COLUMN_NAME_ORDER_ID + "))";
}