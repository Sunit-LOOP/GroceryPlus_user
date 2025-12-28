# GroceryPlus Android App API Documentation

This document outlines the REST API endpoints that the Android app expects for full functionality. The Android app connects to `http://YOUR_SERVER_IP/groceryplus/api/index.php/` and makes requests to various endpoints.

## Authentication

### POST /auth
Login endpoint for user authentication.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (Success):**
```json
{
  "success": true,
  "token": "jwt_token_here",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "user@example.com",
    "phone": "1234567890"
  }
}
```

**Response (Error):**
```json
{
  "success": false,
  "message": "Invalid credentials"
}
```

### POST /register
User registration endpoint.

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "user@example.com",
  "password": "password123",
  "phone": "1234567890"
}
```

**Response (Success):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "user@example.com"
  }
}
```

## Products

### GET /products
Get products with optional filtering.

**Query Parameters:**
- `category` (optional): Filter by category name
- `search` (optional): Search in product names
- `limit` (optional): Number of products to return (default: 50)
- `offset` (optional): Pagination offset (default: 0)

**Headers:**
```
Authorization: jwt_token_here
```

**Response:**
```json
{
  "success": true,
  "products": [
    {
      "id": 1,
      "name": "Apple",
      "description": "Fresh red apple",
      "price": 2.50,
      "category": "Fruits",
      "image": "apple.jpg",
      "stock_quantity": 100,
      "rating": 4.5
    }
  ],
  "total": 150
}
```

### GET /products/{productId}
Get single product details.

**Headers:**
```
Authorization: jwt_token_here
```

**Response:**
```json
{
  "success": true,
  "product": {
    "id": 1,
    "name": "Apple",
    "description": "Fresh red apple",
    "price": 2.50,
    "category": "Fruits",
    "images": ["apple.jpg", "apple2.jpg"],
    "stock_quantity": 100,
    "rating": 4.5,
    "reviews_count": 25
  }
}
```

## Categories

### GET /categories
Get all product categories.

**Headers:**
```
Authorization: jwt_token_here
```

**Response:**
```json
{
  "success": true,
  "categories": [
    {
      "id": 1,
      "name": "Fruits",
      "description": "Fresh fruits",
      "image": "fruits.jpg"
    },
    {
      "id": 2,
      "name": "Vegetables",
      "description": "Fresh vegetables",
      "image": "vegetables.jpg"
    }
  ]
}
```

## Cart

### GET /cart
Get user's cart items.

**Headers:**
```
Authorization: jwt_token_here
```

**Response:**
```json
{
  "success": true,
  "cart": [
    {
      "id": 1,
      "product_id": 1,
      "product_name": "Apple",
      "quantity": 5,
      "price": 2.50,
      "total": 12.50
    }
  ],
  "total_amount": 12.50
}
```

### POST /cart
Add item to cart.

**Headers:**
```
Authorization: jwt_token_here
```

**Request Body:**
```json
{
  "product_id": 1,
  "quantity": 3
}
```

**Response:**
```json
{
  "success": true,
  "message": "Item added to cart",
  "cart_item": {
    "id": 1,
    "product_id": 1,
    "quantity": 3
  }
}
```

### PUT /cart/{cartItemId}
Update cart item quantity.

**Headers:**
```
Authorization: jwt_token_here
```

**Request Body:**
```json
{
  "quantity": 5
}
```

**Response:**
```json
{
  "success": true,
  "message": "Cart updated"
}
```

### DELETE /cart/{cartItemId}
Remove item from cart.

**Headers:**
```
Authorization: jwt_token_here
```

**Response:**
```json
{
  "success": true,
  "message": "Item removed from cart"
}
```

## Orders

### GET /orders
Get user's order history.

**Headers:**
```
Authorization: jwt_token_here
```

**Response:**
```json
{
  "success": true,
  "orders": [
    {
      "id": 1,
      "order_date": "2024-01-15 10:30:00",
      "status": "delivered",
      "total_amount": 25.00,
      "delivery_fee": 2.99,
      "items": [
        {
          "product_id": 1,
          "product_name": "Apple",
          "quantity": 5,
          "price": 2.50
        }
      ]
    }
  ]
}
```

### POST /orders
Place a new order.

**Headers:**
```
Authorization: jwt_token_here
```

**Request Body:**
```json
{
  "user_id": 1,
  "items": [
    {
      "product_id": 1,
      "quantity": 5,
      "price": 2.50
    }
  ],
  "delivery_fee": 2.99
}
```

**Response:**
```json
{
  "success": true,
  "message": "Order placed successfully",
  "order": {
    "id": 1,
    "status": "pending",
    "total_amount": 15.49
  }
}
```

## Favorites

### GET /favorites
Get user's favorite products.

**Headers:**
```
Authorization: jwt_token_here
```

**Response:**
```json
{
  "success": true,
  "favorites": [
    {
      "id": 1,
      "product_id": 1,
      "product_name": "Apple",
      "product_price": 2.50,
      "product_image": "apple.jpg"
    }
  ]
}
```

### POST /favorites
Add product to favorites.

**Headers:**
```
Authorization: jwt_token_here
```

**Request Body:**
```json
{
  "product_id": 1
}
```

**Response:**
```json
{
  "success": true,
  "message": "Added to favorites"
}
```

### DELETE /favorites/{productId}
Remove product from favorites.

