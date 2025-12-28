<?php
// GroceryPlus API - Main Entry Point
// This file routes API requests to appropriate handlers

header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

// Include your existing PHP files/classes
// require_once '../your-existing-config.php';
// require_once '../your-existing-database.php';
// require_once '../your-existing-functions.php';

// JWT Secret Key (change this to a secure random string)
define('JWT_SECRET', 'your-secure-jwt-secret-key-here');

// Database connection (replace with your existing connection)
function getDB() {
    static $db = null;
    if ($db === null) {
        try {
            $db = new PDO('mysql:host=localhost;dbname=groceryplus', 'username', 'password');
            $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        } catch(PDOException $e) {
            die(json_encode(['success' => false, 'message' => 'Database connection failed']));
        }
    }
    return $db;
}

// JWT functions
function base64UrlEncode($data) {
    return str_replace(['+', '/', '='], ['-', '_', ''], base64_encode($data));
}

function generateJWT($userId) {
    $header = json_encode(['typ' => 'JWT', 'alg' => 'HS256']);
    $payload = json_encode([
        'user_id' => $userId,
        'iat' => time(),
        'exp' => time() + (24 * 60 * 60) // 24 hours
    ]);

    $headerEncoded = base64UrlEncode($header);
    $payloadEncoded = base64UrlEncode($payload);

    $signature = hash_hmac('sha256', $headerEncoded . "." . $payloadEncoded, JWT_SECRET, true);
    $signatureEncoded = base64UrlEncode($signature);

    return $headerEncoded . "." . $payloadEncoded . "." . $signatureEncoded;
}

function verifyJWT($token) {
    $parts = explode('.', $token);
    if (count($parts) !== 3) return false;

    $header = $parts[0];
    $payload = $parts[1];
    $signature = $parts[2];

    $expectedSignature = hash_hmac('sha256', $header . "." . $payload, JWT_SECRET, true);
    $expectedSignatureEncoded = base64UrlEncode($expectedSignature);

    if ($signature !== $expectedSignatureEncoded) return false;

    $payloadDecoded = json_decode(base64_decode(str_replace(['-', '_'], ['+', '/'], $payload)), true);
    if ($payloadDecoded['exp'] < time()) return false;

    return $payloadDecoded;
}

function getCurrentUser() {
    $headers = getallheaders();
    if (!isset($headers['Authorization'])) {
        return false;
    }

    $token = $headers['Authorization'];
    return verifyJWT($token);
}

function requireAuth() {
    $user = getCurrentUser();
    if (!$user) {
        http_response_code(401);
        echo json_encode(['success' => false, 'message' => 'Unauthorized']);
        exit;
    }
    return $user;
}

// Route handling
$requestMethod = $_SERVER['REQUEST_METHOD'];
$requestUri = $_SERVER['REQUEST_URI'];

// Remove query string and base path
$path = parse_url($requestUri, PHP_URL_PATH);
$path = str_replace('/groceryplus/api/index.php/', '', $path);
$path = str_replace('/groceryplus/api/', '', $path); // Alternative path

$pathParts = explode('/', trim($path, '/'));
$endpoint = $pathParts[0] ?? '';

try {
    switch ($endpoint) {
        case 'auth':
            require_once 'auth.php';
            break;
        case 'register':
            require_once 'register.php';
            break;
        case 'products':
            require_once 'products.php';
            break;
        case 'categories':
            require_once 'categories.php';
            break;
        case 'cart':
            require_once 'cart.php';
            break;
        case 'orders':
            require_once 'orders.php';
            break;
        case 'favorites':
            require_once 'favorites.php';
            break;
        case 'messages':
            require_once 'messages.php';
            break;
        case 'reviews':
            require_once 'reviews.php';
            break;
        case 'analytics':
            require_once 'analytics.php';
            break;
        case 'notifications':
            require_once 'notifications.php';
            break;
        default:
            http_response_code(404);
            echo json_encode(['success' => false, 'message' => 'Endpoint not found']);
            break;
    }
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'Server error: ' . $e->getMessage()]);
}
?>