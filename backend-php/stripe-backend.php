<?php
/**
 * Stripe Payment Backend for GroceryPlus
 * PHP Backend Implementation
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

// Stripe PHP Library - Install with: composer require stripe/stripe-php
require_once 'vendor/autoload.php';

// Configuration - Use environment variables
$STRIPE_SECRET_KEY = getenv('STRIPE_SECRET_KEY') ?: 'sk_test_your_secret_key_here';
$STRIPE_PUBLISHABLE_KEY = getenv('STRIPE_PUBLISHABLE_KEY') ?: 'pk_test_your_publishable_key_here';

// Initialize Stripe
\Stripe\Stripe::setApiKey($STRIPE_SECRET_KEY);

/**
 * Create Payment Intent
 * POST /create-payment-intent
 */
function createPaymentIntent() {
    try {
        // Get JSON input
        $json_input = file_get_contents('php://input');
        $data = json_decode($json_input, true);
        
        // Validate input
        if (!isset($data['amount']) || !isset($data['currency'])) {
            http_response_code(400);
            echo json_encode(['error' => 'Missing amount or currency']);
            return;
        }
        
        $amount = (int) $data['amount']; // Amount in cents
        $currency = $data['currency'];
        
        // Validate minimum amount (50 cents = ₹0.50)
        if ($amount < 50) {
            http_response_code(400);
            echo json_encode(['error' => 'Minimum amount is 50 cents']);
            return;
        }
        
        // Create Payment Intent
        $payment_intent = \Stripe\PaymentIntent::create([
            'amount' => $amount,
            'currency' => $currency,
            'payment_method_types' => ['card'],
            'automatic_payment_methods' => [
                'enabled' => true,
            ],
            'metadata' => [
                'app' => 'GroceryPlus',
                'platform' => 'android'
            ]
        ]);
        
        // Return client secret
        echo json_encode([
            'clientSecret' => $payment_intent->client_secret,
            'paymentIntentId' => $payment_intent->id
        ]);
        
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            'error' => $e->getMessage()
        ]);
    }
}

/**
 * Confirm Payment Intent
 * POST /confirm-payment
 */
function confirmPayment() {
    try {
        $json_input = file_get_contents('php://input');
        $data = json_decode($json_input, true);
        
        if (!isset($data['payment_intent_id'])) {
            http_response_code(400);
            echo json_encode(['error' => 'Missing payment_intent_id']);
            return;
        }
        
        $payment_intent = \Stripe\PaymentIntent::retrieve($data['payment_intent_id']);
        
        echo json_encode([
            'status' => $payment_intent->status,
            'amount' => $payment_intent->amount,
            'currency' => $payment_intent->currency,
            'payment_method' => $payment_intent->payment_method
        ]);
        
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            'error' => $e->getMessage()
        ]);
    }
}

/**
 * Stripe Webhook Handler
 * POST /webhook
 */
function handleWebhook() {
    $endpoint_secret = 'whsec_YOUR_WEBHOOK_SECRET'; // Get from Stripe Dashboard
    
    $payload = @file_get_contents('php://input');
    $sig_header = $_SERVER['HTTP_STRIPE_SIGNATURE'];
    
    try {
        $event = \Stripe\Webhook::constructEvent(
            $payload, $sig_header, $endpoint_secret
        );
    } catch(\UnexpectedValueException $e) {
        http_response_code(400);
        exit();
    } catch(\Stripe\Exception\SignatureVerificationException $e) {
        http_response_code(400);
        exit();
    }
    
    // Handle the event
    switch ($event->type) {
        case 'payment_intent.succeeded':
            $paymentIntent = $event->data->object;
            handleSuccessfulPayment($paymentIntent);
            break;
        case 'payment_intent.payment_failed':
            $paymentIntent = $event->data->object;
            handleFailedPayment($paymentIntent);
            break;
        default:
            // Unexpected event type
            http_response_code(400);
            exit();
    }
    
    http_response_code(200);
}

/**
 * Handle Successful Payment
 */