**Headers:**
```
Authorization: jwt_token_here
```

**Response:**
```json
{
  "success": true,
  "message": "Removed from favorites"
}
```

## Reviews

### GET /reviews/{productId}
Get reviews for a product.

**Headers:**
```
Authorization: jwt_token_here
```

**Response:**
```json
{
  "success": true,
  "reviews": [
    {
      "id": 1,
      "user_id": 1,
      "user_name": "John Doe",
      "rating": 5,
      "review": "Great product!",
      "created_at": "2024-01-15 10:30:00"
    }
  ]
}
```

### POST /reviews
Submit a product review.

**Headers:**
```
Authorization: jwt_token_here
```

**Request Body:**
```json
{
  "product_id": 1,
  "rating": 5,
  "review": "Excellent quality!"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Review submitted"
}
```

## Messages

### GET /messages
Get user's conversations.

**Headers:**
```
Authorization: jwt_token_here
```

**Response:**
```json
{
  "success": true,
  "conversations": [
    {
      "user_id": 2,
      "user_name": "Support",
      "last_message": "Thank you for your order",
      "timestamp": "2024-01-15 10:30:00",
      "unread_count": 1
    }
  ]
}
```

### GET /messages/{userId}
Get messages with specific user.

**Headers:**
```
Authorization: jwt_token_here
```

**Response:**
```json
{
  "success": true,
  "messages": [
    {
      "id": 1,
      "sender_id": 2,
      "receiver_id": 1,
      "message": "Hello! How can I help you?",
      "timestamp": "2024-01-15 10:30:00"
    }
  ]
}
```

### POST /messages
Send a message.

**Headers:**
```
Authorization: jwt_token_here
```

**Request Body:**
```json
{
  "receiver_id": 2,
  "message": "Thank you for the quick response!"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Message sent"
}
```

## Analytics

### GET /analytics
Get dashboard analytics (for admin).

**Headers:**
```
Authorization: jwt_token_here
```

**Response:**
```json
{
  "success": true,
  "analytics": {
    "total_users": 1250,
    "total_orders": 450,
    "total_revenue": 12500.00,
    "monthly_orders": [
      {"month": "Jan", "count": 45},
      {"month": "Feb", "count": 52}
    ]
  }
}
```

## Notifications

### GET /notifications
Get user notifications.

**Headers:**
```
Authorization: jwt_token_here
```

**Response:**
```json
{
  "success": true,
  "notifications": [
    {
      "id": 1,
      "title": "Order Delivered",
      "message": "Your order #123 has been delivered",
      "type": "order",
      "read": false,
      "created_at": "2024-01-15 10:30:00"
    }
  ]
}
```

## PHP Implementation Notes

### 1. Base URL Configuration
```php
define('BASE_URL', 'http://your-domain.com/groceryplus/api/index.php/');
```

### 2. JWT Token Handling
```php
function getUserFromToken($token) {
    // Verify JWT token and return user data
    // Return user array or false if invalid
}

function validateAuthHeader() {
    $headers = getallheaders();
    if (!isset($headers['Authorization'])) {
        http_response_code(401);
        echo json_encode(['success' => false, 'message' => 'No authorization token']);
        exit;
    }

    $user = getUserFromToken($headers['Authorization']);
    if (!$user) {
        http_response_code(401);
        echo json_encode(['success' => false, 'message' => 'Invalid token']);
        exit;
    }

    return $user;
}
```

### 3. CORS Headers
```php
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}
```

### 4. Route Handling Example
```php
$request_method = $_SERVER['REQUEST_METHOD'];
$request_uri = $_SERVER['REQUEST_URI'];

// Remove base path
$path = str_replace('/groceryplus/api/index.php/', '', $request_uri);
$path_parts = explode('/', $path);
$endpoint = $path_parts[0];

switch ($endpoint) {
    case 'auth':
        handleAuth();
        break;
    case 'register':
        handleRegister();
        break;
    case 'products':
        handleProducts();
        break;
    // ... other endpoints
    default:
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Endpoint not found']);
}
```

### 5. Database Connection
```php
function getDBConnection() {
    $host = 'localhost';
    $db = 'groceryplus';
    $user = 'your_db_user';
    $pass = 'your_db_password';

    try {
        $pdo = new PDO("mysql:host=$host;dbname=$db", $user, $pass);
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        return $pdo;
    } catch(PDOException $e) {
        die("Connection failed: " . $e->getMessage());
    }
}
```

### 6. Error Response Format
```php
function sendError($message, $code = 400) {
    http_response_code($code);
    echo json_encode([
        'success' => false,
        'message' => $message
    ]);
    exit;
}

function sendSuccess($data = null, $message = 'Success') {
    echo json_encode([
        'success' => true,
        'message' => $message,
        'data' => $data
    ]);
}
```

## Security Considerations

1. **Input Validation**: Validate all user inputs
2. **SQL Injection Prevention**: Use prepared statements
3. **XSS Protection**: Sanitize output data
4. **Rate Limiting**: Implement rate limiting for API endpoints
5. **HTTPS**: Always use HTTPS in production
6. **Token Expiration**: Implement JWT token expiration
7. **Password Hashing**: Use bcrypt for password storage

## Testing

Use tools like Postman to test each endpoint with proper headers and request bodies before integrating with the Android app.