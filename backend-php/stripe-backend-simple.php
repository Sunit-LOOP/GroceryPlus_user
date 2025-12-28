<?php
/**
 * Stripe Payment Backend for GroceryPlus
 * PHP Implementation - No Composer Required
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS, GET');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

// Stripe Configuration - Use environment variables
$STRIPE_SECRET_KEY = getenv('STRIPE_SECRET_KEY') ?: 'sk_test_your_secret_key_here';
$STRIPE_PUBLISHABLE_KEY = getenv('STRIPE_PUBLISHABLE_KEY') ?: 'pk_test_your_publishable_key_here';

/**
 * Create Payment Intent
 * Using cURL to call Stripe API directly
 */
function createPaymentIntent($amount, $currency = 'inr') {
    global $STRIPE_SECRET_KEY;
    
    // Validate minimum amount (50 cents = ₹0.50)
    if ($amount < 50) {
        return [
            'error' => 'Minimum amount is 50 cents'
        ];
    }
    
    $data = [
        'amount' => $amount,
        'currency' => $currency,
        'payment_method_types' => ['card'],
        'automatic_payment_methods' => ['enabled' => true],
        'metadata' => [
            'app' => 'GroceryPlus',
            'platform' => 'android'
        ]
    ];
    
    $ch = curl_init();
    
    curl_setopt_array($ch, [
        CURLOPT_URL => 'https://api.stripe.com/v1/payment_intents',
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_POST => true,
        CURLOPT_POSTFIELDS => http_build_query($data),
        CURLOPT_HTTPHEADER => [
            'Authorization: Bearer ' . $STRIPE_SECRET_KEY,
            'Content-Type: application/x-www-form-urlencoded'
        ],
        CURLOPT_TIMEOUT => 30
    ]);
    
    $response = curl_exec($ch);
    $http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    $error = curl_error($ch);
    curl_close($ch);
    
    if ($error) {
        return [
            'error' => 'Network error: ' . $error
        ];
    }
    
    $result = json_decode($response, true);
    
    if ($http_code !== 200) {
        return [
            'error' => $result['error']['message'] ?? 'Stripe API error'
        ];
    }
    
    return [
        'clientSecret' => $result['client_secret'],
        'paymentIntentId' => $result['id']
    ];
}

/**
 * Retrieve Payment Intent
 */
function retrievePaymentIntent($payment_intent_id) {
    global $STRIPE_SECRET_KEY;
    
    $ch = curl_init();
    
    curl_setopt_array($ch, [
        CURLOPT_URL => 'https://api.stripe.com/v1/payment_intents/' . $payment_intent_id,
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_HTTPHEADER => [
            'Authorization: Bearer ' . $STRIPE_SECRET_KEY
        ],
        CURLOPT_TIMEOUT => 30
    ]);
    
    $response = curl_exec($ch);
    $http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    $error = curl_error($ch);
    curl_close($ch);
    
    if ($error) {
        return [
            'error' => 'Network error: ' . $error
        ];
    }
    
    $result = json_decode($response, true);
    
    if ($http_code !== 200) {
        return [
            'error' => $result['error']['message'] ?? 'Stripe API error'
        ];
    }
    
    return [
        'status' => $result['status'],
        'amount' => $result['amount'],
        'currency' => $result['currency'],
        'payment_method' => $result['payment_method'],
        'metadata' => $result['metadata']
    ];
}

/**
 * Log Payment Data
 */
function logPayment($data) {
    $log_entry = [
        'timestamp' => date('Y-m-d H:i:s'),
        'data' => $data
    ];
    
    file_put_contents('payments.log', json_encode($log_entry) . "\n", FILE_APPEND);
}

/**
 * Send JSON Response
 */
function sendResponse($data, $http_code = 200) {
    http_response_code($http_code);
    echo json_encode($data);
    exit;
}

/**
 * Route and Handle Requests
 */
$method = $_SERVER['REQUEST_METHOD'];
$uri = $_SERVER['REQUEST_URI'];
$path = parse_url($uri, PHP_URL_PATH);

// Remove query string and base path
$path = explode('?', $path)[0];
$path = str_replace('/groceryplus', '', $path);

switch ($path) {
    case '/create-payment-intent':
        if ($method === 'POST') {
            $json_input = file_get_contents('php://input');
            $data = json_decode($json_input, true);
            
            if (!isset($data['amount']) || !isset($data['currency'])) {
                sendResponse(['error' => 'Missing amount or currency'], 400);
            }
            
            $amount = (int) $data['amount'];
            $currency = $data['currency'];
            
            $result = createPaymentIntent($amount, $currency);
            
            if (isset($result['error'])) {
                sendResponse(['error' => $result['error']], 400);
            } else {
                logPayment([
                    'action' => 'create_payment_intent',
                    'amount' => $amount,
                    'currency' => $currency,
                    'payment_intent_id' => $result['paymentIntentId']
                ]);
                sendResponse($result);
            }
        } else {
            sendResponse(['error' => 'Method not allowed'], 405);
        }
        break;
        
    case '/payment-status':
        if ($method === 'GET') {
            if (!isset($_GET['payment_intent_id'])) {
                sendResponse(['error' => 'Missing payment_intent_id'], 400);
            }
            
            $payment_intent_id = $_GET['payment_intent_id'];
            $result = retrievePaymentIntent($payment_intent_id);
            
            if (isset($result['error'])) {
                sendResponse(['error' => $result['error']], 400);
            } else {
                sendResponse($result);
            }
        } else {
            sendResponse(['error' => 'Method not allowed'], 405);
        }
        break;
        
    case '/webhook':
        if ($method === 'POST') {
            $payload = file_get_contents('php://input');
            $event = json_decode($payload, true);
            
            if ($event) {
                logPayment([
                    'action' => 'webhook_received',
                    'event_type' => $event['type'] ?? 'unknown',
                    'event_data' => $event
                ]);
                
                // Handle specific events
                switch ($event['type'] ?? '') {
                    case 'payment_intent.succeeded':
                        logPayment([
                            'action' => 'payment_succeeded',
                            'payment_intent_id' => $event['data']['object']['id'] ?? '',
                            'amount' => $event['data']['object']['amount'] ?? 0
                        ]);
                        break;
                        
                    case 'payment_intent.payment_failed':
                        logPayment([
                            'action' => 'payment_failed',
                            'payment_intent_id' => $event['data']['object']['id'] ?? '',
                            'error' => $event['data']['object']['last_payment_error']['message'] ?? 'Unknown error'
                        ]);
                        break;
                }
            }
            
            sendResponse(['status' => 'webhook_received']);
        } else {
            sendResponse(['error' => 'Method not allowed'], 405);
        }
        break;
        
    case '/test':
        // Test endpoint to verify backend is working
        sendResponse([
            'status' => 'backend_working',
            'timestamp' => date('Y-m-d H:i:s'),
            'stripe_configured' => !empty($STRIPE_SECRET_KEY)
        ]);
        break;
        
    default:
        sendResponse(['error' => 'Endpoint not found'], 404);
        break;
}
?>