function handleSuccessfulPayment($paymentIntent) {
    // Log successful payment
    $log_data = [
        'timestamp' => date('Y-m-d H:i:s'),
        'payment_intent_id' => $paymentIntent->id,
        'amount' => $paymentIntent->amount,
        'currency' => $paymentIntent->currency,
        'status' => 'succeeded',
        'metadata' => $paymentIntent->metadata
    ];
    
    file_put_contents('payments.log', json_encode($log_data) . "\n", FILE_APPEND);
    
    // You can also:
    // - Update database with payment status
    // - Send confirmation email
    // - Trigger order fulfillment
    // - Update inventory
}

/**
 * Handle Failed Payment
 */
function handleFailedPayment($paymentIntent) {
    $log_data = [
        'timestamp' => date('Y-m-d H:i:s'),
        'payment_intent_id' => $paymentIntent->id,
        'amount' => $paymentIntent->amount,
        'currency' => $paymentIntent->currency,
        'status' => 'failed',
        'last_payment_error' => $paymentIntent->last_payment_error
    ];
    
    file_put_contents('payments.log', json_encode($log_data) . "\n", FILE_APPEND);
}

/**
 * Get Payment Status
 * GET /payment-status?payment_intent_id=pi_...
 */
function getPaymentStatus() {
    if (!isset($_GET['payment_intent_id'])) {
        http_response_code(400);
        echo json_encode(['error' => 'Missing payment_intent_id']);
        return;
    }
    
    try {
        $payment_intent = \Stripe\PaymentIntent::retrieve($_GET['payment_intent_id']);
        
        echo json_encode([
            'status' => $payment_intent->status,
            'amount' => $payment_intent->amount,
            'currency' => $paymentIntent->currency,
            'created' => $payment_intent->created,
            'metadata' => $paymentIntent->metadata
        ]);
        
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            'error' => $e->getMessage()
        ]);
    }
}

/**
 * Cancel Payment Intent
 * POST /cancel-payment
 */
function cancelPayment() {
    try {
        $json_input = file_get_contents('php://input');
        $data = json_decode($json_input, true);
        
        if (!isset($data['payment_intent_id'])) {
            http_response_code(400);
            echo json_encode(['error' => 'Missing payment_intent_id']);
            return;
        }
        
        $payment_intent = \Stripe\PaymentIntent::retrieve($data['payment_intent_id']);
        $payment_intent->cancel();
        
        echo json_encode([
            'status' => 'canceled',
            'payment_intent_id' => $payment_intent->id
        ]);
        
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            'error' => $e->getMessage()
        ]);
    }
}

// Route the request
$request_uri = $_SERVER['REQUEST_URI'];
$request_method = $_SERVER['REQUEST_METHOD'];

// Parse URL path
$path = parse_url($request_uri, PHP_URL_PATH);
$path = str_replace('/groceryplus', '', $path); // Remove base path if needed

switch ($path) {
    case '/create-payment-intent':
        if ($request_method === 'POST') {
            createPaymentIntent();
        } else {
            http_response_code(405);
            echo json_encode(['error' => 'Method not allowed']);
        }
        break;
        
    case '/confirm-payment':
        if ($request_method === 'POST') {
            confirmPayment();
        } else {
            http_response_code(405);
            echo json_encode(['error' => 'Method not allowed']);
        }
        break;
        
    case '/webhook':
        if ($request_method === 'POST') {
            handleWebhook();
        } else {
            http_response_code(405);
            echo json_encode(['error' => 'Method not allowed']);
        }
        break;
        
    case '/payment-status':
        if ($request_method === 'GET') {
            getPaymentStatus();
        } else {
            http_response_code(405);
            echo json_encode(['error' => 'Method not allowed']);
        }
        break;
        
    case '/cancel-payment':
        if ($request_method === 'POST') {
            cancelPayment();
        } else {
            http_response_code(405);
            echo json_encode(['error' => 'Method not allowed']);
        }
        break;
        
    default:
        http_response_code(404);
        echo json_encode(['error' => 'Endpoint not found']);
        break;
}
?>