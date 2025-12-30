/**
 * Firestore Seeder (Node.js)
 * Run once to populate Firestore with sample data for GroceryPlus.
 *
 * Prerequisites:
 *   npm install firebase-admin
 *   Set up a service account key and export GOOGLE_APPLICATION_CREDENTIALS=<path-to-key.json>
 *
 * Run:
 *   node firestore-seeder-node.js
 */

const admin = require('firebase-admin');

// Initialize Firebase Admin SDK
admin.initializeApp({
  credential: admin.credential.applicationDefault(),
});

const db = admin.firestore();

const collections = {
  categories: [
    { name: 'Dairy' },
    { name: 'Vegetables' },
    { name: 'Fruits' },
  ],
  vendors: [
    { name: 'Fresh Dairy Co', phone: '9876543210' },
    { name: 'Green Veggies', phone: '9876543211' },
  ],
  users: [
    { email: 'admin@gmail.com', name: 'Admin', phone: '9999999999', type: 'admin' },
    { email: 'ram@gmail.com', name: 'Ram', phone: '9123456789', type: 'customer' },
  ],
  products: [
    { name: 'Organic Milk', price: 45.5, stock: 20, categoryId: 'cat_001', vendorId: 'ven_001', imageUrl: 'milk.png' },
    { name: 'Fresh Tomato', price: 12.0, stock: 50, categoryId: 'cat_002', vendorId: 'ven_002', imageUrl: 'tomato.png' },
    { name: 'Red Apple', price: 80.0, stock: 30, categoryId: 'cat_003', vendorId: 'ven_002', imageUrl: 'apple.png' },
  ],
  addresses: [
    { userId: 'ram@gmail.com', addressLine: '123 Main St', city: 'Kathmandu', pincode: '44600', isDefault: true },
  ],
  orders: [
    { userId: 'ram@gmail.com', totalAmount: 137.5, deliveryFee: 20.0, status: 'PENDING', addressId: 'addr_001', deliveryInstructions: 'Leave at door' },
  ],
  orderItems: [
    { orderId: 'order_001', productId: 'prod_001', quantity: 2, price: 45.5 },
    { orderId: 'order_001', productId: 'prod_002', quantity: 3, price: 12.0 },
  ],
  cart: [
    { userId: 'ram@gmail.com', productId: 'prod_001', quantity: 1 },
  ],
  notifications: [
    { userId: 'ram@gmail.com', title: 'Order Placed', message: 'Your order #order_001 has been placed successfully.', type: 'ORDER', refId: 'order_001' },
  ],
};

async function seedAll() {
  console.log('Starting Firestore seeding...');
  const batch = db.batch();

  // Helper to add timestamp
  const withTimestamp = (doc) => ({ ...doc, createdAt: admin.firestore.FieldValue.serverTimestamp() });

  // Categories
  const catRef = db.collection('categories');
  collections.categories.forEach((doc) => {
    const ref = catRef.doc(); // auto ID
    batch.set(ref, withTimestamp(doc));
  });

  // Vendors
  const venRef = db.collection('vendors');
  collections.vendors.forEach((doc) => {
    const ref = venRef.doc(); // auto ID
    batch.set(ref, withTimestamp(doc));
  });

  // Users (use email as doc ID)
  const userRef = db.collection('users');
  collections.users.forEach((doc) => {
    const ref = userRef.doc(doc.email);
    batch.set(ref, withTimestamp(doc));
  });

  // Products
  const prodRef = db.collection('products');
  collections.products.forEach((doc) => {
    const ref = prodRef.doc(); // auto ID
    batch.set(ref, withTimestamp(doc));
  });

  // Addresses
  const addrRef = db.collection('addresses');
  collections.addresses.forEach((doc) => {
    const ref = addrRef.doc(); // auto ID
    batch.set(ref, withTimestamp(doc));
  });

  // Orders
  const orderRef = db.collection('orders');
  collections.orders.forEach((doc) => {
    const ref = orderRef.doc(); // auto ID
    batch.set(ref, withTimestamp(doc));
  });

  // OrderItems
  const oiRef = db.collection('orderItems');
  collections.orderItems.forEach((doc) => {
    const ref = oiRef.doc(); // auto ID
    batch.set(ref, withTimestamp(doc));
  });

  // Cart
  const cartRef = db.collection('cart');
  collections.cart.forEach((doc) => {
    const ref = cartRef.doc(); // auto ID
    batch.set(ref, withTimestamp(doc));
  });

  // Notifications
  const notifRef = db.collection('notifications');
  collections.notifications.forEach((doc) => {
    const ref = notifRef.doc(); // auto ID
    batch.set(ref, withTimestamp(doc));
  });

  try {
    await batch.commit();
    console.log('Firestore seeded successfully!');
  } catch (e) {
    console.error('Error seeding Firestore:', e);
  }
}

seedAll();
