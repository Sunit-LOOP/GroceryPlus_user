-- SQLite schema for GroceryPlus app

CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_name TEXT NOT NULL,
    user_email TEXT,
    user_phone TEXT,
    user_password TEXT,
    user_type TEXT DEFAULT 'customer',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS categories (
    category_id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_name TEXT NOT NULL,
    category_description TEXT
);

CREATE TABLE IF NOT EXISTS vendors (
    vendor_id INTEGER PRIMARY KEY AUTOINCREMENT,
    vendor_name TEXT NOT NULL,
    vendor_description TEXT
);

CREATE TABLE IF NOT EXISTS products (
    product_id INTEGER PRIMARY KEY AUTOINCREMENT,
    product_name TEXT NOT NULL,
    category_id INTEGER,
    price REAL NOT NULL,
    description TEXT,
    image TEXT,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    vendor_id INTEGER,
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id)
);

CREATE TABLE IF NOT EXISTS addresses (
    address_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    address_line TEXT,
    city TEXT,
    state TEXT,
    zip_code TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS orders (
    order_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    total_amount REAL NOT NULL DEFAULT 0,
    delivery_fee REAL DEFAULT 0,
    status TEXT DEFAULT 'pending',
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    shipped_date DATETIME,
    delivery_person_id INTEGER,
    address_id INTEGER,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (address_id) REFERENCES addresses(address_id)
);

CREATE TABLE IF NOT EXISTS order_items (
    order_item_id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id INTEGER,
    product_id INTEGER,
    quantity INTEGER NOT NULL,
    price REAL NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE IF NOT EXISTS cart_items (
    cart_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    product_id INTEGER,
    quantity INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE IF NOT EXISTS favorites (
    favorite_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    product_id INTEGER,
    added_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE IF NOT EXISTS messages (
    message_id INTEGER PRIMARY KEY AUTOINCREMENT,
    sender_id INTEGER,
    receiver_id INTEGER,
    message_text TEXT NOT NULL,
    is_read INTEGER DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(user_id),
    FOREIGN KEY (receiver_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS notifications (
    notification_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    title TEXT,
    message TEXT,
    is_read INTEGER DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    product_id INTEGER,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review_text TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE IF NOT EXISTS likes (
    like_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    entity_type TEXT NOT NULL, -- e.g., 'product', 'review', 'message'
    entity_id INTEGER NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, entity_type, entity_id), -- A user can only like an entity once
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Insert sample data
INSERT OR IGNORE INTO categories (category_id, category_name, category_description) VALUES
(1, 'Fruits', 'Fresh fruits'),
(2, 'Vegetables', 'Fresh vegetables');

INSERT OR IGNORE INTO vendors (vendor_id, vendor_name, vendor_description) VALUES
(1, 'Local Farm', 'Organic produce');

INSERT OR IGNORE INTO users (user_id, user_name, user_email, user_phone, user_password, user_type) VALUES
(1, 'Ram', 'ram@gmail.com', '123-456-7890', '$2y$10$/vJQrUe7mmnHEGwh7ifXee6ugt8c/0jhIJ9PLMBJOv5KoQBeQ5waa', 'customer'),
(2, 'Jane Smith', 'jane@example.com', '098-765-4321', '$2y$10$/vJQrUe7mmnHEGwh7ifXee6ugt8c/0jhIJ9PLMBJOv5KoQBeQ5waa', 'customer'),
(3, 'admin', 'admin@example.com', '111-222-3333', '$2y$10$/vJQrUe7mmnHEGwh7ifXee6ugt8c/0jhIJ9PLMBJOv5KoQBeQ5waa', 'admin');

INSERT OR IGNORE INTO products (product_id, product_name, category_id, price, description, stock_quantity, vendor_id) VALUES
(1, 'Apple', 1, 1.50, 'Fresh red apple', 100, 1),
(2, 'Banana', 1, 0.75, 'Yellow banana', 150, 1),
(3, 'Milk', 2, 2.00, '1L milk bottle', 50, 1);
